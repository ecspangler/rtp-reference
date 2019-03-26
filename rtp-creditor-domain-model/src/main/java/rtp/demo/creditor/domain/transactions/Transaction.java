package rtp.demo.creditor.domain.transactions;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Transaction")
@XmlAccessorType(XmlAccessType.FIELD)
public class Transaction implements Serializable {

	private static final long serialVersionUID = 847186233458570682L;

	private String transId;
	private String creditDebitCode;
	private String accountNumber;
	private BigDecimal amount;
	private String receiverFirstName;
	private String receiverLastName;
	private String receiverEmail;
	private String receiverCellPhone;
	private String senderFirstName;
	private String senderLastName;
	private String senderEmail;
	private String senderCellPhone;
	private String status = "PENDING";

	public String getTransId() {
		return transId;
	}

	public void setTransId(String transId) {
		this.transId = transId;
	}

	public String getCreditDebitCode() {
		return creditDebitCode;
	}

	public void setCreditDebitCode(String creditDebitCode) {
		this.creditDebitCode = creditDebitCode;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getReceiverFirstName() {
		return receiverFirstName;
	}

	public void setReceiverFirstName(String receiverFirstName) {
		this.receiverFirstName = receiverFirstName;
	}

	public String getReceiverLastName() {
		return receiverLastName;
	}

	public void setReceiverLastName(String receiverLastName) {
		this.receiverLastName = receiverLastName;
	}

	public String getReceiverEmail() {
		return receiverEmail;
	}

	public void setReceiverEmail(String receiverEmail) {
		this.receiverEmail = receiverEmail;
	}

	public String getReceiverCellPhone() {
		return receiverCellPhone;
	}

	public void setReceiverCellPhone(String receiverCellPhone) {
		this.receiverCellPhone = receiverCellPhone;
	}

	public String getSenderFirstName() {
		return senderFirstName;
	}

	public void setSenderFirstName(String senderFirstName) {
		this.senderFirstName = senderFirstName;
	}

	public String getSenderLastName() {
		return senderLastName;
	}

	public void setSenderLastName(String senderLastName) {
		this.senderLastName = senderLastName;
	}

	public String getSenderEmail() {
		return senderEmail;
	}

	public void setSenderEmail(String senderEmail) {
		this.senderEmail = senderEmail;
	}

	public String getSenderCellPhone() {
		return senderCellPhone;
	}

	public void setSenderCellPhone(String senderCellPhone) {
		this.senderCellPhone = senderCellPhone;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accountNumber == null) ? 0 : accountNumber.hashCode());
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((creditDebitCode == null) ? 0 : creditDebitCode.hashCode());
		result = prime * result + ((receiverCellPhone == null) ? 0 : receiverCellPhone.hashCode());
		result = prime * result + ((receiverEmail == null) ? 0 : receiverEmail.hashCode());
		result = prime * result + ((receiverFirstName == null) ? 0 : receiverFirstName.hashCode());
		result = prime * result + ((receiverLastName == null) ? 0 : receiverLastName.hashCode());
		result = prime * result + ((senderCellPhone == null) ? 0 : senderCellPhone.hashCode());
		result = prime * result + ((senderEmail == null) ? 0 : senderEmail.hashCode());
		result = prime * result + ((senderFirstName == null) ? 0 : senderFirstName.hashCode());
		result = prime * result + ((senderLastName == null) ? 0 : senderLastName.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((transId == null) ? 0 : transId.hashCode());
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
		Transaction other = (Transaction) obj;
		if (accountNumber == null) {
			if (other.accountNumber != null)
				return false;
		} else if (!accountNumber.equals(other.accountNumber))
			return false;
		if (amount == null) {
			if (other.amount != null)
				return false;
		} else if (!amount.equals(other.amount))
			return false;
		if (creditDebitCode == null) {
			if (other.creditDebitCode != null)
				return false;
		} else if (!creditDebitCode.equals(other.creditDebitCode))
			return false;
		if (receiverCellPhone == null) {
			if (other.receiverCellPhone != null)
				return false;
		} else if (!receiverCellPhone.equals(other.receiverCellPhone))
			return false;
		if (receiverEmail == null) {
			if (other.receiverEmail != null)
				return false;
		} else if (!receiverEmail.equals(other.receiverEmail))
			return false;
		if (receiverFirstName == null) {
			if (other.receiverFirstName != null)
				return false;
		} else if (!receiverFirstName.equals(other.receiverFirstName))
			return false;
		if (receiverLastName == null) {
			if (other.receiverLastName != null)
				return false;
		} else if (!receiverLastName.equals(other.receiverLastName))
			return false;
		if (senderCellPhone == null) {
			if (other.senderCellPhone != null)
				return false;
		} else if (!senderCellPhone.equals(other.senderCellPhone))
			return false;
		if (senderEmail == null) {
			if (other.senderEmail != null)
				return false;
		} else if (!senderEmail.equals(other.senderEmail))
			return false;
		if (senderFirstName == null) {
			if (other.senderFirstName != null)
				return false;
		} else if (!senderFirstName.equals(other.senderFirstName))
			return false;
		if (senderLastName == null) {
			if (other.senderLastName != null)
				return false;
		} else if (!senderLastName.equals(other.senderLastName))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (transId == null) {
			if (other.transId != null)
				return false;
		} else if (!transId.equals(other.transId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Transaction [transId=" + transId + ", creditDebitCode=" + creditDebitCode + ", accountNumber="
				+ accountNumber + ", amount=" + amount + ", receiverFirstName=" + receiverFirstName
				+ ", receiverLastName=" + receiverLastName + ", receiverEmail=" + receiverEmail + ", receiverCellPhone="
				+ receiverCellPhone + ", senderFirstName=" + senderFirstName + ", senderLastName=" + senderLastName
				+ ", senderEmail=" + senderEmail + ", senderCellPhone=" + senderCellPhone + ", status=" + status + "]";
	}

}
