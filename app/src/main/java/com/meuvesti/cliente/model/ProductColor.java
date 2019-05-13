package com.meuvesti.cliente.model;

/**
 * Created by hersonrodrigues on 13/02/17.
 */
public class ProductColor {
    private String id;
    private String name;
    private String code;
    private String stamp;
    private String photo;

    public ProductColor(){}

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEstampa() {
        return stamp;
    }

    public void setEstampa(String estampa) {
        this.stamp = estampa;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
