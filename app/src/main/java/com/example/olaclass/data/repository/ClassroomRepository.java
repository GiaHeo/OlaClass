package com.example.olaclass.data.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingSource;
import androidx.paging.rxjava3.PagingRx;
import androidx.paging.rxjava3.RxPagingSource;

import com.example.olaclass.data.model.Classroom;
import com.example.olaclass.data.paging.ClassroomPagingSource;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.FieldValue;

import io.reactivex.rxjava3.core.Flowable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.example.olaclass.data.model.Student;

public class ClassroomRepository {

    private static final String CLASSROOMS_COLLECTION = "classrooms";
    private static final String STUDENTS_COLLECTION = "users";

    private final FirebaseFirestore db;
    private final FirebaseAuth mAuth;
    private final CollectionReference classroomRef;
    private final CollectionReference studentsRef;

    public ClassroomRepository(FirebaseFirestore db, FirebaseAuth mAuth) {
        this.db = db;
        this.mAuth = mAuth;
        this.classroomRef = db.collection(CLASSROOMS_COLLECTION);
        this.studentsRef = db.collection(STUDENTS_COLLECTION);
    }
    
    /**
     * Kiểm tra quyền ghi vào Firestore
     */
    public Task<Boolean> checkWritePermission() {
        // Tạo một document tạm để kiểm tra quyền ghi
        Map<String, Object> testData = new HashMap<>();
        testData.put("testField", "testValue" + System.currentTimeMillis());
        testData.put("testTimestamp", com.google.firebase.Timestamp.now());
        
        DocumentReference testDoc = db.collection("test_permissions").document();
        
        return testDoc.set(testData)
            .continueWith(task -> {
                boolean success = task.isSuccessful();
                android.util.Log.d("ClassroomRepo", "[REPO] Kiểm tra quyền ghi: " + (success ? "THÀNH CÔNG" : "THẤT BẠI"));
                if (!success && task.getException() != null) {
                    android.util.Log.e("ClassroomRepo", "[REPO] Lỗi kiểm tra quyền ghi: " + task.getException().getMessage());
                }
                return success;
            });
    }

    // Thêm một lớp học mới
    public Task<Void> addClassroom(String classroomId, Map<String, Object> classroomData) {
        return classroomRef.document(classroomId).set(classroomData);
    }

    // Cập nhật thông tin lớp học
    public Task<Void> updateClassroom(String classroomId, Map<String, Object> updates) {
        return classroomRef.document(classroomId).update(updates);
    }

    // Lấy lớp học theo ID
    public Task<DocumentSnapshot> getClassroom(String classroomId) {
        return classroomRef.document(classroomId).get();
    }

    // Lấy danh sách lớp học phân trang cho giáo viên
    public Flowable<PagingData<Classroom>> getClassroomsPaged() {
        String currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() != null ?
                com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        if (currentUserId == null) {
            android.util.Log.e("ClassroomRepository", "currentUserId null khi lấy lớp học");
            return Flowable.just(PagingData.empty());
        }
        android.util.Log.d("ClassroomRepository", "Query classroom với teacherId = " + currentUserId);
        
        // Tạm thởi bỏ orderBy để tránh lỗi index
        com.google.firebase.firestore.Query query = classroomRef.whereEqualTo("teacherId", currentUserId);
        
        PagingConfig pagingConfig = new PagingConfig(
            /* pageSize = */ 10,
            /* prefetchDistance = */ 20,
            /* enablePlaceholders = */ false
        );
        
        return PagingRx.getFlowable(new Pager<>(
            pagingConfig,
            () -> new ClassroomPagingSource(query)
        ));
    }

    // Lấy danh sách lớp học phân trang mà học sinh đã tham gia
    // Hiện tại phương thức này sẽ trả về PagingData rỗng để tránh crash
    public Flowable<PagingData<Classroom>> getJoinedClassroomsPaged() {
        return Flowable.just(PagingData.empty());
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

    // Xóa một lớp học
    public Task<Void> deleteClassroom(String classroomId) {
        return classroomRef.document(classroomId).delete();
    }

    // Lấy danh sách học sinh trong lớp (sử dụng Task)
    public Task<List<Student>> getStudentsForClass(String classroomId) {
        Log.d("ClassroomRepository", "[getStudentsForClass] Fetching classroom document for ID: " + classroomId);
        return classroomRef.document(classroomId).get().continueWithTask(task -> {
            if (!task.isSuccessful() || task.getResult() == null || !task.getResult().exists()) {
                Log.e("ClassroomRepository", "[getStudentsForClass] Classroom document not found or error: " + (task.getException() != null ? task.getException().getMessage() : "unknown error"));
                throw new Exception("Classroom not found.");
            }
            DocumentSnapshot classroomDoc = task.getResult();
            List<String> studentIds = (List<String>) classroomDoc.get("students");
            Log.d("ClassroomRepository", "[getStudentsForClass] Raw student IDs from classroom: " + studentIds);

            if (studentIds == null || studentIds.isEmpty()) {
                Log.d("ClassroomRepository", "[getStudentsForClass] Student ID list is null or empty. Returning empty list.");
                return Tasks.forResult(new ArrayList<>());
            }

            List<Task<DocumentSnapshot>> fetchStudentTasks = new ArrayList<>();
            for (String studentId : studentIds) {
                Log.d("ClassroomRepository", "[getStudentsForClass] Adding task to fetch student profile for ID: " + studentId);
                fetchStudentTasks.add(studentsRef.document(studentId).get());
            }

            return Tasks.whenAllSuccess(fetchStudentTasks).continueWith(studentsTask -> {
                List<Student> students = new ArrayList<>();
                for (Object docObj : studentsTask.getResult()) {
                    DocumentSnapshot studentDoc = (DocumentSnapshot) docObj;
                    Log.d("ClassroomRepository", "[getStudentsForClass] Processing student document with ID: " + studentDoc.getId() + ", Exists: " + studentDoc.exists());
                    if (studentDoc.exists()) {
                        Student student = studentDoc.toObject(Student.class);
                        if (student != null) {
                            // Thủ công gán displayName vào name, đề phòng việc ánh xạ tự động không hoạt động
                            String displayName = studentDoc.getString("displayName");
                            if (displayName != null) {
                                student.setName(displayName);
                            } else {
                                Log.w("ClassroomRepository", "[getStudentsForClass] displayName is null for student ID: " + studentDoc.getId());
                            }

                            student.setUserId(studentDoc.getId());
                            students.add(student);
                            Log.d("ClassroomRepository", "[getStudentsForClass] Added student: " + student.getName() + " (" + student.getUserId() + ")");
                        } else {
                            Log.w("ClassroomRepository", "[getStudentsForClass] Failed to convert document " + studentDoc.getId() + " to Student object. Data: " + studentDoc.getData());
                        }
                    } else {
                        Log.w("ClassroomRepository", "[getStudentsForClass] Student document does not exist for ID: " + studentDoc.getId());
                    }
                }
                Log.d("ClassroomRepository", "[getStudentsForClass] Finished processing students. Total found: " + students.size());
                return students;
            });
        });
    }

    // Thêm học sinh vào lớp (thay đổi để thêm vào mảng students của classroom)
    public Task<Void> addStudentToClass(String classroomId, String studentId) {
        // Check if student profile exists in top-level students collection
        return studentsRef.document(studentId).get().continueWithTask(studentProfileTask -> {
            if (!studentProfileTask.isSuccessful() || !studentProfileTask.getResult().exists()) {
                throw new Exception("Student profile not found.");
            }
            // Student profile exists, now add studentId to classroom's students array
            return classroomRef.document(classroomId).update("students", FieldValue.arrayUnion(studentId));
        });
    }

    // Xóa học sinh khỏi lớp (xóa khỏi mảng students của classroom)
    public Task<Void> removeStudentFromClass(String classroomId, String studentId) {
        return classroomRef.document(classroomId).update("students", FieldValue.arrayRemove(studentId));
    }

    // Tham gia lớp học với mã mời
    public Task<Void> joinClassroom(String inviteCode, String studentId) {
        return classroomRef.whereEqualTo("inviteCode", inviteCode).get()
                .continueWithTask(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot classroomDoc = task.getResult().getDocuments().get(0);
                        String classroomId = classroomDoc.getId();
                        Map<String, Object> studentData = new HashMap<>();
                        studentData.put("userId", studentId); // Add student ID to the student data
                        // You might want to get student name/email from FirebaseAuth or another source
                        // For now, let's just add the userId
                        return classroomRef.document(classroomId).update("students", com.google.firebase.firestore.FieldValue.arrayUnion(studentId));
                    } else {
                        throw new Exception("Invalid invite code or classroom not found.");
                    }
                });
    }

    // Đếm tổng số học sinh trong lớp
    public Task<QuerySnapshot> countStudents(String classId) {
        return classroomRef.document(classId).collection("students").get();
    }

    // Lấy danh sách học sinh trong lớp (CollectionReference để theo dõi thay đổi)
    public CollectionReference getStudents(String classId) {
        return classroomRef.document(classId).collection("students");
    }

    // Lấy profile chi tiết của một học sinh trong lớp (DocumentSnapshot)
    public Task<DocumentSnapshot> getStudentProfile(String classId, String studentDocId) {
        return classroomRef.document(classId).collection("students").document(studentDocId).get();
    }

    // Gửi lời mời qua email (lưu vào collection con 'invites')
    public Task<DocumentReference> sendInvite(String classId, String email) {
        Map<String, Object> inviteData = new HashMap<>();
        inviteData.put("email", email);
        inviteData.put("status", "pending");
        inviteData.put("createdAt", com.google.firebase.Timestamp.now());
        return classroomRef.document(classId).collection("invites").add(inviteData);
    }

    // Lấy danh sách lời mời (CollectionReference)
    public CollectionReference getInvites(String classId) {
        return classroomRef.document(classId).collection("invites");
    }

    // Tạo một assignment mới cho lớp học
    public Task<DocumentReference> createAssignment(String classId, Map<String, Object> assignmentData) {
        return classroomRef.document(classId).collection("assignments").add(assignmentData);
    }

    // Giao bài tập cho một học sinh cụ thể (lưu vào collection con 'submissions')
    public Task<DocumentReference> assignToStudent(String classId, String assignmentId, String studentId, Map<String, Object> submissionData) {
        return classroomRef.document(classId).collection("assignments").document(assignmentId).collection("submissions").add(submissionData);
    }

    // Lấy danh sách các bài tập của lớp học
    public CollectionReference getClassAssignments(String classId) {
        return classroomRef.document(classId).collection("assignments");
    }

    // Lấy danh sách các bài nộp của một assignment
    public CollectionReference getAssignmentSubmissions(String classId, String assignmentId) {
        return classroomRef.document(classId).collection("assignments").document(assignmentId).collection("submissions");
    }

    // Chấm điểm bài nộp của học sinh
    public Task<Void> gradeSubmission(String classId, String assignmentId, String studentId, Map<String, Object> gradeData) {
        return classroomRef.document(classId).collection("assignments").document(assignmentId).collection("submissions").document(studentId).update(gradeData);
    }

    // Phương thức cũ để thêm assignment (có thể bị bỏ đi)
    @Deprecated
    public Task<DocumentReference> addAssignmentToClass(String classId, Map<String, Object> assignmentData) {
        return classroomRef.document(classId).collection("assignments").add(assignmentData);
    }
}
