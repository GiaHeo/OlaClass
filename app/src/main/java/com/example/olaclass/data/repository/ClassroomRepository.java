package com.example.olaclass.data.repository;

import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingSource;
import androidx.paging.rxjava2.RxPagingSource;
import io.reactivex.Flowable;
import com.example.olaclass.data.model.Classroom;
import com.example.olaclass.data.paging.ClassroomPagingSource;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.HashMap;
import java.util.Map;
import androidx.annotation.NonNull;

public class ClassroomRepository {
    private final CollectionReference classroomRef = FirebaseFirestore.getInstance().collection("classrooms");

    // Lưu Classroom mới vào Firestore
    public Task<DocumentReference> addClassroom(@NonNull Classroom classroom) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", classroom.getName());
        data.put("description", classroom.getDescription());
        data.put("teacherId", classroom.getTeacherId());
        return classroomRef.add(data);
    }

    // Lấy danh sách lớp học thực tế từ Firestore
    public Task<QuerySnapshot> getAllClassrooms() {
        return classroomRef.get();
    }

    // Tạo liên kết mời có thể chia sẻ cho lớp học dựa trên mã code đã lưu
    public Task<String> getInviteLink(String classId) {
        return classroomRef.document(classId).get().continueWith(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().contains("inviteCode")) {
                String code = task.getResult().getString("inviteCode");
                // Có thể tuỳ chỉnh domain/app link theo thực tế triển khai
                return "https://olaclass.app/invite?code=" + code;
            } else {
                return null;
            }
        });
    }

    // Tạo mã code mời lớp học duy nhất, lưu vào Firestore
    public Task<String> generateAndSaveUniqueInviteCode(String classId) {
        String code = com.example.olaclass.utils.InviteCodeUtil.generateCode();
        // Kiểm tra trùng code
        return classroomRef.whereEqualTo("inviteCode", code).get().continueWithTask(task -> {
            if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                // Nếu trùng, thử lại đệ quy
                return generateAndSaveUniqueInviteCode(classId);
            } else {
                // Không trùng, lưu vào lớp học
                return classroomRef.document(classId).update("inviteCode", code).continueWith(t -> code);
            }
        });
    }

    // Thêm học sinh vào lớp (collection con 'students')
    public Task<DocumentReference> addStudentToClass(String classId, String studentId, String studentName) {
        Map<String, Object> data = new HashMap<>();
        data.put("studentId", studentId);
        data.put("studentName", studentName);
        return classroomRef.document(classId).collection("students").add(data);
    }

    // Lấy danh sách học sinh trong lớp
    public CollectionReference getStudents(String classId) {
        return classroomRef.document(classId).collection("students");
    }

    // Lấy profile chi tiết của một học sinh trong lớp
    public Task<DocumentSnapshot> getStudentProfile(String classId, String studentDocId) {
        return classroomRef.document(classId).collection("students").document(studentDocId).get();
    }

    // Xóa học sinh khỏi lớp
    public Task<Void> removeStudentFromClass(String classId, String studentDocId) {
        return classroomRef.document(classId).collection("students").document(studentDocId).delete();
    }

    // Đếm tổng số học sinh trong lớp
    public Task<QuerySnapshot> countStudents(String classId) {
        return classroomRef.document(classId).collection("students").get();
    }

    // Gửi lời mời qua email (lưu vào collection con 'invites')
    public Task<DocumentReference> sendInvite(String classId, String email) {
        Map<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("status", "pending");
        data.put("sentAt", System.currentTimeMillis());
        return classroomRef.document(classId).collection("invites").add(data);
    }

    // Lấy danh sách lời mời đã gửi của lớp học
    public CollectionReference getInvites(String classId) {
        return classroomRef.document(classId).collection("invites");
    }

    // Thêm bài tập vào lớp (collection con 'assignments')
    public Task<DocumentReference> addAssignmentToClass(String classId, Map<String, Object> assignmentData) {
        return classroomRef.document(classId).collection("assignments").add(assignmentData);
    }

    // Hàm paging mock giữ lại cho demo
    public Flowable<PagingData<Classroom>> getClassroomsPaged() {
        return new Pager<>(
                new PagingConfig(10),
                () -> new ClassroomPagingSource()
        ).flowable();
    }
}


