package rtp.demo.workflow.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpClientConfigurer;
import org.apache.camel.component.http4.HttpComponent;
import org.apache.camel.component.kafka.KafkaComponent;
import org.apache.camel.util.jsse.KeyStoreParameters;
import org.apache.camel.util.jsse.SSLContextParameters;
import org.apache.camel.util.jsse.TrustManagersParameters;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import rtp.demo.workflow.transformer.ParseFITToFICustomerCreditTransferMsg;
import rtp.message.model.serde.FIToFICustomerCreditTransferV06Deserializer;
import sun.net.www.http.HttpClient;

@Component
public class RtpWorkflowRouteBuilder extends RouteBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(RtpWorkflowRouteBuilder.class);

	private String kafkaBootstrap = System.getenv("BOOTSTRAP_SERVERS");
	private String kafkaCreditorCompletedPaymentsTopic = System.getenv("CREDIT_TRANS_CREDITOR_TOPIC");
	private String consumerMaxPollRecords = System.getenv("CONSUMER_MAX_POLL_RECORDS");
	private String consumerCount = System.getenv("CONSUMER_COUNT");
	private String consumerSeekTo = System.getenv("CONSUMER_SEEK_TO");
	private String consumerGroup = System.getenv("CONSUMER_GROUP");
	private String bcHost = System.getenv("BC_HOST");

	@Override
	public void configure() throws Exception {
		LOG.info("Configuring Creditor Core Banking Routes");
		String startCase = "rest:post:/services/rest/server/containers/RTPProcessingEngine_1.0.0-SNAPSHOT/cases/RTPProcessingEngine.RTPWorkflow/instances?" +
				bcHost +
				"&produces=application/json";

		HttpComponent httpComponent = getContext().getComponent("https4", HttpComponent.class);


		KafkaComponent kafka = new KafkaComponent();
		kafka.setBrokers(kafkaBootstrap);
		this.getContext().addComponent("kafka", kafka);

		try {

			from("kafka:" + kafkaCreditorCompletedPaymentsTopic + "?brokers=" + kafkaBootstrap + "&maxPollRecords="
					+ consumerMaxPollRecords + "&consumersCount=" + consumerCount + "&seekTo=" + consumerSeekTo
					+ "&groupId=" + consumerGroup
			).routeId("FromKafka")
					.removeHeader("*")
					.to("rest:post:/rtp/rt?host=https://txelgef3x4:4fqo676p85@app-name-kafka-2058344660.us-east-1.bonsaisearch.net:443&produces=application/json");
			;
		}catch (Exception e) {

		}


	}

}
