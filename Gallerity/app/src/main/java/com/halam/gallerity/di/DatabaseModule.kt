package com.halam.gallerity.di

import android.app.Application
import androidx.room.Room
import com.halam.gallerity.data.local.GallerityDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideGallerityDatabase(app: Application): GallerityDatabase {
        return Room.databaseBuilder(
            app,
            GallerityDatabase::class.java,
            "gallerity_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideMediaMetadataDao(db: GallerityDatabase) = db.mediaMetadataDao
}
