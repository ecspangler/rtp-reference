package rtp.demo.mock.routes;

import java.math.BigDecimal;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import iso.std.iso._20022.tech.xsd.pacs_008_001.AccountIdentification4Choice;
import iso.std.iso._20022.tech.xsd.pacs_008_001.ActiveCurrencyAndAmount;
import iso.std.iso._20022.tech.xsd.pacs_008_001.BranchAndFinancialInstitutionIdentification5;
import iso.std.iso._20022.tech.xsd.pacs_008_001.CashAccount24;
import iso.std.iso._20022.tech.xsd.pacs_008_001.ClearingSystemMemberIdentification2;
import iso.std.iso._20022.tech.xsd.pacs_008_001.CreditTransferTransaction25;
import iso.std.iso._20022.tech.xsd.pacs_008_001.FIToFICustomerCreditTransferV06;
import iso.std.iso._20022.tech.xsd.pacs_008_001.FinancialInstitutionIdentification8;
import iso.std.iso._20022.tech.xsd.pacs_008_001.GenericAccountIdentification1;
import iso.std.iso._20022.tech.xsd.pacs_008_001.GenericFinancialIdentification1;
import iso.std.iso._20022.tech.xsd.pacs_008_001.GroupHeader70;
import iso.std.iso._20022.tech.xsd.pacs_008_001.PaymentIdentification3;
import iso.std.iso._20022.tech.xsd.pacs_008_001.SettlementInstruction4;
import iso.std.iso._20022.tech.xsd.pacs_008_001.SettlementMethod1Code;

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
						.to("kafka:" + kafkaCreditTransferCreditorTopic
								+ "?serializerClass=rtp.message.model.serde.FIToFICustomerCreditTransferV06Serializer");

		from("kafka:" + kafkaCreditorAcknowledgementTopic + "?brokers=" + kafkaBootstrap + "&maxPollRecords="
				+ consumerMaxPollRecords + "&consumersCount=" + consumerCount + "&seekTo=" + consumerSeekTo
				+ "&groupId=" + consumerGroup
				+ "&valueDeserializer=rtp.message.model.serde.FIToFIPaymentStatusReportV07Deserializer")
						.routeId("FromKafka").log("\n/// Mock RTP - Receiving Acknowledgements >>> ${body}")
						.to("kafka:" + kafkaDebtorConfirmationTopic
								+ "?serializerClass=rtp.message.model.serde.FIToFIPaymentStatusReportV07Serializer")
						.to("kafka:" + kafkaCreditorConfirmationTopic
								+ "?serializerClass=rtp.message.model.serde.FIToFIPaymentStatusReportV07Serializer");

	}

	public String getKafkaBootstrap() {
		return kafkaBootstrap;
	}

	public void setKafkaBootstrap(String kafkaBootstrap) {
		this.kafkaBootstrap = kafkaBootstrap;
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

	private FIToFICustomerCreditTransferV06 makeDummyRtpCreditTransferMessage() throws DatatypeConfigurationException {
		FIToFICustomerCreditTransferV06 dummyRtpCreditTransferMessage = new FIToFICustomerCreditTransferV06();
		dummyRtpCreditTransferMessage.setGrpHdr(new GroupHeader70());

		// Set Credit Transfer Message Id
		dummyRtpCreditTransferMessage.getGrpHdr().setMsgId("M2015111511021200201BFFF00000000001");

		// Set Message Created Date Time
		dummyRtpCreditTransferMessage.getGrpHdr()
				.setCreDtTm(DatatypeFactory.newInstance().newXMLGregorianCalendar("2015-11-12T10:05:00"));

		// Set Number of Transactions
		dummyRtpCreditTransferMessage.getGrpHdr().setNbOfTxs("1");

		// Set Debtor Id
		dummyRtpCreditTransferMessage.getGrpHdr().setInstgAgt(new BranchAndFinancialInstitutionIdentification5());
		dummyRtpCreditTransferMessage.getGrpHdr().getInstgAgt()
				.setFinInstnId(new FinancialInstitutionIdentification8());
		dummyRtpCreditTransferMessage.getGrpHdr().getInstgAgt().getFinInstnId()
				.setClrSysMmbId(new ClearingSystemMemberIdentification2());
		dummyRtpCreditTransferMessage.getGrpHdr().getInstgAgt().getFinInstnId()
				.setOthr(new GenericFinancialIdentification1());
		dummyRtpCreditTransferMessage.getGrpHdr().getInstgAgt().getFinInstnId().getOthr().setId("021200201");

		// Set Creditor Id
		dummyRtpCreditTransferMessage.getGrpHdr().setInstdAgt(new BranchAndFinancialInstitutionIdentification5());
		dummyRtpCreditTransferMessage.getGrpHdr().getInstdAgt()
				.setFinInstnId(new FinancialInstitutionIdentification8());
		dummyRtpCreditTransferMessage.getGrpHdr().getInstdAgt().getFinInstnId()
				.setClrSysMmbId(new ClearingSystemMemberIdentification2());
		dummyRtpCreditTransferMessage.getGrpHdr().getInstdAgt().getFinInstnId()
				.setOthr(new GenericFinancialIdentification1());
		dummyRtpCreditTransferMessage.getGrpHdr().getInstdAgt().getFinInstnId().getOthr().setId("020010001");

		// Set Payment Amount and Currency
		ActiveCurrencyAndAmount activeCurrencyAndAmount = new ActiveCurrencyAndAmount();
		activeCurrencyAndAmount.setValue(new BigDecimal("525.25"));
		activeCurrencyAndAmount.setCcy("USD");
		dummyRtpCreditTransferMessage.getGrpHdr().setTtlIntrBkSttlmAmt(activeCurrencyAndAmount);

		// Set Settlement Method
		dummyRtpCreditTransferMessage.getGrpHdr().setSttlmInf(new SettlementInstruction4());
		dummyRtpCreditTransferMessage.getGrpHdr().getSttlmInf().setSttlmMtd(SettlementMethod1Code.CLRG);

		// Set End-to-End Id and Payment Instruction Id
		CreditTransferTransaction25 creditTransferTransaction25 = new CreditTransferTransaction25();
		creditTransferTransaction25.setPmtId(new PaymentIdentification3());
		creditTransferTransaction25.getPmtId().setEndToEndId("E2E-Ref001");
		creditTransferTransaction25.getPmtId().setInstrId("2015111511021200201BFFFF00000000001");

		// Set Creditor Account Number
		creditTransferTransaction25.setCdtrAcct(new CashAccount24());
		creditTransferTransaction25.getCdtrAcct().setId(new AccountIdentification4Choice());
		creditTransferTransaction25.getCdtrAcct().getId().setOthr(new GenericAccountIdentification1());
		creditTransferTransaction25.getCdtrAcct().getId().getOthr().setId("12000194212199001");

		// Set Debtor Account Number
		creditTransferTransaction25.setDbtrAcct(new CashAccount24());
		creditTransferTransaction25.getDbtrAcct().setId(new AccountIdentification4Choice());
		creditTransferTransaction25.getDbtrAcct().getId().setOthr(new GenericAccountIdentification1());
		creditTransferTransaction25.getDbtrAcct().getId().getOthr().setId("12000194212199001");

		dummyRtpCreditTransferMessage.getCdtTrfTxInf().add(creditTransferTransaction25);

		return dummyRtpCreditTransferMessage;
	}

}
