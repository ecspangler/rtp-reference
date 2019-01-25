package rtp.demo.debtor.domain.model.transaction;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DebitTransaction")
@XmlAccessorType(XmlAccessType.FIELD)
public class DebitTransaction extends Transaction {

	private static final long serialVersionUID = 4075683530008322892L;

	private String receiverFirstName;
	private String receiverLastName;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((receiverFirstName == null) ? 0 : receiverFirstName.hashCode());
		result = prime * result + ((receiverLastName == null) ? 0 : receiverLastName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DebitTransaction other = (DebitTransaction) obj;
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
		return true;
	}

	@Override
	public String toString() {
		return "DebitTransaction [receiverFirstName=" + receiverFirstName + ", receiverLastName=" + receiverLastName
				+ "]";
	}

}
