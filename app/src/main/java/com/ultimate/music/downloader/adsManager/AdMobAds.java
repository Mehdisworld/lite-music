package com.ultimate.music.downloader.adsManager;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.ultimate.music.downloader.App;
import com.ultimate.music.downloader.R;


public class AdMobAds {
    // TODO: x number of clicks
    public static int maxClick = 5;
    public static int maxInter = 2;
    private static final String TAG = "Rewarded";
    private static final String TAG_INTER = "Inter";
    private final SharedPref sharedPref;
    Context context;
    Boolean Action = true;
    ActionListener listener;
    InterstitialAd mInterstitialAd;
    private RewardedAd mRewardedAd;

    public AdMobAds(Context context) {
        this.context = context;
        MobileAds.initialize(context);
        sharedPref = new SharedPref(context);
    }

    public interface ActionListener {
        void onDone();
    }

    public void showBanner(RelativeLayout adContainer) {
        AdView adView = new AdView(context);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(App.Banner_Admob);
        adContainer.addView(adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    public void loadAd() {
        LoadInter();
    }

    public void LoadInter() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(context, App.Interstitial_Admob, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                mInterstitialAd = interstitialAd;
                Log.i(TAG_INTER, "onAdLoaded");
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        loadAd();
                        Log.d(TAG, "The ad was dismissed.");
                        if (listener != null && Action)
                            listener.onDone();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        Log.d(TAG_INTER, "The ad failed to show.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        mInterstitialAd = null;
                        Log.d(TAG_INTER, "The ad was shown.");
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.i(TAG_INTER, loadAdError.getMessage());
                mInterstitialAd = null;
            }
        });
    }

    public void showInter(ActionListener listener) {
        Action = true;
        this.listener = listener;
        Log.d(TAG_INTER, "InterAd " + sharedPref.LoadInt("ads"));
        if (sharedPref.LoadInt("ads") % maxInter == 0) {
            if (mInterstitialAd != null) {
                mInterstitialAd.show((Activity) context);
            } else if (listener != null)
                listener.onDone();
        } else if (listener != null)
            listener.onDone();
        sharedPref.SaveInt("ads", sharedPref.LoadInt("ads") + 1);

    }

    /*Rewarded Ad*/
    public void showRewardedAd(ActionListener listener) {
        Action = true;
        this.listener = listener;
        Log.d(TAG, "showRewardedAd " + sharedPref.LoadInt("ads"));
        if (sharedPref.LoadInt("ads") % maxClick == 0) {
            displayRewardedAd();
        } else if (listener != null) {
            listener.onDone();
        }
        sharedPref.SaveInt("ads", sharedPref.LoadInt("ads") + 1);

    }

    public void displayRewardedAd() {
        if (mRewardedAd != null) {
            mRewardedAd.show((Activity) context, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    Log.d(TAG, "The user earned the reward.");

                }
            });
        } else {
            Log.d(TAG, "The rewarded ad wasn't ready yet.");
        }
    }

    public void loadRewardedAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(context, context.getString(R.string.Admob_Reward_Ad_Unit),
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.d(TAG, loadAdError.getMessage());
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        Log.d(TAG, "Ad was loaded.");

                        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                Log.d(TAG, "Ad was shown.");
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                Log.d(TAG, "Ad failed to show.");
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                Log.d(TAG, "Ad was dismissed.");
                                mRewardedAd = null;
                                loadRewardedAd();
                                if (listener != null && Action)
                                    listener.onDone();
                            }
                        });
                    }
                });
    }
}