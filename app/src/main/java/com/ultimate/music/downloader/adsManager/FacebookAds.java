package com.ultimate.music.downloader.adsManager;

import android.content.Context;
import android.util.Log;
import android.widget.RelativeLayout;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAdListener;
import com.ultimate.music.downloader.R;


public class FacebookAds {
    public static int maxclick = 2; //todo change number of click for showing Fan Ads
    private SharedPref sharedPref;
    Context context;
    Boolean Action = true;
    ActionListener listen;
    com.facebook.ads.InterstitialAd interstitialAd;
    com.facebook.ads.AdView adView;

    public FacebookAds(Context context) {
        this.context = context;
        sharedPref = new SharedPref(context);
    }

    public void LoadFacebookAds() {
        LoadInterstitial();
    }

    public void showBanner(RelativeLayout adContainer){
        adView = new AdView(context,context.getString(R.string.Fan_Banner_Ad_Unit), AdSize.BANNER_HEIGHT_50);
        adContainer.addView(adView);
        adView.loadAd();
    }

    private void LoadInterstitial() {
        interstitialAd = new com.facebook.ads.InterstitialAd(context, context.getString(R.string.Fan_Interstitial_Ad_Unit));
        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                interstitialAd.loadAd();
                if (listen != null && Action)
                    listen.onDone();
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback

            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed

                // Show the ad

            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback

            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback

            }
        };

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd(
                interstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());

        // interstitialAd.show();
    }

    public void showAd(ActionListener listener) {
        Action = true;
        this.listen = listener;
        Log.i("AdNetwork", " " + sharedPref.LoadInt("ads"));
        if (sharedPref.LoadInt("ads") % maxclick == 0) {
            if (interstitialAd != null && interstitialAd.isAdLoaded()) {
                interstitialAd.show();
                Log.i("AdNetwork", " facebook" + interstitialAd.isAdLoaded());
            } else if (listen != null)
                listen.onDone();
        } else if (listen != null)
            listen.onDone();
        sharedPref.SaveInt("ads", sharedPref.LoadInt("ads") + 1);
    }


    public interface ActionListener {
        void onDone();
    }

}
