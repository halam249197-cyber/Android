package com.halam.gallerity.domain.repository

import com.halam.gallerity.domain.model.MediaFile
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    fun getAllMediaFiles(): Flow<List<MediaFile>>
}
