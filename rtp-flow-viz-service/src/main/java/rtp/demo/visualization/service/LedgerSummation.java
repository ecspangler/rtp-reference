package rtp.demo.visualization.service;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "LedgerSumation")
@XmlAccessorType(XmlAccessType.FIELD)
public class LedgerSummation {

	private double creditor;
	private double debtor;

	public double getCreditor() {
		return creditor;
	}

	public void setCreditor(double creditor) {
		this.creditor = creditor;
	}

	public double getDebtor() {
		return debtor;
	}

	public void setDebtor(double debtor) {
		this.debtor = debtor;
	}

	public void addPayment(BigDecimal value) {
		creditor+=value.doubleValue();
		debtor-=value.doubleValue();

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(creditor);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(debtor);
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
		if (Double.doubleToLongBits(debtor) != Double.doubleToLongBits(other.debtor))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LedgerSummation [creditor=" + creditor + ", debtor=" + debtor + "]";
	}

}
