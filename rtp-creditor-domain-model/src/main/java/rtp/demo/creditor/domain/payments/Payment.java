package rtp.demo.creditor.domain.payments;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import rtp.demo.creditor.domain.error.PaymentValidationError;
import rtp.demo.creditor.domain.rtp.simplified.CreditTransferMessage;
import rtp.demo.creditor.domain.rtp.simplified.MessageStatusReport;

public class Payment {

	private String creditTransferMessageId;

	private String paymentInstructionId;

	private String endToEndId;

	private LocalDateTime creationDateTime;

	private Integer numberOfTransactions;

	private BigDecimal paymentAmount;

	private String paymentCurrency;

	private String settlementMethod;

	private String debtorId;

	private String debtorAccountNumber;

	private String creditorId;

	private String creditorAccountNumber;

	private String status = "PENDING";

	private Boolean isValidated = false;

	private String rejectReasonCode;

	private List<PaymentValidationError> errors = new ArrayList<PaymentValidationError>();

	private String messageStatusReportId;

	public Payment() {
		super();
	}

	public Payment(Payment payment, MessageStatusReport messageStatusReport, String status) {
		super();
		this.creditTransferMessageId = payment.getCreditTransferMessageId();
		this.paymentInstructionId = payment.getPaymentInstructionId();
		this.endToEndId = payment.getEndToEndId();
		this.creationDateTime = payment.getCreationDateTime();
		this.numberOfTransactions = payment.getNumberOfTransactions();
		this.paymentAmount = payment.getPaymentAmount();
		this.paymentCurrency = payment.getPaymentCurrency();
		this.settlementMethod = payment.getSettlementMethod();
		this.debtorId = payment.getDebtorId();
		this.debtorAccountNumber = payment.getDebtorAccountNumber();
		this.creditorId = payment.getCreditorId();
		this.creditorAccountNumber = payment.getCreditorAccountNumber();
		this.status = payment.getStatus();
		this.isValidated = payment.getIsValidated();
		this.rejectReasonCode = payment.getRejectReasonCode();
		this.errors = payment.getErrors();
		this.status = status;
		this.messageStatusReportId = messageStatusReport.getMessageStatusReportId();
	}

	public String getCreditTransferMessageId() {
		return creditTransferMessageId;
	}

	public void setCreditTransferMessageId(String creditTransferMessageId) {
		this.creditTransferMessageId = creditTransferMessageId;
	}

	public String getPaymentInstructionId() {
		return paymentInstructionId;
	}

	public void setPaymentInstructionId(String paymentInstructionId) {
		this.paymentInstructionId = paymentInstructionId;
	}

	public String getEndToEndId() {
		return endToEndId;
	}

	public void setEndToEndId(String endToEndId) {
		this.endToEndId = endToEndId;
	}

	public LocalDateTime getCreationDateTime() {
		return creationDateTime;
	}

	public void setCreationDateTime(LocalDateTime creationDateTime) {
		this.creationDateTime = creationDateTime;
	}

	public Integer getNumberOfTransactions() {
		return numberOfTransactions;
	}

	public void setNumberOfTransactions(Integer numberOfTransactions) {
		this.numberOfTransactions = numberOfTransactions;
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

	public String getSettlementMethod() {
		return settlementMethod;
	}

	public void setSettlementMethod(String settlementMethod) {
		this.settlementMethod = settlementMethod;
	}

	public String getDebtorId() {
		return debtorId;
	}

	public void setDebtorId(String debtorId) {
		this.debtorId = debtorId;
	}

	public String getDebtorAccountNumber() {
		return debtorAccountNumber;
	}

	public void setDebtorAccountNumber(String debtorAccountNumber) {
		this.debtorAccountNumber = debtorAccountNumber;
	}

	public String getCreditorId() {
		return creditorId;
	}

	public void setCreditorId(String creditorId) {
		this.creditorId = creditorId;
	}

	public String getCreditorAccountNumber() {
		return creditorAccountNumber;
	}

	public void setCreditorAccountNumber(String creditorAccountNumber) {
		this.creditorAccountNumber = creditorAccountNumber;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Boolean getIsValidated() {
		return isValidated;
	}

	public void setIsValidated(Boolean isValidated) {
		this.isValidated = isValidated;
	}

	public String getRejectReasonCode() {
		return rejectReasonCode;
	}

	public void setRejectReasonCode(String rejectReasonCode) {
		this.rejectReasonCode = rejectReasonCode;
	}

	public List<PaymentValidationError> getErrors() {
		return errors;
	}

	public void setErrors(List<PaymentValidationError> errors) {
		this.errors = errors;
	}

	public String getMessageStatusReportId() {
		return messageStatusReportId;
	}

	public void setMessageStatusReportId(String messageStatusReportId) {
		this.messageStatusReportId = messageStatusReportId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((creationDateTime == null) ? 0 : creationDateTime.hashCode());
		result = prime * result + ((creditTransferMessageId == null) ? 0 : creditTransferMessageId.hashCode());
		result = prime * result + ((creditorAccountNumber == null) ? 0 : creditorAccountNumber.hashCode());
		result = prime * result + ((creditorId == null) ? 0 : creditorId.hashCode());
		result = prime * result + ((debtorAccountNumber == null) ? 0 : debtorAccountNumber.hashCode());
		result = prime * result + ((debtorId == null) ? 0 : debtorId.hashCode());
		result = prime * result + ((endToEndId == null) ? 0 : endToEndId.hashCode());
		result = prime * result + ((errors == null) ? 0 : errors.hashCode());
		result = prime * result + ((isValidated == null) ? 0 : isValidated.hashCode());
		result = prime * result + ((messageStatusReportId == null) ? 0 : messageStatusReportId.hashCode());
		result = prime * result + ((numberOfTransactions == null) ? 0 : numberOfTransactions.hashCode());
		result = prime * result + ((paymentAmount == null) ? 0 : paymentAmount.hashCode());
		result = prime * result + ((paymentCurrency == null) ? 0 : paymentCurrency.hashCode());
		result = prime * result + ((paymentInstructionId == null) ? 0 : paymentInstructionId.hashCode());
		result = prime * result + ((rejectReasonCode == null) ? 0 : rejectReasonCode.hashCode());
		result = prime * result + ((settlementMethod == null) ? 0 : settlementMethod.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		Payment other = (Payment) obj;
		if (creationDateTime == null) {
			if (other.creationDateTime != null)
				return false;
		} else if (!creationDateTime.equals(other.creationDateTime))
			return false;
		if (creditTransferMessageId == null) {
			if (other.creditTransferMessageId != null)
				return false;
		} else if (!creditTransferMessageId.equals(other.creditTransferMessageId))
			return false;
		if (creditorAccountNumber == null) {
			if (other.creditorAccountNumber != null)
				return false;
		} else if (!creditorAccountNumber.equals(other.creditorAccountNumber))
			return false;
		if (creditorId == null) {
			if (other.creditorId != null)
				return false;
		} else if (!creditorId.equals(other.creditorId))
			return false;
		if (debtorAccountNumber == null) {
			if (other.debtorAccountNumber != null)
				return false;
		} else if (!debtorAccountNumber.equals(other.debtorAccountNumber))
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
		if (errors == null) {
			if (other.errors != null)
				return false;
		} else if (!errors.equals(other.errors))
			return false;
		if (isValidated == null) {
			if (other.isValidated != null)
				return false;
		} else if (!isValidated.equals(other.isValidated))
			return false;
		if (messageStatusReportId == null) {
			if (other.messageStatusReportId != null)
				return false;
		} else if (!messageStatusReportId.equals(other.messageStatusReportId))
			return false;
		if (numberOfTransactions == null) {
			if (other.numberOfTransactions != null)
				return false;
		} else if (!numberOfTransactions.equals(other.numberOfTransactions))
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
		if (paymentInstructionId == null) {
			if (other.paymentInstructionId != null)
				return false;
		} else if (!paymentInstructionId.equals(other.paymentInstructionId))
			return false;
		if (rejectReasonCode == null) {
			if (other.rejectReasonCode != null)
				return false;
		} else if (!rejectReasonCode.equals(other.rejectReasonCode))
			return false;
		if (settlementMethod == null) {
			if (other.settlementMethod != null)
				return false;
		} else if (!settlementMethod.equals(other.settlementMethod))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Payment [creditTransferMessageId=" + creditTransferMessageId + ", paymentInstructionId="
				+ paymentInstructionId + ", endToEndId=" + endToEndId + ", creationDateTime=" + creationDateTime
				+ ", numberOfTransactions=" + numberOfTransactions + ", paymentAmount=" + paymentAmount
				+ ", paymentCurrency=" + paymentCurrency + ", settlementMethod=" + settlementMethod + ", debtorId="
				+ debtorId + ", debtorAccountNumber=" + debtorAccountNumber + ", creditorId=" + creditorId
				+ ", creditorAccountNumber=" + creditorAccountNumber + ", status=" + status + ", isValidated="
				+ isValidated + ", rejectReasonCode=" + rejectReasonCode + ", errors=" + errors
				+ ", messageStatusReportId=" + messageStatusReportId + "]";
	}

}
