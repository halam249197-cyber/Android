package com.halam.gallerity.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.halam.gallerity.domain.model.MediaFile
import com.halam.gallerity.domain.usecase.GetMediaFilesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMediaFilesUseCase: GetMediaFilesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadMediaFiles()
    }

    private fun loadMediaFiles() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            getMediaFilesUseCase()
                .catch { e ->
                    _uiState.value = HomeUiState.Error(e.message ?: "Unknown Error")
                }
                .collect { files ->
                    _uiState.value = HomeUiState.Success(files)
                }
        }
    }
}

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val media: List<MediaFile>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
