package org.example.repository;

import org.hibernate.Query;
import org.hibernate.Transaction;

public class TestMySqlCustomerRepository extends MySqlCustomerRepository {

	public void clearAllCustomers() {
		Transaction transaction = session.beginTransaction();

		String hql = String.format("delete from Customer");
		Query query = session.createQuery(hql);
		query.executeUpdate();

		transaction.commit();
	}

}
