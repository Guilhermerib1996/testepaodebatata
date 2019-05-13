package com.meuvesti.cliente.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.meuvesti.cliente.R;
import com.meuvesti.cliente.model.BaseResult;
import com.meuvesti.cliente.model.ConfigurationRequest;
import com.meuvesti.cliente.model.LoginRequest;
import com.meuvesti.cliente.model.User;
import com.meuvesti.cliente.model.UserResult;
import com.meuvesti.cliente.model.Usuario;
import com.meuvesti.cliente.realm.RealmService;
import com.meuvesti.cliente.realm.UsuarioRealm;
import com.meuvesti.cliente.service.VestiAPI;
import com.meuvesti.cliente.utils.Globals;
import com.meuvesti.cliente.utils.Tip;
import com.meuvesti.cliente.utils.Utils;
import com.meuvesti.cliente.utils.Validacoes;
import com.meuvesti.cliente.utils.VestiActionBar;

import java.net.UnknownHostException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadastroActivity extends AppCompatActivity {

    public static final String REQUEST_NAME = "name";
    public static final String REQUEST_EMAIL = "email";
    public static final String REQUEST_LASTNAME = "lastname";
    public static final String REQUEST_COMPLETE_FORM = "request_complete_form";
    EditText txtDoc, txtNome, txtEmail, txtCelular, txtSenha, txtConfirmaSenha;
    private View imgErroDoc, imgErroNome, imgErroCelular, imgErroEmail,
            imgErroSenha, imgErroConfirmaSenha, loading, btnRegistrar;
    private VestiActionBar mVestiActionBar;
    private Context mContext;
    private boolean mHasCpf, mHasCnpj;
    private boolean mIsCompleteForm = false;
    private boolean mFinishBuy = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();

        if (bundle!=null && bundle.containsKey(REQUEST_COMPLETE_FORM)){
            mIsCompleteForm = getIntent().getExtras().getBoolean(REQUEST_COMPLETE_FORM, false);
        }

        if (bundle!=null && bundle.containsKey(CarrinhoActivity.FINISH_BUY)){
            mFinishBuy = getIntent().getExtras().getBoolean(CarrinhoActivity.FINISH_BUY, false);
        }

        mContext = getBaseContext();

        if (savedInstanceState == null) {
            this.overridePendingTransition(com.meuvesti.cliente.R.anim.slide_in_left,
                    com.meuvesti.cliente.R.anim.slide_out_left);
        }

        mVestiActionBar = new VestiActionBar(this);
        setContentView(mVestiActionBar.getMainContent());
        mVestiActionBar.setTitle(getString(com.meuvesti.cliente.R.string.menu_cadastro));
        mVestiActionBar.setContentView(com.meuvesti.cliente.R.layout.activity_cadastro);
        mVestiActionBar.lockMenu();
        mVestiActionBar.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.clearCookies(CadastroActivity.this);
                finish();
            }
        });

        txtDoc = (EditText) findViewById(com.meuvesti.cliente.R.id.txtDoc);
        txtNome = (EditText) findViewById(com.meuvesti.cliente.R.id.txtNome);
        txtEmail = (EditText) findViewById(com.meuvesti.cliente.R.id.txtEmail);
        txtCelular = (EditText) findViewById(com.meuvesti.cliente.R.id.txtCelular);
        txtSenha = (EditText) findViewById(com.meuvesti.cliente.R.id.txtSenha);
        txtConfirmaSenha = (EditText) findViewById(com.meuvesti.cliente.R.id.txtConfirmaSenha);

        if (mIsCompleteForm) {
            findViewById(R.id.complete_profile).setVisibility(View.VISIBLE);

            String name = getIntent().getExtras().getString(REQUEST_NAME);
            String lastname = getIntent().getExtras().getString(REQUEST_LASTNAME);
            String email = getIntent().getExtras().getString(REQUEST_EMAIL);

            findViewById(R.id.box_confirmasenha).setVisibility(View.GONE);
            findViewById(R.id.box_senha).setVisibility(View.GONE);
            txtNome.setText(name + " " + lastname);

            txtEmail.setText(email);
            txtEmail.setEnabled(false);
            txtEmail.setClickable(false);
            txtEmail.setFocusable(false);
            findViewById(R.id.bgErroEmail).setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorFieldDisabled));

            closeKeyboard();
        }

        imgErroDoc = findViewById(com.meuvesti.cliente.R.id.imgErroDoc);
        imgErroNome = findViewById(com.meuvesti.cliente.R.id.imgErroNome);
        imgErroCelular = findViewById(com.meuvesti.cliente.R.id.imgErroCelular);
        imgErroEmail = findViewById(com.meuvesti.cliente.R.id.imgErroEmail);
        imgErroSenha = findViewById(com.meuvesti.cliente.R.id.imgErroSenha);
        imgErroConfirmaSenha = findViewById(com.meuvesti.cliente.R.id.imgErroConfirmaSenha);

        btnRegistrar = findViewById(com.meuvesti.cliente.R.id.btnRegistrar);

        mHasCpf = Utils.getConfigCPF(mContext);
        mHasCnpj = Utils.getConfigCNPJ(mContext);

        TextView labelDoc = (TextView) findViewById(R.id.labelDoc);
        if(mHasCnpj && mHasCpf){
            labelDoc.setText(getString(R.string.cpf_ou_cnpj));
        }else if(mHasCpf){
            labelDoc.setText(getString(R.string.cpf));
        }else{
            labelDoc.setText(getString(R.string.cnpj));
        }

        imgErroDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Tip(imgErroDoc, getString(com.meuvesti.cliente.R.string.campo_doc_error));
            }
        });

        imgErroNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Tip(imgErroNome, getString(com.meuvesti.cliente.R.string.campo_nome_obrigatorio));
            }
        });

        imgErroEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Tip(imgErroEmail, getString(com.meuvesti.cliente.R.string.campo_email_obrigatorio));
            }
        });

        imgErroCelular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Tip(imgErroCelular, getString(com.meuvesti.cliente.R.string.campo_celular_error));
            }
        });

        imgErroSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Tip(imgErroSenha, getString(com.meuvesti.cliente.R.string.campo_senha_error));
            }
        });

        imgErroConfirmaSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Tip(imgErroConfirmaSenha, getString(com.meuvesti.cliente.R.string.campo_confirmar_senha_error));
            }
        });

        txtNome.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    nomeFormIsValid(true);
                }
            }
        });

        txtSenha.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    senhaFormIsValid(true);
                }
            }
        });

        txtConfirmaSenha.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    confirmaSenhaFormIsValid(true);
                }
            }
        });

        txtDoc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && docFormIsValid(true)) {
                    String doc = txtDoc.getText().toString();
                    callServiceValidateDoc(docGetMask(doc), new Loader.OnLoadCompleteListener() {
                        @Override
                        public void onLoadComplete(Loader loader, Object data) {

                        }
                    });
                }
            }
        });

        txtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String email = txtEmail.getText().toString();
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        emailIsNotValid(getString(com.meuvesti.cliente.R.string.campo_email_invalido));
                    } else {
                        callServiceValidateEmail(email, new Loader.OnLoadCompleteListener() {
                            @Override
                            public void onLoadComplete(Loader loader, Object data) {

                            }
                        });
                    }
                }
            }
        });

        txtCelular.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    celularFormIsValid(true);
                }
            }
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                closeKeyboard();

                Animation anim = AnimationUtils.loadAnimation(getBaseContext(), com.meuvesti.cliente.R.anim.scale);
                v.startAnimation(anim);

                boolean valid = true;

                valid = docFormIsValid(valid);
                valid = nomeFormIsValid(valid);
                valid = emailFormIsValid(valid);
                valid = celularFormIsValid(valid);

                if (!mIsCompleteForm) {
                    valid = senhaFormIsValid(valid);
                    valid = confirmaSenhaFormIsValid(valid);
                }

                if (valid) {
                    if (!Utils.isNetworkAvailable(CadastroActivity.this)) {
                        Toast.makeText(CadastroActivity.this, getString(com.meuvesti.cliente.R.string.internet_error), Toast.LENGTH_LONG).show();
                    } else {
                        validateForm();
                    }
                }
            }
        });
    }

    private void showLoading(boolean showLoading) {
        if (loading == null) {
            loading = findViewById(com.meuvesti.cliente.R.id.box_loading);
        }
        if (showLoading) {
            loading.setVisibility(View.VISIBLE);
        } else {
            loading.setVisibility(View.GONE);
        }
    }

    private void validateForm() {
        showLoading(true);
        // Antes de salvar valida no servidor os dados do e-mail
        callServiceValidateEmail(txtEmail.getText().toString(), new Loader.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(Loader loader, Object data) {
                boolean emailIsValid = (boolean) data;
                if (emailIsValid) {
                    closeKeyboard();
                    // Antes de salvar valida no servidor os dados do documento
                    callServiceValidateDoc(txtDoc.getText().toString(), new Loader.OnLoadCompleteListener() {
                        @Override
                        public void onLoadComplete(Loader loader, Object data) {
                            boolean cnpjIsValid = (boolean) data;
                            if (cnpjIsValid) {
                                // Tenta salvar os dados do formulário
                                callServiceSave();
                            }
                        }
                    });
                } else {
                    callServiceValidateDoc(txtDoc.getText().toString(), new Loader.OnLoadCompleteListener() {
                        @Override
                        public void onLoadComplete(Loader loader, Object data) {
                            showLoading(false);
                        }
                    });
                }
            }
        });
    }

    private boolean confirmaSenhaFormIsValid(boolean valid) {
        if (txtConfirmaSenha.getText().toString().isEmpty()) {
            confirmaSenhaIsNotValid(getString(com.meuvesti.cliente.R.string.campo_confirmar_senha_error));
            valid = false;
        } else {
            if (!txtSenha.getText().toString().toLowerCase().equals(txtConfirmaSenha.getText().toString().toLowerCase())) {
                senhaIsNotValid(getString(com.meuvesti.cliente.R.string.campo_confirmar_senha_diferentes));
                confirmaSenhaIsNotValid(getString(com.meuvesti.cliente.R.string.campo_confirmar_senha_diferentes));
                valid = false;
            } else {
                senhaIsValid();
                confirmaSenhaIsValid();
            }
        }
        return valid;
    }

    private boolean senhaFormIsValid(boolean valid) {
        if (txtSenha.getText().toString().isEmpty()) {
            senhaIsNotValid(getString(com.meuvesti.cliente.R.string.campo_senha_error));
            valid = false;
        } else {
            senhaIsValid();
        }
        return valid;
    }

    private boolean emailFormIsValid(boolean valid) {
        if (txtEmail.getText().toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(txtEmail.getText().toString()).matches()) {
            emailIsNotValid(getString(com.meuvesti.cliente.R.string.campo_email_obrigatorio));
            valid = false;
        } else {
            emailIsValid();
        }
        return valid;
    }

    private boolean nomeFormIsValid(boolean valid) {
        if (txtNome.getText().toString().isEmpty()) {
            nomeIsNotValid(getString(com.meuvesti.cliente.R.string.campo_nome_obrigatorio));
            valid = false;
        } else {
            nomeIsValid();
        }
        return valid;
    }

    private boolean docFormIsValid(boolean valid) {
        String validation = txtDoc.getText().toString().replaceAll("[^0-9]", "");

        docSetMask();

        if (txtDoc.getText().toString().isEmpty()) {
            docIsNotValid(getString(com.meuvesti.cliente.R.string.campo_doc_error));
            valid = false;
        } else {
            if (validation.length() != 11 && validation.length() != 14) {
                docIsNotValid(getString(com.meuvesti.cliente.R.string.campo_doc_invalido));
                valid = false;
            } else {

                if (mHasCpf && mHasCnpj) {

                    if (validation.length() == 11 && !Validacoes.isValidCPF(validation)) {
                        docIsNotValid(getString(com.meuvesti.cliente.R.string.campo_cpf_invalido));
                        valid = false;
                    }

                    if (validation.length() == 14 && !Validacoes.isValidCNPJ(validation)) {
                        docIsNotValid(getString(com.meuvesti.cliente.R.string.campo_cnpj_invalido));
                        valid = false;
                    }

                } else if (mHasCpf) {

                    if (!Validacoes.isValidCPF(validation)) {
                        docIsNotValid(getString(com.meuvesti.cliente.R.string.campo_cpf_invalido));
                        valid = false;
                    }

                } else if (mHasCnpj) {

                    if (!Validacoes.isValidCNPJ(validation)) {
                        docIsNotValid(getString(com.meuvesti.cliente.R.string.campo_cnpj_invalido));
                        valid = false;
                    }
                }
            }
        }

        if (valid == true) {
            docIsValid();
        }

        return valid;
    }

    private void docSetMask() {
        String doc = docGetMask(txtDoc.getText().toString());
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(18);
        txtDoc.setFilters(FilterArray);
        txtDoc.setText(doc);
    }

    private String docGetMask(String doc) {
        doc = doc.replaceAll("[^0-9]", "");
        if (doc.length() == 11 && mHasCpf) {
            doc = String.format("%s.%s.%s-%s", doc.substring(0, 3), doc.substring(3, 6), doc.substring(6, 9), doc.substring(9, 11));
        } else if (doc.length() == 14 && mHasCnpj) {
            doc = String.format("%s.%s.%s/%s-%s", doc.substring(0, 2), doc.substring(2, 5), doc.substring(5, 8), doc.substring(8, 12), doc.substring(12, 14));
        }
        return doc;
    }

    private boolean celularFormIsValid(boolean valid) {
        String cel = txtCelular.getText().toString().replaceAll("[^0-9]", "");
        if (cel.length() != 10 && cel.length() != 11) {
            valid = false;
            celularIsNotValid(getString(com.meuvesti.cliente.R.string.campo_celular_error));
        } else {

            if (txtCelular.getText().toString().isEmpty()) {
                valid = false;
                celularIsNotValid(getString(com.meuvesti.cliente.R.string.campo_celular_error));
            } else {

                if (cel.length() == 10) {
                    cel = String.format("(%s) %s-%s", cel.substring(0, 2), cel.substring(2, 6), cel.substring(6, 10));
                } else if (cel.length() == 11) {
                    cel = String.format("(%s) %s%s-%s", cel.substring(0, 2), cel.substring(2, 3), cel.substring(3, 7), cel.substring(7, 11));
                }

                if (cel.length() > 1) {
                    InputFilter[] FilterArray = new InputFilter[1];
                    FilterArray[0] = new InputFilter.LengthFilter(cel.length() + 2);
                    txtCelular.setFilters(FilterArray);
                    txtCelular.setText(cel);

                    String validation = cel.replaceAll("[^0-9]", "");
                    if (!validation.isEmpty() && (validation.length() == 10 || validation.length() == 11)) {
                        celularIsValid();
                    } else {
                        celularIsNotValid(getString(com.meuvesti.cliente.R.string.campo_celular_error));
                        valid = false;
                    }
                }
            }
        }
        return valid;
    }

    private void callServiceValidateEmail(String email, final Loader.OnLoadCompleteListener onLoadCompleteListener) {
        VestiAPI.get().api(mContext).validaEmail(email).enqueue(new Callback<ConfigurationRequest>() {
            @Override
            public void onResponse(Call<ConfigurationRequest> call, Response<ConfigurationRequest> response) {
                if (response.body().getResult().isSuccess()) {
                    emailIsValid();
                    onLoadCompleteListener.onLoadComplete(null, true);
                } else {
                    emailIsNotValid(getString(R.string.campo_email_ja_associado));
                    onLoadCompleteListener.onLoadComplete(null, false);
                }
            }

            @Override
            public void onFailure(Call<ConfigurationRequest> call, Throwable t) {
                onLoadCompleteListener.onLoadComplete(null, false);
            }
        });
    }

    private void callServiceValidateDoc(String doc, final Loader.OnLoadCompleteListener onLoadCompleteListener) {
        VestiAPI.get().api(mContext).validaDocumento(doc).enqueue(new Callback<ConfigurationRequest>() {
            @Override
            public void onResponse(Call<ConfigurationRequest> call, Response<ConfigurationRequest> response) {
                if (response.body().getResult().isSuccess()) {
                    docIsValid();
                    onLoadCompleteListener.onLoadComplete(null, true);
                } else {
                    docIsNotValid(response.body().getResult().getMessage());
                    onLoadCompleteListener.onLoadComplete(null, false);
                }
            }

            @Override
            public void onFailure(Call<ConfigurationRequest> call, Throwable t) {
                onLoadCompleteListener.onLoadComplete(null, false);
            }
        });
    }

    private void callServiceSave() {
        showLoading(true);

        final String firstName = txtNome.getText().toString().split(" ")[0];
        final String lastName = txtNome.getText().toString().replace(firstName + " ", "");

        User user = new User();
        user.setEmail(txtEmail.getText().toString());
        user.setName(firstName);
        user.setLastname(lastName);
        user.setDocument(txtDoc.getText().toString());
        user.setPhone(txtCelular.getText().toString());
        user.setSocialName(firstName);
        user.setCompanyName(Globals.SCHEME_NAME);
        user.setSchemeUrl(Globals.SCHEME_NAME);
        if (Utils.getSellerid(mContext) != null) {
            user.setSellerId(Utils.getSellerid(mContext));
        }

        String create = null;
        if (mIsCompleteForm) {
            create = "password";
        } else {
            user.setPassword(txtSenha.getText().toString());
            user.setPasswordConfirmation(txtConfirmaSenha.getText().toString());
        }

        VestiAPI.get().api(mContext).cadastro(user, create).enqueue(new Callback<UserResult>() {
            @Override
            public void onResponse(Call<UserResult> call, Response<UserResult> response) {
                if (response.body().getResult().isSuccess()) {

                    Utils.clearAll(CadastroActivity.this);
                    Utils.setSellerid(mContext, null);
                    User userResponse = response.body().getUser();

                    UsuarioRealm ur = new UsuarioRealm();
                    ur.setLogado(false);
                    ur.setNome(userResponse.getName());
                    ur.setToken(response.body().getToken());
                    ur.setId(userResponse.getId());
                    ur.setEmail(userResponse.getEmail());
                    ur.setPhone(userResponse.getPhone());
                    ur.setLastName(userResponse.getLastname());
                    ur.setDocument(userResponse.getCompany().getDocument());
                    ur.setCompanyScheme(userResponse.getCompanies().get(0).getCompanyScheme());
                    //Log.e("xumbrelu", userResponse.getCompanies().toString());

                    RealmService.logar(ur);

                    if(mFinishBuy){
                        setResult(Activity.RESULT_OK);
                        Intent intent = new Intent(CadastroActivity.this, CarrinhoActivity.class);
                        intent.putExtra(CarrinhoActivity.FINISH_BUY, mFinishBuy);
                        startActivity(intent);
                        finish();
                    }else {
                        Intent intent = new Intent(CadastroActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }else{
                    showLoading(false);
                    Toast.makeText(mContext, response.body().getResult().getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserResult> call, Throwable t) {
                findViewById(R.id.box_loading).setVisibility(View.GONE);
                if (t instanceof UnknownHostException) {
                    Toast.makeText(mContext, getString(R.string.internet_error), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
                }
                showLoading(false);
            }
        });
    }

    private void closeKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void celularIsNotValid(String message) {
        findViewById(com.meuvesti.cliente.R.id.bgErroCelular).setBackground(ContextCompat.getDrawable(CadastroActivity.this, com.meuvesti.cliente.R.drawable.shape_line_error));
        imgErroCelular.setBackgroundResource(com.meuvesti.cliente.R.mipmap.icon_exclamcao);
        imgErroCelular.setVisibility(View.VISIBLE);
        new Tip(imgErroCelular, message);
    }

    private void celularIsValid() {
        findViewById(com.meuvesti.cliente.R.id.bgErroCelular).setBackground(ContextCompat.getDrawable(CadastroActivity.this, com.meuvesti.cliente.R.drawable.shape_line_success));
        imgErroCelular.setBackgroundResource(com.meuvesti.cliente.R.mipmap.icon_check);
        imgErroCelular.setVisibility(View.VISIBLE);
    }

    private void docIsNotValid(String message) {
        findViewById(com.meuvesti.cliente.R.id.bgErroDoc).setBackground(ContextCompat.getDrawable(CadastroActivity.this, com.meuvesti.cliente.R.drawable.shape_line_error));
        imgErroDoc.setBackgroundResource(com.meuvesti.cliente.R.mipmap.icon_exclamcao);
        imgErroDoc.setVisibility(View.VISIBLE);
        new Tip(imgErroDoc, message);
    }

    private void docIsValid() {
        findViewById(com.meuvesti.cliente.R.id.bgErroDoc).setBackground(ContextCompat.getDrawable(CadastroActivity.this, com.meuvesti.cliente.R.drawable.shape_line_success));
        imgErroDoc.setBackgroundResource(com.meuvesti.cliente.R.mipmap.icon_check);
        imgErroDoc.setVisibility(View.VISIBLE);
    }

    private void emailIsNotValid(String message) {
        findViewById(com.meuvesti.cliente.R.id.bgErroEmail).setBackground(ContextCompat.getDrawable(CadastroActivity.this, com.meuvesti.cliente.R.drawable.shape_line_error));
        imgErroEmail.setBackgroundResource(com.meuvesti.cliente.R.mipmap.icon_exclamcao);
        imgErroEmail.setVisibility(View.VISIBLE);
        new Tip(imgErroEmail, message);
    }

    private void emailIsValid() {
        findViewById(com.meuvesti.cliente.R.id.bgErroEmail).setBackground(ContextCompat.getDrawable(CadastroActivity.this, com.meuvesti.cliente.R.drawable.shape_line_success));
        imgErroEmail.setBackgroundResource(com.meuvesti.cliente.R.mipmap.icon_check);
        imgErroEmail.setVisibility(View.VISIBLE);
    }

    private void nomeIsNotValid(String message) {
        findViewById(com.meuvesti.cliente.R.id.bgErroNome).setBackground(ContextCompat.getDrawable(CadastroActivity.this, com.meuvesti.cliente.R.drawable.shape_line_error));
        imgErroNome.setBackgroundResource(com.meuvesti.cliente.R.mipmap.icon_exclamcao);
        imgErroNome.setVisibility(View.VISIBLE);
        new Tip(imgErroNome, message);
    }

    private void nomeIsValid() {
        findViewById(com.meuvesti.cliente.R.id.bgErroNome).setBackground(ContextCompat.getDrawable(CadastroActivity.this, com.meuvesti.cliente.R.drawable.shape_line_success));
        imgErroNome.setBackgroundResource(com.meuvesti.cliente.R.mipmap.icon_check);
        imgErroNome.setVisibility(View.VISIBLE);
    }

    private void senhaIsNotValid(String message) {
        findViewById(com.meuvesti.cliente.R.id.bgErroSenha).setBackground(ContextCompat.getDrawable(CadastroActivity.this, com.meuvesti.cliente.R.drawable.shape_line_error));
        imgErroSenha.setBackgroundResource(com.meuvesti.cliente.R.mipmap.icon_exclamcao);
        imgErroSenha.setVisibility(View.VISIBLE);
        new Tip(imgErroSenha, message);
    }

    private void senhaIsValid() {
        findViewById(com.meuvesti.cliente.R.id.bgErroSenha).setBackground(ContextCompat.getDrawable(CadastroActivity.this, com.meuvesti.cliente.R.drawable.shape_line_success));
        imgErroSenha.setBackgroundResource(com.meuvesti.cliente.R.mipmap.icon_check);
        imgErroSenha.setVisibility(View.VISIBLE);
    }

    private void confirmaSenhaIsNotValid(String message) {
        findViewById(com.meuvesti.cliente.R.id.bgErroConfirmaSenha).setBackground(ContextCompat.getDrawable(CadastroActivity.this, com.meuvesti.cliente.R.drawable.shape_line_error));
        imgErroConfirmaSenha.setBackgroundResource(com.meuvesti.cliente.R.mipmap.icon_exclamcao);
        imgErroConfirmaSenha.setVisibility(View.VISIBLE);
        new Tip(imgErroConfirmaSenha, message);
    }

    private void confirmaSenhaIsValid() {
        findViewById(com.meuvesti.cliente.R.id.bgErroConfirmaSenha).setBackground(ContextCompat.getDrawable(CadastroActivity.this, com.meuvesti.cliente.R.drawable.shape_line_success));
        imgErroConfirmaSenha.setBackgroundResource(com.meuvesti.cliente.R.mipmap.icon_check);
        imgErroConfirmaSenha.setVisibility(View.VISIBLE);
    }
}
