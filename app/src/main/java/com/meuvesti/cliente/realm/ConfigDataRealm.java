package com.meuvesti.cliente.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by hersonrodrigues on 29/01/17.
 */

public class ConfigDataRealm extends RealmObject {

    @PrimaryKey
    public int id = 1;
    public String whatsAppItems;
    public boolean hasFilter = false;
    private double test;

    public ConfigDataRealm() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setHasFilter(boolean hasFilter) {
        this.hasFilter = hasFilter;
    }

    public String getWhatsAppIds() {
        return whatsAppItems;
    }

    public void setWhatsAppIds(String whatsAppItems) {
        this.whatsAppItems = whatsAppItems;
    }

    public boolean isHasFilter() {
        return hasFilter;
    }

    public void setTest(double test) {
        this.test = test;
    }

    public double getTest() {
        return test;
    }
}
