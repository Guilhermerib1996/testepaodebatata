package com.meuvesti.cliente.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrcamentoAceito {

    @SerializedName("data")
    @Expose
    private List<String> data;

    public OrcamentoAceito() {

    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
