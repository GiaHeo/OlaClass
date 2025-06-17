package com.example.olaclass.ui.assignments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.olaclass.data.model.Classroom;
import com.example.olaclass.data.model.Quiz;
import com.example.olaclass.data.repository.ClassroomRepository;
import com.example.olaclass.data.repository.QuizAttemptRepository;
import com.example.olaclass.data.repository.QuizRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AssignmentsStudentViewModel extends ViewModel {

    private final ClassroomRepository classroomRepository;
    private final QuizRepository quizRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final FirebaseAuth mAuth;
    private final FirebaseFirestore db;

    private final MutableLiveData<List<Quiz>> _unattemptedQuizzes = new MutableLiveData<>();
    public LiveData<List<Quiz>> unattemptedQuizzes = _unattemptedQuizzes;

    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public LiveData<String> errorMessage = _errorMessage;

    public AssignmentsStudentViewModel() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        classroomRepository = new ClassroomRepository(db, mAuth);
        quizRepository = new QuizRepository();
        quizAttemptRepository = new QuizAttemptRepository();
        loadUnattemptedQuizzes();
    }

    public void loadUnattemptedQuizzes() {
        String currentUserId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (currentUserId == null) {
            _errorMessage.setValue("Không tìm thấy ID người dùng.");
            return;
        }

        classroomRepository.getClassroomsForStudent(currentUserId)
                .addOnSuccessListener(classrooms -> {
                    List<Task<List<Quiz>>> quizTasks = new ArrayList<>();
                    for (Classroom classroom : classrooms) {
                        quizTasks.add(quizRepository.getQuizzesForClassroom(classroom.getId()));
                    }

                    Tasks.whenAllSuccess(quizTasks)
                            .addOnSuccessListener(results -> {
                                List<Quiz> allQuizzes = new ArrayList<>();
                                for (Object result : results) {
                                    allQuizzes.addAll((List<Quiz>) result);
                                }

                                // Filter out quizzes that ended more than 30 minutes ago
                                long currentTime = System.currentTimeMillis();
                                List<Quiz> validQuizzes = new ArrayList<>();
                                for (Quiz quiz : allQuizzes) {
                                    long quizEndTime = quiz.getEndTime();
                                    // Keep quizzes that haven't ended yet or ended within the last 30 minutes
                                    if (currentTime <= quizEndTime + (30 * 60 * 1000)) {
                                        validQuizzes.add(quiz);
                                    }
                                }

                                List<Task<Boolean>> checkAttemptTasks = new ArrayList<>();
                                List<Quiz> quizzesToCheck = new ArrayList<>();

                                for (Quiz quiz : validQuizzes) {
                                    checkAttemptTasks.add(quizAttemptRepository.getQuizAttemptsForStudentAndQuiz(currentUserId, quiz.getId())
                                            .continueWith(task -> task.getResult() != null && !task.getResult().isEmpty()));
                                    quizzesToCheck.add(quiz);
                                }

                                if (checkAttemptTasks.isEmpty()) {
                                    _unattemptedQuizzes.setValue(new ArrayList<>());
                                    return;
                                }

                                Tasks.whenAllSuccess(checkAttemptTasks)
                                        .addOnSuccessListener(attemptResults -> {
                                            List<Quiz> unattempted = new ArrayList<>();
                                            for (int i = 0; i < attemptResults.size(); i++) {
                                                if (!(Boolean) attemptResults.get(i)) {
                                                    unattempted.add(quizzesToCheck.get(i));
                                                }
                                            }
                                            _unattemptedQuizzes.setValue(unattempted);
                                        })
                                        .addOnFailureListener(e -> {
                                            _errorMessage.setValue("Lỗi khi kiểm tra bài kiểm tra đã làm: " + e.getMessage());
                                        });
                            })
                            .addOnFailureListener(e -> {
                                _errorMessage.setValue("Lỗi tải bài kiểm tra cho lớp học: " + e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    _errorMessage.setValue("Lỗi tải lớp học: " + e.getMessage());
                });
    }
}