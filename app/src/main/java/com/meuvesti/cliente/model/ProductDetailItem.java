package com.meuvesti.cliente.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hersonrodrigues on 07/02/17.
 */
public class ProductDetailItem {

    @SerializedName("colors")
    protected List<ProductColor> colors;
    @SerializedName("id")
    private String id;
    @SerializedName("code")
    private String codigo;
    @SerializedName("name")
    private String nome;
    @SerializedName("price")
    private Double valor;
    @SerializedName("photos")
    private List<Photo> imagem;

    @SerializedName("images")
    private List<Image> images;

    @SerializedName("description")
    private String descricao;
    @SerializedName("composition")
    private String composicao;
    @SerializedName("brand_name")
    private String marca;
    @SerializedName("active")
    private boolean ativo;
    @SerializedName("stockout")
    private boolean stockout;
    @SerializedName("packs")
    private List<Pack> packs;
    @SerializedName("ptype")
    private String ptype;

    public ProductDetailItem(){}

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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getComposicao() {
        return composicao;
    }

    public void setComposicao(String composicao) {
        this.composicao = composicao;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public List<Pack> getPacks() {
        return packs;
    }

    public void setPacks(List<Pack>  packs) {
        this.packs = packs;
    }

    public List<ProductColor> getColors() {
        return colors;
    }

    public void setColors(List<ProductColor> colors) {
        this.colors = colors;
    }

    public boolean isStockout() {
        return stockout;
    }

    public void setStockout(boolean stockout) {
        this.stockout = stockout;
    }

    public String getPtype() {
        return ptype;
    }

    public void setPtype(String ptype) {
        this.ptype = ptype;
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

    /*public List<Photo> getImagem() {
        return imagem;
    }*/
    public void setImagem(List<Photo> imagem) {
        this.imagem = imagem;
    }
}
