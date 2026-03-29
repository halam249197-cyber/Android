package com.halam.gallerity.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.halam.gallerity.domain.model.MediaFile
import com.halam.gallerity.presentation.security.SecurityScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Tất cả", "Album (AI)", "Thùng rác", "Bảo mật")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gallerity", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: AI Chatbot in Phase 4 */ },
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(text = "✨", modifier = Modifier.padding(16.dp))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 16.dp,
                divider = {},
                indicator = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    val isSelected = selectedTabIndex == index
                    Tab(
                        selected = isSelected,
                        onClick = { selectedTabIndex = index },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                    ) {
                        Text(
                            text = title,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is HomeUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Lỗi: ${state.message}", color = MaterialTheme.colorScheme.error)
                    }
                }
                is HomeUiState.Success -> {
                    when (selectedTabIndex) {
                        0 -> PhotoGrid(state.media.filter { !it.isSecured && !it.isTrashed })
                        1 -> AlbumList(state.media.filter { !it.isSecured && !it.isTrashed })
                        2 -> PhotoGrid(state.media.filter { it.isTrashed })
                        3 -> SecurityScreenWrapper(state.media.filter { it.isSecured })
                    }
                }
            }
        }
    }
}

@Composable
fun SecurityScreenWrapper(securedMedia: List<MediaFile>) {
    var isUnlocked by remember { mutableStateOf(false) }

    if (isUnlocked) {
        PhotoGrid(securedMedia)
    } else {
        SecurityScreen(onUnlockSuccess = { isUnlocked = true })
    }
}

@Composable
fun PhotoGrid(mediaFiles: List<MediaFile>) {
    if (mediaFiles.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Không có ảnh nào", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 100.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(mediaFiles, key = { it.id }) { media ->
            AsyncImage(
                model = media.uri,
                contentDescription = media.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { /* TODO: Open Image Details Phase 3 */ }
            )
        }
    }
}

@Composable
fun AlbumList(mediaFiles: List<MediaFile>) {
    val folders = mutableMapOf<String, List<MediaFile>>()
    
    // Core Folders từ máy tính
    mediaFiles.groupBy { it.folderName }.forEach { (name, files) ->
        folders[name] = files
    }

    // AI Labeling Albums từ sức mạnh của ML Kit
    val facePhotos = mediaFiles.filter { it.faceCount > 0 }
    if (facePhotos.isNotEmpty()) folders["Khuôn mặt 👤"] = facePhotos

    val naturePhotos = mediaFiles.filter { it.tags.contains("nature", true) || it.tags.contains("plant", true) || it.tags.contains("sky", true) }
    if (naturePhotos.isNotEmpty()) folders["Thiên nhiên 🌲"] = naturePhotos

    val animalPhotos = mediaFiles.filter { it.tags.contains("dog", true) || it.tags.contains("cat", true) || it.tags.contains("animal", true) }
    if (animalPhotos.isNotEmpty()) folders["Thú cưng 🐶"] = animalPhotos

    if (folders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Chưa có album nào", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        val folderList = folders.entries.toList().sortedByDescending { it.value.size }
        items(folderList, key = { it.key }) { (folderName, files) ->
            Column(
                modifier = Modifier.clickable { /* TODO: Open Folder */ }
            ) {
                val coverImage = files.firstOrNull()?.uri
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    if (coverImage != null) {
                        AsyncImage(
                            model = coverImage,
                            contentDescription = folderName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = folderName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${files.size} ảnh",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
