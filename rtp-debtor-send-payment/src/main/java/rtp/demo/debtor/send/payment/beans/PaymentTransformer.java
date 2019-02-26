package rtp.demo.debtor.send.payment.beans;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

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
import rtp.demo.debtor.domain.model.payment.Payment;

public class PaymentTransformer {

	public FIToFICustomerCreditTransferV06 toRtpCreditTransferMessage(Payment payment) {
		FIToFICustomerCreditTransferV06 rtpCreditTransferMessage = new FIToFICustomerCreditTransferV06();
		rtpCreditTransferMessage.setGrpHdr(new GroupHeader70());

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
		LocalDateTime now = LocalDateTime.now();

		// Set Credit Transfer Message Id
		rtpCreditTransferMessage.getGrpHdr().setMsgId(payment.getPaymentId());

		// Set Message Created Date Time
		try {
			rtpCreditTransferMessage.getGrpHdr().setCreDtTm(DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(GregorianCalendar.from(ZonedDateTime.of(now, ZoneId.systemDefault()))));
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}

		// Set Number of Transactions
		rtpCreditTransferMessage.getGrpHdr().setNbOfTxs("1");

		// Set Debtor Id
		rtpCreditTransferMessage.getGrpHdr().setInstgAgt(new BranchAndFinancialInstitutionIdentification5());
		rtpCreditTransferMessage.getGrpHdr().getInstgAgt().setFinInstnId(new FinancialInstitutionIdentification8());
		rtpCreditTransferMessage.getGrpHdr().getInstgAgt().getFinInstnId()
				.setClrSysMmbId(new ClearingSystemMemberIdentification2());
		rtpCreditTransferMessage.getGrpHdr().getInstgAgt().getFinInstnId()
				.setOthr(new GenericFinancialIdentification1());
		rtpCreditTransferMessage.getGrpHdr().getInstgAgt().getFinInstnId().getOthr().setId("021200201");

		// Set Creditor Id
		rtpCreditTransferMessage.getGrpHdr().setInstdAgt(new BranchAndFinancialInstitutionIdentification5());
		rtpCreditTransferMessage.getGrpHdr().getInstdAgt().setFinInstnId(new FinancialInstitutionIdentification8());
		rtpCreditTransferMessage.getGrpHdr().getInstdAgt().getFinInstnId()
				.setClrSysMmbId(new ClearingSystemMemberIdentification2());
		rtpCreditTransferMessage.getGrpHdr().getInstdAgt().getFinInstnId()
				.setOthr(new GenericFinancialIdentification1());
		rtpCreditTransferMessage.getGrpHdr().getInstdAgt().getFinInstnId().getOthr()
				.setId(payment.getReceiverRoutingNumber());

		// Set Payment Amount and Currency
		ActiveCurrencyAndAmount activeCurrencyAndAmount = new ActiveCurrencyAndAmount();
		activeCurrencyAndAmount.setValue(payment.getAmount());
		activeCurrencyAndAmount.setCcy("USD");
		rtpCreditTransferMessage.getGrpHdr().setTtlIntrBkSttlmAmt(activeCurrencyAndAmount);

		// Set Settlement Method
		rtpCreditTransferMessage.getGrpHdr().setSttlmInf(new SettlementInstruction4());
		rtpCreditTransferMessage.getGrpHdr().getSttlmInf().setSttlmMtd(SettlementMethod1Code.CLRG);

		// Set End-to-End Id and Payment Instruction Id
		CreditTransferTransaction25 creditTransferTransaction25 = new CreditTransferTransaction25();
		creditTransferTransaction25.setPmtId(new PaymentIdentification3());
		creditTransferTransaction25.getPmtId().setEndToEndId("E2E-Ref001");
		creditTransferTransaction25.getPmtId().setInstrId(formatter.format(now));

		// Set Creditor Account Number
		creditTransferTransaction25.setCdtrAcct(new CashAccount24());
		creditTransferTransaction25.getCdtrAcct().setId(new AccountIdentification4Choice());
		creditTransferTransaction25.getCdtrAcct().getId().setOthr(new GenericAccountIdentification1());
		creditTransferTransaction25.getCdtrAcct().getId().getOthr().setId(payment.getReceiverAccountNumber());

		// Set Debtor Account Number
		creditTransferTransaction25.setDbtrAcct(new CashAccount24());
		creditTransferTransaction25.getDbtrAcct().setId(new AccountIdentification4Choice());
		creditTransferTransaction25.getDbtrAcct().getId().setOthr(new GenericAccountIdentification1());
		creditTransferTransaction25.getDbtrAcct().getId().getOthr().setId(payment.getSenderAccountNumber());

		rtpCreditTransferMessage.getCdtTrfTxInf().add(creditTransferTransaction25);

		return rtpCreditTransferMessage;
	}

}
