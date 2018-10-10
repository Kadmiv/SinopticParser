package com.gaijin.sinopticparser.views;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.gaijin.sinopticparser.MainActivity;
import com.gaijin.sinopticparser.db.RealmCity;

import java.util.regex.Matcher;

import io.realm.RealmResults;

/**
 * Created by Kachulyak Ivan.
 */
public class SinopticApp extends Application {

    RealmResults<RealmCity> cityList;
    SharedPreferences sharedCity;
    final String cityKey = "city_key";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MyLog"," Create Sinoptic App");
        Intent startMainActivity = new Intent(this, MainActivity.class);
        //startMainActivity.putExtra("FirstLoadedCity",)
    }
}
