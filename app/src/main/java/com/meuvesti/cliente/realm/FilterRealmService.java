package com.meuvesti.cliente.realm;

import java.util.List;

import com.meuvesti.cliente.model.BuyItemData;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by hersonrodrigues on 02/02/17.
 */

public class FilterRealmService {

    public static void uncheckFilters() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<FilterRealm> filters = realm.where(FilterRealm.class).equalTo("selecionado", true).findAll();
                for (FilterRealm filter : filters) {
                    filter.setSelecionado(false);
                    realm.copyToRealmOrUpdate(filter);
                }
            }
        });
        realm.close();
    }

    public static double getTeste() {
        Realm realm = Realm.getDefaultInstance();
        ConfigDataRealm data = realm.where(ConfigDataRealm.class).equalTo("id", 1).findFirst();
        if (data == null) {
            return 0;
        } else {
            return data.getTest();
        }
    }

    public static RealmResults<FilterRealm> getMySelectedFilter() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<FilterRealm> filters = realm.where(FilterRealm.class).equalTo("selecionado", true).findAll();
        return filters;
    }

    public static String getMySelectedFilterIds() {
        RealmResults<FilterRealm> filters = getMySelectedFilter();

        String ids = "";
        for (FilterRealm fr : filters) {
            ids += fr.getId() + "_";
        }

        if (ids != null && ids.length() > 1) {
            ids = ids.substring(0, ids.length() - 1);
        }

        return ids;
    }

    public static RealmResults<FilterRealm> getMyFilters() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<FilterRealm> filters = realm.where(FilterRealm.class).findAll();
        return filters;
    }

    public static void clearFilter() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(FilterRealm.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();
        realm.close();
    }

    public static void addFilter(final List<BuyItemData> dados) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                realm.delete(FilterRealm.class);

                for (BuyItemData buy : dados) {
                    FilterRealm f = new FilterRealm();
                    f.setId(buy.getId());
                    f.setNome(buy.getName());
                    f.setSelecionado(false);
                    realm.copyToRealm(f);
                }

            }
        });


        realm.close();
    }

    public static void checkFilters(final List<String> filters) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (FilterRealm fr : FilterRealmService.getMyFilters()) {
                    fr.setSelecionado(false);
                    for (String id : filters) {
                        if(fr.getId().equals(id)) {
                            fr.setSelecionado(true);
                        }
                    }
                }
            }
        });
        realm.close();
    }
}
