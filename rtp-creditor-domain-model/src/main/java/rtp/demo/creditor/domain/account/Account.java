package rtp.demo.creditor.domain.account;

import java.io.Serializable;

import org.infinispan.protostream.annotations.ProtoField;

/*
 * Simple domain class representing an account held by the Creditor
 */
public class Account implements Serializable {

	private static final long serialVersionUID = -9131494827098991910L;

	private String accountNumber;

	private String accountType;

	// private AccountStatus status;

	// private Customer customer = new Customer();

	@ProtoField(number = 1, required = true)
	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	@ProtoField(number = 2, required = false)
	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

//	@ProtoField(number = 3, required = false)
//	public AccountStatus getStatus() {
//		return status;
//	}
//
//	public void setStatus(AccountStatus status) {
//		this.status = status;
//	}

//	@ProtoField(number = 4, required = false)
//	public Customer getCustomer() {
//		return customer;
//	}

//	public void setCustomer(Customer customer) {
//		this.customer = customer;
//	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accountNumber == null) ? 0 : accountNumber.hashCode());
		result = prime * result + ((accountType == null) ? 0 : accountType.hashCode());
		// result = prime * result + ((customer == null) ? 0 : customer.hashCode());
		// result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		Account other = (Account) obj;
		if (accountNumber == null) {
			if (other.accountNumber != null)
				return false;
		} else if (!accountNumber.equals(other.accountNumber))
			return false;
		if (accountType == null) {
			if (other.accountType != null)
				return false;
		} else if (!accountType.equals(other.accountType))
			return false;
//		if (customer == null) {
//			if (other.customer != null)
//				return false;
//		} else if (!customer.equals(other.customer))
//			return false;
//		if (status != other.status)
//			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Account [accountNumber=" + accountNumber + ", accountType=" + accountType + "]"; // ", status=" + status
																									// + "]";
		// + ", customer=" + customer + "]";
	}

}
