package com.ali.foldergallery.presentation.screens.albumdetail

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import com.ali.foldergallery.domain.model.AlbumType
import com.ali.foldergallery.domain.model.MediaItem
import com.ali.foldergallery.domain.usecases.GetMediaInAlbumUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class AlbumDetailViewModelTest {
    private lateinit var viewModel: AlbumDetailViewModel
    private lateinit var getMediaInAlbumUseCase: GetMediaInAlbumUseCase
    private lateinit var testDispatcher: TestDispatcher

    @Before
    fun setup() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        getMediaInAlbumUseCase = mockk()
        coEvery { getMediaInAlbumUseCase("test_album", AlbumType.ALL_IMAGES) } returns flow { emit(emptyList()) }
        viewModel = AlbumDetailViewModel(
            getMediaInAlbumUseCase = getMediaInAlbumUseCase,
            savedStateHandle = SavedStateHandle().apply {
                set("albumId", "test_album")
                set("albumName", "Test Album")
                set("albumType", AlbumType.ALL_IMAGES.name)
            }
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        assertTrue(viewModel.uiState.value is AlbumDetailUiState.Loading)
    }

    @Test
    fun `state updates to Success when media items are loaded`() = runTest {
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
        coEvery { getMediaInAlbumUseCase("test_album", AlbumType.ALL_IMAGES) } returns flow { emit(mediaItems) }

        viewModel.loadMediaItems()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is AlbumDetailUiState.Success)
        assertEquals("Test Album", state.albumName)
        assertEquals(mediaItems, state.mediaItems)
    }

    @Test
    fun `state updates to Error when exception occurs`() = runTest {
        coEvery { getMediaInAlbumUseCase("test_album", AlbumType.ALL_IMAGES) } throws RuntimeException("Test error")

        viewModel.loadMediaItems()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is AlbumDetailUiState.Error)
        assertEquals("Test error", state.message)
    }
} 