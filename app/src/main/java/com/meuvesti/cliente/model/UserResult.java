package com.meuvesti.cliente.model;

/**
 * Created by hersonrodrigues on 17/02/17.
 */

public class UserResult {

    private User user;
    private String token;
    private Result result;

    public UserResult(){}

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
