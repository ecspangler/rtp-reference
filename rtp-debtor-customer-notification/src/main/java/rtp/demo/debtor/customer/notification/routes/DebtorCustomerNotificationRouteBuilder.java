package rtp.demo.debtor.customer.notification.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import rtp.demo.debtor.domain.model.payment.serde.PaymentDeserializer;

@Component
public class DebtorCustomerNotificationRouteBuilder extends RouteBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(DebtorCustomerNotificationRouteBuilder.class);

	private String kafkaBootstrap = System.getenv("BOOTSTRAP_SERVERS");
	private String kafkaDebtorCompletedPaymentsTopic = System.getenv("DEBTOR_COMPLETED_PAYMENTS_TOPIC");
	private String consumerMaxPollRecords = System.getenv("CONSUMER_MAX_POLL_RECORDS");
	private String consumerCount = System.getenv("CONSUMER_COUNT");
	private String consumerSeekTo = System.getenv("CONSUMER_SEEK_TO");
	private String consumerGroup = System.getenv("CONSUMER_GROUP");

	@Override
	public void configure() throws Exception {
		LOG.info("Configuring Debtor Customer Notification Routes");

		KafkaComponent kafka = new KafkaComponent();
		kafka.setBrokers(kafkaBootstrap);
		this.getContext().addComponent("kafka", kafka);

		from("kafka:" + kafkaDebtorCompletedPaymentsTopic + "?brokers=" + kafkaBootstrap + "&maxPollRecords="
				+ consumerMaxPollRecords + "&consumersCount=" + consumerCount + "&seekTo=" + consumerSeekTo
				+ "&groupId=" + consumerGroup
				+ "&valueDeserializer=" + PaymentDeserializer.class.getName()).routeId("FromKafka")
						.log("\n/// Debtor Customer Notification stub service received payment >>> ${body}");
	}

}
