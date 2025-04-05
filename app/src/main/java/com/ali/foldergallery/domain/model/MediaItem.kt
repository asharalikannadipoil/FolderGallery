package com.ali.foldergallery.domain.model

import android.net.Uri
import java.util.Date

sealed class MediaItem {
    abstract val id: Long
    abstract val uri: Uri
    abstract val name: String
    abstract val path: String
    abstract val dateAdded: Date
    abstract val size: Long

    data class Image(
        override val id: Long,
        override val uri: Uri,
        override val name: String,
        override val path: String,
        override val dateAdded: Date,
        override val size: Long,
        val width: Int,
        val height: Int
    ) : MediaItem()

    data class Video(
        override val id: Long,
        override val uri: Uri,
        override val name: String,
        override val path: String,
        override val dateAdded: Date,
        override val size: Long,
        val width: Int,
        val height: Int,
        val duration: Long
    ) : MediaItem()
}
