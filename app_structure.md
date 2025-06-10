# Cấu trúc Ứng dụng Chi tiết OlaClass

Đây là mô tả chi tiết về cấu trúc màn hình (Activities/Scenes) và các thành phần (Fragments) của ứng dụng OlaClass, cùng với luồng điều hướng chính, các yêu cầu kỹ thuật và chỉ số thành công.

### Lưu ý về Lưu trữ Dữ liệu
Toàn bộ dữ liệu liên quan đến bài kiểm tra, điểm số, thông tin lớp học, và hồ sơ người dùng sẽ được lưu trữ và quản lý trên Firebase Firestore. Điều này bao gồm:
-   **Bài kiểm tra/Bài tập:** Cấu trúc câu hỏi, đáp án, thời gian làm bài, và các thiết lập khác của từng bài.
-   **Điểm số:** Kết quả chấm điểm của học sinh cho từng bài làm.
-   **Bài làm của học sinh:** Câu trả lời của học sinh cho từng câu hỏi, trạng thái nộp bài.
-   **Thông tin lớp học:** Tên lớp, mô tả, danh sách giáo viên/học sinh trong lớp.
-   **Hồ sơ người dùng:** Thông tin cá nhân, vai trò (Giáo viên/Học sinh), và các cài đặt liên quan.
Việc sử dụng Firestore đảm bảo dữ liệu được đồng bộ hóa theo thời gian thực, có khả năng mở rộng cao và được bảo mật bởi các quy tắc của Firebase.

## 1. Kiến trúc Tổng quan

Ứng dụng OlaClass được thiết kế với kiến trúc mô-đun, dễ mở rộng và bảo trì, bao gồm các lớp chính:

-   Authentication Layer: Quản lý đăng ký, đăng nhập và vai trò người dùng.
-   Role-based Navigation: Điều hướng ứng dụng dựa trên vai trò (Giáo viên/Học sinh).
-   Teacher Module: Chứa các chức năng dành riêng cho giáo viên (quản lý lớp, bài tập, chấm điểm).
-   Student Module: Chứa các chức năng dành riêng cho học sinh (tham gia lớp, làm bài, xem điểm).
-   Shared Components: Các thành phần giao diện và logic được sử dụng chung cho cả hai vai trò.
-   Firebase Integration: Tích hợp các dịch vụ Backend của Firebase.

## 2. Luồng Ứng dụng Chính

### Luồng Khởi động Ứng dụng

Khi ứng dụng khởi động, luồng sẽ diễn ra như sau:

1.  **Khởi chạy ứng dụng:**
    -   Ứng dụng kiểm tra trạng thái xác thực của người dùng hiện tại (sử dụng Firebase Authentication).
2.  **Xác định trạng thái người dùng:**
    -   Nếu người dùng đã đăng nhập và thông tin vai trò (Giáo viên/Học sinh) có sẵn:
        -   Ứng dụng sẽ điều hướng trực tiếp đến màn hình Home tương ứng với vai trò của họ.
    -   Nếu người dùng chưa đăng nhập hoặc thông tin vai trò không rõ ràng:
        -   Ứng dụng sẽ chuyển hướng đến màn hình Đăng nhập/Đăng ký.

### Màn hình Đăng nhập/Đăng ký (Authentication Scene)

Đây là màn hình mà người dùng sẽ tương tác để tạo tài khoản hoặc truy cập vào ứng dụng.

-   **Các thành phần chính:**
    -   Form Đăng nhập (Email/Mật khẩu)
    -   Form Đăng ký (Email/Mật khẩu/Chọn vai trò Giáo viên/Học sinh)
    -   Nút Đăng nhập/Đăng ký bằng Google (Tích hợp Firebase Google Sign-In)
    -   Tùy chọn Quên mật khẩu
-   **Logic xử lý:**
    -   Xử lý xác thực người dùng qua Firebase Authentication.
    -   Lưu trữ thông tin vai trò người dùng vào Firebase Firestore khi đăng ký.
    -   Sau khi đăng nhập/đăng ký thành công, điều hướng đến màn hình Home tương ứng với vai trò.

## 3. Màn hình Home của Giáo viên (Teacher Home Scene)

Màn hình chính dành cho giáo viên, hiển thị các chức năng quản lý lớp học và bài tập.

### Cấu trúc Điều hướng (Sử dụng TabLayout hoặc Navigation Drawer)

-   Classes: Quản lý các lớp học.
-   Assignments: Tạo và quản lý bài tập.
-   Grading: Chấm điểm và phản hồi bài làm của học sinh.
-   Communications: Đăng thông báo và chia sẻ tài liệu.
-   Profile: Cập nhật thông tin cá nhân và cài đặt ứng dụng.

### Chi tiết Fragments

-   **Classes Fragment:**
    -   Hiển thị danh sách các lớp học mà giáo viên đang dạy.
    -   Chức năng: Tạo lớp học mới (UC5), chỉnh sửa thông tin lớp, xóa lớp.
    -   Khi chọn một lớp: Hiển thị danh sách học sinh trong lớp (UC7) và các bài tập đã giao cho lớp đó.
-   **Assignments Fragment:**
    -   Giao diện để tạo bài tập mới (UC8).
    -   Tùy chọn tạo bài kiểm tra/bài tập tùy chỉnh trong ứng dụng (UC9).
    -   Thiết lập hạn nộp (UC10) và lên lịch phát hành (UC11).
    -   Sao chép bài tập (UC12).
-   **Grading Fragment:**
    -   Xem kết quả bài làm của học sinh trong ứng dụng (UC13).
    -   Nhập điểm thủ công (UC14).
    -   Gửi phản hồi cá nhân (UC15) và trả bài (UC16).
-   **Communications Fragment:**
    -   Đăng thông báo chung cho lớp (UC17).
    -   Chia sẻ tài liệu học tập (UC18).
-   **Profile Fragment:**
    -   Cập nhật thông tin cá nhân (UC2).
    -   Cài đặt thông báo (UC4).

## 4. Màn hình Home của Học sinh (Student Home Scene)

Màn hình chính dành cho học sinh, hiển thị các lớp học, bài tập và thông báo.

### Cấu trúc Điều hướng (Sử dụng TabLayout hoặc Navigation Drawer)

-   My Classes: Xem các lớp học đã tham gia.
-   Assignments: Xem và làm bài tập được giao.
-   Grades: Xem điểm và phản hồi từ giáo viên.
-   Materials: Truy cập thông báo và tài liệu học tập.
-   Profile: Cập nhật thông tin cá nhân và cài đặt ứng dụng.

### Chi tiết Fragments

-   **My Classes Fragment:**
    -   Hiển thị danh sách các lớp học mà học sinh đang tham gia.
    -   Chức năng: Tham gia lớp học bằng mã code hoặc liên kết (UC19).
    -   Khi chọn một lớp: Hiển thị danh sách bài tập của lớp đó.
-   **Assignments Fragment:**
    -   Hiển thị danh sách các bài tập đã được giao (UC20), với trạng thái (đã làm, chưa làm, quá hạn).
    -   Mở và làm bài tập/bài kiểm tra tùy chỉnh trong ứng dụng (UC21).
    -   Nộp bài tập (UC22).
    -   Xem lại bài tập đã nộp (UC23).
-   **Grades Fragment:**
    -   Xem điểm các bài tập đã chấm (UC24).
    -   Xem phản hồi chi tiết từ giáo viên (UC25).
-   **Materials Fragment:**
    -   Xem các thông báo mới từ giáo viên (UC26).
    -   Truy cập và tải xuống tài liệu học tập (UC27).
-   **Profile Fragment:**
    -   Cập nhật thông tin cá nhân (UC2).
    -   Cài đặt thông báo (UC4).

## 5. Hướng dẫn UI/UX

### Nguyên tắc Thiết kế

-   **Clarity:** Rõ ràng về vai trò và chức năng.
-   **Efficiency:** Giảm thiểu thao tác để hoàn thành nhiệm vụ.
-   **Consistency:** Thống nhất về giao diện (màu sắc, phông chữ, khoảng cách).
-   **Accessibility:** Hỗ trợ tốt cho nhiều đối tượng người dùng.

### Bảng màu

-   Primary: Blue (#2196F3) - Tin cậy, học thuật.
-   Secondary: Orange (#FF9800) - Năng động, tương tác.
-   Success: Green (#4CAF50) - Hoàn thành, đúng.
-   Warning: Amber (#FFC107) - Cảnh báo, chờ xử lý.
-   Error: Red (#F44336) - Lỗi, quan trọng.

### Kiểu chữ

-   Headers: Roboto Bold
-   Body: Roboto Regular
-   Code/Data: Roboto Mono.

## 6. Yêu cầu Kỹ thuật

### Frontend (Android)

-   Framework: Android Native (Java/Kotlin)
-   State Management: ViewModel/LiveData
-   Navigation: Android Navigation Component
-   HTTP Client: Retrofit hoặc OkHttp (nếu cần tương tác với API ngoài Google Forms/Firebase)

### Backend Services

-   Authentication: Firebase Authentication
-   Database: Firebase Firestore
-   Storage: Firebase Storage
-   Functions: Firebase Cloud Functions (để xử lý logic backend)
-   Messaging: Firebase Cloud Messaging

### Tích hợp bên thứ ba

-   Email Service: Firebase Extensions (ví dụ: Trigger Email) để gửi thông báo.

## 7. Các giai đoạn Phát triển

### Giai đoạn 1: Nền tảng (Tuần 1-4)

-   Thiết lập dự án và cấu hình Firebase.
-   Hệ thống xác thực người dùng.
-   Cấu trúc điều hướng cơ bản.
-   Quản lý hồ sơ người dùng.

### Giai đoạn 2: Tính năng cốt lõi (Tuần 5-8)

-   Tạo và quản lý lớp học.
-   Tạo bài tập cơ bản.
-   Hệ thống đăng ký học sinh.
-   Phát triển giao diện và logic tạo/làm bài kiểm tra/bài tập tùy chỉnh.

### Giai đoạn 3: Tính năng nâng cao (Tuần 9-12)

-   Hệ thống chấm điểm.
-   Hệ thống thông báo.
-   Chia sẻ tệp và tài liệu.
-   Các tính năng bài tập nâng cao.

### Giai đoạn 4: Hoàn thiện & Thử nghiệm (Tuần 13-16)

-   Cải thiện UI/UX.
-   Tối ưu hóa hiệu suất.
-   Thử nghiệm toàn diện.
-   Chuẩn bị triển khai.

## 8. Chiến lược Thử nghiệm

### Thử nghiệm đơn vị (Unit Testing)

-   Logic xác thực.
-   Chức năng xác thực dữ liệu.
-   Chức năng tích hợp Firebase.
-   Logic xử lý bài kiểm tra/bài tập tùy chỉnh.

### Thử nghiệm tích hợp (Integration Testing)

-   Luồng người dùng hoàn chỉnh.
-   Khả năng tương thích đa nền tảng.
-   Quy tắc bảo mật Firebase.
-   Tích hợp API bên thứ ba.

### Thử nghiệm chấp nhận người dùng (User Acceptance Testing - UAT)

-   Luồng công việc của giáo viên.
-   Luồng công việc của học sinh.
-   Chức năng quản trị.
-   Hiệu suất dưới tải.

## 9. Các chỉ số Thành công

### Mức độ Tương tác Người dùng

-   Số người dùng hoạt động hàng ngày/hàng tháng.
-   Thời lượng phiên sử dụng.
-   Tỷ lệ chấp nhận tính năng.
-   Tỷ lệ giữ chân người dùng.

### Chỉ số Chức năng

-   Tỷ lệ hoàn thành bài tập.
-   Thời gian phản hồi cho việc chấm điểm.
-   Tỷ lệ lỗi và báo cáo sự cố.
-   Điểm hài lòng của người dùng.

### Chỉ số Kỹ thuật

-   Thời gian tải ứng dụng.
-   Thời gian phản hồi API.
-   Hiệu suất truy vấn cơ sở dữ liệu.
-   Tối ưu hóa việc sử dụng lưu trữ. 