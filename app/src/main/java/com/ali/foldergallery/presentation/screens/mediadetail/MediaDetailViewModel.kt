package com.ali.foldergallery.presentation.screens.mediadetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ali.foldergallery.domain.model.MediaItem
import com.ali.foldergallery.domain.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaDetailViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val mediaId: Long = checkNotNull(savedStateHandle["mediaId"])

    private val _uiState = MutableStateFlow<MediaDetailUiState>(MediaDetailUiState.Loading)
    val uiState: StateFlow<MediaDetailUiState> = _uiState

    init {
        loadMediaItem()
    }

    private fun loadMediaItem() {
        viewModelScope.launch {
            try {
                _uiState.value = MediaDetailUiState.Loading
                val mediaItem = mediaRepository.getMediaItem(mediaId)
                if (mediaItem != null) {
                    _uiState.value = MediaDetailUiState.Success(mediaItem)
                } else {
                    _uiState.value = MediaDetailUiState.Error("Media item not found")
                }
            } catch (e: Exception) {
                _uiState.value = MediaDetailUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class MediaDetailUiState {
    data object Loading : MediaDetailUiState()
    data class Success(val mediaItem: MediaItem) : MediaDetailUiState()
    data class Error(val message: String) : MediaDetailUiState()
}