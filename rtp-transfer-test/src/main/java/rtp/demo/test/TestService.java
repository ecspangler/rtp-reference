package rtp.demo.test;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import rtp.message.model.serde.FIToFICustomerCreditTransferV06Deserializer;
import rtp.message.model.serde.FIToFICustomerCreditTransferV06Serializer;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;

public class TestService extends AbstractVerticle {

	private static final Logger LOGGER = Logger.getLogger(TestService.class.getName());

	private List<KafkaConsumer<String, ?>> consumers = new LinkedList<>();

	@Override
	public void start() {

		Runtime.getRuntime().addShutdownHook(new Thread(() -> consumers.forEach(KafkaConsumer::wakeup)));

		createTopicConnector(
				"test-topic",
				"mock-rtp-debtor-credit-transfer",
				FIToFICustomerCreditTransferV06Deserializer.class,
				FIToFICustomerCreditTransferV06Serializer.class,
				Function.identity()
		).start();

		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());

		router.get("/halt").handler(this::halt);
		router.get("/*").handler(StaticHandler.create());
		vertx.createHttpServer().requestHandler(router::accept).listen(8080);

		LOGGER.info("THE HTTP APPLICATION HAS STARTED");
	}

	private void halt(RoutingContext context) {
		consumers.forEach(KafkaConsumer::wakeup);
		System.exit(0);
	}

	private <D, S> Thread createTopicConnector(
			String fromTopic,
			String toTopic,
			Class<? extends Deserializer<D>> fromDeserializer,
			Class<? extends Serializer<S>> toSerializer,
			Function<D, S> transform
	) {
		return new Thread(() -> {
			Properties consumerProps = new Properties();
			consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv("BOOTSTRAP_SERVERS"));
			consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
			consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
			consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, fromDeserializer.getName());
			KafkaConsumer<String, D> consumer = new KafkaConsumer<>(consumerProps);
			consumers.add(consumer);

			Properties producerProps = new Properties();
			producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv("BOOTSTRAP_SERVERS"));
			producerProps.put(ProducerConfig.ACKS_CONFIG, System.getenv("ACKS"));
			producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
			producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, toSerializer.getName());
			KafkaProducer<String, S> producer = new KafkaProducer<>(producerProps);

			try {
				consumer.subscribe(Collections.singletonList(fromTopic));
				for (;;) {
					ConsumerRecords<String, D> records = consumer.poll(Long.MAX_VALUE);
					for (ConsumerRecord<String, D> record : records) {
						LOGGER.info("Consumed message: topic=" +record.topic() + ",key=" + record.key());
						producer.send(new ProducerRecord<>(toTopic, record.key(), transform.apply(record.value())));
					}
				}
			} catch (WakeupException ignored) {
			} finally {
				consumer.close();
			}
		});
	}
}
