package com.ultimate.music.downloader

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import dagger.hilt.android.HiltAndroidApp
import org.json.JSONException


@HiltAndroidApp
class App: MultiDexApplication(){

    private val TAG = "MyApp"
    var mQueue: RequestQueue? = null

    companion object{
        @JvmField
        var NETWORK: String = ""
        @JvmField
        var Interstitial_Admob: String = " "
        @JvmField
        var Banner_Admob: String = " "
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        // TODO: Json File URL
        const val JSON_URL = "https://drive.google.com/uc?export=download&id=1kcLZzBjUV-qi_WkdjMNHaFWBOuq4peN_"
        var is_Update = false
        var NEW_PACKAGE_NAME = " "
    }
    override fun onCreate() {
        super.onCreate()
        context = this

        adNetworkData();
    }

    fun adNetworkData() {
        mQueue = Volley.newRequestQueue(getApplicationContext());
        val request = JsonObjectRequest(
            Request.Method.GET, JSON_URL, null,
            { response ->
                Log.d(TAG, "onResponse: Successfully")
                try {
                    val remoteArray = response.getJSONArray("app_config")
                    for (i in 0 until remoteArray.length()) {
                        val remoteConf = remoteArray.getJSONObject(i)
                        NEW_PACKAGE_NAME = remoteConf.getString("new_package_name")
                        is_Update = remoteConf.getBoolean("send_update")
                        NETWORK = remoteConf.getString("adNetwork")
                        Banner_Admob = remoteConf.getString("admob_banner")
                        Interstitial_Admob = remoteConf.getString("admob_interstitial")
                        Log.d(TAG, "Ad Network: $NETWORK")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }) { error ->
            error.printStackTrace()
            Log.d(TAG, "NO DATA ")
        }

        request.setShouldCache(false);
        mQueue!!.add(request);
    }
}