package com.halam.gallerity.domain.usecase

import com.halam.gallerity.domain.model.MediaFile
import com.halam.gallerity.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow

class GetMediaFilesUseCase(
    private val repository: MediaRepository
) {
    operator fun invoke(): Flow<List<MediaFile>> {
        return repository.getAllMediaFiles()
    }
}
