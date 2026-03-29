package com.halam.gallerity.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.halam.gallerity.data.local.dao.MediaMetadataDao
import com.halam.gallerity.data.local.entity.MediaMetadataEntity
import com.halam.gallerity.domain.repository.MediaRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

@HiltWorker
class MlKitScannerWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val mediaRepository: MediaRepository,
    private val metadataDao: MediaMetadataDao
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        try {
            val allMedia = mediaRepository.getAllMediaFiles().first()
            val scannedIds = metadataDao.getScannedMediaIds().toSet()
            
            val pendingMedia = allMedia.filter { it.id !in scannedIds }

            if (pendingMedia.isEmpty()) return Result.success()

            val faceDetector = FaceDetection.getClient()
            val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

            for (media in pendingMedia) {
                if (isStopped) break

                try {
                    val inputImage = InputImage.fromFilePath(applicationContext, media.uri)
                    
                    val faces = faceDetector.process(inputImage).await()
                    val labels = labeler.process(inputImage).await()
                    
                    val labelString = labels.joinToString(",") { it.text }
                    
                    val entity = MediaMetadataEntity(
                        mediaId = media.id,
                        faceCount = faces.size,
                        isScanned = true,
                        tags = labelString,
                        isSecured = false,
                        isTrashed = false,
                        trashedAt = null
                    )
                    metadataDao.insertMetadata(entity)
                } catch (e: Exception) {
                    // Bypass decoding errors securely
                }
            }
            return Result.success()
        } catch (e: Exception) {
            return Result.retry()
        }
    }
}
