package rtp.demo.mock.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import rtp.message.model.serde.FIToFICustomerCreditTransferV06Deserializer;
import rtp.message.model.serde.FIToFICustomerCreditTransferV06Serializer;
import rtp.message.model.serde.FIToFIPaymentStatusReportV07Deserializer;
import rtp.message.model.serde.FIToFIPaymentStatusReportV07Serializer;

@Component
public class MockRtpRouteBuilder extends RouteBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(MockRtpRouteBuilder.class);

	private String kafkaBootstrap = System.getenv("BOOTSTRAP_SERVERS");
	private String kafkaDebtorCreditTransferTopic = System.getenv("CREDIT_TRANS_DEBTOR_TOPIC");
	private String kafkaDebtorConfirmationTopic = System.getenv("DEBTOR_CONFIRMATION_TOPIC");
	private String kafkaCreditorCreditTransferTopic = System.getenv("CREDIT_TRANS_CREDITOR_TOPIC");
	private String kafkaCreditorAcknowledgementTopic = System.getenv("CREDITOR_ACK_TOPIC");
	private String kafkaCreditorConfirmationTopic = System.getenv("CREDITOR_CONFIRMATION_TOPIC");
	private String consumerMaxPollRecords = System.getenv("CONSUMER_MAX_POLL_RECORDS");
	private String consumerCount = System.getenv("CONSUMER_COUNT");
	private String consumerSeekTo = System.getenv("CONSUMER_SEEK_TO");
	private String consumerGroup = System.getenv("CONSUMER_GROUP");

	@Override
	public void configure() throws Exception {
		LOG.info("Configuring Mock RTP Routes");

		KafkaComponent kafka = new KafkaComponent();
		kafka.setBrokers(kafkaBootstrap);

		this.getContext().addComponent("kafka", kafka);

		from("kafka:" + kafkaDebtorCreditTransferTopic + "?brokers=" + kafkaBootstrap + "&maxPollRecords="
				+ consumerMaxPollRecords + "&consumersCount=" + consumerCount + "&seekTo=" + consumerSeekTo
				+ "&groupId=" + consumerGroup
				+ "&valueDeserializer=" + FIToFICustomerCreditTransferV06Deserializer.class.getName())
						.log("\\n/// Mock RTP - Sending Credit Transfer Message >>> ${body}")
						.to("kafka:" + kafkaCreditorCreditTransferTopic
								+ "?serializerClass=" + FIToFICustomerCreditTransferV06Serializer.class.getName());

		from("kafka:" + kafkaCreditorAcknowledgementTopic + "?brokers=" + kafkaBootstrap + "&maxPollRecords="
				+ consumerMaxPollRecords + "&consumersCount=" + consumerCount + "&seekTo=" + consumerSeekTo
				+ "&groupId=" + consumerGroup
				+ "&valueDeserializer=" + FIToFIPaymentStatusReportV07Deserializer.class.getName())
						.routeId("FromKafka").log("\n/// Mock RTP - Receiving Acknowledgements >>> ${body}")
						.to("kafka:" + kafkaDebtorConfirmationTopic
								+ "?serializerClass=" + FIToFIPaymentStatusReportV07Serializer.class.getName())
						.to("kafka:" + kafkaCreditorConfirmationTopic
								+ "?serializerClass=" + FIToFIPaymentStatusReportV07Serializer.class.getName());

	}

}
