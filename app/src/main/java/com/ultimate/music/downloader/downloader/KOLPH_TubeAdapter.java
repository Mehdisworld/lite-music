package com.ultimate.music.downloader.downloader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ultimate.music.downloader.R;

import java.util.ArrayList;
import java.util.List;


public class KOLPH_TubeAdapter extends BaseAdapter {
    private Context context;
    private List<Songs> songsList = new ArrayList();
    private int pageNo = 0;

    public long getItemId(int i) {
        return (long) i;
    }

    public KOLPH_TubeAdapter(Context context2) {
        this.context = context2;
    }

    public void clear() {
        this.pageNo = 0;
        this.songsList.clear();
        notifyDataSetChanged();
    }

    public void addPage(List<Songs> list) {
        this.songsList.addAll(list);
        this.pageNo++;
        notifyDataSetChanged();
    }

    public int getPageNo() {
        return this.pageNo;
    }

    public int getCount() {
        return this.songsList.size();
    }

    public Songs getItem(int i) {
        return this.songsList.get(i);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(this.context).inflate(R.layout.adapter_music, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.tvSongname_ijkuy = view.findViewById(R.id.tv_songname);
            viewHolder.tvArtist_ijuhbn = view.findViewById(R.id.tv_artist);
            viewHolder.tvTime_okloj = view.findViewById(R.id.tv_time);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.tvSongname_ijkuy.setText(this.songsList.get(i).getSongname());
        viewHolder.tvArtist_ijuhbn.setText(this.songsList.get(i).getArtistname());
        viewHolder.tvTime_okloj.setText(this.songsList.get(i).getTime());
        return view;
    }

    class ViewHolder {
        private TextView tvArtist_ijuhbn;
        private TextView tvSongname_ijkuy;
        private TextView tvTime_okloj;

        ViewHolder() {
        }
    }
}
