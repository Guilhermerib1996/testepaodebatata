package com.meuvesti.cliente.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hersonrodrigues on 07/02/17.
 */
public class SizeItem {

    @SerializedName("qty")
    private int quantity;

    @SerializedName("sell")
    private boolean sell;

    @SerializedName("key")
    private String key;


    public SizeItem(){

    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isSell() {
        return sell;
    }

    public void setSell(boolean sell) {
        this.sell = sell;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}

