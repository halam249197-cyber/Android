package com.halam.gallerity.domain.model

import android.net.Uri

data class MediaFile(
    val id: Long,
    val uri: Uri,
    val name: String,
    val size: Long,
    val mimeType: String,
    val dateAdded: Long,
    val folderName: String
)
