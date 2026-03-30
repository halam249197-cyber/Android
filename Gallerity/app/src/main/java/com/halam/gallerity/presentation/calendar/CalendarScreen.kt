package com.halam.gallerity.presentation.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.halam.gallerity.domain.model.MediaFile
import com.halam.gallerity.presentation.home.HomeUiState
import com.halam.gallerity.presentation.home.HomeViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun CalendarScreen(
    onDayClick: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is HomeUiState.Loading -> {
            Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
        }
        is HomeUiState.Success -> {
            CalendarContent(state.media, onDayClick)
        }
        is HomeUiState.Error -> {
            Box(Modifier.fillMaxSize(), Alignment.Center) { Text(state.message) }
        }
    }
}

@Composable
fun CalendarContent(mediaFiles: List<MediaFile>, onDayClick: (Long) -> Unit) {
    val dateMap = remember(mediaFiles) {
        mediaFiles.groupBy {
            Instant.ofEpochMilli(it.dateAdded)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }
    }

    val currentMonth = YearMonth.now()
    var displayMonth by remember { mutableStateOf(currentMonth) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { displayMonth = displayMonth.minusMonths(1) }) { Text("<") }
            Text(
                text = displayMonth.format(DateTimeFormatter.ofPattern("MM / yyyy")),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Button(onClick = { displayMonth = displayMonth.plusMonths(1) }) { Text(">") }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            val days = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
            days.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxSize()
        ) {
            val firstDayOfMonth = displayMonth.atDay(1)
            val offset = firstDayOfMonth.dayOfWeek.value - 1
            val daysInMonth = displayMonth.lengthOfMonth()

            items(offset) {
                Box(modifier = Modifier.aspectRatio(1f))
            }

            items(daysInMonth) { dayIndex ->
                val date = displayMonth.atDay(dayIndex + 1)
                val mediaForDay = dateMap[date] ?: emptyList()

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(
                            if (mediaForDay.isNotEmpty()) MaterialTheme.colorScheme.primaryContainer 
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable(enabled = mediaForDay.isNotEmpty()) {
                            val timestamp = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                            onDayClick(timestamp)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${dayIndex + 1}",
                        color = if (mediaForDay.isNotEmpty()) MaterialTheme.colorScheme.onPrimaryContainer
                              else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (mediaForDay.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.error),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${mediaForDay.size}",
                                color = MaterialTheme.colorScheme.onError,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
