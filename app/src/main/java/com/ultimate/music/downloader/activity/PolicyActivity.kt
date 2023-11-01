package com.ultimate.music.downloader.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.ultimate.music.downloader.R
import com.ultimate.music.downloader.fragment.HelpFragment
import com.ultimate.music.downloader.fragment.PolicyFragment

class PolicyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.policy_webview)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Privacy Policy"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        supportFragmentManager.beginTransaction().replace(R.id.fragment, PolicyFragment()).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return false
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.home)
            finish();
        return super.onOptionsItemSelected(item)
    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}