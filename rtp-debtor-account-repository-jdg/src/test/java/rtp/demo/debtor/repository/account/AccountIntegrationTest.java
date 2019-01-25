package rtp.demo.debtor.repository.account;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import rtp.demo.debtor.domain.account.Account;

public class AccountIntegrationTest {

	private JdgAccountRepository accountRepository = new JdgAccountRepository();

	@Before
	public void setup() {
		accountRepository.deleteAccount("0000111100001111");
	}

	@Test
	public void shouldAddAccount() {
		Account accountForSave = makeDummyAccount();
		accountRepository.addAccount(accountForSave);

		Account retrievedAccount = accountRepository.getAccount("0000111100001111");

		assertEquals(accountForSave, retrievedAccount);

	}

	private Account makeDummyAccount() {
		Account account = new Account();
		account.setAccountNumber("0000111100001111");
		account.setAccountType("Checking");

		return account;
	}
}
