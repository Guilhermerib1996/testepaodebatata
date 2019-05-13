package com.meuvesti.cliente.model;

import com.google.gson.annotations.SerializedName;

import com.meuvesti.cliente.utils.Globals;

/**
 * Created by hersonrodrigues on 24/01/17.
 */

public class LoginRequest {
    private String email;
    private String password;

    @SerializedName("scheme_url")
    private String schemeUrl = Globals.SCHEME_NAME;


    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return password;
    }

    public void setSenha(String password) {
        this.password = password;
    }
}
