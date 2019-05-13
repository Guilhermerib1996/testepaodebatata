package com.meuvesti.cliente.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hersonrodrigues on 19/02/17.
 */

public class ValidateError {

    @SerializedName("erro")
    @Expose
    private transient Erro erro;

    private Result result;

    public ValidateError() {}

    public Erro getErro() {
        return erro;
    }

    public void setErro(Erro erro) {
        this.erro = erro;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
