package com.ali.foldergallery.domain.usecases

import com.ali.foldergallery.domain.model.Album
import com.ali.foldergallery.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAlbumsUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {
    operator fun invoke(): Flow<List<Album>> {
        return mediaRepository.getAlbums()
    }
}