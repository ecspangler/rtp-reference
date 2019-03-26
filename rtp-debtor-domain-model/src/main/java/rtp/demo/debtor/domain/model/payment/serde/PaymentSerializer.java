package rtp.demo.debtor.domain.model.payment.serde;

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import rtp.demo.debtor.domain.model.payment.Payment;

public class PaymentSerializer implements Serializer<Payment> {

	@Override
	public void close() {

	}

	@Override
	public void configure(Map arg0, boolean arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public byte[] serialize(String arg0, Payment arg1) {
		byte[] retVal = null;
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			retVal = objectMapper.writeValueAsString(arg1).getBytes();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retVal;
	}
}
