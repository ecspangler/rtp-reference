package rtp.demo.creditor.intake.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rtp.demo.creditor.domain.account.Account;
import rtp.demo.creditor.domain.rtp.simplified.CreditTransferMessage;
import rtp.demo.creditor.repository.account.AccountRepository;
import rtp.demo.creditor.repository.account.JdgAccountRepository;
import rtp.demo.creditor.validation.PaymentValidationRequest;
import rtp.demo.creditor.validation.wrappers.Accounts;
import rtp.demo.creditor.validation.wrappers.CreditorBank;

@Service
public class CreditTransferMessageValidationBean {

	private static final Logger LOG = Logger.getLogger(CreditTransferMessageValidationBean.class.getName());

	private static final String KSESSION = "payments-validation-ksession";

	private final KieContainer kieContainer;
	private AccountRepository accountRepository = new JdgAccountRepository();

	@Autowired
	public CreditTransferMessageValidationBean(KieContainer kieContainer) {
		LOG.info("Initializing a new session.");
		this.kieContainer = kieContainer;
	}

	public PaymentValidationRequest validateCreditTransferMessage(CreditTransferMessage creditTransferMessage) {
		LOG.info("Validation Rules");

		LOG.info(creditTransferMessage.toString());

		Accounts accounts = new Accounts();
		Account account = null;
		if (creditTransferMessage.getCreditorAccountNumber() != null) {
			account = accountRepository.getAccount(creditTransferMessage.getCreditorAccountNumber());
			if (account != null) {
				accounts.getAccounts().add(account);
			}
		}

		PaymentValidationRequest validationRequest = new PaymentValidationRequest();
		validationRequest.setCreditTransferMessage(creditTransferMessage);

		StatelessKieSession kSession = kieContainer.newStatelessKieSession(KSESSION);

		List<Object> facts = new ArrayList<Object>();
		facts.add(makeDummyCreditor());
		facts.add(accounts);
		facts.add(validationRequest);

		LOG.info("Incoming Payment Validation Request: " + validationRequest);
		kSession.execute(facts);
		LOG.info("Outgoing Payment Validation Request: " + validationRequest);

		return validationRequest;
	}

	// Test data for reference example
	private CreditorBank makeDummyCreditor() {
		CreditorBank creditor = new CreditorBank();
		creditor.setRoutingAndTransitNumber("020010001");
		return creditor;
	}

	public KieContainer getKieContainer() {
		return kieContainer;
	}

}
