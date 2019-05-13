package com.meuvesti.cliente.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hersonrodrigues on 18/02/17.
 */

public class Quote {

    private String id;
    private int code;
    private String date;
    @SerializedName("total_price")
    private double totalPrice;

    public Quote(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
