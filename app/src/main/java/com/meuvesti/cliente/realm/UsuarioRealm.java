package com.meuvesti.cliente.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by hersonrodrigues on 24/01/17.
 */

public class UsuarioRealm extends RealmObject{

    @PrimaryKey
    public String id;
    public String email;
    public boolean logado = false;
    public String nome;
    public String lastName;
    public String token;
    public String phone;
    public String document;
    public String companyScheme;

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean getLogado(){
        return logado;
    }

    public void setLogado(boolean logado) {
        this.logado = logado;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getCompanyScheme(){
        return companyScheme;
    }

    public void setCompanyScheme(String customerScheme){
        this.companyScheme = customerScheme;
    }

}
