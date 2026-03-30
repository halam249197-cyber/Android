package com.halam.gallerity.presentation.calendar

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.halam.gallerity.domain.model.MediaFile
import com.halam.gallerity.presentation.home.HomeUiState
import com.halam.gallerity.presentation.home.HomeViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
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

    val today = LocalDate.now()
    var displayMonth by remember { mutableStateOf(YearMonth.now()) }
    var showMonthYearPicker by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // ── Header: Month/Year Navigation ──
        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 2.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { displayMonth = displayMonth.minusMonths(1) }) {
                    Icon(
                        Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Tháng trước",
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Tappable month/year title → opens picker
                Surface(
                    modifier = Modifier.clickable { showMonthYearPicker = true },
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "Tháng ${displayMonth.monthValue}, ${displayMonth.year}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)
                    )
                }

                IconButton(onClick = { displayMonth = displayMonth.plusMonths(1) }) {
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = "Tháng sau",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ── Day of Week Header ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            val dayLabels = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
            dayLabels.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (day == "CN") MaterialTheme.colorScheme.error
                           else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // ── Calendar Grid ──
        val firstDayOfMonth = displayMonth.atDay(1)
        val offset = firstDayOfMonth.dayOfWeek.value - 1  // Mon=0
        val daysInMonth = displayMonth.lengthOfMonth()
        val totalCells = offset + daysInMonth

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Empty offset cells
            items(offset) {
                Box(modifier = Modifier.aspectRatio(1f))
            }

            // Day cells
            items(daysInMonth) { dayIndex ->
                val dayNumber = dayIndex + 1
                val date = displayMonth.atDay(dayNumber)
                val mediaForDay = dateMap[date] ?: emptyList()
                val isToday = date == today
                val hasPhotos = mediaForDay.isNotEmpty()

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(3.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .then(
                            if (isToday) Modifier.border(
                                2.dp,
                                MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(12.dp)
                            ) else Modifier
                        )
                        .background(
                            when {
                                hasPhotos && mediaForDay.size >= 10 -> Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.tertiary
                                    )
                                )
                                hasPhotos -> Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                                else -> Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                    )
                                )
                            }
                        )
                        .clickable(enabled = hasPhotos) {
                            val timestamp = date
                                .atStartOfDay(ZoneId.systemDefault())
                                .toInstant()
                                .toEpochMilli()
                            onDayClick(timestamp)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$dayNumber",
                            fontWeight = if (isToday) FontWeight.ExtraBold else FontWeight.Normal,
                            fontSize = 16.sp,
                            color = when {
                                hasPhotos && mediaForDay.size >= 10 -> MaterialTheme.colorScheme.onPrimary
                                isToday -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                        if (hasPhotos) {
                            Text(
                                text = "${mediaForDay.size}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (mediaForDay.size >= 10) MaterialTheme.colorScheme.onPrimary
                                       else MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }

    // ── Month/Year Picker Dialog ──
    if (showMonthYearPicker) {
        MonthYearPickerDialog(
            currentMonth = displayMonth,
            onDismiss = { showMonthYearPicker = false },
            onMonthSelected = { selected ->
                displayMonth = selected
                showMonthYearPicker = false
            }
        )
    }
}

@Composable
fun MonthYearPickerDialog(
    currentMonth: YearMonth,
    onDismiss: () -> Unit,
    onMonthSelected: (YearMonth) -> Unit
) {
    var selectedYear by remember { mutableIntStateOf(currentMonth.year) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { selectedYear-- }) {
                    Icon(Icons.Default.KeyboardArrowLeft, "Năm trước")
                }
                Text(
                    text = "$selectedYear",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { selectedYear++ }) {
                    Icon(Icons.Default.KeyboardArrowRight, "Năm sau")
                }
            }
        },
        text = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(12) { monthIndex ->
                    val month = monthIndex + 1
                    val yearMonth = YearMonth.of(selectedYear, month)
                    val isCurrentSelection = yearMonth == currentMonth

                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onMonthSelected(yearMonth) },
                        color = if (isCurrentSelection) MaterialTheme.colorScheme.primary
                               else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Thg $month",
                            modifier = Modifier.padding(vertical = 14.dp),
                            textAlign = TextAlign.Center,
                            fontWeight = if (isCurrentSelection) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCurrentSelection) MaterialTheme.colorScheme.onPrimary
                                   else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Đóng")
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}
