package com.ultimate.music.downloader.downloader;

import static android.text.TextUtils.isEmpty;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import androidx.media2.exoplayer.external.C;
import androidx.media2.exoplayer.external.upstream.DefaultLoadErrorHandlingPolicy;

import com.ultimate.music.downloader.R;

import java.io.File;
import java.security.MessageDigest;
import java.util.Locale;


public class Utils {
  Context context;
  public static String MAIN_DIR = "/Music";

  public Utils(Context context) {
    this.context = context;
  }

  public static int getScoreStatus(Context context) {
    return context.getSharedPreferences("data", 0).getInt("score_status", 0);
  }

  public static void setScoreStatus(Context context) {
    Editor edit = context.getSharedPreferences("data", 0).edit();
    edit.putInt("score_status", 1);
    edit.commit();
  }


  @SuppressLint({"WrongConstant", "RestrictedApi"})
  public static void launchAppDetail(Context context, String str) {
    if (!str.contains("https"))
      try {
        if (!isEmpty(str)) {
          StringBuilder sb = new StringBuilder();
          sb.append("market://details?id=");
          sb.append(str);
          Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(sb.toString()));
          intent.addFlags(C.ENCODING_PCM_MU_LAW);
          context.startActivity(intent);
        }
      } catch (Exception unused) {
        Toast.makeText(context, "Jump failure", 1).show();
      }
    else {
      Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(str));
      context.startActivity(browserIntent);
    }
  }


  public static String formatTime(String str, long j) {
    @SuppressLint("RestrictedApi") int i = (int) (j / DefaultLoadErrorHandlingPolicy.DEFAULT_TRACK_BLACKLIST_MS);
    int i2 = (int) ((j / 1000) % 60);
    Object[] objArr = {Integer.valueOf(i)};
    String str2 = "%02d";
    String format = String.format(Locale.getDefault(), str2, objArr);
    return str.replace("mm", format).replace("ss", String.format(Locale.getDefault(), str2, new Object[]{Integer.valueOf(i2)}));
  }

  public static int getSecondsNum(String str) {
    String[] split = str.split(":");
    int i = 0;
    if (split != null && split.length == 2) {
      return (Integer.parseInt(split[0]) * 60) + Integer.parseInt(split[1]);
    }
    if (split != null && split.length == 3) {
      i = (Integer.parseInt(split[0]) * 60 * 60) + (Integer.parseInt(split[1]) * 60) + Integer.parseInt(split[2]);
    }
    return i;
  }

  public static String MD5Encode(String origin, String charsetname) {
    String resultString = null;
    try {
      resultString = new String(origin);
      MessageDigest md = MessageDigest.getInstance("MD5");
      if (charsetname == null || "".equals(charsetname))
        resultString = byteArrayToHexString(md.digest(resultString
                .getBytes()));
      else
        resultString = byteArrayToHexString(md.digest(resultString
                .getBytes(charsetname)));
    } catch (Exception exception) {
    }
    return resultString;
  }

  private static String byteArrayToHexString(byte b[]) {
    StringBuffer resultSb = new StringBuffer();
    for (int i = 0; i < b.length; i++)
      resultSb.append(byteToHexString(b[i]));

    return resultSb.toString();
  }

  private static final String hexDigits[] = {"0", "1", "2", "3", "4", "5",
          "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

  private static String byteToHexString(byte b) {
    int n = b;
    if (n < 0)
      n += 256;
    int d1 = n / 16;
    int d2 = n % 16;
    return hexDigits[d1] + hexDigits[d2];
  }


  public static String getAppExternalDir() {
    StringBuilder sb = new StringBuilder();
    sb.append(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS));
    sb.append(MAIN_DIR);
    return sb.toString();
  }

  public static String getSongDir() {
    return mkdirs(getAppExternalDir());
  }

  private static String mkdirs(String str) {
    File file = new File(str);
    if (!file.exists()) {
      file.mkdirs();
    }
    return str;
  }

  public void shareApp() {
    String shareLink = "http://play.google.com/store/apps/details?id=" + context.getPackageName();
    Intent share = new Intent(Intent.ACTION_SEND);
    share.setType("text/plain");
    share.putExtra(Intent.EXTRA_TEXT, shareLink);
    context.startActivity(share);
  }

  public void moreApps() {
    final String NameStore = context.getString(R.string.store_name);

    try {
      context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://developer?id=" + NameStore)));
    } catch (android.content.ActivityNotFoundException e) {
      context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=" + NameStore)));
    }
  }

  public void rateApp() {
    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
  }
}
