package rtp.demo.debtor.complete.payment.stream;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

import org.apache.kafka.streams.Consumed;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import java.util.logging.Logger;

import rtp.demo.debtor.domain.model.payment.DebitPayment;
import rtp.demo.debtor.domain.model.payment.Payment;
import rtp.demo.debtor.domain.model.payment.serde.PaymentSerde;
import rtp.demo.debtor.domain.rtp.simplified.MessageStatusReport;
import rtp.demo.debtor.domain.rtp.simplified.serde.MessageStatusReportSerde;
import rtp.demo.repository.DebitPaymentRepository;
import rtp.demo.repository.MySqlDebitPaymentRepository;

public class CompletePaymentStream {

	private static final Logger LOG = Logger.getLogger(CompletePaymentStream.class.getName());

	private String bootstrapServers = System.getenv("BOOTSTRAP_SERVERS");
	private String paymentsTopic = System.getenv("DEBTOR_PAYMENTS_TOPIC");
	private String completedPaymentsTopic = System.getenv("DEBTOR_COMPLETED_PAYMENTS_TOPIC");
	private String confirmationsTopic = System.getenv("DEBTOR_CONFIRMATION_TOPIC");
	private String applicationId = System.getenv("APPLICATION_ID");
	private String clientId = System.getenv("CLIENT_ID");

	private Properties streamsConfiguration = new Properties();
	private KafkaStreams streams;

	public CompletePaymentStream() {
		LOG.info("Configuring Debtor Payments Stream");

		final Serde<Payment> paymentSerde = new PaymentSerde();
		final Serde<MessageStatusReport> messageStatusReportSerde = new MessageStatusReportSerde();
		final DebitPaymentRepository debitPaymentRepository = new MySqlDebitPaymentRepository();

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
				(payment, confirmation) -> new Payment(payment, confirmation),
				JoinWindows.of(TimeUnit.MINUTES.toMillis(5)), Serdes.String(), paymentSerde, messageStatusReportSerde);

		try {
			paymentsStream.print();
			confirmationsStream.print();

			LOG.info("Joined stream: ");
			completedPaymentsStream.print();

			completedPaymentsStream.foreach((key, value) -> {
				LOG.info("Updating Payment Key: " + key);
				LOG.info("Updating Payment: " + value);

				DebitPayment debitPayment = debitPaymentRepository.getPaymentByPaymentKey(key);
				if (debitPayment != null) {
					debitPayment.setStatus(value.getStatus());
					debitPaymentRepository.updatePayment(debitPayment);
				} else {
					LOG.info("Payment not retrieved by key: " + key);
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					debitPayment = debitPaymentRepository.getPaymentByPaymentKey(key);
					debitPayment.setStatus(value.getStatus());
					debitPaymentRepository.updatePayment(debitPayment);
				}
			});

			completedPaymentsStream.to(completedPaymentsTopic);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {

			KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration);

			streams.start();

			Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void startStream() {
		LOG.info("Starting Debttor Payments Stream");
		streams.start();
	}

	public void closeStream() {
		LOG.info("Stopping Debtor Payments Stream");
		streams.close();
	}

}