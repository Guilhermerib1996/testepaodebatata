package com.meuvesti.cliente.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by FabianoVasconcelos on 28/12/16.
 */
public class FilterRealm extends RealmObject {

    @PrimaryKey
    public String id;
    public String nome;
    public boolean selecionado = false;

    public FilterRealm() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isSelecionado() {
        return selecionado;
    }

    public void setSelecionado(boolean selecionado) {
        this.selecionado = selecionado;
    }
}
