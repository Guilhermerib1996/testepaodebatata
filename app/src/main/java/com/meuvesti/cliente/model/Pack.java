package com.meuvesti.cliente.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hersonrodrigues on 07/02/17.
 */
public class Pack {

    @SerializedName("pack_open")
    private boolean packOpen;
    @SerializedName("pack_color")
    private boolean packColor;
    @SerializedName("pack_sizes")
    private List<String> packSizes;
    private List<PackItem> itens;

    public Pack() {
    }

    public boolean isPackOpen() {
        return packOpen;
    }

    public void setPackOpen(boolean packOpen) {
        this.packOpen = packOpen;
    }

    public boolean isPackColor() {
        return packColor;
    }

    public void setPackColor(boolean packColor) {
        this.packColor = packColor;
    }

    public List<PackItem> getItens() {
        return itens;
    }

    public void setItens(List<PackItem> itens) {
        this.itens = itens;
    }

    public List<String> getPackSizes() {
        return packSizes;
    }

    public void setPackSizes(List<String> packSizes) {
        this.packSizes = packSizes;
    }
}
