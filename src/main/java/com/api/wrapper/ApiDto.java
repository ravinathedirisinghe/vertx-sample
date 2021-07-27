package com.api.wrapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.vertx.ext.web.RoutingContext;

@JsonIgnoreProperties( ignoreUnknown = true )
public class ApiDto {

    private PaymentRequest paymentRequest;

    private RoutingContext routingContext;

    public PaymentRequest getPaymentRequest() {
        return paymentRequest;
    }

    public void setPaymentRequest(PaymentRequest paymentRequest) {
        this.paymentRequest = paymentRequest;
    }

    public RoutingContext getRoutingContext() {
        return routingContext;
    }

    public void setRoutingContext(RoutingContext routingContext) {
        this.routingContext = routingContext;
    }

    @Override
    public String toString() {
        return "ApiPojo{" +
                "paymentRequest=" + paymentRequest +
                ", routingContext=" + routingContext +
                '}';
    }

}
