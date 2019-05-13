package com.meuvesti.cliente.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QuotesDetailRequest {
    @SerializedName("product_id")
    private String productId;

    @SerializedName("itens")
    private List<ItenRequest> itens = null;

    @SerializedName("unit_price")
    private String unitPrice;

    @SerializedName("qtde")
    private Integer qtde;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public List<ItenRequest> getItens() {
        return itens;
    }

    public void setItens(List<ItenRequest> itens) {
        this.itens = itens;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getQtde() {
        return qtde;
    }

    public void setQtde(Integer qtde) {
        this.qtde = qtde;
    }
}
