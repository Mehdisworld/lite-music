package com.ultimate.music.downloader.downloader;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;

import com.ultimate.music.downloader.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.callback.Callback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Response;

public class VK_Engine extends Fragment {
    private static final String TAG = "VK_Engine";
    private static final String ARG_SECTION_NUMBER = "section_number";
    public VKAdapter adapter;
    private boolean isLoaded = false;
    public AutoLoadListView listView;
    private PageViewModel pageViewModel;
    public String q;
    public SwipeRefreshLayout swipe;

    public static VK_Engine newInstance(int i) {
        VK_Engine VKEngine = new VK_Engine();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, i);
        VKEngine.setArguments(bundle);
        return VKEngine;
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
                VK_Engine VKEngine = VK_Engine.this;
                VKEngine.getList(VKEngine.q);
            }
        });
        this.listView = (AutoLoadListView) inflate.findViewById(R.id.listview);
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                new DownloadDialog();
                DownloadDialog.newInstance(VK_Engine.this.adapter.getItem(i)).show(VK_Engine.this.getChildFragmentManager(), "download");
                new Random().nextInt(5);
            }
        });
        this.listView.setOnLoadListener(new AutoLoadListView.OnLoadListener() {
            public void onLoad() {
                VK_Engine.this.listView.onLoadComplete();
            }
        });
        this.adapter = new VKAdapter(getActivity());
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
        ((GetBuilder) getBuilder.url("http://rilds.com/song/215534564-" + str + "/")).build().execute(new Callback<List<Songs>>() {
            public List<Songs> parseNetworkResponse(Response response, int i) throws Exception {
                Elements select = Jsoup.parse(response.body().string()).select("ul.playlist li");
                ArrayList arrayList = new ArrayList();
                for (int i2 = 0; i2 < select.size(); i2++) {
                    Element element = (Element) select.get(i2);
                    Songs songs = new Songs();
                    songs.setSongname(element.select("div.playlist-name span.playlist-name-title a em").text());
                    songs.setArtistname(element.select("div.playlist-name span.playlist-name-artist a").text());
                    songs.setTime(element.select("div.playlist-right span.playlist-duration").text());
                    songs.setDuation(Utils.getSecondsNum(songs.getTime()));
                    songs.setMp3url(element.select("div.playlist-btn a.playlist-play").attr("data-url"));
                    arrayList.add(songs);
                }
                Log.d(TAG, "parseNetworkResponse: ");
                return arrayList;
            }

            public void onError(Call call, Exception exc, int i) {
                Log.d(TAG, "onError: " + exc.toString());
                exc.printStackTrace();
                VK_Engine.this.swipe.setRefreshing(false);
                VK_Engine.this.adapter.clear();
            }

            public void onResponse(List<Songs> list, int i) {
                Log.d(TAG, "onResponse: ");
                VK_Engine.this.swipe.setRefreshing(false);
                VK_Engine.this.adapter.clear();
                VK_Engine.this.adapter.addPage(list);
                VK_Engine.this.listView.smoothScrollToPosition(0);
            }
        });
    }
}
