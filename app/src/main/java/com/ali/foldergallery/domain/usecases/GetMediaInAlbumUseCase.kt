package com.ali.foldergallery.domain.usecases

import com.ali.foldergallery.domain.model.AlbumType
import com.ali.foldergallery.domain.model.MediaItem
import com.ali.foldergallery.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMediaInAlbumUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {
    operator fun invoke(albumId: String, albumType: AlbumType): Flow<List<MediaItem>> {
        return mediaRepository.getMediaInAlbum(albumId, albumType)
    }
}