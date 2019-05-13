package com.meuvesti.cliente.model;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import io.realm.annotations.Ignore;

/**
 * Created by Herson Rodrigues <hersonrodrigues@gmail.com> on 03/02/2018.
 */

public class Category  implements Serializable {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("display_name")
    @Expose
    private String displayName;
    @SerializedName("parent_name")
    @Expose
    private String parentName;
    @SerializedName("qtde")
    @Expose
    private int qtde;

    @Ignore
    private boolean checked;

    @Ignore
    private List<Category> children;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public int getQtde() {
        return qtde;
    }

    public void setQtde(int qtde) {
        this.qtde = qtde;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public List<Category> getChildren() {
        return children;
    }

    public void setChildren(List<Category> children) {
        this.children = children;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return qtde == category.qtde &&
                checked == category.checked &&
                Objects.equals(id, category.id) &&
                Objects.equals(name, category.name) &&
                Objects.equals(displayName, category.displayName) &&
                Objects.equals(parentName, category.parentName) &&
                Objects.equals(children, category.children);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {

        return Objects.hash(id, name, displayName, parentName, qtde, checked, children);
    }
}