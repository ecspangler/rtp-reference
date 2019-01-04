package rtp.demo.creditor.validation.wrappers;

public class CreditorBank {

	private String routingAndTransitNumber;

	public String getRoutingAndTransitNumber() {
		return routingAndTransitNumber;
	}

	public void setRoutingAndTransitNumber(String routingAndTransitNumber) {
		this.routingAndTransitNumber = routingAndTransitNumber;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((routingAndTransitNumber == null) ? 0 : routingAndTransitNumber.hashCode());
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
		CreditorBank other = (CreditorBank) obj;
		if (routingAndTransitNumber == null) {
			if (other.routingAndTransitNumber != null)
				return false;
		} else if (!routingAndTransitNumber.equals(other.routingAndTransitNumber))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CreditorBank [routingAndTransitNumber=" + routingAndTransitNumber + "]";
	}
}
