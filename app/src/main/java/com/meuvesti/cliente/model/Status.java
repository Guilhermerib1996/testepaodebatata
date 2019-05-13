package com.meuvesti.cliente.model;

import com.google.gson.annotations.Expose;

/**
 * Created by hersonrodrigues on 19/02/17.
 */

public class Status {

    @Expose
    private String status;

    public Status() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
