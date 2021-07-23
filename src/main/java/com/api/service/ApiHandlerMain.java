package com.api.service;

import java.io.IOException;

import javax.validation.ValidationException;

import com.api.utils.GenericCodec;
import com.api.wrapper.ApiDto;
import com.api.wrapper.CommonObjWrapper;
import com.api.wrapper.PaymentRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public class ApiHandlerMain extends AbstractVerticle {

    public static final String ADDRESS = "ApiHandler-Service";

    private static final Logger LOG = LoggerFactory.getLogger(ApiHandlerMain.class);
    //https://vertx.io/docs/vertx-web-validation/java/
    //https://vertx.io/docs/vertx-web-api-contract/java/

    @Override
    public void start() {

        LOG.info("Starting Mysql class");

        vertx.eventBus().consumer(CommonObjWrapper.ADDRESS, message -> {

            CommonObjWrapper commonObjWrapper = null;

            try {
                commonObjWrapper = ( CommonObjWrapper ) message.body();
            }
            catch ( Exception e ) {
                e.printStackTrace();
            }

            commonObjWrapper.getRouter().post("/hello").handler(routingContext -> {
                routingContext.request().bodyHandler(body -> {

                    PaymentRequest paymentRequest = null;

                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        paymentRequest = mapper.readValue(body.toString(), PaymentRequest.class);

                        if ( paymentRequest == null ) {
                            routingContext.fail(400);
                            return;
                        }


                        try {
                            Integer.parseInt(paymentRequest.getChargingInformation().getAmount());
                        }
                        catch ( NumberFormatException e ) {
                            routingContext.fail(400, e);
                            return;
                        }

                        ApiDto apiDto = new ApiDto();
                        apiDto.setPaymentRequest(paymentRequest);
                        apiDto.setRoutingContext(routingContext);

                        vertx.eventBus().registerDefaultCodec(PaymentRequest.class,
                                new GenericCodec < PaymentRequest >(PaymentRequest.class));
                        vertx.eventBus().send("payment-request", paymentRequest);


                        vertx.eventBus().registerDefaultCodec(ApiDto.class,
                                new GenericCodec < ApiDto >(ApiDto.class));
                        vertx.eventBus().send("api-dto", apiDto);

                    }
                    catch ( IOException e ) {
                        e.printStackTrace();
                        LOG.error("Error in message conversion " + e);

                    }

                });

            })
                    .failureHandler((routingContext) -> {
                        Throwable failure = routingContext.failure();
                        if ( failure instanceof ValidationException ) {
                            // Something went wrong during validation!
                            String validationErrorMessage = failure.getMessage();
                        }
                    });

// Manage the validation failure for all routes in the router
            commonObjWrapper.getRouter().errorHandler(400, routingContext -> {
                if ( routingContext.failure() instanceof ValidationException ) {
                    // Something went wrong during validation!
                    String validationErrorMessage = routingContext.failure().getMessage();
                } else {
                    // Unknown 400 failure happened
                    routingContext.response().setStatusCode(400).end();
                }
            });

        });
    }

}



