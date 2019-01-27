package rtp.demo.creditor.acknowledgement.beans;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import iso.std.iso._20022.tech.xsd.pacs_002_001.FIToFIPaymentStatusReportV07;
import iso.std.iso._20022.tech.xsd.pacs_002_001.GroupHeader53;
import iso.std.iso._20022.tech.xsd.pacs_002_001.PaymentTransaction63;
import iso.std.iso._20022.tech.xsd.pacs_002_001.StatusReason6Choice;
import iso.std.iso._20022.tech.xsd.pacs_002_001.StatusReasonInformation9;
import iso.std.iso._20022.tech.xsd.pacs_002_001.TransactionIndividualStatus3Code;
import rtp.demo.creditor.domain.error.PaymentValidationError;
import rtp.demo.creditor.domain.payments.Payment;

public class MessageStatusReportTransformer {

	public FIToFIPaymentStatusReportV07 toMessageStatusReport(Payment payment) {
		FIToFIPaymentStatusReportV07 messageStatusReport = new FIToFIPaymentStatusReportV07();
		PaymentValidationError validationError = null;

		System.out.println("PAYMENT: " + payment);

		if (payment.getErrors().size() > 0) {
			validationError = payment.getErrors().get(0);
		}

		messageStatusReport.setGrpHdr(new GroupHeader53());

		// Payment status key generated based on timestamp for the reference example
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
		LocalDateTime now = LocalDateTime.now();
		messageStatusReport.getGrpHdr().setMsgId("PAYMENT_STATUS" + formatter.format(now));

		PaymentTransaction63 transaction = new PaymentTransaction63();
		transaction.setOrgnlInstrId(payment.getCreditTransferMessageId());
		if (validationError != null) {
			TransactionIndividualStatus3Code status = TransactionIndividualStatus3Code.RJCT;
			transaction.setTxSts(status);

			StatusReasonInformation9 reason = new StatusReasonInformation9();
			StatusReason6Choice reasonCode = new StatusReason6Choice();
			reasonCode.setCd(validationError.getRtpReasonCode().getRejectReasonCode());
			reason.setRsn(reasonCode);
			transaction.getStsRsnInf().add(reason);
		} else {
			TransactionIndividualStatus3Code status = TransactionIndividualStatus3Code.ACCP;
			transaction.setTxSts(status);
		}

		messageStatusReport.getTxInfAndSts().add(transaction);

		return messageStatusReport;
	}

}
