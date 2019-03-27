package rtp.demo.creditor.payments.service;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import rtp.demo.creditor.domain.payments.Payment;
import rtp.demo.creditor.domain.transactions.Transaction;
import rtp.demo.creditor.domain.transactions.Transactions;
import rtp.demo.repository.CreditPaymentRepository;
import rtp.demo.creditor.payments.service.TransactionsRequest;

import rtp.demo.repository.MySqlCreditPaymentRepository;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

import java.util.List;
import java.util.logging.Logger;

public class CreditorPaymentService extends AbstractVerticle {

	private static final Logger LOGGER = Logger.getLogger(CreditorPaymentService.class.getName());

	private CreditPaymentRepository creditPaymentRepository = new MySqlCreditPaymentRepository();

	@Override
	public void start() {
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());

		router.post("/transactions-service/queries/transactions").handler(this::getTransactionsByAccount);

		router.get("/*").handler(StaticHandler.create());

		vertx.createHttpServer().requestHandler(router::accept).listen(8080);
		LOGGER.info("THE HTTP APPLICATION HAS STARTED");

	}

	private void getTransactionsByAccount(RoutingContext routingContext) {
		TransactionsRequest transactionsRequest = routingContext.getBodyAsJson().mapTo(TransactionsRequest.class);
		LOGGER.info("Retrieving transactions for account: " + transactionsRequest.getAccountNumber());
		Transactions transactions = new Transactions();

		// call lookups
		List<Payment> creditPayments = creditPaymentRepository.getPayments(transactionsRequest.getAccountNumber());

		creditPayments.forEach(creditPayment -> {
			Transaction transaction = new Transaction();
			transaction.setTransId(creditPayment.getCreditTransferMessageId());
			transaction.setAmount(creditPayment.getPaymentAmount());
			transaction.setCreditDebitCode("CREDIT");
			transaction.setSenderFirstName(creditPayment.getDebtorAccountNumber());
			transaction.setSenderLastName("");
			transaction.setStatus(creditPayment.getStatus());
			transactions.getTransactions().add(transaction);
		});

		LOGGER.info("Retrieved transactions: " + transactions.getTransactions());

		routingContext.response().putHeader(CONTENT_TYPE, "application/json; charset=utf-8")
				.putHeader("Access-Control-Allow-Origin", "*").end(Json.encodePrettily(transactions));
	}
}
