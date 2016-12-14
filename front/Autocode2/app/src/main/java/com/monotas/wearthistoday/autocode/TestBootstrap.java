package com.monotas.wearthistoday.autocode;

/**
 * Created by Maeno on 2016/12/14.
 */
import android.app.Application;
import com.beardedhen.androidbootstrap.TypefaceProvider;

// Applicationを継承
public class TestBootstrap extends Application {
    @Override public void onCreate() {
        super.onCreate();
        TypefaceProvider.registerDefaultIconSets();
    }
}