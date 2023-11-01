package com.ultimate.music.downloader.downloader;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.ultimate.music.downloader.App;
import com.ultimate.music.downloader.R;
import com.ultimate.music.downloader.adsManager.AdMobAds;
import com.ultimate.music.downloader.adsManager.IronSourceAds;
import com.ultimate.music.downloader.autoComplete.AutoCompleteMusicAdapter;
import com.ultimate.music.downloader.autoComplete.MusicItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LibraryFragment extends Fragment {

    View view;
    private static final String TAG = "Json";
    public AutoCompleteTextView etSearch;
    public FlowLayout flow;
    private ImageView iv_clear;

    private final TubeFragment tubeEngine = new TubeFragment();
    private final KW_Engine kw_engine = new KW_Engine();
    private final VK_Engine vk_engine = new VK_Engine();

    public LayoutInflater mInflater;
    private MediaPlayer mediaPlayer;
    public TabLayout tabs;
    ViewPager viewPager;
    RockBandsAdapter rockBandsAdapter;
    TopRappersAdapter topRappersAdapter;
    PopularArtistsAdapter popularArtistsAdapter;
    NewReleasesAdapter newReleasesAdapter;
    GlobalMusicAdapter globalMusicAdapter;
    HappyMorningAdapter happyMorningAdapter;
    AfricaMusicAdapter africaMusicAdapter;
    private ArrayList<Item> mList, mList2, mList3, mList4, mList5, mList6, mList7;
    RecyclerView recyclerView_1, recyclerView_2, recyclerView_3, recyclerView_4, recyclerView_5, recyclerView_6, recyclerView_7;
    TextView txtGlobalMusic, txtPopArtists, txtPopularArtist, txtTopRappers, txtHappyMorning, txtAfricaMusic, txtRockBands;
    ProgressDialog progressDialog;
    RequestQueue mRequestQueue;
    JsonObjectRequest request;
    private List<MusicItem> autoMusicList;
    Utils utils;
    Context context;
    RelativeLayout adLayout;
    AdMobAds adMobAds;
    IronSourceAds ironSourceAds;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_library, container, false);
        setHasOptionsMenu(true);
        requireActivity().setTitle("Library");


        utils = new Utils(getContext());
        initView();

        // Progress Dialog
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Loading data");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        if (progressDialog != null) {
            progressDialog.show();
            new CountDownTimer(5000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    progressDialog.dismiss();
                }
            }.start();
        }
        // End Of Progress Dialog


        /* =================== Ad Network ======================= */
        adMobAds = new AdMobAds(requireActivity());
        adMobAds.loadAd();
        ironSourceAds = new IronSourceAds(requireActivity());
        ironSourceAds.loadIronSourceAdNetwork();
        /*========================Banner Ad===================*/
        if (App.NETWORK.equalsIgnoreCase("AdMob")) {
            adMobAds.showBanner(adLayout);
        } else {
            Log.d(TAG, "Failed to load");
        }

        // Important ArrayList Declaration ==================>
        autoMusicList = new ArrayList<>();
        mList = new ArrayList<>();
        mList2 = new ArrayList<>();
        mList3 = new ArrayList<>();
        mList4 = new ArrayList<>();
        mList5 = new ArrayList<>();
        mList6 = new ArrayList<>();
        mList7 = new ArrayList<>();

        parseJSON();
        autoComplete();

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getContext(), getChildFragmentManager());
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(sectionsPagerAdapter);
        ArrayList<androidx.fragment.app.Fragment> arrayList = new ArrayList<>();

        /* ========================= Music Search Engines  ===================*/
        arrayList.add(this.tubeEngine);
        arrayList.add(this.kw_engine);
        arrayList.add(this.vk_engine);
        sectionsPagerAdapter.addFragments(arrayList);
        this.tabs.setupWithViewPager(viewPager);
       /* tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });*/
        mInflater = LayoutInflater.from(getActivity().getApplicationContext());
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int i, KeyEvent event) {
                if (!etSearch.getText().toString().isEmpty()) {

                    if (i != 3) {
                        return false;
                    }
                    search(etSearch.getText().toString());
                    hideKeyboard(etSearch);
                    tabs.setVisibility(View.VISIBLE);
                    flow.setVisibility(View.GONE);
                }

                return true;
            }

        });

        iv_clear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                hideKeyboard(etSearch);
                etSearch.setText("");
            }
        });

        return view;
    }

    public void hideKeyboard(View view) {
        ((InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public MediaPlayer getMediaPlayer() {
        if (this.mediaPlayer == null) {
            this.mediaPlayer = new MediaPlayer();
        }
        return this.mediaPlayer;
    }

    private void autoComplete() {
        mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        request = new JsonObjectRequest(Request.Method.GET, App.JSON_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: successfully");
                try {
                    JSONArray autoCompleteArray = response.getJSONArray("auto_complete_songs");
                    for (int i = 0; i < autoCompleteArray.length(); i++) {
                        JSONObject info = autoCompleteArray.getJSONObject(i);
                        String autoCompleteArtist = info.getString("artist_name");
                        String autoCompleteCover = info.getString("album_cover_url");

                        autoMusicList.add(new MusicItem(autoCompleteArtist, autoCompleteCover));

                        AutoCompleteMusicAdapter adapter = new AutoCompleteMusicAdapter(getActivity().getApplicationContext(), autoMusicList);
                        etSearch.setAdapter(adapter);
                        etSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                hideKeyboard(etSearch);
                                loadMusic();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: NO DATA!! (Auto Complete)");
                error.printStackTrace();
            }
        });
        mRequestQueue.add(request);
    }

    private void loadMusic() {
        search(etSearch.getText().toString());
        hideKeyboard(etSearch);
        tabs.setVisibility(View.VISIBLE);
        flow.setVisibility(View.GONE);
    }

    private void parseJSON() {
        mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        request = new JsonObjectRequest(Request.Method.GET, App.JSON_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: Data is showing");
                progressDialog.dismiss();
                txtGlobalMusic.setVisibility(View.VISIBLE);
                txtPopArtists.setVisibility(View.VISIBLE);
                txtPopularArtist.setVisibility(View.VISIBLE);
                txtTopRappers.setVisibility(View.VISIBLE);
                txtHappyMorning.setVisibility(View.VISIBLE);
                txtAfricaMusic.setVisibility(View.VISIBLE);
                txtRockBands.setVisibility(View.VISIBLE);

                try {
                    JSONArray jsonArray7 = response.getJSONArray("Best_Rock_Bands");
                    JSONArray jsonArray = response.getJSONArray("top_rappers");
                    JSONArray jsonArray2 = response.getJSONArray("popular_artists");
                    JSONArray jsonArray3 = response.getJSONArray("top_pop_artists");
                    JSONArray jsonArray4 = response.getJSONArray("global_music");
                    JSONArray jsonArray5 = response.getJSONArray("happy_morning_music");
                    JSONArray jsonArray6 = response.getJSONArray("africa_music");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject hit = jsonArray.getJSONObject(i);
                        String coverUrl = hit.getString("album_cover_url");
                        String artistName = hit.getString("artist_name");

                        mList.add(new Item(coverUrl, artistName));
                        Collections.shuffle(mList);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

                        topRappersAdapter = new TopRappersAdapter(mList);
                        recyclerView_1.setLayoutManager(mLayoutManager);
                        recyclerView_1.setAdapter(topRappersAdapter);
                        topRappersAdapter.notifyDataSetChanged();


                        topRappersAdapter.setOnItemClickListener(new TopRappersAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                etSearch.setText(mList.get(position).getTextCover());
                                loadMusic();
                            }
                        });
                    }

                    for (int i = 0; i < jsonArray2.length(); i++) {
                        JSONObject chart = jsonArray2.getJSONObject(i);

                        String coverUrl2 = chart.getString("album_cover_url");
                        String artistName2 = chart.getString("artist_name");

                        mList2.add(new Item(coverUrl2, artistName2));
                        Collections.shuffle(mList2);
                        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                        popularArtistsAdapter = new PopularArtistsAdapter(mList2);
                        recyclerView_2.setLayoutManager(mLayoutManager2);
                        recyclerView_2.setAdapter(popularArtistsAdapter);
                        popularArtistsAdapter.notifyDataSetChanged();

                        popularArtistsAdapter.setOnItemClickListener(new PopularArtistsAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                etSearch.setText(mList2.get(position).getTextCover());
                                loadMusic();
                            }
                        });
                    }

                    for (int i = 0; i < jsonArray3.length(); i++) {

                        JSONObject releases = jsonArray3.getJSONObject(i);

                        String coverUrl3 = releases.getString("album_cover_url");
                        String artistName3 = releases.getString("artist_name");

                        mList3.add(new Item(coverUrl3, artistName3));
                        Collections.shuffle(mList3);
                        RecyclerView.LayoutManager mLayoutManager3 = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                        newReleasesAdapter = new NewReleasesAdapter(mList3);
                        recyclerView_3.setLayoutManager(mLayoutManager3);
                        recyclerView_3.setAdapter(newReleasesAdapter);
                        //newReleasesAdapter.notifyDataSetChanged();

                        newReleasesAdapter.setOnItemClickListener(new NewReleasesAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                etSearch.setText(mList3.get(position).getTextCover());
                                loadMusic();
                            }
                        });

                    }

                    for (int i = 0; i < jsonArray4.length(); i++) {

                        JSONObject global = jsonArray4.getJSONObject(i);

                        String coverUrl4 = global.getString("album_cover_url");
                        String artistName4 = global.getString("artist_name");

                        mList4.add(new Item(coverUrl4, artistName4));
                        Collections.shuffle(mList4);
                        RecyclerView.LayoutManager mLayoutManager4 = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                        globalMusicAdapter = new GlobalMusicAdapter(mList4);
                        recyclerView_4.setLayoutManager(mLayoutManager4);
                        recyclerView_4.setAdapter(globalMusicAdapter);
                        globalMusicAdapter.notifyDataSetChanged();

                        globalMusicAdapter.setOnItemClickListener(new GlobalMusicAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                etSearch.setText(mList4.get(position).getTextCover());
                                loadMusic();
                            }
                        });

                    }

                    for (int i = 0; i < jsonArray5.length(); i++) {
                        JSONObject happyMusic = jsonArray5.getJSONObject(i);

                        String coverUrl5 = happyMusic.getString("album_cover_url");
                        String artistName5 = happyMusic.getString("artist_name");

                        mList5.add(new Item(coverUrl5, artistName5));
                        Collections.shuffle(mList5);
                        RecyclerView.LayoutManager mLayoutManager5 = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                        happyMorningAdapter = new HappyMorningAdapter(mList5);
                        recyclerView_5.setLayoutManager(mLayoutManager5);
                        recyclerView_5.setAdapter(happyMorningAdapter);
                        happyMorningAdapter.notifyDataSetChanged();
                        happyMorningAdapter.setOnItemClickListener(new HappyMorningAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                etSearch.setText(mList5.get(position).getTextCover());
                                loadMusic();
                            }
                        });

                    }


                    for (int i = 0; i < jsonArray6.length(); i++) {
                        JSONObject africaMusic = jsonArray6.getJSONObject(i);

                        String coverUrl6 = africaMusic.getString("album_cover_url");
                        String artistName6 = africaMusic.getString("artist_name");

                        mList6.add(new Item(coverUrl6, artistName6));
                        Collections.shuffle(mList6);
                        RecyclerView.LayoutManager mLayoutManager6 = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                        africaMusicAdapter = new AfricaMusicAdapter(mList6);
                        recyclerView_6.setLayoutManager(mLayoutManager6);
                        recyclerView_6.setAdapter(africaMusicAdapter);
                        africaMusicAdapter.notifyDataSetChanged();

                        africaMusicAdapter.setOnItemClickListener(new AfricaMusicAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                etSearch.setText(mList6.get(position).getTextCover());
                                loadMusic();
                            }
                        });

                    }

                    for (int i = 0; i < jsonArray7.length(); i++) {
                        JSONObject rockBands = jsonArray7.getJSONObject(i);

                        String coverUrl7 = rockBands.getString("album_cover_url");
                        String artistName7 = rockBands.getString("artist_name");

                        mList7.add(new Item(coverUrl7, artistName7));
                        Collections.shuffle(mList7);
                        RecyclerView.LayoutManager mLayoutManager7 = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                        rockBandsAdapter = new RockBandsAdapter(mList7);
                        recyclerView_7.setLayoutManager(mLayoutManager7);
                        recyclerView_7.setAdapter(rockBandsAdapter);
                        rockBandsAdapter.notifyDataSetChanged();
                        rockBandsAdapter.setOnItemClickListener(new RockBandsAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                etSearch.setText(mList7.get(position).getTextCover());
                                loadMusic();
                            }
                        });

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: NO DATA!! (Json Data)");
                error.printStackTrace();

            }
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
                    if (cacheEntry == null) {
                        cacheEntry = new Cache.Entry();
                    }
                    final long cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
                    final long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this cache entry expires completely
                    long now = System.currentTimeMillis();
                    final long softExpire = now + cacheHitButRefreshed;
                    final long ttl = now + cacheExpired;
                    cacheEntry.data = response.data;
                    cacheEntry.softTtl = softExpire;
                    cacheEntry.ttl = ttl;
                    String headerValue;
                    assert response.headers != null;
                    headerValue = response.headers.get("Date");
                    if (headerValue != null) {
                        cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    headerValue = response.headers.get("Last-Modified");
                    if (headerValue != null) {
                        cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    cacheEntry.responseHeaders = response.headers;
                    final String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    return (Response<JSONObject>) Response.success(new JSONObject(jsonString), cacheEntry);
                } catch (UnsupportedEncodingException | JSONException e) {
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            protected void deliverResponse(JSONObject response) {
                super.deliverResponse(response);
            }

            @Override
            public void deliverError(VolleyError error) {
                super.deliverError(error);
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                return super.parseNetworkError(volleyError);
            }
        };
        mRequestQueue.add(request);
    }

    public void search(String str) {
        if (!TextUtils.isEmpty(str)) {
            if (App.NETWORK.equalsIgnoreCase("AdMob")) {
                //adMobAds.showInter(null,getActivity());
                adMobAds.showInter(new AdMobAds.ActionListener() {
                    @Override
                    public void onDone() {
                        tubeEngine.search(str);
                        kw_engine.search(str);
                        vk_engine.search(str);
                    }
                });

            } else if (App.NETWORK.equalsIgnoreCase("IronSource")) {
                //ironSourceAds.showInter(null);
                ironSourceAds.showInter(new IronSourceAds.ActionListener() {
                    @Override
                    public void onDone() {
                        tubeEngine.search(str);
                        kw_engine.search(str);
                        vk_engine.search(str);
                    }
                });

            } else {
                tubeEngine.search(str);
                kw_engine.search(str);
                vk_engine.search(str);
                Log.d(TAG, "Something went wrong! ");
            }

        }
    }

    private void initView() {
        adLayout = view.findViewById(R.id.banner_layout);
        flow = view.findViewById(R.id.flow);
        tabs = view.findViewById(R.id.tabs);
        viewPager = view.findViewById(R.id.view_pager);
        etSearch = view.findViewById(R.id.et_search);
        iv_clear = view.findViewById(R.id.iv_clear);
        txtGlobalMusic = view.findViewById(R.id.txt_global_music);
        txtPopArtists = view.findViewById(R.id.txt_pop_artists);
        txtPopularArtist = view.findViewById(R.id.txt_popular_artists);
        txtTopRappers = view.findViewById(R.id.txt_top_rappers);
        txtHappyMorning = view.findViewById(R.id.txt_happy_morning);
        txtAfricaMusic = view.findViewById(R.id.txt_africa_music);
        txtRockBands = view.findViewById(R.id.txt_rock_bands);

        recyclerView_1 = view.findViewById(R.id.recyclerView_Top_Artist);
        recyclerView_2 = view.findViewById(R.id.recyclerView_Popular_Artists);
        recyclerView_3 = view.findViewById(R.id.recyclerView_Top_Pop);
        recyclerView_4 = view.findViewById(R.id.recyclerView_global_music);
        recyclerView_5 = view.findViewById(R.id.recyclerView_happy_morning);
        recyclerView_6 = view.findViewById(R.id.recyclerView_africa_music);
        recyclerView_7 = view.findViewById(R.id.recyclerView_rock_bands);
    }
}