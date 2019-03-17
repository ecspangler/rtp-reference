package rtp.message.model.serde;

import java.util.Map;

import iso.std.iso._20022.tech.xsd.pacs_002_001.FIToFIPaymentStatusReportV07;
import org.apache.kafka.common.serialization.Serializer;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FIToFIPaymentStatusReportV07Serializer implements Serializer<FIToFIPaymentStatusReportV07> {

	@Override
	public void close() {

	}

	@Override
	public void configure(Map arg0, boolean arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public byte[] serialize(String arg0, FIToFIPaymentStatusReportV07 arg1) {
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
