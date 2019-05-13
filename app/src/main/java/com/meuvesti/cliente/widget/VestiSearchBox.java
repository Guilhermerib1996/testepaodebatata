package com.meuvesti.cliente.widget;


import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.meuvesti.cliente.R;
import com.meuvesti.cliente.activity.MainActivity;
import com.meuvesti.cliente.iclass.OnCheckedChangeListener;
import com.meuvesti.cliente.iclass.OnSearchBox;
import com.meuvesti.cliente.iclass.OnVestBehavor;
import com.meuvesti.cliente.model.Category;
import com.meuvesti.cliente.model.KeywordResponse;
import com.meuvesti.cliente.service.VestiAPI;
import com.meuvesti.cliente.utils.CategoryViewSingleton;
import com.meuvesti.cliente.utils.Globals;
import com.meuvesti.cliente.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VestiSearchBox extends LinearLayout implements OnVestBehavor {

    private View mSearchBox, mClose;
    private LinearLayout mTagsSelected, mTagsSearched;
    private HorizontalScrollView mHSVTagsSelected;
    private EditText mField;
    private OnSearchBox callBack;
    private Call<KeywordResponse> mThread;
    private List<Category> mCached = new ArrayList<>();
    private CategoryViewSingleton cache;
    private View mSearchIcon;
    private View mLoadingIcon;
    private View mNotFound;
    private OnVestBehavor behavor;
    private View mMainSearchArea;
    private boolean mEnableSearchMode = false;
    private MainActivity activity;

    public VestiSearchBox(Context context) {
        super(context);
        init(null, 0, context);
    }

    public VestiSearchBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0, context);
    }

    public VestiSearchBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle, context);
    }

    private void init(AttributeSet attrs, int defStyle, Context context) {
        View view = inflate(getContext(), R.layout.vesti_search_box_component, null);

        mEnableSearchMode = Utils.getConfigSearchEnableTags(getContext());

        mField = view.findViewById(R.id.search_field);

        mMainSearchArea = view.findViewById(R.id.main_searchbox_area);
        mMainSearchArea.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mField.requestFocus();
            }
        });

        mHSVTagsSelected = view.findViewById(R.id.hsv_tags_selected);
        mTagsSelected = view.findViewById(R.id.tags_selected);
        calcWidthField();

        mTagsSearched = view.findViewById(R.id.tags_search);

        mSearchBox = view.findViewById(R.id.search_box);
        mClose = view.findViewById(R.id.close);

        mSearchIcon = view.findViewById(R.id.search_icon);
        mLoadingIcon = view.findViewById(R.id.loading_icon);

        mNotFound = view.findViewById(R.id.not_found);

        cache = new CategoryViewSingleton().getInstance();

        Log.d("SingCache", cache.toString());


        if (cache.getSearchText() != null) {
            mField.setText(cache.getSearchText());
            mField.setSelection(cache.getSearchText().length());
            showCloseButton(true);
        }

        if(cache.getTempSearch()!=null){
            recoveryData(cache.getList());
        }

        mClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mField.setText(null);
                cache.setSearchText(null);
                if (callBack != null)
                    callBack.onClose();
            }
        });

        mField.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                //Evita que chame duas vezes o evento
                if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                    return true;

                boolean isTextFilled = mField.getText().toString().length() == 0;
                boolean isDel = KeyEvent.KEYCODE_DEL == keyEvent.getKeyCode();
                boolean isEnter = KeyEvent.KEYCODE_ENTER == keyEvent.getKeyCode();

                if (isTextFilled && (isDel || isEnter)) {
                    closeKeyboard();
                    int count = mTagsSelected.getChildCount();
                    if (count > 0) {
                        try {
                            mTagsSelected.removeViewAt(count - 1);
                            mCached.remove(count - 1);
                            cache.setList(mCached);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        calcWidthField();
                        callSearch();
                    }
                    verifyIsCloseSearch();
                } else if (isEnter) {
                    closeKeyboard();
                }
                return false;
            }
        });

        mField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String tagText = s.toString();

                if (tagText.length() > 0) {
                    showCloseButton(true);
                } else {
                    showCloseButton(false);
                    cleanSearchedTags();
                }
                // So inicia pesquisa com no minimo 2 caracteres digitados
                if (isEnabledSearchTagMode()) {
                    if (tagText.length() >= 2) {
                        callServiceListTags(tagText);
                    }
                } else {
                    // inicia busca simples com 3 caracteres
                    if (tagText.length() >= 0 || tagText.length() >= 3) {
                        callSearch();
                    }
                }
            }
        });

        mSearchBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mField.requestFocus();
            }
        });

        mField.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (mField.getText() != null && mField.getText().length() > 0) {
                    showCloseButton(true);
                } else {
                    showCloseButton(false);
                }
                if (hasFocus) {
                    if (callBack != null) {
                        callBack.onFocus();
                    }
                } else {
                    showCloseButton(false);
                    if (callBack != null) {
                        callBack.onLostFocus();
                    }
                }
            }
        });

        addView(view);
    }

    private void closeKeyboard() {
        if (activity == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private boolean isEnabledSearchTagMode() {
        return mEnableSearchMode == true;
    }

    public void recoveryData(List<Category> cached) {
        if (isEnabledSearchTagMode()) {
            if (cached != null && !cached.isEmpty()) {
                for (Category category : cached) {
                    final VestiSwitchButton view = new VestiSwitchButton(getContext());
                    view.setTexto(category.getDisplayName());
                    view.setTag(category.getDisplayName());
                    view.setTag(R.id.tag_key, category.getId());
                    view.setSelected(true);
                    view.setAtivo(true);
                    view.noIcon();
                    view.setOnCheckedChangeListener(getClickAdd(view, category, true));
                    if (mTagsSelected != null && mTagsSelected.findViewWithTag(category.getDisplayName()) == null)
                        mTagsSelected.addView(view);
                }
            }
        }
    }

    public void clearAll() {
        mTagsSelected.removeAllViews();
        mTagsSearched.removeAllViews();
    }

    private void verifyIsCloseSearch() {
        if (null != callBack && mTagsSelected.getChildCount() == 0 && mField.getText().toString().length() == 0)
            callBack.onClose();
    }

    private void showCloseButton(boolean show) {
        mClose.setVisibility(show ? VISIBLE : INVISIBLE);
    }

    private void callSearch() {
        if (callBack != null) {
            String textSearch = mField.getText().toString();
            if (isEnabledSearchTagMode()) {
                String ids = "";
                List<String> list = new ArrayList<>();
                for (int i = 0; i < mTagsSelected.getChildCount(); i++) {
                    list.add(((String) mTagsSelected.getChildAt(i).getTag(R.id.tag_key)));
                    ids = ids + mTagsSelected.getChildAt(i).getTag(R.id.tag_key) + "_";
                }
                callBack.onFinishSearch(removeLastUnderscore(ids), cache.getList(), list);
            } else if (!textSearch.isEmpty()) {
                cache.setSearchText(textSearch);
                callBack.onSingleSearch(textSearch);
            }
        }
    }

    public String removeLastUnderscore(String str) {
        if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == '_') {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    private void callServiceListTags(String tagText) {
        if (isEnabledSearchTagMode()) {
            showLoading(true);
            mThread = VestiAPI.get().api(getContext()).keywords(tagText, Globals.SCHEME_NAME);
            mThread.enqueue(new Callback<KeywordResponse>() {
                @Override
                public void onResponse(Call<KeywordResponse> call, Response<KeywordResponse> response) {
                    showLoading(false);
                    if (response.body() != null && response.body().getResult() != null && response.body().getResult().isSuccess()) {
                        cleanSearchedTags();
                        for (Category category : response.body().getResponse()) {
                            addNewKey(category, true);
                        }
                    } else {
                        Toast.makeText(getContext(), getContext().getString(R.string.erro_servico_categorias, "Indefinido."), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<KeywordResponse> call, Throwable t) {
                    showLoading(false);
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void cleanSearchedTags() {
        mTagsSearched.removeAllViews();
    }

    /*
        Adiciona a view (palavra chave) no array (horizontal view) de palavras chaves
     */
    private void addNewKey(final Category category, final boolean callbackActive) {
        String name = category.getDisplayName();
        String id = category.getId();
        // Detect if this view exist into the array
        View findView = mTagsSelected.findViewWithTag(name);
        View selectedView = mTagsSearched.findViewWithTag(name);

        if (findView == null && selectedView == null) {
            final VestiSwitchButton view = new VestiSwitchButton(getContext());
            view.setTexto(name);
            view.setTag(name);
            view.noIcon();
            view.setTag(R.id.tag_key, id);
            view.setOnCheckedChangeListener(getClickAdd(view, category, callbackActive));
            mTagsSearched.addView(view);
        }
    }

    private void removeFromCacheByView(VestiSwitchButton view) {
        String id = (String) view.getTag(R.id.tag_key);
        if (id != null && mCached != null && !mCached.isEmpty()) {
            for (int i = 0; i < mCached.size(); i++) {
                Category category = mCached.get(i);
                if (category.getId().equals(id)) {
                    mCached.remove(i);
                    cache.setList(mCached);
                    //saveListIdsToSharedPrefs(getContext(), mCached);
                }
            }
        }
    }

    private void calcWidthField() {
        // Pra facilitar imagine quantos por cento o campo de texto ira crescer esse 'e o weight
        /*float fieldPercent, boxSearchPercent;
        if (mTagsSelected.getChildCount() == 0) {
            fieldPercent = 100;
            boxSearchPercent = 0;
            //mHSVTagsSelected.setVisibility(GONE);
        } else if (mTagsSelected.getChildCount() >= 1 && mTagsSelected.getChildCount() <= 1) {
            fieldPercent = 90;
            boxSearchPercent = 10;
            //mHSVTagsSelected.setVisibility(VISIBLE);
        } else if (mTagsSelected.getChildCount() >= 2 && mTagsSelected.getChildCount() <= 3) {
            fieldPercent = 70;
            boxSearchPercent = 30;
            //mHSVTagsSelected.setVisibility(VISIBLE);
        } else if (mTagsSelected.getChildCount() >= 3 && mTagsSelected.getChildCount() <= 5) {
            fieldPercent = 40;
            boxSearchPercent = 60;
            //mHSVTagsSelected.setVisibility(VISIBLE);
        } else {
            fieldPercent = 40;
            boxSearchPercent = 60;
            //mHSVTagsSelected.setVisibility(VISIBLE);
        }

        FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.setFlexBasisPercent(boxSearchPercent);
        mBox1.setLayoutParams(params);

        FlexboxLayout.LayoutParams params2 = new FlexboxLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params2.setFlexBasisPercent(fieldPercent);
        mBox2.setLayoutParams(params2);*/

    }

    private OnClickListener removeKey() {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                mTagsSearched.removeView(v);
            }
        };
    }

    public void setCallBack(OnSearchBox callBack) {
        this.callBack = callBack;
    }

    public OnCheckedChangeListener getClickAdd(final VestiSwitchButton view, final Category category, final boolean callbackActive) {
        return new OnCheckedChangeListener() {
            @Override
            public void onChecked(boolean checked) {
                if (checked) {
                    mTagsSearched.removeView(view);
                    view.setSelected(true);
                    view.setAtivo(true);
                    view.noIcon();
                    view.setOnClickListener(removeKey());
                    mTagsSelected.addView(view);

                    mCached.add(category);
                    cache.setList(mCached);
                    //saveListIdsToSharedPrefs(getContext(), mCached);

                    mField.setText(null);
                    calcWidthField();
                    if (callBack != null) {
                        callBack.onTop();
                    }
                    mHSVTagsSelected.postDelayed(new Runnable() {
                        public void run() {
                            mHSVTagsSelected.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                        }
                    }, 100L);
                } else {

                    mTagsSelected.removeView(view);
                    mHSVTagsSelected.postDelayed(new Runnable() {
                        public void run() {
                            removeFromCacheByView(view);
                        }
                    }, 300);

                    calcWidthField();
                    verifyIsCloseSearch();
                }

                if (callbackActive) {
                    callSearch();
                }
            }
        };
    }

    public void setCachedData(List<Category> cachedData) {
        if (cachedData != null && !cachedData.isEmpty()) {
            mCached = cachedData;
            recoveryData(cachedData);
        }
    }

    public void showLoading(boolean show) {
        mSearchIcon.setVisibility(show ? GONE : VISIBLE);
        mLoadingIcon.setVisibility(show ? VISIBLE : GONE);
    }

    public void showNotFound(boolean show) {
        mNotFound.setVisibility(show ? VISIBLE : GONE);
    }

    @Override
    public void notFound(boolean show) {
        showNotFound(show);
        if (behavor != null)
            behavor.notFound(show);
    }

    @Override
    public void loading(boolean show) {
        showLoading(show);
        if (behavor != null)
            behavor.loading(show);
    }

    public void setBehavor(OnVestBehavor behavor) {
        this.behavor = behavor;
    }

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }
}
