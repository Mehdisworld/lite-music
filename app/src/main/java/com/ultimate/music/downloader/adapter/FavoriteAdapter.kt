package com.ultimate.music.downloader.adapter

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.ultimate.music.downloader.util.Constants
import com.ultimate.music.downloader.R
import com.ultimate.music.downloader.activity.SongPlayingActivity
import com.ultimate.music.downloader.databinding.RowCustomMainscreenAdapterBinding
import com.ultimate.music.downloader.model.Songs
import com.ultimate.music.downloader.util.MediaUtils
import com.ultimate.music.downloader.util.MediaUtils.mediaPlayer
import com.bumptech.glide.Glide
import java.io.FileDescriptor
import java.lang.Exception
import kotlin.math.max

class FavoriteAdapter(_songDetails: ArrayList<Songs>, _context: Context) : RecyclerView.Adapter<FavoriteAdapter.MyViewHolder>() {

    /*Local variables used for storing the data sent from the fragment to be used in the adapter
    * These variables are initially null*/
    var songDetails: ArrayList<Songs>? = null
    var mContext: Context? = null

    /*In the init block we assign the data received from the params to our local variables*/
    init {
        this.songDetails = _songDetails
        this.mContext = _context
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val songObject = songDetails?.get(position)

        if (MediaUtils.currSong != null && songObject == MediaUtils.currSong
        ) {
            holder.binding.contentRow.strokeWidth = 1
            holder.binding.contentRow.strokeColor = mContext?.resources?.getColor(R.color.colorAccent)!!
        }
        else{
            holder.binding.contentRow.strokeWidth = 0
            holder.binding.contentRow.strokeColor = mContext?.resources?.getColor(R.color.colorPrimary)!!
        }

        /*The holder object of our MyViewHolder class has two properties i.e
        * trackTitle for holding the name of the song and
        * trackArtist for holding the name of the artist*/
        holder.binding.trackTitle?.text = songObject?.songTitle
        holder.binding.trackArtist?.text = songObject?.artist

        if(holder.binding.trackTitle?.text?.equals("<unknown>") == true)
            holder.binding.trackTitle?.text="unknown"

        if(holder.binding.trackArtist?.text ?.equals("<unknown>") == true)
            holder.binding.trackArtist?.text ="unknown"

        var albumId = songObject?.songAlbum as Long

        if(albumId<=0L) holder.binding.album!!.setImageDrawable(mContext!!.resources.getDrawable(R.drawable.now_playing_bar_eq_image))
        val sArtworkUri: Uri = Uri
                .parse("content://media/external/audio/albumart")
        val uri: Uri = ContentUris.withAppendedId(sArtworkUri, albumId)
        mContext?.let { holder.binding.album?.let { it1 -> Glide.with(it).load(uri).placeholder(R.drawable.now_playing_bar_eq_image).into(it1) } }
//        var art = getAlbumart(albumId)
//
//        if(art!=null) holder.binding.trackArt?.setImageBitmap(art)
//        else holder.binding.trackArt?.setImageDrawable(mContext?.resources?.getDrawable(R.drawable.now_playing_bar_eq_image))

        /*Handling the click event i.e. the action which happens when we click on any song*/
        holder.binding.contentRow?.setOnClickListener {

            /*Let's discuss this peice of code*/
            /*Firstly we define an object of the SongPlayingFragment*/
            var intent = Intent(mContext,SongPlayingActivity::class.java)
            notifyItemChanged(max(MediaUtils.getSongIndex(),0))
            MediaUtils.currSong = songObject

            /*A bundle is used to transfer data from one point in your activity to another
            * Here we create an object of Bundle to send the sond details to the fragment so that we can display the song details there and also play the song*/
            /*putString() function is used for adding a string to the bundle object
            * the string written in green is the name of the string which is placed in the bundle object with the value of that string written alongside
            * Note: Remember the name of the strings/entities you place inside the bundle object as you will retrieve them later using the same name. And these names are case-sensitive*/
            intent.putExtra("songArtist", songObject.artist)
            intent.putExtra("songTitle", songObject.songTitle)
            intent.putExtra("path", songObject.songData)
            intent.putExtra("SongID", songObject.songID)
            intent.putExtra("songPosition", position)
            intent.putExtra("songAlbum", songObject.songAlbum as Long)

            MediaUtils.songsList = songDetails?: ArrayList()

            stopPlaying(intent)

            holder.binding?.contentRow?.strokeWidth = 1
            holder.binding?.contentRow?.strokeColor = mContext?.resources?.getColor(R.color.colorAccent)!!

//            var serviceIntent = Intent(mContext, EchoNotification::class.java)
//
//            serviceIntent.putExtra("title", songObject.songTitle)
//            serviceIntent.putExtra("artist", songObject.artist)
//            serviceIntent.putExtra("album", songObject.songAlbum?)
//
//            serviceIntent.action = Constants.ACTION.STARTFOREGROUND_ACTION
//
//            mContext?.startService(serviceIntent)

            /*Now after placing the song details inside the bundle, we inflate the song playing fragment*/
            (mContext as FragmentActivity).startActivity(intent)
        }
    }

    /*This has the same implementation which we did for the navigation drawer adapter*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_custom_mainscreen_adapter, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {

        /*If the array list for the songs is null i.e. there are no songs in your device
        * then we return 0 and no songs are displayed*/
        if (songDetails == null) {
            return 0
        }

        /*Else we return the total size of the song details which will be the total number of song details*/
        else {
            return (songDetails as ArrayList<Songs>).size
        }
    }

    /*Every view holder class we create will serve the same purpose as it did when we created it for the navigation drawer*/
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        
        val binding = RowCustomMainscreenAdapterBinding.bind(view)
    }

    private fun stopPlaying(intent: Intent) {
        try {
            if (mediaPlayer != null && MediaUtils.isMediaPlayerPlaying()) {
                mediaPlayer.stop()
                intent.putExtra(Constants.WAS_MEDIA_PLAYING,true)
            }
            MainScreenAdapter.Statified.stopPlayingCalled = true
        }catch (e:Exception){}


    }
    fun getAlbumart(album_id: Long): Bitmap? {
        var bm: Bitmap? = null
        if(album_id <= 0L) return bm
        try {
            val sArtworkUri: Uri = Uri
                    .parse("content://media/external/audio/albumart")
            val uri: Uri = ContentUris.withAppendedId(sArtworkUri, album_id)
            val pfd: ParcelFileDescriptor? = mContext?.contentResolver
                    ?.openFileDescriptor(uri, "r")
            if (pfd != null) {
                val fd: FileDescriptor = pfd.fileDescriptor
                bm = BitmapFactory.decodeFileDescriptor(fd)
            }
        } catch (e: java.lang.Exception) {
        }
        return bm
    }


    fun filter_data(newList : ArrayList<Songs>?){


        if(newList!=null) {
//            songDetails?.removeAll(ArrayList<Songs>())

            songDetails = ArrayList<Songs>()
            songDetails?.addAll(newList)

            notifyDataSetChanged()
        }



    }


}