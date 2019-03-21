package rtp.demo.mock.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaComponent;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MockRtpRouteBuilder extends RouteBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(MockRtpRouteBuilder.class);

	private String kafkaBootstrap = System.getenv("BOOTSTRAP_SERVERS");
	private String kafkaCreditTransferDebtorTopic = System.getenv("CREDIT_TRANS_DEBTOR_TOPIC");
	private String kafkaCreditTransferCreditorTopic = System.getenv("CREDIT_TRANS_CREDITOR_TOPIC");
	private String kafkaCreditorAcknowledgementTopic = System.getenv("CREDITOR_ACK_TOPIC");
	private String kafkaDebtorConfirmationTopic = System.getenv("DEBTOR_CONFIRMATION_TOPIC");
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

		from("kafka:" + kafkaCreditTransferDebtorTopic + "?brokers=" + kafkaBootstrap + "&maxPollRecords="
				+ consumerMaxPollRecords + "&consumersCount=" + consumerCount + "&seekTo=" + consumerSeekTo
				+ "&groupId=" + consumerGroup
				+ "&valueDeserializer=rtp.message.model.serde.FIToFICustomerCreditTransferV06Deserializer")
						.log("\\n/// Mock RTP - Sending Credit Transfer Message >>> ${body}")
						.process(exchange -> {
							Object key = exchange.getIn().getHeader(KafkaConstants.KEY);
							LOG.info("Key: " + key + " value: " + new String((byte[]) key));

							LOG.info("From Topic: " + new String((byte[]) exchange.getIn().getHeader(KafkaConstants.TOPIC)));
							exchange.getIn().setHeader(KafkaConstants.TOPIC, kafkaCreditTransferCreditorTopic);
							exchange.getIn().setHeader(KafkaConstants.KEY, new String((byte[]) exchange.getIn().getHeader(KafkaConstants.KEY)));

							LOG.info("To Topic: " + kafkaCreditTransferCreditorTopic);
						})
						.to("kafka:" + kafkaCreditTransferCreditorTopic
								+ "?serializerClass=rtp.message.model.serde.FIToFICustomerCreditTransferV06Serializer");

//		from("kafka:" + kafkaCreditorAcknowledgementTopic + "?brokers=" + kafkaBootstrap + "&maxPollRecords="
//				+ consumerMaxPollRecords + "&consumersCount=" + consumerCount + "&seekTo=" + consumerSeekTo
//				+ "&groupId=" + consumerGroup
//				+ "&valueDeserializer=rtp.message.model.serde.FIToFIPaymentStatusReportV07Deserializer")
//						.routeId("FromKafka").log("\n/// Mock RTP - Receiving Acknowledgements >>> ${body}")
//						.to("kafka:" + kafkaDebtorConfirmationTopic
//								+ "?serializerClass=rtp.message.model.serde.FIToFIPaymentStatusReportV07Serializer")
//						.to("kafka:" + kafkaCreditorConfirmationTopic
//								+ "?serializerClass=rtp.message.model.serde.FIToFIPaymentStatusReportV07Serializer");

	}

}
