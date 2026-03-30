package com.halam.gallerity.presentation.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.halam.gallerity.presentation.security.SecurityScreen

@Composable
fun OnboardingScreen(onFinishOnboarding: () -> Unit) {
    var showSetupPin by remember { mutableStateOf(false) }

    if (showSetupPin) {
        // Reuse Security component to define PIN flawlessly
        SecurityScreen(onUnlockSuccess = {
            onFinishOnboarding() 
        })
    } else {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Bảo mật thư viện",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Bạn có muốn thiết lập Mật khẩu PIN hoặc Vân tay cho \"Thư mục ẩn\" ngay bây giờ không?",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(64.dp))

                Button(
                    onClick = { showSetupPin = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("Thiết lập mã PIN / Vân tay")
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(
                    onClick = { onFinishOnboarding() }
                ) {
                    Text("Bỏ qua, tôi sẽ cài đặt sau")
                }
            }
        }
    }
}
