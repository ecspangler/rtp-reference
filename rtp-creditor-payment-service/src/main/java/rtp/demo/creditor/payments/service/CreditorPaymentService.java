package rtp.demo.creditor.payments.service;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreditorPaymentService extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreditorPaymentService.class.getName());

    private WebClient client;
    private Router router;

    @Override
    public void start() {
        router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        connect(HttpMethod.POST, "/payments-service/payments");
        connect(HttpMethod.GET, "/transactions-service/transactions");
        connect(HttpMethod.POST, "/transactions-service/queries/transactions");

        router.get("/*").handler(StaticHandler.create());

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
        LOGGER.info("THE HTTP APPLICATION HAS STARTED");

        client = WebClient.create(vertx);

    }

    private void connect(HttpMethod method, String path) {
        router.route(method, path)
                .handler(context -> {
                    LOGGER.info(method + " " + path);
                    LOGGER.info(context.getBodyAsString());
                    client.request(method, 8080, "rtp-debtor-payment-service", path)
                            .putHeader("content-type", "application/json; charset=utf-8")
                            .sendBuffer(context.getBody(), ar -> {
                        LOGGER.info(" +++ Returned: " + ar.result().statusCode() + " " + ar.result().statusMessage());
                        LOGGER.info(" +++ " + ar.result().bodyAsString());
                        context.response().setStatusCode(ar.result().statusCode());
                        context.response().setStatusMessage(ar.result().statusMessage());
                        context.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8");
                        context.response().end(ar.result().body());
                    });
                });
    }
}
