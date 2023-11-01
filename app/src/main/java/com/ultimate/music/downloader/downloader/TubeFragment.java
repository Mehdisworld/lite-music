package com.ultimate.music.downloader.downloader;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ultimate.music.downloader.R;
import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.YoutubeException;
import com.github.kiulian.downloader.model.YoutubeVideo;
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

public class TubeFragment extends Fragment {
  private static final String ARG_SECTION_NUMBER = "section_number";
  private KOLPH_TubeAdapter adapter;
  private boolean isLoaded = false;
  private AutoLoadListView listView_music_tube;
  private String pageToken = "";
  private PageViewModel pageViewModel;
  private String q;
  private SwipeRefreshLayout swipe_music_tube;

  public static TubeFragment newInstance(int i) {
    TubeFragment uHJNB_SearchTubeFragment = new TubeFragment();
    Bundle bundle = new Bundle();
    bundle.putInt(ARG_SECTION_NUMBER, i);
    uHJNB_SearchTubeFragment.setArguments(bundle);
    return uHJNB_SearchTubeFragment;
  }

  @Override
  public void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    this.pageViewModel = (PageViewModel) ViewModelProviders.of(this).get(PageViewModel.class);
    this.pageViewModel.setIndex(getArguments() != null ? getArguments().getInt(ARG_SECTION_NUMBER) : 1);
  }

  @Override
  public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
    View inflate = layoutInflater.inflate(R.layout.fragment_main, viewGroup, false);
    SwipeRefreshLayout swipeRefreshLayout = inflate.findViewById(R.id.swipe);
    this.swipe_music_tube = swipeRefreshLayout;
    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        TubeFragment uHJNB_SearchTubeFragment = TubeFragment.this;
        uHJNB_SearchTubeFragment.getList(uHJNB_SearchTubeFragment.q);
      }
    });
    AutoLoadListView iJNM_AutoLoaderListView = inflate.findViewById(R.id.listview);
    this.listView_music_tube = iJNM_AutoLoaderListView;
    iJNM_AutoLoaderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        TubeFragment uHJNB_SearchTubeFragment = TubeFragment.this;
        uHJNB_SearchTubeFragment.getMp3Url(uHJNB_SearchTubeFragment.adapter.getItem(i));
        new Random().nextInt(5);
      }
    });
    this.listView_music_tube.setOnLoadListener(new AutoLoadListView.OnLoadListener() {
      @Override
      public void onLoad() {
        TubeFragment.this.getLoad();
        TubeFragment.this.listView_music_tube.onLoadComplete();
      }
    });
    KOLPH_TubeAdapter kOLPH_TubeAdapter = new KOLPH_TubeAdapter(getActivity());
    this.adapter = kOLPH_TubeAdapter;
    this.listView_music_tube.setAdapter((ListAdapter) kOLPH_TubeAdapter);
    this.pageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {

      public void onChanged(String str) {
      }
    });
    return inflate;
  }

  @Override
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

  private void getList(String str) {
    this.swipe_music_tube.setRefreshing(true);
    this.pageToken = "";
    GetBuilder getBuilder = OkHttpUtils.get();
    ((GetBuilder) ((GetBuilder) getBuilder.url("https://line.1010diy.com/web/free-mp3-finder/query?q=" + str + "&type=youtube&pageToken=" + this.pageToken)).addHeader("user-agent", WebSettings.getDefaultUserAgent(getActivity()))).build().execute(new Callback<List<Songs>>() {

      @Override
      public List<Songs> parseNetworkResponse(Response response, int i) throws Exception {
        JSONObject jSONObject = new JSONObject(response.body().string()).getJSONObject("data");
        TubeFragment.this.pageToken = jSONObject.getString("nextPageToken");
        ArrayList arrayList = new ArrayList();
        JSONArray jSONArray = jSONObject.getJSONArray("items");
        if (jSONArray != null && jSONArray.length() > 0) {
          for (int i2 = 0; i2 < jSONArray.length(); i2++) {
            Songs songs = new Songs();
            JSONObject jSONObject2 = jSONArray.getJSONObject(i2);
            songs.setSongid(jSONObject2.getString("id"));
            songs.setSongname(jSONObject2.getString("title"));
            songs.setArtistname("");
            songs.setTime(jSONObject2.getString("duration"));
            arrayList.add(songs);
          }
        }
        return arrayList;
      }

      @Override
      public void onError(Call call, Exception exc, int i) {
        exc.printStackTrace();
        TubeFragment.this.swipe_music_tube.setRefreshing(false);
        TubeFragment.this.adapter.clear();
      }

      public void onResponse(List<Songs> list, int i) {
        TubeFragment.this.swipe_music_tube.setRefreshing(false);
        TubeFragment.this.adapter.clear();
        TubeFragment.this.adapter.addPage(list);
        TubeFragment.this.listView_music_tube.smoothScrollToPosition(0);
      }
    });
  }

  private void getLoad() {
    if (TextUtils.isEmpty(this.pageToken)) {
      this.listView_music_tube.onLoadComplete();
      return;
    }
    GetBuilder getBuilder = OkHttpUtils.get();
    ((GetBuilder) ((GetBuilder) getBuilder.url("https://line.1010diy.com/web/free-mp3-finder/query?q=" + this.q + "&type=youtube&pageToken=" + this.pageToken)).addHeader("user-agent", WebSettings.getDefaultUserAgent(getActivity()))).build().execute(new Callback<List<Songs>>() {
      @Override
      public List<Songs> parseNetworkResponse(Response response, int i) throws Exception {
        JSONObject jSONObject = new JSONObject(response.body().string()).getJSONObject("data");
        TubeFragment.this.pageToken = jSONObject.getString("nextPageToken");
        ArrayList arrayList = new ArrayList();
        JSONArray jSONArray = jSONObject.getJSONArray("items");
        if (jSONArray != null && jSONArray.length() > 0) {
          for (int i2 = 0; i2 < jSONArray.length(); i2++) {
            Songs songs = new Songs();
            JSONObject jSONObject2 = jSONArray.getJSONObject(i2);
            songs.setSongid(jSONObject2.getString("id"));
            songs.setSongname(jSONObject2.getString("title").replace("...", " ").replace(".", " ").replace("-", " ").replace("\\", " ").replace("/", " ").replace("#", " "));
            songs.setArtistname("");
            songs.setTime(jSONObject2.getString("duration"));
            arrayList.add(songs);
          }
        }
        return arrayList;
      }

      @Override
      public void onError(Call call, Exception exc, int i) {
        exc.printStackTrace();
        TubeFragment.this.listView_music_tube.onLoadComplete();
      }

      public void onResponse(List<Songs> list, int i) {
        TubeFragment.this.listView_music_tube.onLoadComplete();
        if (list.size() > 0) {
          TubeFragment.this.adapter.addPage(list);
        }
      }
    });
  }

  @SuppressLint("StaticFieldLeak")
  private void getMp3Url(final Songs songs) {
    final ProgressDialog progressDialog = new ProgressDialog(getActivity());
    progressDialog.setMessage("loading");
    progressDialog.show();
    new AsyncTask<Void, Void, Songs>() {
      public Songs doInBackground(Void... voidArr) {
        try {
          YoutubeVideo video = new YoutubeDownloader().getVideo(songs.getSongid());
          if (video == null || video.audioFormats().size() <= 0 || video.videoFormats().size() <= 0) {
            return null;
          }
          Songs songs = new Songs();
          songs.setSongid(songs.getSongid());
          songs.setSongname(video.details().title().replace("...", " ").replace("-", " ").replace("\\", " ").replace("/", " ").replace("#", " "));
          songs.setArtistname("");
          songs.setMp3url(video.audioFormats().get(0).url());
          return songs;
        } catch (YoutubeException e) {
          e.printStackTrace();
          return null;
        }
      }

      @SuppressLint("WrongConstant")
      public void onPostExecute(Songs songs) {
        super.onPostExecute((Songs) songs);
        if (songs != null) {
          progressDialog.dismiss();
          if (!TextUtils.isEmpty(songs.getMp3url())) {
            DownloadDialog.newInstance(songs).show(TubeFragment.this.getFragmentManager(), "download");
            return;
          }
          return;
        }
        progressDialog.dismiss();
        Toast.makeText(TubeFragment.this.getActivity(), "There is no music", 1).show();
      }
    }.execute(new Void[0]);
  }
}
