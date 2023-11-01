package com.ultimate.music.downloader.repositoryModules


import com.ultimate.music.downloader.repository.SongsRepository
import com.ultimate.music.downloader.viewModel.SongsViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object ViewModelModule {

    @Singleton
    @Provides
    fun provideSongsViewModel(
            songsRepository: SongsRepository
    ): SongsViewModel {
        return SongsViewModel(songsRepository)
    }
}














