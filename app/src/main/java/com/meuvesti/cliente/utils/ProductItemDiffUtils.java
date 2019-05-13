package com.meuvesti.cliente.utils;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.util.Log;

import com.meuvesti.cliente.model.ItemProduct;

import java.util.List;

public class ProductItemDiffUtils extends DiffUtil.Callback {

    private static final String TAG = ProductItemDiffUtils.class.getSimpleName();
    private final List<ItemProduct> mOld;
    private final List<ItemProduct> mNew;

    public ProductItemDiffUtils(List<ItemProduct> oldEmployeeList, List<ItemProduct> newEmployeeList) {
        this.mOld = oldEmployeeList;
        this.mNew = newEmployeeList;
    }

    @Override
    public int getOldListSize() {
        return mOld.size();
    }

    @Override
    public int getNewListSize() {
        return mNew.size();
    }

    @Override
    public boolean areItemsTheSame(int oldPos, int newPos) {
        //Log.i(TAG, mOld.get(oldItemPosition).toString() + " old: " + String.valueOf(mOld.get(oldItemPosition).getId()) + " new: " + String.valueOf(mNew.get(oldItemPosition).getId()));
        return mOld != null && mNew != null && mOld.get(oldPos).getId().equalsIgnoreCase(mNew.get(newPos).getId());
    }

    @Override
    public boolean areContentsTheSame(int oldPos, int newPos) {
        final ItemProduct oldList = mOld.get(oldPos);
        final ItemProduct newList = mNew.get(newPos);
        Log.i(TAG, "new: " + oldList.toString() + " old: " + newList.toString() + " equalContent: " + oldList.equals(newList));
        return mOld != null && mNew != null && oldList != null && newList != null && oldList.equals(newList);
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}