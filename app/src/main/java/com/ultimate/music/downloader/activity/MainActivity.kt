package com.ultimate.music.downloader.activity


import android.app.Dialog
import android.app.NotificationManager
import android.content.*
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ultimate.music.downloader.App
import com.ultimate.music.downloader.R
import com.ultimate.music.downloader.activity.MainActivity.Statified.notify
import com.ultimate.music.downloader.adapter.NavigationDrawerAdapter
import com.ultimate.music.downloader.downloader.LibraryFragment
import com.ultimate.music.downloader.fragment.AlbumTracksFragment.Companion.TAG
import com.ultimate.music.downloader.fragment.FavoriteFragment
import com.ultimate.music.downloader.fragment.MainScreenFragment
import com.ultimate.music.downloader.fragment.OfflineAlbumsFragment
import com.ultimate.music.downloader.fragment.SongPlayingFragment
import com.ultimate.music.downloader.fragment.SongPlayingFragment.Staticated.mSensorListener
import com.ultimate.music.downloader.util.Constants
import com.ultimate.music.downloader.util.MediaUtils.mediaPlayer
import com.ultimate.music.downloader.viewModel.SongsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    val viewModel: SongsViewModel by viewModels()
    var song: SongPlayingFragment? = null
    var bottomNav: BottomNavigationView? = null

    var mLocalBroadcastManager: LocalBroadcastManager? = null
    var mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {


        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Constants.ACTION.CLOSE) {
                try {
                    if (mediaPlayer != null) {
                        mediaPlayer.stop()
                        mediaPlayer.release()
                    }
                } catch (e: Exception) {
                }
                SongPlayingFragment.Staticated.mSensorManager?.unregisterListener(mSensorListener)
                song!!.unregister()
                finishAffinity()
            }
        }
    }




    var navigationDrawerIconsList: ArrayList<String> = arrayListOf()

    var images_for_navdrawer = arrayOf(R.drawable.navigation_allsongs, R.drawable.navigation_settings,R.drawable.navigation_policy, R.drawable.ic_baseline_help_24, R.drawable.baseline_share_white_36dp, R.drawable.baseline_star_rate_white_36dp,R.drawable.ic_apps, R.drawable.baseline_feedback_white_36dp, R.drawable.baseline_album_white_24dp)

    object Statified {
        var drawerLayout: DrawerLayout? = null
        var notify = false
        var notificationManager: NotificationManager? = null
        var settingsOn: Boolean = false
        var policy: Boolean = false
        var MainorFavOn: Boolean = false

    }



    fun setNotify_val(bool: Boolean) {
        notify = bool

    }

    fun getnotify_val(): Boolean {
        return notify
    }

    /*Added By Youssef*/
    private fun requiredUpdate() {
        val dialog = Dialog(this@MainActivity)
        dialog.setContentView(R.layout.update_dialog)
        dialog.setCancelable(false)
        val btn_update: Button = dialog.findViewById(R.id.btn_update)
      btn_update.setOnClickListener(View.OnClickListener {
          try {
              startActivity(
                  Intent(
                      Intent.ACTION_VIEW,
                      Uri.parse("market://details?id=" + App.NEW_PACKAGE_NAME)
                  )
              )
          } catch (exception: ActivityNotFoundException) {
              startActivity(
                  Intent(
                      Intent.ACTION_VIEW,
                      Uri.parse("https://play.google.com/store/apps/details?id=" + App.NEW_PACKAGE_NAME)
                  )
              )
          }
      })
        dialog.show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        /*Added By Youssef*/
        if (App.is_Update) {
            requiredUpdate();
        } else {
            Log.d(TAG, "onCreate: no update!");
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        song = SongPlayingFragment()

        viewModel.init()

        Statified.drawerLayout = findViewById(R.id.drawer_layout)

        navigationDrawerIconsList.add("Library")
        navigationDrawerIconsList.add("Settings")
        navigationDrawerIconsList.add("Policy")
        navigationDrawerIconsList.add("Help")
        navigationDrawerIconsList.add("Share")
        navigationDrawerIconsList.add("Rate Us")
        navigationDrawerIconsList.add("More Apps")
        navigationDrawerIconsList.add("Report & Feedback")


        val toggle = ActionBarDrawerToggle(this@MainActivity, Statified.drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        Statified.drawerLayout?.addDrawerListener(toggle)
        toggle.syncState()

        val _navigationAdapter = NavigationDrawerAdapter(navigationDrawerIconsList, images_for_navdrawer, this)

        _navigationAdapter.notifyDataSetChanged()

        val navigation_recycler_view = findViewById<RecyclerView>(R.id.navRecyclerView)

        navigation_recycler_view.layoutManager = LinearLayoutManager(this)

        navigation_recycler_view.itemAnimator = DefaultItemAnimator()


        navigation_recycler_view.adapter = _navigationAdapter

        navigation_recycler_view.setHasFixedSize(true)

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this)
        val mIntentFilter = IntentFilter()
        mIntentFilter.addAction(Constants.ACTION.CLOSE)


        mLocalBroadcastManager?.registerReceiver(
                mBroadcastReceiver,
                mIntentFilter
        )

        bottomNav = findViewById(R.id.bottom_nav)
        bottomNav!!.selectedItemId = R.id.navigation_library_screen

        bottomNav!!.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_library_screen -> {
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.details_fragment, LibraryFragment())
                            .commit()

                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_main_screen -> {
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.details_fragment, MainScreenFragment(), MainScreenFragment.TAG)
                            .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_albums -> {
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.details_fragment, OfflineAlbumsFragment(), OfflineAlbumsFragment.TAG)
                            .commit()
                    return@setOnNavigationItemSelectedListener true

                }
                R.id.navigation_favorites -> {
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.details_fragment, FavoriteFragment(), FavoriteFragment.TAG)
                            .commit()
                    return@setOnNavigationItemSelectedListener true

                }
                else -> {
                    return@setOnNavigationItemSelectedListener false
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        try {
            SongPlayingFragment.Staticated.mSensorManager?.registerListener(
                    mSensorListener,
                    SongPlayingFragment.Staticated.mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL
            )
        } catch (e: Exception) {
        }
    }

    override fun onDestroy() {
        try {
            super.onDestroy()
            mLocalBroadcastManager?.unregisterReceiver(mBroadcastReceiver)
            SongPlayingFragment.Staticated.mSensorManager?.unregisterListener(mSensorListener)
            song!!.unregister()
        } catch (e: Exception) {
        }


    }

    fun moveToHome() {
        bottomNav!!.selectedItemId = R.id.navigation_library_screen
    }

    override fun onBackPressed() {


        if (Statified.drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            Statified.drawerLayout!!.closeDrawer(GravityCompat.START)
        }

        var fragment = supportFragmentManager.findFragmentById(R.id.navigation_library_screen)

        if (fragment != null && fragment.isVisible) {
            finish()
            return
        }

        fragment = supportFragmentManager.findFragmentByTag(MainScreenFragment.TAG)
        if (fragment != null && fragment.isVisible) {
            findViewById<BottomNavigationView>(R.id.bottom_nav)?.selectedItemId = R.id.navigation_library_screen
            OfflineAlbumsFragment.postion = 0
            return
        }

        fragment = supportFragmentManager.findFragmentByTag(FavoriteFragment.TAG)
        if (fragment != null && fragment.isVisible) {
            findViewById<BottomNavigationView>(R.id.bottom_nav)?.selectedItemId = R.id.navigation_library_screen
            return
        }

        fragment = supportFragmentManager.findFragmentByTag(OfflineAlbumsFragment.TAG)
        if (fragment != null && fragment.isVisible) {
            findViewById<BottomNavigationView>(R.id.bottom_nav)?.selectedItemId = R.id.navigation_library_screen
            OfflineAlbumsFragment.postion = 0
            return
        }



        super.onBackPressed()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && grantResults[0] == PackageManager.PERMISSION_DENIED)
            Toast.makeText(this, "Please Provide Storage Permission to Continue", Toast.LENGTH_SHORT).show()
    }
}
