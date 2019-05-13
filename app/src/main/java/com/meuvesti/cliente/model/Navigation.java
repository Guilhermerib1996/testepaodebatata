package com.meuvesti.cliente.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hersonrodrigues on 26/01/17.
 */
public class Navigation {

    private int total;
    private int per_page;
    private int current_page;
    private int last_page;
    @SerializedName("next_page_url")
    private String nextPageURL;
    @SerializedName("prev_page_url")
    private String prevPageURL;
    private int next_page;
    private int form;
    private int to;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPer_page() {
        return per_page;
    }

    public void setPer_page(int per_page) {
        this.per_page = per_page;
    }

    public int getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(int current_page) {
        this.current_page = current_page;
    }

    public int getLast_page() {
        return last_page;
    }

    public void setLast_page(int last_page) {
        this.last_page = last_page;
    }

    public String getNextPageURL() {
        return nextPageURL;
    }

    public void setNextPageURL(String nextPageURL) {
        this.nextPageURL = nextPageURL;
    }

    public String getPrevPageURL() {
        return prevPageURL;
    }

    public void setPrevPageURL(String prevPageURL) {
        this.prevPageURL = prevPageURL;
    }

    public int getNext_page() {
        return next_page;
    }

    public void setNext_page(int next_page) {
        this.next_page = next_page;
    }

    public int getForm() {
        return form;
    }

    public void setForm(int form) {
        this.form = form;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }
}
