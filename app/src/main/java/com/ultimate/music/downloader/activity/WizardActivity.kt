package com.ultimate.music.downloader.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.ultimate.music.downloader.R
import com.ultimate.music.downloader.fragment.NotificationSetup

class WizardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Wizard"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        supportFragmentManager.beginTransaction().replace(R.id.fragment, NotificationSetup()).commit()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return false
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}