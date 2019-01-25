package rtp.demo.creditor.validation.steps;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;

import rtp.demo.creditor.domain.rtp.simplified.CreditTransferMessage;
import rtp.demo.creditor.validation.PaymentValidationRequest;
import rtp.demo.creditor.validation.wrappers.Accounts;
import rtp.demo.creditor.validation.wrappers.CreditorBank;
import rtp.demo.creditor.validation.wrappers.ProcessingDateTime;

public class PaymentsValidationTestContext {

	private CreditorBank creditor = new CreditorBank();
	private ProcessingDateTime processingDateTime;
	private Accounts accounts = new Accounts();
	private CreditTransferMessage creditTransferMessage = new CreditTransferMessage();
	private Set<PaymentValidationRequest> validationRequestResults = new HashSet<PaymentValidationRequest>();
	private PaymentValidationRequest validationRequest = new PaymentValidationRequest();

	public void executeRules() {
		validationRequest.setCreditTransferMessage(creditTransferMessage);

		KieServices ks = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();

		StatelessKieSession kSession = kContainer.newStatelessKieSession("payments-validation-ksession");

		List<Object> facts = new ArrayList<Object>();
		facts.add(creditor);
		facts.add(processingDateTime);
		facts.add(accounts);
		facts.add(validationRequest);

		kSession.execute(facts);
	}

	public CreditorBank getCreditor() {
		return creditor;
	}

	public void setCreditor(CreditorBank creditor) {
		this.creditor = creditor;
	}

	public ProcessingDateTime getProcessingDateTime() {
		return processingDateTime;
	}

	public void setProcessingDateTime(ProcessingDateTime processingDateTime) {
		this.processingDateTime = processingDateTime;
	}

	public Accounts getAccounts() {
		return accounts;
	}

	public void setAccounts(Accounts accounts) {
		this.accounts = accounts;
	}

	public CreditTransferMessage getCreditTransferMessage() {
		return creditTransferMessage;
	}

	public void setCreditTransferMessage(CreditTransferMessage creditTransferMessage) {
		this.creditTransferMessage = creditTransferMessage;
	}

	public Set<PaymentValidationRequest> getValidationRequestResults() {
		return validationRequestResults;
	}

	public void setValidationRequestResults(Set<PaymentValidationRequest> validationRequestResults) {
		this.validationRequestResults = validationRequestResults;
	}

	public PaymentValidationRequest getValidationRequest() {
		return validationRequest;
	}

	public void setValidationRequest(PaymentValidationRequest validationRequest) {
		this.validationRequest = validationRequest;
	}

}
