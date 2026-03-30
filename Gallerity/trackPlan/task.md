# 🚀 Gallerity - Task Tracker

Dự án ứng dụng thư viện ảnh thông minh `Gallerity` với package `com.halam.gallerity`.

- [x] **Giai đoạn 1: Nền tảng và MediaStore (Core Gallery)**
  - [x] Khởi tạo bộ khung dự án Android Jetpack Compose (Gallerity).
  - [x] Thiết lập cấu trúc thư mục (Clean Architecture: `data`, `domain`, `presentation`, `di`).
  - [x] Thêm các thư viện (Dependencies): Hilt, Room, Coil, ViewModel, Navigation Compose.
  - [x] Cấu hình Material Design 3 Theme (Màu sắc, Typography, Shape).
  - [x] Xử lý xin quyền hệ thống `READ_MEDIA_IMAGES` (và `READ_MEDIA_VISUAL_USER_SELECTED` cho Android 14).
  - [x] Viết hàm đọc ảnh gốc từ `MediaStore` (Sắp xếp theo Ngày/Tháng/Năm tạo).
  - [x] Giao diện màn hình Home (Lưới ảnh).
  - [x] Giao diện màn hình liệt kê các Folder cục bộ trong máy.

- [/] **Giai đoạn 2: Tích hợp ML Offline & Quản lý Album**
  - [x] Thiết lập Room Database cơ sở (`MediaMetadataEntity`, `Dao`) và Hilt Module.
  - [x] Xây dựng `MlKitScannerWorker` để quét nền thầm lặng (Idle/Charging) cho Face & Image Labeling.
  - [x] Nhập mã PIN dự phòng vào `DataStore`.
  - [x] Tích hợp `BiometricPrompt` kết hợp mã PIN cho Thư mục ẩn.
  - [x] Giao diện `SecurityScreen` (Bàn phím PIN / Mở khoá).
  - [x] Cập nhật `HomeScreen` chia thành các Album (Thùng rác, Khuôn mặt, Chó mèo, Security).

- [ ] **Giai đoạn 2.5: Bottom Navigation, Onboarding & Calendar**
  - [x] Thiết lập luồng `AppNavigation` bằng Jetpack Navigation Compose.
  - [x] Màn hình `OnboardingScreen` (Hỏi setup PIN/Biometric lần đầu).
  - [x] Thêm `MainScreen` với Bottom Navigation Bar 4 nút: `Home`, `Calendar`, `Settings`, `Login`.
  - [x] Tính năng Lịch (`CalendarScreen`): Hiển thị lưới ngày tháng kèm đếm số ảnh.
  - [x] Khi bấm vào Lịch: Trượt sang màn hình mới (`DayPhotosScreen`).
  - [x] Cấu hình Strict Lock cho Tab Security (Rời Tab tự động khóa lại).

- [ ] **Giai đoạn 3: Tính năng App Chỉnh sửa mặc định, Cài đặt**
  - [ ] Tích hợp Gemini API và kết nối Prompt.
  - [ ] Floating Button Chatbot có khả năng nhận Voice, Text, Image.
  - [ ] NLP: Phân tích Chat để trỏ Navigation/Intent đi tìm ảnh đúng mô tả.
  - [ ] Logic lưu ảnh tạo bởi AI vào "AI photos".
