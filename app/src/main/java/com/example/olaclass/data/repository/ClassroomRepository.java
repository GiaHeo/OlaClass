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

    // Thêm học sinh vào lớp (collection con 'students')
    public Task<DocumentReference> addStudentToClass(String classId, String studentId, String studentName) {
        Map<String, Object> data = new HashMap<>();
        data.put("studentId", studentId);
        data.put("studentName", studentName);
        return classroomRef.document(classId).collection("students").add(data);
    }

    // Xóa học sinh khỏi lớp
    public Task<Void> removeStudentFromClass(String classId, String studentDocId) {
        return classroomRef.document(classId).collection("students").document(studentDocId).delete();
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


