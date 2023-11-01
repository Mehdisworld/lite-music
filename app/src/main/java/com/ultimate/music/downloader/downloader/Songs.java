package com.ultimate.music.downloader.downloader;

import java.io.Serializable;

public class Songs implements Serializable {
    private String album_id;
    private String artistname;
    private int duation;
    private String imgurl;
    private String mp3url;
    private String songid;
    private String songname;
    private String time;
    private String title;

    public String getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(String album_id) {
        this.album_id = album_id;
    }

    public String getSongname() {
        return this.songname;
    }

    public void setSongname(String str) {
        this.songname = str;
    }

    public String getArtistname() {
        return this.artistname;
    }

    public void setArtistname(String str) {
        this.artistname = str;
    }

    public String getSongid() {
        return this.songid;
    }

    public void setSongid(String str) {
        this.songid = str;
    }

    public String getImgurl() {
        return this.imgurl;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String str) {
        this.title = str;
    }

    public void setImgurl(String str) {
        this.imgurl = str;
    }

    public String getMp3url() {
        return this.mp3url;
    }

    public void setMp3url(String str) {
        this.mp3url = str;
    }

    public int getDuation() {
        return this.duation;
    }

    public void setDuation(int i) {
        this.duation = i;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String str) {
        this.time = str;
    }
}
