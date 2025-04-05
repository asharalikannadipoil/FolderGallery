package com.ali.foldergallery.domain.usecases

import com.ali.foldergallery.domain.model.AlbumType
import com.ali.foldergallery.domain.model.MediaItem
import com.ali.foldergallery.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import java.net.URLDecoder
import javax.inject.Inject

class GetMediaInAlbumUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {
    operator fun invoke(albumId: String, albumType: AlbumType): Flow<List<MediaItem>> {
        val decodedAlbumId = URLDecoder.decode(albumId, "utf-8")
        return mediaRepository.getMediaInAlbum(decodedAlbumId, albumType)
    }
}