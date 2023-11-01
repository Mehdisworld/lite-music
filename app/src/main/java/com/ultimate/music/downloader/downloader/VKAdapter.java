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

public class VKAdapter extends BaseAdapter {
    private Context context;
    private List<Songs> songsList = new ArrayList();
    private int pageNo = 1;

    class ViewHolder {

        public TextView tvArtist;
        public TextView tvSongname;
        public TextView tvTime;

        ViewHolder() {
        }
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public VKAdapter(Context context2) {
        this.context = context2;
    }

    public void clear() {
        this.pageNo = 1;
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
        return (Songs) this.songsList.get(i);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(this.context).inflate(R.layout.adapter_music, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.tvSongname = (TextView) view.findViewById(R.id.tv_songname);
            viewHolder.tvArtist = (TextView) view.findViewById(R.id.tv_artist);
            viewHolder.tvTime = (TextView) view.findViewById(R.id.tv_time);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.tvSongname.setText(((Songs) this.songsList.get(i)).getSongname());
        viewHolder.tvArtist.setText(((Songs) this.songsList.get(i)).getArtistname());
        viewHolder.tvTime.setText(((Songs) this.songsList.get(i)).getTime());
        return view;
    }
}
