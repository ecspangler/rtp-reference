package rtp.demo.creditor.validation;

import java.util.HashSet;
import java.util.Set;

import rtp.demo.creditor.domain.error.PaymentValidationError;
import rtp.demo.creditor.domain.rtp.simplified.CreditTransferMessage;

public class PaymentValidationRequest {

	private CreditTransferMessage creditTransferMessage;
	private Set<PaymentValidationError> errors = new HashSet<PaymentValidationError>();

	public CreditTransferMessage getCreditTransferMessage() {
		return creditTransferMessage;
	}

	public void setCreditTransferMessage(CreditTransferMessage creditTransferMessage) {
		this.creditTransferMessage = creditTransferMessage;
	}

	public Set<PaymentValidationError> getErrors() {
		return errors;
	}

	public void setErrors(Set<PaymentValidationError> errors) {
		this.errors = errors;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((errors == null) ? 0 : errors.hashCode());
		result = prime * result + ((creditTransferMessage == null) ? 0 : creditTransferMessage.hashCode());
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
		PaymentValidationRequest other = (PaymentValidationRequest) obj;
		if (errors == null) {
			if (other.errors != null)
				return false;
		} else if (!errors.equals(other.errors))
			return false;
		if (creditTransferMessage == null) {
			if (other.creditTransferMessage != null)
				return false;
		} else if (!creditTransferMessage.equals(other.creditTransferMessage))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PaymentValidationRequest [creditTransferMessage=" + creditTransferMessage + ", errors=" + errors + "]";
	}

}
