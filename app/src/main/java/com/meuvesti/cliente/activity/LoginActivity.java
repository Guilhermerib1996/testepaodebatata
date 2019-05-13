package com.meuvesti.cliente.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.meuvesti.cliente.R;
import com.meuvesti.cliente.model.BaseResult;
import com.meuvesti.cliente.model.ConfigurationRequest;
import com.meuvesti.cliente.model.LoginRequest;
import com.meuvesti.cliente.model.Usuario;
import com.meuvesti.cliente.realm.FilterRealmService;
import com.meuvesti.cliente.realm.RealmService;
import com.meuvesti.cliente.realm.UsuarioRealm;
import com.meuvesti.cliente.service.VestiAPI;
import com.meuvesti.cliente.utils.Globals;
import com.meuvesti.cliente.utils.Utils;
import com.meuvesti.cliente.utils.VestiActionBar;

import java.net.UnknownHostException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    public static final String FORCE_LOGIN = "menu_entrar";
    public static final String LOGIN_WITH_CLOSE = "login_with_close";
    public static final int REQUEST_LOGIN = 3344;
    public static final int REQUEST_CLOSE_LOGIN = 1133;
    private static final long INTERVALO_PRESSIONAR_BOTAO_SAIR = 1200;
    TextView txtCadastro, txtEmail, txtSenha;
    private ImageView imgErrorSenha, imgErrorMail;
    private boolean mPressionouBackDuasVezes;
    private View loading, btnLogin;
    private Context mContext;
    private VestiActionBar mVestiActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getBaseContext();

        if (Globals.isHomolog()) {
            Toast.makeText(mContext, getString(R.string.ambiente_homolog), Toast.LENGTH_SHORT).show();
        }

        callConfigurationService();

        if (getIntent().hasExtra(LOGIN_WITH_CLOSE) && getIntent().getExtras().getBoolean(LOGIN_WITH_CLOSE)
                || isCameFromTheCart()) {
            mVestiActionBar = new VestiActionBar(this);
            mVestiActionBar.setTitle(getString(R.string.menu_login));
            mVestiActionBar.setContentView(R.layout.activity_login);
            setContentView(mVestiActionBar.getMainContent());
            mVestiActionBar.setOnCloseClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isCameFromTheCart()) {
                        finish();
                        Intent intent = new Intent(LoginActivity.this, CarrinhoActivity.class);
                        startActivity(intent);
                    } else {
                        finish();
                    }
                }
            });
        } else {
            setContentView(R.layout.activity_login);
        }

        txtCadastro = (TextView) findViewById(R.id.txtCadastro);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtSenha = (EditText) findViewById(R.id.txtSenha);
        imgErrorSenha = (ImageView) findViewById(R.id.imgErroSenha);
        imgErrorMail = (ImageView) findViewById(R.id.imgErroEmail);
        btnLogin = findViewById(R.id.btnLogin);
        loading = findViewById(R.id.box_loading);
        loading.setVisibility(View.GONE);

        // Verifica se veio do link interno de entrar ou do link filtros
        // Checagem necessaria para nao entrar em loop por conta da lista de whatsapp
        if (!getIntent().hasExtra(FORCE_LOGIN) && !getIntent().hasExtra(LOGIN_WITH_CLOSE)) {
            verificaUsuarioLogado();
        }

        txtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    findViewById(R.id.btnApagarEmail).setVisibility(View.VISIBLE);
                } else {
                    validaEmail(true);
                    findViewById(R.id.btnApagarEmail).setVisibility(View.GONE);
                }
            }
        });

        txtCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
                if (isCameFromTheCart()) {
                    intent.putExtra(CarrinhoActivity.FINISH_BUY, true);
                }
                startActivity(intent);
//                startActivityForResult(intent, REQUEST_CLOSE_LOGIN);
            }
        });

        findViewById(R.id.btnApagarEmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtEmail.setText("");
            }
        });

        findViewById(R.id.txtEsqueceuSenha).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, EsqueceuSenhaActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.closeKeyboard(LoginActivity.this);
                Animation anim = AnimationUtils.loadAnimation(getBaseContext(), R.anim.scale);
                v.startAnimation(anim);
                if (!Utils.isNetworkAvailable(LoginActivity.this)) {
                    Toast.makeText(LoginActivity.this, getString(R.string.internet_error), Toast.LENGTH_LONG).show();
                } else {
                    login();
                }
            }
        });

        View btnFacebook = findViewById(R.id.login_facebook);
        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, WebviewActivity.class);
                i.putExtra(WebviewActivity.REQUEST_FACEBOOK, true);
                i.putExtra(CarrinhoActivity.FINISH_BUY, isCameFromTheCart());
                startActivityForResult(i, REQUEST_CLOSE_LOGIN);
            }
        });

        View btnGoogle = findViewById(R.id.login_google);
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, WebviewActivity.class);
                i.putExtra(WebviewActivity.REQUEST_GOOGLE, true);
                i.putExtra(CarrinhoActivity.FINISH_BUY, isCameFromTheCart());
                startActivityForResult(i, REQUEST_CLOSE_LOGIN);
            }
        });
    }

    private boolean isCameFromTheCart() {
        return getIntent().getBooleanExtra(CarrinhoActivity.REQUEST_CARRINHO, false);
    }

    private void verificaUsuarioLogado() {
        UsuarioRealm usuario = RealmService.getUsuarioLogado(this);
        if (usuario != null) {
            FilterRealmService.uncheckFilters();
            // Marca true pra forcar o catalogo basico
            Utils.setHasFilter(LoginActivity.this, true);
            showLoading(false);
            goToMain();
        }
    }

    private void goToMain() {
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        setResult(Activity.RESULT_OK);
    }

    private void login() {
        Utils.closeKeyboard(LoginActivity.this);
        findViewById(R.id.bgErroEmail).setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.shape_line_success));
        findViewById(R.id.imgErroEmail).setVisibility(View.GONE);
        findViewById(R.id.bgErroSenha).setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.shape_line_success));
        findViewById(R.id.imgErroSenha).setVisibility(View.GONE);
        if (isValid()) {
            showLoading(true);
            callServiceLogin();
        }
    }

    private void callServiceLogin() {
        LoginRequest request = new LoginRequest(txtEmail.getText().toString(), txtSenha.getText().toString());
        VestiAPI.get().api(mContext, true).login(request)
                .enqueue(new Callback<Usuario>() {
                    @Override
                    public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                        if (response.body() == null) {
                            Toast.makeText(LoginActivity.this, getString(R.string.processamento_error), Toast.LENGTH_LONG).show();
                        } else {
                            Usuario usuario = response.body();
                            if (usuario.getResult().isSuccess()) {

                                //callProfileService(usuario);
                                Utils.clearAll(LoginActivity.this);
                                RealmService.logar(usuario.getRealm());
                                loginAfterProfile();

                            } else {
                                Toast.makeText(LoginActivity.this, usuario.getResult().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                        showLoading(false);
                    }

                    @Override
                    public void onFailure(Call<Usuario> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, getString(R.string.processamento_error), Toast.LENGTH_LONG).show();
                        showLoading(false);
                    }
                });
    }

    private void callProfileService(final Usuario usuario) {
        Utils.setStokEdit(mContext, false);

        Utils.clearAll(LoginActivity.this);
        RealmService.logar(usuario.getRealm());

        VestiAPI.get().api(mContext).profilePermStockEdit().enqueue(new Callback<BaseResult>() {
            @Override
            public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {

                if (response.body().getResult().isSuccess()) {
                    Utils.setStokEdit(mContext, true);
                }

                loginAfterProfile();
            }

            @Override
            public void onFailure(Call<BaseResult> call, Throwable t) {
                loginAfterProfile();
            }
        });
    }

    private void loginAfterProfile() {
        Intent intent;
        if (isCameFromTheCart()) {
            finish();
            intent = new Intent(LoginActivity.this, CarrinhoActivity.class);
            intent.putExtra(CarrinhoActivity.FINISH_BUY, true);
        } else {
            intent = new Intent(LoginActivity.this, MainActivity.class);
        }
        startActivity(intent);
    }

    private void callConfigurationService() {
        VestiAPI.get().api(mContext).configurations(Globals.SCHEME_NAME).enqueue(new Callback<ConfigurationRequest>() {
            @Override
            public void onResponse(Call<ConfigurationRequest> call, Response<ConfigurationRequest> response) {
                ConfigurationRequest config = response.body();
                if (config != null
                        && config.getConfiguration() != null
                        && config.getConfiguration().getOrcamentoAceito() != null
                        && config.getConfiguration().getOrcamentoAceito().getData() != null) {

                    Utils.clearCPFCNPJ(mContext);

                    List<String> s = config.getConfiguration().getOrcamentoAceito().getData();
                    if (s.contains("cpf")) {
                        Utils.setConfigCPF(mContext);
                    }
                    if (s.contains("cnpj")) {
                        Utils.setConfigCNPJ(mContext);
                    }
                    if (s.contains("cpf") && s.contains("cnpj")) {
                        Utils.setConfigCNPJ(mContext);
                        Utils.setConfigCPF(mContext);
                    }
                }
            }

            @Override
            public void onFailure(Call<ConfigurationRequest> call, Throwable t) {
                if (t instanceof UnknownHostException) {
                    Toast.makeText(mContext, getString(com.meuvesti.cliente.R.string.internet_error), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean isValid() {
        boolean valid = true;
        valid = validaEmail(valid);
        valid = validaSenha(valid);
        return valid;
    }

    private boolean validaSenha(boolean valid) {
        if (txtSenha.getText().toString().isEmpty()) {
            valid = false;
            findViewById(R.id.bgErroSenha).setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.shape_line_error));
        }
        return valid;
    }

    private boolean validaEmail(boolean valid) {
        if (txtEmail.getText().toString().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(txtEmail.getText().toString()).matches()) {
            valid = false;
            findViewById(R.id.bgErroEmail).setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.shape_line_error));
        }
        return valid;
    }

    public void showLoading(boolean flag) {
        if (loading == null) {
            loading = findViewById(R.id.box_loading);
        }
        if (flag) {
            loading.setVisibility(View.VISIBLE);
        } else {
            loading.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        /*if (mPressionouBackDuasVezes) {
            super.onBackPressed();
        }
        this.mPressionouBackDuasVezes = true;
        Toast.makeText(this, R.string.mensagem_sair_app, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mPressionouBackDuasVezes = false;
            }
        }, INTERVALO_PRESSIONAR_BOTAO_SAIR);
        */

        this.moveTaskToBack(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CLOSE_LOGIN) {
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        callConfigurationService();
        if (loading != null) {
            loading.setVisibility(View.GONE);
        } else {
            loading = findViewById(R.id.box_loading);
            loading.setVisibility(View.GONE);
        }
        super.onResume();
    }
}
