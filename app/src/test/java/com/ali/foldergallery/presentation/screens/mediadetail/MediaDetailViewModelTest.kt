package com.ali.foldergallery.presentation.screens.mediadetail

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import com.ali.foldergallery.domain.model.MediaItem
import com.ali.foldergallery.domain.repository.MediaRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class MediaDetailViewModelTest {
    private lateinit var viewModel: MediaDetailViewModel
    private lateinit var mediaRepository: MediaRepository
    private lateinit var testDispatcher: TestDispatcher

    @Before
    fun setup() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        mediaRepository = mockk()
        coEvery { mediaRepository.getMediaItem(1L) } returns null
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        viewModel = MediaDetailViewModel(
            mediaRepository = mediaRepository,
            savedStateHandle = SavedStateHandle().apply {
                set("mediaId", 1L)
            }
        )

        assertTrue(viewModel.uiState.value is MediaDetailUiState.Loading)
    }

    @Test
    fun `state updates to Success when media item is found`() = runTest {
        val mediaItem = MediaItem.Image(
            id = 1L,
            uri = Uri.parse("content://media/external/images/media/1"),
            name = "test.jpg",
            path = "/test/path/test.jpg",
            dateAdded = Date(),
            size = 1024L,
            width = 1920,
            height = 1080
        )
        coEvery { mediaRepository.getMediaItem(1L) } returns mediaItem

        viewModel = MediaDetailViewModel(
            mediaRepository = mediaRepository,
            savedStateHandle = SavedStateHandle().apply {
                set("mediaId", 1L)
            }
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is MediaDetailUiState.Success)
        assertEquals(mediaItem, state.mediaItem)
    }

    @Test
    fun `state updates to Error when media item is not found`() = runTest {
        coEvery { mediaRepository.getMediaItem(1L) } returns null

        viewModel = MediaDetailViewModel(
            mediaRepository = mediaRepository,
            savedStateHandle = SavedStateHandle().apply {
                set("mediaId", 1L)
            }
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is MediaDetailUiState.Error)
        assertEquals("Media item not found", state.message)
    }

    @Test
    fun `state updates to Error when exception occurs`() = runTest {
        coEvery { mediaRepository.getMediaItem(1L) } throws RuntimeException("Test error")

        viewModel = MediaDetailViewModel(
            mediaRepository = mediaRepository,
            savedStateHandle = SavedStateHandle().apply {
                set("mediaId", 1L)
            }
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is MediaDetailUiState.Error)
        assertEquals("Test error", state.message)
    }
} 