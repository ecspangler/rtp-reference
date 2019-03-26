package rtp.demo.repository;

import java.math.BigInteger;
import java.util.List;

import rtp.demo.debtor.domain.model.payment.CreditPayment;

public interface CreditPaymentRepository {

	public void addPayment(CreditPayment payment);

	public List<CreditPayment> getAllPayments();

	public CreditPayment getPayment(BigInteger id);

	public List<CreditPayment> getPayments(String accountNumber);

	public void updatePayment(CreditPayment payment);

	public void deletePayment(CreditPayment payment);

}
