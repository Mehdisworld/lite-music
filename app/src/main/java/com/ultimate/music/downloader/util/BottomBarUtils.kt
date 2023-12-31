package com.ultimate.music.downloader.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.annotation.Keep
import androidx.fragment.app.FragmentManager
import com.ultimate.music.downloader.App
import com.ultimate.music.downloader.EchoNotification
import com.ultimate.music.downloader.R
import com.ultimate.music.downloader.activity.MainActivity
import com.ultimate.music.downloader.activity.SongPlayingActivity
import com.ultimate.music.downloader.adapter.MainScreenAdapter
import com.ultimate.music.downloader.databinding.BottomBarBinding
import com.ultimate.music.downloader.fragment.SongPlayingFragment
import com.ultimate.music.downloader.util.MediaUtils.mediaPlayer
import com.ultimate.music.downloader.util.SongHelper.currentSongHelper
import com.bumptech.glide.Glide
import java.lang.Exception

@Keep
object BottomBarUtils {
    var bottomBarBinding: BottomBarBinding  ?= null


    fun bottomBarSetup(activity: Activity, main: MainActivity, fragmentManager: FragmentManager, bottomBarBinding: BottomBarBinding) {
        bottomBarClickHandler(activity, main, fragmentManager, bottomBarBinding)
        this.bottomBarBinding = bottomBarBinding
        if (!MediaUtils.isMediaPlayerPlaying() && !isMyServiceRunning(
                EchoNotification::class.java,
                App.context
            )
        ) {
            bottomBarBinding.bottomBar.visibility = View.GONE
            return
        }
        bottomBarBinding.bottomBar.visibility = View.VISIBLE
        if (MediaUtils.isMediaPlayerPlaying()) {
            bottomBarBinding.playPause.setImageDrawable(App.context.resources.getDrawable(R.drawable.pause_icon))
        } else {
            bottomBarBinding.playPause.setImageDrawable(App.context.resources.getDrawable(R.drawable.play_icon))
        }
        bottomBarBinding.songTitle.text = currentSongHelper.songTitle
        var artist = currentSongHelper.songArtist
        if (artist.equals("<unknown>", ignoreCase = true))
            bottomBarBinding.songArtist.visibility = View.GONE
        else
            bottomBarBinding.songArtist.text = artist
        setAlbumArt(currentSongHelper.songAlbum)

    }

    private fun bottomBarClickHandler(
        myActivity: Activity,
        main: MainActivity,
        fragmentManager: FragmentManager,
        bottomBarBinding: BottomBarBinding
    ) {

        bottomBarBinding.bottomBar.setOnClickListener {
            var intent = Intent(App.context,SongPlayingActivity::class.java)
            intent.putExtra("songArtist", currentSongHelper.songArtist)
            intent.putExtra("songTitle", currentSongHelper.songTitle)
            intent.putExtra("path", currentSongHelper.songpath)
            intent.putExtra("SongID", currentSongHelper.songId!!)
            intent.putExtra("songAlbum", currentSongHelper.songAlbum!!)
            intent.putExtra("songPosition", currentSongHelper.currentPosition?.toInt() as Int)
            intent.putExtra("fromBottomBar",true)
            MediaUtils.songsList = SongPlayingFragment.Statified.fetchSongs?:ArrayList()
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            App.context.startActivity(intent)
        }

        bottomBarBinding.playPause.setOnClickListener {

            if (MediaUtils.isMediaPlayerPlaying()) {

                mediaPlayer.pause()
                bottomBarBinding.playPause.setImageDrawable(myActivity.resources.getDrawable(R.drawable.play_icon))
                var play = Intent(App.context, EchoNotification::class.java)
                play.action = Constants.ACTION.CHANGE_TO_PLAY
                myActivity.startService(play)
                SongPlayingFragment.Staticated.updateButton("pause")
            } else {
                MainScreenAdapter.Statified.stopPlayingCalled = true
                if (!main.getnotify_val()) {
                    var trackPosition =
                        MediaUtils.getCurrentPosition()
                    mediaPlayer.seekTo(trackPosition.toLong())
                    mediaPlayer.play()

                    bottomBarBinding.playPause.setImageDrawable(App.context.resources.getDrawable(R.drawable.pause_icon))
                    var serviceIntent = Intent(myActivity, EchoNotification::class.java)

                    serviceIntent.putExtra("title", bottomBarBinding.songTitle.text.toString())
                    serviceIntent.putExtra("artist", bottomBarBinding.songArtist.text.toString())
                    serviceIntent.action = Constants.ACTION.STARTFOREGROUND_ACTION


                    myActivity.startService(serviceIntent)

                    var play = Intent(myActivity, EchoNotification::class.java)
                    play.action = Constants.ACTION.CHANGE_TO_PAUSE
                    myActivity.startService(play)
                    SongPlayingFragment.Staticated.updateButton("play")

                } else if (main.getnotify_val()) {


                    /*If the music was already paused and we then click on the button
                * it plays the song from the same position where it was paused
                * and change the button to pause button*/
                    var trackPosition =
                        MediaUtils.getCurrentPosition()  // current postiton where the player as stopped
                    mediaPlayer.seekTo(trackPosition.toLong())
                    mediaPlayer.play()
                    bottomBarBinding.playPause.setImageDrawable(myActivity.resources.getDrawable(R.drawable.pause_icon))

                    var play = Intent(myActivity, EchoNotification::class.java)
                    play.action = Constants.ACTION.CHANGE_TO_PAUSE
                    myActivity.startService(play)
                    SongPlayingFragment.Staticated.updateButton("play")
                }
            }
        }
        val shuffle = myActivity.getSharedPreferences(Constants.APP_PREFS, Context.MODE_PRIVATE)

        val isshuffled = shuffle!!.getBoolean(Constants.SHUFFLE, false)

        bottomBarBinding.next.setOnClickListener {
            SongPlayingFragment.playNext(isshuffled)
            bottomBarBinding.playPause.setImageDrawable(myActivity.resources.getDrawable(R.drawable.pause_icon))
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun setAlbumArt(songAlbum: Long?) {
        var albumId = songAlbum
        if (albumId == null || albumId <= 0L) {
            bottomBarBinding?.songImg?.setImageDrawable(
                App.context.resources?.getDrawable(
                    R.drawable.echo_icon
                )
            )
            return
        }
        try {
            val sArtworkUri: Uri = Uri
                .parse("content://media/external/audio/albumart")
            val uri: Uri = ContentUris.withAppendedId(sArtworkUri, albumId)
            Glide.with(App.context).load(uri).placeholder(R.drawable.echo_icon)
                .into(bottomBarBinding?.songImg!!)
        } catch (e: Exception) {
        }
    }


    private fun isMyServiceRunning(serviceClass: Class<*>, context: Context): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    fun setTitle() {
        if (null != currentSongHelper.songTitle && null != currentSongHelper)
            bottomBarBinding?.songTitle?.text = currentSongHelper?.songTitle
    }

    fun setArtist() {
        if (null != currentSongHelper.songArtist && null != currentSongHelper) {
            var artist = currentSongHelper.songArtist
            if (artist.equals("<unknown>", ignoreCase = true))
                bottomBarBinding?.songArtist?.visibility = View.GONE
            else {
                bottomBarBinding?.songArtist?.visibility = View.VISIBLE
                bottomBarBinding?.songArtist?.text = artist
            }
        }
    }

    fun setAlbumArt() {
        if (null != bottomBarBinding?.songImg && null != currentSongHelper) {
            val sArtworkUri: Uri = Uri
                .parse("content://media/external/audio/albumart")
            val uri: Uri = ContentUris.withAppendedId(sArtworkUri, currentSongHelper.songAlbum!!)
            if (currentSongHelper.songAlbum!! < 0 || null == uri || uri.toString().isEmpty())
                bottomBarBinding?.songImg?.setImageResource(R.drawable.echo_icon)
            else
                bottomBarBinding?.songImg?.setImageURI(uri)

            if (null == bottomBarBinding?.songImg?.drawable) {
                bottomBarBinding?.songImg?.setImageResource(R.drawable.echo_icon)
            }
        }
    }

    fun updatePlayPause(){
        if(MediaUtils.isMediaPlayerPlaying()){
            bottomBarBinding?.playPause?.setImageDrawable(App.context.resources.getDrawable(R.drawable.pause_icon))
        }
        else{
            bottomBarBinding?.playPause?.setImageDrawable(App.context.resources.getDrawable(R.drawable.play_icon))
        }
    }
}