package rtp.demo.creditor.repository.account;

import org.infinispan.commons.api.BasicCache;

import rtp.demo.creditor.domain.account.Account;

public class JdgAccountRepository implements AccountRepository {

	private static final String JDG_CACHE_NAME = "debtorAccountCache";

	private CacheContainerProvider containerProvider = new CacheContainerProvider();
	private BasicCache<String, Object> accountCache;

	public JdgAccountRepository() {
		accountCache = containerProvider.getBasicCacheContainer().getCache(JDG_CACHE_NAME);
	}

	@Override
	public void addAccount(Account account) {
		accountCache.put(AccountManager.encode(account.getAccountNumber()), account);
	}

	@Override
	public Account getAccount(String accountNumber) {
		Account retrievedAccount = (Account) accountCache.get(AccountManager.encode(accountNumber));
		return retrievedAccount;
	}

	@Override
	public void deleteAccount(String accountNumber) {
		accountCache.remove(accountNumber);
	}

}
