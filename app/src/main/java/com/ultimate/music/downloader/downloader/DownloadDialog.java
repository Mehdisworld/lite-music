package com.ultimate.music.downloader.downloader;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog.Builder;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.media2.exoplayer.external.source.hls.DefaultHlsExtractorFactory;
import androidx.media2.exoplayer.external.util.MimeTypes;

import com.ultimate.music.downloader.BuildConfig;
import com.ultimate.music.downloader.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;

public class DownloadDialog extends DialogFragment {

    public CardView btnDownload;
    public File downloadFile;
    public boolean isDownload = false;
    public boolean isShow = false;
    private ImageView iv_play;
    public LinearLayout ll_play, ll_open;
    public Handler mHandler = new Handler();

    public Runnable mPublishRunnable = new Runnable() {
        public void run() {
            if (DownloadDialog.this.player.isPlaying() && DownloadDialog.this.player.getCurrentPosition() <= DownloadDialog.this.player.getDuration()) {
                DownloadDialog.this.seek_play.setProgress(DownloadDialog.this.player.getCurrentPosition());
                String str = "mm:ss";
                DownloadDialog.this.tvTime.setText(Utils.formatTime(str, (long) DownloadDialog.this.player.getCurrentPosition()));
                DownloadDialog.this.tvTimeMax.setText(Utils.formatTime(str, (long) DownloadDialog.this.player.getDuration()));
            }
            DownloadDialog.this.mHandler.postDelayed(this, 100);
        }
    };
    private Songs songs;
    public MediaPlayer player;
    public ProgressBar progress_download;
    public ProgressBar progress_loading;
    public SeekBar seek_play;
    public TextView tvTime;
    public TextView tvTimeMax;

    public static DownloadDialog newInstance(Songs songs2) {
        DownloadDialog downloadDialog = new DownloadDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable("musicapp", songs2);
        downloadDialog.setArguments(bundle);
        return downloadDialog;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.dialog_download, viewGroup, false);


        initView(inflate);
        return inflate;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.songs = (Songs) getArguments().getSerializable("musicapp");
        this.isShow = true;
    }

    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            Window window = dialog.getWindow();
            double d = (double) displayMetrics.widthPixels;
            Double.isNaN(d);
            window.setLayout((int) (d * 0.9d), -2);
        }
    }

    public Dialog onCreateDialog(Bundle bundle) {
        Dialog onCreateDialog = super.onCreateDialog(bundle);
        onCreateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        onCreateDialog.setCanceledOnTouchOutside(false);
        onCreateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        onCreateDialog.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                return i == 4;
            }
        });
        return onCreateDialog;
    }

    public void onCancel(DialogInterface dialogInterface) {
        super.onCancel(dialogInterface);
    }

    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
    }

    @SuppressLint("CutPasteId")
    private void initView(View view) {
        ImageView btnClose = view.findViewById(R.id.iv_close);
        btnClose.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                DownloadDialog.this.mHandler.removeCallbacks(DownloadDialog.this.mPublishRunnable);
                DownloadDialog.this.isShow = false;
                if (DownloadDialog.this.isDownload) {
                    Toast.makeText(DownloadDialog.this.getActivity(), "Downloading in the background...", Toast.LENGTH_LONG).show();
                }
                if (player != null) {
                    player.pause();
                    player = null;
                }
                DownloadDialog.this.dismiss();
            }
        });
        this.iv_play = view.findViewById(R.id.iv_play);
        this.iv_play.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (DownloadDialog.this.player.isPlaying()) {
                    DownloadDialog.this.player.pause();
                    DownloadDialog.this.iv_play.setImageResource(R.drawable.play_btn);
                    return;
                }
                DownloadDialog.this.player.start();
                DownloadDialog.this.iv_play.setImageResource(R.drawable.pause_btn);
            }
        });
        this.progress_download = view.findViewById(R.id.progress_download);
        this.seek_play = view.findViewById(R.id.seek_play);
        this.seek_play.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                DownloadDialog.this.player.seekTo(seekBar.getProgress());
            }
        });
        this.ll_play = view.findViewById(R.id.ll_play);
        this.progress_loading = view.findViewById(R.id.progress_loading);
        this.tvTime = view.findViewById(R.id.tv_time);
        this.tvTimeMax = view.findViewById(R.id.tv_time_max);
        ll_open = view.findViewById(R.id.ll_open);
        ll_open.setOnClickListener(new OnClickListener() {
            @SuppressLint({"WrongConstant", "RestrictedApi"})
            public void onClick(View view) {
                if (DownloadDialog.this.downloadFile != null) {
                    if (!DownloadDialog.this.downloadFile.exists()) {
                        Toast.makeText(DownloadDialog.this.getActivity(), "Mp3 files don't exist", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Uri uriForFile = FileProvider.getUriForFile(DownloadDialog.this.requireActivity(),
                            BuildConfig.APPLICATION_ID + ".provider", DownloadDialog.this.downloadFile);
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    intent.setDataAndType(uriForFile, MimeTypes.AUDIO_MPEG);
                    intent.addFlags(1);
                    intent.addFlags(128);
                    if (intent.resolveActivity(DownloadDialog.this.requireActivity().getPackageManager()) != null) {
                        DownloadDialog.this.startActivity(intent);
                        if (DownloadDialog.this.player.isPlaying()) {
                            DownloadDialog.this.player.pause();
                            DownloadDialog.this.iv_play.setImageResource(R.drawable.play_btn);
                        }
                    } else {
                        Toast.makeText(DownloadDialog.this.getActivity(), "No app installed to play this file", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

        LibraryFragment libraryFragment = new LibraryFragment();
        this.player = libraryFragment.getMediaPlayer();
        this.player.setOnPreparedListener(new OnPreparedListener() {
            public void onPrepared(MediaPlayer mediaPlayer) {
                DownloadDialog.this.ll_play.setVisibility(View.VISIBLE);
                DownloadDialog.this.progress_loading.setVisibility(View.GONE);
                DownloadDialog.this.iv_play.setImageResource(R.drawable.pause_btn);
                String str = "mm:ss";
                DownloadDialog.this.tvTime.setText(Utils.formatTime(str, (long) mediaPlayer.getCurrentPosition()));
                DownloadDialog.this.tvTimeMax.setText(Utils.formatTime(str, (long) mediaPlayer.getDuration()));
                mediaPlayer.start();
                DownloadDialog.this.seek_play.setProgress(0);
                DownloadDialog.this.seek_play.setMax(mediaPlayer.getDuration());
            }
        });
        this.player.setOnCompletionListener(new OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });
        this.player.setOnErrorListener(new OnErrorListener() {
            public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                return false;
            }
        });
        TextView songName = (TextView) view.findViewById(R.id.tv_songname);
        TextView artistName = (TextView) view.findViewById(R.id.tv_artist);
        Songs songs2 = this.songs;
        if (songs2 != null && !TextUtils.isEmpty(songs2.getMp3url())) {
            songName.setText(this.songs.getSongname());
            artistName.setText(this.songs.getArtistname());
            this.player.reset();
            try {
                this.player.setDataSource(this.songs.getMp3url());
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.player.prepareAsync();
        }
        this.btnDownload = view.findViewById(R.id.bt_download);
        List<Animator> animList = new ArrayList<>();
        ObjectAnimator anim = ObjectAnimator.ofFloat(btnDownload, "scaleX", 0.90f);
        anim.setRepeatCount(ObjectAnimator.INFINITE);
        anim.setRepeatMode(ObjectAnimator.REVERSE);
        animList.add(anim);

        anim = ObjectAnimator.ofFloat(view.findViewById(R.id.bt_download), "scaleY", 0.90f);
        anim.setRepeatCount(ObjectAnimator.INFINITE);
        anim.setRepeatMode(ObjectAnimator.REVERSE);
        animList.add(anim);

        AnimatorSet animButton = new AnimatorSet();
        animButton.playTogether(animList);
        animButton.setDuration(600);
        animButton.start();
        this.btnDownload.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {

                DownloadDialog.this.startDownload();
            }
        });
        this.mHandler.post(this.mPublishRunnable);
    }


    public void startDownload() {
        String str;
        this.btnDownload.setVisibility(View.GONE);
        this.progress_download.setVisibility(View.VISIBLE);
        this.isDownload = true;
        if (TextUtils.isEmpty(this.songs.getArtistname())) {
            str = this.songs.getSongname();
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(this.songs.getSongname());
            sb.append(" - ");
            sb.append(this.songs.getArtistname());
            str = sb.toString();
        }
        @SuppressLint("RestrictedApi") String str2 = DefaultHlsExtractorFactory.MP3_FILE_EXTENSION;
        if (!str.endsWith(str2) && !str.endsWith(".m4a") && !str.endsWith(".flac")) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str);
            sb2.append(str2);
            str = sb2.toString();
        }
        OkHttpUtils.get().url(this.songs.getMp3url()).addHeader("User-Agent",
                WebSettings.getDefaultUserAgent(getActivity())).build().execute(new FileCallBack(Utils.getSongDir(), str) {
            public void onAfter(int i) {
            }

            public void inProgress(float f, long j, int i) {
                DownloadDialog.this.progress_download.setProgress((int) (f * 100.0f));
            }

            public void onResponse(File file, int i) {
                if (DownloadDialog.this.isShow) {
                    DownloadDialog.this.isDownload = false;
                    Toast.makeText(DownloadDialog.this.getActivity(), "The download is complete", Toast.LENGTH_LONG).show();
                    if (file != null && file.exists()) {
                        DownloadDialog.this.downloadFile = file;
                        Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                        intent.setData(Uri.fromFile(file));
                        DownloadDialog.this.getActivity().sendBroadcast(intent);
                    }
                    DownloadDialog.this.ll_open.setVisibility(View.VISIBLE);
                    DownloadDialog.this.progress_download.setVisibility(View.GONE);
                    if (new Random().nextInt(10) == 1 && Utils.getScoreStatus(DownloadDialog.this.getActivity()) == 0) {
                        new Builder(DownloadDialog.this.getActivity()).setMessage((CharSequence) "Give us a five-star review on Google play?").setPositiveButton((CharSequence) "Yes,I do", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Utils.launchAppDetail(DownloadDialog.this.getActivity(), DownloadDialog.this.getActivity().getPackageName());
                                Utils.setScoreStatus(DownloadDialog.this.getActivity());
                            }
                        }).setNegativeButton("No", (DialogInterface.OnClickListener) null).create().show();
                    }
                }
            }

            public void onError(Call call, Exception exc, int i) {
                if (DownloadDialog.this.isShow) {
                    DownloadDialog.this.isDownload = false;
                    Toast.makeText(DownloadDialog.this.getActivity(), "Download failed", Toast.LENGTH_LONG).show();
                    Toast.makeText(DownloadDialog.this.getActivity(), exc.getMessage(), Toast.LENGTH_LONG).show();
                    DownloadDialog.this.progress_download.setVisibility(View.GONE);
                    DownloadDialog.this.btnDownload.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
