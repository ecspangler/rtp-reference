package rtp.demo.visualization.service;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import rtp.demo.debtor.domain.account.Account;
import rtp.demo.debtor.domain.model.payment.CreditPayment;
import rtp.demo.debtor.domain.model.payment.DebitPayment;
import rtp.demo.debtor.domain.model.payment.Payments;
import rtp.demo.debtor.domain.model.transaction.Transaction;
import rtp.demo.debtor.domain.model.transaction.Transactions;
import rtp.demo.visualization.producer.DebtorPaymentsProducer;
import rtp.demo.debtor.repository.account.AccountRepository;
import rtp.demo.debtor.repository.account.JdgAccountRepository;
import rtp.demo.visualization.beans.PayeeAccountLookupBean;
import rtp.demo.repository.CreditPaymentRepository;
import rtp.demo.repository.DebitPaymentRepository;
import rtp.demo.repository.MySqlCreditPaymentRepository;
import rtp.demo.repository.MySqlDebitPaymentRepository;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

public class DebtorPaymentService extends AbstractVerticle {

	private static final Logger LOGGER = Logger.getLogger(DebtorPaymentService.class.getName());

	private AccountRepository accountRepository = new JdgAccountRepository();
	private CreditPaymentRepository creditPaymentRepository = new MySqlCreditPaymentRepository();
	private DebitPaymentRepository debitPaymentRepository = new MySqlDebitPaymentRepository();

	@Override
	public void start() {
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());

		router.post("/payments-service/payments").handler(this::createPayments);
		router.get("/transactions-service/transactions").handler(this::getTransactions);
		router.post("/transactions-service/queries/transactions").handler(this::getTransactionsByAccount);

		router.get("/*").handler(StaticHandler.create());

		vertx.createHttpServer().requestHandler(router::accept).listen(8080);
		LOGGER.info("THE HTTP APPLICATION HAS STARTED");

		// Populate test payees in the cache, for purposes of the reference example
		Account testAccount1 = new Account();
		testAccount1.setRoutingNumber("020010001");
		testAccount1.setAccountNumber("12000194212199001");
		accountRepository.addAccount("alopez@company.com", testAccount1);

	}

	private void createPayments(RoutingContext routingContext) {
		Payments payments = routingContext.getBodyAsJson().mapTo(Payments.class);
		LOGGER.info("Creating payments: " + payments);

		DebtorPaymentsProducer debtorPaymentsProducer = new DebtorPaymentsProducer();
		payments.getPayments().forEach(payment -> {

			// Find the routing number and account number for the payee
			payment = PayeeAccountLookupBean.enrichPayeeAccountInformation(payment);

			try {
				// Payment key generated based on timestamp for the reference example
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
				LocalDateTime now = LocalDateTime.now();
				payment.setPaymentId("ABCBANK" + formatter.format(now));

				debtorPaymentsProducer.sendMessage(payment.getPaymentId(), payment);
				debitPaymentRepository.addPayment(new DebitPayment(payment));

			} catch (Exception e) {
				LOGGER.severe("Error publishing payment to topic");
				LOGGER.severe(e.getMessage());
				e.printStackTrace();
			}
		});

		HttpServerResponse response = routingContext.response();
		response.putHeader(CONTENT_TYPE, "application/json; charset=utf-8");
		response.putHeader("Access-Control-Allow-Origin", "*");

		response.end(Json.encode(payments));
	}

	private void getTransactions(RoutingContext routingContext) {
		LOGGER.info("Retrieving transactions");
		Transactions transactions = new Transactions();

		// call lookup

		transactions.getTransactions().add(makeDummyTransaction());
		Transaction dummyTransaction2 = makeDummyTransaction();
		dummyTransaction2.setSenderFirstName("John");
		dummyTransaction2.setSenderLastName("Smith");
		dummyTransaction2.setReceiverFirstName("Amy");
		dummyTransaction2.setReceiverLastName("Lopez");
		dummyTransaction2.setReceiverEmail("alopez@company.com");
		dummyTransaction2.setAmount(new BigDecimal("100.25"));
		dummyTransaction2.setStatus("PENDING");
		dummyTransaction2.setCreditDebitCode("DEBIT");
		dummyTransaction2.setAccountNumber("12000194212199001");
		transactions.getTransactions().add(dummyTransaction2);

		routingContext.response().putHeader(CONTENT_TYPE, "application/json; charset=utf-8")
				.putHeader("Access-Control-Allow-Origin", "*").end(Json.encodePrettily(transactions));
	}

	private void getTransactionsByAccount(RoutingContext routingContext) {
		TransactionsRequest transactionsRequest = routingContext.getBodyAsJson().mapTo(TransactionsRequest.class);
		LOGGER.info("Retrieving transactions for account: " + transactionsRequest.getAccountNumber());
		Transactions transactions = new Transactions();

		// call lookups
		List<DebitPayment> debitPayments = debitPaymentRepository.getPayments(transactionsRequest.getAccountNumber());
		List<CreditPayment> creditPayments = creditPaymentRepository
				.getPayments(transactionsRequest.getAccountNumber());

		debitPayments.forEach(debitPayment -> {
			Transaction transaction = new Transaction();
			transaction.setTransId(debitPayment.getPaymentId());
			transaction.setAmount(debitPayment.getAmount());
			transaction.setCreditDebitCode("DEBIT");
			transaction.setReceiverFirstName(debitPayment.getReceiverFirstName());
			transaction.setReceiverLastName(debitPayment.getReceiverLastName());
			transaction.setReceiverEmail(debitPayment.getReceiverEmail());
			transaction.setReceiverCellPhone(debitPayment.getReceiverCellPhone());
			transaction.setStatus(debitPayment.getStatus());
			transactions.getTransactions().add(transaction);
		});

		creditPayments.forEach(creditPayment -> {
			Transaction transaction = new Transaction();
			transaction.setTransId(creditPayment.getPaymentId());
			transaction.setAmount(creditPayment.getAmount());
			transaction.setCreditDebitCode("DEBIT");
			transaction.setReceiverFirstName(creditPayment.getReceiverFirstName());
			transaction.setReceiverLastName(creditPayment.getReceiverLastName());
			transaction.setReceiverEmail(creditPayment.getReceiverEmail());
			transaction.setReceiverCellPhone(creditPayment.getReceiverCellPhone());
			transaction.setStatus(creditPayment.getStatus());
			transactions.getTransactions().add(transaction);
		});

		LOGGER.info("Retrieved transactions: " + transactions.getTransactions());

//		transactions.getTransactions().add(makeDummyTransaction());
//		Transaction dummyTransaction2 = makeDummyTransaction();
//		dummyTransaction2.setTransId("123457");
//		dummyTransaction2.setSenderFirstName("John");
//		dummyTransaction2.setSenderLastName("Smith");
//		dummyTransaction2.setSenderEmail("john.smith@email.com");
//		dummyTransaction2.setReceiverFirstName("Amy");
//		dummyTransaction2.setReceiverLastName("Lopez");
//		dummyTransaction2.setReceiverEmail("alopez@company.com");
//		dummyTransaction2.setAmount(new BigDecimal("100.25"));
//		dummyTransaction2.setStatus("PENDING");
//		dummyTransaction2.setCreditDebitCode("DEBIT");
//		dummyTransaction2.setAccountNumber("12000194212199001");
//		transactions.getTransactions().add(dummyTransaction2);

		routingContext.response().putHeader(CONTENT_TYPE, "application/json; charset=utf-8")
				.putHeader("Access-Control-Allow-Origin", "*").end(Json.encodePrettily(transactions));
	}

	private Transaction makeDummyTransaction() {
		Transaction transaction = new Transaction();
		transaction.setTransId("123456");
		transaction.setAmount(new BigDecimal("20.00"));
		transaction.setSenderFirstName("Maria");
		transaction.setSenderLastName("Park");
		transaction.setSenderEmail("maria.park@email.com");
		transaction.setReceiverFirstName("John");
		transaction.setReceiverLastName("Smith");
		transaction.setReceiverEmail("john.smith@email.com");
		transaction.setStatus("COMPLETED");
		transaction.setCreditDebitCode("CREDIT");
		transaction.setAccountNumber("12000194212199001");

		return transaction;
	}

}
