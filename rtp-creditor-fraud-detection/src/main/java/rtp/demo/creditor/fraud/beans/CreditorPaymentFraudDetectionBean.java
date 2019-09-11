package rtp.demo.creditor.fraud.beans;

import java.math.BigDecimal;

import rtp.demo.creditor.domain.payments.Payment;

public class CreditorPaymentFraudDetectionBean {

	// Placeholder method for fraud evaluation or call to fraud system
	public Payment evaluate(Payment payment) {
		payment.setFraudScore(new BigDecimal("92.345"));
		payment.setIsFraudValidated(true);

		return payment;
	}

}
