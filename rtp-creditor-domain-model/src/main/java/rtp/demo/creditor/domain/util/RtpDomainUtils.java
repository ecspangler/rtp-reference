package rtp.demo.creditor.domain.util;

import java.time.LocalDateTime;

import javax.xml.datatype.XMLGregorianCalendar;

public class RtpDomainUtils {

	public static LocalDateTime toLocalDateTime(XMLGregorianCalendar xmlGregorianCalendar) {
		return xmlGregorianCalendar.toGregorianCalendar().toZonedDateTime().toLocalDateTime();
	}

}
