package com.meuvesti.cliente.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hersonrodrigues on 16/02/17.
 */
public class ConfigurationItem {

    @SerializedName("orcamento_aceito")
    @Expose
    private OrcamentoAceito orcamentoAceito;

    public ConfigurationItem() {

    }

    public OrcamentoAceito getOrcamentoAceito() {
        return orcamentoAceito;
    }

    public void setOrcamentoAceito(OrcamentoAceito orcamentoAceito) {
        this.orcamentoAceito = orcamentoAceito;
    }
}
