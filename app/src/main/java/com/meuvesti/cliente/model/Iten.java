package com.meuvesti.cliente.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class Iten {

    @SerializedName("color")
    private Color color;

    @SerializedName("sizes")
    private List<SizeItem> sizes;

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public List<SizeItem> getSizes() {
        return sizes;
    }

    public void setSizes(List<SizeItem> sizes) {
        this.sizes = sizes;
    }
}

