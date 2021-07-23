package com.api.main.app;

import com.api.service.ApiHandlerMain;
import com.api.utils.GenericCodec;
import com.api.wrapper.CommonObjWrapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import com.api.service.ApiLogicHandler;
import com.api.service.MySqlConnection;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public class MainApp extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(MainApp.class);

    public static void main(String[] args) {
        LOG.info("Api call started ");

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new ApiHandlerMain());
        vertx.deployVerticle(new MySqlConnection());
        vertx.deployVerticle(new ApiLogicHandler());

        HttpServer httpServer = vertx.createHttpServer();
        Router router = Router.router(vertx);

        CommonObjWrapper commonObjWrapper = new CommonObjWrapper();
        commonObjWrapper.setRouter(router);
        commonObjWrapper.setVertx(vertx);

        vertx.eventBus().registerDefaultCodec(CommonObjWrapper.class,
                new GenericCodec < CommonObjWrapper >(CommonObjWrapper.class));
        vertx.eventBus().send("common", commonObjWrapper);

        httpServer
                .requestHandler(router::accept)
                .listen(8091);


    }


}
