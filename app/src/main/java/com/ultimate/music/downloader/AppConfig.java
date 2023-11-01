package com.ultimate.music.downloader;

import androidx.multidex.MultiDexApplication;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;

import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient.Builder;

public class AppConfig extends MultiDexApplication {

  public void onCreate() {
    super.onCreate();

    new CookieJarImpl(new PersistentCookieStore(getApplicationContext()));
    OkHttpUtils.initClient(new Builder().connectTimeout(60L, TimeUnit.SECONDS).readTimeout(60L, TimeUnit.SECONDS).writeTimeout(60L, TimeUnit.SECONDS).proxy(Proxy.NO_PROXY).build());

  }

}
