package rtp.message.model.serde;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import iso.std.iso._20022.tech.xsd.pacs_002_001.FIToFIPaymentStatusReportV07;

public class FIToFIPaymentStatusReportV07Deserializer implements Deserializer<FIToFIPaymentStatusReportV07> {

	@Override
	public void close() {
	}

	@Override
	public void configure(Map arg0, boolean arg1) {
	}

	@Override
	public FIToFIPaymentStatusReportV07 deserialize(String arg0, byte[] arg1) {
		ObjectMapper mapper = new ObjectMapper();
		FIToFIPaymentStatusReportV07 message = null;
		try {
			message = mapper.readValue(arg1, FIToFIPaymentStatusReportV07.class);
		} catch (Exception e) {

			e.printStackTrace();
		}
		return message;
	}
}
