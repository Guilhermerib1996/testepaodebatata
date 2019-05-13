package com.meuvesti.cliente.model;

import com.meuvesti.cliente.service.VestiAPI;

/**
 * Created by hersonrodrigues on 24/01/17.
 */

public class Result {
    private boolean success;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

}
