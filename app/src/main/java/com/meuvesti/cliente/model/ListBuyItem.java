package com.meuvesti.cliente.model;

import java.util.List;

/**
 * Created by hersonrodrigues on 25/01/17.
 */

public class ListBuyItem extends Navigation {

    private List<BuyItemData> data;

    public ListBuyItem() {}

    public List<BuyItemData> getData() {
        return data;
    }

    public void setData(List<BuyItemData> data) {
        this.data = data;
    }
}
