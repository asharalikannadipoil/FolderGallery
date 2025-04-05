package com.ali.foldergallery.domain.repository

import com.ali.foldergallery.domain.model.Album
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    fun getAlbums(): Flow<List<Album>>
}