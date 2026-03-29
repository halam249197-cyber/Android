package com.halam.gallerity.domain.usecase

import com.halam.gallerity.data.local.dao.MediaMetadataDao
import com.halam.gallerity.domain.model.MediaFile
import com.halam.gallerity.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetMediaFilesUseCase(
    private val repository: MediaRepository,
    private val metadataDao: MediaMetadataDao
) {
    operator fun invoke(): Flow<List<MediaFile>> {
        return combine(
            repository.getAllMediaFiles(),
            metadataDao.getAllMetadata()
        ) { rawFiles, metadataList ->
            val metaMap = metadataList.associateBy { it.mediaId }
            rawFiles.map { file ->
                val meta = metaMap[file.id]
                file.copy(
                    isSecured = meta?.isSecured ?: false,
                    isTrashed = meta?.isTrashed ?: false,
                    tags = meta?.tags ?: "",
                    faceCount = meta?.faceCount ?: 0
                )
            }
        }
    }
}
