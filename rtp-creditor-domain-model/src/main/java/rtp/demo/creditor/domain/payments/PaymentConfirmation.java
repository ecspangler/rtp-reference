package rtp.demo.creditor.domain.payments;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentConfirmation {

	private String messageStatusReportId;

	private LocalDateTime creationDateTime;

	private String originalMessageId;

	private String originalPaymentInstructionId;

	private String endToEndId;

	private BigDecimal paymentAmount;

	private String paymentCurrency;

	private LocalDateTime originalMessageCreationDateTime;

	private Integer originalNumberOfTransactions;

	private String transactionStatus;

	private String rejectReasonCode;

	private LocalDateTime acceptanceDateTime;

	private String debtorId;

	private String creditorId;

	public String getMessageStatusReportId() {
		return messageStatusReportId;
	}

	public void setMessageStatusReportId(String messageStatusReportId) {
		this.messageStatusReportId = messageStatusReportId;
	}

	public LocalDateTime getCreationDateTime() {
		return creationDateTime;
	}

	public void setCreationDateTime(LocalDateTime creationDateTime) {
		this.creationDateTime = creationDateTime;
	}

	public String getOriginalMessageId() {
		return originalMessageId;
	}

	public void setOriginalMessageId(String originalMessageId) {
		this.originalMessageId = originalMessageId;
	}

	public String getOriginalPaymentInstructionId() {
		return originalPaymentInstructionId;
	}

	public void setOriginalPaymentInstructionId(String originalPaymentInstructionId) {
		this.originalPaymentInstructionId = originalPaymentInstructionId;
	}

	public String getEndToEndId() {
		return endToEndId;
	}

	public void setEndToEndId(String endToEndId) {
		this.endToEndId = endToEndId;
	}

	public BigDecimal getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(BigDecimal paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public String getPaymentCurrency() {
		return paymentCurrency;
	}

	public void setPaymentCurrency(String paymentCurrency) {
		this.paymentCurrency = paymentCurrency;
	}

	public LocalDateTime getOriginalMessageCreationDateTime() {
		return originalMessageCreationDateTime;
	}

	public void setOriginalMessageCreationDateTime(LocalDateTime originalMessageCreationDateTime) {
		this.originalMessageCreationDateTime = originalMessageCreationDateTime;
	}

	public Integer getOriginalNumberOfTransactions() {
		return originalNumberOfTransactions;
	}

	public void setOriginalNumberOfTransactions(Integer originalNumberOfTransactions) {
		this.originalNumberOfTransactions = originalNumberOfTransactions;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public String getRejectReasonCode() {
		return rejectReasonCode;
	}

	public void setRejectReasonCode(String rejectReasonCode) {
		this.rejectReasonCode = rejectReasonCode;
	}

	public LocalDateTime getAcceptanceDateTime() {
		return acceptanceDateTime;
	}

	public void setAcceptanceDateTime(LocalDateTime acceptanceDateTime) {
		this.acceptanceDateTime = acceptanceDateTime;
	}

	public String getDebtorId() {
		return debtorId;
	}

	public void setDebtorId(String debtorId) {
		this.debtorId = debtorId;
	}

	public String getCreditorId() {
		return creditorId;
	}

	public void setCreditorId(String creditorId) {
		this.creditorId = creditorId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((acceptanceDateTime == null) ? 0 : acceptanceDateTime.hashCode());
		result = prime * result + ((creationDateTime == null) ? 0 : creationDateTime.hashCode());
		result = prime * result + ((creditorId == null) ? 0 : creditorId.hashCode());
		result = prime * result + ((debtorId == null) ? 0 : debtorId.hashCode());
		result = prime * result + ((endToEndId == null) ? 0 : endToEndId.hashCode());
		result = prime * result + ((messageStatusReportId == null) ? 0 : messageStatusReportId.hashCode());
		result = prime * result
				+ ((originalMessageCreationDateTime == null) ? 0 : originalMessageCreationDateTime.hashCode());
		result = prime * result + ((originalMessageId == null) ? 0 : originalMessageId.hashCode());
		result = prime * result
				+ ((originalNumberOfTransactions == null) ? 0 : originalNumberOfTransactions.hashCode());
		result = prime * result
				+ ((originalPaymentInstructionId == null) ? 0 : originalPaymentInstructionId.hashCode());
		result = prime * result + ((paymentAmount == null) ? 0 : paymentAmount.hashCode());
		result = prime * result + ((paymentCurrency == null) ? 0 : paymentCurrency.hashCode());
		result = prime * result + ((rejectReasonCode == null) ? 0 : rejectReasonCode.hashCode());
		result = prime * result + ((transactionStatus == null) ? 0 : transactionStatus.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PaymentConfirmation other = (PaymentConfirmation) obj;
		if (acceptanceDateTime == null) {
			if (other.acceptanceDateTime != null)
				return false;
		} else if (!acceptanceDateTime.equals(other.acceptanceDateTime))
			return false;
		if (creationDateTime == null) {
			if (other.creationDateTime != null)
				return false;
		} else if (!creationDateTime.equals(other.creationDateTime))
			return false;
		if (creditorId == null) {
			if (other.creditorId != null)
				return false;
		} else if (!creditorId.equals(other.creditorId))
			return false;
		if (debtorId == null) {
			if (other.debtorId != null)
				return false;
		} else if (!debtorId.equals(other.debtorId))
			return false;
		if (endToEndId == null) {
			if (other.endToEndId != null)
				return false;
		} else if (!endToEndId.equals(other.endToEndId))
			return false;
		if (messageStatusReportId == null) {
			if (other.messageStatusReportId != null)
				return false;
		} else if (!messageStatusReportId.equals(other.messageStatusReportId))
			return false;
		if (originalMessageCreationDateTime == null) {
			if (other.originalMessageCreationDateTime != null)
				return false;
		} else if (!originalMessageCreationDateTime.equals(other.originalMessageCreationDateTime))
			return false;
		if (originalMessageId == null) {
			if (other.originalMessageId != null)
				return false;
		} else if (!originalMessageId.equals(other.originalMessageId))
			return false;
		if (originalNumberOfTransactions == null) {
			if (other.originalNumberOfTransactions != null)
				return false;
		} else if (!originalNumberOfTransactions.equals(other.originalNumberOfTransactions))
			return false;
		if (originalPaymentInstructionId == null) {
			if (other.originalPaymentInstructionId != null)
				return false;
		} else if (!originalPaymentInstructionId.equals(other.originalPaymentInstructionId))
			return false;
		if (paymentAmount == null) {
			if (other.paymentAmount != null)
				return false;
		} else if (!paymentAmount.equals(other.paymentAmount))
			return false;
		if (paymentCurrency == null) {
			if (other.paymentCurrency != null)
				return false;
		} else if (!paymentCurrency.equals(other.paymentCurrency))
			return false;
		if (rejectReasonCode == null) {
			if (other.rejectReasonCode != null)
				return false;
		} else if (!rejectReasonCode.equals(other.rejectReasonCode))
			return false;
		if (transactionStatus == null) {
			if (other.transactionStatus != null)
				return false;
		} else if (!transactionStatus.equals(other.transactionStatus))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PaymentConfirmation [messageStatusReportId=" + messageStatusReportId + ", creationDateTime="
				+ creationDateTime + ", originalMessageId=" + originalMessageId + ", originalPaymentInstructionId="
				+ originalPaymentInstructionId + ", endToEndId=" + endToEndId + ", paymentAmount=" + paymentAmount
				+ ", paymentCurrency=" + paymentCurrency + ", originalMessageCreationDateTime="
				+ originalMessageCreationDateTime + ", originalNumberOfTransactions=" + originalNumberOfTransactions
				+ ", transactionStatus=" + transactionStatus + ", rejectReasonCode=" + rejectReasonCode
				+ ", acceptanceDateTime=" + acceptanceDateTime + ", debtorId=" + debtorId + ", creditorId=" + creditorId
				+ "]";
	}

}
