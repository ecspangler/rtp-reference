package rtp.message.model.serde;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import iso.std.iso._20022.tech.xsd.pacs_008_001.FIToFICustomerCreditTransferV06;

public class FIToFICustomerCreditTransferV06Deserializer implements Deserializer<FIToFICustomerCreditTransferV06> {

	@Override
	public void close() {
	}

	@Override
	public void configure(Map arg0, boolean arg1) {
	}

	@Override
	public FIToFICustomerCreditTransferV06 deserialize(String arg0, byte[] arg1) {
		ObjectMapper mapper = new ObjectMapper();
		FIToFICustomerCreditTransferV06 message = null;
		try {
			message = mapper.readValue(arg1, FIToFICustomerCreditTransferV06.class);
		} catch (Exception e) {

			e.printStackTrace();
		}
		return message;
	}

}
