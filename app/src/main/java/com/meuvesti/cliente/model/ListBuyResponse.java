package com.meuvesti.cliente.model;

/**
 * Created by hersonrodrigues on 24/01/17.
 */
public class ListBuyResponse{

    private ListBuyItem response;
    private Result result;

    public ListBuyItem getProducts() {
        return response;
    }

    public void setProducts(ListBuyItem products) {
        this.response = products;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
