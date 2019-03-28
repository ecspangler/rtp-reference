package rtp.demo.visualization.service;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Logger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import rtp.demo.creditor.domain.rtp.simplified.serde.MessageStatusReportDeserializer;
import rtp.demo.debtor.domain.model.payment.Payment;
import rtp.demo.debtor.domain.model.payment.serde.PaymentDeserializer;
import rtp.message.model.serde.FIToFICustomerCreditTransferV06Deserializer;
import rtp.message.model.serde.FIToFIPaymentStatusReportV07Deserializer;

public class VisualizationService extends AbstractVerticle {

	private static final Logger LOGGER = Logger.getLogger(VisualizationService.class.getName());

	private final BlockingDeque<Event> events = new LinkedBlockingDeque<>();
	private final List<KafkaConsumer<?, ?>> consumers = new LinkedList<>();
	private final LedgerSummation ledgerSummation = new LedgerSummation();

	private boolean running = true;

	@Override
	public void start() {
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());

		Runtime.getRuntime().addShutdownHook(new Thread(() -> consumers.forEach(KafkaConsumer::wakeup)));

		newConsumerThread("creditor-completed-payments",
				rtp.demo.creditor.domain.payments.serde.PaymentDeserializer.class,
				record -> record.value().getCreditTransferMessageId(),
				record -> record.value().getMessageStatusReportId()).start();

		newConsumerThread("creditor-payment-confirmation", MessageStatusReportDeserializer.class,
				record -> record.value().getOriginalMessageId(), record -> record.value().getMessageStatusReportId())
						.start();

		newConsumerThread("creditor-payments", rtp.demo.creditor.domain.payments.serde.PaymentDeserializer.class,
				record -> record.value().getCreditTransferMessageId(),
				record -> record.value().getCreditTransferMessageId()).start();

		newConsumerThread("debtor-completed-payments", PaymentDeserializer.class,
				record -> record.value().getPaymentId(), record -> record.value().getPaymentId()).start();

		newConsumerThread("debtor-payment-confirmation",
				rtp.demo.debtor.domain.rtp.simplified.serde.MessageStatusReportDeserializer.class,
				record -> record.value().getOriginalMessageId(), record -> record.value().getOriginalMessageId())
						.start();

		newConsumerThread("debtor-payments", PaymentDeserializer.class, record -> record.value().getPaymentId(),
				record -> record.value().getPaymentId()).start();

		newConsumerThread("mock-rtp-creditor-acknowledgement", FIToFIPaymentStatusReportV07Deserializer.class,
				record -> record.value().getOrgnlGrpInfAndSts().get(0).getOrgnlMsgId(),
				record -> record.value().getOrgnlGrpInfAndSts().get(0).getOrgnlMsgId()).start();

		newConsumerThread("mock-rtp-creditor-confirmation", FIToFIPaymentStatusReportV07Deserializer.class,
				record -> record.value().getOrgnlGrpInfAndSts().get(0).getOrgnlMsgId(),
				record -> record.value().getGrpHdr().getMsgId()).start();

		newConsumerThread("mock-rtp-creditor-credit-transfer", FIToFICustomerCreditTransferV06Deserializer.class,
				record -> record.value().getGrpHdr().getMsgId(), record -> record.value().getGrpHdr().getMsgId())
						.start();

		newConsumerThread("mock-rtp-debtor-confirmation", FIToFIPaymentStatusReportV07Deserializer.class,
				record -> record.value().getOrgnlGrpInfAndSts().get(0).getOrgnlMsgId(),
				record -> record.value().getOrgnlGrpInfAndSts().get(0).getOrgnlMsgId()).start();

		newConsumerThread("mock-rtp-debtor-credit-transfer", FIToFICustomerCreditTransferV06Deserializer.class,
				record -> record.value().getGrpHdr().getMsgId(), record -> record.value().getGrpHdr().getMsgId())
						.start();

		ledgerSummation.setCreditor(1000000);
		ledgerSummation.setDebtor(1000000);

		Map<String, String> config = new HashMap<>();
		config.put("bootstrap.servers", System.getenv("BOOTSTRAP_SERVERS"));
		config.put("key.deserializer", StringDeserializer.class.getName());
		config.put("value.deserializer", PaymentDeserializer.class.getName());
		config.put("group.id", "visualization");

		String confirmationTopic = "debtor-completed-payments";

		// use consumer for interacting with Apache Kafka
		io.vertx.kafka.client.consumer.KafkaConsumer<String, Payment> consumer = io.vertx.kafka.client.consumer.KafkaConsumer
				.create(vertx, config);
		consumer.subscribe(confirmationTopic);
		consumer.handler(record -> {
			LOGGER.info("Adding $" + record.value().getAmount() + " payment to ledger balance. ");
			ledgerSummation.addPayment(record.value().getAmount());
		});

		router.get("/events").handler(this::getEvents);
		router.get("/summation-ledger").handler(this::getLedgerSummation);

		router.get("/*").handler(StaticHandler.create());

		vertx.createHttpServer().requestHandler(router::accept).listen(8080);
		LOGGER.info("THE HTTP APPLICATION HAS STARTED");

	}

	private void getEvents(RoutingContext routingContext) {
		if (routingContext.queryParam("poll") != null && routingContext.queryParam("poll").size() == 1
				&& "long".equals(routingContext.queryParam("poll").get(0))) {
			try {
				LOGGER.info("Waiting for events...");
				Event event = events.poll(25L, TimeUnit.SECONDS);
				if (event != null) // If its not null, then we didn't time out, and we need to add it back to be
									// handled below.
					events.addFirst(event);
			} catch (InterruptedException ignored) {
			} // We just continue on and try to return something.
		}

		// Synchronized because the consumer is running in a different thread.
		List<Event> removedEvents;
		synchronized (events) {
			removedEvents = new LinkedList<>(events);
			events.clear();
		}

		routingContext.response().putHeader(CONTENT_TYPE, "application/json; charset=utf-8")
				.putHeader("Access-Control-Allow-Origin", "*").end(Json.encodePrettily(removedEvents));
	}

	private void getLedgerSummation(RoutingContext routingContext) {
		routingContext.response().putHeader(CONTENT_TYPE, "application/json; charset=utf-8")
				.putHeader("Access-Control-Allow-Origin", "*").end(Json.encodePrettily(ledgerSummation));
	}

	@XmlRootElement(name = "Event")
	@XmlAccessorType(XmlAccessType.FIELD)
	private class Event implements Serializable {

		private static final long serialVersionUID = 847186233458080682L;

		private String correlationId;
		private String messageId;
		private String location;

		public Event(String correlationId, String messageId, String location) {
			this.correlationId = correlationId;
			this.location = location;
			this.messageId = messageId;
		}

		public String getCorrelationId() {
			return correlationId;
		}

		public void setCorrelationId(String correlationId) {
			this.correlationId = correlationId;
		}

		public String getLocation() {
			return location;
		}

		public void setLocation(String location) {
			this.location = location;
		}

		public String getMessageId() {
			return messageId;
		}

		public void setMessageId(String messageId) {
			this.messageId = messageId;
		}

		@Override
		public String toString() {
			return "Event[messageId=" + messageId + ", correlationId=" + correlationId + ", location=" + location + "]";
		}
	}

	private <T> Thread newConsumerThread(String topic, Class<? extends Deserializer<T>> valueDeserializer,
			Function<ConsumerRecord<String, T>, String> getCorrelationId,
			Function<ConsumerRecord<String, T>, String> getMessageId) {
		return new Thread(() -> {
			String groupId = "visualization";
			final Logger THREAD_LOGGER = Logger.getLogger(groupId);

			Properties props = new Properties();
			props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv("BOOTSTRAP_SERVERS"));
			props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId + "-" + topic);
			props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
			props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
			KafkaConsumer<String, T> consumer = new KafkaConsumer<>(props);

			THREAD_LOGGER.info("Created consumer (t=" + topic + "g=" + groupId + ")");

			synchronized (consumers) {
				consumers.add(consumer);
			}

			try {
				consumer.subscribe(Collections.singletonList(topic));
				while (running) {
					ConsumerRecords<String, T> records = consumer.poll(1000);
					for (ConsumerRecord<String, T> record : records) {
						THREAD_LOGGER.info("Consumed message: topic=" + record.topic() + ",key=" + record.key());
						Event event = new Event(getCorrelationId.apply(record), getMessageId.apply(record),
								record.topic());
						synchronized (events) {
							events.add(event);
							THREAD_LOGGER.info("Processed event: " + event);
						}
					}
				}
			} catch (WakeupException ignored) {
			} finally {
				consumer.close();
			}
		});
	}
}
