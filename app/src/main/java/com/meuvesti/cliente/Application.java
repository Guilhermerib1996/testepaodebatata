package com.meuvesti.cliente;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.meuvesti.cliente.realm.RealmService;
import com.meuvesti.cliente.utils.Globals;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import okhttp3.OkHttpClient;
import com.onesignal.OneSignal;

/**
 * Created by FabianoVasconcelos on 16/12/16.
 */
public final class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/FUTURASTD-MEDIUM.OTF");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/FUTURASTD-MEDIUM.OTF");
        FontsOverride.setDefaultFont(this, "SERIF", "fonts/FUTURASTD-MEDIUM.OTF");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "fonts/FUTURASTD-HEAVY.OTF");

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        Realm.init(getApplicationContext());
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder()
                .name(Globals.DATABASE_NAME)
                .deleteRealmIfMigrationNeeded()
                .schemaVersion(Globals.DATABASE_VERSION)
                .build());

        OkHttpClient client = new OkHttpClient();
        Picasso picasso = new Picasso.Builder(this)
                .downloader(new OkHttp3Downloader(client))
                .build();

        if (BuildConfig.DEBUG && Globals.isHomolog()) {
            //built.setIndicatorsEnabled(true);
            //built.setLoggingEnabled(true);
        }
    }
}