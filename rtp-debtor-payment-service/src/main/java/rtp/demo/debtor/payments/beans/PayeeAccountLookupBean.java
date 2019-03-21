package rtp.demo.debtor.payments.beans;

import rtp.demo.debtor.domain.account.Account;
import rtp.demo.debtor.domain.model.payment.Payment;
import rtp.demo.debtor.repository.account.AccountRepository;
import rtp.demo.debtor.repository.account.JdgAccountRepository;

/*
 * This class represents a service to lookup a payment receiver's 
 * bank account routing number and account number, used to direct the payment. 
 * 
 * In a real application this may locate the payee's information based on phone number or email address,
 * and perform additional validations to confirm the recipient. 
 * For example purposes here, the lookup is by email into a cache of recipient info.
 * 
 */
public class PayeeAccountLookupBean {

	private static AccountRepository accountRepository = new JdgAccountRepository();

	public static Payment enrichPayeeAccountInformation(Payment payment) {
		Account payeeAccount = accountRepository.getAccount(payment.getReceiverEmail());

		if (payeeAccount != null) {
			payment.setReceiverRoutingNumber(payeeAccount.getRoutingNumber());
			payment.setReceiverAccountNumber(payeeAccount.getAccountNumber());
		}
		return payment;
	}

}
