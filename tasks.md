# Dự án OlaClass - Tài liệu Chi tiết

## Tổng quan dự án
OlaClass là ứng dụng quản lý lớp học trực tuyến cho phép giáo viên tạo và quản lý bài tập/bài kiểm tra ngay trong ứng dụng, học sinh làm bài và nhận phản hồi một cách hiệu quả.

### Công nghệ sử dụng
- **Backend**: Firebase (Authentication, Firestore, Storage, Cloud Messaging)
- **Kiến trúc**: MVVM (Model-View-ViewModel)
- **Ngôn ngữ**: Java
- **Thư viện chính**:
  - AndroidX (AppCompat, Material, ConstraintLayout)
  - ViewModel & LiveData
  - Navigation Component
  - Glide (tải ảnh)
  - CircleImageView (hiển thị ảnh đại diện)
  - Gson (chuyển đổi JSON)

### Cấu trúc thư mục
```
app/
├── data/
│   ├── model/      # Data classes
│   ├── remote/     # API clients, Firebase services
│   └── repository/ # Repository pattern implementation
├── di/             # Dependency injection
├── ui/
│   ├── auth/      # Authentication screens
│   ├── teacher/    # Teacher features
│   ├── student/    # Student features
│   ├── shared/     # Shared components
│   └── base/       # Base classes
└── utils/          # Utility classes
```


## Danh sách Task Ưu Tiên

### 1. Cơ sở hạ tầng & Hỗ trợ (Infrastructure & Support)
#### 1.1. Cơ sở MVVM và Kiến trúc
- [x] Task 1.1: Thiết lập base classes (BaseActivity, BaseFragment, BaseViewModel)
- [x] Task 1.2: Triển khai Repository pattern
- [x] Task 1.3: Xây dựng lớp Resource wrapper cho API responses
- [x] Task 1.4: Triển khai ViewBinding cho các màn hình
- [x] Task 1.5: Xây dựng lớp Event wrapper cho singleLiveEvents
- [x] Task 1.6: Triển khai Data Binding
- [x] Task 1.7: Xử lý lỗi và loading states
- [x] Task 1.8: Tích hợp Dependency Injection với Hilt

#### 1.2. Đa ngôn ngữ
- [x] Task 2.1: Tạo file strings.xml cho tiếng Anh (en) và tiếng Việt (vi)
- [x] Task 2.2: Tạo utility class quản lý ngôn ngữ
- [x] Task 2.3: Xây dựng giao diện chuyển đổi ngôn ngữ
- [x] Task 2.4: Lưu cài đặt ngôn ngữ vào SharedPreferences
- [x] Task 2.5: Áp dụng đa ngôn ngữ cho tất cả màn hình
- [x] Task 2.6: Kiểm thử với các ngôn ngữ khác nhau

#### 1.3. Tối ưu hiệu năng
- [x] Task 3.1: Phân tích và tối ưu thời gian khởi động ứng dụng
- [x] Task 3.2: Tối ưu bộ nhớ và kích thước APK
- [x] Task 3.3: Triển khai phân trang (Paging) cho danh sách lớn

#### 1.4. Bảo mật
- [x] Task 4.1: Mã hóa dữ liệu nhạy cảm
- [x] Task 4.2: Bảo vệ API keys với Android Keystore
- [x] Task 4.3: Kiểm tra và khắc phục các lỗ hổng bảo mật
- [x] Task 4.4: Triển khai SSL Pinning

---

### 2. User Cases & Chức năng nghiệp vụ

### 1. Xác thực, Hồ sơ và Thông báo (Đã hoàn thành)
#### UC1: Đăng ký/Đăng nhập với Vai trò
Mô tả: Hệ thống xác thực người dùng với phân biệt vai trò rõ ràng
- [x] Task 1.1: Thiết kế UI màn hình đăng nhập/đăng ký
- [x] Task 1.2: Tích hợp Firebase Authentication
- [x] Task 1.3: Tạo form đăng ký với lựa chọn vai trò (Giáo viên/Học sinh)
- [x] Task 1.4: Xây dựng logic xác thực Google Sign-In
- [x] Task 1.5: Lưu trữ thông tin vai trò trong Firestore
- [x] Task 1.6: Xử lý điều hướng sau đăng nhập thành công

#### UC2: Cập nhật Hồ sơ
Mô tả: Cho phép người dùng chỉnh sửa thông tin cá nhân
- [x] Task 2.1: Tạo form cập nhật thông tin cá nhân
- [x] Task 2.2: Validation dữ liệu đầu vào
- [x] Task 2.3: Cập nhật dữ liệu trong Firestore
- [x] Task 2.4: Upload và quản lý ảnh đại diện

#### UC3: Nhận Thông báo
Mô tả: Hệ thống thông báo đa kênh
- [x] Task 3.1: Tích hợp Firebase Cloud Messaging
- [x] Task 3.2: Thiết kế template thông báo
- [x] Task 3.3: Xây dựng logic gửi thông báo tự động

### 2. Nghiệp vụ lớp học (Chưa hoàn thành)
#### UC4: Quản lý Lớp học
Mô tả: Lưu trữ và quản lý thông tin lớp học, dữ liệu nền tảng
- [x] Task 4.1: Lưu trữ thông tin lớp học trong Firestore
- [x] Task 4.2: Tạo collection con cho học sinh và bài tập

#### UC5: Mời Học sinh vào Lớp
Mô tả: Nhiều cách thức mời học sinh tham gia
- [x] Task 5.1: Tạo mã code 6-8 ký tự duy nhất
- [x] Task 5.2: Tạo liên kết mời có thể chia sẻ
- [x] Task 5.3: Gửi lời mời qua email (tùy chọn)
- [x] Task 5.4: Quản lý danh sách lời mời đã gửi

#### UC6: Quản lý Danh sách Học sinh
Mô tả: Xem và quản lý học sinh trong lớp
- [x] Task 6.1: Hiển thị danh sách học sinh với thông tin cơ bản
- [x] Task 6.2: Xem profile chi tiết của từng học sinh
- [x] Task 6.3: Chức năng xóa học sinh khỏi lớp
- [x] Task 6.4: Thống kê tình hình học tập của học sinh

#### UC7: Quản lý Bài tập
Mô tả: Tạo, giao, quản lý bài tập cho lớp học
- [x] Task 7.1: Tạo bài tập mới
- [x] Task 7.2: Giao bài tập cho học sinh
- [x] Task 7.3: Theo dõi, chấm điểm bài tập

#### UC8: Cài đặt Thông báo
Mô tả: Người dùng có thể tùy chỉnh loại thông báo nhận được
- [x] Task 8.1: Tạo giao diện cài đặt thông báo
- [x] Task 8.2: Lưu preferences của người dùng
- [x] Task 8.3: Áp dụng cài đặt vào logic gửi thông báo



### 2. Chức năng Giáo viên

#### Quản lý Lớp học
#### UC5: Tạo Lớp học Mới
Mô tả: Giáo viên có thể tạo và cấu hình lớp học
- [x] Task 5.1: Thiết kế form tạo lớp học (tên, mô tả, môn học)
- [x] Task 5.2: Tạo mã code tham gia duy nhất cho lớp
- [x] Task 5.3: Lưu trữ thông tin lớp học trong Firestore
- [x] Task 5.4: Tạo collection con cho học sinh và bài tập

#### UC6: Mời Học sinh vào Lớp
Mô tả: Nhiều cách thức mời học sinh tham gia
- [x] Task 6.1: Tạo mã code 6-8 ký tự duy nhất
- [x] Task 6.2: Tạo liên kết mời có thể chia sẻ
- [x] Task 6.3: Gửi lời mời qua email (tùy chọn)
- [x] Task 6.4: Quản lý danh sách lời mời đã gửi

#### UC7: Quản lý Danh sách Học sinh
Mô tả: Xem và quản lý học sinh trong lớp
- [x] Task 7.1: Hiển thị danh sách học sinh với thông tin cơ bản
- [x] Task 7.2: Xem profile chi tiết của từng học sinh
- [x] Task 7.3: Chức năng xóa học sinh khỏi lớp
- [x] Task 7.4: Thống kê tình hình học tập của học sinh

#### Quản lý Bài tập
#### UC8: Tạo Bài tập Mới
Mô tả: Tạo bài tập với nhiều loại khác nhau
- [x] Task 8.1: Form tạo bài tập (tiêu đề, mô tả, loại bài tập)
- [x] Task 8.2: Phân loại bài tập (thường, kiểm tra, project)
- [x] Task 8.3: Gán bài tập cho một hoặc nhiều lớp
- [x] Task 8.4: Preview bài tập trước khi xuất bản

#### UC9: Tạo Bài kiểm tra/Bài tập Tùy chỉnh trong Ứng dụng
Mô tả: Xây dựng giao diện và logic tạo bài kiểm tra/bài tập trực tiếp trong ứng dụng
- [x] Task 9.1: Tạo giao diện tạo bài kiểm tra/bài tập (UI builder)
- [x] Task 9.2: Hỗ trợ nhiều loại câu hỏi (tự luận, trắc nghiệm, điền vào chỗ trống)
- [x] Task 9.3: Chức năng thêm/sửa/xóa câu hỏi và đáp án
- [x] Task 9.4: Xử lý logic chấm điểm tự động (cho câu hỏi trắc nghiệm)

#### UC10: Thiết lập Hạn nộp
Mô tả: Quản lý thời gian nộp bài linh hoạt
- [x] Task 10.1: Date/time picker cho hạn nộp
- [x] Task 10.2: Cảnh báo trước hạn nộp
- [x] Task 10.3: Xử lý bài nộp trễ
- [x] Task 10.4: Gia hạn nộp bài cho học sinh cụ thể

#### UC11: Lên lịch Phát hành
Mô tả: Tự động hóa việc phát hành bài tập
- [x] Task 11.1: Scheduler để phát hành bài tập theo thời gian
- [x] Task 11.2: Preview bài tập được lên lịch
- [x] Task 11.3: Chỉnh sửa/hủy lịch phát hành
- [x] Task 11.4: Thông báo tự động khi phát hành

#### UC12: Sao chép Bài tập
Mô tả: Tái sử dụng bài tập cho nhiều lớp
- [x] Task 12.1: Sao chép bài tập sang lớp khác
- [ ] Task 12.1: Chức năng duplicate bài tập
- [ ] Task 12.2: Chỉnh sửa thông tin khi sao chép
- [ ] Task 12.3: Sao chép sang lớp khác
- [ ] Task 12.4: Quản lý template bài tập

#### Chấm điểm và Phản hồi
#### UC13: Xem Kết quả Bài làm
Mô tả: Dashboard theo dõi tiến độ làm bài
- [ ] Task 13.1: Đồng bộ kết quả bài làm từ cơ sở dữ liệu
- [ ] Task 13.2: Hiển thị danh sách bài đã nộp/chưa nộp
- [ ] Task 13.3: Xem chi tiết từng bài làm
- [ ] Task 13.4: Thống kê kết quả theo lớp

#### UC14: Chấm điểm Bài làm
Mô tả: Hệ thống chấm điểm linh hoạt
- [ ] Task 14.1: Interface nhập điểm thủ công
- [ ] Task 14.2: Hỗ trợ thang điểm đa dạng (10, 100, A-F)
- [ ] Task 14.3: Chấm điểm nhanh cho nhiều bài cùng lúc
- [ ] Task 14.4: Lưu trữ lịch sử chấm điểm

#### UC15-16: Phản hồi và Trả bài
Mô tả: Giao tiếp với học sinh về kết quả học tập
- [ ] Task 15.1: Form gửi phản hồi chi tiết
- [ ] Task 15.2: Template phản hồi có sẵn
- [ ] Task 15.3: Đính kèm file/hình ảnh trong phản hồi
- [ ] Task 15.4: Thông báo tự động khi trả bài

#### Thông báo và Tài liệu
#### UC17: Đăng Thông báo
Mô tả: Kênh giao tiếp chính với lớp học
- [ ] Task 17.1: Editor soạn thông báo (rich text)
- [ ] Task 17.2: Đính kèm file/hình ảnh
- [ ] Task 17.3: Lên lịch đăng thông báo
- [ ] Task 17.4: Quản lý thông báo đã đăng

#### UC18: Chia sẻ Tài liệu
Mô tả: Thư viện tài liệu học tập
- [ ] Task 18.1: Upload đa dạng loại file
- [ ] Task 18.2: Tổ chức tài liệu theo folder
- [ ] Task 18.3: Quyền truy cập tài liệu
- [ ] Task 18.4: Theo dõi lượt tải/xem tài liệu


### 3. Chức năng Học sinh

#### Tham gia Lớp học
#### UC19: Tham gia Lớp học
Mô tả: Các cách thức tham gia lớp học
- [ ] Task 19.1: Nhập mã code tham gia
- [ ] Task 19.2: Tham gia qua liên kết
- [ ] Task 19.3: Xác nhận thông tin trước khi tham gia
- [ ] Task 19.4: Hiển thị lớp học mới tham gia

#### Làm bài tập
#### UC20: Xem Danh sách Bài tập
Mô tả: Dashboard bài tập với trạng thái rõ ràng
- [ ] Task 20.1: Danh sách bài tập theo lớp
- [ ] Task 20.2: Phân loại theo trạng thái (chưa làm, đã nộp, quá hạn, đã chấm)
- [ ] Task 20.3: Bộ lọc và tìm kiếm bài tập
- [ ] Task 20.4: Countdown thời gian còn lại

#### UC21-22: Làm và Nộp Bài tập trong Ứng dụng
Mô tả: Trải nghiệm làm bài mượt mà ngay trong ứng dụng
- [x] Task 21.1: Giao diện làm bài kiểm tra/bài tập tùy chỉnh
- [ ] Task 21.2: Lưu tạm khi làm bài dở
- [ ] Task 21.3: Xác nhận trước khi nộp bài
- [ ] Task 21.4: Thông báo nộp bài thành công

#### UC23: Xem lại Bài tập đã nộp
Mô tả: Theo dõi lịch sử học tập
- [ ] Task 23.1: Danh sách bài đã nộp
- [ ] Task 23.2: Xem lại nội dung đã làm
- [ ] Task 23.3: Thời gian nộp và trạng thái
- [ ] Task 23.4: So sánh với bài làm khác (nếu được phép)

#### Xem Điểm và Phản hồi
#### UC24-25: Điểm số và Phản hồi
Mô tả: Theo dõi kết quả học tập
- [ ] Task 24.1: Bảng điểm tổng hợp
- [ ] Task 24.2: Chi tiết điểm từng bài
- [ ] Task 24.3: Biểu đồ tiến bộ học tập
- [ ] Task 24.4: Hiển thị phản hồi từ giáo viên

#### Thông báo và Tài liệu
#### UC26-27: Thông tin Lớp học
Mô tả: Truy cập thông tin và tài liệu
- [ ] Task 26.1: Timeline thông báo mới nhất
- [ ] Task 26.2: Đánh dấu đã đọc thông báo
- [ ] Task 26.3: Tìm kiếm trong thông báo cũ
- [ ] Task 27.1: Danh mục tài liệu theo môn/chủ đề
- [ ] Task 27.2: Download/xem trực tuyến
- [ ] Task 27.3: Bookmark tài liệu quan trọng

### 4. Cơ sở hạ tầng và Hỗ trợ

#### Cơ sở MVVM và Kiến trúc
- [x] Task 28.1: Thiết lập base classes (BaseActivity, BaseFragment, BaseViewModel)
- [x] Task 28.2: Triển khai Repository pattern
- [x] Task 28.3: Xây dựng lớp Resource wrapper cho API responses
- [x] Task 28.4: Triển khai ViewBinding cho các màn hình
- [x] Task 28.5: Xây dựng lớp Event wrapper cho singleLiveEvents
- [x] Task 28.6: Triển khai Data Binding
- [x] Task 28.7: Xử lý lỗi và loading states
- [ ] Task 28.8: Tích hợp Dependency Injection với Hilt

#### Đa ngôn ngữ
- [x] Task 29.1: Tạo file strings.xml cho tiếng Anh (en) và tiếng Việt (vi)
- [x] Task 29.2: Tạo utility class quản lý ngôn ngữ
- [x] Task 29.3: Xây dựng giao diện chuyển đổi ngôn ngữ
- [x] Task 29.4: Lưu cài đặt ngôn ngữ vào SharedPreferences
- [x] Task 29.5: Áp dụng đa ngôn ngữ cho tất cả màn hình
- [x] Task 29.6: Kiểm thử với các ngôn ngữ khác nhau

#### Tối ưu hiệu năng
- [ ] Task 30.1: Phân tích và tối ưu thời gian khởi động ứng dụng
- [ ] Task 30.2: Tối ưu bộ nhớ và kích thước APK
- [ ] Task 30.3: Triển khai phân trang (Paging) cho danh sách lớn
- [x] Task 30.4: Tối ưu hình ảnh và tài nguyên

