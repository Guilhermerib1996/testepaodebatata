package com.meuvesti.cliente.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meuvesti.cliente.R;
import com.meuvesti.cliente.iclass.OnCheckedChangeListener;

/**
 * Created by Herson Rodrigues <hersonrodrigues@gmail.com> on 11/02/2018.
 */

public class VestiSwitchButton extends LinearLayout implements Checkable {

    private boolean mChecked;
    private String mTexto;
    private TextView mTextView;
    private ImageView mIconCheck;
    private ImageView mIcon;
    private View mContainer;

    private OnCheckedChangeListener mOnCheckedChangeListener;

    public VestiSwitchButton(Context context) {
        super(context);
        init(null, 0, context);
    }

    public VestiSwitchButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0, context);
    }

    public VestiSwitchButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle, context);
    }

    private void init(AttributeSet attrs, int defStyle, Context context) {
        View view = inflate(getContext(), R.layout.vesti_switch_button, null);

        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.VestiSwitchButton, defStyle, 0);

        mChecked = a.getBoolean(R.styleable.VestiSwitchButton_ativo, false);
        mTexto = a.getString(R.styleable.VestiSwitchButton_texto);

        mContainer = view.findViewById(R.id.container);
        mContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                anima();
                boolean check = isAtivo();
                setAtivo(!check);
                setChecked(!check);
            }
        });

        mTextView = (TextView) view.findViewById(R.id.text);
        mTextView.setText(mTexto);

        mIcon = (ImageView) view.findViewById(R.id.icon);
        mIconCheck = (ImageView) view.findViewById(R.id.icon_check);

        setChecked(mChecked);

        addView(view);

        a.recycle();
    }

    /**
     * Gets the example mDrawable attribute value.
     *
     * @return The example mDrawable attribute value.
     */
    public boolean isAtivo() {
        return mChecked;
    }

    /**
     * Sets the view's example color attribute value. In the example view, this color
     * is the font color.
     */
    public void setAtivo(boolean ativo) {
        mChecked = ativo;
        defineIcone();
        pintaComponente();
    }

    private void anima() {
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.scale);
        mContainer.startAnimation(anim);
    }

    private void pintaComponente() {
        if (mChecked) {
            mContainer.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_tag_inverse));
            mTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.branco));
        } else {
            mContainer.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_tag));
            mTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.texto_nao_ativo));
        }
    }

    private void defineIcone() {
        if (mChecked) {
            mIconCheck.setVisibility(VISIBLE);
            mIcon.setVisibility(GONE);
        } else {
            mIconCheck.setVisibility(GONE);
            mIcon.setVisibility(VISIBLE);
        }
    }

    public void setTexto(String texto) {
        mTexto = texto;
        mTextView.setText(texto);
    }

    public String getTexto() {
        return mTexto;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        mOnCheckedChangeListener = onCheckedChangeListener;
    }

    @Override
    public void setChecked(boolean checked) {
        setAtivo(checked);
        mChecked = checked;
        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onChecked(checked);
        }
    }

    public TextView getTextView() {
        return mTextView;
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!isChecked());
    }

    public void noIcon() {
        mIcon.setVisibility(GONE);
        mIconCheck.setVisibility(GONE);
    }
}
