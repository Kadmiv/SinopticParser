package com.gaijin.sinopticparser;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Kachulyak Ivan.
 */
public class SinopticApp extends Application {

    private final String nameOfDB = "SinpticDB.realm";

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().name(nameOfDB).build();
        Realm.setDefaultConfiguration(config);
    }

}
