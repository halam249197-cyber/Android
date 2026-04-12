package com.halam.gallerity.presentation.calendar

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.hilt.navigation.compose.hiltViewModel
import com.halam.gallerity.domain.model.MediaFile
import com.halam.gallerity.presentation.home.HomeUiState
import com.halam.gallerity.presentation.home.HomeViewModel
import com.halam.gallerity.presentation.home.PhotoGrid
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CalendarScreen(
    onDayClick: (Long) -> Unit,
    onMediaClick: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val displayMonth by viewModel.displayMonth.collectAsState()

    var showMonthYearPicker by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
            }
            is HomeUiState.Success -> {
                CalendarContent(
                    mediaFiles = state.media,
                    selectedDate = selectedDate,
                    displayMonth = displayMonth,
                    onDateSelected = { viewModel.updateSelectedDate(it) },
                    onMonthChange = { viewModel.updateDisplayMonth(it) },
                    onDayClick = onDayClick,
                    onMediaClick = onMediaClick,
                    onOpenMonthPicker = { showMonthYearPicker = true }
                )
            }
            is HomeUiState.Error -> {
                Box(Modifier.fillMaxSize(), Alignment.Center) { Text(state.message) }
            }
        }
    }

    if (showMonthYearPicker) {
        MonthYearPickerDialog(
            currentMonth = displayMonth,
            onDismiss = { showMonthYearPicker = false },
            onMonthSelected = { selected ->
                viewModel.updateDisplayMonth(selected)
                showMonthYearPicker = false
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarContent(
    mediaFiles: List<MediaFile>,
    selectedDate: LocalDate,
    displayMonth: YearMonth,
    onDateSelected: (LocalDate) -> Unit,
    onMonthChange: (YearMonth) -> Unit,
    onDayClick: (Long) -> Unit,
    onMediaClick: (Long) -> Unit,
    onOpenMonthPicker: () -> Unit
) {
    val dateMap = remember(mediaFiles) {
        mediaFiles.groupBy {
            Instant.ofEpochMilli(it.dateAdded)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }
    }

    val today = remember { LocalDate.now() }
    
    // Page state synchronized with VM displayMonth
    val pagerState = rememberPagerState(
        initialPage = (displayMonth.year - 2000) * 12 + displayMonth.monthValue - 1,
        pageCount = { (2100 - 2000) * 12 }
    )

    // Sync VM when pager scrolls
    LaunchedEffect(pagerState.currentPage) {
        val totalMonths = pagerState.currentPage
        val year = 2000 + totalMonths / 12
        val month = totalMonths % 12 + 1
        val newMonth = YearMonth.of(year, month)
        if (newMonth != displayMonth) {
            onMonthChange(newMonth)
        }
    }

    // Sync pager when VM month changes (picker/buttons)
    LaunchedEffect(displayMonth) {
        val targetPage = (displayMonth.year - 2000) * 12 + displayMonth.monthValue - 1
        if (pagerState.currentPage != targetPage) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 0.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onMonthChange(displayMonth.minusMonths(1)) }) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Tháng trước",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                TextButton(onClick = onOpenMonthPicker) {
                    Text(
                        text = "Tháng ${displayMonth.monthValue}, ${displayMonth.year}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                IconButton(onClick = { onMonthChange(displayMonth.plusMonths(1)) }) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Tháng sau",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Calendar Grid Section (Independent Cells)
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().height(290.dp), // Lowered height
            verticalAlignment = Alignment.Top
        ) { page ->
            val pageYear = 2000 + page / 12
            val pageMonth = page % 12 + 1
            val monthToDisplay = YearMonth.of(pageYear, pageMonth)

            CalendarGridSection(
                displayMonth = monthToDisplay,
                selectedDate = selectedDate,
                dateMap = dateMap,
                today = today,
                onDateSelected = onDateSelected
            )
        }

        // Photo Preview Section
        val photosInSelectedDay = remember(selectedDate, dateMap) {
            (dateMap[selectedDate] ?: emptyList()).sortedBy { it.dateAdded }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                )
                .padding(top = 16.dp)
        ) {
            
            Box(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp)) {
                if (photosInSelectedDay.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "Không có kỉ niệm...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    PhotoGrid(
                        mediaFiles = photosInSelectedDay,
                        onImageClick = onMediaClick
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarGridSection(
    displayMonth: YearMonth,
    selectedDate: LocalDate,
    dateMap: Map<LocalDate, List<MediaFile>>,
    today: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val firstDayOfMonth = displayMonth.atDay(1)
    val offset = firstDayOfMonth.dayOfWeek.value - 1
    val daysInMonth = displayMonth.lengthOfMonth()

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        // Week Header
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            val dayLabels = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
            dayLabels.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (day == "CN") MaterialTheme.colorScheme.error 
                           else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Grid with independent cells
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(250.dp),
            userScrollEnabled = false,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(offset) { 
                Box(modifier = Modifier.aspectRatio(1f)) 
            }

            items(daysInMonth) { index ->
                val day = index + 1
                val date = displayMonth.atDay(day)
                val mediaFiles = dateMap[date] ?: emptyList()
                val isToday = date == today
                val isSelected = date == selectedDate
                val hasPhotos = mediaFiles.isNotEmpty()

                Card(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clickable { onDateSelected(date) },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            isSelected -> MaterialTheme.colorScheme.primaryContainer
                            isToday -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)
                            else -> MaterialTheme.colorScheme.surface
                        }
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(4.dp)) {
                        Text(
                            text = "$day",
                            modifier = Modifier.align(Alignment.TopStart),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (isSelected || isToday) FontWeight.Black else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.primary 
                                   else MaterialTheme.colorScheme.onSurface
                        )
                        
                        if (hasPhotos) {
                            Text(
                                text = "${mediaFiles.size}",
                                modifier = Modifier.align(Alignment.BottomEnd),
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.primary 
                                       else MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MonthYearPickerDialog(
    currentMonth: YearMonth,
    onDismiss: () -> Unit,
    onMonthSelected: (YearMonth) -> Unit
) {
    var selectedYear by remember { mutableIntStateOf(currentMonth.year) }
    var selectedMonth by remember { mutableIntStateOf(currentMonth.monthValue) }

    val years = remember { (2000..2100).toList() }
    val months = remember { (1..12).toList() }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { 
                onMonthSelected(YearMonth.of(selectedYear, selectedMonth)) 
            }) {
                Text("OK", fontWeight = FontWeight.Black, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        title = {
            Text(
                "Chọn thời gian", 
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                )

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        WheelPicker(
                            items = years,
                            initialItem = currentMonth.year,
                            onItemSelected = { selectedYear = it },
                            label = { it.toString() }
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(60.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                    )

                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        WheelPicker(
                            items = months,
                            initialItem = currentMonth.monthValue,
                            onItemSelected = { selectedMonth = it },
                            label = { "Tháng $it" }
                        )
                    }
                }
            }
        },
        shape = RoundedCornerShape(32.dp),
        tonalElevation = 0.dp,
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> WheelPicker(
    items: List<T>,
    initialItem: T,
    onItemSelected: (T) -> Unit,
    label: @Composable (T) -> String
) {
    val itemHeight = 48.dp
    val visibleItems = 5
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = items.indexOf(initialItem).coerceAtLeast(0)
    )
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val centerIndex = listState.firstVisibleItemIndex
            if (centerIndex in items.indices) {
                onItemSelected(items[centerIndex])
            }
        }
    }

    Box(
        modifier = Modifier
            .height(itemHeight * visibleItems)
            .fillMaxWidth()
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = snapFlingBehavior,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = itemHeight * 2),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(items.size) { index ->
                val item = items[index]
                val isSelected = listState.firstVisibleItemIndex == index
                
                val alphaState = animateFloatAsState(
                    targetValue = if (isSelected) 1f else 0.35f,
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                    label = "alpha"
                )
                val alpha = alphaState.value

                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    val finalFontSize = if (isSelected) 24.sp else 18.sp
                    Text(
                        text = label(item),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = finalFontSize
                        ),
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                        color = if (isSelected) MaterialTheme.colorScheme.primary 
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to MaterialTheme.colorScheme.surface,
                        0.25f to Color.Transparent,
                        0.75f to Color.Transparent,
                        1f to MaterialTheme.colorScheme.surface
                    )
                )
        )
    }
}
