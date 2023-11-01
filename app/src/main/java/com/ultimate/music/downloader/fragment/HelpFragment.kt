package com.ultimate.music.downloader.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ultimate.music.downloader.R
import com.ultimate.music.downloader.activity.MainActivity

class HelpFragment : Fragment() {

    companion object{
        val TAG = "Help Fragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_help, container, false)

        setHasOptionsMenu(true)

        activity?.title = "Help"
        MainActivity.Statified.settingsOn=true


        return  v
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        val item = menu.findItem(R.id.action_sort)
        item?.isVisible=false

        val item1 = menu.findItem(R.id.action_search)
        item1?.isVisible=false
    }

}