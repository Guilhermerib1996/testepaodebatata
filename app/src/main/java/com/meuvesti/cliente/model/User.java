package com.meuvesti.cliente.model;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by c1277295 on 16/02/17.
 */

public class User {

    public Usuario.Company company;
    private String id;
    private String email;
    private String name;
    private String lastname;
    private String phone;
    private String password;
    @SerializedName("password_confirmation")
    private String passwordConfirmation;
    @SerializedName("tax_document")
    private String document;
    @SerializedName("social_name")
    private String socialName;
    @SerializedName("company_name")
    private String companyName;
    @SerializedName("seller_id")
    private String sellerId;
    @SerializedName("scheme_url")
    private String schemeUrl;
    @SerializedName("company_branches")
    @Expose
    private List<Usuario.ListCompanies> companies;
    private boolean logado;

    public List<Usuario.ListCompanies> getCompanies() {
        return companies;
    }

    public void setCompanies(List<Usuario.ListCompanies> companies) {
        this.companies = companies;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setLogado(boolean logado) {
        logado = logado;
    }

    public Usuario.Company getCompany() {
        return company;
    }

    public void setCompany(Usuario.Company company) {
        this.company = company;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getSocialName() {
        return socialName;
    }

    public void setSocialName(String socialName) {
        this.socialName = socialName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public void setSchemeUrl(String schemeUrl) {
        this.schemeUrl = schemeUrl;
    }
}
