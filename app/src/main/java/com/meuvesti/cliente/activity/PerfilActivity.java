package com.meuvesti.cliente.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.meuvesti.cliente.R;
import com.meuvesti.cliente.model.ConfigurationRequest;
import com.meuvesti.cliente.model.User;
import com.meuvesti.cliente.model.UserResult;
import com.meuvesti.cliente.realm.RealmService;
import com.meuvesti.cliente.realm.UsuarioRealm;
import com.meuvesti.cliente.service.VestiAPI;
import com.meuvesti.cliente.utils.Tip;
import com.meuvesti.cliente.utils.Utils;
import com.meuvesti.cliente.utils.VestiActionBar;

import java.net.UnknownHostException;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilActivity extends AppCompatActivity {

    public static final int REQUEST = 123;
    EditText txtDoc, txtNome, txtEmail, txtCelular;
    private VestiActionBar mVestiActionBar;
    private UsuarioRealm mUsuario;
    private View navHeaderView, imgErroCelular, btnRegistrar;
    private TextView txtFakeCelular;
    private ImageView imgErroNome, imgErroEmail;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            this.overridePendingTransition(
                    R.anim.slide_in_left,
                    R.anim.slide_out_left);
        }

        mUsuario = RealmService.getUsuarioLogado(this);
        mVestiActionBar = new VestiActionBar(this);
        mVestiActionBar.setTitle(getString(R.string.menu_editar_perfil));
        mVestiActionBar.setContentView(R.layout.fragment_editar_cadastro);
        setContentView(mVestiActionBar.getMainContent());

        mVestiActionBar.setOnHomeClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVestiActionBar.openMenu();
            }
        });

        txtDoc = (EditText) findViewById(R.id.txtDoc);
        txtNome = (EditText) findViewById(R.id.txtNome);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtCelular = (EditText) findViewById(R.id.txtCelular);
        txtFakeCelular = (TextView) findViewById(R.id.txtFakeCelular);
        imgErroNome = (ImageView) findViewById(R.id.imgErroNome);
        imgErroCelular = findViewById(R.id.imgErroCelular);
        imgErroEmail = (ImageView) findViewById(R.id.imgErroEmail);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        mUsuario = RealmService.getUsuarioLogado(this);
        mContext = PerfilActivity.this;

        if (mUsuario == null) {
            Toast.makeText(mContext, getString(R.string.requer_login), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            configureData();
            configureFields();
        }

        configureNavgation();
        configureUserDataInMenu();
        Utils.closeKeyboard(this);
    }

    private void configureNavgation() {
        NavigationView navigationView = (NavigationView) mVestiActionBar.getMainContent().findViewById(R.id.nav_view);
        navHeaderView = navigationView.getHeaderView(0);
        View menuLogin = navHeaderView.findViewById(R.id.menu_login);
        View menuSair = navHeaderView.findViewById(R.id.menu_sair);
        View menuCatalogo = navHeaderView.findViewById(R.id.menu_catalogo);
        View menuPerfil = navHeaderView.findViewById(R.id.menu_perfil);

        menuSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
        menuLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PerfilActivity.this, LoginActivity.class));
                finish();
            }
        });
        menuCatalogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        menuPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVestiActionBar.closeMenu(true);
            }
        });
    }

    private void configureUserDataInMenu() {
        String username = mUsuario.getNome();
        if(mUsuario.getNome().length() >= 2){
            TextView nomeUsuario = (TextView) navHeaderView.findViewById(R.id.txtNomeUsuario);
            nomeUsuario.setText(username.substring(0, 2));
            TextView nome = (TextView) navHeaderView.findViewById(R.id.txtNome);
            nome.setText(username);
        }else{
            TextView nomeUsuario = (TextView) navHeaderView.findViewById(R.id.txtNomeUsuario);
            nomeUsuario.setText(username.substring(0, 1));
            TextView nome = (TextView) navHeaderView.findViewById(R.id.txtNome);
            nome.setText(username);
        }
    }

    private void configureData() {
        String mask = docGetMask(mUsuario.getDocument());
        txtDoc.setText(mask);

        txtNome.setText(String.format("%s %s", mUsuario.getNome(), mUsuario.getLastName()));
        txtEmail.setText(mUsuario.getEmail());

        String celular = "";
        try {
            if (mUsuario != null && mUsuario.getPhone() != null) {
                celular = mUsuario.getPhone().replaceAll("[^0-9]", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        txtCelular.setText(celular);
        txtFakeCelular.setText(mUsuario.getPhone());
        txtFakeCelular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtFakeCelular.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        txtCelular.requestLayout();
                        txtCelular.requestFocus();
                        txtCelular.setSelection(txtCelular.getText().toString().length());
                        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(txtCelular, InputMethodManager.SHOW_IMPLICIT);
                    }
                }, 100);
                txtCelular.setVisibility(View.VISIBLE);
            }
        });
    }

    private void configureFields() {
        findViewById(R.id.imgErroNome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nomeIsNotValid(getString(R.string.campo_nome_obrigatorio));
            }
        });

        imgErroEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailIsNotValid(getString(R.string.campo_email_obrigatorio));
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

        txtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(txtEmail.getText().toString()).matches()) {
                        emailIsNotValid(getString(R.string.campo_email_invalido));
                    } else {
                        callServiceValidateEmail(new Loader.OnLoadCompleteListener() {
                            @Override
                            public void onLoadComplete(Loader loader, Object data) {
                                boolean validate = (boolean) data;
                                if (validate) {
                                    callServiceEditForm();
                                }
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
                Animation anim = AnimationUtils.loadAnimation(PerfilActivity.this, R.anim.scale);
                v.startAnimation(anim);

                boolean valid = true;

                valid = nomeFormIsValid(valid);
                valid = emailFormIsValid(valid);
                valid = celularFormIsValid(valid);

                if (valid) {
                    if (!Utils.isNetworkAvailable(mContext)) {
                        Toast.makeText(mContext, getString(R.string.internet_error), Toast.LENGTH_LONG).show();
                    } else {
                        service();
                    }
                }
            }
        });
    }

    private void callServiceValidateEmail(final Loader.OnLoadCompleteListener onLoadCompleteListener) {
        findViewById(R.id.box_loading).setVisibility(View.VISIBLE);
        if (!txtEmail.getText().toString().equals(mUsuario.getEmail())) {
            VestiAPI.get().api(mContext).validaEmail(txtEmail.getText().toString()).enqueue(new Callback<ConfigurationRequest>() {
                @Override
                public void onResponse(Call<ConfigurationRequest> call, Response<ConfigurationRequest> response) {
                    if (response.body().getResult().isSuccess()) {
                        emailIsValid();
                        onLoadCompleteListener.onLoadComplete(null, true);
                    } else {
                        emailIsNotValid(getString(R.string.campo_email_ja_associado));
                        onLoadCompleteListener.onLoadComplete(null, false);
                    }
                    findViewById(R.id.box_loading).setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<ConfigurationRequest> call, Throwable t) {
                    onLoadCompleteListener.onLoadComplete(null, false);
                    findViewById(R.id.box_loading).setVisibility(View.GONE);
                }
            });
        }else{
            onLoadCompleteListener.onLoadComplete(null, true);
        }
    }

    private boolean celularFormIsValid(boolean valid) {
        String cel = txtCelular.getText().toString().replaceAll("[^0-9]", "");
        if (cel.length() != 10 && cel.length() != 11) {
            valid = false;
            celularIsNotValid(getString(R.string.campo_celular_error));
        } else {

            if (txtCelular.getText().toString().isEmpty()) {
                valid = false;
                celularIsNotValid(getString(R.string.campo_celular_error));
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
                        celularIsNotValid(getString(R.string.campo_celular_error));
                        valid = false;
                    }
                }
            }
        }
        return valid;
    }

    private void service() {
        Utils.closeKeyboard(this);
        callServiceValidateEmail(new Loader.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(Loader loader, Object data) {
                boolean validate = (boolean) data;
                if (validate) {
                    callServiceEditForm();
                }
            }
        });
    }

    private void callServiceEditForm() {
        findViewById(R.id.box_loading).setVisibility(View.VISIBLE);

        final String firstName = txtNome.getText().toString().split(" ")[0];
        final String lastName = txtNome.getText().toString().replace(firstName + " ", "");

        User user = new User();
        user.setId(mUsuario.getId());
        user.setName(firstName);
        user.setLastname(lastName);
        user.setEmail(txtEmail.getText().toString());
        user.setPhone(txtCelular.getText().toString());
        user.setCompanyName(firstName);
        user.setSocialName(firstName);

        VestiAPI.get().api(mContext).editiForm(mUsuario.getId(), user).enqueue(new Callback<UserResult>() {
            @Override
            public void onResponse(Call<UserResult> call, Response<UserResult> response) {
                if (response.body().getResult().isSuccess()) {
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();

                    mUsuario.setNome(firstName);
                    mUsuario.setLastName(lastName);
                    mUsuario.setEmail(txtEmail.getText().toString());
                    mUsuario.setPhone(txtCelular.getText().toString());

                    realm.commitTransaction();
                    realm.close();

                    Toast.makeText(mContext, response.body().getResult().getMessage(), Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(mContext, getString(R.string.processamento_error), Toast.LENGTH_SHORT).show();
                }

                findViewById(R.id.box_loading).setVisibility(View.GONE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.box_loading).setVisibility(View.GONE);
                        Utils.closeKeyboard(PerfilActivity.this);
                    }
                }, 500);
            }

            @Override
            public void onFailure(Call<UserResult> call, Throwable t) {
                findViewById(R.id.box_loading).setVisibility(View.GONE);
                if (t instanceof UnknownHostException) {
                    Toast.makeText(mContext, getString(R.string.internet_error), Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private String docGetMask(String doc) {
        doc = doc.replaceAll("[^0-9]", "");
        if (doc.length() == 11) {
            doc = String.format("%s.%s.%s-%s", doc.substring(0, 3), doc.substring(3, 6), doc.substring(6, 9), doc.substring(9, 11));
        } else if (doc.length() == 14) {
            doc = String.format("%s.%s.%s/%s-%s", doc.substring(0, 2), doc.substring(2, 5), doc.substring(5, 8), doc.substring(8, 12), doc.substring(12, 14));
        }
        return doc;
    }

    private boolean emailFormIsValid(boolean valid) {
        if (txtEmail.getText().toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(txtEmail.getText().toString()).matches()) {
            emailIsNotValid(getString(R.string.campo_email_obrigatorio));
            valid = false;
        } else {
            emailIsValid();
        }
        return valid;
    }

    private boolean nomeFormIsValid(boolean valid) {
        if (txtNome.getText().toString().isEmpty()) {
            nomeIsNotValid(getString(R.string.campo_nome_obrigatorio));
            valid = false;
        } else {
            nomeIsValid();
        }
        return valid;
    }

    private void celularIsNotValid(String message) {
        findViewById(R.id.bgErroCelular).setBackground(ContextCompat.getDrawable(mContext, R.drawable.shape_line_error));
        imgErroCelular.setBackgroundResource(R.mipmap.icon_exclamcao);
        imgErroCelular.setVisibility(View.VISIBLE);
        new Tip(imgErroCelular, message);
    }

    private void celularIsValid() {
        findViewById(R.id.bgErroCelular).setBackground(ContextCompat.getDrawable(mContext, R.drawable.shape_line_success));
        imgErroCelular.setBackgroundResource(R.mipmap.icon_check);
        imgErroCelular.setVisibility(View.VISIBLE);
    }

    private void emailIsNotValid(String message) {
        findViewById(R.id.bgErroEmail).setBackground(ContextCompat.getDrawable(mContext, R.drawable.shape_line_error));
        imgErroEmail.setBackgroundResource(R.mipmap.icon_exclamcao);
        imgErroEmail.setVisibility(View.VISIBLE);
        new Tip(imgErroEmail, message);
    }

    private void emailIsValid() {
        findViewById(R.id.bgErroEmail).setBackground(ContextCompat.getDrawable(mContext, R.drawable.shape_line_success));
        imgErroEmail.setBackgroundResource(R.mipmap.icon_check);
        imgErroEmail.setVisibility(View.VISIBLE);
    }

    private void nomeIsNotValid(String message) {
        findViewById(R.id.bgErroNome).setBackground(ContextCompat.getDrawable(mContext, R.drawable.shape_line_error));
        imgErroNome.setBackgroundResource(R.mipmap.icon_exclamcao);
        imgErroNome.setVisibility(View.VISIBLE);
        new Tip(imgErroNome, message);
    }

    private void nomeIsValid() {
        findViewById(R.id.bgErroNome).setBackground(ContextCompat.getDrawable(mContext, R.drawable.shape_line_success));
        imgErroNome.setBackgroundResource(R.mipmap.icon_check);
        imgErroNome.setVisibility(View.VISIBLE);
    }
}
