package com.meuvesti.cliente.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.meuvesti.cliente.R;
import com.meuvesti.cliente.activity.MainActivity;
import com.meuvesti.cliente.adapter.CatalogoRecycleAdapter;
import com.meuvesti.cliente.iclass.OnSearchBox;
import com.meuvesti.cliente.iclass.OnVestBehavor;
import com.meuvesti.cliente.iclass.UpdateCatalog;
import com.meuvesti.cliente.iclass.VestiCallback;
import com.meuvesti.cliente.model.CatalogResponse;
import com.meuvesti.cliente.model.Category;
import com.meuvesti.cliente.model.ItemProduct;
import com.meuvesti.cliente.model.Product;
import com.meuvesti.cliente.realm.CartRealmService;
import com.meuvesti.cliente.realm.FilterRealm;
import com.meuvesti.cliente.realm.FilterRealmService;
import com.meuvesti.cliente.realm.RealmService;
import com.meuvesti.cliente.realm.UsuarioRealm;
import com.meuvesti.cliente.service.VestiAPI;
import com.meuvesti.cliente.service.VestiService;
import com.meuvesti.cliente.utils.CategoriaDB;
import com.meuvesti.cliente.utils.CategoryViewSingleton;
import com.meuvesti.cliente.utils.Globals;
import com.meuvesti.cliente.utils.Utils;
import com.meuvesti.cliente.utils.VestiActionBar;
import com.meuvesti.cliente.widget.VestiSearchBox;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class CatalagoFragment extends Fragment {
    public static final int REQUEST_FILTRO = 10001;
    public static final int PER_PAGE = 20;
    private static final String TAG = CatalagoFragment.class.getSimpleName();
    SwipeRefreshLayout mSwipeRefreshLayout;
    private View rootView;
    private Context mContext;
    private VestiActionBar mVestiActionBar;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private CatalogoRecycleAdapter mAdapter;
    private UsuarioRealm mUsuario;
    private List<ItemProduct> mItemProducts = new ArrayList<>();
    private MainActivity mActivity;
    private Date mStartTime;
    private CatalogResponse mCatalog;
    private boolean mHasScrollError = false;
    private boolean isZap = false;
    private String sellerId;
    private Call<CatalogResponse> mCatalogThread;
    private boolean mSearchMode = false;
    private boolean refresh = false;
    private OnVestBehavor mVestBehavor;
    private OnSearchBox mSearchCallback;
    private List<String> mTagKeysList = new ArrayList<>();
    private VestiSearchBox searchBox;
    private int mPosition = -1;
    private ArrayList<ItemProduct> mTList = new ArrayList<>();
    private Handler handler;
    private CategoryViewSingleton cache;


    public static CatalagoFragment newInstance() {
        Bundle args = new Bundle();
        CatalagoFragment fragment = new CatalagoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_catalago, container, false);

        mContext = getActivity();
        mActivity = (MainActivity) mContext;
        mVestiActionBar = mActivity.getVestiActionBar();
        mVestiActionBar.setImageTitle();
        mUsuario = RealmService.getUsuarioLogado(mContext);

        init();

        return rootView;
    }

    private void init() {

        mActivity.setFragmentInterface(new VestiCallback() {

            @Override
            public void call(Object... args) {
                callNewCatalog(false, false);
            }
        });

        mSwipeRefreshLayout = rootView.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setProgressViewOffset(true, 100, 350);
        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(mContext, R.color.branco));
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(mContext, R.color.colorButton));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                if (mCatalog == null || mCatalog.getResponse() == null) {
                    Toast.makeText(mContext, "Ocorreu um erro, tente novamente.", Toast.LENGTH_SHORT).show();
                    init();
                } else {
                    if (mCatalogThread != null) {
                        mCatalogThread.cancel();
                    }
                    mItemProducts.clear();

                    ItemProduct item = new ItemProduct();
                    item.setId("search_item");
                    item.setListCategory(null);
                    mItemProducts.add(0, item);

                    mSearchMode = true;

                    clearCache();

                    mAdapter.swap(mItemProducts);

                    if (searchBox != null) {
                        searchBox.clearAll();
                    }

                    cache = CategoryViewSingleton.getInstance();
                    cache.setTempSearch("");

                    mCatalog.getResponse().setNext_page(1);
                    callNewCatalog(false, true);
                }
            }
        });

        showLoading();
        configureBulletFilter();
        configureActionBar();
        configureCart();
        mItemProducts.clear();
        callServiceCatalog(false, getNextPage());
    }

    private void clearCache() {
        cache = new CategoryViewSingleton().getInstance();
        cache.setList(new ArrayList<Category>());
        cache.setSearchText(null);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        //clearCache();

        if (searchBox != null) {
            //searchBox.clearAll();
        }
    }

    private void configureBulletFilter() {
        if (Utils.isUsingCategory(mContext)) {
            int totalBadge = CategoriaDB.getTotalBadge(mContext);
            mVestiActionBar.setBulletFilterText(totalBadge);
        } else {
            RealmResults<FilterRealm> selectedFilters = FilterRealmService.getMySelectedFilter();
            if (!selectedFilters.isEmpty()) {
                mVestiActionBar.setBulletFilterText(selectedFilters.size());
            } else {
                mVestiActionBar.setBulletFilterText(0);
            }
        }
    }


    private void configureActionBar() {
        mStartTime = new Date();

        configureRecycle();

        if (mUsuario == null) {
            mActivity.showLoginMenu(true);
        }

        if (mVestiActionBar != null) {
            mVestiActionBar.setScrollViewToHideBar(mRecyclerView, mGridLayoutManager);
            mVestiActionBar.show();
        }

        configureCart();
    }

    private void configureRecycle() {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);

        mSearchCallback = new OnSearchBox() {

            @Override
            public void onFocus() {
                //mVestiActionBar.hide();
            }

            @Override
            public void onLostFocus() {
            }

            @Override
            public void onClose() {
                mSearchMode = false;
                cache = CategoryViewSingleton.getInstance();
                cache.setTempSearch("");
                cache.setSearchText("");
                cache.setList(new ArrayList<Category>());
                callNewCatalog(true, true);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mRecyclerView != null) {
                            mRecyclerView.scrollToPosition(0);
                        }
                        mVestiActionBar.show();
                    }
                }, 100);
            }

            @Override
            public void onFinishSearch(String ids, List<Category> mCached, List<String> list) {
                mTagKeysList.clear();
                mTagKeysList.addAll(list);

                mItemProducts.clear();

                ItemProduct item = new ItemProduct();
                item.setId("search_item");
                item.setListCategory(mCached);
                mItemProducts.add(0, item);

                mSearchMode = true;
                cache = CategoryViewSingleton.getInstance();
                cache.setTempSearch(ids);
                cache.setList(mCached);

                callNewCatalog(true, true);
            }

            @Override
            public void onTop() {
                if (mRecyclerView != null) {
                    mRecyclerView.scrollToPosition(0);
                }
            }

            @Override
            public void showNotFound(boolean show) {

            }

            @Override
            public void onSingleSearch(String searchText) {
                mItemProducts.clear();

                ItemProduct item = new ItemProduct();
                item.setId("search_item");
                item.setListCategory(new ArrayList<Category>());
                mItemProducts.add(0, item);

                mSearchMode = true;

                cache = CategoryViewSingleton.getInstance();
                cache.setTempSearch(searchText);
                callNewCatalog(true, true);
            }
        };

        mVestBehavor = new OnVestBehavor() {
            @Override
            public void notFound(boolean show) {

            }

            @Override
            public void loading(boolean show) {

            }
        };

        mAdapter = new CatalogoRecycleAdapter(mActivity, mContext, mSearchCallback, mVestBehavor);
        mAdapter.setHasStableIds(true);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                //r will be populated with the coordinates of your view that area still visible.
                rootView.getWindowVisibleDisplayFrame(r);

                int heightDiff = rootView.getRootView().getHeight() - (r.bottom - r.top);
                if (heightDiff > 500) { // if more than 100 pixels, its probably a keyboard...
                    if (mRecyclerView != null) {
                        mRecyclerView.scrollToPosition(0);
                    }
                }
            }
        });

        mGridLayoutManager = new GridLayoutManager(getContext(), 2);
        final int numberOfColumns = 2;
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                // Na primeira posicao mostra 1 coluna
                return (position == 0) ? numberOfColumns : 1;
            }
        });

        mRecyclerView.setLayoutManager(mGridLayoutManager);
        RecyclerView.ItemAnimator animator = mRecyclerView.getItemAnimator();
        mRecyclerView.getItemAnimator().setChangeDuration(0);
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        mRecyclerView.setAdapter(mAdapter);
    }

    private void callNewCatalog(boolean clearOnLoad, boolean showLoading) {
        if (mCatalogThread != null) {
            mCatalogThread.cancel();
        }
        if (showLoading) {
            showLoading();
        }
        mCatalog = null;
        callServiceCatalog(clearOnLoad, getNextPage());
    }

    @Override
    public void onPause() {
        super.onPause();
        // Necessario para calcular o tempo de ociosidade da aplicacao e decidir de seve atualizar os produtos
        mStartTime = new Date();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Necessario caso ele volte do carrinho, deve atualizar os bullets
        configureCart();
        configureBulletFilter();

        Date currentTime = new Date();
        long diff = currentTime.getTime() - mStartTime.getTime();
        long sec = diff / 1000;
        long min = diff / (60 * 1000);
        long hour = diff / (60 * 60 * 1000);

        if (hour >= 6) {
            //Toast.makeText(mContext, "Passou mais de 6 horas em background, os produtos serão atualizados!", Toast.LENGTH_LONG).show();
            mItemProducts.clear();
            mCatalog = null;
            callServiceCatalog(false, getNextPage());
        }
    }

    private void configureCart() {
        mVestiActionBar.setBulletCartText(CartRealmService.getTotalProductsInCart());
    }

    private void callServiceCatalog(final boolean clearOnLoad, int nextPage) {
        if (nextPage == 0) {
            return;
        }
        mVestiActionBar.showNetworkError(false);

        Callback<CatalogResponse> callBack = new Callback<CatalogResponse>() {
            @Override
            public void onResponse(Call<CatalogResponse> call, Response<CatalogResponse> response) {
                if (clearOnLoad) {
                    mItemProducts.clear();
                }
                renderData(response);
            }

            @Override
            public void onFailure(Call<CatalogResponse> call, Throwable t) {
                if (t instanceof UnknownHostException && Utils.isNetworkAvailable(mContext)) {
                    loadingSearch(false);
                    Toast.makeText(mContext, getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                    mVestiActionBar.showNetworkError(true);
                    tryAgain();
                } else if (!call.isCanceled()) {
                    loadingSearch(false);
                    Toast.makeText(mContext, t.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
                mHasScrollError = true;
            }
        };

        String filter = FilterRealmService.getMySelectedFilterIds();
        String zap = Utils.getWhatsAppLink(getActivity());
        String categories = Utils.getCategories(mContext);

        cache = CategoryViewSingleton.getInstance();

        boolean hasFilter = Utils.getHasFilter(getActivity()) || (categories != null && !categories.isEmpty()) || (cache != null && cache.getTempSearch() != null && !cache.getTempSearch().isEmpty());

        Log.e(">>>>>>", "hasFilter:" + hasFilter + " mUsuario != null: " + String.valueOf(mUsuario != null) + " zap: ");

        if (refresh == true || mUsuario != null && hasFilter) {
            String search = cache.getTempSearch();
            mCatalogThread = VestiAPI.get().api(mContext).catalogue(PER_PAGE, search, filter, categories, nextPage, Globals.SCHEME_NAME);
            mCatalogThread.enqueue(callBack);
            refresh = false;
        } else if (refresh == false) {
            if (zap != null && !zap.isEmpty() && hasFilter == false) {
                isZap = true;
                mCatalogThread = VestiAPI.get().api(mContext).whatsAppList(zap, Globals.SCHEME_NAME, nextPage);
                mCatalogThread.enqueue(callBack);
            } else {
                // Sem lista de zap e de catalogo
                showWelcome();
            }
        }
    }

    private void loadingSearch(boolean show) {

    }

    private void tryAgain() {
        Log.e(TAG, "tryAgain " + new Date());
        final Handler handler = new Handler();
        final Runnable callback = new Runnable() {
            @Override
            public void run() {
                if (mHasScrollError == true && isAdded()) {
                    mHasScrollError = false;
                    callServiceCatalog(false, getNextPage());
                    handler.postDelayed(this, 6000);
                } else {
                    handler.removeCallbacks(this);
                }
            }
        };
        handler.postDelayed(callback, 6000);
    }

    private void renderData(Response<CatalogResponse> response) {
        loadingSearch(false);

        showLoading();

        if (mItemProducts.isEmpty()) {
            ItemProduct item = new ItemProduct();
            item.setId("search_item");
            mItemProducts.add(0, item);
        }

        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setRefreshing(false);

        if (response != null)
            mCatalog = response.body();

        if (mCatalog != null && mCatalog.getResult() != null && mCatalog.getResult().isSuccess() && mCatalog.getResponse() != null) {
            Product product = mCatalog.getResponse();
            List<ItemProduct> listItems = product.getData();
            if (listItems != null && !listItems.isEmpty()) {

                mItemProducts.addAll(listItems);
                mAdapter.swap(mItemProducts);

                try {
                    searchBox = ((CatalogoRecycleAdapter.SearchViewHolder) mRecyclerView.getChildViewHolder(mRecyclerView.getChildAt(0))).mVestiSearchBox;
                    searchBox.showNotFound(false);
                } catch (Exception e) {
                    searchBox = null;
                }

                if (mPosition != -1 && mItemProducts.size() >= mPosition) {
                    mRecyclerView.scrollToPosition(mPosition);
                    mPosition = -1;
                }

                if (mCatalog.getResponse().getCurrent_page() == 1) {
                    if (mRecyclerView != null) {
                        Handler h = new Handler();
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mRecyclerView.scrollToPosition(0);
                            }
                        }, 100);
                    }
                }

                showCatalog();

                // CARREGAMENTO UM ATRAS DO OUTRO PARA EVITAR TRAVAMENTO DE SCROLL PARA O USUARIO
                final int page = getNextPage();
                if (page != 0) {
                    Log.e(TAG, ">>> LOAD PAGE >>> " + page);
                    handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("TAG", "called");
                            callServiceCatalog(false, page);
                        }
                    }, 0);
                }
            } else {
                if (mSearchMode == true) {
                    mItemProducts.clear();
                    if (mItemProducts.isEmpty()) {
                        ItemProduct item = new ItemProduct();
                        item.setId("search_item");
                        mItemProducts.add(0, item);
                    }
                    mAdapter.swap(mItemProducts);
                    try {
                        searchBox = ((CatalogoRecycleAdapter.SearchViewHolder) mRecyclerView.getChildViewHolder(mRecyclerView.getChildAt(0))).mVestiSearchBox;
                        searchBox.showNotFound(true);
                    } catch (Exception e) {
                        searchBox = null;
                    }
                    showCatalog();
                } else {
                    if (mItemProducts.isEmpty()) {
                        if (mItemProducts.isEmpty()) {
                            ItemProduct item = new ItemProduct();
                            item.setId("search_item");
                            mItemProducts.add(0, item);
                        }
                        mAdapter.swap(mItemProducts);
                        showWelcome();
                    } else {
                        if (mItemProducts.isEmpty()) {
                            ItemProduct item = new ItemProduct();
                            item.setId("search_item");
                            mItemProducts.add(0, item);
                        }
                        mAdapter.swap(mItemProducts);
                        try {
                            searchBox = ((CatalogoRecycleAdapter.SearchViewHolder) mRecyclerView.getChildViewHolder(mRecyclerView.getChildAt(0))).mVestiSearchBox;
                            searchBox.showNotFound(true);
                        } catch (Exception e) {
                            searchBox = null;
                        }
                        showCatalog();
                    }
                }
            }
        }
    }

    private int getNextPage() {
        if (mCatalog == null) {
            return 1;
        } else {
            if (mCatalog == null || mCatalog.getResponse() == null || mCatalog.getResponse().getCurrent_page() == mCatalog.getResponse().getLast_page()) {
                return 0;
            } else {
                return mCatalog.getResponse().getCurrent_page() + 1;
            }
        }
    }

    private void showNenhumProdutoEncontrado() {
        if (mItemProducts.isEmpty() && mSearchMode) {
            rootView.findViewById(R.id.box_message).setVisibility(View.VISIBLE);
        } else {
            rootView.findViewById(R.id.box_message).setVisibility(View.GONE);
        }
    }

    private void showCatalog() {
        loadingSearch(false);
        rootView.findViewById(R.id.box_message).setVisibility(View.GONE);
        rootView.findViewById(R.id.box_recyclerview).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.welcome).setVisibility(View.GONE);
        rootView.findViewById(R.id.box_loading).setVisibility(View.GONE);
        rootView.findViewById(R.id.box_message).setVisibility(View.GONE);
    }

    private void showWelcome() {
        loadingSearch(false);
        rootView.findViewById(R.id.box_loading).setVisibility(View.GONE);
        rootView.findViewById(R.id.box_message).setVisibility(View.GONE);
        if (cache != null && cache.getTempSearch() != null && cache.getTempSearch().isEmpty()) {
            rootView.findViewById(R.id.box_recyclerview).setVisibility(View.GONE);
        } else {
            rootView.findViewById(R.id.box_recyclerview).setVisibility(View.VISIBLE);
        }
        rootView.findViewById(R.id.welcome).setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        rootView.findViewById(R.id.box_loading).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.box_message).setVisibility(View.GONE);
        //rootView.findViewById(R.id.box_recyclerview).setVisibility(View.GONE);
        rootView.findViewById(R.id.welcome).setVisibility(View.GONE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        configureCart();
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateCatalog event) {
        if (event != null) {
            EventBus.getDefault().removeStickyEvent(event);
        }

        mItemProducts.clear();

        ItemProduct item = new ItemProduct();
        item.setId("search_item");
        item.setListCategory(cache.getList());
        mItemProducts.add(0, item);

        mSearchMode = true;

        callNewCatalog(false, true);
    }
}
