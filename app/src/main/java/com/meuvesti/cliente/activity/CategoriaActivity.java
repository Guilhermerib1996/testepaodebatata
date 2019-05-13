package com.meuvesti.cliente.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.meuvesti.cliente.R;
import com.meuvesti.cliente.adapter.CategoriaRecyclerAdapter;
import com.meuvesti.cliente.model.Category;
import com.meuvesti.cliente.model.CategoryResponse;
import com.meuvesti.cliente.service.VestiAPI;
import com.meuvesti.cliente.utils.CategoriaDB;
import com.meuvesti.cliente.utils.Globals;
import com.meuvesti.cliente.utils.Utils;
import com.meuvesti.cliente.utils.VestiActionBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Herson Rodrigues <hersonrodrigues@gmail.com> on 31/01/2018.
 */

public class CategoriaActivity extends AppCompatActivity {

    private CategoriaActivity mContext;
    private VestiActionBar mVestiActionBar;
    private List<Category> mList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private CategoriaRecyclerAdapter mAdapter;
    private FlexboxLayoutManager mLayoutManger;
    private View boxLoading, boxContent;
    private List<String> mIdsSelecionados = new ArrayList<>();
    private List<String> mIdsFilhos = new ArrayList<>();
    private List<String> mIdsNetos = new ArrayList<>();
    private View mBoxFilhos;
    private RecyclerView mRecyclerViewFilhos;
    private CategoriaRecyclerAdapter mAdapterFilhos;
    private List<Category> mListFilhos = new ArrayList<>();
    private FlexboxLayoutManager mLayoutMangerFilhos;

    private View mBoxNetos;
    private RecyclerView mRecyclerViewNetos;
    private CategoriaRecyclerAdapter mAdapterNetos;
    private List<Category> mListNetos = new ArrayList<>();
    private FlexboxLayoutManager mLayoutMangerNetos;
    private Call<JsonObject> mThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catergoria);

        mContext = CategoriaActivity.this;
        mVestiActionBar = new VestiActionBar(this);
        mVestiActionBar.setContentView(R.layout.activity_catergoria);
        mVestiActionBar.setTitle(getString(R.string.menu_filtros));
        setContentView(mVestiActionBar.getMainContent());
        mVestiActionBar.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //mVestiActionBar.forceCloseAllIcons();

        Button btnFiltrar = (Button) findViewById(R.id.btnFiltrar);
        Button btnLimpar = (Button) findViewById(R.id.btnLimpar);
        boxLoading = findViewById(R.id.box_loading);
        boxContent = findViewById(R.id.content);

        btnLimpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.setHasFilter(CategoriaActivity.this, true);
                List<Category> lista = CategoriaDB.listarTodas(mContext);
                for (Category category : lista) {
                    category.setChecked(false);
                }
                CategoriaDB.salvar(mContext, lista);
                CategoriaDB.salvarIds(mContext, new ArrayList<String>());
                CategoriaDB.salvarIdsFilhos(mContext, new ArrayList<String>());
                CategoriaDB.salvarIdsNetos(mContext, new ArrayList<String>());
                CategoriaDB.salvarTotalBadge(mContext, 0);

                setResult(Activity.RESULT_OK);
                finish();
            }
        });

        btnFiltrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIdsSelecionados.size() > 0) {
                    Utils.setHasFilter(CategoriaActivity.this, true);
                    CategoriaDB.salvarIds(mContext, mIdsSelecionados);
                    CategoriaDB.salvarIdsFilhos(mContext, mIdsFilhos);
                    CategoriaDB.salvarIdsNetos(mContext, mIdsNetos);
                    CategoriaDB.salvarTotalBadge(mContext, getTotalBage());

                    setResult(Activity.RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(getBaseContext(), getString(R.string.sem_filtro_selecionado), Toast.LENGTH_LONG).show();
                }
            }
        });
        showLoading(true);
        init();
        servicoListarCategorias(null, new OnCarregouChildren() {
            @Override
            public void onOk(List<Category> filhos) {
                mList.clear();
                CategoriaDB.salvar(mContext, filhos);
                mList = filhos;
                init();
            }
        });
    }

    private void showLoading(boolean flag) {
        boxLoading.setVisibility(flag ? View.VISIBLE : View.GONE);
        boxContent.setVisibility(flag ? View.GONE : View.VISIBLE);
    }

    private int getTotalBage() {
        int totalGeral = mIdsSelecionados.size();
        int totalFilhos = mIdsFilhos.size();
        int totalNetos = mIdsNetos.size();
        int totalBage = totalGeral;
        if (totalNetos > 0) {
            totalBage = totalNetos;
        } else if (totalFilhos > 0) {
            totalBage = totalFilhos;
        }
        return totalBage;
    }

    private void init() {
        mList = CategoriaDB.listarTodas(mContext);

        mIdsSelecionados = CategoriaDB.listarIdsSelecionadas(mContext);
        mIdsFilhos = CategoriaDB.listarIdsFilhos(mContext);
        mIdsNetos = CategoriaDB.listarIdsNetos(mContext);

        oderAlphabetic(mList);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        mLayoutManger = new FlexboxLayoutManager(mContext);
        mLayoutManger.setAlignItems(AlignItems.STRETCH);
        mLayoutManger.setFlexWrap(FlexWrap.WRAP);
        mLayoutManger.setJustifyContent(JustifyContent.FLEX_START);
        mAdapter = new CategoriaRecyclerAdapter(mContext, mList, new AcoesCategoria() {

            @Override
            public void adicionar(String id) {
                mIdsSelecionados.add(id);
                processaFilhos();
            }

            @Override
            public void remover(String id) {
                mIdsSelecionados.remove(id);
                processaFilhos();
            }

            @Override
            public List<String> getIds() {
                return mIdsSelecionados;
            }
        });
        mRecyclerView.setLayoutManager(mLayoutManger);
        mRecyclerView.setAdapter(mAdapter);
        configuraFilhos();
        configuraNetos();
        // Abre automaticamento os filhos ja selecionados
        if (mIdsSelecionados != null && mIdsSelecionados.size() == 1) {
            processaFilhos();
        }
        // Abre automaticamento os netos ja selecionados
        if (mIdsFilhos != null && mIdsFilhos.size() == 1) {
            processaNetos();
        }
    }

    private void configuraFilhos() {
        mBoxFilhos = findViewById(R.id.box_filhos);
        mRecyclerViewFilhos = (RecyclerView) findViewById(R.id.filhos);
        mLayoutMangerFilhos = new FlexboxLayoutManager(mContext);
        mLayoutMangerFilhos.setAlignItems(AlignItems.STRETCH);
        mLayoutMangerFilhos.setFlexWrap(FlexWrap.WRAP);
        mLayoutMangerFilhos.setJustifyContent(JustifyContent.FLEX_START);
        mAdapterFilhos = new CategoriaRecyclerAdapter(mContext, mListFilhos, new AcoesCategoria() {
            @Override
            public void adicionar(String id) {
                mIdsFilhos.add(id);
                processaNetos();
            }

            @Override
            public void remover(String id) {
                mIdsFilhos.remove(id);
                processaNetos();
            }

            @Override
            public List<String> getIds() {
                return mIdsFilhos;
            }
        });
        mRecyclerViewFilhos.setLayoutManager(mLayoutMangerFilhos);
        mRecyclerViewFilhos.setAdapter(mAdapterFilhos);
    }

    private void configuraNetos() {
        mBoxNetos = findViewById(R.id.box_netos);
        mRecyclerViewNetos = (RecyclerView) findViewById(R.id.netos);
        mLayoutMangerNetos = new FlexboxLayoutManager(mContext);
        mLayoutMangerNetos.setAlignItems(AlignItems.STRETCH);
        mLayoutMangerNetos.setFlexWrap(FlexWrap.WRAP);
        mLayoutMangerNetos.setJustifyContent(JustifyContent.FLEX_START);
        mAdapterNetos = new CategoriaRecyclerAdapter(mContext, mListNetos, new AcoesCategoria() {
            @Override
            public void adicionar(String id) {
                mIdsNetos.add(id);
            }

            @Override
            public void remover(String id) {
                mIdsNetos.remove(id);
                if (mThread != null) {
                    mThread.cancel();
                }
            }

            @Override
            public List<String> getIds() {
                return mIdsNetos;
            }
        });
        mRecyclerViewNetos.setLayoutManager(mLayoutMangerNetos);
        mRecyclerViewNetos.setAdapter(mAdapterNetos);
    }

    private void servicoListarCategorias(final String idCategoria, final OnCarregouChildren onCarregouFilhos) {
        mThread = VestiAPI.get().api(mContext).categories(idCategoria, Globals.SCHEME_NAME);
        mThread.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                showLoading(false);
                if (response.body().has("response")) {
                    Gson gson = new Gson();
                    CategoryResponse categoryResponse = gson.fromJson(response.body(), CategoryResponse.class);
                    if (onCarregouFilhos != null && categoryResponse.getResponse().size() > 0) {
                        onCarregouFilhos.onOk(categoryResponse.getResponse());
                    }
                } else {
                    Toast.makeText(mContext, getString(R.string.erro_servico_categorias, "A Api não devolveu o JSON corretamente."), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                showLoading(false);
                Toast.makeText(mContext, getString(R.string.erro_servico_categorias, t.getMessage()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void oderAlphabetic(List<Category> list) {
        Collections.sort(list, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                Category c1 = (Category) o1;
                Category c2 = (Category) o2;
                return c1.getDisplayName().compareToIgnoreCase(c2.getDisplayName());
            }
        });
    }

    private void processaFilhos() {
        if (mIdsSelecionados.size() == 1) {
            final String idItemSobrou = mIdsSelecionados.get(0);
            mListFilhos = getChildrenById(idItemSobrou);

            if (mListFilhos != null && !mListFilhos.isEmpty()) {
                mAdapterFilhos.notifyDataSetChanged();
                mBoxFilhos.setVisibility(View.VISIBLE);
            }

            servicoListarCategorias(idItemSobrou, new OnCarregouChildren() {

                @Override
                public void onOk(List<Category> filhos) {
                    adicionaFilhosNoPai(idItemSobrou, filhos);
                    mListFilhos = filhos;
                    configuraFilhos();
                    if (mIdsSelecionados.size() == 1) {
                        mBoxFilhos.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            removeFilhosUI();
            removeNetosUI();
        }
    }

    private void processaNetos() {
        if (mIdsFilhos.size() == 1) {
            final String idItemSobrou = mIdsFilhos.get(0);
            mListNetos = getChildrenById(idItemSobrou);

            if (mListNetos != null && !mListNetos.isEmpty()) {
                mAdapterNetos.notifyDataSetChanged();
                mBoxNetos.setVisibility(View.VISIBLE);
            }

            servicoListarCategorias(idItemSobrou, new OnCarregouChildren() {

                @Override
                public void onOk(List<Category> netos) {
                    adicionaFilhosNoPai(idItemSobrou, netos);
                    mListNetos = netos;
                    configuraNetos();
                    if (mIdsFilhos.size() == 1) {
                        mBoxNetos.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            removeNetosUI();
        }
    }

    private void adicionaFilhosNoPai(String id, List<Category> filhos) {
        for (Category category : mList) {
            if (category.getId().equals(id)) {
                category.setChildren(filhos);
                CategoriaDB.salvar(mContext, mList);
            }
        }
    }

    private List<Category> getChildrenById(String id) {
        for (Category category : mList) {
            if (category.getId().equals(id)) {
                if (category.getChildren() != null && !category.getChildren().isEmpty()) {
                    return category.getChildren();
                }
            }
        }
        return null;
    }

    private void removeNetosUI() {
        mBoxNetos.setVisibility(View.GONE);
        mIdsNetos = new ArrayList<>();
    }

    private void removeFilhosUI() {
        mBoxFilhos.setVisibility(View.GONE);
        mIdsFilhos = new ArrayList<>();
    }

    interface OnCarregouChildren {
        void onOk(List<Category> filhos);
    }

    public interface AcoesCategoria {
        void adicionar(String id);

        void remover(String id);

        List<String> getIds();
    }
}
