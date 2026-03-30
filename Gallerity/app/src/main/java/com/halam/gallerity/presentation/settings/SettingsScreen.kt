package com.halam.gallerity.presentation.settings

import android.content.pm.PackageManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.halam.gallerity.data.local.preferences.SecurityPreferences
import com.halam.gallerity.presentation.security.SecurityScreen
import com.halam.gallerity.presentation.security.SecurityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val securityViewModel: SecurityViewModel = hiltViewModel()
    val isPinSet by securityViewModel.isPinSet.collectAsState()
    val authMethod by securityViewModel.authMethod.collectAsState()
    val geminiApiKey by securityViewModel.geminiApiKey.collectAsState()

    var showChangePinScreen by remember { mutableStateOf(false) }
    var showResetConfirm by remember { mutableStateOf(false) }
    var showGeminiKeyDialog by remember { mutableStateOf(false) }

    val appVersion = remember {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "1.0"
        } catch (_: PackageManager.NameNotFoundException) {
            "1.0"
        }
    }

    if (showChangePinScreen) {
        SecurityScreen(
            onUnlockSuccess = {
                showChangePinScreen = false
            }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cài đặt", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // ── Security Section ──
            Text(
                text = "Bảo mật",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Surface(
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // PIN Status
                    ListItem(
                        headlineContent = { Text("Mã PIN") },
                        supportingContent = {
                            Text(
                                if (isPinSet == true) "Đã thiết lập"
                                else "Chưa thiết lập"
                            )
                        },
                        leadingContent = {
                            Icon(Icons.Default.Lock, contentDescription = null)
                        },
                        trailingContent = {
                            TextButton(onClick = { showChangePinScreen = true }) {
                                Text(if (isPinSet == true) "Đổi PIN" else "Thiết lập")
                            }
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                    // Auth Method
                    ListItem(
                        headlineContent = { Text("Phương thức mở khóa") },
                        supportingContent = {
                            Text(
                                when (authMethod) {
                                    SecurityPreferences.AUTH_FINGERPRINT -> "Vân tay / Khuôn mặt"
                                    SecurityPreferences.AUTH_PIN -> "Chỉ dùng mã PIN"
                                    else -> "Chưa chọn"
                                }
                            )
                        },
                        leadingContent = {
                            Icon(Icons.Default.Lock, contentDescription = null)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── AI Configuration ──
            Text(
                text = "Trợ lý ảo (AI)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Surface(
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                ListItem(
                    headlineContent = { Text("Gemini API Key") },
                    supportingContent = {
                        Text(
                            if (geminiApiKey.isNullOrBlank()) "Chưa thiết lập (Bắt buộc cho Chatbot)"
                            else "Đã thiết lập (••••" + geminiApiKey?.takeLast(4) + ")"
                        )
                    },
                    leadingContent = {
                        Icon(
                            if (geminiApiKey.isNullOrBlank()) Icons.Default.Warning else Icons.Default.Settings,
                            contentDescription = null,
                            tint = if (geminiApiKey.isNullOrBlank()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    modifier = Modifier.clickable { showGeminiKeyDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Storage Section ──
            Text(
                text = "Bộ nhớ",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Surface(
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                ListItem(
                    headlineContent = { Text("Xóa thùng rác") },
                    supportingContent = { Text("Dọn sạch ảnh trong Thùng rác") },
                    leadingContent = {
                        Icon(Icons.Default.Delete, contentDescription = null)
                    },
                    modifier = Modifier.clickable { showResetConfirm = true }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── About Section ──
            Text(
                text = "Thông tin",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Surface(
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    ListItem(
                        headlineContent = { Text("Phiên bản ứng dụng") },
                        supportingContent = { Text("Gallerity v$appVersion") },
                        leadingContent = {
                            Icon(Icons.Default.Info, contentDescription = null)
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    ListItem(
                        headlineContent = { Text("Công nghệ") },
                        supportingContent = { Text("Jetpack Compose • Material 3 • ML Kit • Room") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Confirm dialog for emptying trash
    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("Xóa thùng rác?") },
            text = { Text("Tất cả ảnh trong thùng rác sẽ bị xóa vĩnh viễn. Không thể hoàn tác.") },
            confirmButton = {
                Button(
                    onClick = { showResetConfirm = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Xóa hết")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text("Hủy")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Gemini API Key Dialog
    if (showGeminiKeyDialog) {
        var inputKey by remember { mutableStateOf(geminiApiKey ?: "") }
        AlertDialog(
            onDismissRequest = { showGeminiKeyDialog = false },
            title = { Text("Thiết lập API Key") },
            text = {
                Column {
                    Text("Nhập mã Gemini API Key từ Google AI Studio để kích hoạt tính năng Chatbot thông minh.")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = inputKey,
                        onValueChange = { inputKey = it },
                        label = { Text("API Key") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        securityViewModel.saveGeminiApiKey(inputKey.trim())
                        showGeminiKeyDialog = false
                    }
                ) {
                    Text("Lưu")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGeminiKeyDialog = false }) {
                    Text("Hủy")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}
