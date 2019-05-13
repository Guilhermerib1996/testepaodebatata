package com.meuvesti.cliente.widget;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.meuvesti.cliente.R;
import com.meuvesti.cliente.adapter.CatalogoRecycleAdapter;
import com.meuvesti.cliente.iclass.OnSearch;

/**
 * Created by Herson Rodrigues <hersonrodrigues@gmail.com> on 23/02/2018.
 */

public class SearchControl {
    /*private final Context mContext;
    private final OnSearch mCallBack;
    private final Animation anim;
    private final CatalogoRecycleAdapter.SearchViewHolder mHolder;
    private String mText = null;

    public SearchControl(Context context, CatalogoRecycleAdapter.SearchViewHolder holder, OnSearch onSearch) {
        mContext = context;
        mHolder = holder;
        mCallBack = onSearch;
        mText = mCallBack.getSearchText();
        anim = AnimationUtils.loadAnimation(mContext, R.anim.scale);
        initViews();
    }

    private void initViews() {
        mHolder.mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHolder.mSearchEdit.setText("");
                configuraBusca("");
            }
        });
        mHolder.mSearchEdit.setText(mText);
        mHolder.mSearchEdit.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    configuraBusca(mHolder.mSearchEdit.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    private void configuraBusca(String searchText) {
        mText = searchText;
        final boolean isText = mText.length() != 0;
        if (isText) {
            processaBusca();
        } else {
            cancelaBusca();
        }
    }

    private void processaBusca() {
        mCallBack.proccess(mText);
        mHolder.mCancel.setVisibility(View.VISIBLE);
        mHolder.mCancel.setAnimation(anim);
    }

    private void cancelaBusca() {
        mText = null;
        mHolder.mCancel.setVisibility(View.GONE);
        mCallBack.cancel();
    }*/
}
