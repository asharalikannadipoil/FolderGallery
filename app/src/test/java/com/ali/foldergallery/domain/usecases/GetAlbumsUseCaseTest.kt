package com.ali.foldergallery.domain.usecases

import android.net.Uri
import com.ali.foldergallery.domain.model.Album
import com.ali.foldergallery.domain.model.AlbumType
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
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class GetAlbumsUseCaseTest {
    private lateinit var useCase: GetAlbumsUseCase
    private lateinit var mediaRepository: MediaRepository
    private lateinit var testDispatcher: TestDispatcher

    @Before
    fun setup() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        mediaRepository = mockk()
        useCase = GetAlbumsUseCase(mediaRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke returns albums from repository`() = runTest {
        val albums = listOf(
            Album(
                id = "test_album",
                name = "Test Album",
                coverUri = Uri.parse("content://media/external/images/media/1"),
                mediaCount = 5,
                albumType = AlbumType.REGULAR
            )
        )
        coEvery { mediaRepository.getAlbums() } returns flow { emit(albums) }

        val result = useCase().first()

        assertEquals(albums, result)
    }

    @Test
    fun `invoke returns empty list when repository returns empty list`() = runTest {
        val emptyList = emptyList<Album>()
        coEvery { mediaRepository.getAlbums() } returns flow { emit(emptyList) }

        val result = useCase().first()

        assertEquals(emptyList, result)
    }
} 