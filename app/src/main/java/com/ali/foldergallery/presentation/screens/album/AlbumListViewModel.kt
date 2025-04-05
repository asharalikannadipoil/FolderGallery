package com.ali.foldergallery.presentation.screens.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ali.foldergallery.domain.model.Album
import com.ali.foldergallery.domain.usecases.GetAlbumsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumListViewModel@Inject constructor(
    private val getAlbumsUseCase: GetAlbumsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AlbumListUiState>(AlbumListUiState.Loading)
    val uiState: StateFlow<AlbumListUiState> = _uiState

    init {
        loadAlbums()
    }

    private fun loadAlbums() {
        viewModelScope.launch {
            try {
                _uiState.value = AlbumListUiState.Loading
                getAlbumsUseCase().collectLatest { albums ->
                    _uiState.value = AlbumListUiState.Success(albums)
                }
            } catch (e: Exception) {
                _uiState.value = AlbumListUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class AlbumListUiState {
    data object Loading : AlbumListUiState()
    data class Success(val albums: List<Album>) : AlbumListUiState()
    data class Error(val message: String) : AlbumListUiState()
}

