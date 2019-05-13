package com.meuvesti.cliente.realm;

import io.realm.RealmObject;

/**
 * Created by hersonrodrigues on 08/02/17.
 */

public class PhotoRealm extends RealmObject {
    private String url;

    public PhotoRealm(){

    }

    public PhotoRealm(String url){
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
