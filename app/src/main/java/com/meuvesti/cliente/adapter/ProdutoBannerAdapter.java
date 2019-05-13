package com.meuvesti.cliente.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

import com.meuvesti.cliente.R;
import com.meuvesti.cliente.activity.ProdutoZoomActivity;

/**
 * Created by FabianoVasconcelos on 10/01/17.
 */

public class ProdutoBannerAdapter extends PagerAdapter {

    private static final int HEIGHT = 800;
    private final List<String> fotosHd;
    Context mContext;
    LayoutInflater mLayoutInflater;
    List<String> fotos;

    public ProdutoBannerAdapter(Context context, List<String> fotos, List<String> fotosHds) {
        mContext = context;
        this.fotos = fotos;
        this.fotosHd = fotosHds;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return fotos.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);
        String foto;
        try {
            foto = fotos.get(position);
        } catch (IndexOutOfBoundsException e) {
            foto = fotos.get(0);
        }

        final ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.setTransitionName("itemProductPhoto");
        }

        final String finalFoto = foto;

        Picasso.with(mContext)
                .load(finalFoto)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .resize(0, HEIGHT)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                        Picasso.with(mContext)
                                .load(finalFoto)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .resize(0, HEIGHT)
                                .into(imageView, new Callback() {

                                    @Override
                                    public void onSuccess() {
                                    }

                                    @Override
                                    public void onError() {

                                        Picasso.with(mContext)
                                                .load(finalFoto)
                                                .resize(0, HEIGHT)
                                                .into(imageView, new Callback() {

                                                    @Override
                                                    public void onSuccess() {

                                                    }

                                                    @Override
                                                    public void onError() {

                                                        Picasso.with(mContext)
                                                                .load(R.mipmap.ic_launcher)
                                                                .resize(0, HEIGHT)
                                                                .into(imageView);

                                                    }
                                                });

                                    }
                                });

                    }
                });



        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, ProdutoZoomActivity.class);
                i.putExtra("fotos", (Serializable) fotosHd);
                i.putExtra("index", position);
                mContext.startActivity(i);
            }
        });

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
