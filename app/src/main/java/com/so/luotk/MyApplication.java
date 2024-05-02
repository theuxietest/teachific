package com.so.luotk;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.so.luotk.utils.PreferenceHandler;

import java.util.Locale;


public class MyApplication extends Application {
    public static Typeface productSansRegular, productSansBold, productSansMedium, latoBold, latoLight, latoRegular, latoSemiBold, lato;
    private static Context context;
    private Locale locale = null;
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        if (locale != null)
        {
            newConfig.locale = locale;
            Locale.setDefault(locale);
            getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
        }
    }
    @Override
    public void onCreate() {

        super.onCreate();
        //Get futurah_font from assets folder, Make sure use correct directory and correct spellings
        productSansBold = Typeface.createFromAsset(getAssets(), "font/ProductSansBold.ttf");
        productSansRegular = Typeface.createFromAsset(getAssets(), "font/ProductSansRegular.ttf");
        productSansMedium = Typeface.createFromAsset(getAssets(), "font/ProductSansMedium.ttf");
        latoBold = Typeface.createFromAsset(getAssets(), "font/LatoBold.ttf");
        latoLight = Typeface.createFromAsset(getAssets(), "font/LatoLight.ttf");
        latoRegular = Typeface.createFromAsset(getAssets(), "font/LatoRegular.ttf");
        latoSemiBold = Typeface.createFromAsset(getAssets(), "font/latosemibold.ttf");
        lato = Typeface.createFromAsset(getAssets(), "font/LatoRegular.ttf");

        FirebaseApp.initializeApp(getApplicationContext());
        String lang = PreferenceHandler.readString(getBaseContext(), PreferenceHandler.LOCALE, "");
        Log.d("TAG", "onCreate: " + lang);
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(myLocale);
        } else {
            conf.locale = myLocale;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            createConfigurationContext(conf);
        } else {
            res.updateConfiguration(conf, dm);
        }
    }



    public static Context getAppContext() {
        return MyApplication.context;
    }
}
