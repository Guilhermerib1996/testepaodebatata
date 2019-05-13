package com.meuvesti.cliente.model;

import com.google.gson.annotations.SerializedName;

public class SizeItemRequest {

    @SerializedName("value")
    private int quantity;

    @SerializedName("key")
    private String key;


    public SizeItemRequest(){

    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
