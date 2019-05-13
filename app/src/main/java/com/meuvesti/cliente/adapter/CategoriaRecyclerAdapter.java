package com.meuvesti.cliente.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.meuvesti.cliente.R;
import com.meuvesti.cliente.activity.CategoriaActivity;
import com.meuvesti.cliente.iclass.OnCheckedChangeListener;
import com.meuvesti.cliente.model.Category;
import com.meuvesti.cliente.widget.VestiSwitchButton;

import java.util.List;


/**
 * Created by Herson Rodrigues <hersonrodrigues@gmail.com> on 31/01/2018.
 */

public class CategoriaRecyclerAdapter extends RecyclerView.Adapter<CategoriaRecyclerAdapter.CategoriaItemView> {
    private final List<Category> mList;
    private final CategoriaActivity mActivity;
    private final CategoriaActivity.AcoesCategoria mAcoes;

    public CategoriaRecyclerAdapter(CategoriaActivity context, List<Category> list, CategoriaActivity.AcoesCategoria acoesCategoria) {
        mActivity = context;
        mList = list;
        mAcoes = acoesCategoria;
    }

    @Override
    public CategoriaItemView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categoria_adapter, parent, false);
        return new CategoriaItemView(view);
    }

    @Override
    public void onBindViewHolder(final CategoriaItemView holder, int position) {
        final Category item = mList.get(position);
        holder.mSwitch.setTexto(item.getDisplayName());
        if(mAcoes.getIds().contains(item.getId())) {
            holder.mSwitch.setAtivo(true);
        }else{
            holder.mSwitch.setAtivo(false);
        }
        holder.mSwitch.getTextView().setAllCaps(false);
        holder.mSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onChecked(boolean checked) {
                item.setChecked(checked);
                if(!mAcoes.getIds().contains(item.getId())){
                    mAcoes.adicionar(item.getId());
                    holder.mSwitch.setAtivo(true);
                }else{
                    mAcoes.remover(item.getId());
                    holder.mSwitch.setAtivo(false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public class CategoriaItemView extends RecyclerView.ViewHolder {
        public final VestiSwitchButton mSwitch;
        public final View mContainer;

        public CategoriaItemView(View view) {
            super(view);
            mContainer = view;
            mSwitch = (VestiSwitchButton) view.findViewById(R.id.button);
        }
    }
}

