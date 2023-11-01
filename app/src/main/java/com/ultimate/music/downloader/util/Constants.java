package com.ultimate.music.downloader.util;

public class Constants {
    public static final String SETTINGS_APP_SETTINGS = "ECHO_A_LITE_MUSIC_PLAYER";
    public static final String WAS_MEDIA_PLAYING = "WAS_MEDIA_PLAYING";

    public interface ACTION {
        String MAIN_ACTION = "com.ultimate.music.downloader.action.main";
        String INIT_ACTION = "com.ultimate.music.downloader.action.init";
        String PREV_ACTION = "com.ultimate.music.downloader.action.prev";
        String PLAY_ACTION = "com.ultimate.music.downloader.action.play";
        String PAUSE_ACTION = "com.ultimate.music.downloader.action.pause";
        String NEXT_ACTION = "com.ultimate.music.downloader.action.next";
        String SHUFFLE_ACTION = "com.ultimate.music.downloader.action.shuffle";
        String STARTFOREGROUND_ACTION = "com.ultimate.music.downloader.action.startforeground";
        String STOPFOREGROUND_ACTION = "com.ultimate.music.downloader.action.stopforeground";
        String CHANGE_TO_PAUSE = "com.ultimate.music.downloader.action.changetopause";
        String CHANGE_TO_PLAY = "com.ultimate.music.downloader.action.changetoplay";
        String NEXT_UPDATE = "com.ultimate.music.downloader.action.nextupdate";
        String NEXT_UPDATE_SHUFFLE = "com.ultimate.music.downloader.action.nextupdateshuffle";
        String PREV_UPDATE = "com.ultimate.music.downloader.action.prevupdate";
        String PREV_UPDATE_SHUFFLE = "com.ultimate.music.downloader.action.prevupdateshuffle";
        String CLOSE = "com.ultimate.music.downloader.action.close";
    }

    public static final String APP_PREFS = "APP_PREFS";
    public static final String SHUFFLE = "SHUFFLE";
    public static final String LOOP = "LOOP";
    public static final String SHAKE_TO_CHANGE = "SHAKE_TO_CHANGE";

    public interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 101;
    }

}