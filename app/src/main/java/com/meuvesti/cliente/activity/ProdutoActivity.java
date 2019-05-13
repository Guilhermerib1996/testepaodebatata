package com.meuvesti.cliente.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.JustifyContent;
import com.meuvesti.cliente.FontsOverride;
import com.meuvesti.cliente.R;
import com.meuvesti.cliente.adapter.ProdutoBannerAdapter;
import com.meuvesti.cliente.model.Image;
import com.meuvesti.cliente.model.ItemProduct;
import com.meuvesti.cliente.model.Photo;
import com.meuvesti.cliente.model.ProductDetail;
import com.meuvesti.cliente.realm.CartRealmService;
import com.meuvesti.cliente.realm.ColorRealm;
import com.meuvesti.cliente.realm.PackItemRealm;
import com.meuvesti.cliente.realm.PackRealm;
import com.meuvesti.cliente.realm.ProductRealm;
import com.meuvesti.cliente.realm.SizeItemRealm;
import com.meuvesti.cliente.realm.UsuarioRealm;
import com.meuvesti.cliente.realm.RealmService;
import com.meuvesti.cliente.service.VestiAPI;
import com.meuvesti.cliente.utils.CircleTransform;
import com.meuvesti.cliente.utils.DownloadFile;
import com.meuvesti.cliente.utils.Globals;
import com.meuvesti.cliente.utils.PicassoBigCache;
import com.meuvesti.cliente.utils.Utils;
import com.meuvesti.cliente.utils.VestiActionBar;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tooltip.Tooltip;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hersonrodrigues on 06/02/17.
 */
public class ProdutoActivity extends AppCompatActivity {

    private final static int LEFT = 10;
    private final static int TOP = 10;
    private final static int RIGHT = 10;
    private final static int BOTTOM = 10;

    private static final String SEM_COR = "Sem Cor";

    private static final int VIBRATE_LONG = 50;
    //private static final int VIBRATE_SHORT = 25;

    private static final long TIME_TOOLTIP_SHOWING = 5000;
    private static final int RC_WRITE_EXTERNAL = 1212;

    private VestiActionBar mVestiActionBar;
    private Context mContext;
    private boolean mLongClick = false;
    private ProductRealm mProduct;
    private boolean mDisableColorLabel = false;
    private boolean mTooltipHasShow = false;
    private Tooltip mTip;
    private int mTooltipViewId = -1;
    private int mTime;
    private ItemProduct itemProduct;

    @SuppressLint({"WrongViewCast", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = ProdutoActivity.this;

        if (savedInstanceState == null) {
            this.overridePendingTransition(
                    R.anim.slide_in_left,
                    R.anim.slide_out_left);
        }

        mVestiActionBar = new VestiActionBar(this);
        mVestiActionBar.setContentView(R.layout.activity_produto);;

        mVestiActionBar.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mVestiActionBar.setOnCartClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(mContext, CarrinhoActivity.class));
            }
        });

        setContentView(mVestiActionBar.getMainContent());

        init();
    }

    private void methodRequiresPermission() {
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(ProdutoActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ProdutoActivity.this, permission)) {
                ActivityCompat.requestPermissions(ProdutoActivity.this, new String[]{permission}, RC_WRITE_EXTERNAL);
            } else {
                ActivityCompat.requestPermissions(ProdutoActivity.this, new String[]{permission}, RC_WRITE_EXTERNAL);
            }
        } else {
            tryShareLink(new Loader.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(Loader loader, Object data) {
                    ArrayList<Uri> imageUriArray = (ArrayList<Uri>) data;
                    if (imageUriArray == null || imageUriArray.isEmpty()) {
                        Toast.makeText(mContext, "Não foi possível preparar as imagens", Toast.LENGTH_SHORT).show();
                    } else {
                        callIntentSharedWhatsApp(imageUriArray);
                    }
                }
            });
        }
    }

    private void callIntentSharedWhatsApp(final ArrayList<Uri> imageUriArray) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                String marca = "";
                if (itemProduct.getBrandName() != null) {
                    marca = itemProduct.getBrandName();
                }
                String productName = "";
                if (itemProduct.getNome() != null) {
                    productName = itemProduct.getNome();
                }
                intent.putExtra(Intent.EXTRA_TEXT, marca + " - " + productName);
                intent.setType("text/plain");
                intent.setPackage("com.whatsapp");
                if (imageUriArray != null) {
                    intent.setType("image/jpeg");
                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUriArray);
                }
                startActivity(intent);
            }
        });
    }

    private void tryShareLink(final Loader.OnLoadCompleteListener onLoadCompleteListener) {
        if (itemProduct.existImages()) {
            final List<String> listUrl = new ArrayList<>();
            for (String url : itemProduct.getCorrectImages()) {
                listUrl.add(url);
            }
            try {
                new DownloadFile(ProdutoActivity.this, listUrl, new Loader.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(Loader loader, Object data) {
                        final ArrayList<Uri> imageUriArray = (ArrayList<Uri>) data;
                        onLoadCompleteListener.onLoadComplete(null, imageUriArray);
                    }
                }).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(mContext, "Sem imagem para exibir", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case RC_WRITE_EXTERNAL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    methodRequiresPermission();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.sem_permissao), Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void init() {

        findViewById(R.id.box_loading).setVisibility(View.VISIBLE);
        itemProduct = (ItemProduct) getIntent().getExtras().getSerializable(ItemProduct.ITEM_PRODUCT);
        configureSlidePage(itemProduct);
        configureCart();
        VestiAPI.get().api(mContext).detailProduct(itemProduct.getId(), Globals.SCHEME_NAME).enqueue(new Callback<ProductDetail>() {
            @Override
            public void onResponse(Call<ProductDetail> call, Response<ProductDetail> response) {
                findViewById(R.id.box_loading).setVisibility(View.GONE);
                if (response != null && response.body().getResult().isSuccess()) {
                    ProductDetail pdi = response.body();
                    Log.e("PTYPE", pdi.getProduct().getPtype());
                    if (pdi.getProduct().isStockout()) {
                        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.fadein);
                        View textEsgotado = findViewById(R.id.textProdutoEsgoto);
                        textEsgotado.setAnimation(anim);
                        textEsgotado.setVisibility(View.VISIBLE);
                    } else {
                        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.fadeout);
                        View textEsgotado = findViewById(R.id.textProdutoEsgoto);
                        textEsgotado.setAnimation(anim);
                        textEsgotado.setVisibility(View.GONE);
                    }
                    if (CartRealmService.hasProduct(pdi.getProduct().getId())) {
                        mProduct = CartRealmService.getProductById(pdi.getProduct().getId());
                        buttonAddOrEdit();
                    } else {
                        mProduct = CartRealmService.objectToRealm(itemProduct, pdi.getProduct());
                    }
                    renderProduct(pdi);
                } else {
                    findViewById(R.id.textProdutoInexistente).setVisibility(View.VISIBLE);
                    findViewById(R.id.scroll).setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ProductDetail> call, Throwable t) {
                findViewById(R.id.box_loading).setVisibility(View.GONE);
                if (t instanceof UnknownHostException) {
                    Toast.makeText(mContext, getString(R.string.internet_error), Toast.LENGTH_LONG).show();
                    mVestiActionBar.showNetworkError(true);
                } else {
                    Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void buttonAddOrEdit() {
        Button addCartButton = (Button) findViewById(R.id.button);
        if (mProduct != null && CartRealmService.hasProduct(mProduct.getId())) {
            addCartButton.setText(getString(R.string.atualizar_carrinho));
        } else {
            addCartButton.setText(getString(R.string.adicionar_carrinho));
        }
    }

    private void configureSlidePage(ItemProduct itemProduct) {
        final ViewPager pagerView = (ViewPager) findViewById(R.id.pager);
        List<String> fotos = new ArrayList<>();
        List<String> fotosHd = new ArrayList<>();
        if (itemProduct.getImages() != null && !itemProduct.getImages().isEmpty()) {
            fotos = Image.toStringList(itemProduct.getImages(), Image.LARGE);
            fotosHd = Image.toStringList(itemProduct.getImages(), Image.ORIGINAL);
        } else if (itemProduct.getImagem() != null && !itemProduct.getImagem().isEmpty()) {
            for (Photo foto : itemProduct.getImagem()) {
                fotos.add(foto.getUrl());
                fotosHd.add(foto.getUrl());
            }
        }
        preDownload(fotosHd);
        pagerView.setAdapter(new ProdutoBannerAdapter(mContext, fotos, fotosHd));
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(pagerView, true);
        if (fotos.size() <= 1) {
            tabLayout.setVisibility(View.GONE);
        }
    }

    private void preDownload(List<String> fotosHd) {
        mTime = 500;
        for (final String imagem : fotosHd) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mTime = mTime + 500;

                    PicassoBigCache.INSTANCE.getPicassoBigCache(mContext)
                            .load(imagem)
                            .into(new ImageView(mContext));
                }
            }, mTime);
        }
    }


    private void renderProduct(final ProductDetail pdi) {
        mVestiActionBar.setTitle(mProduct.getNome());

        Typeface fontFuturaMedium = FontsOverride.fontMedium(getAssets());
        Typeface fontFuturaHeavy = FontsOverride.fontHeavy(getAssets());
        Typeface fontFuturaBook = FontsOverride.fontBook(getAssets());

        TextView titleView = (TextView) findViewById(R.id.title);
        titleView.setText(mProduct.getMarca());
        titleView.setTypeface(fontFuturaHeavy);

        TextView labelReference = (TextView) findViewById(R.id.labelReferencia);
        labelReference.setTypeface(fontFuturaHeavy);

        TextView descriptionView = (TextView) findViewById(R.id.description);
        descriptionView.setText(mProduct.getNome());
        descriptionView.setTypeface(fontFuturaMedium);

        TextView priceView = (TextView) findViewById(R.id.price);
        priceView.setText(String.format("R$ %.2f", mProduct.getValor()));
        priceView.setTypeface(fontFuturaHeavy);

        TextView textView = (TextView) findViewById(R.id.text);
        textView.setText(mProduct.getDescricao());
        priceView.setTypeface(fontFuturaMedium);

        TextView codeView = (TextView) findViewById(R.id.code);
        codeView.setText(mProduct.getCodigo());
        codeView.setTypeface(fontFuturaBook);

        TextView labelCompose = (TextView) findViewById(R.id.labelCompose);
        labelCompose.setTypeface(fontFuturaHeavy);

        TextView composeView = (TextView) findViewById(R.id.compose);
        composeView.setText(mProduct.getComposicao());
        composeView.setTypeface(fontFuturaBook);

        TextView labelResumo = (TextView) findViewById(R.id.labelResumo);
        labelResumo.setTypeface(fontFuturaMedium);

        TextView labelInformacoes = (TextView) findViewById(R.id.labelInformacoes);
        labelInformacoes.setTypeface(fontFuturaMedium);

        TextView labelPiece = (TextView) findViewById(R.id.labelPiece);
        labelPiece.setTypeface(fontFuturaHeavy);

        TextView labelPiece2 = (TextView) findViewById(R.id.labelPiece2);
        labelPiece2.setTypeface(fontFuturaMedium);

        TextView labelTotal = (TextView) findViewById(R.id.labelTotal);
        labelTotal.setTypeface(fontFuturaHeavy);

        TextView labelCores = (TextView) findViewById(R.id.labelCores);
        labelCores.setTypeface(fontFuturaHeavy);

        View shared = findViewById(R.id.shared);
        shared.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mProduct.getImagem().isEmpty()) {
                    methodRequiresPermission();
                } else {
                    Toast.makeText(mContext, getString(R.string.sem_foto), Toast.LENGTH_LONG).show();
                }
            }
        });



        Button buttonView = (Button) findViewById(R.id.button);
        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation anim = AnimationUtils.loadAnimation(getBaseContext(), R.anim.scale);
                view.startAnimation(anim);

                if (pdi.getProduct().isStockout()) {
                    Toast.makeText(mContext, getString(R.string.produto_fora_estoque), Toast.LENGTH_SHORT).show();
                } else {
                    CartRealmService.addToCart(mProduct);

                    if (CartRealmService.hasProduct(mProduct.getId())) {
                        int total = CartRealmService.totalByProduct(mProduct);
                        if (total >= 1) {
                            Toast.makeText(mContext, getString(R.string.produto_atualizado), Toast.LENGTH_SHORT).show();
                        } else {
                            calcTotalAndRemoveEqualZero();
                            Toast.makeText(mContext, getString(R.string.selecione_quantidade), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mContext, getString(R.string.produto_adicionado_carrinho), Toast.LENGTH_SHORT).show();
                    }

                    buttonAddOrEdit();
                    configureCart();
                }
            }
        });

        LinearLayout grid = (LinearLayout) findViewById(R.id.grid);
        for (PackRealm pack : mProduct.getPacks()) {
            grid.addView(createPackLine(pack));
        }

        generatePackColors();

        calculaTotal();

        final ScrollView scrollView = (ScrollView) findViewById(R.id.scroll);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, 0);
            }
        });
    }

    private void disableColorLabel() {
        LinearLayout container = (LinearLayout) findViewById(R.id.boxColor);
        container.setVisibility(View.GONE);
    }

    private void generatePackColors() {
        if (mDisableColorLabel == false) {
            LinearLayout container = (LinearLayout) findViewById(R.id.boxColor);

            HashMap<String, ColorRealm> map = new HashMap<>();
            if (mProduct.getColors() != null && !mProduct.getColors().isEmpty()) {
                for (ColorRealm itemColor : mProduct.getColors()) {
                    String colorId = itemColor.getId();
                    String color = itemColor.getCode();
                    String estampa = itemColor.getEstampa();
                    if (itemColor.getName() != null && !itemColor.getName().equals(SEM_COR) || estampa != null) {
                        map.put(colorId, itemColor);
                    }
                }
            }

            if (!map.entrySet().isEmpty()) {
                container.setVisibility(View.VISIBLE);
                FlexboxLayout flexboxLayout = (FlexboxLayout) findViewById(R.id.colors);
                for (final Map.Entry<String, ColorRealm> entry : map.entrySet()) {
                    if (entry.getValue().getEstampa() != null || (entry.getValue().getCode() != null && entry.getValue().getName() != null && !entry.getValue().getName().equals(SEM_COR))) {
                        final ImageView tv = new ImageView(mContext);
                        tv.setPadding(LEFT, TOP, RIGHT, BOTTOM);
                        //tv.setBackground(ContextCompat.getDrawable(mContext, R.drawable.shape_grid));

                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        tv.setLayoutParams(layoutParams);

                        tv.setMinimumWidth(100);
                        tv.setMinimumHeight(100);

                        // COR
                        if (entry.getValue().getCode() != null && (entry.getValue().getCode() != null && entry.getValue().getName() != null && !entry.getValue().getName().equals(SEM_COR))) {
                            tv.setColorFilter(Color.parseColor(entry.getValue().getCode()));
                            tv.setTag(R.id.tag_draw, entry.getValue().getCode());
                            final Drawable draw = ContextCompat.getDrawable(mContext, R.drawable.shape_grid_circle_color);
                            tv.setImageDrawable(draw);
                            tv.setColorFilter(Color.parseColor(entry.getValue().getCode()));

                            //ESTAMPA
                        } else {
                            if (entry.getValue().getEstampa() != null && !entry.getValue().getEstampa().isEmpty()) {
                                Picasso.with(mContext)
                                        .load(entry.getValue().getEstampa())
                                        .networkPolicy(NetworkPolicy.OFFLINE)
                                        .resize(80, 80)
                                        .transform(new CircleTransform())
                                        .into(tv, new com.squareup.picasso.Callback() {
                                            @Override
                                            public void onSuccess() {

                                            }

                                            @Override
                                            public void onError() {
                                                Picasso.with(mContext)
                                                        .load(entry.getValue().getEstampa())
                                                        .resize(80, 80)
                                                        .transform(new CircleTransform())
                                                        .into(tv);
                                            }
                                        });
                                tv.setTag(R.id.tag_estampa, entry.getValue().getEstampa());
                            }
                        }

                        tv.setTag(R.id.tag_color_name, entry.getValue().getName());
                        tv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String title = "";
                                String code = "";
                                String estampa = "";

                                try {
                                    title = (String) view.getTag(R.id.tag_color_name);
                                    code = (String) view.getTag(R.id.tag_draw);
                                    estampa = (String) view.getTag(R.id.tag_estampa);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                popupColorPreview(ProdutoActivity.this, title, code, estampa);
                            }
                        });

                        flexboxLayout.addView(tv);
                    }
                }
            } else {
                disableColorLabel();
            }
        } else {
            disableColorLabel();
        }
    }

    private void calcTotalAndRemoveEqualZero() {
        int total = CartRealmService.totalByProduct(mProduct);

        if (total == 0) {
            CartRealmService.removeFromCart(mProduct.getId());
            configureCart();
            buttonAddOrEdit();
        }
    }

    private View createPackLine(PackRealm pack) {
        LinearLayout container = getLinearLayout();

        for (int i = 0; i < pack.getItens().size(); i++) {
            View view = createGridPackLayout(pack, i);
            container.addView(view);
        }
        return container;
    }

    private View createGridPackLayout(PackRealm packRealm, int pos) {
        LinearLayout container = getLinearLayout();
        if (pos == 0) {
            container.addView(createMapSize(packRealm, pos));
        }
        container.addView(createMapSizeValue(packRealm, pos));
        return container;
    }

    @NonNull
    private LinearLayout getLinearLayout() {
        LinearLayout container = new LinearLayout(mContext);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT)
        );
        return container;
    }

    private FlexboxLayout createMapSize(PackRealm packRealm, int pos) {
        FlexboxLayout flexboxLayout = getFlexboxLayout();
        PackItemRealm packItem = packRealm.getItens().get(pos);
        // RENDERIZA VAZIO CASO SEJA ESTAMPA E COR
//        if ((packItem.getColor().getId()() != null && !packItem.getColor().getId()().equals("sem_cor"))) {
        if (mProduct.getPtype().equalsIgnoreCase("1") || mProduct.getPtype().equalsIgnoreCase("2")) {
            if (mProduct.getColors() != null
                    && !mProduct.getColors().isEmpty()
                    && mProduct.getColors().get(0) != null
                    && mProduct.getColors().get(0).getName().equalsIgnoreCase("Sem Cor")) {
                // Do noting
            } else {
                flexboxLayout.addView(renderPMG(""));
            }
        }
//        }

        // P, M, G
        for (SizeItemRealm key : packItem.getSizes()) {
            flexboxLayout.addView(
                    renderPMG(key.getKey())
            );
        }

        return flexboxLayout;
    }

    private TextView renderPMG(String key) {
        final int minHeight = (int) getResources().getDimension(R.dimen.min_height_box_pmg);
        TextView tv = getTextView();
        tv.setBackground(ContextCompat.getDrawable(mContext, R.drawable.shape_grid2));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        tv.setMinHeight(minHeight);
        tv.setLayoutParams(layoutParams);
        tv.setText(key);
        return tv;
    }

    private LinearLayout createMapSizeValue(PackRealm packRealm, int pos) {
        LinearLayout container = getLinearLayout();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        FlexboxLayout flexboxLayout = getFlexboxLayout();

        PackItemRealm packItem = packRealm.getItens().get(pos);

        renderEstampColor(packItem, packRealm, flexboxLayout);

        // Margin entre packs quando pack grade fechada
        if (mProduct.getPtype().equalsIgnoreCase("3") || mProduct.getPtype().equalsIgnoreCase("4")) {
            params.setMargins(0, 0, 0, 150);
            container.setLayoutParams(params);
        }

        if (packItem.getSizes().size() >= 1) {
            for (int i = 0; i < packItem.getSizes().size(); i++) {
                SizeItemRealm m = packItem.getSizes().get(i);
                TextView tv = renderBoxSize(packItem, packRealm, m);

                // Primeira grid e primeira vez que ver grid
                if (i == 0) {
                    mTooltipViewId = Utils.getRandomId();
                    tv.setId(mTooltipViewId);
                }

                flexboxLayout.addView(tv);
            }
        }

        container.addView(flexboxLayout);

        // GRADE FECHADA - É PACK - Cria a caixa de baixo
        if (mProduct.getPtype().equalsIgnoreCase("3") || mProduct.getPtype().equalsIgnoreCase("4")) {
            closedPack(container, packItem);
        }
        return container;
    }

    private void closedPack(LinearLayout container, PackItemRealm packItem) {

        final int minHeight = (int) getResources().getDimension(R.dimen.min_height_icon_pack);
        final int margin = (int) getResources().getDimension(R.dimen.margin_icon_pack);

        TextView textPackView = openPackLayout(packItem);

        LinearLayout containerPlus = getLinearLayout();
        containerPlus.setOrientation(LinearLayout.HORIZONTAL);
        containerPlus.setGravity(Gravity.CENTER);

        ImageView ivMinus = new ImageView(mContext);
        ivMinus.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.icone_menos));
        ivMinus.setBackground(ContextCompat.getDrawable(mContext, R.drawable.shape_grid));
        LinearLayout.LayoutParams paramsMinus = new LinearLayout.LayoutParams(
                minHeight,
                minHeight);
        paramsMinus.setMargins(0, 0, margin, 0);
        ivMinus.setLayoutParams(paramsMinus);
        //ivMinus.setPadding(MARGIN_BUTTON, MARGIN_BUTTON, MARGIN_BUTTON, MARGIN_BUTTON);
        ivMinus.setTag(R.id.tag_id_pack, packItem.getRandomId());
        ivMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int idOfPack = (int) view.getTag(R.id.tag_id_pack);

                PackItemRealm item = findPackItemById(idOfPack);
                if (item != null) {

                    showTooltip(findViewById(idOfPack));

                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();

                    int total = item.getQuantityPacks() - 1;

                    if (total <= 0) {
                        total = 0;
                    }

                    item.setQuantityPacks(total);

                    realm.commitTransaction();
                    realm.close();

                    TextView textView = (TextView) findViewById(idOfPack);
                    textView.setText(String.valueOf(total));

                    //vibrate(VIBRATE_SHORT);

                    calculaTotal();
                }
            }
        });

        ImageView ivPlus = new ImageView(mContext);
        ivPlus.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.icone_mais));
        ivPlus.setBackground(ContextCompat.getDrawable(mContext, R.drawable.shape_grid));
        LinearLayout.LayoutParams paramsPlus = new LinearLayout.LayoutParams(
                minHeight,
                minHeight);
        paramsPlus.setMargins(margin, 0, 0, 0);
        ivPlus.setLayoutParams(paramsPlus);
        //ivPlus.setPadding(MARGIN_BUTTON, MARGIN_BUTTON, MARGIN_BUTTON, MARGIN_BUTTON);
        ivPlus.setTag(R.id.tag_id_pack, packItem.getRandomId());
        ivPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int idOfPack = (int) view.getTag(R.id.tag_id_pack);

                PackItemRealm item = findPackItemById(idOfPack);

                if (item != null) {

                    if (mTooltipHasShow == false) {
                        showTooltip(findViewById(idOfPack));
                    }

                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();

                    int total = item.getQuantityPacks() + 1;
                    item.setQuantityPacks(total);

                    realm.commitTransaction();
                    realm.close();

                    TextView textView = (TextView) findViewById(idOfPack);
                    textView.setText(String.valueOf(total));

                    //vibrate(VIBRATE_SHORT);

                    calculaTotal();
                }
            }
        });

        final int padding = (int) getResources().getDimension(R.dimen.padding_icon_pack);

        LinearLayout.LayoutParams nParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout box = getLinearLayout();
        box.setLayoutParams(nParams);
        ivMinus.setPadding(padding, padding, padding, padding);
        box.addView(ivMinus);

        LinearLayout box1 = getLinearLayout();
        box1.setLayoutParams(nParams);
        ivPlus.setPadding(padding, padding, padding, padding);
        box1.addView(ivPlus);

        containerPlus.addView(box);
        containerPlus.addView(textPackView);
        containerPlus.addView(box1);
        container.addView(containerPlus);
    }

    private void renderEstampColor(PackItemRealm packItem, PackRealm packRealm, FlexboxLayout flexboxLayout) {
        // ESTAMPA OU COR

        /*if ((packItem.getCode() != null && !packItem.getCode().equals("sem_cor"))
                || (packItem.getEstampa() != null && !packItem.getEstampa().isEmpty())) {
        */
        if (packItem.getColor().getId() != null) {

            // Se tem colorId é pq nao precisa exibir os labels de cores
            if (mProduct.getPtype().equalsIgnoreCase("1") || mProduct.getPtype().equalsIgnoreCase("2")) {
                mDisableColorLabel = true;
            }

            final ColorRealm colorInfo = findColorByInd(packItem.getColor().getId());

            if (mProduct.getPtype().equalsIgnoreCase("1") || mProduct.getPtype().equalsIgnoreCase("2")/*packRealm.isPackOpen() && packRealm.isPackColor() && colorInfo != null*/) {

                final ImageView tv = new ImageView(mContext);

                final int m = (int) getResources().getDimension(R.dimen.margin_around_image_curcle);
                tv.setPadding(m, m, m, m);

                tv.setBackground(ContextCompat.getDrawable(mContext, R.drawable.shape_grid));

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);

                tv.setLayoutParams(layoutParams);

                // COR
                if (colorInfo.getCode() != null && colorInfo.getName() != null && !colorInfo.getName().equalsIgnoreCase("Sem Cor")) {
                    final Drawable draw = ContextCompat.getDrawable(mContext, R.drawable.shape_grid_circle_color);
                    tv.setImageDrawable(draw);
                    tv.setColorFilter(Color.parseColor(colorInfo.getCode()));
                    tv.setTag(R.id.tag_draw, colorInfo.getCode());

                    //ESTAMPA
                } else {
                    if (colorInfo.getEstampa() != null && !colorInfo.getEstampa().isEmpty()) {

                        final int width = (int) getResources().getDimension(R.dimen.image_width);

                        Picasso.with(mContext)
                                .load(colorInfo.getEstampa())
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .resize(width, width)
                                .transform(new CircleTransform())
                                .into(tv, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Picasso.with(mContext)
                                                .load(colorInfo.getEstampa())
                                                .resize(width, width)
                                                .transform(new CircleTransform())
                                                .into(tv);
                                    }
                                });
                        tv.setTag(R.id.tag_estampa, colorInfo.getEstampa());
                    }
                }

                tv.setTag(R.id.tag_color_name, colorInfo.getName());

                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String title = "";
                        String code = "";
                        String estampa = "";

                        try {
                            title = (String) view.getTag(R.id.tag_color_name);
                            code = (String) view.getTag(R.id.tag_draw);
                            estampa = (String) view.getTag(R.id.tag_estampa);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        popupColorPreview(ProdutoActivity.this, title, code, estampa);
                    }
                });

                if (colorInfo.getName() != null && colorInfo.getName().equalsIgnoreCase(SEM_COR)) {
                    // Do noting
                } else {
                    flexboxLayout.addView(tv);
                }
            }
        }
    }

    @NonNull
    private TextView renderBoxSize(PackItemRealm packItem, PackRealm packRealm, SizeItemRealm m) {
        TextView tv = getTextView();
        tv.setBackground(ContextCompat.getDrawable(mContext, R.drawable.shape_grid));
        tv.setText(String.valueOf(m.getQuantity()));

        final int minHeight = (int) getResources().getDimension(R.dimen.min_height_box_pmg);
        tv.setMinHeight(minHeight);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(layoutParams);

        int id = Utils.getRandomId();

        // Informacoes para se recuperar
        tv.setTag(R.id.tag_color_id, packItem.getColor().getId());
        tv.setTag(R.id.tag_key, m.getKey());
        tv.setTag(R.id.tag_id, id);
        tv.setId(id);

        if (!m.isSell()) {
            tv.setBackgroundColor(getResources().getColor(R.color.gridColorOpaque));
            tv.setTextColor(getResources().getColor(R.color.gridColorOpaque));
        } else {

            // GRADE ABERTA - NAO É PACK
            if (mProduct.getPtype().equalsIgnoreCase("1") || mProduct.getPtype().equalsIgnoreCase("2")) {
                tv.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        mLongClick = true;
                        String colorId = (String) view.getTag(R.id.tag_color_id);
                        String tagSize = (String) view.getTag(R.id.tag_key);
                        int id = (int) view.getTag(R.id.tag_id);

                        SizeItemRealm item = findSizeItemByColorAndSizeName(colorId, tagSize);
                        if (item != null) {

                            if (mTooltipHasShow == false) {
                                showTooltip(view);
                            }

                            Realm realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            item.setQuantity(0);
                            realm.commitTransaction();
                            realm.close();

                            TextView textView = (TextView) view;
                            try {
                                textView.setText(String.valueOf(item.getQuantity()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            vibrate(VIBRATE_LONG);

                            calculaTotal();
                        }
                        return false;
                    }
                });

                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mLongClick == false) {
                            String colorId = (String) view.getTag(R.id.tag_color_id);
                            String tagSize = (String) view.getTag(R.id.tag_key);
                            int id = (int) view.getTag(R.id.tag_id);

                            SizeItemRealm item = findSizeItemByColorAndSizeName(colorId, tagSize);
                            if (item != null) {

                                if (mTooltipHasShow == false) {
                                    showTooltip(view);
                                }

                                int total = item.getQuantity() + 1;

                                Realm realm = Realm.getDefaultInstance();
                                realm.beginTransaction();

                                item.setQuantity(total);

                                realm.commitTransaction();
                                realm.close();

                                TextView textView = (TextView) view;
                                try {
                                    textView.setText(String.valueOf(total));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                //vibrate(VIBRATE_SHORT);

                                calculaTotal();
                            }
                        }
                        mLongClick = false;
                    }
                });
            }
        }
        return tv;
    }

    private TextView openPackLayout(PackItemRealm packItem) {

        final int minHeight = (int) getResources().getDimension(R.dimen.min_height_icon_pack);

        TextView textPackView = getTextView();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                (int) (minHeight * 1.2),
                minHeight);

        params.setMargins(0, 20, 0, 20);
        textPackView.setLayoutParams(params);
        textPackView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.shape_grid));

        textPackView.setTag(R.id.tag_id_pack, packItem.getRandomId());
        textPackView.setGravity(Gravity.CENTER);
        textPackView.setId(packItem.getRandomId());
        textPackView.setText(String.valueOf(packItem.getQuantityPacks()));
        textPackView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mLongClick = true;

                int packId = (int) view.getTag(R.id.tag_id_pack);

                PackItemRealm item = findPackItemById(packId);
                if (item != null) {

                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    item.setQuantityPacks(0);
                    realm.commitTransaction();
                    realm.close();

                    TextView textView = (TextView) view;
                    textView.setText(String.valueOf(item.getQuantityPacks()));

                    if (mTooltipHasShow == false) {
                        showTooltip(view);
                    }

                    vibrate(VIBRATE_LONG);

                    calculaTotal();
                }
                return false;
            }
        });

        textPackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLongClick == false) {
                    int packId = (int) view.getTag(R.id.tag_id_pack);

                    PackItemRealm item = findPackItemById(packId);
                    if (item != null) {

                        if (mTooltipHasShow == false) {
                            showTooltip(view);
                        }

                        int total = item.getQuantityPacks() + 1;

                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();

                        item.setQuantityPacks(total);

                        realm.commitTransaction();
                        realm.close();

                        TextView textView = (TextView) view;
                        textView.setText(String.valueOf(total));

                        //vibrate(VIBRATE_SHORT);
                        calculaTotal();
                    }
                }
                mLongClick = false;
            }
        });

        return textPackView;
    }

    private void vibrate(int milliseconds) {
        Vibrator v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(milliseconds);
    }

    private void calculaTotal() {
        Typeface fontFuturaMedium = FontsOverride.fontMedium(getAssets());
        Typeface fontFuturaHeavy = FontsOverride.fontHeavy(getAssets());

        int total = CartRealmService.totalByProduct(mProduct);

        TextView pieceTextView = (TextView) findViewById(R.id.piece);
        pieceTextView.setText(String.valueOf(total));
        pieceTextView.setTypeface(fontFuturaHeavy);

        // Preços
        TextView total1 = (TextView) findViewById(R.id.total1);
        total1.setText(String.valueOf(total));
        total1.setTypeface(fontFuturaMedium);

        TextView total2 = (TextView) findViewById(R.id.total2);
        total2.setText(String.format("R$ %.2f", total * mProduct.getValor()));
        total2.setTypeface(fontFuturaHeavy);

    }

    private SizeItemRealm findSizeItemByColorAndSizeName(String colorId, String tagSize) {
        for (PackRealm pack : mProduct.getPacks()) {
            for (PackItemRealm packItem : pack.getItens()) {
                String ci = packItem.getColor().getId();
                if (ci.equals(colorId)) {
                    for (SizeItemRealm size : packItem.getSizes()) {
                        if (size.getKey().equals(tagSize)) {
                            SizeItemRealm item = size;
                            return item;
                        }
                    }
                }
            }
        }
        return null;
    }

    private void configureCart() {
        mVestiActionBar.setBulletCartText(CartRealmService.getTotalProductsInCart());
    }

    public void popupColorPreview(Activity activity, String textMessage, String code, final String estampa) {
        final Dialog dialog = new Dialog(activity);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    dialogInterface.dismiss();
                }

                return false;
            }
        });

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_color);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView text = (TextView) dialog.findViewById(R.id.text);
        text.setText(textMessage);
        final ImageView image = (ImageView) dialog.findViewById(R.id.image);
        if (code != null && !code.isEmpty()) {
            final Drawable draw = ContextCompat.getDrawable(mContext, R.drawable.shape_grid_circle_color);
            image.setImageDrawable(draw);
            image.setColorFilter(Color.parseColor(code));
        } else {
            Picasso.with(mContext)
                    .load(estampa)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .transform(new CircleTransform())
                    .into(image, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                            Picasso.with(mContext)
                                    .load(estampa)
                                    .transform(new CircleTransform())
                                    .into(image);
                        }
                    });
        }
        View view = dialog.findViewById(R.id.box_dialog);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @NonNull
    private TextView getTextView() {
        TextView tv = new TextView(mContext);
        tv.setPadding(LEFT, TOP, RIGHT, BOTTOM);
        tv.setGravity(Gravity.CENTER);
        //tv.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        //tv.setHeight(HEIGHT);
        tv.setTypeface(FontsOverride.fontBook(getAssets()));

        LinearLayout.LayoutParams lptv = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        tv.setLayoutParams(lptv);
        return tv;
    }

    @NonNull
    private FlexboxLayout getFlexboxLayout() {
        FlexboxLayout flexboxLayout = new FlexboxLayout(mContext);
        FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        flexboxLayout.setLayoutParams(lp);
        flexboxLayout.setFlexDirection(FlexDirection.ROW);
        flexboxLayout.setFlexWrap(FlexWrap.NOWRAP);
        flexboxLayout.setJustifyContent(JustifyContent.CENTER);
        return flexboxLayout;
    }


    private ColorRealm findColorByInd(String colorId) {
        for (ColorRealm color : mProduct.getColors()) {
            if (color.getId().equals(colorId)) {
                return color;
            }
        }
        return null;
    }

    private PackItemRealm findPackItemById(int packId) {
        for (PackRealm pack : mProduct.getPacks()) {
            for (PackItemRealm packItem : pack.getItens()) {
                int ci = packItem.getRandomId();
                if (ci == packId) {
                    return packItem;
                }
            }
        }
        return null;
    }

    private void showTooltip(View tv) {
        if (mTip == null && mTooltipHasShow == false && Utils.isFirstGradTooltip(mContext)) {
            mTooltipHasShow = true;
            Utils.setFirstGradTooltip(mContext);
            mTip = new Tooltip.Builder(tv)
                    .setBackgroundColor(Color.BLACK)
                    .setTextColor(Color.WHITE)
                    .setTextSize(16f)
                    .setPadding(18f)
                    .setCornerRadius(20f)
                    .setDismissOnClick(true)
                    .setTypeface(FontsOverride.fontMedium(getAssets()))
                    .setText(getString(R.string.info_first_time_grid))
                    .show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mTip.dismiss();
                }
            }, TIME_TOOLTIP_SHOWING);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        configureCart();
        buttonAddOrEdit();
    }
}
