package com.halam.gallerity.presentation.security

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.halam.gallerity.data.local.preferences.SecurityPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val securityPreferences: SecurityPreferences
) : ViewModel() {

    private val _isPinSet = MutableStateFlow<Boolean?>(null)
    val isPinSet = _isPinSet.asStateFlow()

    private val _currentInput = MutableStateFlow("")
    val currentInput = _currentInput.asStateFlow()

    private val _isUnlocked = MutableStateFlow(false)
    val isUnlocked = _isUnlocked.asStateFlow()

    private val _errorMsg = MutableStateFlow<String?>(null)
    val errorMsg = _errorMsg.asStateFlow()

    init {
        viewModelScope.launch {
            val savedPin = securityPreferences.pinCodeFlow.first()
            _isPinSet.value = savedPin != null
        }
    }

    fun onNumberPadClick(number: Int) {
        if (_currentInput.value.length < 4) {
            _currentInput.value += number
        }
    }

    fun onDeleteClick() {
        if (_currentInput.value.isNotEmpty()) {
            _currentInput.value = _currentInput.value.dropLast(1)
        }
    }

    fun onSubmitPin() {
        viewModelScope.launch {
            val input = _currentInput.value
            if (input.length < 4) return@launch

            val isSetupMode = _isPinSet.value == false

            if (isSetupMode) {
                securityPreferences.savePinCode(input)
                _isPinSet.value = true
                _currentInput.value = ""
                _isUnlocked.value = true // Tu dong thao khoa
            } else {
                val savedPin = securityPreferences.pinCodeFlow.first()
                if (savedPin == input) {
                    _errorMsg.value = null
                    _currentInput.value = ""
                    _isUnlocked.value = true
                } else {
                    _errorMsg.value = "PIN không đúng!"
                    _currentInput.value = ""
                }
            }
        }
    }

    fun setUnlockedDirectly() {
        _isUnlocked.value = true
    }
}
