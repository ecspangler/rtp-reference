package rtp.demo.creditor.validation.runners;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(features = { "classpath:features" }, glue = { "rtp.demo.creditor.validation.steps" }, tags = {
		"~@Ignore" })
public class PaymentsValidationTest {

}