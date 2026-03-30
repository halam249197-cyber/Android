package com.halam.gallerity.presentation.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.halam.gallerity.presentation.home.HomeUiState
import com.halam.gallerity.presentation.home.HomeViewModel
import com.halam.gallerity.presentation.home.PhotoGrid
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayPhotosScreen(
    timestamp: Long,
    onBack: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val targetDate = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(targetDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val state = uiState) {
                is HomeUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is HomeUiState.Error -> Text(state.message)
                is HomeUiState.Success -> {
                    val filteredMedia = state.media.filter {
                        val mediaDate = Instant.ofEpochMilli(it.dateAdded).atZone(ZoneId.systemDefault()).toLocalDate()
                        mediaDate == targetDate
                    }
                    PhotoGrid(filteredMedia)
                }
            }
        }
    }
}
