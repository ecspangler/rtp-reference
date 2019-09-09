package rtp.demo.debtor.complete.payment.stream;

import com.google.gson.Gson;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.jboss.resteasy.client.jaxrs.BasicAuthentication;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.engines.URLConnectionEngine;
import rtp.demo.creditor.domain.payments.Payment;
import rtp.demo.debtor.complete.payment.pojo.BusinessCentralTaskInterface;
import rtp.demo.debtor.complete.payment.pojo.CaseDataTransformer;
import rtp.demo.debtor.complete.payment.pojo.ElasticInterface;


import javax.net.ssl.*;
import javax.ws.rs.client.WebTarget;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


public class UpdateValidationStatusGlue {


	private static final Logger LOG = Logger.getLogger(UpdateValidationStatusGlue.class.getName());

	private String bootstrapServers = System.getenv("BOOTSTRAP_SERVERS");
	private String paymentsTopic = System.getenv("CREDITOR_PAYMENTS_TOPIC");
	private String glueTopic = System.getenv("CASE_ID_GLUE");
	private String applicationId = System.getenv("APPLICATION_ID");
	private String clientId = System.getenv("CLIENT_ID");
	private String bcPath = System.getenv("BC_PATH");
	private String bcUserName = System.getenv("BC_USER_NAME");
	private String bcPassword = System.getenv("BC_PASSWORD");

	private Properties streamsConfiguration = new Properties();
	private KafkaStreams streams;

	public UpdateValidationStatusGlue() {
		LOG.info("Configuring Debtor Payments Stream");




		streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
		streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, clientId);

		StreamsBuilder builder = new StreamsBuilder();

		KStream<String, String> paymentsStream = builder.stream(paymentsTopic,
				Consumed.with(Serdes.String(), Serdes.String()));

		KStream<String, String> rtpCaseListStream = builder.stream(glueTopic,
				Consumed.with(Serdes.String(),  Serdes.String()));

		KStream<String, String> joined = rtpCaseListStream.join(paymentsStream,
				(leftValue, rightValue) -> leftValue + "|" + rightValue, /* ValueJoiner */
				JoinWindows.of(TimeUnit.MINUTES.toMillis(5)),
				Serdes.String(), /* key */
				Serdes.String(),   /* left value */
				Serdes.String() /* right value */
		);



		joined.print();

		LOG.info("Joined stream: ");

		joined.foreach((key, value) -> {
			triggerAdhocTask(key, value);

		});
		CaseDataTransformer caseDataTransformer = new CaseDataTransformer();

		KStream<String, String> elasticStream = joined.map((k,v) -> new KeyValue<>(v.split("\\|")[0],caseDataTransformer.caseDataForElastic(v)));

		KStream<String, String> caseDataStream = elasticStream.map((k,v) -> new KeyValue<String,String>(k,invokeElasticURL(v)));
		caseDataStream.to("rtp-case-file-elastic");



		KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration);

		streams.start();

		Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

	}

	private void triggerAdhocTask(String key, String value) {
		try {
			LOG.info("triggering account validation completion" + key);
			ResteasyClient client = new ResteasyClientBuilder().httpEngine(new URLConnectionEngine()).build();
			WebTarget target = client.target(bcPath);
			ResteasyWebTarget tgt = (ResteasyWebTarget) target;
			tgt.register(new BasicAuthentication(bcUserName, bcPassword));
			BusinessCentralTaskInterface customerProxy = tgt.proxy(BusinessCentralTaskInterface.class);
			String[] val = value.split("\\|");
			Payment payment = new Gson().fromJson(val[1], Payment.class);
			customerProxy.triggerAdhocTask(new Gson().fromJson(val[0], String.class), "{\"caseFile_caseCompleted\":true}");
			customerProxy.closeCase(new Gson().fromJson(new Gson().fromJson(val[0], String.class), String.class));
		}catch (Exception e) {
			LOG.info(e.getMessage());
		}
	}

	private String invokeElasticURL(String value) {
		try {

			ResteasyClient client = new ResteasyClientBuilder().httpEngine(new URLConnectionEngine()).build();
			WebTarget target = client.target(bcPath);
			ResteasyWebTarget tgt = (ResteasyWebTarget) target;
			tgt.register(new BasicAuthentication(bcUserName, bcPassword));
			BusinessCentralTaskInterface customerProxy = tgt.proxy(BusinessCentralTaskInterface.class);
			String[] val = value.split("\\|");
			Payment payment = new Gson().fromJson(val[1], Payment.class);
			String caseFile = customerProxy.getCaseFile(new Gson().fromJson(val[0], String.class));
			LOG.info("caseFile" + caseFile);
			String caseMetrics = customerProxy.getCaseMetrics(new Gson().fromJson(val[0], String.class));


			LOG.info("caseFile" + caseMetrics);

			LinkedHashMap<String,Object> map = new Gson().fromJson(caseMetrics,LinkedHashMap.class);
			System.out.println("keyset"+map.keySet());

			caseFile = caseFile.substring(1,caseFile.length()-2);

			Double doubleStarted =(Double)map.get("case-started-at");
			Double doubleEnded = (Double)map.get("case-completed-at");
			Double duration = (doubleEnded-doubleStarted)/1000;
			System.out.println(doubleStarted);
			String slaViolation = (Double)map.get("case-sla-compliance") == 2.0 ? "Met" : "Violated";

			caseFile+=",\"case-duration\":"+duration+",\"case-sla-complaince\":\""+slaViolation+"\"";
			System.out.println("{"+caseFile+"}");

			return "{"+caseFile+"}";


		}catch (Exception e) {
			LOG.info(e.getMessage());
		}
		return null;
	}



	public void startStream() {
		LOG.info("Starting Stream");
		streams.start();
	}

	public void closeStream() {
		LOG.info("Stopping Stream");
		streams.close();
	}

}