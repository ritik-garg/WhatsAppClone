package com.ritik.whatsappclone;

import android.app.Application;

import com.parse.Parse;

public class App extends Application {
    @Override
    public void onCreate() {
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("SekpyRVPILfbvNFrhFPxTk9fcQMEK68ERm5U9nnM")
                .clientKey("QxY2pK0yE69YgsLUTvgjFQr3FZJAdJ4FoA3sf8Sj")
                .server("https://parseapi.back4app.com/")
                .build()
        );
        super.onCreate();
    }
}
