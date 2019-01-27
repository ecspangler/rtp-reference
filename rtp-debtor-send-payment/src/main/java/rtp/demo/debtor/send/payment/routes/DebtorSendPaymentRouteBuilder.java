package rtp.demo.debtor.send.payment.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rtp.demo.debtor.send.payment.beans.PaymentTransformer;

public class DebtorSendPaymentRouteBuilder extends RouteBuilder {
	private static final Logger LOG = LoggerFactory.getLogger(DebtorSendPaymentRouteBuilder.class);

	private String kafkaBootstrap = System.getenv("BOOTSTRAP_SERVERS");
	private String kafkaMockRtpCreditTransferTopic = System.getenv("MOCK_RTP_CREDIT_TRANSFER_TOPIC");
	private String kafkaDebtorPaymentsTopic = System.getenv("DEBTOR_PAYMENTS_TOPIC");
	private String deserializerClass = System.getenv("DESERIALIZER_CLASS");
	private String serializerClass = System.getenv("SERIALIZER_CLASS");
	private String consumerMaxPollRecords = System.getenv("CONSUMER_MAX_POLL_RECORDS");
	private String consumerCount = System.getenv("CONSUMER_COUNT");
	private String consumerSeekTo = System.getenv("CONSUMER_SEEK_TO");
	private String consumerGroup = System.getenv("CONSUMER_GROUP");

	@Override
	public void configure() throws Exception {
		LOG.info("Configuring Debtor Send Payment Routes");

		KafkaComponent kafka = new KafkaComponent();
		kafka.setBrokers(kafkaBootstrap);
		this.getContext().addComponent("kafka", kafka);

		from("kafka:" + kafkaDebtorPaymentsTopic + "?brokers=" + kafkaBootstrap + "&maxPollRecords="
				+ consumerMaxPollRecords + "&consumersCount=" + consumerCount + "&seekTo=" + consumerSeekTo
				+ "&groupId=" + consumerGroup + "&valueDeserializer=" + deserializerClass).routeId("FromKafka")
						.log("\n/// Payments to be sent to  RTP  >>> ${body}")
						.bean(PaymentTransformer.class, "toRtpCreditTransferMessage")
						.log("Transformed to Credit Transfer Message >>> ${body}")
						.to("kafka:" + kafkaMockRtpCreditTransferTopic + "?serializerClass=" + serializerClass);
	}

}
