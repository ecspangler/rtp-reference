package rtp.demo.debtor.domain.model.payment;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment implements Serializable {

	private static final long serialVersionUID = -8864097979538228811L;

	private String clientId;
	private BigDecimal amount;
	private LocalDateTime creationDate;
	private String senderFirstName;
	private String senderLastName;
	private String receiverFirstName;
	private String receiverLastName;
	private String receiverEmail;
	private String receiverCellPhone;
	private Boolean sendPayment;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public LocalDateTime getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = creationDate;
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

	public Boolean getSendPayment() {
		return sendPayment;
	}

	public void setSendPayment(Boolean sendPayment) {
		this.sendPayment = sendPayment;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((clientId == null) ? 0 : clientId.hashCode());
		result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());
		result = prime * result + ((receiverCellPhone == null) ? 0 : receiverCellPhone.hashCode());
		result = prime * result + ((receiverEmail == null) ? 0 : receiverEmail.hashCode());
		result = prime * result + ((receiverFirstName == null) ? 0 : receiverFirstName.hashCode());
		result = prime * result + ((receiverLastName == null) ? 0 : receiverLastName.hashCode());
		result = prime * result + ((sendPayment == null) ? 0 : sendPayment.hashCode());
		result = prime * result + ((senderFirstName == null) ? 0 : senderFirstName.hashCode());
		result = prime * result + ((senderLastName == null) ? 0 : senderLastName.hashCode());
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
		if (clientId == null) {
			if (other.clientId != null)
				return false;
		} else if (!clientId.equals(other.clientId))
			return false;
		if (creationDate == null) {
			if (other.creationDate != null)
				return false;
		} else if (!creationDate.equals(other.creationDate))
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
		if (sendPayment == null) {
			if (other.sendPayment != null)
				return false;
		} else if (!sendPayment.equals(other.sendPayment))
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
		return true;
	}

	@Override
	public String toString() {
		return "Payment [clientId=" + clientId + ", amount=" + amount + ", creationDate=" + creationDate
				+ ", senderFirstName=" + senderFirstName + ", senderLastName=" + senderLastName + ", receiverFirstName="
				+ receiverFirstName + ", receiverLastName=" + receiverLastName + ", receiverEmail=" + receiverEmail
				+ ", receiverCellPhone=" + receiverCellPhone + ", sendPayment=" + sendPayment + "]";
	}

}
