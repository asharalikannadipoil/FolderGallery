package com.ali.foldergallery.presentation.screens.albumdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ali.foldergallery.domain.model.AlbumType
import com.ali.foldergallery.domain.model.MediaItem
import com.ali.foldergallery.domain.usecases.GetMediaInAlbumUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    private val getMediaInAlbumUseCase: GetMediaInAlbumUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val albumId: String = checkNotNull(savedStateHandle["albumId"])
    private val albumName: String = checkNotNull(savedStateHandle["albumName"])
    private val albumType: AlbumType = AlbumType.valueOf(checkNotNull(savedStateHandle["albumType"]))

    private val _uiState = MutableStateFlow<AlbumDetailUiState>(AlbumDetailUiState.Loading)
    val uiState: StateFlow<AlbumDetailUiState> = _uiState

    init {
        loadMediaItems()
    }

    internal fun loadMediaItems() {
        viewModelScope.launch {
            try {
                _uiState.value = AlbumDetailUiState.Loading
                getMediaInAlbumUseCase(albumId, albumType).collectLatest { mediaItems ->
                    _uiState.value = AlbumDetailUiState.Success(
                        albumName = albumName,
                        mediaItems = mediaItems
                    )
                }
            } catch (e: Exception) {
                _uiState.value = AlbumDetailUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class AlbumDetailUiState {
    data object Loading : AlbumDetailUiState()
    data class Success(
        val albumName: String,
        val mediaItems: List<MediaItem>
    ) : AlbumDetailUiState()
    data class Error(val message: String) : AlbumDetailUiState()
}