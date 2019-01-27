package rtp.demo.debtor.payments.service;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import rtp.demo.debtor.domain.account.Account;
import rtp.demo.debtor.domain.model.payment.Payments;
import rtp.demo.debtor.domain.model.transaction.DebitTransaction;
import rtp.demo.debtor.domain.model.transaction.Transactions;
import rtp.demo.debtor.payments.producer.DebtorPaymentsProducer;
import rtp.demo.debtor.repository.account.AccountRepository;
import rtp.demo.debtor.repository.account.JdgAccountRepository;
import rtp.demo.payments.beans.PayeeAccountLookupBean;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

public class DebtorPaymentService extends AbstractVerticle {

	private static final Logger LOGGER = Logger.getLogger(DebtorPaymentService.class.getName());

	private AccountRepository accountRepository = new JdgAccountRepository();

	@Override
	public void start() {
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());

		router.post("/payments-service/payments").handler(this::createPayments);
		router.get("/transactions-service/transactions").handler(this::getTransactions);

		router.get("/*").handler(StaticHandler.create());

		vertx.createHttpServer().requestHandler(router::accept).listen(8080);
		LOGGER.info("THE HTTP APPLICATION HAS STARTED");

		// Populate test payees in the cache, for purposes of the reference example
		Account testAccount1 = new Account();
		testAccount1.setRoutingNumber("020010001");
		testAccount1.setAccountNumber("12000194212199001");
		accountRepository.addAccount("JANE_SHAW", testAccount1);

		Account testAccount2 = new Account();
		testAccount1.setRoutingNumber("020010001");
		testAccount1.setAccountNumber("12000194212199002");
		accountRepository.addAccount("DIEGO_LOPEZ", testAccount2);
	}

	private void createPayments(RoutingContext routingContext) {
		Payments payments = routingContext.getBodyAsJson().mapTo(Payments.class);
		LOGGER.info("Creating payments: " + payments);

		DebtorPaymentsProducer debtorPaymentsProducer = new DebtorPaymentsProducer();
		payments.getPayments().forEach(payment -> {

			// Find the routing number and account number for the payee
			payment = PayeeAccountLookupBean.enrichPayeeAccountInformation(payment);

			try {
				// Pyament key generated based on timestamp for the reference example
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
				LocalDateTime now = LocalDateTime.now();
				payment.setPaymentId(formatter.format(now));

				debtorPaymentsProducer.sendMessage(payment.getPaymentId(), payment);
			} catch (Exception e) {
				LOGGER.severe("Error publishing payment to topic");
				LOGGER.severe(e.getMessage());
				e.printStackTrace();
			}
		});

		HttpServerResponse response = routingContext.response();
		response.putHeader(CONTENT_TYPE, "application/json; charset=utf-8");

		response.end(Json.encode(payments));
	}

	private void getTransactions(RoutingContext routingContext) {
		LOGGER.info("Retrieving transactions");
		Transactions transactions = new Transactions();

		// call lookup

		transactions.getTransactions().add(makeDummyTransaction());
		DebitTransaction dummyTransaction2 = makeDummyTransaction();
		dummyTransaction2.setReceiverFirstName("Amy");
		dummyTransaction2.setReceiverLastName("Lopez");
		dummyTransaction2.setAmount(new BigDecimal("100.25"));
		dummyTransaction2.setStatus("COMPLETED");
		transactions.getTransactions().add(dummyTransaction2);

		routingContext.response().putHeader(CONTENT_TYPE, "application/json; charset=utf-8")
				.end(Json.encodePrettily(transactions));
	}

	private DebitTransaction makeDummyTransaction() {
		DebitTransaction transaction = new DebitTransaction();
		transaction.setTransId("123456");
		transaction.setAmount(new BigDecimal("20.00"));
		transaction.setReceiverFirstName("John");
		transaction.setReceiverLastName("Smith");
		transaction.setStatus("PENDING");

		return transaction;
	}

}