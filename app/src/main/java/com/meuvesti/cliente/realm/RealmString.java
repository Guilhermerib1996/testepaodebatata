package com.meuvesti.cliente.realm;

import com.google.gson.annotations.Expose;

import io.realm.RealmObject;

/**
 * Created by hersonrodrigues on 11/02/17.
 */
public class RealmString extends RealmObject {

    @Expose
    private String val;

    public String getValue() {
        return val;
    }

    public void setValue(String value) {
        this.val = value;
    }
}