package rtp.demo.creditor.intake.routes;

import java.util.logging.Logger;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaComponent;
import org.apache.camel.component.kafka.KafkaConstants;

import rtp.demo.creditor.domain.payments.serde.PaymentSerializer;
import rtp.demo.creditor.repository.account.AccountRepository;
import rtp.demo.creditor.repository.account.JdgAccountRepository;
import rtp.demo.repository.CreditPaymentRepository;
import rtp.demo.repository.MySqlCreditPaymentRepository;
import rtp.demo.creditor.domain.account.Account;
import rtp.demo.creditor.domain.payments.Payment;
import rtp.demo.creditor.intake.beans.CreditTransferMessageTransformer;
import rtp.demo.creditor.intake.beans.CreditTransferMessageValidationBean;
import rtp.demo.creditor.intake.beans.PaymentTransformer;

import rtp.message.model.serde.FIToFICustomerCreditTransferV06Deserializer;

public class CreditorIntakeRouteBuilder extends RouteBuilder {

	private static final Logger LOG = Logger.getLogger(CreditorIntakeRouteBuilder.class.getName());

	private String kafkaBootstrap = System.getenv("BOOTSTRAP_SERVERS");
	private String kafkaCreditTransferCreditorTopic = System.getenv("CREDIT_TRANS_CREDITOR_TOPIC");
	private String kafkaCreditorPaymentsTopic = System.getenv("CREDITOR_PAYMENTS_TOPIC");
	private String consumerMaxPollRecords = System.getenv("CONSUMER_MAX_POLL_RECORDS");
	private String consumerCount = System.getenv("CONSUMER_COUNT");
	private String consumerSeekTo = System.getenv("CONSUMER_SEEK_TO");
	private String consumerGroup = System.getenv("CONSUMER_GROUP");

	private AccountRepository accountRepository = new JdgAccountRepository();

	@Override
	public void configure() throws Exception {
		LOG.info("Configuring Creditor Intake Routes");

		// Populate test data, valid accounts for the reference example
		Account testAccount1 = new Account();
		testAccount1.setAccountNumber("wlaw");
		accountRepository.addAccount(testAccount1);

		Account testAccount2 = new Account();
		testAccount2.setAccountNumber("ashaw");
		accountRepository.addAccount(testAccount2);

		Account testAccount3 = new Account();
		testAccount3.setAccountNumber("mkemp");
		accountRepository.addAccount(testAccount3);

		Account testAccount4 = new Account();
		testAccount4.setAccountNumber("egarcia");
		accountRepository.addAccount(testAccount4);

		Account testAccount5 = new Account();
		testAccount5.setAccountNumber("syoung");
		accountRepository.addAccount(testAccount5);

		KafkaComponent kafka = new KafkaComponent();
		kafka.setBrokers(kafkaBootstrap);
		this.getContext().addComponent("kafka", kafka);
		LOG.info(kafkaCreditorPaymentsTopic);

		from("kafka:" + kafkaCreditTransferCreditorTopic + "?brokers=" + kafkaBootstrap + "&maxPollRecords="
				+ consumerMaxPollRecords + "&consumersCount=" + consumerCount + "&seekTo=" + consumerSeekTo
				+ "&groupId=" + consumerGroup + "&valueDeserializer="
				+ FIToFICustomerCreditTransferV06Deserializer.class.getName()).routeId("FromKafka")
						.log("\n/// Creditor Intake Route >>> ${body}")
						.process(exchange -> LOG.info(exchange.getIn().getHeader(KafkaConstants.KEY).toString()))
						.bean(CreditTransferMessageTransformer.class, "toCreditTransferMessage").log(" >>> ${body}")
						.log(" Retrieved message >>> ${body}")
						.bean(CreditTransferMessageValidationBean.class, "validateCreditTransferMessage")
						.log(" Validated payment >>> ${body}").bean(PaymentTransformer.class, "toPayment")
						.log(" Transformed payment >>> ${body}  >> key ${body.getCreditTransferMessageId}")
						.process(new Processor() {
							@Override
							public void process(Exchange exchange) throws Exception {
								exchange.getIn().setHeader(KafkaConstants.KEY,
										((Payment) exchange.getIn().getBody()).getCreditTransferMessageId());
							}
						}).log(" Sending payment >>> ${body}").to("kafka:" + kafkaCreditorPaymentsTopic
								+ "?serializerClass=" + PaymentSerializer.class.getName());
	}

}
