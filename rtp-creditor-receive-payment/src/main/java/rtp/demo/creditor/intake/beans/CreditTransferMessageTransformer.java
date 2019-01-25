package rtp.demo.creditor.intake.beans;

import iso.std.iso._20022.tech.xsd.pacs_008_001.CreditTransferTransaction25;
import iso.std.iso._20022.tech.xsd.pacs_008_001.FIToFICustomerCreditTransferV06;
import iso.std.iso._20022.tech.xsd.pacs_008_001.GroupHeader70;
import rtp.demo.creditor.domain.rtp.simplified.CreditTransferMessage;

public class CreditTransferMessageTransformer {

	public CreditTransferMessage toCreditTransferMessage(FIToFICustomerCreditTransferV06 rtpCreditTransferMessage) {
		CreditTransferMessage creditTransferMessage = new CreditTransferMessage();
		GroupHeader70 groupHeader70 = rtpCreditTransferMessage.getGrpHdr();

		creditTransferMessage.setCreditTransferMessageId(groupHeader70.getMsgId());
		// creditTransferMessage.setCreationDateTime(RtpDomainUtils.toLocalDateTime(groupHeader70.getCreDtTm()));

		if (groupHeader70.getNbOfTxs() != null) {
			creditTransferMessage.setNumberOfTransactions(Integer.parseInt(groupHeader70.getNbOfTxs()));
		}
		creditTransferMessage.setSettlementMethod(groupHeader70.getSttlmInf().getSttlmMtd().toString());

		creditTransferMessage.setPaymentAmount(groupHeader70.getTtlIntrBkSttlmAmt().getValue());
		creditTransferMessage.setPaymentCurrency(groupHeader70.getTtlIntrBkSttlmAmt().getCcy());

		creditTransferMessage.setDebtorId(groupHeader70.getInstgAgt().getFinInstnId().getOthr().getId());
		creditTransferMessage.setCreditorId(groupHeader70.getInstdAgt().getFinInstnId().getOthr().getId());

		if (rtpCreditTransferMessage.getCdtTrfTxInf().size() > 0) {
			CreditTransferTransaction25 creditTransferTransaction25 = rtpCreditTransferMessage.getCdtTrfTxInf().get(0);
			creditTransferMessage.setEndToEndId(creditTransferTransaction25.getPmtId().getEndToEndId());
			creditTransferMessage.setPaymentInstructionId(creditTransferTransaction25.getPmtId().getInstrId());
			creditTransferMessage
					.setDebtorAccountNumber(creditTransferTransaction25.getDbtrAcct().getId().getOthr().getId());
			creditTransferMessage
					.setCreditorAccountNumber(creditTransferTransaction25.getCdtrAcct().getId().getOthr().getId());
		}

		return creditTransferMessage;
	}

}
