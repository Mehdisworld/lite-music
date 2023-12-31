package com.ultimate.music.downloader.repository

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import com.ultimate.music.downloader.model.SongAlbum
import com.ultimate.music.downloader.model.Songs
import com.ultimate.music.downloader.database.CacheMapper
import com.ultimate.music.downloader.database.dao.EchoDao
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlin.collections.ArrayList

class SongsRepository
constructor(
        @ApplicationContext private val context: Context,
        private val echoDao: EchoDao,
        private val cacheMapper: CacheMapper
){

    suspend fun fetchSongs(){
        var songs = getSongsFromPhone()
            echoDao.insertAll(cacheMapper.mapToEntityList(songs))
    }

    suspend fun getAllSongs(): List<Songs> {
        return cacheMapper.mapFromEntityList(echoDao.getSongs())
    }

    suspend fun fetchAlbums(){
        var albums = echoDao.getAllAlbums()
        echoDao.insertAllAlbums(albums)
    }

    suspend fun getAlbums(): List<SongAlbum>{
       return cacheMapper.mapFromAlbumEntityList(echoDao.getAlbums())
    }

    suspend fun getSongsByAlbum(id:Long?) :List<Songs>{
        return cacheMapper.mapFromEntityList(echoDao.getSongsByAlbum(id))
    }


    @SuppressLint("Recycle")
    fun getSongsFromPhone(): ArrayList<Songs> {

        var arralist = ArrayList<Songs>()
        var contentResolver = context.contentResolver

        val songURI =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Audio.Media.getContentUri(
                            MediaStore.VOLUME_EXTERNAL
                    )
                } else {
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
//        var songURI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var songCursor = contentResolver?.query(songURI, null, null, null, null)

        if (songCursor != null && songCursor.moveToFirst()) {
            // getting column indices to query
            val songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dateAdded = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
            val songAlbum = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
            val songAlbumName = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)

            while (songCursor.moveToNext()) {
                // getting the data from the indices
                var currentID = songCursor.getLong(songId)
                var currTitle = songCursor.getString(songTitle)
                var currArtist = songCursor.getString(songArtist)
                var album = songCursor.getString(songAlbumName)
                var currData = songCursor.getString(songData)
                var currdate = songCursor.getLong(dateAdded)
                var currAlbum = songCursor.getLong(songAlbum)


                try {
                    arralist.add(Songs(currentID, currTitle,  currArtist, album, currData, currdate, currAlbum))
                }
                catch (e:Exception){
//                    Toast.makeText(context,e.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }

        try {
            removeduplicates(arralist)
        }catch (e:Exception){}

        try {
            songCursor!!.close()
        }catch (e:Exception){}

        return arralist


    }

    fun removeduplicates(list:ArrayList<Songs>) {

        // preventing index out of bounds
        try {
            for (i in 0 until list.size - 3) {
                for (j in i + 1 until list.size - 3) {
                    if (list.get(j).songTitle == list.get(i).songTitle && list.get(j).artist == list.get(i).artist)
                        list.removeAt(j)
                }
            }
        }catch (e:Exception){}
    }
}