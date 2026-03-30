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

    private val _authMethod = MutableStateFlow<String?>(null)
    val authMethod = _authMethod.asStateFlow()

    private val _currentInput = MutableStateFlow("")
    val currentInput = _currentInput.asStateFlow()

    private val _isUnlocked = MutableStateFlow(false)
    val isUnlocked = _isUnlocked.asStateFlow()

    private val _errorMsg = MutableStateFlow<String?>(null)
    val errorMsg = _errorMsg.asStateFlow()

    // After PIN is created in setup mode, show dialog asking for fingerprint
    private val _showFingerprintSetupDialog = MutableStateFlow(false)
    val showFingerprintSetupDialog = _showFingerprintSetupDialog.asStateFlow()

    init {
        viewModelScope.launch {
            val savedPin = securityPreferences.pinCodeFlow.first()
            _isPinSet.value = savedPin != null
            _authMethod.value = securityPreferences.authMethodFlow.first()
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
                // Save PIN and ask user which auth method they want
                securityPreferences.savePinCode(input)
                _isPinSet.value = true
                _currentInput.value = ""
                _showFingerprintSetupDialog.value = true
            } else {
                // Unlock mode: verify PIN
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

    fun onChooseAuthMethod(method: String) {
        viewModelScope.launch {
            securityPreferences.saveAuthMethod(method)
            _authMethod.value = method
            _showFingerprintSetupDialog.value = false
            _isUnlocked.value = true
        }
    }

    fun setUnlockedDirectly() {
        _isUnlocked.value = true
    }

    /** Reset lock state — called every time Security tab is re-entered */
    fun resetUnlockState() {
        _isUnlocked.value = false
        _currentInput.value = ""
        _errorMsg.value = null
    }
}
