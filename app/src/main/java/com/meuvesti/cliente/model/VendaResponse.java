package com.meuvesti.cliente.model;

/**
 * Created by hersonrodrigues on 18/02/17.
 */
public class VendaResponse {

    private Quote quote;

    private Result result;

    public VendaResponse() {}

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Quote getQuote() {
        return quote;
    }

    public void setQuote(Quote quote) {
        this.quote = quote;
    }
}
