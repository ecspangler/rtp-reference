package rtp.demo.creditor.fraud.beans;

import java.math.BigDecimal;
import java.util.logging.Logger;

import rtp.demo.creditor.domain.payments.Payment;

public class CreditorPaymentFraudDetectionBean {

	private static final Logger LOG = Logger.getLogger(CreditorPaymentFraudDetectionBean.class.getName());

	// Placeholder method for fraud evaluation or call to fraud system
	public Payment evaluate(Payment payment) {

		LOG.info("Payment Pre-Fraud Check: " + payment);

		payment.setFraudScore(new BigDecimal("92.345"));

		if (payment.getDebtorId().equals("888888888")) {
			payment.setIsFraudValidated(false);
		} else {
			payment.setIsFraudValidated(true);
		}

		LOG.info("Payment Post-Fraud Check: " + payment);

		return payment;
	}

}
