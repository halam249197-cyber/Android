package com.halam.gallerity.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.halam.gallerity.data.local.preferences.SecurityPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val securityPreferences: SecurityPreferences
) : ViewModel() {
    val isFirstLaunch = securityPreferences.isFirstLaunchFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    fun completeFirstLaunch() {
        viewModelScope.launch {
            securityPreferences.setFirstLaunchCompleted()
        }
    }
}
