package rtp.demo.debtor.domain.rtp.simplified.serde;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import rtp.demo.debtor.domain.rtp.simplified.CreditTransferMessage;

public class CreditTransferMessageDeserializer implements Deserializer<CreditTransferMessage> {

	@Override
	public void close() {
	}

	@Override
	public void configure(Map arg0, boolean arg1) {
	}

	@Override
	public CreditTransferMessage deserialize(String arg0, byte[] arg1) {
		ObjectMapper mapper = new ObjectMapper();
		CreditTransferMessage message = null;
		try {
			message = mapper.readValue(arg1, CreditTransferMessage.class);
		} catch (Exception e) {

			e.printStackTrace();
		}
		return message;
	}

}
