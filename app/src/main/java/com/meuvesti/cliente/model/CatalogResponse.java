package com.meuvesti.cliente.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hersonrodrigues on 25/01/17.
 */

public class CatalogResponse {

    private Product response;
    private Result result;
    @SerializedName("seller_id")
    @Expose
    private String sellerId;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Product getProducts() {
        return response;
    }

    public void setProducts(Product products) {
        this.response = products;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public Product getResponse() {
        return response;
    }

    public void setResponse(Product response) {
        this.response = response;
    }
}
