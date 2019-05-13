package com.meuvesti.cliente.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.meuvesti.cliente.R;
import com.meuvesti.cliente.activity.MainActivity;
import com.meuvesti.cliente.iclass.OnSearch;
import com.meuvesti.cliente.iclass.OnSearchBox;
import com.meuvesti.cliente.iclass.OnVestBehavor;
import com.meuvesti.cliente.model.Category;
import com.meuvesti.cliente.model.Image;
import com.meuvesti.cliente.utils.ProductItemDiffUtils;
import com.meuvesti.cliente.widget.VestiSearchBox;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import com.meuvesti.cliente.activity.ProdutoActivity;
import com.meuvesti.cliente.model.ItemProduct;


/**
 * Created by hersonrodrigues on 23/01/17.
 */
public class CatalogoRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ITEM = 1;
    private static final int VIEW_TYPE_LOADING = 2;
    private static final int VIEW_TYPE_SEARCH = 3;

    private final Context mContext;
    private final OnSearch mSearchCallBack;
    private final OnSearchBox mSearchBox;
    private final OnVestBehavor mOnBehavor;
    private final MainActivity mActivity;
    private List<ItemProduct> mList = new ArrayList<>();

    public void swap(List<ItemProduct> itens) {
        final ProductItemDiffUtils diffCallback = new ProductItemDiffUtils(this.mList, itens);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        this.mList.clear();
        this.mList.addAll(itens);
        diffResult.dispatchUpdatesTo(this);
    }

    public CatalogoRecycleAdapter(MainActivity mActivity, Context context, OnSearchBox searchBox, OnVestBehavor onVestBehavor) {
        mSearchCallBack = null;
        this.mActivity = mActivity;
        this.mContext = context;
        this.mSearchBox = searchBox;
        this.mOnBehavor = onVestBehavor;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_SEARCH;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(com.meuvesti.cliente.R.layout.catalogo_item, parent, false);
            return new ItemViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress, parent, false);
            return new LoadingViewHolder(view);
        } else if (viewType == VIEW_TYPE_SEARCH) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search, parent, false);
            return new SearchViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder genericHolder, int position) {
        if (genericHolder instanceof ItemViewHolder) {
            final ItemViewHolder holder = (ItemViewHolder) genericHolder;
            renderViewHolder(holder, position);
        } else if (genericHolder instanceof SearchViewHolder) {
            final SearchViewHolder holder = (SearchViewHolder) genericHolder;
            renderSearchViewHolder(holder, position);
        }
    }

    private void renderSearchViewHolder(SearchViewHolder mSearchViewHolder, int position) {
        if (position == 0) {
            mSearchViewHolder.mVestiSearchBox.setVisibility(View.VISIBLE);
        } else {
            mSearchViewHolder.mVestiSearchBox.setVisibility(View.GONE);
        }
        List<Category> data = getItem(position).getListCategory();
        mSearchViewHolder.mVestiSearchBox.setCachedData(data);
        mSearchViewHolder.mVestiSearchBox.setCallBack(mSearchBox);
        mSearchViewHolder.mVestiSearchBox.setBehavor(mOnBehavor);
        mSearchViewHolder.mVestiSearchBox.setActivity(mActivity);
    }

    private void renderViewHolder(final ItemViewHolder holder, int position) {
        final ItemProduct item = getItem(position);
        holder.mTitle.setText(item.getNome());
        holder.mPrice.setText(String.format("R$ %.2f", item.getValor()));

        if (item.isStockout()) {
            holder.mBoxEsgotado.setVisibility(View.VISIBLE);
        } else {
            holder.mBoxEsgotado.setVisibility(View.GONE);
        }

        if (item != null && item.getImages() != null && !item.getImages().isEmpty()) {
            final String urlImage = Image.getFirstLarger(item.getImages());
            Picasso.with(mContext)
                    .load(urlImage)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .resize(650, 0)
                    .into(holder.mImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.mContainer.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            Picasso.with(mContext)
                                    .load(urlImage)
                                    .resize(650, 0)
                                    .into(holder.mImage);
                        }
                    });

        } else {
            Picasso.with(mContext)
                    .load(R.mipmap.ic_launcher)
                    .resize(200, 0)
                    .into(holder.mImage);
        }

        holder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, ProdutoActivity.class);
                i.putExtra(ItemProduct.ITEM_PRODUCT, item);
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public ItemProduct getItem(int pos) {
        return mList.get(pos);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public long getItemId(int position) {
        try {
            return mList.get(position).hashCode();
        } catch (Exception e) {
            return super.getItemId(position);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mImage;
        private final TextView mTitle;
        private final TextView mPrice;
        private final View mSpace;
        public View mContainer;
        public View mBoxEsgotado;

        public ItemViewHolder(View view) {
            super(view);
            mContainer = view;
            mImage = (ImageView) view.findViewById(R.id.imgCatalogo);
            mTitle = (TextView) view.findViewById(R.id.txProduto);
            mPrice = (TextView) view.findViewById(R.id.txValor);
            mSpace = view.findViewById(com.meuvesti.cliente.R.id.space);
            mBoxEsgotado = view.findViewById(R.id.box_esgotado);
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        private final ProgressBar mProgress;
        public View mContainer;

        public LoadingViewHolder(View view) {
            super(view);
            mContainer = view;
            mProgress = (ProgressBar) view.findViewById(com.meuvesti.cliente.R.id.progress);
        }
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {
        public final VestiSearchBox mVestiSearchBox;

        public SearchViewHolder(View view) {
            super(view);
            mVestiSearchBox = view.findViewById(R.id.vestiSearchBox);
        }
    }
}