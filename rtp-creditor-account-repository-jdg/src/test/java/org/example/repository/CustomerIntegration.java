package org.example.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDateTime;
import java.util.List;

import org.example.model.Customer;
import org.junit.Before;
import org.junit.Test;

public class CustomerIntegration {

	private TestMySqlCustomerRepository testCustomerRepository = new TestMySqlCustomerRepository();

	@Before
	public void setup() {
		// Clear test data using test impl of the repository, before each test
		testCustomerRepository.clearAllCustomers();
	}

	@Test
	public void shouldSaveAndRetrieveCustomer() {
		// Populate test data
		Customer customerForSave = makeCustomer();

		testCustomerRepository.addCustomer(customerForSave);

		// Retrieve and check
		List<Customer> retrievedCustomers = testCustomerRepository.getAllCustomers();
		if (retrievedCustomers.isEmpty()) {
			fail("No customer retrieved");
		} else {
			assertTrue(retrievedCustomers.size() == 1);
			Customer retrievedCustomer = retrievedCustomers.get(0);
			assertEquals(customerForSave, retrievedCustomer);
		}
	}

	@Test
	public void shouldSaveAndRetrieveMultipleCustomers() {
		// Populate test data
		Customer customerForSave1 = makeCustomer();
		testCustomerRepository.addCustomer(customerForSave1);

		Customer customerForSave2 = makeCustomer();
		customerForSave2.setUniqueId("A00002");
		customerForSave2.setLastName("Smith II");
		testCustomerRepository.addCustomer(customerForSave2);

		Customer customerForSave3 = makeCustomer();
		customerForSave3.setUniqueId("A00003");
		customerForSave3.setLastName("Smith III");
		testCustomerRepository.addCustomer(customerForSave3);

		// Retrieve and check
		List<Customer> retrievedCustomers = testCustomerRepository.getAllCustomers();
		if (retrievedCustomers.isEmpty()) {
			fail("No customers retrieved");
		} else {
			assertTrue(retrievedCustomers.size() == 3);
		}
	}

	@Test
	public void shouldRetrieveSingleCustomer() {
		// Populate test data
		Customer customerForSave1 = makeCustomer();
		testCustomerRepository.addCustomer(customerForSave1);

		Customer customerForSave2 = makeCustomer();
		customerForSave2.setUniqueId("A00002");
		customerForSave2.setLastName("Smith II");
		testCustomerRepository.addCustomer(customerForSave2);

		Customer customerForSave3 = makeCustomer();
		customerForSave3.setUniqueId("A00003");
		customerForSave3.setLastName("Smith III");
		testCustomerRepository.addCustomer(customerForSave3);

		// Retrieve and check
		Customer retrievedCustomer = testCustomerRepository.getCustomer(customerForSave2.getId());
		if (retrievedCustomer == null) {
			fail("No customer retrieved");
		} else {
			assertEquals(retrievedCustomer, customerForSave2);
		}
	}

	@Test
	public void shouldUpdateCustomer() {
		// Populate test data
		Customer customerForSave = makeCustomer();
		testCustomerRepository.addCustomer(customerForSave);

		// Retrieve and update
		Customer retrievedCustomer = testCustomerRepository.getCustomer(customerForSave.getId());
		assertEquals(retrievedCustomer.getFavoriteColor(), "Green");
		retrievedCustomer.setFavoriteColor("Orange");
		testCustomerRepository.updateCustomer(retrievedCustomer);

		// Retrieve and check
		Customer retrievedCustomer2 = testCustomerRepository.getCustomer(customerForSave.getId());

		if (retrievedCustomer2 == null) {
			fail("No customer retrieved");
		} else {
			assertEquals(retrievedCustomer2.getFavoriteColor(), "Orange");
		}
	}

	@Test
	public void shouldDeleteCustomer() {
		// Populate test data
		Customer customerForSave1 = makeCustomer();
		testCustomerRepository.addCustomer(customerForSave1);

		Customer customerForSave2 = makeCustomer();
		customerForSave2.setUniqueId("A00002");
		customerForSave2.setLastName("Smith II");
		testCustomerRepository.addCustomer(customerForSave2);

		Customer customerForSave3 = makeCustomer();
		customerForSave3.setUniqueId("A00003");
		customerForSave3.setLastName("Smith III");
		testCustomerRepository.addCustomer(customerForSave3);

		// Delete customer
		testCustomerRepository.deleteCustomer(customerForSave1);

		// Retrieve and check
		List<Customer> retrievedCustomers = testCustomerRepository.getAllCustomers();
		if (retrievedCustomers.isEmpty()) {
			fail("No customers retrieved");
		} else {
			assertTrue(retrievedCustomers.size() == 2);
		}
	}

	private Customer makeCustomer() {
		Customer customer = new Customer();

		customer.setUniqueId("A00001");
		customer.setFirstName("James");
		customer.setLastName("Smith");
		customer.setAccountNumber("0000111100001111");
		customer.setFavoriteColor("Green");

		customer.setCreatedBy("test123");
		customer.setCreatedTimestamp(LocalDateTime.now());
		customer.setUpdatedTimestamp(LocalDateTime.now());

		return customer;

	}

}
