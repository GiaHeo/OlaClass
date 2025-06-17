package com.example.olaclass.ui.assignments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.olaclass.R;
import com.example.olaclass.data.model.Quiz;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {

    private List<Quiz> quizzes = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Quiz quiz);
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
        Quiz currentQuiz = quizzes.get(position);

        // Set basic quiz info
        holder.titleTextView.setText(currentQuiz.getTitle());
        holder.creatorTextView.setText("Người tạo: " + currentQuiz.getCreatorName());

        // Format and set time information
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String startTime = dateFormat.format(new Date(currentQuiz.getStartTime()));
        String endTime = dateFormat.format(new Date(currentQuiz.getEndTime()));

        holder.startTimeTextView.setText("Thời gian bắt đầu: " + startTime);
        holder.endTimeTextView.setText("Thời gian kết thúc: " + endTime);

        // Calculate remaining time
        long currentTime = System.currentTimeMillis();
        long timeRemaining = currentQuiz.getEndTime() - currentTime;

        if (timeRemaining > 0) {
            long minutesRemaining = timeRemaining / (60 * 1000);
            long hoursRemaining = minutesRemaining / 60;
            long daysRemaining = hoursRemaining / 24;

            String remainingTimeText = "Thời gian còn lại: ";
            if (daysRemaining > 0) {
                remainingTimeText += daysRemaining + " ngày ";
            }
            if (hoursRemaining % 24 > 0) {
                remainingTimeText += (hoursRemaining % 24) + " giờ ";
            }
            if (minutesRemaining % 60 > 0 && daysRemaining == 0) {
                remainingTimeText += (minutesRemaining % 60) + " phút";
            }
            holder.remainingTimeTextView.setText(remainingTimeText);
        } else {
            holder.remainingTimeTextView.setText("Đã hết thời gian làm bài");
        }

        // Set duration
        holder.durationTextView.setText("Thời lượng làm bài: " + currentQuiz.getDuration() + " phút");

        // Set status
        if (currentTime < currentQuiz.getStartTime()) {
            holder.statusTextView.setText("Trạng thái: Chưa bắt đầu");
            holder.statusTextView.setTextColor(holder.itemView.getContext().getColor(android.R.color.holo_blue_dark));
        } else if (currentTime <= currentQuiz.getEndTime()) {
            holder.statusTextView.setText("Trạng thái: Đang diễn ra");
            holder.statusTextView.setTextColor(holder.itemView.getContext().getColor(android.R.color.holo_green_dark));
        } else {
            holder.statusTextView.setText("Trạng thái: Đã kết thúc");
            holder.statusTextView.setTextColor(holder.itemView.getContext().getColor(android.R.color.holo_red_dark));
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(currentQuiz);
            }
        });
    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    static class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView creatorTextView;
        TextView startTimeTextView;
        TextView endTimeTextView;
        TextView remainingTimeTextView;
        TextView durationTextView;
        TextView statusTextView;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.tv_quiz_title_student);
            creatorTextView = itemView.findViewById(R.id.tv_quiz_creator_student);
            startTimeTextView = itemView.findViewById(R.id.tv_quiz_start_time_student);
            endTimeTextView = itemView.findViewById(R.id.tv_quiz_end_time_student);
            remainingTimeTextView = itemView.findViewById(R.id.tv_quiz_remaining_time_student);
            durationTextView = itemView.findViewById(R.id.tv_quiz_duration_student);
            statusTextView = itemView.findViewById(R.id.tv_quiz_status_student);
        }
    }
}