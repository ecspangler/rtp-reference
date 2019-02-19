package rtp.demo.debtor.domain.model.payment;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CreditPayment")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "DEBTOR_CREDIT_PAYMENT")
public class CreditPayment {

}
