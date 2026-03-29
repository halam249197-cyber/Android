package com.halam.gallerity.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.halam.gallerity.data.local.entity.MediaMetadataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaMetadataDao {
    @Query("SELECT * FROM media_metadata")
    fun getAllMetadata(): Flow<List<MediaMetadataEntity>>

    @Query("SELECT * FROM media_metadata WHERE mediaId = :mediaId")
    suspend fun getMetadataById(mediaId: Long): MediaMetadataEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetadata(metadata: MediaMetadataEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMetadata(metadataList: List<MediaMetadataEntity>)

    @Query("UPDATE media_metadata SET isSecured = :isSecured WHERE mediaId = :mediaId")
    suspend fun updateSecurityStatus(mediaId: Long, isSecured: Boolean)
    
    @Query("UPDATE media_metadata SET isTrashed = :isTrashed, trashedAt = :trashedAt WHERE mediaId = :mediaId")
    suspend fun updateTrashStatus(mediaId: Long, isTrashed: Boolean, trashedAt: Long?)
    
    @Query("SELECT mediaId FROM media_metadata WHERE isScanned = 1")
    suspend fun getScannedMediaIds(): List<Long>
}
