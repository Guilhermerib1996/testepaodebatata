package com.meuvesti.cliente.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;
import com.meuvesti.cliente.R;
import com.meuvesti.cliente.adapter.FiltroAdapter;
import com.meuvesti.cliente.model.BuyItemData;
import com.meuvesti.cliente.model.ListBuyResponse;
import com.meuvesti.cliente.realm.FilterRealm;
import com.meuvesti.cliente.realm.FilterRealmService;
import com.meuvesti.cliente.service.VestiAPI;
import com.meuvesti.cliente.utils.Globals;
import com.meuvesti.cliente.utils.OnClickItem;
import com.meuvesti.cliente.utils.Utils;
import com.meuvesti.cliente.utils.VestiActionBar;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FiltroActivity extends AppCompatActivity {

    RealmResults<FilterRealm> filterRealms;
    private VestiActionBar mVestiActionBar;
    private View btnFiltrar;
    private View btnLimpar;

    private List<String> mFiltersSelectedInScreen = new ArrayList<>();
    private View boxLoading;
    private Context mContext;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_filtro);

            mContext = FiltroActivity.this;
            mVestiActionBar = new VestiActionBar(this);
            mVestiActionBar.setContentView(R.layout.activity_filtro);
            mVestiActionBar.setTitle(getString(R.string.menu_filtros));
            setContentView(mVestiActionBar.getMainContent());
            mVestiActionBar.setOnBackClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            btnFiltrar = findViewById(R.id.btnFiltrar);
            btnLimpar = findViewById(R.id.btnLimpar);
            boxLoading = findViewById(R.id.box_loading);

            init();
    }

    private void init() {
        filterRealms = FilterRealmService.getMyFilters();

        if(filterRealms==null || filterRealms.isEmpty()){
            findViewById(R.id.messageWahtsApp).setVisibility(View.VISIBLE);
            findViewById(R.id.box_buttons).setVisibility(View.GONE);
        }else{
            findViewById(R.id.box_buttons).setVisibility(View.VISIBLE);
        }

        for (FilterRealm f : FilterRealmService.getMySelectedFilter()) {
            mFiltersSelectedInScreen.add(f.getId());
        }

        btnLimpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFiltersSelectedInScreen = new ArrayList<>();

                // tem que setar true para manter a lista de filtros sendo chamadas
                Utils.setHasFilter(FiltroActivity.this, true);
                FilterRealmService.uncheckFilters();

                setResult(Activity.RESULT_OK);
                finish();
            }
        });

        btnFiltrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFiltersSelectedInScreen.size() > 0) {

                    Utils.setHasFilter(FiltroActivity.this, true);
                    FilterRealmService.checkFilters(mFiltersSelectedInScreen);

                    setResult(Activity.RESULT_OK);
                    finish();

                } else {
                    Toast.makeText(getBaseContext(), getString(R.string.sem_filtro_selecionado), Toast.LENGTH_LONG).show();
                }
            }
        });

        flexAddView(-1);
    }


    @Override
    protected void onResume() {
        super.onResume();

        Date currentTime = new Date();
        long diff = currentTime.getTime() - Utils.getDateLastFilter(FiltroActivity.this);
        long sec = diff / 1000;
        long min = diff / (60 * 1000);
        long hour = diff / (60 * 60 * 1000);

        // Atualiza os filtros a cada 1 hora
        if(hour >= 1){
            //Toast.makeText(mContext, "Passou mais de 1 hora, desde sua última visita a essa tela!", Toast.LENGTH_LONG).show();
            callServiceFilter();
        }
    }

    private void callServiceFilter(){
        VestiAPI.get().api(FiltroActivity.this).listsbuy(Globals.SCHEME_NAME)
        .enqueue(new Callback<ListBuyResponse>() {
            @Override
            public void onResponse(Call<ListBuyResponse> call, Response<ListBuyResponse> response) {
                ListBuyResponse buy = response.body();

                if (buy != null && buy.getProducts() != null && buy.getProducts().getData() != null) {
                    List<BuyItemData> filters = buy.getProducts().getData();
                    //Toast.makeText(FiltroActivity.this, "O sistema buscou "+filters.size()+" filtros na retaguarda!", Toast.LENGTH_SHORT).show();
                    FilterRealmService.addFilter(filters);
                    FilterRealmService.checkFilters(mFiltersSelectedInScreen);
                    long time = new Date().getTime();
                    Utils.saveDateLastFilter(FiltroActivity.this, time);
                    init();
                }
            }

            @Override
            public void onFailure(Call<ListBuyResponse> call, Throwable t) {
                if(t instanceof UnknownHostException){
                    Toast.makeText(FiltroActivity.this, getString(R.string.internet_error), Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(FiltroActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void flexAddView(int idToAnimate) {
        FiltroAdapter adapter = new FiltroAdapter(getBaseContext(), mFiltersSelectedInScreen, filterRealms, new OnClickItem() {
            @Override
            public void onClick(View v, int pos) {
                FilterRealm filter = filterRealms.get(pos);
                if (mFiltersSelectedInScreen.contains(filter.getId())) {
                    mFiltersSelectedInScreen.remove(filter.getId());
                } else {
                    mFiltersSelectedInScreen.add(filter.getId());
                }
                flexAddView(pos);
            }
        });

        final FlexboxLayout flexboxLayout = (FlexboxLayout) findViewById(R.id.flex);
        flexboxLayout.removeAllViews();
        for (int i = 0; i < filterRealms.size(); i++) {
            final View itemView = adapter.getView(i, null, null);
            if (i == idToAnimate) {
                itemView.startAnimation(AnimationUtils.loadAnimation(FiltroActivity.this, R.anim.scale));
            }
            flexboxLayout.addView(itemView);
        }
    }
}
