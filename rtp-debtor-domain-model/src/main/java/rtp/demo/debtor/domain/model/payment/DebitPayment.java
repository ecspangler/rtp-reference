package rtp.demo.debtor.domain.model.payment;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DebitPayment")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "DEBTOR_DEBIT_PAYMENT")
public class DebitPayment extends Payment {

	private static final long serialVersionUID = -3174709855548644836L;

}
