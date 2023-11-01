package com.ultimate.music.downloader.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.ultimate.music.downloader.R
import com.ultimate.music.downloader.activity.MainActivity
import com.ultimate.music.downloader.fragment.SongPlayingFragment
import com.ultimate.music.downloader.fragment.SongPlayingFragment.Statified.myActivity
import com.ultimate.music.downloader.fragment.SongPlayingFragment.Statified.wasPlaying
import com.ultimate.music.downloader.EchoNotification
import com.ultimate.music.downloader.util.MediaUtils.mediaPlayer

class CaptureBroadcast : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_NEW_OUTGOING_CALL) {
            try {
                MainActivity.Statified.notificationManager?.cancel(1998)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                if (MediaUtils.isMediaPlayerPlaying() as Boolean) {
                    wasPlaying=true
                    mediaPlayer.pause()
                    SongPlayingFragment.Statified.playpausebutton?.setBackgroundResource(R.drawable.play_icon)

                    var play = Intent(myActivity, EchoNotification::class.java)
                    play.action = Constants.ACTION.CHANGE_TO_PLAY
                    myActivity?.startService(play)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        else if(intent?.action == android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
            try {
                if (MediaUtils.isMediaPlayerPlaying() as Boolean) {
                    wasPlaying=true
                    mediaPlayer.pause()
                    SongPlayingFragment.Statified.playpausebutton?.setBackgroundResource(R.drawable.play_icon)

                    var play = Intent(myActivity, EchoNotification::class.java)
                    play.action = Constants.ACTION.CHANGE_TO_PLAY
                    myActivity?.startService(play)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        else {
            /*Here we use the telephony manager to get the access for the service*/
            val tm: TelephonyManager = context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            when (tm.callState) {
            /*We check the call state and if the call is ringing i.e. the user has an incoming call
            * then also we pause the media player*/
                TelephonyManager.CALL_STATE_RINGING -> {

                    try{
                        MainActivity.Statified.notificationManager?.cancel(1998)
                    }catch (e:Exception){
                        e.printStackTrace()
                    }

                    try {
                        if (MediaUtils.isMediaPlayerPlaying() as Boolean) {
                            wasPlaying=true
                            mediaPlayer.pause()
                            SongPlayingFragment.Statified.playpausebutton?.setBackgroundResource(R.drawable.play_icon)

                            var play = Intent(myActivity, EchoNotification::class.java)
                            play.action = Constants.ACTION.CHANGE_TO_PLAY
                            myActivity?.startService(play)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                TelephonyManager.CALL_STATE_IDLE-> {
                    // not in call
                    try {
                        if (!MediaUtils.isMediaPlayerPlaying() && !SongPlayingFragment.Statified.inform && wasPlaying) {

                            mediaPlayer.play()
                            wasPlaying=false
                            SongPlayingFragment.Statified.playpausebutton?.setBackgroundResource(R.drawable.pause_icon)

                            var play = Intent(myActivity, EchoNotification::class.java)
                            play.action = Constants.ACTION.CHANGE_TO_PAUSE
                            myActivity?.startService(play)
                        }
                        else if(SongPlayingFragment.Statified.inform){
                            SongPlayingFragment.Statified.inform=false
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                TelephonyManager.CALL_STATE_OFFHOOK-> {
                    //A call is dialing, active or on hold

                    try {
                        if (MediaUtils.isMediaPlayerPlaying()) {
                            wasPlaying=true
                            mediaPlayer.pause()
                            SongPlayingFragment.Statified.playpausebutton?.setBackgroundResource(R.drawable.play_icon)

                            var play = Intent(myActivity, EchoNotification::class.java)
                            play.action = Constants.ACTION.CHANGE_TO_PLAY
                            myActivity?.startService(play)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
                else -> {
                    /*Else we do nothing*/
                }
            }
        }
    }
}