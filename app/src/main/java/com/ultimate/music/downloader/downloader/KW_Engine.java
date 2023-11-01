package com.ultimate.music.downloader.downloader;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;

import com.ultimate.music.downloader.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import okhttp3.Call;
import okhttp3.Response;

public class KW_Engine extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    public KWAdapter adapter;
    private boolean isLoaded = false;
    public AutoLoadListView listView;
    private PageViewModel pageViewModel;
    public String q;
    public SwipeRefreshLayout swipe;
    private String f227q;

    public static KW_Engine newInstance(int i) {
        KW_Engine KWEngine = new KW_Engine();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, i);
        KWEngine.setArguments(bundle);
        return KWEngine;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.pageViewModel = (PageViewModel) ViewModelProviders.of((Fragment) this).get(PageViewModel.class);
        this.pageViewModel.setIndex(getArguments() != null ? getArguments().getInt(ARG_SECTION_NUMBER) : 1);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_main, viewGroup, false);
        this.swipe = (SwipeRefreshLayout) inflate.findViewById(R.id.swipe);
        this.swipe.setOnRefreshListener(new OnRefreshListener() {
            public void onRefresh() {
                KW_Engine KWEngine = KW_Engine.this;
                KWEngine.getList(KWEngine.q);
            }
        });
        this.listView = (AutoLoadListView) inflate.findViewById(R.id.listview);
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                KW_Engine KWEngine = KW_Engine.this;
                KWEngine.getMp3Url(KWEngine.adapter.getItem(i));
                new Random().nextInt(5);
            }
        });
        this.listView.setOnLoadListener(new AutoLoadListView.OnLoadListener() {
            public void onLoad() {
                KW_Engine.this.getLoad();
            }
        });
        this.adapter = new KWAdapter(getActivity());
        this.listView.setAdapter(this.adapter);
        this.pageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            public void onChanged(String str) {
            }
        });
        return inflate;
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.isLoaded = true;
    }

    public void search(String str) {
        if (this.isLoaded) {
            this.q = str;
            getList(str);
        }
    }


    /* access modifiers changed from: private */
    public void getList(String str) {
        this.swipe.setRefreshing(true);
        GetBuilder getBuilder = OkHttpUtils.get();
        ((GetBuilder) ((GetBuilder) getBuilder.url("https://search.kuwo.cn/r.s?client=kt&all=" + str + "&pn=" + 0 + "&rn=" + 100 + "&uid=221260053&ver=kwplayer_ar_99.99.99.99&vipver=1&ft=music&cluster=0&strategy=2012&encoding=utf8&rformat=json&vermerge=1&mobi=1")).addHeader("user-agent", WebSettings.getDefaultUserAgent(getActivity()))).build().execute(new Callback<List<Songs>>() {
            public List<Songs> parseNetworkResponse(Response response, int i) throws Exception {
                JSONObject jSONObject = new JSONObject(response.body().string());
                ArrayList arrayList = new ArrayList();
                JSONArray jSONArray = jSONObject.getJSONArray("abslist");
                if (jSONArray != null && jSONArray.length() > 0) {
                    for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                        Songs songs = new Songs();
                        JSONObject jSONObject2 = jSONArray.getJSONObject(i2);
                        songs.setSongid(jSONObject2.getString("MUSICRID").replace("MUSIC_", ""));
                        songs.setSongname(jSONObject2.getString("SONGNAME"));
                        songs.setArtistname(jSONObject2.getString("ARTIST"));
                        songs.setDuation(Integer.parseInt(jSONObject2.getString("DURATION")));
                        songs.setTime(Utils.formatTime("mm:ss", (long) (songs.getDuation() * 1000)));
                        arrayList.add(songs);
                    }
                }
                return arrayList;
            }

            public void onError(Call call, Exception exc, int i) {
                exc.printStackTrace();
                Log.d("error", exc.toString());
                Log.d("error", "\"https://search.kuwo.cn/r.s?client=kt&all=\" + str + \"&pn=\" + 0 + \"&rn=\" + 100 + \"&uid=221260053&ver=kwplayer_ar_99.99.99.99&vipver=1&ft=music&cluster=0&strategy=2012&encoding=utf8&rformat=json&vermerge=1&mobi=1\"");
                KW_Engine.this.swipe.setRefreshing(false);
                KW_Engine.this.adapter.clear();
            }

            public void onResponse(List<Songs> list, int i) {
                KW_Engine.this.swipe.setRefreshing(false);
                KW_Engine.this.adapter.clear();
                KW_Engine.this.adapter.addPage(list);
                KW_Engine.this.listView.smoothScrollToPosition(0);
            }
        });
    }


    /* access modifiers changed from: private */
    public void getLoad() {
        GetBuilder getBuilder = OkHttpUtils.get();
        ((GetBuilder) ((GetBuilder) getBuilder.url("https://search.kuwo.cn/r.s?client=kt&all=" + this.f227q + "&pn=" + this.adapter.getPageNo() + "&rn=" + 100 + "&uid=221260053&ver=kwplayer_ar_99.99.99.99&vipver=1&ft=music&cluster=0&strategy=2012&encoding=utf8&rformat=json&vermerge=1&mobi=1")).addHeader("user-agent", WebSettings.getDefaultUserAgent(getActivity()))).build().execute(new Callback<List<Songs>>() {
            public List<Songs> parseNetworkResponse(Response response, int i) throws Exception {
                JSONObject jSONObject = new JSONObject(response.body().string());
                ArrayList arrayList = new ArrayList();
                JSONArray jSONArray = jSONObject.getJSONArray("abslist");
                if (jSONArray != null && jSONArray.length() > 0) {
                    for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                        Songs songs = new Songs();
                        JSONObject jSONObject2 = jSONArray.getJSONObject(i2);
                        songs.setSongid(jSONObject2.getString("MUSICRID").replace("MUSIC_", ""));
                        songs.setSongname(jSONObject2.getString("SONGNAME"));
                        songs.setArtistname(jSONObject2.getString("ARTIST"));
                        songs.setDuation(Integer.parseInt(jSONObject2.getString("DURATION")));
                        songs.setTime(Utils.formatTime("mm:ss", (long) (songs.getDuation() * 1000)));
                        arrayList.add(songs);
                    }
                }
                return arrayList;
            }

            public void onError(Call call, Exception exc, int i) {
                exc.printStackTrace();
                KW_Engine.this.listView.onLoadComplete();
            }

            public void onResponse(List<Songs> list, int i) {
                KW_Engine.this.listView.onLoadComplete();
                if (list.size() > 0) {
                    KW_Engine.this.adapter.addPage(list);
                }
            }
        });
    }


    /* access modifiers changed from: private */
    public void getMp3Url(final Songs songs) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("loading");
        progressDialog.show();
        GetBuilder getBuilder = OkHttpUtils.get();

        ((GetBuilder) ((GetBuilder) getBuilder.url("https://antiserver.kuwo.cn/anti.s?useless=&format=mp3&rid=MUSIC_" + songs.getSongid() + "&response=res&type=convert_url&")).addHeader("user-agent", WebSettings.getDefaultUserAgent(getActivity()))).build().execute(new Callback<Songs>() {
            @Override
            public Songs parseNetworkResponse(Response response, int i) throws Exception {
                String url = response.request().url().url().toString();
                if (!TextUtils.isEmpty(url)) {
                    songs.setMp3url(url);
                }
                return songs;
            }

            public void onError(Call call, Exception exc, int i) {
                exc.printStackTrace();
                progressDialog.dismiss();
                Toast.makeText(KW_Engine.this.getActivity(), "There is no music 2", Toast.LENGTH_LONG).show();
            }

            public void onResponse(Songs songs, int i) {
                progressDialog.dismiss();
                if (!TextUtils.isEmpty(songs.getMp3url())) {
                    assert KW_Engine.this.getFragmentManager() != null;
                    DownloadDialog.newInstance(songs).show(KW_Engine.this.getFragmentManager(), "download");
                }
            }
        });
    }

}
