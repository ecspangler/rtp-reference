package rtp.demo.creditor.validation.wrappers;

import java.util.HashSet;
import java.util.Set;

import rtp.demo.creditor.domain.account.Account;

public class Accounts {

	private Set<Account> accounts = new HashSet<Account>();

	public Set<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(Set<Account> accounts) {
		this.accounts = accounts;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accounts == null) ? 0 : accounts.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Accounts other = (Accounts) obj;
		if (accounts == null) {
			if (other.accounts != null)
				return false;
		} else if (!accounts.equals(other.accounts))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Accounts [accounts=" + accounts + "]";
	}

}
