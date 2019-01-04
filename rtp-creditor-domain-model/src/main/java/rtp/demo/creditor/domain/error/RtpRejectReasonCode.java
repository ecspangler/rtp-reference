package rtp.demo.creditor.domain.error;

public enum RtpRejectReasonCode {

	FAILED_STRUCTURAL_VALIDATION("650"), ACCOUNT_NUMBER_MISSING_INVALID("AC03"), ACCOUNT_BLOCKED("AC06"),
	ACCOUNT_CLOSED("AC07"), ACCOUNT_TYPE_MISSING_INVALID("AC14"), TRANSACTION_FORBIDDEN_FOR_ACCOUNT_TYPE("AG01"),
	CURRENCY_MISSING_INVALID("AM11"), PAYMENT_AMOUNT_MISSING_INVALID("AM12"), CREDITOR_ID_MISSING_INVALID("BE17"),
	OUTSIDE_OF_DATE_RANGE("DT04"), CUSTOMER_DECEASED("MD07");

	private final String rejectReasonCode;

	private RtpRejectReasonCode(String rejectReasonCode) {
		this.rejectReasonCode = rejectReasonCode;
	}

	public String getRejectReasonCode() {
		return rejectReasonCode;
	}

	public static RtpRejectReasonCode fromString(String code) {
		for (RtpRejectReasonCode rejectReasonCode : RtpRejectReasonCode.values()) {
			if (rejectReasonCode.rejectReasonCode.equalsIgnoreCase(code)) {
				return rejectReasonCode;
			}
		}
		return null;
	}

}
