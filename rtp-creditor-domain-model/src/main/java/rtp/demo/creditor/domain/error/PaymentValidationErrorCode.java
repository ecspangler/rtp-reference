package rtp.demo.creditor.domain.error;

public enum PaymentValidationErrorCode {

	OUTSIDE_OF_DATE_RANGE("CPVE_001"), INVALID_NUM_TRANSACTIONS("CPVE_002"), INVALID_PAYMENT_AMOUNT("CPVE_003"),
	INVALID_CURRENCY("CPVE_004"), INVALID_CREDITOR("CPVE_005"), INVALID_ACCOUNT("CPVE_006"), ACCOUNT_CLOSED("CPVE_007"),
	ACCOUNT_BLOCKED("CPVE_008"), INVALID_SETTLEMENT_METHOD("CPVE_009");

	private final String errorCode;

	private PaymentValidationErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public static PaymentValidationErrorCode fromString(String code) {
		for (PaymentValidationErrorCode errorCode : PaymentValidationErrorCode.values()) {
			if (errorCode.errorCode.equalsIgnoreCase(code)) {
				return errorCode;
			}
		}
		return null;
	}

}
