package com.ali.foldergallery.domain.model

import android.net.Uri

data class Album(
    val id: String,
    val name: String,
    val coverUri: Uri?,
    val mediaCount: Int,
    val albumType: AlbumType
)

enum class AlbumType {
    REGULAR, ALL_IMAGES, ALL_VIDEOS, CAMERA
}