package com.ali.foldergallery.data.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.ali.foldergallery.domain.model.Album
import com.ali.foldergallery.domain.model.AlbumType
import com.ali.foldergallery.domain.model.MediaItem
import com.ali.foldergallery.domain.repository.MediaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Date
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val contentResolver: ContentResolver
) : MediaRepository {
    companion object {
        private const val CAMERA_FOLDER = "Camera"
    }

    override fun getAlbums(): Flow<List<Album>> = flow {
        val albums = mutableMapOf<String, MutableList<MediaItem>>()
        val allImages = mutableListOf<MediaItem>()
        val allVideos = mutableListOf<MediaItem>()
        val cameraItems = mutableListOf<MediaItem>()

        // Get all images
        getMediaItems(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            isVideo = false
        ).forEach { image ->
            allImages.add(image)

            // Get folder name
            val folder = File(image.path).parent ?: ""
            val folderName = File(folder).name

            if (folderName == CAMERA_FOLDER) {
                cameraItems.add(image)
            }

            albums.getOrPut(folder) { mutableListOf() }.add(image)
        }

        // Get all videos
        getMediaItems(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            isVideo = true
        ).forEach { video ->
            allVideos.add(video)

            // Get folder name
            val folder = File(video.path).parent ?: ""
            val folderName = File(folder).name

            if (folderName == CAMERA_FOLDER) {
                cameraItems.add(video)
            }

            albums.getOrPut(folder) { mutableListOf() }.add(video)
        }

        // Create album list
        val albumList = mutableListOf<Album>()

        // Add special albums
        albumList.add(
            Album(
                id = "all_images",
                name = "All Images",
                coverUri = allImages.firstOrNull()?.uri,
                mediaCount = allImages.size,
                albumType = AlbumType.ALL_IMAGES
            )
        )

        albumList.add(
            Album(
                id = "all_videos",
                name = "All Videos",
                coverUri = allVideos.firstOrNull()?.uri,
                mediaCount = allVideos.size,
                albumType = AlbumType.ALL_VIDEOS
            )
        )

        albumList.add(
            Album(
                id = "camera",
                name = "Camera",
                coverUri = cameraItems.firstOrNull()?.uri,
                mediaCount = cameraItems.size,
                albumType = AlbumType.CAMERA
            )
        )

        // Add regular albums
        albums.forEach { (path, items) ->
            val folderName = File(path).name
            if(folderName == CAMERA_FOLDER) return@forEach
            albumList.add(
                Album(
                    id = path,
                    name = folderName,
                    coverUri = items.firstOrNull()?.uri,
                    mediaCount = items.size,
                    albumType = AlbumType.REGULAR
                )
            )
        }

        emit(albumList)
    }.flowOn(Dispatchers.IO)

    override fun getMediaInAlbum(albumId: String, albumType: AlbumType): Flow<List<MediaItem>> =
        flow {
            val mediaItems = when (albumType) {
                AlbumType.ALL_IMAGES -> {
                    getMediaItems(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, isVideo = false)
                }

                AlbumType.ALL_VIDEOS -> {
                    getMediaItems(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, isVideo = true)
                }

                AlbumType.CAMERA -> {
                    val allMedia = mutableListOf<MediaItem>()
                    allMedia.addAll(
                        getMediaItems(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            isVideo = false
                        )
                    )
                    allMedia.addAll(
                        getMediaItems(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            isVideo = true
                        )
                    )
                    allMedia.filter { mediaItem ->
                        val folderName = File(File(mediaItem.path).parent ?: "").name
                        folderName == CAMERA_FOLDER
                    }
                }

                AlbumType.REGULAR -> {
                    val allMedia = mutableListOf<MediaItem>()
                    allMedia.addAll(
                        getMediaItems(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            isVideo = false
                        )
                    )
                    allMedia.addAll(
                        getMediaItems(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            isVideo = true
                        )
                    )
                    allMedia.filter { mediaItem ->
                        File(mediaItem.path).parent == albumId
                    }
                }
            }
            emit(mediaItems)
        }.flowOn(Dispatchers.IO)

    override suspend fun getMediaItem(mediaId: Long): MediaItem? = withContext(Dispatchers.IO) {
        var mediaItem: MediaItem? = null

        // Try to find in images
        getMediaItems(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            isVideo = false
        ).find { it.id == mediaId }?.let {
            mediaItem = it
        }

        // If not found, try videos
        if (mediaItem == null) {
            getMediaItems(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                isVideo = true
            ).find { it.id == mediaId }?.let {
                mediaItem = it
            }
        }

        mediaItem
    }

    private fun getMediaItems(uri: Uri, isVideo: Boolean): List<MediaItem> {
        val mediaItems = mutableListOf<MediaItem>()

        val projection = if (isVideo) {
            arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.WIDTH,
                MediaStore.Video.Media.HEIGHT,
                MediaStore.Video.Media.DURATION
            )
        } else {
            arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT
            )
        }

        // Skip .nomedia folders
        val selection = if (isVideo) {
            "${MediaStore.Video.Media.DATA} NOT LIKE ?"
        } else {
            "${MediaStore.Images.Media.DATA} NOT LIKE ?"
        }
        val selectionArgs = arrayOf("%/nomedia/%")

        val sortOrder = if (isVideo) {
            "${MediaStore.Video.Media.DATE_ADDED} DESC"
        } else {
            "${MediaStore.Images.Media.DATE_ADDED} DESC"
        }

        contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)?.use { cursor ->
            while (cursor.moveToNext()) {
                val mediaItem = if (isVideo) {
                    createVideoItem(cursor)
                } else {
                    createImageItem(cursor)
                }
                mediaItems.add(mediaItem)
            }
        }

        return mediaItems
    }

    private fun createVideoItem(cursor: Cursor): MediaItem.Video {
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
        val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
        val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
        val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
        val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
        val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH)
        val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT)
        val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)

        val id = cursor.getLong(idColumn)
        val name = cursor.getString(nameColumn)
        val path = cursor.getString(pathColumn)
        val dateAdded = Date(cursor.getLong(dateAddedColumn) * 1000)
        val size = cursor.getLong(sizeColumn)
        val width = cursor.getInt(widthColumn)
        val height = cursor.getInt(heightColumn)
        val duration = cursor.getLong(durationColumn)

        val contentUri = ContentUris.withAppendedId(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            id
        )

        return MediaItem.Video(
            id = id,
            uri = contentUri,
            name = name,
            path = path,
            dateAdded = dateAdded,
            size = size,
            width = width,
            height = height,
            duration = duration
        )
    }

    private fun createImageItem(cursor: Cursor): MediaItem.Image {
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
        val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
        val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
        val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
        val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)

        val id = cursor.getLong(idColumn)
        val name = cursor.getString(nameColumn)
        val path = cursor.getString(pathColumn)
        val dateAdded = Date(cursor.getLong(dateAddedColumn) * 1000)
        val size = cursor.getLong(sizeColumn)
        val width = cursor.getInt(widthColumn)
        val height = cursor.getInt(heightColumn)

        val contentUri = ContentUris.withAppendedId(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            id
        )

        return MediaItem.Image(
            id = id,
            uri = contentUri,
            name = name,
            path = path,
            dateAdded = dateAdded,
            size = size,
            width = width,
            height = height
        )
    }
}