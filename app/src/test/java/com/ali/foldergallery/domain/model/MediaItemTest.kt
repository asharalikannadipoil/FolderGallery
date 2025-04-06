package com.ali.foldergallery.domain.model

import android.net.Uri
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class MediaItemTest {
    @Test
    fun `MediaItem Image creation test`() {
        val id = 1L
        val uri = Uri.parse("content://media/external/images/media/1")
        val name = "test.jpg"
        val path = "/test/path/test.jpg"
        val dateAdded = Date()
        val size = 1024L
        val width = 1920
        val height = 1080

        val image = MediaItem.Image(
            id = id,
            uri = uri,
            name = name,
            path = path,
            dateAdded = dateAdded,
            size = size,
            width = width,
            height = height
        )

        assertEquals(id, image.id)
        assertEquals(uri, image.uri)
        assertEquals(name, image.name)
        assertEquals(path, image.path)
        assertEquals(dateAdded, image.dateAdded)
        assertEquals(size, image.size)
        assertEquals(width, image.width)
        assertEquals(height, image.height)
        assertTrue(image is MediaItem.Image)
    }

    @Test
    fun `MediaItem Video creation test`() {
        val id = 2L
        val uri = Uri.parse("content://media/external/video/media/2")
        val name = "test.mp4"
        val path = "/test/path/test.mp4"
        val dateAdded = Date()
        val size = 2048L
        val width = 1920
        val height = 1080
        val duration = 60000L // 1 minute in milliseconds

        val video = MediaItem.Video(
            id = id,
            uri = uri,
            name = name,
            path = path,
            dateAdded = dateAdded,
            size = size,
            width = width,
            height = height,
            duration = duration
        )

        assertEquals(id, video.id)
        assertEquals(uri, video.uri)
        assertEquals(name, video.name)
        assertEquals(path, video.path)
        assertEquals(dateAdded, video.dateAdded)
        assertEquals(size, video.size)
        assertEquals(width, video.width)
        assertEquals(height, video.height)
        assertEquals(duration, video.duration)
        assertTrue(video is MediaItem.Video)
    }

    @Test
    fun `MediaItem Image equality test`() {
        val dateAdded = Date()
        val image1 = MediaItem.Image(
            id = 1L,
            uri = Uri.parse("content://media/external/images/media/1"),
            name = "test.jpg",
            path = "/test/path/test.jpg",
            dateAdded = dateAdded,
            size = 1024L,
            width = 1920,
            height = 1080
        )

        val image2 = MediaItem.Image(
            id = 1L,
            uri = Uri.parse("content://media/external/images/media/1"),
            name = "test.jpg",
            path = "/test/path/test.jpg",
            dateAdded = dateAdded,
            size = 1024L,
            width = 1920,
            height = 1080
        )

        assertEquals(image1, image2)
    }

    @Test
    fun `MediaItem Video equality test`() {
        val dateAdded = Date()
        val video1 = MediaItem.Video(
            id = 2L,
            uri = Uri.parse("content://media/external/video/media/2"),
            name = "test.mp4",
            path = "/test/path/test.mp4",
            dateAdded = dateAdded,
            size = 2048L,
            width = 1920,
            height = 1080,
            duration = 60000L
        )

        val video2 = MediaItem.Video(
            id = 2L,
            uri = Uri.parse("content://media/external/video/media/2"),
            name = "test.mp4",
            path = "/test/path/test.mp4",
            dateAdded = dateAdded,
            size = 2048L,
            width = 1920,
            height = 1080,
            duration = 60000L
        )

        assertEquals(video1, video2)
    }

    @Test
    fun `MediaItem Image copy test`() {
        val originalImage = MediaItem.Image(
            id = 1L,
            uri = Uri.parse("content://media/external/images/media/1"),
            name = "test.jpg",
            path = "/test/path/test.jpg",
            dateAdded = Date(),
            size = 1024L,
            width = 1920,
            height = 1080
        )

        val copiedImage = originalImage.copy(name = "new.jpg")

        assertEquals(originalImage.id, copiedImage.id)
        assertEquals("new.jpg", copiedImage.name)
        assertEquals(originalImage.uri, copiedImage.uri)
        assertEquals(originalImage.path, copiedImage.path)
        assertEquals(originalImage.dateAdded, copiedImage.dateAdded)
        assertEquals(originalImage.size, copiedImage.size)
        assertEquals(originalImage.width, copiedImage.width)
        assertEquals(originalImage.height, copiedImage.height)
    }

    @Test
    fun `MediaItem Video copy test`() {
        val originalVideo = MediaItem.Video(
            id = 2L,
            uri = Uri.parse("content://media/external/video/media/2"),
            name = "test.mp4",
            path = "/test/path/test.mp4",
            dateAdded = Date(),
            size = 2048L,
            width = 1920,
            height = 1080,
            duration = 60000L
        )

        val copiedVideo = originalVideo.copy(name = "new.mp4")

        assertEquals(originalVideo.id, copiedVideo.id)
        assertEquals("new.mp4", copiedVideo.name)
        assertEquals(originalVideo.uri, copiedVideo.uri)
        assertEquals(originalVideo.path, copiedVideo.path)
        assertEquals(originalVideo.dateAdded, copiedVideo.dateAdded)
        assertEquals(originalVideo.size, copiedVideo.size)
        assertEquals(originalVideo.width, copiedVideo.width)
        assertEquals(originalVideo.height, copiedVideo.height)
        assertEquals(originalVideo.duration, copiedVideo.duration)
    }
} 