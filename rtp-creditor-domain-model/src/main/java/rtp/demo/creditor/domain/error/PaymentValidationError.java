package rtp.demo.creditor.domain.error;

public class PaymentValidationError {

	private PaymentValidationErrorCode errorCode;

	private RtpRejectReasonCode rtpReasonCode;

	private String errorMessage;

	public PaymentValidationError() {
		super();
	}

	public PaymentValidationError(PaymentValidationErrorCode errorCode, RtpRejectReasonCode rtpReasonCode,
			String errorMessage) {
		super();
		this.errorCode = errorCode;
		this.rtpReasonCode = rtpReasonCode;
		this.errorMessage = errorMessage;
	}

	public PaymentValidationError(String errorCode, String rtpReasonCode, String errorMessage) {
		super();
		this.errorCode = PaymentValidationErrorCode.fromString(errorCode);
		this.rtpReasonCode = RtpRejectReasonCode.fromString(rtpReasonCode);
		this.errorMessage = errorMessage;
	}

	public PaymentValidationErrorCode getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(PaymentValidationErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	public RtpRejectReasonCode getRtpReasonCode() {
		return rtpReasonCode;
	}

	public void setRtpReasonCode(RtpRejectReasonCode rtpReasonCode) {
		this.rtpReasonCode = rtpReasonCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((errorCode == null) ? 0 : errorCode.hashCode());
		result = prime * result + ((errorMessage == null) ? 0 : errorMessage.hashCode());
		result = prime * result + ((rtpReasonCode == null) ? 0 : rtpReasonCode.hashCode());
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
		PaymentValidationError other = (PaymentValidationError) obj;
		if (errorCode == null) {
			if (other.errorCode != null)
				return false;
		} else if (!errorCode.equals(other.errorCode))
			return false;
		if (errorMessage == null) {
			if (other.errorMessage != null)
				return false;
		} else if (!errorMessage.equals(other.errorMessage))
			return false;
		if (rtpReasonCode != other.rtpReasonCode)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PaymentValidationError [errorCode=" + errorCode + ", rtpReasonCode=" + rtpReasonCode + ", errorMessage="
				+ errorMessage + "]";
	}

}
