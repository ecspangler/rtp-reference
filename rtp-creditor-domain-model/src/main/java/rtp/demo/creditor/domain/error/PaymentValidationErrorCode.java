package rtp.demo.creditor.domain.error;

public enum PaymentValidationErrorCode {

	OUTSIDE_OF_DATE_RANGE("Out of Date Range"), INVALID_NUM_TRANSACTIONS("Invalid Number Transactions"),
	INVALID_PAYMENT_AMOUNT("Invalid Amount"), INVALID_CURRENCY("Invalid Currency"),
	INVALID_CREDITOR("Invalid Creditor"), INVALID_ACCOUNT("Invalid Account"), ACCOUNT_CLOSED("Account Closed"),
	ACCOUNT_BLOCKED("Account Blocked"), INVALID_SETTLEMENT_METHOD("Invalid Settlement Method"),
	RESTRICTED_PAYMENT_SOURCE("Restricted Payment Source");

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
