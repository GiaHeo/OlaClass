package com.example.olaclass.ui.assignments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.olaclass.R;
import com.example.olaclass.data.model.QuizScoreDisplayItem;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class QuizScoresAdapter extends RecyclerView.Adapter<QuizScoresAdapter.ScoreViewHolder> {

    private List<QuizScoreDisplayItem> quizAttempts;

    public QuizScoresAdapter(List<QuizScoreDisplayItem> quizAttempts) {
        this.quizAttempts = quizAttempts;
    }

    public void setQuizAttempts(List<QuizScoreDisplayItem> quizAttempts) {
        this.quizAttempts = quizAttempts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_quiz_score, parent, false);
        return new ScoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreViewHolder holder, int position) {
        QuizScoreDisplayItem attempt = quizAttempts.get(position);
        holder.bind(attempt, position);
    }

    @Override
    public int getItemCount() {
        return quizAttempts.size();
    }

    static class ScoreViewHolder extends RecyclerView.ViewHolder {
        TextView tvStt;
        TextView tvStudentName;
        TextView tvGrade;
        TextView tvCorrectAnswers;
        TextView tvTimeTaken;
        TextView tvStartTime;

        public ScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStt = itemView.findViewById(R.id.tv_score_stt);
            tvStudentName = itemView.findViewById(R.id.tv_score_student_name);
            tvGrade = itemView.findViewById(R.id.tv_score_grade);
            tvCorrectAnswers = itemView.findViewById(R.id.tv_score_correct_answers);
            tvTimeTaken = itemView.findViewById(R.id.tv_score_time_taken);
            tvStartTime = itemView.findViewById(R.id.tv_score_start_time);
        }

        public void bind(QuizScoreDisplayItem attempt, int position) {
            tvStt.setText(String.valueOf(position + 1));
            tvStudentName.setText(attempt.getStudentName());
            tvGrade.setText(String.format(Locale.getDefault(), "%.1f", attempt.getScore()));
            tvCorrectAnswers.setText(String.format(Locale.getDefault(), "%d/%d", attempt.getCorrectAnswers(), attempt.getTotalQuestions()));

            long timeTakenMillis = attempt.getTimeTakenMillis();
            long minutes = TimeUnit.MILLISECONDS.toMinutes(timeTakenMillis);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(timeTakenMillis) % 60;
            tvTimeTaken.setText(String.format(Locale.getDefault(), "%d phút %d giây", minutes, seconds));

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            tvStartTime.setText(sdf.format(attempt.getStartTimeMillis()));
        }
    }
} 