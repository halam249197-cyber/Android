# 🚀 Gallerity - Task Tracker

Dự án ứng dụng thư viện ảnh thông minh `Gallerity` với package `com.halam.gallerity`.

- [ ] **Giai đoạn 1: Nền tảng và MediaStore (Core Gallery)**
  - [x] Khởi tạo bộ khung dự án Android Jetpack Compose (Gallerity).
  - [x] Thiết lập cấu trúc thư mục (Clean Architecture: `data`, `domain`, `presentation`, `di`).
  - [x] Thêm các thư viện (Dependencies): Hilt, Room, Coil, ViewModel, Navigation Compose.
  - [x] Cấu hình Material Design 3 Theme (Màu sắc, Typography, Shape).
  - [x] Xử lý xin quyền hệ thống `READ_MEDIA_IMAGES` (và `READ_MEDIA_VISUAL_USER_SELECTED` cho Android 14).
  - [/] Viết hàm đọc ảnh gốc từ `MediaStore` (Sắp xếp theo Ngày/Tháng/Năm tạo).
  - [ ] Giao diện màn hình Home (Lưới ảnh).
  - [ ] Giao diện màn hình liệt kê các Folder cục bộ trong máy.

- [ ] **Giai đoạn 2: Tích hợp ML Offline & Quản lý Album**
  - [ ] Tích hợp Google ML Kit (Face Detection, Image Labeling) chạy background.
  - [ ] Room Database: Lưu trữ Metadata (Tag, Custom Album, thời gian xóa vào Thùng rác).
  - [ ] Tính năng gom nhóm ảnh khuôn mặt, cảnh vật thành Album.
  - [ ] Chức năng Thùng rác (Xóa 30 ngày).
  - [ ] Security Photos (Thư mục bảo mật mã PIN/Biometric).

- [ ] **Giai đoạn 3: Tính năng chỉnh sửa & Setting**
  - [ ] Màn hình chi tiết ảnh mượt mà kèm Navigation trượt.
  - [ ] Nút Mở app Edit ảnh mặc định qua Intent.
  - [ ] Màn hình Cài đặt (Đổi Theme, Setting mật khẩu, Xóa rác, Version info).

- [ ] **Giai đoạn 4: Trợ lý AI và Chatbot chỉnh sửa**
  - [ ] Tích hợp Gemini API và kết nối Prompt.
  - [ ] Floating Button Chatbot có khả năng nhận Voice, Text, Image.
  - [ ] NLP: Phân tích Chat để trỏ Navigation/Intent đi tìm ảnh đúng mô tả.
  - [ ] Logic lưu ảnh tạo bởi AI vào "AI photos".
