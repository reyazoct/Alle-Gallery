package com.reyaz.allegallery

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ImagesViewModel(app: Application) : AndroidViewModel(app) {
    private val _allUri = MutableStateFlow<List<Uri>>(emptyList())
    val allUri = _allUri.asStateFlow()

    private val _currentSelectedPosition = MutableStateFlow(0)

    @OptIn(FlowPreview::class)
    val currentSelectedPosition = _currentSelectedPosition.debounce(50)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0,
        )

    fun updateUriList(uriList: List<Uri>) {
        viewModelScope.launch {
            _allUri.emit(uriList)
        }
    }

    fun onSelect(position: Int) {
        viewModelScope.launch {
            _currentSelectedPosition.emit(position)
        }
    }
}