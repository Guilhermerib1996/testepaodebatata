package com.meuvesti.cliente.realm;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;

/**
 * Created by hersonrodrigues on 01/02/17.
 */
public class RealmService {

    public static boolean hasWhatsAppItems() {
        Realm realm = Realm.getDefaultInstance();
        ConfigDataRealm data = realm.where(ConfigDataRealm.class).equalTo("id", 1).findFirst();
        return (data != null && data.getWhatsAppIds() != null && !data.getWhatsAppIds().isEmpty());
    }

    public static String getRealmWhatsAppItems() {
        Realm realm = Realm.getDefaultInstance();
        ConfigDataRealm data = realm.where(ConfigDataRealm.class).equalTo("id", 1).findFirst();
        if (data != null) {
            return data.getWhatsAppIds();
        }
        return null;
    }

    public static void clearConfig() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(ConfigDataRealm.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();
        realm.close();
    }

    public static void logar(UsuarioRealm usuario) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        RealmResults<UsuarioRealm> usuarios = realm.where(UsuarioRealm.class).equalTo("logado", true).findAll();
        for (UsuarioRealm u : usuarios) {
            u.setLogado(false);
        }

        usuario.setLogado(true);
        realm.copyToRealmOrUpdate(usuario);

        realm.commitTransaction();
        realm.close();
    }

    public static UsuarioRealm getUsuarioLogado(Context context) {

        Realm realm;

        // initialize Realm
        Realm.init(context);

        // create your Realm configuration
        RealmConfiguration config = new RealmConfiguration.
                Builder().
                deleteRealmIfMigrationNeeded().
                build();
        Realm.setDefaultConfiguration(config);

        realm = Realm.getInstance(config);

        final UsuarioRealm usuarioLogado = realm.where(UsuarioRealm.class).equalTo("logado", true).findFirst();
        return usuarioLogado;
    }

    public static void logout() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        RealmResults<UsuarioRealm> usuarios = realm.where(UsuarioRealm.class).equalTo("logado", true).findAll();
        for (UsuarioRealm u : usuarios) {
            u.setLogado(false);
        }

        realm.commitTransaction();
        realm.close();
    }

    public static void emailToOneSignal(UsuarioRealm user){
        
    }
}
