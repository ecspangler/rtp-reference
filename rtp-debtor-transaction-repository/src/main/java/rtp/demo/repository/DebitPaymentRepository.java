package rtp.demo.repository;

import java.math.BigInteger;
import java.util.List;

import rtp.demo.debtor.domain.model.payment.DebitPayment;

public interface DebitPaymentRepository {

	public void addPayment(DebitPayment payment);

	public List<DebitPayment> getAllPayments();

	public DebitPayment getPayment(BigInteger id);

	public List<DebitPayment> getPayments(String accountNumber);

	public void updatePayment(DebitPayment payment);

	public void deletePayment(DebitPayment payment);

	public DebitPayment getPaymentByPaymentKey(String paymentKey);

}
