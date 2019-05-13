package com.meuvesti.cliente.realm;

import java.util.Random;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by hersonrodrigues on 08/02/17.
 */

public class PackItemRealm extends RealmObject {

    private ColorRealm color;
    //private String name;
    //private String code;
    //private String estampa;

    // Propriedade existente somente aqui, nao no servidor
    private int quantityPacks;

    private RealmList<SizeItemRealm> sizes;
    private int randomId;

    public PackItemRealm(){
        randomId = getRand();
    }

    private int getRand(){
        Random r = new Random();
        return (r.nextInt(Integer.MAX_VALUE) + 1);

    }
    public int getQuantityPacks() {
        return quantityPacks;
    }

    public void setQuantityPacks(int quantityPacks) {
        this.quantityPacks = quantityPacks;
    }

    public ColorRealm getColor() {
        return color;
    }

    public void setColor(ColorRealm color) {
        this.color = color;
    }

    public RealmList<SizeItemRealm> getSizes() {
        return sizes;
    }

    public void setSizes(RealmList<SizeItemRealm> sizes) {
        this.sizes = sizes;
    }

    public int getRandomId() {
        return randomId;
    }

    public void setRandomId(int randomId) {
        this.randomId = randomId;
    }
}
