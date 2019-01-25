package rtp.demo.creditor.intake.beans;

import rtp.demo.creditor.domain.payments.Payment;
import rtp.demo.creditor.validation.PaymentValidationRequest;

public class PaymentTransformer {

	public Payment toPayment(PaymentValidationRequest paymentValidationRequest) {
		Payment payment = new Payment();
		payment.setCreditTransferMessage(paymentValidationRequest.getCreditTransferMessage());

		if (paymentValidationRequest.getErrors().size() == 0) {
			payment.setIsValidated(true);
		}

		paymentValidationRequest.getErrors().forEach(error -> {
			payment.getErrors().add(error);
		});

		return payment;
	}

}
