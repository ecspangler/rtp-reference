package rtp.demo.debtor.corebanking.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import rtp.demo.debtor.domain.model.payment.serde.PaymentDeserializer;

@Component
public class DebtorCoreBankingRouteBuilder extends RouteBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(DebtorCoreBankingRouteBuilder.class);

	private String kafkaBootstrap = System.getenv("BOOTSTRAP_SERVERS");
	private String kafkaDebtorCompletedPaymentsTopic = System.getenv("DEBTOR_COMPLETED_PAYMENTS_TOPIC");
	private String consumerMaxPollRecords = System.getenv("CONSUMER_MAX_POLL_RECORDS");
	private String consumerCount = System.getenv("CONSUMER_COUNT");
	private String consumerSeekTo = System.getenv("CONSUMER_SEEK_TO");
	private String consumerGroup = System.getenv("CONSUMER_GROUP");

	@Override
	public void configure() throws Exception {
		LOG.info("Configuring Debtor Core Banking Routes");

		KafkaComponent kafka = new KafkaComponent();
		kafka.setBrokers(kafkaBootstrap);
		this.getContext().addComponent("kafka", kafka);

		from("kafka:" + kafkaDebtorCompletedPaymentsTopic + "?brokers=" + kafkaBootstrap + "&maxPollRecords="
				+ consumerMaxPollRecords + "&consumersCount=" + consumerCount + "&seekTo=" + consumerSeekTo
				+ "&groupId=" + consumerGroup
				+ "&valueDeserializer=" + PaymentDeserializer.class.getName()).routeId("FromKafka")
						.log("\n/// Debtor Core Banking stub service received payment >>> ${body}");
	}

}
