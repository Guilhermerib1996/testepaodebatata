package com.meuvesti.cliente.model;


import com.google.gson.annotations.SerializedName;
import com.meuvesti.cliente.realm.ColorRealm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.realm.annotations.Ignore;

/**
 * Created by FabianoVasconcelos on 26/12/16.
 */
public class ItemProduct implements Serializable {

    public static final String ITEM_PRODUCT = "item_product";

    @SerializedName("id")
    private String id;

    @SerializedName("code")
    private String codigo;

    @SerializedName("name")
    private String nome;

    @SerializedName("price")
    private Double valor;

    @SerializedName("stockout")
    private boolean stockout;

    @SerializedName("images")
    private List<Image> images;

    @SerializedName("seller_id")
    private String sellerId;

    @SerializedName("domain_id")
    private String domainId;

    @SerializedName("brand_name")
    private String brandName;

    @SerializedName("company_name")
    private String companyName;

    @SerializedName("original_price")
    private String originalPrice;

    @SerializedName("ptype")
    private String ptype;

    @SerializedName("active")
    private boolean active;

    @SerializedName("colors")
    private List<ColorRealm> colors;

    // Atributo antigo mas funcional
    private List<Photo> imagem;

    @Ignore
    private List<Category> listCategory;


    public ItemProduct() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public boolean isStockout() {
        return stockout;
    }

    public void setStockout(boolean stockout) {
        this.stockout = stockout;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getPtype() {
        return ptype;
    }

    public void setPtype(String ptype) {
        this.ptype = ptype;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<ColorRealm> getColors() {
        return colors;
    }

    public void setColors(List<ColorRealm> colors) {
        this.colors = colors;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public List<Photo> getImagem() {
        return imagem;
    }

    public void setImagem(List<Photo> imagem) {
        this.imagem = imagem;
    }

    public boolean existImages() {
        return !getCorrectImages().isEmpty();
    }

    public List<String> getCorrectImages() {
        final List<String> listUrl = new ArrayList<>();
        if (getImages() != null && !getImages().isEmpty()) {
            for (Image image : getImages()) {
                String url = image.getUrlOrig();
                listUrl.add(url);
            }
        } else {
            if (getImagem() != null && !getImagem().isEmpty()) {
                for (Photo pr : getImagem()) {
                    String url = pr.getUrl();
                    listUrl.add(url);
                }
            }
        }
        return listUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemProduct that = (ItemProduct) o;

        if (stockout != that.stockout) return false;
        if (active != that.active) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (codigo != null ? !codigo.equals(that.codigo) : that.codigo != null) return false;
        if (nome != null ? !nome.equals(that.nome) : that.nome != null) return false;
        if (valor != null ? !valor.equals(that.valor) : that.valor != null) return false;
        if (images != null ? !images.equals(that.images) : that.images != null) return false;
        if (sellerId != null ? !sellerId.equals(that.sellerId) : that.sellerId != null)
            return false;
        if (domainId != null ? !domainId.equals(that.domainId) : that.domainId != null)
            return false;
        if (brandName != null ? !brandName.equals(that.brandName) : that.brandName != null)
            return false;
        if (companyName != null ? !companyName.equals(that.companyName) : that.companyName != null)
            return false;
        if (originalPrice != null ? !originalPrice.equals(that.originalPrice) : that.originalPrice != null)
            return false;
        if (ptype != null ? !ptype.equals(that.ptype) : that.ptype != null) return false;
        if (colors != null ? !colors.equals(that.colors) : that.colors != null) return false;
        return imagem != null ? imagem.equals(that.imagem) : that.imagem == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (codigo != null ? codigo.hashCode() : 0);
        result = 31 * result + (nome != null ? nome.hashCode() : 0);
        result = 31 * result + (valor != null ? valor.hashCode() : 0);
        result = 31 * result + (stockout ? 1 : 0);
        result = 31 * result + (images != null ? images.hashCode() : 0);
        result = 31 * result + (sellerId != null ? sellerId.hashCode() : 0);
        result = 31 * result + (domainId != null ? domainId.hashCode() : 0);
        result = 31 * result + (brandName != null ? brandName.hashCode() : 0);
        result = 31 * result + (companyName != null ? companyName.hashCode() : 0);
        result = 31 * result + (originalPrice != null ? originalPrice.hashCode() : 0);
        result = 31 * result + (ptype != null ? ptype.hashCode() : 0);
        result = 31 * result + (active ? 1 : 0);
        result = 31 * result + (colors != null ? colors.hashCode() : 0);
        result = 31 * result + (imagem != null ? imagem.hashCode() : 0);
        return result;
    }

    public void setListCategory(List<Category> listCategory) {
        this.listCategory = listCategory;
    }

    public List<Category> getListCategory() {
        return listCategory;
    }
}
