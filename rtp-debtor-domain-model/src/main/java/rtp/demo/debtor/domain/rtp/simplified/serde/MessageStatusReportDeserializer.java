package rtp.demo.debtor.domain.rtp.simplified.serde;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import rtp.demo.debtor.domain.rtp.simplified.MessageStatusReport;

public class MessageStatusReportDeserializer implements Deserializer<MessageStatusReport> {

	@Override
	public void close() {
	}

	@Override
	public void configure(Map arg0, boolean arg1) {
	}

	@Override
	public MessageStatusReport deserialize(String arg0, byte[] arg1) {
		ObjectMapper mapper = new ObjectMapper();
		MessageStatusReport message = null;
		try {
			message = mapper.readValue(arg1, MessageStatusReport.class);
		} catch (Exception e) {

			e.printStackTrace();
		}
		return message;
	}

}
