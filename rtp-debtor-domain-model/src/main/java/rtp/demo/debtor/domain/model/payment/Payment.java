package rtp.demo.debtor.domain.model.payment;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import rtp.demo.debtor.domain.rtp.simplified.MessageStatusReport;

@XmlRootElement(name = "Payment")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Payment implements Serializable {

	private static final long serialVersionUID = -8864097979538228811L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", updatable = false, unique = true)
	private BigInteger id;
	@Column(name = "PAYMENT_ID", unique = true, nullable = false, length = 256)
	private String paymentId;
	@Column(name = "SENDER_ROUTING_NUM", unique = true, nullable = false, length = 256)
	private String senderRoutingNumber;
	@Column(name = "SENDER_ACCT_NUM", unique = true, nullable = false, length = 256)
	private String senderAccountNumber;
	@Column(name = "SENDER_FIRST_NAME", unique = true, nullable = false, length = 256)
	private String senderFirstName;
	@Column(name = "SENDER_LAST_NAME", unique = true, nullable = false, length = 256)
	private String senderLastName;
	@Column(name = "SENDER_EMAIL", unique = true, nullable = false, length = 256)
	private String senderEmail;
	@Column(name = "SENDER_CELL_PHONE", unique = true, nullable = false, length = 256)
	private String senderCellPhone;
	@Column(name = "PAYMENT_AMOUNT", unique = true, nullable = false, length = 256)
	private BigDecimal amount;
	@Column(name = "RECEIVER_FIRST_NAME", unique = true, nullable = false, length = 256)
	private String receiverFirstName;
	@Column(name = "RECEIVER_LAST_NAME", unique = true, nullable = false, length = 256)
	private String receiverLastName;
	@Column(name = "RECEIVER_EMAIL", unique = true, nullable = false, length = 256)
	private String receiverEmail;
	@Column(name = "RECEIVER_CELL_PHONE", unique = true, nullable = false, length = 256)
	private String receiverCellPhone;
	@Column(name = "RECEIVER_ROUTING_NUM", unique = true, nullable = false, length = 256)
	private String receiverRoutingNumber;
	@Column(name = "RECEIVER_ACCT_NUM", unique = true, nullable = false, length = 256)
	private String receiverAccountNumber;
	@Column(name = "STATUS", unique = true, nullable = false, length = 256)
	private String status = "PENDING";
	@Column(name = "MESSAGE_STATUS_REPORT_ID", unique = true, nullable = false, length = 256)
	private String messageStatusReportId;

	public Payment() {
		super();
	}

	public Payment(Payment payment, MessageStatusReport messageStatusReport, String status) {
		super();
		this.id = payment.getId();
		this.paymentId = payment.getPaymentId();
		this.senderAccountNumber = payment.getSenderAccountNumber();
		this.amount = payment.getAmount();
		this.receiverFirstName = payment.getReceiverFirstName();
		this.receiverLastName = payment.getReceiverLastName();
		this.receiverCellPhone = payment.getReceiverCellPhone();
		this.receiverAccountNumber = payment.getReceiverAccountNumber();
		this.status = status;
		this.messageStatusReportId = messageStatusReport.getMessageStatusReportId();
	}

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getSenderRoutingNumber() {
		return senderRoutingNumber;
	}

	public void setSenderRoutingNumber(String senderRoutingNumber) {
		this.senderRoutingNumber = senderRoutingNumber;
	}

	public String getSenderAccountNumber() {
		return senderAccountNumber;
	}

	public void setSenderAccountNumber(String senderAccountNumber) {
		this.senderAccountNumber = senderAccountNumber;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessageStatusReportId() {
		return messageStatusReportId;
	}

	public void setMessageStatusReportId(String messageStatusReportId) {
		this.messageStatusReportId = messageStatusReportId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((messageStatusReportId == null) ? 0 : messageStatusReportId.hashCode());
		result = prime * result + ((paymentId == null) ? 0 : paymentId.hashCode());
		result = prime * result + ((receiverAccountNumber == null) ? 0 : receiverAccountNumber.hashCode());
		result = prime * result + ((receiverCellPhone == null) ? 0 : receiverCellPhone.hashCode());
		result = prime * result + ((receiverEmail == null) ? 0 : receiverEmail.hashCode());
		result = prime * result + ((receiverFirstName == null) ? 0 : receiverFirstName.hashCode());
		result = prime * result + ((receiverLastName == null) ? 0 : receiverLastName.hashCode());
		result = prime * result + ((receiverRoutingNumber == null) ? 0 : receiverRoutingNumber.hashCode());
		result = prime * result + ((senderAccountNumber == null) ? 0 : senderAccountNumber.hashCode());
		result = prime * result + ((senderCellPhone == null) ? 0 : senderCellPhone.hashCode());
		result = prime * result + ((senderEmail == null) ? 0 : senderEmail.hashCode());
		result = prime * result + ((senderFirstName == null) ? 0 : senderFirstName.hashCode());
		result = prime * result + ((senderLastName == null) ? 0 : senderLastName.hashCode());
		result = prime * result + ((senderRoutingNumber == null) ? 0 : senderRoutingNumber.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		if (messageStatusReportId == null) {
			if (other.messageStatusReportId != null)
				return false;
		} else if (!messageStatusReportId.equals(other.messageStatusReportId))
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
		if (senderAccountNumber == null) {
			if (other.senderAccountNumber != null)
				return false;
		} else if (!senderAccountNumber.equals(other.senderAccountNumber))
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
		if (senderRoutingNumber == null) {
			if (other.senderRoutingNumber != null)
				return false;
		} else if (!senderRoutingNumber.equals(other.senderRoutingNumber))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Payment [paymentId=" + paymentId + ", senderRoutingNumber=" + senderRoutingNumber
				+ ", senderAccountNumber=" + senderAccountNumber + ", senderFirstName=" + senderFirstName
				+ ", senderLastName=" + senderLastName + ", senderEmail=" + senderEmail + ", senderCellPhone="
				+ senderCellPhone + ", amount=" + amount + ", receiverFirstName=" + receiverFirstName
				+ ", receiverLastName=" + receiverLastName + ", receiverEmail=" + receiverEmail + ", receiverCellPhone="
				+ receiverCellPhone + ", receiverRoutingNumber=" + receiverRoutingNumber + ", receiverAccountNumber="
				+ receiverAccountNumber + ", status=" + status + ", messageStatusReportId=" + messageStatusReportId
				+ ", getPaymentId()=" + getPaymentId() + ", getSenderRoutingNumber()=" + getSenderRoutingNumber()
				+ ", getSenderAccountNumber()=" + getSenderAccountNumber() + ", getSenderFirstName()="
				+ getSenderFirstName() + ", getSenderLastName()=" + getSenderLastName() + ", getSenderEmail()="
				+ getSenderEmail() + ", getSenderCellPhone()=" + getSenderCellPhone() + ", getAmount()=" + getAmount()
				+ ", getReceiverFirstName()=" + getReceiverFirstName() + ", getReceiverLastName()="
				+ getReceiverLastName() + ", getReceiverEmail()=" + getReceiverEmail() + ", getReceiverCellPhone()="
				+ getReceiverCellPhone() + ", getReceiverRoutingNumber()=" + getReceiverRoutingNumber()
				+ ", getReceiverAccountNumber()=" + getReceiverAccountNumber() + ", getStatus()=" + getStatus()
				+ ", getMessageStatusReportId()=" + getMessageStatusReportId() + ", hashCode()=" + hashCode()
				+ ", getClass()=" + getClass() + ", toString()=" + super.toString() + "]";
	}

}
