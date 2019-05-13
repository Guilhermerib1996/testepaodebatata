package com.meuvesti.cliente.realm;

import io.realm.RealmObject;

/**
 * Created by hersonrodrigues on 08/02/17.
 */

public class PackMapRealm extends RealmObject {

    private String key;
    private SizeItemRealm value;

    public PackMapRealm(){

    }

    public SizeItemRealm getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(SizeItemRealm value) {
        this.value = value;
    }
}
