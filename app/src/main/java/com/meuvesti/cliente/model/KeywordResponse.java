package com.meuvesti.cliente.model;

import java.util.List;

public class KeywordResponse {

    private List<Category> response;
    private Result result;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public List<Category> getResponse() {
        return response;
    }

    public void setResponse(List<Category> response) {
        this.response = response;
    }
}
