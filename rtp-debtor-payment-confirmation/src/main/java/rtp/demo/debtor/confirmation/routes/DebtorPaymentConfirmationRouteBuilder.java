package rtp.demo.debtor.confirmation.routes;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaComponent;
import org.apache.camel.component.kafka.KafkaConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import rtp.demo.debtor.confirmation.beans.MessageStatusReportTransformer;
import rtp.demo.debtor.domain.rtp.simplified.MessageStatusReport;
import rtp.demo.debtor.domain.rtp.simplified.serde.MessageStatusReportSerializer;
import rtp.message.model.serde.FIToFIPaymentStatusReportV07Deserializer;

@Component
public class DebtorPaymentConfirmationRouteBuilder extends RouteBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(DebtorPaymentConfirmationRouteBuilder.class);

	private String kafkaBootstrap = System.getenv("BOOTSTRAP_SERVERS");
	private String kafkaMockRtpConfirmationTopic = System.getenv("MOCK_RTP_DEBTOR_CONFIRMATION_TOPIC");
	private String kafkaCreditorConfirmationTopic = System.getenv("DEBTOR_CONFIRMATION_TOPIC");
	private String consumerMaxPollRecords = System.getenv("CONSUMER_MAX_POLL_RECORDS");
	private String consumerCount = System.getenv("CONSUMER_COUNT");
	private String consumerSeekTo = System.getenv("CONSUMER_SEEK_TO");
	private String consumerGroup = System.getenv("CONSUMER_GROUP");

	@Override
	public void configure() throws Exception {
		LOG.info("Configuring Debtor Confirmation Routes");

		KafkaComponent kafka = new KafkaComponent();
		kafka.setBrokers(kafkaBootstrap);
		this.getContext().addComponent("kafka", kafka);

		from("kafka:" + kafkaMockRtpConfirmationTopic + "?brokers=" + kafkaBootstrap + "&maxPollRecords="
				+ consumerMaxPollRecords + "&consumersCount=" + consumerCount + "&seekTo=" + consumerSeekTo
				+ "&groupId=" + consumerGroup
				+ "&valueDeserializer=" + FIToFIPaymentStatusReportV07Deserializer.class.getName())
						.routeId("FromKafka").log("\n/// Creditor Confirmation Route >>> ${body}")
						.bean(MessageStatusReportTransformer.class, "toMessageStatusReport")
						.log("Sending payment message confirmation >>> ${body} >> key ${body.getOriginalMessageId}")
						.process(new Processor() {
							@Override
							public void process(Exchange exchange) throws Exception {
								exchange.getIn().setHeader(KafkaConstants.KEY,
										((MessageStatusReport) exchange.getIn().getBody()).getOriginalMessageId());
							}
						}).to("kafka:" + kafkaCreditorConfirmationTopic
								+ "?serializerClass=" + MessageStatusReportSerializer.class.getName());

	}
}
