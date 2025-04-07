package com.ali.foldergallery.presentation.screens.album

import android.net.Uri
import com.ali.foldergallery.domain.model.Album
import com.ali.foldergallery.domain.model.AlbumType
import com.ali.foldergallery.domain.usecases.GetAlbumsUseCase
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
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class AlbumListViewModelTest {
    private lateinit var viewModel: AlbumListViewModel
    private lateinit var getAlbumsUseCase: GetAlbumsUseCase
    private lateinit var testDispatcher: TestDispatcher

    @Before
    fun setup() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        getAlbumsUseCase = mockk()
        coEvery { getAlbumsUseCase() } returns flow { emit(emptyList()) }
        viewModel = AlbumListViewModel(getAlbumsUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        assertTrue(viewModel.uiState.value is AlbumListUiState.Loading)
    }

    @Test
    fun `loadAlbums updates state to Success when albums are loaded`() = runTest {
        val albums = listOf(
            Album(
                id = "test_album",
                name = "Test Album",
                coverUri = Uri.parse("content://media/external/images/media/1"),
                mediaCount = 5,
                albumType = AlbumType.ALL_IMAGES
            )
        )
        coEvery { getAlbumsUseCase() } returns flow { emit(albums) }

        viewModel.loadAlbums()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is AlbumListUiState.Success)
        assertEquals(albums, state.albums)
    }

    @Test
    fun `loadAlbums updates state to Error when exception occurs`() = runTest {
        coEvery { getAlbumsUseCase() } throws RuntimeException("Test error")

        viewModel.loadAlbums()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is AlbumListUiState.Error)
        assertEquals("Test error", state.message)
    }
} 