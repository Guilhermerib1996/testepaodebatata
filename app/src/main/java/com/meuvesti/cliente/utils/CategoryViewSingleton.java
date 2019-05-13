package com.meuvesti.cliente.utils;

import com.meuvesti.cliente.model.Category;

import java.util.List;

public class CategoryViewSingleton {

    private List<Category> list;
    private String searchText;

    private static CategoryViewSingleton INSTANCE = null;
    private String tempSearch;

    public CategoryViewSingleton() {
    }

    public static CategoryViewSingleton getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CategoryViewSingleton();
        }
        return INSTANCE;
    }

    public List<Category> getList() {
        return this.list;
    }

    public void setList(List<Category> list) {
        this.list = list;
    }

    public void setSearchText(String text) {
        searchText = text;
    }

    public String getSearchText() {
        return searchText;
    }

    @Override
    public String toString() {
        return "CategoryViewSingleton{" +
                "list=" + list +
                ", searchText='" + searchText + '\'' +
                '}';
    }

    public void setTempSearch(String tempSearch) {
        this.tempSearch = tempSearch;
    }

    public String getTempSearch() {
        return tempSearch;
    }
}
