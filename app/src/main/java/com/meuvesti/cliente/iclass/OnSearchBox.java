package com.meuvesti.cliente.iclass;

import com.meuvesti.cliente.model.Category;

import java.util.List;

public interface OnSearchBox {
    void onFocus();

    void onLostFocus();

    void onClose();

    void onFinishSearch(String ids, List<Category> mCached, List<String> list);

    void onTop();

    void showNotFound(boolean show);

    void onSingleSearch(String searchText);
}
