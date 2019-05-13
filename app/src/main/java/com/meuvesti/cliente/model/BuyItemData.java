package com.meuvesti.cliente.model;

/**
 * Created by hersonrodrigues on 25/01/17.
 */
public class BuyItemData {
    private String id;
    private String name;
    private String status;

    public BuyItemData() {

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
