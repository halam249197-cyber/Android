package com.halam.gallerity.presentation.ai

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.halam.gallerity.data.local.preferences.SecurityPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val securityPreferences: SecurityPreferences
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping = _isTyping.asStateFlow()

    private val _apiKey = MutableStateFlow<String?>("")
    val apiKey = _apiKey.asStateFlow()

    private var generativeModel: GenerativeModel? = null

    // This hidden prompt tells Gemini how to act as an offline Gallery Assistant
    private val systemPrompt = """
        Bạn là Trợ lý AI thông minh tích hợp trong ứng dụng xem ảnh có tên "Gallerity". 
        Nhiệm vụ của bạn:
        1. Trả lời các câu hỏi tâm sự, cung cấp thông tin như một trợ lý ảo bình thường.
        2. Nếu người dùng ra lệnh "Tìm ảnh" (ví dụ: tìm ảnh con chó, lọc ảnh thiên nhiên, muốn xem hình bữa ăn):
           Bạn PHẢI trả lời chính xác theo định dạng JSON phía dưới (KHÔNG BAO GỒM BẤT KỲ VĂN BẢN NÀO KHÁC BÊN NGOÀI JSON) để ứng dụng có thể parse:
           {"action":"search", "keywords":["dog", "animal", "pet"]}
           Keywords là mảng các từ khóa tiếng Anh liên quan đến nội dung cần tìm.
        3. Nếu người dùng ra lệnh "Vẽ ảnh", "Tạo ảnh" (ví dụ: tạo ảnh con mèo):
           Bạn PHẢI trả lời theo định dạng JSON:
           {"action":"generate", "prompt":"Từ khóa mô tả bằng tiếng anh để vẽ ảnh"}
    """.trimIndent()

    init {
        viewModelScope.launch {
            securityPreferences.geminiApiKeyFlow.collectLatest { key ->
                _apiKey.value = key
                if (!key.isNullOrBlank()) {
                    generativeModel = GenerativeModel(
                        modelName = "gemini-1.5-flash",
                        apiKey = key
                    )
                }
            }
        }
    }

    fun sendMessage(userText: String, imageBitmap: Bitmap? = null, onNavigateToSearch: (String) -> Unit) {
        val currentModel = generativeModel
        if (currentModel == null) {
            _messages.value += ChatMessage(text = "Lỗi: Không tìm thấy API Key.", isUser = false)
            return
        }

        val userMessage = ChatMessage(text = userText, isUser = true, attachment = imageBitmap)
        _messages.value += userMessage
        _isTyping.value = true

        viewModelScope.launch {
            try {
                // For this demo, prepend the system prompt implicitly to ensure JSON enforcement
                val fullText = "$systemPrompt\n\n---\nNghiêm ngặt tuân thủ bộ luật trên, phản hồi yêu cầu sau:\n$userText"
                
                val response = if (imageBitmap != null) {
                    currentModel.generateContent(
                        content {
                            image(imageBitmap)
                            text(fullText)
                        }
                    )
                } else {
                    val chat = currentModel.startChat()
                    chat.sendMessage(fullText)
                }
                
                val responseText = response.text ?: ""

                // NLP JSON Parsing fallback
                if (responseText.contains("\"action\"") && responseText.contains("\"search\"")) {
                    val keywordsStart = responseText.indexOf("[")
                    val keywordsEnd = responseText.lastIndexOf("]")
                    if (keywordsStart != -1 && keywordsEnd != -1) {
                        val keywords = responseText.substring(keywordsStart + 1, keywordsEnd).replace("\"", "").trim()
                        _messages.value += ChatMessage(text = "Đã hiểu! Mình đang đưa bạn tới những bức ảnh: $keywords", isUser = false)
                        onNavigateToSearch(keywords)
                    } else {
                        _messages.value += ChatMessage(text = "Mình đã hiểu ý, nhưng không trích xuất được từ khóa tìm kiếm.", isUser = false)
                    }
                } else if (responseText.contains("\"action\"") && responseText.contains("\"generate\"")) {
                    _messages.value += ChatMessage(text = "Hệ thống AI vẽ hình đang trong giai đoạn phát triển và sẽ được thêm vào App sau nhé!", isUser = false)
                } else {
                    _messages.value += ChatMessage(text = responseText, isUser = false)
                }

            } catch (e: Exception) {
                _messages.value += ChatMessage(text = "Xin lỗi, đã có lỗi kết nối với Gemini: ${e.localizedMessage}", isUser = false)
            } finally {
                _isTyping.value = false
            }
        }
    }
}
