package rtp.demo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

public class test {

	@Test
	public void test() {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

		LocalDateTime now = LocalDateTime.now();

		System.out.println(formatter.format(now));
	}

}
