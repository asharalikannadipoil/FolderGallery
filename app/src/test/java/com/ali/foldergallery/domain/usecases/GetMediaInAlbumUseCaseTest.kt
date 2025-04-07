package com.ali.foldergallery.domain.usecases

import android.net.Uri
import com.ali.foldergallery.domain.model.AlbumType
import com.ali.foldergallery.domain.model.MediaItem
import com.ali.foldergallery.domain.repository.MediaRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
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
import org.robolectric.annotation.Config
import java.util.Date
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class GetMediaInAlbumUseCaseTest {
    private lateinit var useCase: GetMediaInAlbumUseCase
    private lateinit var mediaRepository: MediaRepository
    private lateinit var testDispatcher: TestDispatcher

    @Before
    fun setup() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        mediaRepository = mockk()
        useCase = GetMediaInAlbumUseCase(mediaRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke returns media items from repository`() = runTest {
        val albumId = "test_album"
        val albumType = AlbumType.REGULAR
        val mediaItems = listOf(
            MediaItem.Image(
                id = 1L,
                uri = Uri.parse("content://media/external/images/media/1"),
                name = "test.jpg",
                path = "/test/path/test.jpg",
                dateAdded = Date(),
                size = 1024L,
                width = 1920,
                height = 1080
            )
        )
        coEvery { mediaRepository.getMediaInAlbum(albumId, albumType) } returns flow { emit(mediaItems) }

        val result = useCase(albumId, albumType).first()

        assertEquals(mediaItems, result)
    }

    @Test
    fun `invoke returns empty list when repository returns empty list`() = runTest {
        val albumId = "test_album"
        val albumType = AlbumType.REGULAR
        val emptyList = emptyList<MediaItem>()
        coEvery { mediaRepository.getMediaInAlbum(albumId, albumType) } returns flow { emit(emptyList) }

        val result = useCase(albumId, albumType).first()

        assertEquals(emptyList, result)
    }

    @Test
    fun `invoke decodes albumId before calling repository`() = runTest {
        val encodedAlbumId = "test%20album"
        val decodedAlbumId = "test album"
        val albumType = AlbumType.REGULAR
        val mediaItems = listOf(
            MediaItem.Image(
                id = 1L,
                uri = Uri.parse("content://media/external/images/media/1"),
                name = "test.jpg",
                path = "/test/path/test.jpg",
                dateAdded = Date(),
                size = 1024L,
                width = 1920,
                height = 1080
            )
        )
        coEvery { mediaRepository.getMediaInAlbum(decodedAlbumId, albumType) } returns flow { emit(mediaItems) }

        val result = useCase(encodedAlbumId, albumType).first()

        assertEquals(mediaItems, result)
    }
} 