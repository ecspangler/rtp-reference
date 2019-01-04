package rtp.demo.creditor.validation.wrappers;

import java.time.LocalDateTime;

public class ProcessingDateTime {

	private LocalDateTime processingDateTime;

	public LocalDateTime getProcessingDateTime() {
		return processingDateTime;
	}

	public void setProcessingDateTime(LocalDateTime processingDateTime) {
		this.processingDateTime = processingDateTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((processingDateTime == null) ? 0 : processingDateTime.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProcessingDateTime other = (ProcessingDateTime) obj;
		if (processingDateTime == null) {
			if (other.processingDateTime != null)
				return false;
		} else if (!processingDateTime.equals(other.processingDateTime))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ProcessingDateTime [processingDateTime=" + processingDateTime + "]";
	}

}
