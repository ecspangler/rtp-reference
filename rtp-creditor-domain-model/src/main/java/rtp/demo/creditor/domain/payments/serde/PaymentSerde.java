package rtp.demo.creditor.domain.payments.serde;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import rtp.demo.creditor.domain.payments.Payment;

public class PaymentSerde implements Serde<Payment> {

	private final Serializer<Payment> serializer;
	private final Deserializer<Payment> deserializer;

	public PaymentSerde() {
		this.deserializer = new PaymentDeserializer();
		this.serializer = new PaymentSerializer();
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
	public Serializer<Payment> serializer() {
		return this.serializer;
	}

	@Override
	public Deserializer<Payment> deserializer() {
		return this.deserializer;
	}

}