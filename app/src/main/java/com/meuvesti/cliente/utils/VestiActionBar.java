package com.meuvesti.cliente.utils;

import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.meuvesti.cliente.R;

/**
 * Created by hersonrodrigues on 21/01/17.
 */

public class VestiActionBar {

    private static final String TAG = VestiActionBar.class.getSimpleName();
    private final AppCompatActivity mContext;
    private final View mActionBar;
    private final View mBox;
    private final DrawerLayout mDrawer;
    private final View mBtnClose;
    private TextView mTitleView;
    private View mBtnHome;
    private View mBtnBack;
    private View mBtnCart;
    private View mContentView;
    private View mMainContent;
    private ImageView mImageTitleView;

    public VestiActionBar(final AppCompatActivity context) {
        mContext = context;
        mMainContent = mContext.getLayoutInflater().inflate(com.meuvesti.cliente.R.layout.vesti_main_content, null, false);

        mActionBar = mMainContent.findViewById(com.meuvesti.cliente.R.id.action_bar);
        mActionBar.setVisibility(View.VISIBLE);
        mBox = mActionBar.findViewById(com.meuvesti.cliente.R.id.box_actionbar);
        mBox.setBackgroundColor(mContext.getResources().getColor(com.meuvesti.cliente.R.color.actionBarColor));

        mBtnHome = mActionBar.findViewById(com.meuvesti.cliente.R.id.btnHome);
        mBtnBack = mActionBar.findViewById(com.meuvesti.cliente.R.id.btnVoltar);
        mBtnClose = mActionBar.findViewById(com.meuvesti.cliente.R.id.btnClose);
        mBtnCart = mActionBar.findViewById(com.meuvesti.cliente.R.id.btnCarrinho);
        mImageTitleView = (ImageView) mActionBar.findViewById(com.meuvesti.cliente.R.id.imgTitulo);

        mDrawer = (DrawerLayout) mMainContent.findViewById(com.meuvesti.cliente.R.id.drawer_layout);
        mDrawer.closeDrawer(GravityCompat.START);
    }

    public void lockMenu() {
        mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public void unlockMenu() {
        mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    public void openMenu() {
        mDrawer.openDrawer(Gravity.LEFT);
    }

    public void closeMenu(boolean animate) {
        mDrawer.closeDrawer(Gravity.LEFT, animate);
    }

    public void setBulletCartText(int number) {
        TextView bullet = (TextView) mMainContent.findViewById(com.meuvesti.cliente.R.id.cart_bullet);
        if (number > 0) {
            bullet.startAnimation(AnimationUtils.loadAnimation(mContext, com.meuvesti.cliente.R.anim.scale));
            bullet.setVisibility(View.VISIBLE);
            bullet.setText(String.valueOf(number));
        } else {
            bullet.setVisibility(View.GONE);
        }
    }

    public void setBulletFilterText(int number) {
        TextView bullet = (TextView) mMainContent.findViewById(com.meuvesti.cliente.R.id.filter_bullet);
        if (number > 0) {
            bullet.startAnimation(AnimationUtils.loadAnimation(mContext, com.meuvesti.cliente.R.anim.scale));
            bullet.setVisibility(View.VISIBLE);
            bullet.setText(String.valueOf(number));
        } else {
            bullet.setVisibility(View.GONE);
        }
    }

    public boolean menuIsOpen() {
        return mDrawer.isDrawerOpen(GravityCompat.START);
    }

    public void setTitle(final String title) {
        mTitleView = (TextView) mActionBar.findViewById(com.meuvesti.cliente.R.id.txtTitulo);
        mTitleView.post(new Runnable() {
            @Override
            public void run() {
                mTitleView.setVisibility(View.VISIBLE);
                mTitleView.setText(title);
                hideImageTitle();
            }
        });
    }

    public void hideTitle() {
        mTitleView = (TextView) mActionBar.findViewById(com.meuvesti.cliente.R.id.txtTitulo);
        // Necessário para manter a quantidade de caracteres alta
        mTitleView.setText("SEM NENHUM TITULO DEFINIDO");
        // As cores esconderao o texto
        mTitleView.setTextColor(ContextCompat.getColor(mContext, com.meuvesti.cliente.R.color.actionBarColor));
    }

    public void setImageTitle() {
        mTitleView = (TextView) mActionBar.findViewById(com.meuvesti.cliente.R.id.txtTitulo);
        mTitleView.post(new Runnable() {
            @Override
            public void run() {
                mImageTitleView.setVisibility(View.VISIBLE);
                mTitleView.setVisibility(View.GONE);
            }
        });
    }

    public void hideImageTitle() {
        mImageTitleView.setVisibility(View.GONE);
    }

    public void setOnBackClickListener(View.OnClickListener onClickListener) {
        if (onClickListener != null) {
            mBtnBack.setVisibility(View.VISIBLE);
            mBtnHome.setVisibility(View.GONE);
            mBtnBack.setOnClickListener(onClickListener);
        }
    }

    public void setOnHomeClickListener(View.OnClickListener onClickListener) {
        if (onClickListener != null) {
            mBtnHome.setVisibility(View.VISIBLE);
            mBtnBack.setVisibility(View.GONE);
            mBtnHome.setOnClickListener(onClickListener);
        }
    }

    public void setOnCloseClickListener(View.OnClickListener onClickListener) {
        if (onClickListener != null) {
            mBtnHome.setVisibility(View.GONE);
            mBtnBack.setVisibility(View.GONE);
            mBtnClose.setVisibility(View.VISIBLE);
            mBtnClose.setOnClickListener(onClickListener);
        }
    }

    public void setOnFilterClickListener(View.OnClickListener onClickListener) {
        View mBtnFiltrar = mActionBar.findViewById(com.meuvesti.cliente.R.id.btnMenuFiltrar);
        if (onClickListener != null) {
            mBtnFiltrar.setVisibility(View.VISIBLE);
            mBtnFiltrar.setOnClickListener(onClickListener);
        } else {
            mBtnFiltrar.setVisibility(View.GONE);
        }
    }

    public void setOnShareClickListener(View.OnClickListener onClickListener) {
        View btn = mActionBar.findViewById(com.meuvesti.cliente.R.id.btnMenuShare);
        if (onClickListener != null) {
            btn.setVisibility(View.VISIBLE);
            btn.setOnClickListener(onClickListener);
        } else {
            btn.setVisibility(View.GONE);
        }
    }

    public void setOnCartClickListener(View.OnClickListener onClickListener) {
        View mBtnCart = mActionBar.findViewById(com.meuvesti.cliente.R.id.btnCarrinho);
        if (onClickListener != null) {
            mBtnCart.setVisibility(View.VISIBLE);
            mBtnCart.setOnClickListener(onClickListener);
        } else {
            mBtnCart.setVisibility(View.GONE);
        }
    }

    public void toggleIconCart(boolean flag) {
        TextView bullet = (TextView) mMainContent.findViewById(com.meuvesti.cliente.R.id.cart_bullet);
        View mBtnCart = mActionBar.findViewById(com.meuvesti.cliente.R.id.btnCarrinho);
        if (flag) {
            mBtnCart.setVisibility(View.VISIBLE);
        } else {
            mBtnCart.setVisibility(View.GONE);
            bullet.setVisibility(View.GONE);
        }
    }

    public void toggleIconFilter(boolean flag) {
        TextView bullet = (TextView) mMainContent.findViewById(com.meuvesti.cliente.R.id.filter_bullet);
        View mBtnFiltrar = mActionBar.findViewById(com.meuvesti.cliente.R.id.btnMenuFiltrar);
        if (flag) {
            mBtnFiltrar.setVisibility(View.VISIBLE);
        } else {
            mBtnFiltrar.setVisibility(View.GONE);
            bullet.setVisibility(View.GONE);
        }
    }

    public View setContentView(int resource) {
        mContentView = mContext.getLayoutInflater().inflate(resource, null, false);
        FrameLayout frame = (FrameLayout) mMainContent.findViewById(R.id.content_main);
        frame.addView(mContentView);
        return mContentView;
    }

    public void setScrollViewToHideBar(View view, final GridLayoutManager gridLayout) {
        if (view instanceof ScrollView) {
            final ScrollView scrollView = (ScrollView) view;
            scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                public int scrollY;

                @Override
                public void onScrollChanged() {
                    int scrollYLocal = scrollView.getScrollY(); // For ScrollView
                    //int scrollX = scrollView.getScrollX();
                    Log.i(TAG, "scrollY " + String.valueOf(scrollY));
                    if (scrollYLocal > scrollY && scrollYLocal >= mActionBar.getHeight() + 10) {
                        hide();
                    } else {
                        show();
                    }

                    scrollY = scrollYLocal;
                }
            });
        }
        if (view instanceof RecyclerView) {
            RecyclerView recyclerview = (RecyclerView) view;
            recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) {
                        hide();
                    } else {
                        show();
                    }
                }

                private void show() {
                    mActionBar.setVisibility(View.VISIBLE);
                }

                private void hide() {
                    mActionBar.setVisibility(View.GONE);
                }
            });
        }
    }

    public void showNetworkError(boolean flag) {
        if (flag) {
            mMainContent.findViewById(com.meuvesti.cliente.R.id.network).setVisibility(View.VISIBLE);
        } else {
            mMainContent.findViewById(com.meuvesti.cliente.R.id.network).setVisibility(View.GONE);
        }
    }

    private void manageScroll(final View scrollView) {
        scrollView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onAnimStart = false, onAnimEnd = false,
                    animStart = false, animEnd = false;

            public int a = 0, b = 0;

            private ViewTreeObserver observer;

            private void detectScroll() {
                a = scrollView.getScrollY();

                Log.e(TAG, "a antes: " + a + " b antes: " + b);

                if (a > b) {
                    hide();
                } else {
                    show();
                }
                b = a;
            }

            private void show() {
                if (animEnd == false) {
                    animEnd = true;
                    animStart = false;
                    onAnimEnd = true;
                    Animation animation = AnimationUtils.loadAnimation(mContext, com.meuvesti.cliente.R.anim.slidedown_bar);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            onAnimEnd = true;
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            onAnimEnd = false;
                            mActionBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    mActionBar.startAnimation(animation);
                }
            }

            private void hide() {
                if (animStart == false) {
                    animStart = true;
                    animEnd = false;
                    onAnimStart = true;
                    Animation animation = AnimationUtils.loadAnimation(mContext, com.meuvesti.cliente.R.anim.slideup_bar);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            onAnimStart = true;
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            onAnimStart = false;
                            mActionBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    mActionBar.startAnimation(animation);
                }
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (observer == null) {
                    observer = scrollView.getViewTreeObserver();
                    observer.addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                        @Override
                        public void onScrollChanged() {
                            detectScroll();
                        }
                    });
                } else if (!observer.isAlive()) {
                    observer.removeOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                        @Override
                        public void onScrollChanged() {
                            detectScroll();
                        }
                    });
                    observer = scrollView.getViewTreeObserver();
                    observer.addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                        @Override
                        public void onScrollChanged() {
                            detectScroll();
                        }
                    });
                }
                return false;
            }
        });
    }

    public View getMainContent() {
        return mMainContent;
    }

    public void setScrollViewToHideBar(ScrollView scroll) {
        manageScroll(scroll);
    }

    public void hide() {
        mActionBar.setVisibility(View.GONE);
    }

    public void show() {
        mActionBar.setVisibility(View.VISIBLE);
    }

    public View getActionBar() {
        return mActionBar;
    }

    public void forceCloseAllIcons() {
        mActionBar.findViewById(R.id.menuIcones).setVisibility(View.GONE);
    }
}