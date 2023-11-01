package com.ultimate.music.downloader.adsManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.RelativeLayout;

import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.BannerListener;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.ultimate.music.downloader.R;

import static com.ironsource.mediationsdk.IronSource.isInterstitialReady;

public class IronSourceAds {
    public static int maxClick = 2;
    private SharedPref sharedPref;
    ProgressDialog progressDialog;
    Context context;
    Boolean Action = true;
    ActionListener listener;
    private static final String TAG = "IronSourceAds";
    public IronSourceAds(Context context) {
        this.context = context;
        sharedPref = new SharedPref(context);
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Ad is Loading");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
    }

    public void loadIronSourceAdNetwork() {
        loadInter();
    }

    public void showBanner(RelativeLayout adContainer) {
        IronSourceBannerLayout banner;
        IronSource.init((Activity) context,context.getString(R.string.IronSource_app_key), IronSource.AD_UNIT.BANNER);
        banner = IronSource.createBanner((Activity) context, ISBannerSize.BANNER);
        adContainer.addView(banner);
        IronSource.loadBanner(banner);
        banner.setBannerListener(new BannerListener() {
            @Override
            public void onBannerAdLoaded() {
                Log.d(TAG, "BannerAdLoaded");
            }

            @Override
            public void onBannerAdLoadFailed(IronSourceError ironSourceError) {
                Log.d(TAG, "BannerAdLoadFailed");
            }

            @Override
            public void onBannerAdClicked() {

            }

            @Override
            public void onBannerAdScreenPresented() {

            }

            @Override
            public void onBannerAdScreenDismissed() {

            }

            @Override
            public void onBannerAdLeftApplication() {

            }
        });
    }

    private void loadInter() {
        IronSource.init( context.getApplicationContext(), this.context.getString(R.string.IronSource_app_key),IronSource.AD_UNIT.INTERSTITIAL);
        IronSource.setInterstitialListener(new InterstitialListener() {
            @Override
            public void onInterstitialAdReady() {
                Log.d("IronSource", "Ad is Ready");
            }

            @Override
            public void onInterstitialAdLoadFailed(IronSourceError error) {
                Log.d(TAG, "Failed to load");
            }

            @Override
            public void onInterstitialAdOpened() {
                Log.d(TAG, "onInterstitialAdOpened: ");
            }

            @Override
            public void onInterstitialAdClosed() {
                if (listener != null && Action)
                    listener.onDone();
                Log.d(TAG, "Ad Closed");
            }

            @Override
            public void onInterstitialAdShowFailed(IronSourceError error) {
                Log.d(TAG, "Failed to show ad");
            }

            @Override
            public void onInterstitialAdClicked() {
            }

            @Override
            public void onInterstitialAdShowSucceeded() {
                Log.d(TAG, "Ad was shown");
            }
        });
        IronSource.loadInterstitial();
    }

    public void showInter(ActionListener listener) {
        Action = true;
        this.listener = listener;
        if (sharedPref.LoadInt("ads") % maxClick == 0) {
            if (isInterstitialReady()) {
                progressDialog.show();
                new CountDownTimer(2000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {  // runs after 2 sec. (2000 millSec.)
                        IronSource.showInterstitial("DefaultInterstitial");
                        progressDialog.cancel();
                    }
                }.start();
            } else if (listener != null)
                listener.onDone();
        } else if (listener != null)
            listener.onDone();
        sharedPref.SaveInt("ads", sharedPref.LoadInt("ads") + 1);
    }

    public interface ActionListener {
        void onDone();
    }
}