package rtp.demo.debtor.repository.account;

import rtp.demo.debtor.domain.account.Account;

public interface AccountRepository {

	public void addAccount(Account account);

	public Account getAccount(String accountNumber);

	public void deleteAccount(String accountNumber);

}