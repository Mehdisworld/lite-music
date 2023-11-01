package com.ultimate.music.downloader.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.ultimate.music.downloader.R
import com.ultimate.music.downloader.model.Songs
import com.ultimate.music.downloader.activity.MainActivity
import com.ultimate.music.downloader.adapter.MainScreenAdapter
import com.ultimate.music.downloader.databinding.FragmentAlbumTracksBinding
import com.ultimate.music.downloader.util.BottomBarUtils
import com.ultimate.music.downloader.util.SongHelper.currentSongHelper
import com.ultimate.music.downloader.viewModel.SongsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AlbumTracksFragment(id: Long?, name: String) : Fragment() {


    companion object {
        val TAG = "AlbumTracksFragment"
    }

    var albumId: Long = id!!
    val viewModel: SongsViewModel by viewModels()
    var binding: FragmentAlbumTracksBinding? = null
    var main: MainActivity? = null
    var songAlbum: Long? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel.getAlbumSongs(albumId)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentAlbumTracksBinding.inflate(layoutInflater)
        main = MainActivity()
        try {
            activity?.actionBar?.title = currentSongHelper.album
            activity?.title = currentSongHelper.album
        } catch (e: java.lang.Exception) {

        }
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav).visibility = View.VISIBLE

        viewModel.albumSongsList.observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty())
                setView(it as ArrayList<Songs>)
        })
        binding!!.nowPlayingBottomBarMain.songArtist.isSelected = true
        binding!!.nowPlayingBottomBarMain.songTitle.isSelected = true
        return binding!!.root
    }

    /* It is used to do the final initialization once the other things are in place*/
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        /*The variable getSongsList() is used to get store the arrayList returned by the function getSongsFromPhone()*/

        BottomBarUtils.bottomBarSetup(requireActivity(),main!!,requireFragmentManager(),
        binding!!.nowPlayingBottomBarMain)
    }


    private fun setView(list: ArrayList<Songs>) {
        binding!!.tracks.layoutManager = LinearLayoutManager(context)
        binding!!.tracks.itemAnimator = DefaultItemAnimator()
        binding!!.tracks.setHasFixedSize(true)
        binding!!.tracks.setItemViewCacheSize(100)
        binding!!.tracks.isDrawingCacheEnabled = true
        binding!!.tracks.isAlwaysDrawnWithCacheEnabled = true
        binding!!.tracks.adapter = MainScreenAdapter(list, activity as Context)
    }

}