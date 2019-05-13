package com.meuvesti.cliente.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Size;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.meuvesti.cliente.FontsOverride;
import com.meuvesti.cliente.R;
import com.meuvesti.cliente.adapter.CarrinhoRecycleAdapter;
import com.meuvesti.cliente.model.Color;
import com.meuvesti.cliente.model.Iten;
import com.meuvesti.cliente.model.ItenRequest;
import com.meuvesti.cliente.model.ProductDetail;
import com.meuvesti.cliente.model.QuotesDetail;
import com.meuvesti.cliente.model.QuotesDetailRequest;
import com.meuvesti.cliente.model.SizeItem;
import com.meuvesti.cliente.model.SizeItemRequest;
import com.meuvesti.cliente.model.VendaOrcamento;
import com.meuvesti.cliente.model.VendaOrcamentoRequest;
import com.meuvesti.cliente.model.VendaResponse;
import com.meuvesti.cliente.realm.CartRealmService;
import com.meuvesti.cliente.realm.PackItemRealm;
import com.meuvesti.cliente.realm.PackMapRealm;
import com.meuvesti.cliente.realm.PackRealm;
import com.meuvesti.cliente.realm.ProductRealm;
import com.meuvesti.cliente.realm.RealmService;
import com.meuvesti.cliente.realm.SizeItemRealm;
import com.meuvesti.cliente.realm.UsuarioRealm;
import com.meuvesti.cliente.service.VestiAPI;
import com.meuvesti.cliente.utils.Globals;
import com.meuvesti.cliente.utils.Utils;
import com.meuvesti.cliente.utils.VestiActionBar;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarrinhoActivity extends AppCompatActivity {

    public static final String REQUEST_CARRINHO = "request_carrinho";
    public static final String FINISH_BUY = "finish_buy";
    TextView obsTextView, txtValorTotal, txtTotalPecas, txtSemItem;
    CarrinhoRecycleAdapter adapter;
    RecyclerView rcvCarrinho;
    private UsuarioRealm mUsuario;
    private RealmResults<ProductRealm> listaProdutos;
    private VestiActionBar mVestiActionBar;
    private EditText edObservacoes;
    private Context mContext;
    private boolean mDeleteOnAdapter = false;
    private String idProductToDelete;
    private Bundle mSavedInstanceState;
    private boolean mIsFinishBuy = false;

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra(FINISH_BUY)) {
            mIsFinishBuy = getIntent().getBooleanExtra(FINISH_BUY, false);
        }

        mSavedInstanceState = savedInstanceState;

        mContext = CarrinhoActivity.this;
        mUsuario = RealmService.getUsuarioLogado(this);

        Typeface fontFuturaMedium = FontsOverride.fontMedium(getAssets());
        Typeface fontFuturaHeavy = FontsOverride.fontHeavy(getAssets());
        Typeface fontFuturaBook = FontsOverride.fontBook(getAssets());

        mVestiActionBar = new VestiActionBar(this);
        mVestiActionBar.setContentView(R.layout.activity_carrinho);
        mVestiActionBar.setTitle(getString(R.string.menu_carrinho));
        setContentView(mVestiActionBar.getMainContent());

        mVestiActionBar.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getDelete()) {
                    try {
                        if (CartRealmService.hasProduct(getIdProductToDelete())) {
                            CartRealmService.removeFromCart(getIdProductToDelete());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                finish();
            }
        });

        listaProdutos = CartRealmService.getProductsInCart();

        rcvCarrinho = (RecyclerView) findViewById(R.id.lstCarrinho);

        txtSemItem = (TextView) findViewById(R.id.textSemItens);
        txtSemItem.setTypeface(fontFuturaMedium);

        TextView labelResumo = (TextView) findViewById(R.id.labelResumo);
        labelResumo.setTypeface(fontFuturaMedium);

        TextView textLabelTotal = (TextView) findViewById(R.id.textLabelTotal);
        textLabelTotal.setTypeface(fontFuturaHeavy);

        txtValorTotal = (TextView) findViewById(R.id.txtValorTotal);
        txtValorTotal.setTypeface(fontFuturaMedium);

        TextView textLabelTotalPecas = (TextView) findViewById(R.id.textLabelTotalPecas);
        textLabelTotalPecas.setTypeface(fontFuturaHeavy);

        txtTotalPecas = (TextView) findViewById(R.id.txtTotalPecas);
        txtTotalPecas.setTypeface(fontFuturaMedium);

        TextView observacoes = (TextView) findViewById(R.id.txtTotalPecas);
        observacoes.setTypeface(fontFuturaHeavy);

        edObservacoes = (EditText) findViewById(R.id.edObservacoes);
        edObservacoes.setTypeface(fontFuturaBook);
        obsOnTextChanged();


        atualizaDados(true);
        verifyProductsIsSell2();

        findViewById(R.id.btnCarrinho).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(getBaseContext(), R.anim.scale);
                v.startAnimation(anim);
                submit();
            }
        });

        if (mIsFinishBuy) {
            submit();
        }
    }

    /*private void verifyProductsIsSell() {
        if (mUsuario != null || !mUsuario.getLogado()) {
            callProductIsValid(getVendaOrcamento());
        }
    }

    private void callProductIsValid(final VendaOrcamento venda) {
        try {
            VestiAPI.get().api(mContext).validateProduct(venda).enqueue(new Callback<ValidateError>() {
                @Override
                public void onResponse(Call<ValidateError> call, Response<ValidateError> response) {
                    try {
                        if (response != null && response.body() != null) {
                            if (response.body().getResult().isSuccess() == false) {

                                if (response.body().getErro() != null) {
                                    for (Map.Entry<String, Status> item : response.body().getErro().getStatus().entrySet()) {
                                        String id = item.getKey();
                                        desativar(CartRealmService.getProductById(id));
                                    }
                                }
                            }
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ValidateError> call, Throwable t) {
                    if (t instanceof UnknownHostException) {
                        Toast.makeText(mContext, getString(R.string.internet_error), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }*/

    private void verifyProductsIsSell2() {
        for (ProductRealm product : listaProdutos) {
            callProductIsSell(product);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            View v = getCurrentFocus();

            if (v != null &&
                    (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                    v instanceof EditText &&
                    !v.getClass().getName().startsWith("android.webkit.")) {
                int scrcoords[] = new int[2];
                v.getLocationOnScreen(scrcoords);
                float x = ev.getRawX() + v.getLeft() - scrcoords[0];
                float y = ev.getRawY() + v.getTop() - scrcoords[1];

                if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                    hideKeyboard(this);
            }
            return super.dispatchTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void callProductIsSell(final ProductRealm product) {
        findViewById(R.id.box_loading).setVisibility(View.VISIBLE);
        VestiAPI.get().api(mContext).detailProduct(product.getId(), Globals.SCHEME_NAME).enqueue(new Callback<ProductDetail>() {
            @Override
            public void onResponse(Call<ProductDetail> call, Response<ProductDetail> response) {
                if (response != null && response.body().getResult().isSuccess()) {
                    ProductDetail pdi = response.body();
                    if (pdi.getProduct().isAtivo() == false) {
                        desativar(product);
                    }
                } else {
                    desativar(product);
                }
                findViewById(R.id.box_loading).setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ProductDetail> call, Throwable t) {
                if (t instanceof UnknownHostException) {
                    Toast.makeText(mContext, getString(R.string.internet_error), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
                }
                findViewById(R.id.box_loading).setVisibility(View.GONE);
            }
        });
    }

    private void desativar(ProductRealm product) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        ProductRealm pr = CartRealmService.getProductById(product.getId());
        pr.setAtivo(false);

        realm.commitTransaction();
        realm.close();
        atualizaDados(true);
    }

    private void obsOnTextChanged() {
        edObservacoes.setText(Utils.getObs(getBaseContext()));
        TextWatcher redoWatcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Utils.setObs(getBaseContext(), edObservacoes.getText().toString());
            }
        };
        edObservacoes.addTextChangedListener(redoWatcher);
    }

    private void submit() {
        findViewById(R.id.box_loading).setVisibility(View.VISIBLE);
        findViewById(R.id.scroll).setVisibility(View.GONE);

        if (!Utils.isNetworkAvailable(CarrinhoActivity.this)) {
            Toast.makeText(CarrinhoActivity.this, getString(R.string.internet_error), Toast.LENGTH_LONG).show();
            return;
        }

        if (mUsuario == null) {
            Toast.makeText(CarrinhoActivity.this, getString(R.string.requer_login), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(CarrinhoActivity.this, LoginActivity.class);
            intent.putExtra(REQUEST_CARRINHO, true);
            startActivity(intent);
            finish();
        } else {
            callServiceBuy();
        }
    }

    private void callServiceBuy() {
        VestiAPI.get().api(mContext).quotes(getVendaOrcamentoRequest()).enqueue(new Callback<VendaResponse>() {
            @Override
            public void onResponse(Call<VendaResponse> call, Response<VendaResponse> response) {
                if (response != null && response.body() != null) {
                    Toast.makeText(mContext, response.body().getResult().getMessage(), Toast.LENGTH_LONG).show();

                    CartRealmService.clear();
                    Utils.setObs(mContext, "");
                    Utils.setSellerid(mContext, null);
                    Utils.saveWhatsAppLink(mContext, null);

                    mVestiActionBar.setOnCloseClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    });

                    findViewById(R.id.box_loading).setVisibility(View.GONE);
                    findViewById(R.id.box_message).setVisibility(View.VISIBLE);
                    findViewById(R.id.box_finalizar).setVisibility(View.GONE);
                    mVestiActionBar.setTitle(getString(R.string.menu_compra_efetuada));
                }
            }

            @Override
            public void onFailure(Call<VendaResponse> call, Throwable t) {
                if (t instanceof UnknownHostException) {
                    Toast.makeText(mContext, getString(R.string.internet_error), Toast.LENGTH_LONG).show();
                    mVestiActionBar.showNetworkError(true);
                } else {
                    Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
                }

                findViewById(R.id.box_loading).setVisibility(View.GONE);
                findViewById(R.id.box_message).setVisibility(View.GONE);
                findViewById(R.id.scroll).setVisibility(View.VISIBLE);
                findViewById(R.id.box_finalizar).setVisibility(View.VISIBLE);
            }
        });
    }

    public void atualizaDados(final boolean b) {
        getVendaOrcamento();
        listaProdutos = CartRealmService.getProductsInCart();
        if (listaProdutos.size() == 0) {
            txtSemItem.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fadein));
            Animation fadeIn = AnimationUtils.loadAnimation(mContext, R.anim.fadein);
            txtSemItem.startAnimation(fadeIn);
            fadeIn.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    txtSemItem.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            findViewById(R.id.box_content).setVisibility(View.GONE);
        } else {
            txtSemItem.setVisibility(View.GONE);
            final View box = findViewById(R.id.box_content);
            box.setVisibility(View.VISIBLE);
            Animation fadeIn = AnimationUtils.loadAnimation(mContext, R.anim.fadein);
            box.startAnimation(fadeIn);
            fadeIn.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    box.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

        if (listaProdutos.size() > 0) {
            if (txtSemItem != null)
                txtSemItem.setVisibility(View.GONE);
            edObservacoes.setText(Utils.getObs(getBaseContext()));
            int totalItems = getTotalPecas(listaProdutos);
            txtTotalPecas.setText(String.valueOf(totalItems));
            double totalValor = getTotalPrice(listaProdutos);
            txtValorTotal.setText(String.format("R$ %.2f", totalValor));

            final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            adapter = new CarrinhoRecycleAdapter(CarrinhoActivity.this, listaProdutos);
            rcvCarrinho.setAdapter(adapter);
            rcvCarrinho.setLayoutManager(layoutManager);
            rcvCarrinho.post(new Runnable() {
                @Override
                public void run() {
                    if (b) {
                        ScrollView scrollView = (ScrollView) findViewById(R.id.scroll);
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                }
            });

        } else {
            if (txtSemItem != null)
                txtSemItem.setVisibility(View.VISIBLE);
            txtTotalPecas.setText(String.valueOf(0));
            txtValorTotal.setText(String.format("R$ %.2f", 0.0));
        }
    }

    public int getTotalPecas(RealmResults<ProductRealm> listaProdutos) {
        int valor = 0;
        for (ProductRealm pr : listaProdutos) {
            if (pr.isAtivo()) {
                int quantity = CartRealmService.totalByProduct(pr);
                valor += quantity;
            }
        }
        return valor;
    }

    public double getTotalPrice(RealmResults<ProductRealm> listaProdutos) {
        double valor = 0;
        for (ProductRealm pr : listaProdutos) {
            if (pr.isAtivo()) {
                int quantity = CartRealmService.totalByProduct(pr);
                valor += quantity * pr.getValor();
            }
        }
        return valor;
    }

    public View getView() {
        return mVestiActionBar.getMainContent();
    }

    public int getColorResource(int colorId) {
        return ContextCompat.getColor(this, colorId);
    }

    public VendaOrcamento getVendaOrcamento() {

        VendaOrcamento vendaOrcamento = new VendaOrcamento();
        vendaOrcamento.setObs(Utils.getObs(mContext));
        vendaOrcamento.setSellerId(Utils.getSellerid(mContext));
        vendaOrcamento.setSchemeUrl(Globals.SCHEME_NAME);

        List<QuotesDetail> quotes = new ArrayList<>();

        for (ProductRealm produto : listaProdutos) {
            if (produto.isAtivo()) {
                for (PackRealm pack : produto.getPacks()) {
                    if (produto.getPtype().equalsIgnoreCase("3") || produto.getPtype().equalsIgnoreCase("4")) {
                        int quantityPack = 0;
                        List<SizeItem> listMap = new ArrayList<>();
                        List<Iten> itens = new ArrayList<>();

                        for (PackItemRealm itemPack : pack.getItens()) {

                            for (SizeItemRealm itemPackSize : itemPack.getSizes()) {
                                SizeItem sizeItem = new SizeItem();
                                sizeItem.setKey(itemPackSize.getKey());
                                sizeItem.setQuantity(itemPackSize.getQuantity());
                                sizeItem.setSell(itemPackSize.isSell());
                                listMap.add(sizeItem);
                                quantityPack = itemPack.getQuantityPacks();
                            }

                            Iten iten = new Iten();
                            iten.setColor(new Color(itemPack.getColor().getId()));
                            iten.setSizes(listMap);
                            itens.add(iten);
                        }

                        QuotesDetail quotesDetail = new QuotesDetail();
                        quotesDetail.setItens(itens);
                        quotesDetail.setProductId(produto.getId());
                        quotesDetail.setQtde(quantityPack);
                        quotesDetail.setUnitPrice(String.valueOf(produto.getValor()));
                        quotes.add(quotesDetail);
                    } else {

                        if (produto.getPtype().equalsIgnoreCase("1") || produto.getPtype().equalsIgnoreCase("2")) {

                            List<Iten> listIten = new ArrayList<>();

                            for (PackItemRealm itemPack : pack.getItens()) {
                                List<SizeItem> sizeItems = new ArrayList<>();
                                for (SizeItemRealm sizeMap : itemPack.getSizes()) {
                                    String key = sizeMap.getKey();
                                    int quantity = sizeMap.getQuantity();
                                    boolean sell = sizeMap.isSell();
                                    SizeItem sizeItem = new SizeItem();
                                    sizeItem.setSell(sell);
                                    sizeItem.setQuantity(quantity);
                                    sizeItem.setKey(key);
                                    sizeItems.add(sizeItem);
                                }

                                Iten iten = new Iten();
                                iten.setSizes(sizeItems);
                                iten.setColor(new Color(itemPack.getColor().getId()));
                                listIten.add(iten);
                            }

                            QuotesDetail quotesDetail = new QuotesDetail();
                            quotesDetail.setItens(listIten);
                            quotesDetail.setProductId(produto.getId());
                            quotesDetail.setQtde(1);
                            quotesDetail.setUnitPrice(String.valueOf(produto.getValor()));
                            quotes.add(quotesDetail);

                        } else {
                            int quantityPack = 1;
                            List<SizeItem> listMap = new ArrayList<>();
                            List<Iten> listIten = new ArrayList<>();

                            for (PackItemRealm itemPack : pack.getItens()) {

                                for (SizeItemRealm sizeMap : itemPack.getSizes()) {
                                    SizeItem item = new SizeItem();
                                    String key = sizeMap.getKey();

                                    int quantity = sizeMap.getQuantity();
                                    item.setKey(key);
                                    item.setQuantity(quantity);
                                    item.setSell(sizeMap.isSell());
                                    listMap.add(item);
                                }

                                Iten iten = new Iten();
                                iten.setSizes(listMap);
                                iten.setColor(new Color(itemPack.getColor().getId()));
                                listIten.add(iten);
                            }

                            QuotesDetail quotesDetail = new QuotesDetail();
                            quotesDetail.setItens(listIten);
                            quotesDetail.setProductId(produto.getId());
                            quotesDetail.setQtde(quantityPack);
                            quotesDetail.setUnitPrice(String.valueOf(produto.getValor()));
                            quotes.add(quotesDetail);
                        }
                    }
                }
            }
        }

        vendaOrcamento.setQuotesDetails(quotes);
        if (Utils.getSellerid(mContext) != null) {
            vendaOrcamento.setSellerId(Utils.getSellerid(mContext));
        }

        return vendaOrcamento;
    }

    public VendaOrcamentoRequest getVendaOrcamentoRequest() {

        VendaOrcamentoRequest vendaOrcamento = new VendaOrcamentoRequest();
        vendaOrcamento.setObs(Utils.getObs(mContext));
        vendaOrcamento.setSellerId(Utils.getSellerid(mContext));
        vendaOrcamento.setSchemeUrl(Globals.SCHEME_NAME);

        List<QuotesDetailRequest> quotes = new ArrayList<>();

        for (ProductRealm produto : listaProdutos) {
            if (produto.isAtivo()) {
                for (PackRealm pack : produto.getPacks()) {
                    if (produto.getPtype().equalsIgnoreCase("3") || produto.getPtype().equalsIgnoreCase("4")) {
                        int quantityPack = 0;
                        List<SizeItemRequest> listMap = new ArrayList<>();
                        List<ItenRequest> itens = new ArrayList<>();

                        for (PackItemRealm itemPack : pack.getItens()) {

                            for (SizeItemRealm itemPackSize : itemPack.getSizes()) {
                                SizeItemRequest sizeItem = new SizeItemRequest();
                                sizeItem.setKey(itemPackSize.getKey());
                                sizeItem.setQuantity(itemPackSize.getQuantity());
                                listMap.add(sizeItem);
                                quantityPack = itemPack.getQuantityPacks();
                            }

                            ItenRequest iten = new ItenRequest();
                            iten.setColor(new Color(itemPack.getColor().getId()));
                            iten.setSizes(listMap);
                            itens.add(iten);
                        }

                        QuotesDetailRequest quotesDetail = new QuotesDetailRequest();
                        quotesDetail.setItens(itens);
                        quotesDetail.setProductId(produto.getId());
                        quotesDetail.setQtde(quantityPack);
                        quotesDetail.setUnitPrice(String.valueOf(produto.getValor()));
                        quotes.add(quotesDetail);
                    } else {

                        if (produto.getPtype().equalsIgnoreCase("1") || produto.getPtype().equalsIgnoreCase("2")) {

                            List<ItenRequest> listIten = new ArrayList<>();

                            for (PackItemRealm itemPack : pack.getItens()) {
                                List<SizeItemRequest> sizeItems = new ArrayList<>();
                                for (SizeItemRealm sizeMap : itemPack.getSizes()) {
                                    String key = sizeMap.getKey();
                                    int quantity = sizeMap.getQuantity();
                                    boolean sell = sizeMap.isSell();
                                    SizeItemRequest sizeItem = new SizeItemRequest();
                                    sizeItem.setQuantity(quantity);
                                    sizeItem.setKey(key);
                                    sizeItems.add(sizeItem);
                                }

                                ItenRequest iten = new ItenRequest();
                                iten.setSizes(sizeItems);
                                iten.setColor(new Color(itemPack.getColor().getId()));
                                listIten.add(iten);
                            }

                            QuotesDetailRequest quotesDetail = new QuotesDetailRequest();
                            quotesDetail.setItens(listIten);
                            quotesDetail.setProductId(produto.getId());
                            quotesDetail.setQtde(1);
                            quotesDetail.setUnitPrice(String.valueOf(produto.getValor()));
                            quotes.add(quotesDetail);

                        } else {
                            int quantityPack = 1;
                            List<SizeItemRequest> listMap = new ArrayList<>();
                            List<ItenRequest> listIten = new ArrayList<>();

                            for (PackItemRealm itemPack : pack.getItens()) {

                                for (SizeItemRealm sizeMap : itemPack.getSizes()) {
                                    SizeItemRequest item = new SizeItemRequest();
                                    String key = sizeMap.getKey();

                                    int quantity = sizeMap.getQuantity();
                                    item.setKey(key);
                                    item.setQuantity(quantity);
                                    listMap.add(item);
                                }

                                ItenRequest iten = new ItenRequest();
                                iten.setSizes(listMap);
                                iten.setColor(new Color(itemPack.getColor().getId()));
                                listIten.add(iten);
                            }

                            QuotesDetailRequest quotesDetail = new QuotesDetailRequest();
                            quotesDetail.setItens(listIten);
                            quotesDetail.setProductId(produto.getId());
                            quotesDetail.setQtde(quantityPack);
                            quotesDetail.setUnitPrice(String.valueOf(produto.getValor()));
                            quotes.add(quotesDetail);
                        }
                    }
                }
            }
        }

        vendaOrcamento.setQuotesDetails(quotes);
        if (Utils.getSellerid(mContext) != null) {
            vendaOrcamento.setSellerId(Utils.getSellerid(mContext));
        }

        return vendaOrcamento;
    }

    public boolean getDelete() {
        return mDeleteOnAdapter;
    }

    public void setDelete(boolean flag) {
        mDeleteOnAdapter = flag;
    }

    public String getIdProductToDelete() {
        return idProductToDelete;
    }

    public void setIdProductToDelete(String id) {
        idProductToDelete = id;
    }
}