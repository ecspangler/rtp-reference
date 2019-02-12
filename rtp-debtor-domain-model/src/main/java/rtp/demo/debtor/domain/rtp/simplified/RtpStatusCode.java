package rtp.demo.debtor.domain.rtp.simplified;

public enum RtpStatusCode {

	ACCEPT("ACTC"), REJECT("RJCT"), ACCEPT_WITHOUT_POSTING("ACWP");

	private final String statusCode;

	private RtpStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public static RtpStatusCode fromString(String code) {
		for (RtpStatusCode statusCode : RtpStatusCode.values()) {
			if (statusCode.statusCode.equalsIgnoreCase(code)) {
				return statusCode;
			}
		}
		return null;
	}

}
