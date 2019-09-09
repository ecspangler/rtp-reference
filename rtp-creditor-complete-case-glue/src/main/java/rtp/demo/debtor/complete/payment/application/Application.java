package rtp.demo.debtor.complete.payment.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import rtp.demo.debtor.complete.payment.stream.UpdateValidationStatusGlue;

@SpringBootApplication
public class Application {

	// must have a main method spring-boot can run
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		UpdateValidationStatusGlue stream = new UpdateValidationStatusGlue();
	}

}
