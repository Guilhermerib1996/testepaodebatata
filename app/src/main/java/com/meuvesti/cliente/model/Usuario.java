package com.meuvesti.cliente.model;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import com.meuvesti.cliente.realm.UsuarioRealm;
import com.google.gson.annotations.Expose;


import java.util.List;
import java.util.Map;

/**
 * Created by hersonrodrigues on 24/01/17.
 */

public class Usuario{

    private Result result;
    private User user;
    private String token;
    private boolean logado;


    private String email;

    public Usuario() {
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public void setLogado(boolean logado) {
        this.logado = logado;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public UsuarioRealm getRealm() {
        UsuarioRealm ur = new UsuarioRealm();
        ur.setLogado(false);
        ur.setNome(this.getUser().getName());
        ur.setToken(this.getToken());
        ur.setId(getUser().getId());
        ur.setEmail(getUser().getEmail());
        ur.setPhone(getUser().getPhone());
        ur.setLastName(getUser().getLastname());
        ur.setDocument(getUser().getCompany().getDocument());
        //Log.e("xumbrelu", user.getCompanies().get(0).getCompanyScheme());
        ur.setCompanyScheme(getUser().getCompanies().get(0).getCompanyScheme());

        return ur;

    }

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }

    public class Company {
        @SerializedName("tax_document")
        private String document;

        public String getDocument() {
            return document;
        }

        public void setDocument(String document) {
            this.document = document;
        }

    }

    public class ListCompanies{
        @SerializedName("scheme_url")
        private String companyScheme;

        public String getCompanyScheme(){
            return companyScheme;
        }

        public void setCompanyScheme(String companyScheme){
            this.companyScheme = companyScheme;
        }
    }
}
