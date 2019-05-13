package com.meuvesti.cliente.model;

import java.io.Serializable;

/**
 * Created by hersonrodrigues on 15/02/17.
 */
public class ConfigurationRequest implements Serializable {

    private ConfigurationItem domain_config;
    private Result result;

    public ConfigurationItem getConfiguration() {
        return domain_config;
    }

    public void setConfiguration(ConfigurationItem configuration) {
        this.domain_config = configuration;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}

