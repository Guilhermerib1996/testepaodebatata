package com.meuvesti.cliente;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;

import java.lang.reflect.Field;

/**
 * Created by FabianoVasconcelos on 16/12/16.
 */

public final class FontsOverride {

    public static void setDefaultFont(Context context,
                                      String staticTypefaceFieldName, String fontAssetName) {
        final Typeface regular = Typeface.createFromAsset(context.getAssets(),
                fontAssetName);
        replaceFont(staticTypefaceFieldName, regular);
    }

    protected static void replaceFont(String staticTypefaceFieldName,
                                      final Typeface newTypeface) {
        try {
            final Field staticField = Typeface.class
                    .getDeclaredField(staticTypefaceFieldName);
            staticField.setAccessible(true);
            staticField.set(null, newTypeface);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Typeface fontMedium(AssetManager assets) {
        return Typeface.createFromAsset(assets, "fonts/FUTURASTD-MEDIUM.OTF");
    }

    public static Typeface fontHeavy(AssetManager assets) {
        return Typeface.createFromAsset(assets, "fonts/FUTURASTD-HEAVY.OTF");
    }

    public static Typeface fontBook(AssetManager assets) {
        return Typeface.createFromAsset(assets, "fonts/FUTURASTD-BOOK.OTF");
    }
}