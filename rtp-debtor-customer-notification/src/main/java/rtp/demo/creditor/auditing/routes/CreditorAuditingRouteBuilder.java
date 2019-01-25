package rtp.demo.creditor.auditing.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CreditorAuditingRouteBuilder extends RouteBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(CreditorAuditingRouteBuilder.class);

	// private String kafkaBootstrap = System.getenv("BOOTSTRAP_SERVERS");
	// private String kafkaConsumerTopic = System.getenv("CONSUMER_TOPIC");
	private String kafkaBootstrap = "172.30.27.66:9092";

	private String kafkaConsumerTopic = "creditor-post-validation";
	private String consumerGroup = "rtp-creditor-auditing-app";
	private String consumerMaxPollRecords = "5000";
	private String consumerCount = "1";
	private String consumerSeekTo = "end";

	@Override
	public void configure() throws Exception {
		LOG.info("Configuring Creditor Intake Routes");

		KafkaComponent kafka = new KafkaComponent();
		kafka.setBrokers(kafkaBootstrap);
		this.getContext().addComponent("kafka", kafka);

		from("kafka:" + kafkaConsumerTopic + "?brokers=" + kafkaBootstrap + "&maxPollRecords=" + consumerMaxPollRecords
				+ "&consumersCount=" + consumerCount + "&seekTo=" + consumerSeekTo + "&groupId=" + consumerGroup
				+ "&valueDeserializer=rtp.demo.creditor.domain.payments.serde.PaymentDeserializer").routeId("FromKafka")
						.log("\n/// Creditor Auditing Route >>> ${body}");
	}

	public String getKafkaBootstrap() {
		return kafkaBootstrap;
	}

	public void setKafkaBootstrap(String kafkaBootstrap) {
		this.kafkaBootstrap = kafkaBootstrap;
	}

	public String getKafkaConsumerTopic() {
		return kafkaConsumerTopic;
	}

	public void setKafkaConsumerTopic(String kafkaConsumerTopic) {
		this.kafkaConsumerTopic = kafkaConsumerTopic;
	}

	public String getConsumerGroup() {
		return consumerGroup;
	}

	public void setConsumerGroup(String consumerGroup) {
		this.consumerGroup = consumerGroup;
	}

	public String getConsumerMaxPollRecords() {
		return consumerMaxPollRecords;
	}

	public void setConsumerMaxPollRecords(String consumerMaxPollRecords) {
		this.consumerMaxPollRecords = consumerMaxPollRecords;
	}

	public String getConsumerCount() {
		return consumerCount;
	}

	public void setConsumerCount(String consumerCount) {
		this.consumerCount = consumerCount;
	}

	public String getConsumerSeekTo() {
		return consumerSeekTo;
	}

	public void setConsumerSeekTo(String consumerSeekTo) {
		this.consumerSeekTo = consumerSeekTo;
	}

}
