package rtp.demo.visualization.service;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "LedgerSumation")
@XmlAccessorType(XmlAccessType.FIELD)
public class LedgerSummation {

	private double creditor;
	private double debitor;

	public double getCreditor() {
		return creditor;
	}

	public void setCreditor(double creditor) {
		this.creditor = creditor;
	}

	public double getDebitor() {
		return debitor;
	}

	public void setDebitor(double debitor) {
		this.debitor = debitor;
	}
	
	public void addPayment(BigDecimal value) {
		creditor+=value.doubleValue();
		debitor-=value.doubleValue();
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(creditor);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(debitor);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		LedgerSummation other = (LedgerSummation) obj;
		if (Double.doubleToLongBits(creditor) != Double.doubleToLongBits(other.creditor))
			return false;
		if (Double.doubleToLongBits(debitor) != Double.doubleToLongBits(other.debitor))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LedgerSummation [creditor=" + creditor + ", debitor=" + debitor + "]";
	}

}
