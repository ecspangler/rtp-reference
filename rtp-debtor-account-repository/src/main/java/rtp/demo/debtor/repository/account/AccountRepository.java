package rtp.demo.debtor.repository.account;

import rtp.demo.debtor.domain.account.Account;

public interface AccountRepository {

	public void addAccount(String key, Account account);

	public Account getAccount(String key);

	public void deleteAccount(String key);

}