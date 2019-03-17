package rtp.demo.visualization.service;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import iso.std.iso._20022.tech.xsd.pacs_008_001.FIToFICustomerCreditTransferV06;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import rtp.demo.debtor.domain.model.payment.Payment;
import rtp.demo.debtor.domain.model.payment.serde.PaymentDeserializer;
import rtp.message.model.serde.FIToFICustomerCreditTransferV06Deserializer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

import java.io.Serializable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

public class VisualizationService extends AbstractVerticle {

	private static final Logger LOGGER = Logger.getLogger(VisualizationService.class.getName());

	private final List<Event> events = new LinkedList<>();
	private final List<KafkaConsumer<?, ?>> consumers = new LinkedList<>();


	private boolean running = true;

	@Override
	public void start() {
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());

		Runtime.getRuntime().addShutdownHook(new Thread(() -> consumers.forEach(KafkaConsumer::wakeup)));

		// Create a simple consumer to listen for new transactions from the originator.
		newConsumerThread(Arrays.asList("debtor-payments"), "visualization-originator", PaymentDeserializer.class.getName(), (ConsumerRecord<String, Payment> record, Logger threadLogger) -> {
			synchronized (events) {
				Event event = new Event(record.value().getPaymentId(), "payment-service|send-payments");
				events.add(event);
				threadLogger.info("Processed event: " + event);
			}
		}).start();

		// Create a simple consumer to listen for new transactions from the odfi.
		newConsumerThread(Arrays.asList("mock-rtp-debtor-credit-transfer"), "visualization-odfi", FIToFICustomerCreditTransferV06Deserializer.class.getName(), (ConsumerRecord<String, FIToFICustomerCreditTransferV06> record, Logger threadLogger) -> {
			synchronized (events) {
				Event event = new Event(record.value().getGrpHdr().getMsgId(), "send-payments|rtp-mock");
				events.add(event);
				threadLogger.info("Processed event: " + event);
			}
		}).start();

		// Create a simple consumer to listen for new transactions from the rdfi.
		newConsumerThread(Arrays.asList("mock-rtp-creditor-credit-transfer"), "visualization-rdfi", FIToFICustomerCreditTransferV06Deserializer.class.getName(), (ConsumerRecord<String, FIToFICustomerCreditTransferV06> record, Logger threadLogger) -> {
			synchronized (events) {
				Event event = new Event(record.value().getGrpHdr().getMsgId(), "rtp-mock|rtp-creditor-receive-payment");
				events.add(event);
				threadLogger.info("Processed event: " + event);
			}
		}).start();

		// Create a simple consumer to listen for new transactions from the receiver.
		newConsumerThread(Arrays.asList("creditor-payments"), "visualization-receiver", rtp.demo.creditor.domain.payments.serde.PaymentDeserializer.class.getName(), (ConsumerRecord<String, rtp.demo.creditor.domain.payments.Payment> record, Logger threadLogger) -> {
			synchronized (events) {
				Event event = new Event(record.value().getDebtorId(), "rtp-creditor-receive-payment|rtp-creditor-payment-acknowledgement");
				events.add(event);
				threadLogger.info("Processed event: " + event);
			}
		}).start();

		router.get("/events").handler(this::getEvents);

		router.get("/*").handler(StaticHandler.create());

		vertx.createHttpServer().requestHandler(router::accept).listen(8080);
		LOGGER.info("THE HTTP APPLICATION HAS STARTED");

	}

	private void getEvents(RoutingContext routingContext) {

		// Synchronized because the consumer is running in a different thread.
		List<Event> removedEvents;
		synchronized (events) {
			removedEvents = new LinkedList<>(events);
			events.clear();
		}

		routingContext.response()
				.putHeader(CONTENT_TYPE, "application/json; charset=utf-8")
				.putHeader("Access-Control-Allow-Origin", "*")
				.end(Json.encodePrettily(removedEvents));
	}

	@XmlRootElement(name = "Event")
	@XmlAccessorType(XmlAccessType.FIELD)
	private class Event implements Serializable {

		private static final long serialVersionUID = 847186233458080682L;

		private String eventId;
		private String location;

		public Event(String eventId, String location) {
			this.eventId = eventId;
			this.location = location;
		}

		public String getEventId() {
			return eventId;
		}

		public void setEventId(String eventId) {
			this.eventId = eventId;
		}

		public String getLocation() {
			return location;
		}

		public void setLocation(String location) {
			this.location = location;
		}

		@Override
		public String toString() {
			return "Event[eventId=" + eventId + ", location=" + location + "]";
		}
	}

	private <T> Thread newConsumerThread(Collection<String> topics, String groupId, String valueDeserializer, BiConsumer<ConsumerRecord<String, T>, Logger> recordAction) {
		return new Thread(() -> {
			final Logger THREAD_LOGGER = Logger.getLogger(groupId);

			Properties props = new Properties();
			props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv("BOOTSTRAP_SERVERS"));
			props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
			props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
			props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
			KafkaConsumer<String, T> consumer = new KafkaConsumer<>(props);

			synchronized (consumers) {
				consumers.add(consumer);
			}

			try {
				consumer.subscribe(topics);
				while (running) {
					ConsumerRecords<String, T> records = consumer.poll(1000);
					for (ConsumerRecord<String, T> record : records) {
						THREAD_LOGGER.info("Consumed message: topic=" +record.topic() + ",key=" + record.key());
						recordAction.accept(record, THREAD_LOGGER);
					}
				}
			} catch (WakeupException ignored) {
			} finally {
				consumer.close();
			}
		});
	}
}
