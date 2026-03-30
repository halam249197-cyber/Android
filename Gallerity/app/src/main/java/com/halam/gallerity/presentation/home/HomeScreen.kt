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
    var selectedTabIndex by remember { mutableIntStateOf(0) }
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
                        1 -> AiAlbumList(state.media.filter { !it.isSecured && !it.isTrashed })
                        2 -> PhotoGrid(state.media.filter { it.isTrashed })
                        3 -> {
                            // key(selectedTabIndex) forces recomposition when tab changes,
                            // which resets isUnlocked to false → strict lock
                            key(selectedTabIndex) {
                                SecurityScreenWrapper(state.media.filter { it.isSecured })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SecurityScreenWrapper(securedMedia: List<MediaFile>) {
    var isUnlocked by remember { mutableStateOf(false) }
    val viewModel: com.halam.gallerity.presentation.security.SecurityViewModel = hiltViewModel()

    // Every time this composable enters (tab switch resets via key()),
    // force the ViewModel to clear its stale unlock state
    LaunchedEffect(Unit) {
        viewModel.resetUnlockState()
    }

    if (isUnlocked) {
        PhotoGrid(securedMedia)
    } else {
        SecurityScreen(viewModel = viewModel, onUnlockSuccess = { isUnlocked = true })
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

/**
 * AI Album - ONLY shows ML Kit generated categories.
 * Does NOT show physical device folders.
 */
@Composable
fun AiAlbumList(mediaFiles: List<MediaFile>) {
    val aiAlbums = remember(mediaFiles) {
        val albums = mutableListOf<Pair<String, List<MediaFile>>>()

        val facePhotos = mediaFiles.filter { it.faceCount > 0 }
        if (facePhotos.isNotEmpty()) albums.add("Khuôn mặt 👤" to facePhotos)

        val naturePhotos = mediaFiles.filter {
            it.tags.contains("nature", true) ||
            it.tags.contains("plant", true) ||
            it.tags.contains("sky", true) ||
            it.tags.contains("flower", true) ||
            it.tags.contains("tree", true)
        }
        if (naturePhotos.isNotEmpty()) albums.add("Thiên nhiên 🌲" to naturePhotos)

        val animalPhotos = mediaFiles.filter {
            it.tags.contains("dog", true) ||
            it.tags.contains("cat", true) ||
            it.tags.contains("animal", true) ||
            it.tags.contains("pet", true)
        }
        if (animalPhotos.isNotEmpty()) albums.add("Thú cưng 🐶" to animalPhotos)

        val foodPhotos = mediaFiles.filter {
            it.tags.contains("food", true) ||
            it.tags.contains("meal", true) ||
            it.tags.contains("dish", true)
        }
        if (foodPhotos.isNotEmpty()) albums.add("Ẩm thực 🍜" to foodPhotos)

        val vehiclePhotos = mediaFiles.filter {
            it.tags.contains("car", true) ||
            it.tags.contains("vehicle", true) ||
            it.tags.contains("motorcycle", true)
        }
        if (vehiclePhotos.isNotEmpty()) albums.add("Phương tiện 🚗" to vehiclePhotos)

        albums.sortedByDescending { it.second.size }
    }

    if (aiAlbums.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "AI đang quét ảnh của bạn...",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Quá trình quét sẽ diễn ra khi máy rảnh hoặc đang sạc.\nVui lòng quay lại sau.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
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
        items(aiAlbums, key = { it.first }) { (albumName, files) ->
            Column(
                modifier = Modifier.clickable { /* TODO: Open AI Album Detail */ }
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
                            contentDescription = albumName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = albumName,
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
