package org.example.repository;

import java.math.BigInteger;
import java.util.List;

import org.example.model.Customer;

public interface CustomerRepository {

	public void addCustomer(Customer customer);

	public List<Customer> getAllCustomers();

	public Customer getCustomer(BigInteger id);

	public void updateCustomer(Customer customer);

	public void deleteCustomer(Customer customer);

}
