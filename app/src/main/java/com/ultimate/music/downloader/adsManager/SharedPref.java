package com.ultimate.music.downloader.adsManager;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    SharedPreferences mySharedPref ;
    public SharedPref(Context context) {
       mySharedPref = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
    }

    public void SaveInt(String key,int state) {
        SharedPreferences.Editor editor = mySharedPref.edit();
        editor.putInt(key,state);
        editor.commit();
    }
    // this method will load the Night Mode State
    public int LoadInt (String key){
       int  state = mySharedPref.getInt(key,0);
        return  state;
    }
    public void clear(){
        mySharedPref.edit().clear().apply();
    }

    public void setNightModeState(Boolean state) {
        SharedPreferences.Editor editor = mySharedPref.edit();
        editor.putBoolean("NightMode",state);
        editor.apply();
    }
    public Boolean loadNightModeState (){
        Boolean state = mySharedPref.getBoolean("NightMode",false);
        return  state;
    }
}
