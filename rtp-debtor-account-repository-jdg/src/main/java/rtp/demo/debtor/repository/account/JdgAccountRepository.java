package rtp.demo.debtor.repository.account;

import org.infinispan.commons.api.BasicCache;

import rtp.demo.debtor.domain.account.Account;

public class JdgAccountRepository implements AccountRepository {

	private static final String JDG_CACHE_NAME = "debtorAccountCache";

	private CacheContainerProvider containerProvider = new CacheContainerProvider();
	private BasicCache<String, Object> accountCache;

	public JdgAccountRepository() {
		accountCache = containerProvider.getBasicCacheContainer().getCache(JDG_CACHE_NAME);
	}

	@Override
	public void addAccount(String key, Account account) {
		accountCache.put(AccountManager.encode(key), account);
	}

	@Override
	public Account getAccount(String key) {
		Account retrievedAccount = (Account) accountCache.get(AccountManager.encode(key));
		return retrievedAccount;
	}

	@Override
	public void deleteAccount(String key) {
		accountCache.remove(key);
	}

}
