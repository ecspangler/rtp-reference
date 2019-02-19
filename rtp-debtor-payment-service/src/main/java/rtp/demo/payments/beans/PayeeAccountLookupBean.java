package rtp.demo.payments.beans;

import rtp.demo.debtor.domain.account.Account;
import rtp.demo.debtor.domain.model.payment.Payment;
//import rtp.demo.debtor.repository.account.AccountRepository;
//import rtp.demo.debtor.repository.account.JdgAccountRepository;

/*
 * This class represents a mock service to lookup a payment receiver's 
 * bank account routing number and account number, used to direct the payment. 
 * 
 * In a real application this may locate the payee's information based on phone number or email address. 
 * For example purposes here, the lookup is by name string.
 * 
 */
public class PayeeAccountLookupBean {

	// private static AccountRepository accountRepository = new
	// JdgAccountRepository();

	public static Payment enrichPayeeAccountInformation(Payment payment) {
		String accountKey = payment.getReceiverFirstName().toUpperCase() + "_"
				+ payment.getReceiverLastName().toUpperCase();
		// Account payeeAccount = accountRepository.getAccount(accountKey);

//		if (payeeAccount != null) {
//			payment.setReceiverRoutingNumber(payeeAccount.getRoutingNumber());
//			payment.setReceiverAccountNumber(payeeAccount.getAccountNumber());
//		}
		return payment;
	}

}
