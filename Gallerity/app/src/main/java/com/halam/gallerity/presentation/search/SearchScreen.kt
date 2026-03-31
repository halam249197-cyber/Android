package com.halam.gallerity.presentation.search

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import com.halam.gallerity.presentation.home.HomeUiState
import com.halam.gallerity.presentation.home.HomeViewModel
import com.halam.gallerity.presentation.home.PhotoGrid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    query: String,
    onBack: () -> Unit,
    onImageClick: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Split the comma-separated or space-separated keywords passed from NLP
    val keywords = remember(query) {
        query.lowercase()
            .split(Regex("[,\\s]+"))
            .map { it.trim() }
            .filter { it.isNotBlank() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Kết quả: $query",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.SemiBold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is HomeUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is HomeUiState.Error -> Text("Lỗi: ${state.message}", modifier = Modifier.align(Alignment.Center))
                is HomeUiState.Success -> {
                    // Start filtering based on keywords
                    val searchResults = state.media.filter { media ->
                        // Reject secured/trashed items from casual search
                        if (media.isSecured || media.isTrashed) return@filter false
                        
                        val tagsLower = media.tags.lowercase()
                        val nameLower = media.name.lowercase()
                        val folderLower = media.folderName.lowercase()
                        
                        // If any keyword matches tags, filename, or folder
                        keywords.any { k ->
                            tagsLower.contains(k) || nameLower.contains(k) || folderLower.contains(k)
                        }
                    }
                    
                    if (searchResults.isEmpty()) {
                        Text(
                            text = "Không tìm thấy ảnh nào khớp với mô tả.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        PhotoGrid(mediaFiles = searchResults, onImageClick = onImageClick)
                    }
                }
            }
        }
    }
}
