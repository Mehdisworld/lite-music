package com.ultimate.music.downloader.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.ultimate.music.downloader.R
import com.ultimate.music.downloader.fragment.SettingsFragment

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Settings"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        supportFragmentManager.beginTransaction().replace(R.id.fragment, SettingsFragment()).commit()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return false
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}