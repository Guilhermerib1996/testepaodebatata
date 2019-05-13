package com.meuvesti.cliente.model;

import java.util.List;

/**
 * Created by hersonrodrigues on 25/01/17.
 */
public class Product extends Navigation {

    public List<ItemProduct> data;
    private Result result;

    public Product() {}

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public List<ItemProduct> getData() {
        return data;
    }

    public void setData(List<ItemProduct> data) {
        this.data = data;
    }

    public class DataItem {

        private String id;
        private String name;
        private int status;

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

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }


}
