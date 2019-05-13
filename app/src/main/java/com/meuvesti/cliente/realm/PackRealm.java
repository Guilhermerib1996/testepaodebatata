package com.meuvesti.cliente.realm;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by hersonrodrigues on 08/02/17.
 */

public class PackRealm extends RealmObject {

    @SerializedName("pack_sizes")
    private RealmList<RealmString> packSizes;
    private RealmList<PackItemRealm> itens;


    public PackRealm() {
    }

    public RealmList<RealmString> getPackSizes() {
        return packSizes;
    }

    public void setPackSizes(RealmList<RealmString> packSizes) {
        this.packSizes = packSizes;
    }

    public RealmList<PackItemRealm> getItens() {
        return itens;
    }

    public void setItens(RealmList<PackItemRealm> itens) {
        this.itens = itens;
    }

}
