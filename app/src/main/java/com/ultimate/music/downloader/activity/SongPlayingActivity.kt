package com.ultimate.music.downloader.activity

import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ultimate.music.downloader.R
import com.ultimate.music.downloader.databinding.ActivitySongPlayingBinding
import com.ultimate.music.downloader.fragment.SongPlayingFragment
import com.ultimate.music.downloader.util.BottomBarUtils

class SongPlayingActivity : AppCompatActivity() {
    lateinit var binding : ActivitySongPlayingBinding
    companion object{
        var instance: SongPlayingActivity?=null
        var songPlayingFragment = SongPlayingFragment()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this@SongPlayingActivity
        binding = ActivitySongPlayingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_queue_music_white_24dp)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimary)))
        val args = intent.extras
        songPlayingFragment.arguments = args
        supportFragmentManager.beginTransaction().replace(R.id.container, songPlayingFragment)
            .commit()
    }

    override fun onBackPressed() {
        instance = null
        BottomBarUtils.bottomBarBinding?.root?.visibility = View.VISIBLE
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }
}