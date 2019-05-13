package com.meuvesti.cliente.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.meuvesti.cliente.R;
import com.meuvesti.cliente.model.BaseResult;
import com.meuvesti.cliente.model.Carrinho;
import com.meuvesti.cliente.model.Usuario;
import com.meuvesti.cliente.realm.RealmService;
import com.meuvesti.cliente.realm.UsuarioRealm;
import com.meuvesti.cliente.service.VestiAPI;
import com.meuvesti.cliente.utils.Globals;
import com.meuvesti.cliente.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WebviewActivity extends AppCompatActivity {

    public static String REQUEST_FACEBOOK = "request_facebook";
    public static String REQUEST_GOOGLE = "request_google";

    private Context mContext;
    private WebView mWebview;
    private FrameLayout mContainer;

    private ProgressDialog mProgress;
    private boolean mIsFacebook;
    private boolean mIsGoogle;
    private View mBoxBlank;
    private boolean mFinishBuy = false;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        mContext = WebviewActivity.this;

        Bundle bundle = getIntent().getExtras();

        mIsFacebook = bundle.getBoolean(REQUEST_FACEBOOK, false);
        mIsGoogle = bundle.getBoolean(REQUEST_GOOGLE, false);

        if (bundle!=null && bundle.containsKey(CarrinhoActivity.FINISH_BUY)){
            mFinishBuy = getIntent().getExtras().getBoolean(CarrinhoActivity.FINISH_BUY, false);
        }

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);

        mProgress = new ProgressDialog(mContext);
        mProgress.setIndeterminate(true);
        mProgress.setMessage(getString(R.string.login_message));
        mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgress.show();

        mBoxBlank = findViewById(R.id.box_blank);

        mContainer = (FrameLayout) findViewById(R.id.webview_frame);
        mWebview = (WebView) findViewById(R.id.webview);
        mWebview.setWebViewClient(new UriWebViewClient());

        //mWebview.setWebChromeClient(new UriChromeClient());

        mWebview.clearCache(true);
        Utils.clearCookies(WebviewActivity.this);

        WebSettings webSettings = mWebview.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setUserAgentString("Mozilla/5.0 (iPhone; CPU iPhone OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5376e Safari/8536.25");
        webSettings.setSupportMultipleWindows(true);

        if (mIsFacebook) {
            mWebview.loadUrl(Globals.URL_SOCIAL_FACEBOOK_URL);
        } else if (mIsGoogle) {
            mWebview.loadUrl(Globals.URL_SOCIAL_GOOGLE_URL);
        }
        mWebview.setVisibility(View.GONE);
    }

    private class UriWebViewClient extends WebViewClient {//WebChromeClient

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(WebviewActivity.this, "Falha ao carregar as informações: " + description, Toast.LENGTH_SHORT).show();
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String host = Uri.parse(url).getHost();
            if (host.equals(Globals.URL_SOCIAL_PREFIX)) {
                mWebview.setVisibility(View.GONE);

                mWebview.setWebChromeClient(new WebChromeClient() {
                    public boolean onConsoleMessage(ConsoleMessage cmsg) {

                        if (cmsg.message().startsWith("MAGIC")) {
                            String jsonHtml = cmsg.message().replace("MAGIC<head></head><body><pre style=\"word-wrap: break-word; white-space: pre-wrap;\">", "").replace("</pre></body>", "");
                            return configureJsonHtml(jsonHtml);
                        }

                        return false;
                    }
                });

                mWebview.setWebViewClient(new WebViewClient() {
                    public void onPageFinished(WebView view, String address) {
                        view.loadUrl("javascript:document.getElementsByTagName('html)[0].setAttribute('style',display:none;');");
                        view.loadUrl("javascript:console.log('MAGIC'+document.getElementsByTagName('html')[0].innerHTML);");
                    }
                });

                return false;
            } else {
                mWebview.setVisibility(View.VISIBLE);
                mProgress.dismiss();
                return false;
            }
        }
    }

    private boolean configureJsonHtml(String jsonHtml) {
        try {
            JSONObject mainObject = new JSONObject(jsonHtml);

            boolean finish = false;

            if (mainObject.has("result")) {
                boolean success = (boolean) mainObject.getJSONObject("result").get("success");
                if (success == false) {
                    Toast.makeText(mContext, (CharSequence) mainObject.getJSONObject("result").get("message"), Toast.LENGTH_LONG).show();
                    finish = true;
                }
            }

            mProgress.dismiss();

            if (!finish) {
                if (mIsFacebook) {
                    facebookAuth(mainObject);
                } else if (mIsGoogle) {
                    googleAuth(mainObject);
                }
            } else {
                finish();
            }

        } catch (JSONException e) {
            Toast.makeText(mContext, "Erro ao recuperar dados do usuário", Toast.LENGTH_LONG).show();
        }
        return true;
    }

    private void facebookAuth(JSONObject mainObject) {
        boolean hasToken = mainObject.has("token");

        String name = "";
        String email = "";
        String phone = "";

        try {
            name = (String) mainObject.getJSONObject("user").get("name");
            email = (String) mainObject.getJSONObject("user").get("email");
            if (mainObject.getJSONObject("user").has("phone")) {
                phone = (String) mainObject.getJSONObject("user").get("phone");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        String lastName = "";
        lastName = TextUtils.join(" ", name.split(" ")).replace(name, "");

        if (hasToken) {

            Utils.clearAll(WebviewActivity.this);

            try {
                UsuarioRealm ur = new UsuarioRealm();
                ur.setNome(name);
                ur.setToken((String) mainObject.get("token"));
                ur.setId((String) mainObject.getJSONObject("user").get("id"));
                ur.setEmail(email);
                ur.setPhone(phone);
                ur.setLastName(lastName);
                ur.setDocument((String) mainObject.getJSONObject("user").getJSONObject("company").get("tax_document"));
                RealmService.logar(ur);

                finializeLogin();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            callIntentStart(name, email, lastName);
        }
    }

    private void callIntentStart(final String name, final String email, final String lastName) {

            Utils.setStokEdit(mContext, false);

            VestiAPI.get().api(mContext).profilePermStockEdit().enqueue(new Callback<BaseResult>() {
                @Override
                public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {

                    if(response.body()!=null
                            && response.body().getResult()!=null
                            && response.body().getResult().isSuccess()){
                        Utils.setStokEdit(mContext, true);
                    }

                    loginAfterProfile(name, email, lastName);
                }

                @Override
                public void onFailure(Call<BaseResult> call, Throwable t) {
                    loginAfterProfile(name, email, lastName);
                }
            });
    }

    private void loginAfterProfile(String name, String email, String lastName) {
        Intent i = new Intent(WebviewActivity.this, CadastroActivity.class);
        i.putExtra(CadastroActivity.REQUEST_COMPLETE_FORM, true);
        i.putExtra(CadastroActivity.REQUEST_NAME, name);
        i.putExtra(CadastroActivity.REQUEST_LASTNAME, lastName);
        i.putExtra(CadastroActivity.REQUEST_EMAIL, email);
        i.putExtra(CarrinhoActivity.FINISH_BUY, mFinishBuy);

        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(i, LoginActivity.REQUEST_CLOSE_LOGIN);

        finish();
    }

    private void googleAuth(JSONObject mainObject) throws JSONException {
        boolean hasToken = mainObject.has("token");
        String lastName = "";
        String phone = "";

        String name = (String) mainObject.getJSONObject("user").get("name");
        if (mainObject.getJSONObject("user").has("lastname")) {
            lastName = (String) mainObject.getJSONObject("user").get("lastname");
        } else {
            lastName = TextUtils.join(" ", name.split(" ")).replace(name, "");
        }
        String email = (String) mainObject.getJSONObject("user").get("email");

        if (mainObject.getJSONObject("user").has("phone")
                && !mainObject.getJSONObject("user").get("phone").toString().equals("null")) {
            phone = (String) mainObject.getJSONObject("user").get("phone");
        }

        if (hasToken) {
            String token = (String) mainObject.get("token");
            String id = (String) mainObject.getJSONObject("user").get("id");
            String document = (String) mainObject.getJSONObject("user").getJSONObject("company").get("tax_document");

            Utils.clearAll(WebviewActivity.this);

            UsuarioRealm ur = new UsuarioRealm();
            ur.setNome(name);
            ur.setToken(token);
            ur.setId(id);
            ur.setEmail(email);
            ur.setPhone(phone);
            ur.setLastName(lastName);
            ur.setDocument(document);

            RealmService.logar(ur);

            finializeLogin();

        } else {
            callIntentStart(name, email, lastName);
        }
    }

    private void finializeLogin() {
        Intent intent;
        if(mFinishBuy){
            setResult(Activity.RESULT_OK);
            intent = new Intent(WebviewActivity.this, CarrinhoActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(CarrinhoActivity.FINISH_BUY, mFinishBuy);
        }else {
            intent = new Intent(WebviewActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        startActivity(intent);
        finish();
    }
}

