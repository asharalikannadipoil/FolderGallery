package com.ali.foldergallery.domain.repository

import com.ali.foldergallery.domain.model.Album
import com.ali.foldergallery.domain.model.AlbumType
import com.ali.foldergallery.domain.model.MediaItem
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    fun getAlbums(): Flow<List<Album>>
    fun getMediaInAlbum(albumId: String, albumType: AlbumType): Flow<List<MediaItem>>
    suspend fun getMediaItem(mediaId: Long): MediaItem?
}