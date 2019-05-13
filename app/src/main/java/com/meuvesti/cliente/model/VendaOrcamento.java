package com.meuvesti.cliente.model;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by hersonrodrigues on 18/02/17.
 */
public class VendaOrcamento {

    @SerializedName("seller_id")
    private String sellerId;

    @SerializedName("obs")
    private String obs;

    @SerializedName("quotes_details")
    private List<QuotesDetail> quotesDetails = null;

    @SerializedName("scheme_url")
    private String schemeUrl;

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public List<QuotesDetail> getQuotesDetails() {
        return quotesDetails;
    }

    public void setQuotesDetails(List<QuotesDetail> quotesDetails) {
        this.quotesDetails = quotesDetails;
    }

    public String getSchemeUrl() {
        return schemeUrl;
    }

    public void setSchemeUrl(String schemeUrl) {
        this.schemeUrl = schemeUrl;
    }
}