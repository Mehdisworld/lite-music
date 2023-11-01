package com.ultimate.music.downloader.autoComplete;

public class MusicItem {
    private String artistName;
    private String imageUrl;

    public MusicItem(String artistName, String imageUrl) {
        this.artistName = artistName;
        this.imageUrl = imageUrl;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}