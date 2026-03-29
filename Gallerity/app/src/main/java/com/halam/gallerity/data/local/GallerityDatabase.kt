package com.halam.gallerity.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.halam.gallerity.data.local.entity.MediaMetadataEntity
import com.halam.gallerity.data.local.dao.MediaMetadataDao

@Database(entities = [MediaMetadataEntity::class], version = 1, exportSchema = false)
abstract class GallerityDatabase : RoomDatabase() {
    abstract val mediaMetadataDao: MediaMetadataDao
}
