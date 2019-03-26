package rtp.demo.repository;

import java.math.BigInteger;
import java.util.List;

import rtp.demo.creditor.domain.payments.Payment;

public interface CreditPaymentRepository {

	public void addPayment(Payment payment);

	public List<Payment> getAllPayments();

	public Payment getPayment(BigInteger id);

	public List<Payment> getPayments(String accountNumber);

	public void updatePayment(Payment payment);

	public void deletePayment(Payment payment);

	public Payment getPaymentByPaymentKey(String paymentKey);

}
