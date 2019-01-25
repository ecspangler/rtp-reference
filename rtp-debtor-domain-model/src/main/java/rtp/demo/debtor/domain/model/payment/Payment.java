package rtp.demo.debtor.domain.model.payment;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Payment")
@XmlAccessorType(XmlAccessType.FIELD)
public class Payment implements Serializable {

	private static final long serialVersionUID = -8864097979538228811L;

	private String paymentId;
	private String debtorAccountNumber;
	private BigDecimal amount;
	private String receiverFirstName;
	private String receiverLastName;
	private String receiverEmail;
	private String receiverCellPhone;
	private String receiverRoutingNumber;
	private String receiverAccountNumber;

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getDebtorAccountNumber() {
		return debtorAccountNumber;
	}

	public void setDebtorAccountNumber(String debtorAccountNumber) {
		this.debtorAccountNumber = debtorAccountNumber;
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

	public String getReceiverRoutingNumber() {
		return receiverRoutingNumber;
	}

	public void setReceiverRoutingNumber(String receiverRoutingNumber) {
		this.receiverRoutingNumber = receiverRoutingNumber;
	}

	public String getReceiverAccountNumber() {
		return receiverAccountNumber;
	}

	public void setReceiverAccountNumber(String receiverAccountNumber) {
		this.receiverAccountNumber = receiverAccountNumber;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((debtorAccountNumber == null) ? 0 : debtorAccountNumber.hashCode());
		result = prime * result + ((paymentId == null) ? 0 : paymentId.hashCode());
		result = prime * result + ((receiverAccountNumber == null) ? 0 : receiverAccountNumber.hashCode());
		result = prime * result + ((receiverCellPhone == null) ? 0 : receiverCellPhone.hashCode());
		result = prime * result + ((receiverEmail == null) ? 0 : receiverEmail.hashCode());
		result = prime * result + ((receiverFirstName == null) ? 0 : receiverFirstName.hashCode());
		result = prime * result + ((receiverLastName == null) ? 0 : receiverLastName.hashCode());
		result = prime * result + ((receiverRoutingNumber == null) ? 0 : receiverRoutingNumber.hashCode());
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
		Payment other = (Payment) obj;
		if (amount == null) {
			if (other.amount != null)
				return false;
		} else if (!amount.equals(other.amount))
			return false;
		if (debtorAccountNumber == null) {
			if (other.debtorAccountNumber != null)
				return false;
		} else if (!debtorAccountNumber.equals(other.debtorAccountNumber))
			return false;
		if (paymentId == null) {
			if (other.paymentId != null)
				return false;
		} else if (!paymentId.equals(other.paymentId))
			return false;
		if (receiverAccountNumber == null) {
			if (other.receiverAccountNumber != null)
				return false;
		} else if (!receiverAccountNumber.equals(other.receiverAccountNumber))
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
		if (receiverRoutingNumber == null) {
			if (other.receiverRoutingNumber != null)
				return false;
		} else if (!receiverRoutingNumber.equals(other.receiverRoutingNumber))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Payment [debtorAccountNumber=" + debtorAccountNumber + ", amount=" + amount + ", receiverFirstName="
				+ receiverFirstName + ", receiverLastName=" + receiverLastName + ", receiverEmail=" + receiverEmail
				+ ", receiverCellPhone=" + receiverCellPhone + ", receiverRoutingNumber=" + receiverRoutingNumber
				+ ", receiverAccountNumber=" + receiverAccountNumber + "]";
	}

}
