package com.api.wrapper;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

public class CommonObjWrapper {

    public static final String ADDRESS = "common";

    private Vertx vertx;

    private Router router;

    public Router getRouter() {
        return router;
    }

    public void setRouter(Router router) {
        this.router = router;
    }

    public Vertx getVertx() {
        return vertx;
    }

    public void setVertx(Vertx vertx) {
        this.vertx = vertx;
    }

}
