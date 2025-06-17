package com.example.olaclass.ui.assignments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.olaclass.R;
import com.example.olaclass.data.model.Classroom;
import com.example.olaclass.data.model.Quiz;
import com.example.olaclass.data.repository.ClassroomRepository;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class StudentQuizAdapter extends RecyclerView.Adapter<StudentQuizAdapter.QuizViewHolder> {

    private List<Quiz> quizzes = new ArrayList<>();
    private OnItemClickListener listener;
    private ClassroomRepository classroomRepository;

    public interface OnItemClickListener {
        void onItemClick(Quiz quiz);
    }

    public StudentQuizAdapter(ClassroomRepository classroomRepository) {
        this.classroomRepository = classroomRepository;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setQuizzes(List<Quiz> quizzes) {
        this.quizzes = quizzes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz_student, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Quiz quiz = quizzes.get(position);
        holder.bind(quiz);
    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuizTitle;
        TextView tvQuizCreator;
        TextView tvClassroomName;
        TextView tvQuizStartTime;
        TextView tvQuizEndTime;
        TextView tvQuizRemainingTime;
        TextView tvQuizDuration;
        TextView tvQuizStatus;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuizTitle = itemView.findViewById(R.id.tv_quiz_title_student);
            tvQuizCreator = itemView.findViewById(R.id.tv_quiz_creator_student);
            tvClassroomName = itemView.findViewById(R.id.tv_classroom_name_student);
            tvQuizStartTime = itemView.findViewById(R.id.tv_quiz_start_time_student);
            tvQuizEndTime = itemView.findViewById(R.id.tv_quiz_end_time_student);
            tvQuizRemainingTime = itemView.findViewById(R.id.tv_quiz_remaining_time_student);
            tvQuizDuration = itemView.findViewById(R.id.tv_quiz_duration_student);
            tvQuizStatus = itemView.findViewById(R.id.tv_quiz_status_student);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(quizzes.get(position));
                }
            });
        }

        public void bind(Quiz quiz) {
            tvQuizTitle.setText(quiz.getTitle());
            tvQuizCreator.setText("Người tạo: " + quiz.getCreatorName());

            if (quiz.getClassroomId() != null && !quiz.getClassroomId().isEmpty()) {
                classroomRepository.getClassroomById(quiz.getClassroomId())
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String classroomName = documentSnapshot.getString("name");
                                if (classroomName != null) {
                                    tvClassroomName.setText("Tên lớp: " + classroomName);
                                } else {
                                    tvClassroomName.setText("Tên lớp: Không tìm thấy");
                                }
                            } else {
                                tvClassroomName.setText("Tên lớp: Không tìm thấy");
                            }
                        })
                        .addOnFailureListener(e -> {
                            tvClassroomName.setText("Tên lớp: Lỗi tải");
                            System.err.println("Error loading classroom: " + e.getMessage());
                        });
            } else {
                tvClassroomName.setText("Tên lớp: N/A");
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

            tvQuizStartTime.setText("Thời gian bắt đầu: " + sdf.format(new Date(quiz.getStartTime())));
            tvQuizEndTime.setText("Thời gian kết thúc: " + sdf.format(new Date(quiz.getEndTime())));

            tvQuizDuration.setText("Thời lượng làm bài: " + quiz.getDuration() + " phút");

            long currentTime = System.currentTimeMillis();
            long remainingTimeMillis = quiz.getEndTime() - currentTime;

            if (remainingTimeMillis <= 0) {
                tvQuizRemainingTime.setText("Thời gian còn lại: Đã kết thúc");
                tvQuizRemainingTime.setTextColor(itemView.getContext().getResources().getColor(android.R.color.darker_gray));
                tvQuizStatus.setText("Trạng thái: Đã hết hạn");
                tvQuizStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.darker_gray));
            } else {
                long days = TimeUnit.MILLISECONDS.toDays(remainingTimeMillis);
                long hours = TimeUnit.MILLISECONDS.toHours(remainingTimeMillis) % 24;
                long minutes = TimeUnit.MILLISECONDS.toMinutes(remainingTimeMillis) % 60;
                long seconds = TimeUnit.MILLISECONDS.toSeconds(remainingTimeMillis) % 60;
                String remainingTimeText = String.format(Locale.getDefault(), "%d ngày, %d giờ, %d phút, %d giây", days, hours, minutes, seconds);
                tvQuizRemainingTime.setText("Thời gian còn lại: " + remainingTimeText);
                tvQuizRemainingTime.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
                
                if (currentTime < quiz.getStartTime()) {
                    tvQuizStatus.setText("Trạng thái: Sắp diễn ra");
                    tvQuizStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_blue_dark));
                } else {
                    tvQuizStatus.setText("Trạng thái: Đang diễn ra");
                    tvQuizStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
                }
            }
            tvQuizStatus.append(" (Chưa làm)");
        }
    }
} 