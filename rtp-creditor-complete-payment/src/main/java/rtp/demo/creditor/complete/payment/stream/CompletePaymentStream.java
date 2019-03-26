package rtp.demo.creditor.complete.payment.stream;

import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

import org.apache.kafka.streams.Consumed;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;

import rtp.demo.creditor.domain.payments.Payment;
import rtp.demo.creditor.domain.payments.serde.PaymentSerde;
import rtp.demo.creditor.domain.rtp.simplified.MessageStatusReport;
import rtp.demo.creditor.domain.rtp.simplified.serde.MessageStatusReportSerde;
import rtp.demo.repository.CreditPaymentRepository;
import rtp.demo.repository.MySqlCreditPaymentRepository;

public class CompletePaymentStream {

	private static final Logger LOG = Logger.getLogger(CompletePaymentStream.class.getName());

	private String bootstrapServers = System.getenv("BOOTSTRAP_SERVERS");
	private String paymentsTopic = System.getenv("CREDITOR_PAYMENTS_TOPIC");
	private String completedPaymentsTopic = System.getenv("CREDITOR_COMPLETED_PAYMENTS_TOPIC");
	private String confirmationsTopic = System.getenv("CREDITOR_CONFIRMATION_TOPIC");
	private String applicationId = System.getenv("APPLICATION_ID");
	private String clientId = System.getenv("CLIENT_ID");

	private Properties streamsConfiguration = new Properties();
	private KafkaStreams streams;

	public CompletePaymentStream() {
		LOG.info("Configuring Creditor Payments Stream");

		final Serde<Payment> paymentSerde = new PaymentSerde();
		final Serde<MessageStatusReport> messageStatusReportSerde = new MessageStatusReportSerde();
		final CreditPaymentRepository creditPaymentRepository = new MySqlCreditPaymentRepository();

		streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
		streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, paymentSerde.getClass().getName());
		streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, clientId);

		StreamsBuilder builder = new StreamsBuilder();

		KStream<String, Payment> paymentsStream = builder.stream(paymentsTopic,
				Consumed.with(Serdes.String(), paymentSerde));

		KStream<String, MessageStatusReport> confirmationsStream = builder.stream(confirmationsTopic,
				Consumed.with(Serdes.String(), messageStatusReportSerde));

		KStream<String, Payment> completedPaymentsStream = paymentsStream.join(confirmationsStream,
				(payment, confirmation) -> new Payment(payment, confirmation, "COMPLETED"),
				JoinWindows.of(TimeUnit.MINUTES.toMillis(5)), Serdes.String(), paymentSerde, messageStatusReportSerde);

		paymentsStream.print();
		confirmationsStream.print();

		LOG.info("Joined stream: ");
		completedPaymentsStream.print();

		completedPaymentsStream.foreach((key, value) -> {
			LOG.info("Updating Payment: " + key);
			Payment debitPayment = creditPaymentRepository.getPaymentByPaymentKey(key);
			debitPayment.setStatus("COMPLETED");
			creditPaymentRepository.updatePayment(debitPayment);
		});

		completedPaymentsStream.to(completedPaymentsTopic);

		KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration);

		streams.start();

		Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

	}

	public void startStream() {
		LOG.info("Starting Creditor Payments Stream");
		streams.start();
	}

	public void closeStream() {
		LOG.info("Stopping Creditor Payments Stream");
		streams.close();
	}

}
