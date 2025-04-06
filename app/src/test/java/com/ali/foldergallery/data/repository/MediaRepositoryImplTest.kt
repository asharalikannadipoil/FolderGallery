package com.ali.foldergallery.data.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import com.ali.foldergallery.domain.model.AlbumType
import com.ali.foldergallery.domain.model.MediaItem
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowEnvironment
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class MediaRepositoryImplTest {
    private lateinit var repository: MediaRepositoryImpl
    private lateinit var contentResolver: ContentResolver
    private lateinit var imageCursor: android.database.Cursor
    private lateinit var videoCursor: android.database.Cursor
    private lateinit var testDispatcher: TestDispatcher

    @Before
    fun setup() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        contentResolver = mockk(relaxed = true)
        imageCursor = mockk(relaxed = true)
        videoCursor = mockk(relaxed = true)

        mockkStatic(ContentUris::class)
        every { ContentUris.withAppendedId(any(), any()) } answers { 
            val uri = firstArg<Uri>()
            val id = secondArg<Long>()
            Uri.parse("$uri/$id")
        }

        ShadowEnvironment.setExternalStorageState(android.os.Environment.MEDIA_MOUNTED)
        val externalDir = RuntimeEnvironment.getApplication().getExternalFilesDir(null)
        val cameraDir = File(externalDir, "DCIM/Camera")
        cameraDir.mkdirs()

        repository = MediaRepositoryImpl(contentResolver)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun setupContentResolverQuery() {
        every { contentResolver.query(any(), any(), any(), any(), any()) } answers {
            val uri = firstArg<Uri>()
            when {
                uri == MediaStore.Images.Media.EXTERNAL_CONTENT_URI -> imageCursor
                uri == MediaStore.Video.Media.EXTERNAL_CONTENT_URI -> videoCursor
                else -> null
            }
        }
    }

    private fun setupImageCursor(hasData: Boolean) {
        every { imageCursor.moveToFirst() } returns hasData
        if (hasData) {
            every { imageCursor.moveToNext() } returnsMany listOf(true, false)
            every { imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA) } returns 0
            val externalDir = RuntimeEnvironment.getApplication().getExternalFilesDir(null)
            val imagePath = File(externalDir, "DCIM/Camera/image.jpg").absolutePath
            every { imageCursor.getString(0) } returns imagePath
            every { imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID) } returns 1
            every { imageCursor.getLong(1) } returns 1L
            every { imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME) } returns 2
            every { imageCursor.getString(2) } returns "image.jpg"
            every { imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED) } returns 3
            every { imageCursor.getLong(3) } returns 1000L
            every { imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE) } returns 4
            every { imageCursor.getLong(4) } returns 1024L
            every { imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH) } returns 5
            every { imageCursor.getInt(5) } returns 1920
            every { imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT) } returns 6
            every { imageCursor.getInt(6) } returns 1080
        }
    }

    private fun setupVideoCursor(hasData: Boolean) {
        every { videoCursor.moveToFirst() } returns hasData
        if (hasData) {
            every { videoCursor.moveToNext() } returnsMany listOf(true, false)
            every { videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA) } returns 0
            val externalDir = RuntimeEnvironment.getApplication().getExternalFilesDir(null)
            val videoPath = File(externalDir, "DCIM/Camera/video.mp4").absolutePath
            every { videoCursor.getString(0) } returns videoPath
            every { videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID) } returns 1
            every { videoCursor.getLong(1) } returns 1L
            every { videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME) } returns 2
            every { videoCursor.getString(2) } returns "video.mp4"
            every { videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED) } returns 3
            every { videoCursor.getLong(3) } returns 1000L
            every { videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE) } returns 4
            every { videoCursor.getLong(4) } returns 1024L
            every { videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH) } returns 5
            every { videoCursor.getInt(5) } returns 1920
            every { videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT) } returns 6
            every { videoCursor.getInt(6) } returns 1080
            every { videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION) } returns 7
            every { videoCursor.getLong(7) } returns 60000L
        }
    }

    @Test
    fun `getAlbums returns special albums when no media exists`() = runTest {
        setupContentResolverQuery()
        setupImageCursor(false)
        setupVideoCursor(false)

        val albums = repository.getAlbums().first()

        assertEquals(3, albums.size)
        assertTrue(albums.any { it.albumType == AlbumType.ALL_IMAGES })
        assertTrue(albums.any { it.albumType == AlbumType.ALL_VIDEOS })
        assertTrue(albums.any { it.albumType == AlbumType.CAMERA })
    }

    @Test
    fun `getAlbums returns albums with media when media exists`() = runTest {
        setupContentResolverQuery()
        setupImageCursor(true)
        setupVideoCursor(false)

        val albums = repository.getAlbums().first()

        assertEquals(3, albums.size) // Only special albums since all media is in Camera folder
        val cameraAlbum = albums.find { it.albumType == AlbumType.CAMERA }
        assertTrue(cameraAlbum != null)
        assertEquals(1, cameraAlbum.mediaCount)
    }

    @Test
    fun `getMediaInAlbum returns empty list when no media exists`() = runTest {
        setupContentResolverQuery()
        setupImageCursor(false)
        setupVideoCursor(false)

        val mediaItems = repository.getMediaInAlbum("all_images", AlbumType.ALL_IMAGES).first()

        assertTrue(mediaItems.isEmpty())
    }

    @Test
    fun `getMediaInAlbum returns list of media items when media exists`() = runTest {
        setupContentResolverQuery()
        setupImageCursor(true)
        setupVideoCursor(false)

        val mediaItems = repository.getMediaInAlbum("all_images", AlbumType.ALL_IMAGES).first()

        assertEquals(1, mediaItems.size)
        assertTrue(mediaItems[0] is MediaItem.Image)
    }
} 