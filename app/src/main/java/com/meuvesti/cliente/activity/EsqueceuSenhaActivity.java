package com.meuvesti.cliente.activity;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.meuvesti.cliente.R;
import com.meuvesti.cliente.model.BaseResult;
import com.meuvesti.cliente.model.Usuario;
import com.meuvesti.cliente.service.VestiAPI;
import com.meuvesti.cliente.utils.Utils;
import com.meuvesti.cliente.utils.VestiActionBar;

import java.net.UnknownHostException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EsqueceuSenhaActivity extends AppCompatActivity {

    TextView txtLogin, txtEmail, txtEnviado;
    private VestiActionBar mVestiActionBar;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null){
            this.overridePendingTransition(R.anim.slide_in_left,
                    R.anim.slide_out_left);
        }

        mContext = EsqueceuSenhaActivity.this;

        mVestiActionBar = new VestiActionBar(this);
        mVestiActionBar.hideTitle();
        mVestiActionBar.setContentView(R.layout.activity_esqueceu_senha);
        setContentView(mVestiActionBar.getMainContent());
        mVestiActionBar.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        txtLogin = (TextView) findViewById(R.id.txtLogin);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtEnviado = (TextView) findViewById(R.id.txtEnviado);
        txtLogin.setPaintFlags(txtLogin.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btnSenha).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(getBaseContext(), R.anim.scale);
                v.startAnimation(anim);

                boolean valid = true;
                findViewById(R.id.bgErroEmail).setBackground(ContextCompat.getDrawable(EsqueceuSenhaActivity.this, R.drawable.shape_line_success));
                findViewById(R.id.imgErroEmail).setVisibility(View.GONE);
                if (txtEmail.getText().toString().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(txtEmail.getText().toString()).matches()) {
                    valid = false;
                    findViewById(R.id.bgErroEmail).setBackground(ContextCompat.getDrawable(EsqueceuSenhaActivity.this, R.drawable.shape_line_error));
                    final ImageView imgErrorSenha = (ImageView) findViewById(R.id.imgErroEmail);
                    Toast.makeText(EsqueceuSenhaActivity.this, getString(R.string.campo_email_invalido), Toast.LENGTH_LONG).show();
                }
                if (valid) {
                    if (!Utils.isNetworkAvailable(EsqueceuSenhaActivity.this)) {
                        Toast.makeText(EsqueceuSenhaActivity.this, getString(R.string.internet_error), Toast.LENGTH_LONG).show();
                    } else {
                        recuperarSenha();
                    }
                }
            }
        });
    }

    private void recuperarSenha() {
        findViewById(R.id.box_loading).setVisibility(View.VISIBLE);

        Usuario usuario = new Usuario();
        usuario.setEmail(txtEmail.getText().toString());

        VestiAPI.get().api(EsqueceuSenhaActivity.this).recoveryPassword(usuario).enqueue(new Callback<BaseResult>() {
            @Override
            public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {
                if(response.body().getResult().isSuccess()) {
                    Utils.closeKeyboard(EsqueceuSenhaActivity.this);
                    findViewById(R.id.box_loading).setVisibility(View.GONE);
                    String[] email = txtEmail.getText().toString().split("@");
                    String parte1 = email[0].substring(0, 3);
                    String asteristico = email[0].substring(3, email[0].length());
                    String emailFinal = parte1 + asteristico.replaceAll(".", "*") + "@" + email[1];
                    txtEnviado.setText(String.format("Solicitação enviada com sucesso!\nVerifique seu e-mail %s\npara obter a nova senha.", emailFinal));
                    findViewById(R.id.txtEnviado).setVisibility(View.VISIBLE);
                    findViewById(R.id.linForm).setVisibility(View.GONE);
                }else{
                    Toast.makeText(mContext, response.body().getResult().getMessage(), Toast.LENGTH_LONG).show();
                }
                findViewById(R.id.box_loading).setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<BaseResult> call, Throwable t) {
                findViewById(R.id.box_loading).setVisibility(View.GONE);
                if (t instanceof UnknownHostException) {
                    Toast.makeText(mContext, getString(R.string.internet_error), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
