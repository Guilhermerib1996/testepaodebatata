package com.meuvesti.cliente.model;

import com.google.gson.annotations.SerializedName;

public class Color {

    @SerializedName("id")
    private String id;

    public Color() {
    }

    public Color(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
