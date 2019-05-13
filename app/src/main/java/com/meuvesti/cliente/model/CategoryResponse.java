package com.meuvesti.cliente.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Herson Rodrigues <hersonrodrigues@gmail.com> on 11/02/2018.
 */

public class CategoryResponse {
    @SerializedName("response")
    @Expose
    private List<Category> response;
    @SerializedName("result")
    @Expose
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
