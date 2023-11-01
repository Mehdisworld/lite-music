package com.ultimate.music.downloader.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Html
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.ultimate.music.downloader.R
import com.ultimate.music.downloader.model.Songs
import com.ultimate.music.downloader.activity.MainActivity
import com.ultimate.music.downloader.adapter.MainScreenAdapter
import com.ultimate.music.downloader.databinding.FragmentMainScreenBinding
import com.ultimate.music.downloader.util.BottomBarUtils
import com.ultimate.music.downloader.util.MediaUtils
import com.ultimate.music.downloader.viewModel.SongsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.math.max


@AndroidEntryPoint
class MainScreenFragment : Fragment() {

    companion object {
        val TAG = "MainScreenFragment"
        var noNext: Boolean = true
    }

    private val viewModel: SongsViewModel by viewModels()
    var args: Bundle? = null
    var main: MainActivity? = null
    var getSongsList: List<Songs>? = null

    var myActivity: Activity? = null
    var trackPosition: Int = 0
    var mainScreenAdapter: MainScreenAdapter? = null

    private lateinit var binding: FragmentMainScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getAllSongs()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        binding = FragmentMainScreenBinding.inflate(layoutInflater)
        main = MainActivity()
        activity?.title = "All Songs"
        binding.nowPlayingBottomBarMain.songTitle.isSelected = true
        binding.nowPlayingBottomBarMain.songArtist.isSelected = true
        
        MainActivity.Statified.MainorFavOn = true
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav).visibility = View.VISIBLE

        if(!viewModel.songsList.value.isNullOrEmpty()){
            binding.loading.visibility = View.GONE
            getSongsList = viewModel.songsList.value
            setView()
        }
        viewModel.songsList.observe(viewLifecycleOwner, {
            binding.loading.visibility = View.GONE
            getSongsList = viewModel.songsList.value
            setView()
        })

        viewModel.isSongPlaying.observeForever {
            if (it)
                binding.nowPlayingBottomBarMain.playPause.setImageDrawable(requireContext().resources.getDrawable(R.drawable.pause_icon))
            else
                binding.nowPlayingBottomBarMain.playPause.setImageDrawable(requireContext().resources.getDrawable(R.drawable.play_icon))
        }

        binding.help.text = (Html.fromHtml("<u>Need Help?</u>"))
        binding.help.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.details_fragment, HelpFragment(), HelpFragment.TAG)
                    .addToBackStack(HelpFragment.TAG)
                    .commit()
        }
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        myActivity = activity
    }

    /* It is used to do the final initialization once the other things are in place*/
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        /*The variable getSongsList() is used to get store the arrayList returned by the function getSongsFromPhone()*/

        BottomBarUtils.bottomBarSetup(myActivity!!,main!!,requireFragmentManager(),binding.nowPlayingBottomBarMain)

    }

    fun setView() {
        val prefs = activity?.getSharedPreferences(getString(R.string.sorting), Context.MODE_PRIVATE)
        val action_sort_ascending = prefs?.getString(getString(R.string.sort_by_name), "false")
        val action_sort_recent = prefs?.getString(getString(R.string.sort_by_recent), "true")

        if (getSongsList == null || getSongsList?.size == 0) {
            binding.noSongs.visibility = View.VISIBLE
        } else {
            binding.visibleLayout.visibility = View.VISIBLE
            binding.noSongs.visibility = View.GONE
            mainScreenAdapter = MainScreenAdapter(getSongsList as ArrayList<Songs>, myActivity as Context)
            val mLayoutManager = LinearLayoutManager(myActivity)
            binding.recyclerView.layoutManager = mLayoutManager
            binding.recyclerView.itemAnimator = DefaultItemAnimator()
            binding.recyclerView.setHasFixedSize(true)
            binding.recyclerView.setItemViewCacheSize(100)
            binding.recyclerView.isDrawingCacheEnabled = true
            binding.recyclerView.isAlwaysDrawnWithCacheEnabled = true
            binding.recyclerView.adapter = mainScreenAdapter
            try {
                binding.recyclerView.scrollToPosition(max(0, MediaUtils.getSongIndex() - 2))
            }
            catch (e:java.lang.Exception){}
        }

        if (getSongsList != null) {
            if (action_sort_ascending!!.equals("true", ignoreCase = true)) {
                Collections.sort(getSongsList, Songs.Statified.nameComparator)
                mainScreenAdapter?.notifyDataSetChanged()
            } else if (action_sort_recent!!.equals("true", ignoreCase = true)) {
                Collections.sort(getSongsList, Songs.Statified.dateComparator)
                mainScreenAdapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.main, menu)

        val searchItem = menu.findItem(R.id.action_search)
        var searchView = searchItem?.actionView as SearchView
        searchView.queryHint = "Search Song, Artist, Album"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(query: String): Boolean {

                var name_to_saerch = query.lowercase(Locale.getDefault())

                var newList: ArrayList<Songs>? = ArrayList<Songs>()

                for (songs in getSongsList!!) {
                    var name = songs.songTitle.lowercase(Locale.getDefault())
                    var artist = songs.artist.lowercase(Locale.getDefault())
                    var album = songs.album.toString()
                    if (name.contains(name_to_saerch, true))
                        newList?.add(songs)
                    else if (artist.contains(name_to_saerch, true))
                        newList?.add(songs)
                    else if (album.contains(name_to_saerch, true))
                        newList?.add(songs)

                }
                mainScreenAdapter?.filter_data(newList)
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {

                return false
            }


        })

        return
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val switcher = item.itemId
        if (switcher == R.id.acton_sort_ascending) {
            val editor = myActivity?.getSharedPreferences(getString(R.string.sorting), Context.MODE_PRIVATE)?.edit()
            editor?.putString(getString(R.string.sort_by_name), "true")
            editor?.putString(getString(R.string.sort_by_recent), "false")
            editor?.apply()
            if (getSongsList != null) {
                Collections.sort(getSongsList, Songs.Statified.nameComparator)
            }
            mainScreenAdapter?.notifyDataSetChanged()
            return false
        } else if (switcher == R.id.action_sort_recent) {
            val editor = myActivity?.getSharedPreferences(getString(R.string.sorting), Context.MODE_PRIVATE)?.edit()
            editor?.putString(getString(R.string.sort_by_recent), "true")
            editor?.putString(getString(R.string.sort_by_name), "false")
            editor?.apply()
            if (getSongsList != null) {
                Collections.sort(getSongsList, Songs.Statified.dateComparator)
            }
            mainScreenAdapter?.notifyDataSetChanged()
            return false
        }
        return super.onOptionsItemSelected(item)
    }


}