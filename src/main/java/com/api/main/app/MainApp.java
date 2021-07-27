package com.api.main.app;

import com.api.service.ApiHandlerMain;
import com.api.utils.CommonConstants;
import com.api.utils.GenericCodec;
import com.api.wrapper.CommonObjWrapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import com.api.service.SBRequestHandler;
import com.api.service.MySqlConnection;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public class MainApp extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(MainApp.class);

    public static void main(String[] args) {
        LOG.info("Api call started ");

        Vertx vertx = Vertx.vertx();
        Router router = Router.router(vertx);

        // registering verticle
        vertx.deployVerticle(new ApiHandlerMain());
        vertx.deployVerticle(new MySqlConnection());
        vertx.deployVerticle(new SBRequestHandler());

        CommonObjWrapper commonObjWrapper = new CommonObjWrapper();
        commonObjWrapper.setRouter(router);
        commonObjWrapper.setVertx(vertx);

        vertx.eventBus().registerDefaultCodec(CommonObjWrapper.class, new GenericCodec <>(CommonObjWrapper.class));
        vertx.eventBus().send(CommonConstants.COMMON_ADDRESS, commonObjWrapper);

        HttpServer httpServer = vertx.createHttpServer();
        httpServer.requestHandler(router::handle).listen(8091);

    }

}
