package com.ultimate.music.downloader.adapter

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.ultimate.music.downloader.R
import com.ultimate.music.downloader.activity.HelpActivity
import com.ultimate.music.downloader.activity.MainActivity
import com.ultimate.music.downloader.activity.PolicyActivity
import com.ultimate.music.downloader.activity.SettingsActivity
import com.ultimate.music.downloader.adsManager.AdMobAds
import java.io.*


class NavigationDrawerAdapter(_contentList: ArrayList<String>, _getImages: Array<Int>, _context: Context)
    : RecyclerView.Adapter<NavigationDrawerAdapter.NavViewHolder>() {

    var contentList: ArrayList<String>? = null
    var getImages: Array<Int>? = null
    var mContext: Context? = null
    var adMobAds: AdMobAds? = null

    init {
        this.contentList = _contentList
        this.getImages = _getImages
        this.mContext = _context
        adMobAds = AdMobAds(this.mContext)
    }

    // TODO: Navigation Drawer OnClick Listener
    override fun onBindViewHolder(holder: NavViewHolder, position: Int) {
        holder.icon_GET?.setBackgroundResource(getImages?.get(position) as Int)
        holder.text_GET?.text = contentList?.get(position)
        holder.contentHolder?.setOnClickListener {
            if (position == 0) {
                (mContext as MainActivity).moveToHome()
            } else if (position == 1) {
                mContext?.startActivity(Intent(mContext, SettingsActivity::class.java))
            } else if (position == 2) {
                mContext?.startActivity(Intent(mContext, PolicyActivity::class.java))
            } else if (position == 3) {
                mContext?.startActivity(Intent(mContext, HelpActivity::class.java))
            } else if (position == 4) {
                val shareLink = "Top App for downloading Music http://play.google.com/store/apps/details?id=" + mContext!!.packageName
                val share = Intent(Intent.ACTION_SEND)
                share.type = "text/plain"
                share.putExtra(Intent.EXTRA_TEXT, shareLink)
                mContext!!.startActivity(share)
            } else if (position == 5) {
                val uri = Uri.parse("market://details?id=" + mContext?.packageName)
                val goToMarket = Intent(Intent.ACTION_VIEW, uri)

                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                try {
                    mContext!!.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + mContext!!.packageName)))
                } catch (e: ActivityNotFoundException) {
                    mContext?.startActivity(goToMarket)
                }
            } else if (position == 6) {
                val storeName = mContext?.getString(R.string.store_name)
                try {
                    mContext!!.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://developer?id=$storeName")))
                } catch (e: ActivityNotFoundException) {
                    mContext!!.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=$storeName")))
                }
            } else if (position == 7) {
                val i = Intent(Intent.ACTION_SEND)
                i.type = "message/html"
                i.putExtra(Intent.EXTRA_EMAIL, arrayOf("contact.flexible@gmail.com"))
                i.putExtra(Intent.EXTRA_SUBJECT, "Feedback")
                try {
                    mContext?.startActivity(Intent.createChooser(i, "Send feedback..."))
                } catch (ex: ActivityNotFoundException) {
                    Toast.makeText(mContext, "There are no email clients installed.", Toast.LENGTH_SHORT).show()
                }


            }
            MainActivity.Statified.drawerLayout?.closeDrawers()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavViewHolder {

        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_custom_navidrawer, parent, false)

        return NavViewHolder(itemView)
    }


    override fun getItemCount(): Int {

        return (contentList as ArrayList).size
    }

    class NavViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var icon_GET: ImageView? = null
        var text_GET: TextView? = null
        var contentHolder: RelativeLayout? = null

        init {
            icon_GET = itemView.findViewById(R.id.icon_navdrawer)
            text_GET = itemView.findViewById(R.id.text_navDrawer)
            contentHolder = itemView.findViewById(R.id.navdrawer_item_skeleton)
        }
    }

    fun getImageUri(): Uri {
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        val bm = BitmapFactory.decodeResource(mContext?.resources, R.drawable.echo_icon)
        val extStorageDirectory = Environment.getExternalStorageDirectory().toString()
        val file = File(extStorageDirectory, "ECHO.png")
        if (!file.exists()) {
            var outStream: OutputStream? = null
            try {
                outStream = FileOutputStream(file)
                bm.compress(Bitmap.CompressFormat.PNG, 100, outStream)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            try {
                outStream?.flush()
                outStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return Uri.parse(file.absolutePath)
    }
}
