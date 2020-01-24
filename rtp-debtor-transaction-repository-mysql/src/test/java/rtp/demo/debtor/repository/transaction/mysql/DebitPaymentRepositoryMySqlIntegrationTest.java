package rtp.demo.debtor.repository.transaction.mysql;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import rtp.demo.debtor.domain.model.payment.DebitPayment;
import rtp.demo.debtor.repository.transaction.DebitPaymentRepository;

//@RunWith(SpringRunner.class)
//@DataJpaTest
//public class DebitPaymentRepositoryMySqlIntegrationTest {
//
//	@Autowired
//	private DebitPaymentRepository debitPaymentsRepository;
//
//	@Test
//	public void whenFindByKey_thenReturnDebitPayment() {
//		assertTrue(debitPaymentsRepository != null);
//		assertTrue(debitPaymentsRepository.getPaymentByPaymentKey("KEY123") == null);
//
//		DebitPayment debitPaymentForSave = new DebitPayment();
//
//		debitPaymentForSave.setPaymentId("KEY123");
//
//	}
//
//}
