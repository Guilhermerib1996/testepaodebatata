package com.meuvesti.cliente.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.Callable;

import com.google.gson.JsonObject;
import com.meuvesti.cliente.BuildConfig;
import com.meuvesti.cliente.R;
import com.meuvesti.cliente.fragment.CatalagoFragment;
import com.meuvesti.cliente.iclass.UpdateCatalog;
import com.meuvesti.cliente.iclass.VestiCallback;
import com.meuvesti.cliente.model.BuyItemData;
import com.meuvesti.cliente.model.ListBuyResponse;
import com.meuvesti.cliente.realm.CartRealmService;
import com.meuvesti.cliente.realm.UsuarioRealm;
import com.meuvesti.cliente.service.VestiAPI;
import com.meuvesti.cliente.realm.FilterRealmService;
import com.meuvesti.cliente.service.VestiService;
import com.meuvesti.cliente.utils.CategoriaDB;
import com.meuvesti.cliente.utils.Globals;
import com.meuvesti.cliente.realm.RealmService;
import com.meuvesti.cliente.utils.Utils;
import com.meuvesti.cliente.utils.VestiActionBar;
import com.onesignal.OneSignal;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    private static final long PRESS_EXIT = 1200;
    private static final String TAG = MainActivity.class.getSimpleName();
    public VestiActionBar mVestiActionBar;
    private boolean mPressBackCount = false;
    private UsuarioRealm mUsuario;
    private Context mContext;
    private View navHeaderView, menuLogin, menuSair, menuCatalogo, menuPerfil, imagePerfil, loading;
    private TextView txtNomeUsuario, txtNome, versiionName;
    private CatalagoFragment fragment;
    private VestiCallback mFragmentInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void setFragmentInterface(VestiCallback fragmentInterface) {
        mFragmentInterface = fragmentInterface;
    }

    private void init() {
        mContext = MainActivity.this;
        mUsuario = RealmService.getUsuarioLogado(mContext);


        if (mUsuario != null) {
            OneSignal.sendTag("Document", mUsuario.getDocument());
        }

        configureActionBar();
        configureNavgation();
        configureUserDataInMenu();

        Uri data = getIntent().getData();
        if (data != null) {
            // Abrir links dinamicos
            onNewIntent(getIntent());
        } else {
            callServiceListBuy();
        }

        callServiceConfigs();
    }


    private void configureActionBar() {
        mVestiActionBar = new VestiActionBar(this);
        //mVestiActionBar.setTitle(getString(R.string.app_name));
        mVestiActionBar.setImageTitle();
        setContentView(mVestiActionBar.getMainContent());

        mVestiActionBar.setOnHomeClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVestiActionBar.openMenu();
            }
        });

        mVestiActionBar.setOnCartClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CarrinhoActivity.class));
            }
        });

        mVestiActionBar.setOnFilterClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUsuario = RealmService.getUsuarioLogado(MainActivity.this);
                if (mUsuario == null) {
                    goToLogin(true);
                } else {
                    Intent i;
                    if (Utils.isUsingCategory(mContext)) {
                        i = new Intent(MainActivity.this, CategoriaActivity.class);
                    } else {
                        i = new Intent(MainActivity.this, FiltroActivity.class);
                    }
                    startActivityForResult(i, CatalagoFragment.REQUEST_FILTRO);
                }
            }
        });

        mVestiActionBar.toggleIconFilter(true);
        mVestiActionBar.toggleIconCart(true);
    }

    private void goToLogin(boolean withCloseMenu) {
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        i.putExtra(LoginActivity.LOGIN_WITH_CLOSE, withCloseMenu);
        startActivityForResult(i, LoginActivity.REQUEST_LOGIN);
    }

    public void renderCatalog() {
        mVestiActionBar.toggleIconFilter(true);
        mVestiActionBar.toggleIconCart(true);
        fragment = CatalagoFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_main, fragment)
                .commitAllowingStateLoss();
    }

    public void showLoginMenu(boolean show) {
        if (show) {
            menuLogin.setVisibility(View.VISIBLE);
            menuSair.setVisibility(View.GONE);
            menuCatalogo.setVisibility(View.GONE);
            menuPerfil.setVisibility(View.GONE);
            imagePerfil.setVisibility(View.VISIBLE);
            txtNomeUsuario.setVisibility(View.GONE);
            txtNome.setVisibility(View.GONE);
        } else {
            menuLogin.setVisibility(View.GONE);
            menuSair.setVisibility(View.VISIBLE);
            menuCatalogo.setVisibility(View.VISIBLE);
            menuPerfil.setVisibility(View.VISIBLE);
            imagePerfil.setVisibility(View.GONE);
            txtNomeUsuario.setVisibility(View.VISIBLE);
            txtNome.setVisibility(View.VISIBLE);
        }
    }


    private void configureUserDataInMenu() {
        if (mUsuario != null) {
            if (mUsuario.getNome().length() >= 2) {
                txtNomeUsuario.setText(mUsuario.getNome().substring(0, 2));
                txtNomeUsuario.setVisibility(View.VISIBLE);

                txtNome.setText(mUsuario.getNome());
                txtNome.setVisibility(View.VISIBLE);

                showLoginMenu(false);
            } else {
                txtNomeUsuario.setText(mUsuario.getNome().substring(0, 1));
                txtNomeUsuario.setVisibility(View.VISIBLE);

                txtNome.setText(mUsuario.getNome());
                txtNome.setVisibility(View.VISIBLE);

                showLoginMenu(false);
            }
        } else {
            showLoginMenu(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Se o usuário mudou no login, é hora de atualizar as informações dele.
        if (RealmService.getUsuarioLogado(this) != null
                && RealmService.getUsuarioLogado(this).getId() != null
                && mUsuario != null
                && mUsuario.getId() != null
                && !RealmService.getUsuarioLogado(this).getId().equals(mUsuario.getId())
                || (mUsuario == null && RealmService.getUsuarioLogado(this) != null)) {
            mUsuario = RealmService.getUsuarioLogado(this);
            configureUserDataInMenu();
            callServiceListBuy();
        }
    }


    private void callServiceListBuy() {
        if (mUsuario != null) {
            VestiAPI.get().api(mContext).listsbuy(Globals.SCHEME_NAME)
                    .enqueue(new Callback<ListBuyResponse>() {
                        @Override
                        public void onResponse(Call<ListBuyResponse> call, Response<ListBuyResponse> response) {
                            ListBuyResponse buy = response.body();

                            if (buy == null || buy.getProducts() == null || buy.getProducts().getData() == null) {
                                renderCatalog();
                            } else {

                                final List<BuyItemData> filters = buy.getProducts().getData();
                                FilterRealmService.addFilter(filters);

                                if (!filters.isEmpty()) {
                                    Log.e("MAIN", "BAIXOU INFORMACOES DE FILTRO E DEFINIU COMO TRUE");
                                    Utils.setHasFilter(MainActivity.this, true);
                                }

                                renderCatalog();
                            }
                            loading(false);
                        }

                        @Override
                        public void onFailure(Call<ListBuyResponse> call, Throwable t) {
                            if (t instanceof UnknownHostException) {
                                Toast.makeText(mContext, getString(R.string.internet_error), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                            loading(false);
                        }
                    });
        } else {
            Log.e("MAIN", "SEM USUARIO LOGADO CHAMA O CATALOGO TELA DE WELCOME");
            renderCatalog();
        }
    }

    private void callServiceConfigs() {
        VestiAPI.get().api(mContext).configs(Globals.SCHEME_NAME).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null
                        && response.body().has("company_config")
                        && response.body().get("company_config").getAsJsonObject().has("parameters")
                        && !response.body().get("company_config").getAsJsonObject().get("parameters").isJsonNull()) {
                    Utils.setConfigsParameter(mContext, response.body().get("company_config").getAsJsonObject().get("parameters").getAsJsonObject());

                    if (response.body().get("company_config").getAsJsonObject().get("parameters").getAsJsonObject().has("search")) {
                        boolean hasSearch = response.body().get("company_config").getAsJsonObject().get("parameters").getAsJsonObject().get("search").getAsJsonObject().has("keyword");
                        Utils.setConfigSearchEnableTags(mContext, hasSearch);
                    } else {
                        Utils.setConfigSearchEnableTags(mContext, false);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });

    }

    private void loading(boolean showLoading) {
        if (loading == null) {
            loading = findViewById(R.id.box_loading);
        }
        if (showLoading) {
            loading.setVisibility(View.VISIBLE);
        } else {
            loading.setVisibility(View.GONE);
        }
    }

    private void configureNavgation() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navHeaderView = navigationView.getHeaderView(0);
        menuLogin = navHeaderView.findViewById(R.id.menu_login);
        menuSair = navHeaderView.findViewById(R.id.menu_sair);
        menuCatalogo = navHeaderView.findViewById(R.id.menu_catalogo);
        menuPerfil = navHeaderView.findViewById(R.id.menu_perfil);
        imagePerfil = navHeaderView.findViewById(R.id.imagePerfil);
        txtNomeUsuario = (TextView) navHeaderView.findViewById(R.id.txtNomeUsuario);
        txtNome = (TextView) navHeaderView.findViewById(R.id.txtNome);

        versiionName = (TextView) navHeaderView.findViewById(R.id.version);
        versiionName.setText(BuildConfig.VERSION_NAME);

        if (Globals.isHomolog()) {
            versiionName.setText(BuildConfig.VERSION_NAME + " " + Globals.SCHEME_NAME + " homolog" + " " + Utils.getDateCompiled(getApplicationContext()));
        }

        menuSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVestiActionBar.closeMenu(false);
                logout();
            }
        });
        menuLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVestiActionBar.closeMenu(false);
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                i.putExtra(LoginActivity.LOGIN_WITH_CLOSE, true);
                startActivityForResult(i, LoginActivity.REQUEST_LOGIN);
            }
        });
        menuCatalogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVestiActionBar.closeMenu(true);
            }
        });
        menuPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVestiActionBar.closeMenu(false);
                Intent i = new Intent(MainActivity.this, PerfilActivity.class);
                startActivityForResult(i, PerfilActivity.REQUEST);
            }
        });
    }

    private void logout() {
        RealmService.logout();
        FilterRealmService.clearFilter();
        RealmService.clearConfig();
        CartRealmService.clear();
        CategoriaDB.clear(mContext);

        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra(LoginActivity.FORCE_LOGIN, true);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        if (mVestiActionBar.menuIsOpen()) {
            mVestiActionBar.closeMenu(true);
        } else {
            this.moveTaskToBack(true);
        }
    }

    /*private void gerenciarSaida() {
        if (mPressBackCount) {
            goToLogin(false);
        }
        this.mPressBackCount = true;
        Toast.makeText(this, R.string.mensagem_saindo, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mPressBackCount = false;
            }
        }, PRESS_EXIT);
    }*/

    @Override
    protected void onNewIntent(Intent intent) {
        try {
            configureDeepLink(intent);
        } catch (Exception e) {
            Toast.makeText(mContext, "Link não legível", Toast.LENGTH_SHORT).show();
        }
        super.onNewIntent(intent);
    }

    private void configureDeepLink(Intent intent) {
        Uri data = intent.getData();
        if (data != null) {

            String[] params = data.toString().split("/");
            String filter = null;
            String base = null;

            if (data.toString().contains("hv2.meuvesti.com")) {
                if (params[6].equals(Globals.SCHEME_NAME)) {
                    filter = params[6];
                    base = params[5];
                }
            } else if (data.toString().contains(".vesti.mobi")) {
                if (params.length >= 4) {
                    filter = params[4];
                    base = params[3];
                }
            }

            if (filter != null) {
                Log.e(">>>> DEEPLINK", "Passou o link aqui: " + filter + " DEFINIU FILTRO COMO FALSE");
                callCatalogWithFilter(filter, base);
            } else {
                Toast.makeText(mContext, getString(R.string.endereco_sem_mapeamento), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void callCatalogWithFilter(String filter, String base) {
        if (mVestiActionBar != null) {
            mVestiActionBar.setTitle(getString(R.string.app_name));
        }
        //Salva a lista do whatsapp
        Utils.saveWhatsAppLink(MainActivity.this, filter);
        Utils.setHasFilter(MainActivity.this, false);
        FilterRealmService.uncheckFilters();
        renderCatalog();
    }

    public VestiActionBar getVestiActionBar() {
        return mVestiActionBar;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Saiu pelo editar perfil
        if (requestCode == PerfilActivity.REQUEST
                && resultCode == Activity.RESULT_OK) {
            logout();
        } else if (requestCode == LoginActivity.REQUEST_LOGIN
                && resultCode == Activity.RESULT_OK) {
            init();
        } else if (requestCode == CatalagoFragment.REQUEST_FILTRO
                && resultCode == Activity.RESULT_OK) {
            //renderCatalog();
            EventBus.getDefault().postSticky(new UpdateCatalog());
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
