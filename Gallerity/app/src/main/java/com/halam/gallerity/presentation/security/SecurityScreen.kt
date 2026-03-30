package com.halam.gallerity.presentation.security

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SecurityScreen(
    viewModel: SecurityViewModel = hiltViewModel(),
    onUnlockSuccess: () -> Unit
) {
    val isPinSet by viewModel.isPinSet.collectAsState()
    val currentInput by viewModel.currentInput.collectAsState()
    val isUnlocked by viewModel.isUnlocked.collectAsState()
    val errorMsg by viewModel.errorMsg.collectAsState()
    
    val context = LocalContext.current

    LaunchedEffect(isUnlocked) {
        if (isUnlocked) onUnlockSuccess()
    }

    LaunchedEffect(isPinSet) {
        if (isPinSet == true && !isUnlocked) {
            val biometricManager = BiometricPromptManager(context as FragmentActivity)
            biometricManager.showBiometricPrompt(
                title = "Mở khóa Thư mục Ẩn",
                description = "Chạm vân tay hoặc dùng khuôn mặt để xem ảnh riêng tư",
                onSuccess = { viewModel.setUnlockedDirectly() },
                onFallbackToPin = { },
                onError = { }
            )
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Lock",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = if (isPinSet == false) "TẠO MÃ PIN 4 SỐ" else "NHẬP MÃ PIN",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            if (errorMsg != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = errorMsg!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                for (i in 0 until 4) {
                    val isFilled = i < currentInput.length
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(
                                if (isFilled) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                    )
                }
            }

            LaunchedEffect(currentInput) {
                if (currentInput.length == 4) {
                    viewModel.onSubmitPin()
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            val numbers = listOf(
                listOf(1, 2, 3),
                listOf(4, 5, 6),
                listOf(7, 8, 9)
            )
            
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                for (row in numbers) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (number in row) {
                            NumberButton(number = number.toString()) {
                                viewModel.onNumberPadClick(number)
                            }
                        }
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(72.dp), contentAlignment = Alignment.Center) {
                        if (isPinSet == true) {
                            IconButton(onClick = {
                                val biometricManager = BiometricPromptManager(context as FragmentActivity)
                                biometricManager.showBiometricPrompt(
                                    "Mở khóa Thư mục Ẩn", "Xác thực danh tính",
                                    onSuccess = { viewModel.setUnlockedDirectly() },
                                    onFallbackToPin = { }, onError = { }
                                )
                            }) {
                                Icon(Icons.Default.Lock, contentDescription = "Biometric", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                    NumberButton(number = "0") { viewModel.onNumberPadClick(0) }
                    Box(modifier = Modifier.size(72.dp), contentAlignment = Alignment.Center) {
                        IconButton(onClick = { viewModel.onDeleteClick() }) {
                            Icon(Icons.Default.Clear, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onBackground)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NumberButton(number: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = number, fontSize = 28.sp, fontWeight = FontWeight.Medium)
    }
}
