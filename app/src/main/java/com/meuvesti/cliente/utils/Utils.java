package com.meuvesti.cliente.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.meuvesti.cliente.activity.LoginActivity;
import com.meuvesti.cliente.model.Image;
import com.meuvesti.cliente.realm.FilterRealmService;
import com.meuvesti.cliente.realm.RealmService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;


/**
 * Created by hersonrodrigues on 14/01/17.
 */
public class Utils {

    private static boolean firstGradTooltip;

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void closeKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static void saveDateLastFilter(Context context, long time) {
        Log.w("Utils", "Salvou o filtro em: " + time);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong("filterLastDate", time);
        editor.commit();
    }

    public static long getDateLastFilter(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getLong("filterLastDate", 0);
    }

    public static void saveWhatsAppLink(Context context, String link) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("link", link);
        editor.commit();
    }

    public static String getWhatsAppLink(Context context) {
        try {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            return sharedPref.getString("link", null);
        } catch (NullPointerException e) {
            return null;
        }
    }


    public static void setHasFilter(Context context, boolean hasFilter) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("hasFilter", hasFilter);
        editor.commit();
    }

    public static boolean getHasFilter(Context context) {
        try {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            return sharedPref.getBoolean("hasFilter", false);
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static void setObs(Context context, String text) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("spObs", text);
        editor.commit();
    }

    public static String getObs(Context context) {
        try {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            return sharedPref.getString("spObs", "");
        } catch (NullPointerException e) {
            return "";
        }
    }

    public static void setFirstGradTooltip(Context context) {
        try {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("firsTimeGradTooltip", false);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isFirstGradTooltip(Context context) {
        try {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            return sharedPref.getBoolean("firsTimeGradTooltip", true);
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static int getRandomId() {
        Random r = new Random();
        return (r.nextInt(Integer.MAX_VALUE) + 1);
    }

    public static void setSellerid(Context context, String text) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("spSellerId", text);
        editor.commit();
    }

    public static String getSellerid(Context context) {
        try {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            return sharedPref.getString("spSellerId", null);
        } catch (NullPointerException e) {
            return "";
        }
    }

    public static void setConfigCPF(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("spConfigCPF", true);
        editor.commit();
    }

    public static boolean getConfigCPF(Context context) {
        try {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            return sharedPref.getBoolean("spConfigCPF", false);
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static void setConfigCNPJ(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("spConfigCNPJ", true);
        editor.commit();
    }

    public static boolean getConfigCNPJ(Context context) {
        try {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            return sharedPref.getBoolean("spConfigCNPJ", false);
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static void clearAll(Context context) {
        setStokEdit(context, false);

        FilterRealmService.uncheckFilters();
        // Marca true pra forcar o catalogo basico
        Utils.setHasFilter(context, true);
        // Limpa lista de whatsapp no login
        Utils.saveWhatsAppLink(context, null);
        RealmService.clearConfig();
    }

    public static void clearCookies(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Log.d("WebviewClearCache", "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else
        {
            Log.d("WebviewClearCache", "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr=CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager=CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    public static void clearCPFCNPJ(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("spConfigCPF", false);
        editor.putBoolean("spConfigCNPJ", false);
        editor.commit();
    }

    public static void setStokEdit(Context context, boolean flag) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("stokEdit", flag);
        editor.commit();
    }

    public static boolean getStokEdit(Context context) {
        try {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            return sharedPref.getBoolean("stokEdit", false);
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static void setCustomerSync(Context context, boolean flag) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("syncPermission", flag);
        editor.commit();
    }

    public static boolean getCustomerSync(Context context) {
        try {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            return sharedPref.getBoolean("syncPermission", false);
        } catch (NullPointerException e) {
            return false;
        }
    }


    public static void setCommunClientBiotipo(Context context, boolean flag) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("spSpecialClient", flag);
        editor.commit();
    }

    public static boolean getCommunClientBiotipo(Context context) {
        try {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            return sharedPref.getBoolean("spSpecialClient", false);
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static String getDateCompiled(Context context) {
        PackageManager pm = context.getPackageManager();
        ApplicationInfo appInfo = null;
        try {
            appInfo = pm.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String appFile = appInfo.sourceDir;
        long installed = new File(appFile).lastModified();

        Date date=new Date(installed);
        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy hh:mm:ss");
        String dateText = df2.format(date);
        return dateText;
    }

    public static boolean isUsingCategory(Context context) {
        try {
            return Utils.getConfigsParameter(context) != null
                    && Utils.getConfigsParameter(context).has("filters")
                    && Utils.getConfigsParameter(context)
                    .get("filters")
                    .getAsJsonObject()
                    .get("categories")
                    .getAsInt() == 1;
        } catch (Exception e) {
            return false;
        }
    }

    public static void setConfigsParameter(Context context, JsonObject jsonData) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(jsonData);
        editor.putString("configParameter", json);
        editor.commit();
    }

    public static JsonObject getConfigsParameter(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString = sharedPref.getString("configParameter", null);
        Gson gson = new Gson();
        return gson.fromJson(jsonString, JsonObject.class);
    }

    public static String getCategories(Context context) {
        List<String> filtroGeralIds = CategoriaDB.listarIdsSelecionadas(context);
        List<String> filtroFilhosIds = CategoriaDB.listarIdsFilhos(context);
        List<String> filtroNetosIds = CategoriaDB.listarIdsNetos(context);
        if (filtroNetosIds != null && !filtroNetosIds.isEmpty()) {
            return covetListToId(filtroNetosIds);
        } else if (filtroFilhosIds != null && !filtroFilhosIds.isEmpty()) {
            return covetListToId(filtroFilhosIds);
        } else {
            return covetListToId(filtroGeralIds);
        }
    }

    public static String covetListToId(List<String> lista) {
        String ids = "";
        for (String fr : lista) {
            ids += fr + "_";
        }
        if (ids != null && ids.length() > 1) {
            ids = ids.substring(0, ids.length() - 1);
        }

        if (ids.equals("")) {
            return null;
        }

        return ids;
    }

    public static void setConfigSearchEnableTags(Context context, boolean b) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("configSearchTag", b);
        editor.commit();
    }

    public static boolean getConfigSearchEnableTags(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getBoolean("configSearchTag", false);
    }
}
