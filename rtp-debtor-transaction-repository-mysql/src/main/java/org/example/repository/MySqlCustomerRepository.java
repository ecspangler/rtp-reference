package org.example.repository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Customer;
import org.example.repository.util.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class MySqlCustomerRepository implements CustomerRepository {

	private static final Logger log = LogManager.getLogger(MySqlCustomerRepository.class);

	protected Session session = HibernateUtil.getHibernateSession();

	@Override
	public void addCustomer(Customer customer) {
		// In a real application the full object would not be logged at info level
		log.info("Saving customer: {}", customer);

		Transaction transaction = session.beginTransaction();
		customer.setCreatedTimestamp(LocalDateTime.now());
		customer.setUpdatedTimestamp(LocalDateTime.now());
		session.save(customer);

		transaction.commit();
	}

	@Override
	public List<Customer> getAllCustomers() {
		log.info("Retrieving all customers");
		Transaction transaction = session.beginTransaction();

		Criteria cr = session.createCriteria(Customer.class);
		List<Customer> results = cr.list();

		transaction.commit();
		return results;
	}

	@Override
	public Customer getCustomer(BigInteger id) {
		log.info("Retrieving customer with id: {}", id);
		Transaction transaction = session.beginTransaction();

		Customer customer = (Customer) session.get(Customer.class, id);

		transaction.commit();
		return customer;
	}

	@Override
	public void updateCustomer(Customer customer) {
		// In a real application the full object would not be logged at info level
		log.info("Updating customer: {}", customer);
		Transaction transaction = session.beginTransaction();

		customer.setUpdatedTimestamp(LocalDateTime.now());
		session.update(customer);

		transaction.commit();
	}

	@Override
	public void deleteCustomer(Customer customer) {
		// In a real application the full object would not be logged at info level
		log.info("Deleting customer: {}", customer);
		Transaction transaction = session.beginTransaction();

		session.delete(customer);

		transaction.commit();
	}

	public Session getSession() {
		return session;
	}

}
