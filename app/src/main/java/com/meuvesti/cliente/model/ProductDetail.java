package com.meuvesti.cliente.model;

import java.io.Serializable;

/**
 * Created by hersonrodrigues on 07/02/17.
 */

public class ProductDetail implements Serializable{

    private ProductDetailItem response;
    private Result result;

    ProductDetail(){}

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public ProductDetailItem getProduct() {
        return response;
    }

    public void setProduct(ProductDetailItem product) {
        this.response = product;
    }
}
