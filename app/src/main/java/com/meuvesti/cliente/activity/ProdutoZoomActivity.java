package com.meuvesti.cliente.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.meuvesti.cliente.R;
import com.meuvesti.cliente.TouchImageView;
import com.meuvesti.cliente.utils.ExtendedViewPager;
import com.meuvesti.cliente.utils.PicassoBigCache;
import com.meuvesti.cliente.utils.VestiActionBar;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProdutoZoomActivity extends AppCompatActivity {

    private VestiActionBar mVestiActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            this.overridePendingTransition(R.anim.slide_in_left,
                    R.anim.slide_out_left);
        }

        mVestiActionBar = new VestiActionBar(this);
        mVestiActionBar.setContentView(R.layout.activity_produto_zoom);
        mVestiActionBar.setTitle("");
        setContentView(mVestiActionBar.getMainContent());
        mVestiActionBar.setOnCloseClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ExtendedViewPager mViewPager = (ExtendedViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(new TouchImageAdapter((List<String>) getIntent().getExtras().getSerializable("fotos")));
        mViewPager.setCurrentItem(getIntent().getExtras().getInt("index"));
    }

    public class TouchImageAdapter extends PagerAdapter {

        private List<String> images;
        public TouchImageAdapter(List<String> images) {
            this.images = images;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            final String imagem = images.get(position);
            final TouchImageView img = new TouchImageView(container.getContext());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                img.setTransitionName("itemProductPhoto");
            }

            PicassoBigCache.INSTANCE.getPicassoBigCache(ProdutoZoomActivity.this)
                    .load(imagem)
                    .into(img, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                            Picasso.with(ProdutoZoomActivity.this)
                                    .load(R.mipmap.ic_launcher)
                                    .into(img);

                        }
                    });

            container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            return img;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }
}


