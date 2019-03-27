package rtp.demo.repository;

import java.math.BigInteger;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import rtp.demo.creditor.domain.payments.Payment;
import rtp.demo.repository.CreditPaymentRepository;
import rtp.demo.repository.util.HibernateUtil;

public class MySqlCreditPaymentRepository implements CreditPaymentRepository {

	private static final Logger log = LogManager.getLogger(MySqlCreditPaymentRepository.class);

	protected Session session = HibernateUtil.getHibernateSession();

	@Override
	public void addPayment(Payment payment) {
		// In a real application the full object would not be logged at info level
		log.info("Saving payment: {}", payment);

		Transaction transaction = session.beginTransaction();
		session.save(payment);

		transaction.commit();
	}

	@Override
	public List<Payment> getAllPayments() {
		log.info("Retrieving all payments");
		Transaction transaction = session.beginTransaction();

		Criteria cr = session.createCriteria(Payment.class);
		List<Payment> results = cr.list();

		transaction.commit();
		return results;
	}

	@Override
	public Payment getPayment(BigInteger id) {
		log.info("Retrieving payment with id: {}", id);
		session.clear();
		Transaction transaction = session.beginTransaction();

		Payment payment = (Payment) session.get(Payment.class, id);

		transaction.commit();
		return payment;
	}

	@Override
	public void updatePayment(Payment payment) {
		// In a real application the full object would not be logged at info level
		log.info("Updating payment: {}", payment);
		Transaction transaction = session.beginTransaction();

		session.update(payment);

		transaction.commit();
	}

	@Override
	public void deletePayment(Payment payment) {
		// In a real application the full object would not be logged at info level
		log.info("Deleting payment: {}", payment);
		Transaction transaction = session.beginTransaction();

		session.delete(payment);

		transaction.commit();
	}

	public Session getSession() {
		return session;
	}

	@Override
	public List<Payment> getPayments(String accountNumber) {
		log.info("Retrieving all payments by account number");
		Transaction transaction = session.beginTransaction();

		session.clear();
		Criteria cr = session.createCriteria(Payment.class);
		cr.add(Restrictions.eq("creditorAccountNumber", accountNumber));
		List<Payment> results = cr.list();

		transaction.commit();
		return results;
	}

	@Override
	public Payment getPaymentByPaymentKey(String paymentKey) {
		log.info("Retrieving payment by payment key: " + paymentKey);
		Transaction transaction = session.beginTransaction();

		Criteria cr = session.createCriteria(Payment.class);
		cr.add(Restrictions.eq("creditTransferMessageId", paymentKey));
		List<Payment> results = cr.list();
		Payment result = null;

		if (results != null) {
			result = results.get(0);
		}

		transaction.commit();

		return result;
	}

}
