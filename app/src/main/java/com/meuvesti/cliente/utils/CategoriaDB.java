package com.meuvesti.cliente.utils;

/**
 * Created by Herson Rodrigues <hersonrodrigues@gmail.com> on 11/02/2018.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meuvesti.cliente.model.Category;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Herson Rodrigues <hersonrodrigues@gmail.com> on 01/02/2018.
 */

public class CategoriaDB {

    public static void limpar(Context context) {
        List<Category> lista = new ArrayList<>();
        salvar(context, lista);
        List<String> listaIds = new ArrayList<>();
        salvarIds(context, listaIds);
    }

    public static void salvar(Context context, List<Category> lista) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(lista);
        editor.putString("categorias1", json);
        editor.commit();
    }

    public static List<Category> listarTodas(Context context) {
        List<Category> lista = new ArrayList<>();
        try {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            String json = sharedPref.getString("categorias1", null);
            if (json != null) {
                Type listType = new TypeToken<List<Category>>() {
                }.getType();
                return new Gson().fromJson(json, listType);
            } else {
                return lista;
            }
        } catch (Exception e) {
            return lista;
        }
    }

    public static void salvarIds(Context context, List<String> lista) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(lista);
        editor.putString("categorias_ids1", json);
        editor.commit();
    }

    public static void salvarIdsFilhos(Context context, List<String> lista) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(lista);
        editor.putString("categorias_ids_filhos1", json);
        editor.commit();
    }

    public static List<String> listarIdsFilhos(Context context) {
        List<String> lista = new ArrayList<>();
        try {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            String json = sharedPref.getString("categorias_ids_filhos1", null);
            if (json != null) {
                Type listType = new TypeToken<List<String>>() {
                }.getType();
                return new Gson().fromJson(json, listType);
            } else {
                return lista;
            }
        } catch (Exception e) {
            return lista;
        }
    }

    public static void salvarIdsNetos(Context context, List<String> lista) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(lista);
        editor.putString("categorias_ids_netos1", json);
        editor.commit();
    }

    public static List<String> listarIdsNetos(Context context) {
        List<String> lista = new ArrayList<>();
        try {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            String json = sharedPref.getString("categorias_ids_netos1", null);
            if (json != null) {
                Type listType = new TypeToken<List<String>>() {
                }.getType();
                return new Gson().fromJson(json, listType);
            } else {
                return lista;
            }
        } catch (Exception e) {
            return lista;
        }
    }

    public static List<String> listarIdsSelecionadas(Context context) {
        List<String> lista = new ArrayList<>();
        try {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            String json = sharedPref.getString("categorias_ids1", null);
            if (json != null) {
                Type listType = new TypeToken<List<String>>() {
                }.getType();
                return new Gson().fromJson(json, listType);
            } else {
                return lista;
            }
        } catch (Exception e) {
            return lista;
        }
    }

    public static void salvarTotalBadge(Context context, int totalBage) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("categorias_badge_total1", totalBage);
        editor.commit();
    }

    public static int getTotalBadge(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        int total = sharedPref.getInt("categorias_badge_total1", 0);
        return total;
    }

    public static void clear(Context context) {
        salvarTotalBadge(context, 0);
        CategoriaDB.salvar(context, new ArrayList<Category>());
        CategoriaDB.salvarIds(context, new ArrayList<String>());
        CategoriaDB.salvarIdsFilhos(context, new ArrayList<String>());
        CategoriaDB.salvarIdsNetos(context, new ArrayList<String>());
        CategoriaDB.salvarTotalBadge(context, 0);
    }
}

