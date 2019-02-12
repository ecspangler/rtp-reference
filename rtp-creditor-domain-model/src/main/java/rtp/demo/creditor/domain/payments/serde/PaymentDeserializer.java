package rtp.demo.creditor.domain.payments.serde;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import rtp.demo.creditor.domain.payments.Payment;

public class PaymentDeserializer implements Deserializer<Payment> {

	public PaymentDeserializer() {

	}

	@Override
	public void close() {
	}

	@Override
	public void configure(Map arg0, boolean arg1) {
	}

	@Override
	public Payment deserialize(String arg0, byte[] arg1) {
		ObjectMapper mapper = new ObjectMapper();
		Payment payment = null;
		try {
			payment = mapper.readValue(arg1, Payment.class);
		} catch (Exception e) {

			e.printStackTrace();
		}
		return payment;
	}

}
