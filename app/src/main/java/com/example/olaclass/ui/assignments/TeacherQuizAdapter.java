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

public class TeacherQuizAdapter extends RecyclerView.Adapter<TeacherQuizAdapter.QuizViewHolder> {

    private List<Quiz> quizzes = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Quiz quiz);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setQuizzes(List<Quiz> quizzes) {
        this.quizzes = quizzes != null ? quizzes : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Quiz currentQuiz = quizzes.get(position);
        holder.titleTextView.setText(currentQuiz.getTitle());
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String startTime = dateFormat.format(new Date(currentQuiz.getStartTime()));
        String endTime = dateFormat.format(new Date(currentQuiz.getEndTime()));
        holder.timeTextView.setText("Thời gian: " + startTime + " - " + endTime);
        
        holder.durationTextView.setText("Thời lượng: " + currentQuiz.getDuration() + " phút");
        
        // Show creator name instead of attempts for now
        String creatorInfo = "Tạo bởi: " + currentQuiz.getCreatorName();
        holder.attemptsTextView.setText(creatorInfo);

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
        TextView timeTextView;
        TextView durationTextView;
        TextView attemptsTextView;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.text_view_quiz_title);
            timeTextView = itemView.findViewById(R.id.text_view_quiz_time);
            durationTextView = itemView.findViewById(R.id.text_view_quiz_duration);
            attemptsTextView = itemView.findViewById(R.id.text_view_quiz_attempts);
        }
    }
}
