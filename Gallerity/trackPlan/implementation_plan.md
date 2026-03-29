# 📱 Implementation Plan: Smart AI Gallery App

Ứng dụng thư viện ảnh thông minh tích hợp AI Assistant, được thiết kế theo chuẩn **Material Design 3** và tối ưu cho **Android 14 - 16**.

## 1. Kiến trúc tổng thể (Architecture)
Ứng dụng sẽ đi theo chuẩn **Clean Architecture** kết hợp **MVVM (Model - View - ViewModel)** để đảm bảo gọn nhẹ, mượt mà và dễ bảo trì:
- **Ngôn ngữ:** Kotlin 100%.
- **Giao diện (UI):** Jetpack Compose, Navigation Compose.
- **Dependency Injection:** Hilt.
- **Luồng dữ liệu (Asynchrony):** Kotlin Coroutines & Flow.

## 2. Các công nghệ và Thư viện cốt lõi
Để giải quyết từng yêu cầu của bạn một cách mượt mà nhất:

### 2.1. Quản lý hệ thống ảnh (Media Storage)
- **MediaStore API:** Truy xuất toàn bộ ảnh/video trên máy cực nhanh (nhận diện được thư mục gốc như Camera, Zalo, Messenger,...).
- **Trình tải ảnh:** Thư viện **Coil** (mượt mà, tích hợp hoàn hảo với Compose, hỗ trợ caching tốt).

### 2.2. Trí tuệ nhân tạo (AI & Machine Learning)
- **Phân loại khuôn mặt & Cảnh vật (Offline):** Sử dụng **Google ML Kit** (Face Detection API, Image Labeling API) chạy ngầm để quét ảnh và tự động gắn thẻ (tag) vào Database nội bộ.
- **Trợ lý AI Chatbot:** Tích hợp **Gemini API** để làm trí tuệ trung tâm. AI sẽ tương tác thông qua prompt của người dùng và metadata ảnh. Phân tích truy vấn bằng Natural Language Processing (NLP).
- **Chỉnh sửa ảnh bằng AI:** Gửi ảnh thao tác và prompt của người dùng lên API để trả về kết quả gen AI, sau đó lưu mượt mà vào album "AI photos".

### 2.3. Cơ sở dữ liệu nội bộ (Local DB)
- **Room Database:** Lưu trữ thông tin metadata (tag nhận diện ML, thời gian bị xóa vào thùng rác, nhóm custom Album).

### 2.4. Bảo mật & Setting
- **AndroidX Biometric:** Hỗ trợ xác thực vân tay/khuôn mặt cho folder **Security photos**.
- **Datastore (Preferences):** Lưu trữ giao diện (Light/Dark mode) và Password hash (nếu dùng mã PIN).
- **Hệ thống xóa ảnh 30 ngày:** Dùng **WorkManager** để hẹn giờ kiểm tra và dọn dẹp ảnh hết hạn mỗi ngày dưới nền.

## 3. Cấu trúc Giao diện (Material Design 3)
Giao diện sẽ tuân thủ kỹ năng `mobile-android-design`:
- **Bottom Navigation Bar:** Các tab chính: _Photos (Theo năm/tháng)_, _Albums (Folder/Face/Scene/Custom)_, _Security_, _Settings_.
- **Floating Action Button (FAB):** Nút gọi Trợ lý AI ở góc dưới bên trái màn hình.
- **Top App Bar:** Thanh tìm kiếm nhanh.
- **Adaptive Layout:** Dùng `LazyVerticalGrid` tự căn chỉnh ảnh đẹp mắt với mọi màn hình, cuộn mượt mà ngay cả với hàng chục ngàn bức ảnh.

---

## 4. Lộ trình phát triển đề xuất (Roadmap)

### 🔴 Giai đoạn 1: Nền tảng và MediaStore (Core Gallery)
1. Cài đặt framework dự án Android cơ bản, Material 3 Theme (Thêm Light/Dark mode).
2. Viết class cấp quyền truy cập hệ thống `READ_MEDIA_IMAGES` (Android 14+).
3. Đọc dữ liệu từ `MediaStore` và hiển thị lưới ảnh theo Năm/Tháng.
4. Xây dựng trang hiển thị Albums, gom nhóm nguyên bản theo Folders máy.

### 🟡 Giai đoạn 2: Tích hợp ML Offline & Custom Album
1. Thêm ML Kit: Chạy background quét ảnh, gom nhóm "Khuôn mặt" và "Phong cảnh/Video".
2. Xây chức năng chọn ảnh tự tạo Album, Thùng rác (Deleted - thùng rác chờ 30 ngày xoá) - lưu tag bằng Room DB.
3. Chức năng Security Folder (Tạo mã PIN hoặc quét Sinh trắc học).

### 🟢 Giai đoạn 3: Tính năng App Chỉnh sửa mặc định, Cài đặt
1. Chức năng mở ảnh Fullscreen mượt mà có Animation, nút Share, Delete.
2. Xử lý logic gọi ứng dụng Edit mặc định của điện thoại thông qua `Intent.ACTION_EDIT`.
3. Hoàn thiện các trang Detail: Version info, Settings.

### 🟣 Giai đoạn 4: Trợ lý AI và Chỉnh sửa thông minh
1. Tạo UI FAB góc trái gọi Popup Chatbot (Voice + Text + Image pick input).
2. Xử lý logic NLP: Phân tích hành vi (vd: "Ảnh chụp tháng 3" -> Truyền tín hiệu Intent về màn hình Gallery để tự động chọt navigation nhảy tới cụm tháng 3).
3. Logic Edit tạo ảnh mới bằng AI chatbot. Lưu ảnh vào folder "AI photos".

---
*Bản kế hoạch này tập trung vào sự kết hợp giữa các Native API hiệu suất cao và giao diện Compose thời thượng để đảm bảo đạt được yêu cầu "mượt mà, dễ dùng" trên Android 14+ đúng như bạn mong muốn.*
