package com.halam.gallerity.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media_metadata")
data class MediaMetadataEntity(
    @PrimaryKey val mediaId: Long,
    val faceCount: Int = 0,
    val isScanned: Boolean = false,
    val tags: String = "", // Comma separated ML labels like "Dog, Nature, Vehicle"
    val isSecured: Boolean = false,
    val isTrashed: Boolean = false,
    val trashedAt: Long? = null
)
