package com.meuvesti.cliente.realm;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by hersonrodrigues on 08/02/17.
 */

public class ProductRealm  extends RealmObject {

    @PrimaryKey
    private String id;
    private String codigo;
    private String nome;
    private Double valor;
    private RealmList<PhotoRealm> imagem;
    private String descricao;
    private String composicao;
    private String marca;
    private RealmList<PackRealm> packs;
    private RealmList<ColorRealm> colors;
    private boolean inCart = false;
    private String ptype;
    private boolean ativo = true;

    public boolean isInCart() {
        return inCart;
    }

    public void setInCart(boolean inCart) {
        this.inCart = inCart;
    }

    public ProductRealm(){}

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

    public List<PhotoRealm> getImagem() {
        return imagem;
    }

    public void setImagem(RealmList<PhotoRealm> imagem) {
        this.imagem = imagem;
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

    public RealmList<PackRealm> getPacks() {
        return packs;
    }

    public void setPacks(RealmList<PackRealm> packs) {
        this.packs = packs;
    }

    public RealmList<ColorRealm> getColors() {
        return colors;
    }

    public void setColors(RealmList<ColorRealm> colors) {
        this.colors = colors;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public String getPtype() {
        return ptype;
    }

    public void setPtype(String ptype) {
        this.ptype = ptype;
    }
}
