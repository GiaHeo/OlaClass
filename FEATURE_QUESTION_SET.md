# Tính năng Tạo Bộ Câu Hỏi trong Tạo Bài Kiểm Tra

## Mô tả
Đã thêm tính năng tạo và quản lý bộ câu hỏi trong màn hình tạo bài kiểm tra (CreateQuizActivity).

## Các tính năng mới

### 1. Nút "Tạo bộ câu hỏi"
- **Vị trí**: Bên cạnh spinner chọn bộ câu hỏi
- **Chức năng**: Mở màn hình CreateQuestionSetActivity để tạo bộ câu hỏi mới
- **Luồng hoạt động**:
  1. Nhấn nút "Tạo bộ câu hỏi"
  2. Chuyển đến màn hình tạo bộ câu hỏi
  3. Tạo và lưu bộ câu hỏi
  4. Quay lại màn hình tạo bài kiểm tra
  5. Danh sách bộ câu hỏi được refresh tự động

### 2. Nút mũi tên "▼"
- **Vị trí**: Bên cạnh nút "Tạo bộ câu hỏi"
- **Chức năng**: Hiển thị dialog danh sách tất cả bộ câu hỏi đã tạo
- **Thông tin hiển thị**: Tên bộ câu hỏi + số lượng câu hỏi
- **Luồng hoạt động**:
  1. Nhấn nút mũi tên
  2. Hiển thị dialog với danh sách bộ câu hỏi
  3. Chọn bộ câu hỏi từ danh sách
  4. Tự động cập nhật spinner và thông tin hiển thị

### 3. Thông tin bộ câu hỏi đã chọn
- **Vị trí**: Dưới spinner chọn bộ câu hỏi
- **Hiển thị**: 
  - Tên bộ câu hỏi
  - Số lượng câu hỏi trong bộ
- **Cập nhật**: Tự động cập nhật khi chọn bộ câu hỏi khác

## Cấu trúc UI mới

```
┌─────────────────────────────────────────────────────────┐
│ Tên bài kiểm tra                                        │
├─────────────────────────────────────────────────────────┤
│ [Spinner bộ câu hỏi] [Tạo bộ câu hỏi] [▼]              │
├─────────────────────────────────────────────────────────┤
│ Bộ câu hỏi: [Tên bộ câu hỏi]                           │
│ Số câu hỏi: [Số lượng]                                 │
├─────────────────────────────────────────────────────────┤
│ [Chọn thời gian bắt đầu]                               │
│ [Chọn thời gian kết thúc]                              │
│ [Thời gian làm bài (phút)]                             │
│ [Tạo bài kiểm tra]                                     │
└─────────────────────────────────────────────────────────┘
```

## Các file đã cập nhật

### 1. `app/src/main/res/layout/activity_create_quiz.xml`
- Thêm LinearLayout chứa spinner, nút tạo bộ câu hỏi và nút mũi tên
- Thêm TextView hiển thị thông tin bộ câu hỏi đã chọn

### 2. `app/src/main/java/com/example/olaclass/ui/assignments/CreateQuizActivity.java`
- Thêm biến cho các nút mới
- Thêm logic xử lý sự kiện cho các nút
- Thêm phương thức `showQuestionSetsDialog()` để hiển thị dialog
- Thêm phương thức `updateSelectedQuestionSetInfo()` để cập nhật thông tin
- Thêm `onResume()` để refresh danh sách khi quay lại
- Thêm listener cho spinner để cập nhật thông tin

## Luồng hoạt động tổng thể

1. **Tạo bộ câu hỏi mới**:
   - Nhấn "Tạo bộ câu hỏi" → Chuyển đến CreateQuestionSetActivity
   - Tạo và lưu bộ câu hỏi
   - Quay lại → Danh sách được refresh

2. **Chọn bộ câu hỏi có sẵn**:
   - Nhấn nút mũi tên "▼" → Hiển thị dialog
   - Chọn bộ câu hỏi → Cập nhật spinner và thông tin

3. **Hiển thị thông tin**:
   - Tự động hiển thị thông tin bộ câu hỏi đã chọn
   - Cập nhật khi thay đổi lựa chọn

## Lợi ích

1. **Tiện lợi**: Có thể tạo bộ câu hỏi ngay trong quá trình tạo bài kiểm tra
2. **Quản lý tốt**: Dễ dàng xem và chọn từ các bộ câu hỏi đã tạo
3. **Thông tin rõ ràng**: Hiển thị đầy đủ thông tin về bộ câu hỏi đã chọn
4. **Tự động cập nhật**: Danh sách được refresh tự động khi có thay đổi 