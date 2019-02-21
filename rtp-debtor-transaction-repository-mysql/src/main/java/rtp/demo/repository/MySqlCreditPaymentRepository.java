package rtp.demo.repository;

import java.math.BigInteger;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import rtp.demo.debtor.domain.model.payment.CreditPayment;
import rtp.demo.repository.CreditPaymentRepository;
import rtp.demo.repository.util.HibernateUtil;

public class MySqlCreditPaymentRepository implements CreditPaymentRepository {

	private static final Logger log = LogManager.getLogger(MySqlCreditPaymentRepository.class);

	protected Session session = HibernateUtil.getHibernateSession();

	@Override
	public void addPayment(CreditPayment payment) {
		// In a real application the full object would not be logged at info level
		log.info("Saving payment: {}", payment);

		Transaction transaction = session.beginTransaction();
		session.save(payment);

		transaction.commit();
	}

	@Override
	public List<CreditPayment> getAllPayments() {
		log.info("Retrieving all payments");
		Transaction transaction = session.beginTransaction();

		Criteria cr = session.createCriteria(CreditPayment.class);
		List<CreditPayment> results = cr.list();

		transaction.commit();
		return results;
	}

	@Override
	public CreditPayment getPayment(BigInteger id) {
		log.info("Retrieving payment with id: {}", id);
		Transaction transaction = session.beginTransaction();

		CreditPayment payment = (CreditPayment) session.get(CreditPayment.class, id);

		transaction.commit();
		return payment;
	}

	@Override
	public void updatePayment(CreditPayment payment) {
		// In a real application the full object would not be logged at info level
		log.info("Updating payment: {}", payment);
		Transaction transaction = session.beginTransaction();

		session.update(payment);

		transaction.commit();
	}

	@Override
	public void deletePayment(CreditPayment payment) {
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
	public List<CreditPayment> getPayments(String accountNumber) {
		log.info("Retrieving all payments by account number");
		Transaction transaction = session.beginTransaction();

		Criteria cr = session.createCriteria(CreditPayment.class);
		cr.add(Restrictions.eq("receiverAccountNumber", accountNumber));
		List<CreditPayment> results = cr.list();

		transaction.commit();
		return results;
	}

}
