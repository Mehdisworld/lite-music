package com.ultimate.music.downloader.fragment


import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ultimate.music.downloader.R
import com.ultimate.music.downloader.activity.MainActivity
import com.ultimate.music.downloader.activity.WizardActivity
import com.ultimate.music.downloader.util.Constants

class SettingsFragment : Fragment() {

    var myActivity: Activity? = null
    var shakeSwitch: Switch? = null
    var aditional: TextView? = null
    var separator: View? = null

    companion object {
        val TAG = "SettingsFragment"
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        setHasOptionsMenu(true)

        activity?.title = "Settings"

        /*Linking switch to its view*/
        shakeSwitch = view?.findViewById(R.id.switchShake)
        aditional = view?.findViewById(R.id.additional)
        separator = view?.findViewById(R.id.v2)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            aditional?.visibility = View.VISIBLE
            separator?.visibility = View.VISIBLE
        }

        MainActivity.Statified.settingsOn = true


        return view
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val prefs = context?.getSharedPreferences(Constants.APP_PREFS, MODE_PRIVATE)

        var isAllowed = prefs?.getBoolean(Constants.SHAKE_TO_CHANGE, false)

        shakeSwitch?.isChecked = isAllowed as Boolean

        shakeSwitch?.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                /*If the switch is turned on we then make the feature to be true*/
                prefs!!.edit().putBoolean(Constants.SHAKE_TO_CHANGE, true).apply()
            } else {
                /*Else the feature remains false*/
                prefs!!.edit().putBoolean(Constants.SHAKE_TO_CHANGE, false).apply()

            }
        }


        aditional?.setOnClickListener {


            startActivity(Intent(context, WizardActivity::class.java))

        }

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        //removing the sorting adn searching options as they r not needed
        val item = menu.findItem(R.id.action_sort)
        item?.isVisible = false

        val item1 = menu.findItem(R.id.action_search)
        item1?.isVisible = false
    }

}