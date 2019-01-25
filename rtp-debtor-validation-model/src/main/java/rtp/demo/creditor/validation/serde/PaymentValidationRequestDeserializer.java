package rtp.demo.creditor.validation.serde;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import rtp.demo.creditor.validation.PaymentValidationRequest;

public class PaymentValidationRequestDeserializer implements Deserializer {

	@Override
	public void close() {
	}

	@Override
	public void configure(Map arg0, boolean arg1) {
	}

	@Override
	public Object deserialize(String arg0, byte[] arg1) {
		ObjectMapper mapper = new ObjectMapper();
		PaymentValidationRequest request = null;
		try {
			request = mapper.readValue(arg1, PaymentValidationRequest.class);
		} catch (Exception e) {

			e.printStackTrace();
		}
		return request;
	}
}
