package com.meuvesti.cliente.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meuvesti.cliente.FontsOverride;
import com.meuvesti.cliente.R;
import com.meuvesti.cliente.activity.CarrinhoActivity;
import com.meuvesti.cliente.activity.ProdutoActivity;
import com.meuvesti.cliente.model.ItemProduct;
import com.meuvesti.cliente.model.Photo;
import com.meuvesti.cliente.realm.CartRealmService;
import com.meuvesti.cliente.realm.PhotoRealm;
import com.meuvesti.cliente.realm.ProductRealm;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by hersonrodrigues on 23/01/17.
 */
public class CarrinhoRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final CarrinhoActivity mActivity;
    private final Typeface fontFuturaMedium;
    private final Typeface fontFuturaHeavy;
    private final Typeface fontFuturaBook;
    private List<ProductRealm> mList;

    public CarrinhoRecycleAdapter(Context context, List<ProductRealm> list) {
        this.mContext = context;
        this.mList = list;
        fontFuturaMedium = FontsOverride.fontMedium(mContext.getAssets());
        fontFuturaHeavy = FontsOverride.fontHeavy(mContext.getAssets());
        fontFuturaBook = FontsOverride.fontBook(mContext.getAssets());
        mActivity = (CarrinhoActivity) context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(com.meuvesti.cliente.R.layout.carrinho_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holderView, int position) {
        final ItemViewHolder holder = (ItemViewHolder) holderView;

        if (position == 0) {
            holder.space.setVisibility(View.VISIBLE);
        } else {
            holder.space.setVisibility(View.GONE);
        }

        final ProductRealm produto = mList.get(position);
        holder.textEsgotado.setTypeface(fontFuturaMedium);
        holder.txtValorTotal.setTypeface(fontFuturaHeavy);
        holder.txtValorTotal.setTypeface(fontFuturaHeavy);
        holder.textLabelTotal.setTypeface(fontFuturaHeavy);
        holder.txtLabelPecas.setTypeface(fontFuturaHeavy);
        holder.txtValor.setTypeface(fontFuturaHeavy);
        holder.txtCodigo.setTypeface(fontFuturaMedium);
        holder.txtProduto.setTypeface(fontFuturaMedium);

        if (!produto.isAtivo()) {
            holder.esgotado.setVisibility(View.VISIBLE);
            holder.body.setAlpha(0.2f);

        } else {
            holder.esgotado.setVisibility(View.GONE);
            holder.body.setAlpha(1f);
        }

        holder.btnExcluir.setTag(com.meuvesti.cliente.R.id.idProduct, produto.getId());
        holder.btnExcluir.setTag(com.meuvesti.cliente.R.id.position, position);
        holder.btnExcluir.setTag(com.meuvesti.cliente.R.id.convertView, holder.mContainer);

        //txtProduto.setText((produto.getNome().length() > 17) ? String.format("%s...", produto.getNome().substring(0, 17)) : produto.getNome());
        holder.txtProduto.setText(produto.getNome());
        holder.txtCodigo.setText(produto.getCodigo());
        holder.txtValor.setText(String.format("R$ %.2f", produto.getValor()));

        int total = CartRealmService.totalByProduct(produto);
        holder.txtPecas.setText(String.valueOf(total));
        holder.txtValorTotal.setText(String.format("R$ %.2f", total * produto.getValor()));

        if(produto.getImagem()!=null && !produto.getImagem().isEmpty()) {
            Picasso.with(mContext)
                    .load(produto.getImagem().get(0).getUrl())
                    .resize(0, 650)
                    .into(holder.imgProduto, new Callback() {
                        @Override
                        public void onSuccess() {
                            Animation myFadeInAnimation = AnimationUtils.loadAnimation(mContext, com.meuvesti.cliente.R.anim.fadein);
                            holder.imgProduto.startAnimation(myFadeInAnimation);
                        }

                        @Override
                        public void onError() {

                            Picasso.with(mContext)
                                    .load(com.meuvesti.cliente.R.mipmap.ic_launcher)
                                    .resize(0, 650)
                                    .into(holder.imgProduto);

                        }
                    });
        }else{
            Picasso.with(mContext)
                    .load(com.meuvesti.cliente.R.mipmap.ic_launcher)
                    .resize(0, 650)
                    .into(holder.imgProduto);
        }
        holder.btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.setDelete(true);

                final String productId = (String) v.getTag(com.meuvesti.cliente.R.id.idProduct);
                final View convertView = (View) v.getTag(com.meuvesti.cliente.R.id.convertView);

                mActivity.setIdProductToDelete(productId);

                //CartRealmService.removeFromCart(productId);
                //mActivity.atualizaDados();

                Snackbar snack = Snackbar.make(mActivity.getView(), mContext.getString(com.meuvesti.cliente.R.string.produto_removido), Snackbar.LENGTH_LONG)
                        .setAction(mContext.getString(com.meuvesti.cliente.R.string.desfazer), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mActivity.setDelete(false);
                                convertView.setVisibility(View.VISIBLE);
                            }
                        });
                snack.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                        if (mActivity.getDelete()) {

                            CartRealmService.removeFromCart(productId);
                            mActivity.atualizaDados(false);
                            mActivity.setDelete(false);

                        }
                    }
                });
                snack.setActionTextColor(mActivity.getColorResource(com.meuvesti.cliente.R.color.textColor));
                snack.show();

                Animation anim = AnimationUtils.loadAnimation(mContext, com.meuvesti.cliente.R.anim.slide_out_left);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        convertView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                convertView.startAnimation(anim);

            }
        });

        holder.body.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailProduct(produto);
            }
        });

    }

    private void detailProduct(ProductRealm produto) {
        Intent intent = new Intent(mContext, ProdutoActivity.class);
        ItemProduct item = new ItemProduct();
        item.setId(produto.getId());
        item.setCodigo(produto.getCodigo());
        item.setValor(produto.getValor());
        item.setBrandName(produto.getMarca());
        item.setNome(produto.getNome());
        List<Photo> listaImagem = new ArrayList<Photo>();
        for (PhotoRealm ph : produto.getImagem()) {
            Photo photo = new Photo();
            photo.setUrlLarge(ph.getUrl());
            photo.setUrl(ph.getUrl());
            photo.setUrlSmall(ph.getUrl());
            listaImagem.add(photo);
        }
        item.setImagem(listaImagem);
        intent.putExtra(ItemProduct.ITEM_PRODUCT, item);
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public View mContainer;
        public ImageView imgProduto;
        public TextView txtProduto;
        public TextView txtCodigo;
        public TextView txtValor;
        public TextView txtLabelPecas;
        public TextView textLabelTotal;
        public TextView txtValorTotal;
        public ImageButton btnExcluir;
        public TextView txtPecas;
        public RelativeLayout esgotado;
        public TextView textEsgotado;
        public View body;
        public View space;

        public ItemViewHolder(View convertView) {
            super(convertView);
            mContainer = convertView;
            imgProduto = (ImageView) convertView.findViewById(com.meuvesti.cliente.R.id.imgProduto);
            txtProduto = (TextView) convertView.findViewById(com.meuvesti.cliente.R.id.txtProduto);
            txtCodigo = (TextView) convertView.findViewById(com.meuvesti.cliente.R.id.txtCodigo);
            txtValor = (TextView) convertView.findViewById(com.meuvesti.cliente.R.id.txtValor);
            txtLabelPecas = (TextView) convertView.findViewById(com.meuvesti.cliente.R.id.textLabelPecas);
            textLabelTotal = (TextView) convertView.findViewById(com.meuvesti.cliente.R.id.textLabelTotal);
            txtPecas = (TextView) convertView.findViewById(com.meuvesti.cliente.R.id.txtTotalPecas);
            txtValorTotal = (TextView) convertView.findViewById(com.meuvesti.cliente.R.id.txtValorTotal);
            btnExcluir = (ImageButton) convertView.findViewById(com.meuvesti.cliente.R.id.btnExcluir);
            esgotado = (RelativeLayout) convertView.findViewById(com.meuvesti.cliente.R.id.esgotado);
            textEsgotado = (TextView) convertView.findViewById(com.meuvesti.cliente.R.id.textEsgotado);
            body = convertView.findViewById(com.meuvesti.cliente.R.id.container_product_body);
            space = convertView.findViewById(R.id.space);
        }
    }

}