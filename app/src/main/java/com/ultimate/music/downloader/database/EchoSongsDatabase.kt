package com.ultimate.music.downloader.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ultimate.music.downloader.database.dao.EchoDao
import com.ultimate.music.downloader.database.entity.FavoriteEntity
import com.ultimate.music.downloader.database.entity.SongAlbumEntity
import com.ultimate.music.downloader.database.entity.SongArtistEntity
import com.ultimate.music.downloader.database.entity.SongsEntity

@Database(entities = arrayOf(SongsEntity::class, SongAlbumEntity::class, SongArtistEntity::class, FavoriteEntity::class), version = 2, exportSchema = false)
abstract class EchoSongsDatabase : RoomDatabase() {
    abstract fun SongsDao(): EchoDao

    companion object {
        val DATABASE_NAME: String = "echo_songs_db"
    }
}