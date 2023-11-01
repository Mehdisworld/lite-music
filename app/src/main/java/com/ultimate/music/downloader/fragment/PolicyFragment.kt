package com.ultimate.music.downloader.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.ultimate.music.downloader.R
import com.ultimate.music.downloader.activity.MainActivity


class PolicyFragment : Fragment() {

    companion object{
        const val TAG = "Policy Fragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_policy, container, false)

        setHasOptionsMenu(true)

        activity?.title = "Privacy Policy"
        MainActivity.Statified.settingsOn=true


        val web: WebView = view.findViewById(R.id.webView)
        web.loadUrl("file:///android_asset/policy.html")

        return  view
    }

}
