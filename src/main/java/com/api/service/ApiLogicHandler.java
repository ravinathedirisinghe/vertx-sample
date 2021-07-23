package com.api.service;

import java.io.IOException;

import com.api.wrapper.ApiDto;
import com.api.wrapper.SuccessResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;

public class ApiLogicHandler extends AbstractVerticle {

    public static final String ADDRESS = "api-handler";

    private static final Logger LOG = LoggerFactory.getLogger(ApiLogicHandler.class);

    @Override
    public void start() {

        LOG.info("Starting Mysql class");

        vertx.eventBus().consumer(ApiDto.ADDRESS, message -> {

            ObjectMapper mapper = new ObjectMapper();
            ApiDto apiDto = null;

            try {
                apiDto = ( ApiDto ) message.body();
            }
            catch ( Exception e ) {
                e.printStackTrace();
            }

            try {

                WebClient client = WebClient.create(vertx);
                String jsonStr = mapper.writeValueAsString(apiDto.getPaymentRequest());
                Buffer buff = Buffer.buffer(jsonStr);
                handleApiCalls(apiDto.getRoutingContext(), mapper, client, buff);

            }
            catch ( Exception e ) {
                e.printStackTrace();
            }

        });


    }

    private static void handleApiCalls(io.vertx.ext.web.RoutingContext routingContext, ObjectMapper mapper, WebClient client, Buffer buff) {
        client
                .post(80, "run.mocky.io", "/v3/b5121fa8-24a5-4984-8230-ded91afb99c5")
                .sendBuffer(buff)
                .onSuccess(res -> {

                    try {
                        SuccessResponse successResponse = null;
                        successResponse = mapper.readValue(res.bodyAsString(), SuccessResponse.class);

                        String jsonStrSuccessResponse = mapper.writeValueAsString(successResponse);

                        HttpServerResponse response = routingContext.response();
                        response.setChunked(true);
                        response.write(jsonStrSuccessResponse);
                        response.end();

                    }
                    catch ( IOException e ) {
                        e.printStackTrace();
               LOG.error("Backend  response Error : {}, mapping exception", e);

                    }
                }).onFailure(res -> {
        });
    }


    private static void getCall(Router router) {
        router
                .get("/hello/:name")
                .handler(routingContext -> {
                    String name = routingContext.request().getParam("name");
                    HttpServerResponse response = routingContext.response();
                    response.setChunked(true);
                    response.write("Hi " + name + "\n");
                    response.end();
                });
    }

}
