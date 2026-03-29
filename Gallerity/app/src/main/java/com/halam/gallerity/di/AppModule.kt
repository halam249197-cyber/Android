package com.halam.gallerity.di

import android.content.Context
import com.halam.gallerity.data.repository.MediaRepositoryImpl
import com.halam.gallerity.domain.repository.MediaRepository
import com.halam.gallerity.domain.usecase.GetMediaFilesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMediaRepository(
        @ApplicationContext context: Context
    ): MediaRepository {
        return MediaRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideGetMediaFilesUseCase(
        repository: MediaRepository
    ): GetMediaFilesUseCase {
        return GetMediaFilesUseCase(repository)
    }
}
