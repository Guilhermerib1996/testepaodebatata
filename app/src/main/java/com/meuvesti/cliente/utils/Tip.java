package com.meuvesti.cliente.utils;

import android.graphics.Color;
import android.os.Handler;
import android.view.View;

import com.tooltip.OnDismissListener;
import com.tooltip.Tooltip;

/**
 * Created by hersonrodrigues on 15/01/17.
 */
public class Tip {

    private static final long TIMEOUT = 3000;
    private final Tooltip tooltip;
    private boolean show = false;
    private String text;
    private View view;

    public Tip(final View view, String text) {
        this.view = view;

        tooltip = new Tooltip.Builder(view)
                .setBackgroundColor(Color.BLACK)
                .setTextColor(Color.WHITE)
                .setTextSize(12f)
                .setPadding(15f)
                .setCornerRadius(20f)
                .setDismissOnClick(true)
                .setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        show = false;
                    }
                })
                .setText(text)
                .show();

        show();

    }

    public void show() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tooltip.dismiss();
            }
        }, TIMEOUT);
    }

    public void hide() {
        tooltip.dismiss();
    }

    public void toggle() {
        if (tooltip.isShowing()) {
            tooltip.dismiss();
        } else {
            tooltip.show();
        }
    }

    public boolean isShow() {
        return tooltip.isShowing();
    }
}
