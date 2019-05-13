package com.meuvesti.cliente.model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by FabianoVasconcelos on 11/01/17.
 */

public class Carrinho implements Serializable {
    private ItemProduct itemProduct;
    private HashMap<String, HashMap<String, Integer>> item;
    private Double valorUnitario;
    private int totalItens;
    private Double valorTotal;

    public ItemProduct getItemProduct() {
        return itemProduct;
    }

    public void setItemProduct(ItemProduct itemProduct) {
        this.itemProduct = itemProduct;
    }

    public HashMap<String, HashMap<String, Integer>> getItem() {
        return item;
    }

    public void setItem(HashMap<String, HashMap<String, Integer>> item) {
        this.item = item;
    }

    public Double getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(Double valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public int getTotalItens() {
        return totalItens;
    }

    public void setTotalItens(int totalItens) {
        this.totalItens = totalItens;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }
}
