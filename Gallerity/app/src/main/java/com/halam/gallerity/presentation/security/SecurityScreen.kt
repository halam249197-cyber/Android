package com.halam.gallerity.presentation.security

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.halam.gallerity.data.local.preferences.SecurityPreferences

@Composable
fun SecurityScreen(
    viewModel: SecurityViewModel = hiltViewModel(),
    onUnlockSuccess: () -> Unit
) {
    val isPinSet by viewModel.isPinSet.collectAsState()
    val authMethod by viewModel.authMethod.collectAsState()
    val currentInput by viewModel.currentInput.collectAsState()
    val isUnlocked by viewModel.isUnlocked.collectAsState()
    val errorMsg by viewModel.errorMsg.collectAsState()
    val showFingerprintDialog by viewModel.showFingerprintSetupDialog.collectAsState()

    val context = LocalContext.current

    // When unlocked, notify parent
    LaunchedEffect(isUnlocked) {
        if (isUnlocked) onUnlockSuccess()
    }

    // Auto-launch biometric ONLY when authMethod is "fingerprint" and PIN is set
    LaunchedEffect(isPinSet, authMethod) {
        if (isPinSet == true && !isUnlocked && authMethod == SecurityPreferences.AUTH_FINGERPRINT) {
            val biometricManager = BiometricPromptManager(context as FragmentActivity)
            biometricManager.showBiometricPrompt(
                title = "Mở khóa Thư mục Ẩn",
                description = "Chạm vân tay hoặc dùng khuôn mặt để xem ảnh riêng tư",
                onSuccess = { viewModel.setUnlockedDirectly() },
                onFallbackToPin = { /* User taps "Use PIN code" → just show PIN pad (already shown) */ },
                onError = { /* Silently fail, user can type PIN */ }
            )
        }
    }

    // Dialog: After PIN setup, ask user which auth method
    if (showFingerprintDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onChooseAuthMethod(SecurityPreferences.AUTH_PIN) },
            title = { Text("Phương thức xác thực", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Bạn muốn dùng Vân tay/Khuôn mặt để mở khóa thư mục ẩn không?\n\n" +
                    "Nếu chọn \"Không\", bạn sẽ chỉ dùng mã PIN vừa tạo.",
                    textAlign = TextAlign.Start
                )
            },
            confirmButton = {
                Button(onClick = { viewModel.onChooseAuthMethod(SecurityPreferences.AUTH_FINGERPRINT) }) {
                    Text("Dùng Vân tay")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onChooseAuthMethod(SecurityPreferences.AUTH_PIN) }) {
                    Text("Chỉ dùng PIN")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
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

            // 4 PIN dots
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
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

            // Auto-submit when 4 digits entered
            LaunchedEffect(currentInput) {
                if (currentInput.length == 4) {
                    viewModel.onSubmitPin()
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Number pad
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
                // Bottom row: [Biometric] [0] [Delete]
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Biometric button: only show if authMethod is fingerprint AND PIN is already set
                    Box(modifier = Modifier.size(72.dp), contentAlignment = Alignment.Center) {
                        if (isPinSet == true && authMethod == SecurityPreferences.AUTH_FINGERPRINT) {
                            IconButton(onClick = {
                                val biometricManager = BiometricPromptManager(context as FragmentActivity)
                                biometricManager.showBiometricPrompt(
                                    "Mở khóa Thư mục Ẩn", "Xác thực danh tính",
                                    onSuccess = { viewModel.setUnlockedDirectly() },
                                    onFallbackToPin = { },
                                    onError = { }
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
