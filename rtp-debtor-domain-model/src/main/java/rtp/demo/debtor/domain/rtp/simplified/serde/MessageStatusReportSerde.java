package rtp.demo.debtor.domain.rtp.simplified.serde;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import rtp.demo.debtor.domain.rtp.simplified.MessageStatusReport;

public class MessageStatusReportSerde implements Serde<MessageStatusReport> {

	private final Serializer<MessageStatusReport> serializer;
	private final Deserializer<MessageStatusReport> deserializer;

	public MessageStatusReportSerde() {
		this.deserializer = new MessageStatusReportDeserializer();
		this.serializer = new MessageStatusReportSerializer();
	}

	@Override
	public void configure(Map<String, ?> settings, boolean isKey) {
		this.serializer.configure(settings, isKey);
		this.deserializer.configure(settings, isKey);
	}

	@Override
	public void close() {
		this.deserializer.close();
		this.serializer.close();
	}

	@Override
	public Serializer<MessageStatusReport> serializer() {
		return this.serializer;
	}

	@Override
	public Deserializer<MessageStatusReport> deserializer() {
		return this.deserializer;
	}

}
