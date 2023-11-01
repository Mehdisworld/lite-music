package com.ultimate.music.downloader.repositoryModules

import android.content.Context
import com.ultimate.music.downloader.database.CacheMapper
import com.ultimate.music.downloader.database.dao.EchoDao
import com.ultimate.music.downloader.repository.SongsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideSongsRepository(
            @ApplicationContext context: Context,
            echoDao: EchoDao,
            cacheMapper: CacheMapper
    ): SongsRepository {
        return SongsRepository(context, echoDao, cacheMapper)
    }
}














