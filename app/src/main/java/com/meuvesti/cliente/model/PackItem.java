package com.meuvesti.cliente.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Created by hersonrodrigues on 07/02/17.
 */
public class PackItem {

    @SerializedName("color")
    private Color color;

    @SerializedName("sizes")
    @Expose
    private List<SizeItem> sizes;

    public PackItem(){}

    public Color getColor() {
        return color;
    }

    public void setColorId(Color color) {
        this.color = color;
    }

    public List<SizeItem> getSizes() {
        return sizes;
    }

    public void setSizes(List<SizeItem> sizes) {
        this.sizes = sizes;
    }
}
