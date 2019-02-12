package rtp.demo.debtor.confirmation.beans;

import iso.std.iso._20022.tech.xsd.pacs_002_001.FIToFIPaymentStatusReportV07;
import rtp.demo.debtor.domain.rtp.simplified.MessageStatusReport;

public class MessageStatusReportTransformer {

	public MessageStatusReport toMessageStatusReport(FIToFIPaymentStatusReportV07 rtpMessageStatusReport) {
		MessageStatusReport messageStatusReport = new MessageStatusReport();

		if (rtpMessageStatusReport.getGrpHdr() != null) {
			messageStatusReport.setMessageStatusReportId(rtpMessageStatusReport.getGrpHdr().getMsgId());
		}

		if (rtpMessageStatusReport.getOrgnlGrpInfAndSts() != null) {
			if (!(rtpMessageStatusReport.getOrgnlGrpInfAndSts().isEmpty())) {
				messageStatusReport
						.setOriginalMessageId(rtpMessageStatusReport.getOrgnlGrpInfAndSts().get(0).getOrgnlMsgId());
			}
		}

		if (rtpMessageStatusReport.getTxInfAndSts() != null) {
			if (!(rtpMessageStatusReport.getTxInfAndSts().isEmpty())) {

				messageStatusReport
						.setTransactionStatus(rtpMessageStatusReport.getTxInfAndSts().get(0).getTxSts().toString());

				if (rtpMessageStatusReport.getTxInfAndSts().get(0).getAccptncDtTm() != null) {
					messageStatusReport.setAcceptanceDateTime(rtpMessageStatusReport.getTxInfAndSts().get(0)
							.getAccptncDtTm().toGregorianCalendar().toZonedDateTime().toLocalDateTime());
				}

				if (rtpMessageStatusReport.getTxInfAndSts().get(0).getStsRsnInf() != null) {
					if (!(rtpMessageStatusReport.getTxInfAndSts().get(0).getStsRsnInf().isEmpty())) {
						messageStatusReport.setRejectReasonCode(rtpMessageStatusReport.getTxInfAndSts().get(0)
								.getStsRsnInf().get(0).getRsn().toString());
					}
				}
			}

		}

		return messageStatusReport;
	}

}
