package com.meuvesti.cliente.realm;

import io.realm.RealmObject;

/**
 * Created by hersonrodrigues on 08/02/17.
 */
public class SizeItemRealm extends RealmObject {

    private int qty;
    private boolean sell;
    private String key;

    public SizeItemRealm(){}

    public int getQuantity() {
        return qty;
    }

    public void setQuantity(int quantity) {
        this.qty = quantity;
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
