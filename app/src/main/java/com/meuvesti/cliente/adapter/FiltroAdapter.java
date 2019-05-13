package com.meuvesti.cliente.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import com.meuvesti.cliente.realm.FilterRealm;
import com.meuvesti.cliente.utils.OnClickItem;
import io.realm.RealmResults;

/**
 * Created by FabianoVasconcelos on 28/12/16.
 */

public class FiltroAdapter extends BaseAdapter {

    private final OnClickItem onClickItem;
    private final List<String> mFiltersSelectedInScreen;
    public RealmResults<FilterRealm> filterRealms;
    private Context context;

    public FiltroAdapter(Context context, List<String> filtersSelectedInScreen, RealmResults<FilterRealm> filterRealms, OnClickItem click) {
        this.context = context;
        this.mFiltersSelectedInScreen = filtersSelectedInScreen;
        this.filterRealms = filterRealms;
        this.onClickItem = click;
    }

    @Override
    public int getCount() {
        return this.filterRealms.size();
    }

    @Override
    public Object getItem(int position) {
        return this.filterRealms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 1000;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            final FilterRealm filterRealm = (FilterRealm) getItem(position);

            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(com.meuvesti.cliente.R.layout.filtro_item, parent, false);

            final TextView txFiltro = (TextView) convertView.findViewById(com.meuvesti.cliente.R.id.txFiltro);
            final LinearLayout linFiltro = (LinearLayout) convertView.findViewById(com.meuvesti.cliente.R.id.linFiltro);

            final ImageView icon = (ImageView) convertView.findViewById(com.meuvesti.cliente.R.id.icon);
            icon.setVisibility(View.VISIBLE);

            final ImageView iconCheck = (ImageView) convertView.findViewById(com.meuvesti.cliente.R.id.icon_check);
            iconCheck.setVisibility(View.GONE);

            txFiltro.setText(filterRealm.getNome());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickItem.onClick(view, position);
                }
            });

            if (mFiltersSelectedInScreen.contains(filterRealm.getId())) {
                setChecked(txFiltro, linFiltro, icon, iconCheck);
            }
        }

        return convertView;
    }

    public void setChecked(TextView txFiltro, LinearLayout linFiltro, ImageView icon, ImageView iconCheck) {
        icon.setVisibility(View.GONE);
        iconCheck.setVisibility(View.VISIBLE);
        txFiltro.setTextColor(context.getResources().getColor(com.meuvesti.cliente.R.color.branco));
        linFiltro.setTag("selecionado");
        linFiltro.setBackground(ContextCompat.getDrawable(context, com.meuvesti.cliente.R.drawable.shape_tag_inverse));
    }



}
