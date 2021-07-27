package com.api.wrapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties( ignoreUnknown = true )
public class PaymentRequest {

    private String endUserId;

    private ChargingInformation chargingInformation;

    public String getEndUserId() {
        return endUserId;
    }

    public void setEndUserId(String endUserId) {
        this.endUserId = endUserId;
    }

    public ChargingInformation getChargingInformation() {
        return chargingInformation;
    }

    public void setChargingInformation(ChargingInformation chargingInformation) {
        this.chargingInformation = chargingInformation;
    }

    @Override
    public String toString() {
        return "PaymentRequest{" +
                "endUserId='" + endUserId + '\'' +
                ", chargingInformation=" + chargingInformation +
                '}';
    }

}
