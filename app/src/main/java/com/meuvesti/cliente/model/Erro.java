package com.meuvesti.cliente.model;

import com.google.gson.annotations.Expose;

import java.util.Map;

/**
 * Created by hersonrodrigues on 19/02/17.
 */
public class Erro {

    @Expose
    private transient Map<String, Status> status;

    public Erro(){}

    public Map<String, Status> getStatus() {
        return status;
    }

    public void setStatus(Map<String, Status> status) {
        this.status = status;
    }
}
