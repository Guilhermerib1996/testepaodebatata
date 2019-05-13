package com.meuvesti.cliente.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ItenRequest {

    @SerializedName("color")
    private Color color;

    @SerializedName("sizes")
    private List<SizeItemRequest> sizes;

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public List<SizeItemRequest> getSizes() {
        return sizes;
    }

    public void setSizes(List<SizeItemRequest> sizes) {
        this.sizes = sizes;
    }
}
