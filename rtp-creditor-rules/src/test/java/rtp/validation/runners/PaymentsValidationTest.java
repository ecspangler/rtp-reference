package rtp.validation.runners;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(features = { "classpath:rtp/demo/features" }, glue = { "rtp.validation.steps" }, tags = { "~@Ignore" })
public class PaymentsValidationTest {

}
