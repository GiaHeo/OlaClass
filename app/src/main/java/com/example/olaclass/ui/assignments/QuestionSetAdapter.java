package com.example.olaclass.ui.assignments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.olaclass.R;
import com.example.olaclass.data.model.QuestionSet;

import java.util.ArrayList;
import java.util.List;

public class QuestionSetAdapter extends RecyclerView.Adapter<QuestionSetAdapter.QuestionSetViewHolder> {

    private List<QuestionSet> questionSets = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(QuestionSet questionSet);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setQuestionSets(List<QuestionSet> questionSets) {
        this.questionSets = questionSets;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public QuestionSetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question_set, parent, false);
        return new QuestionSetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionSetViewHolder holder, int position) {
        QuestionSet currentQuestionSet = questionSets.get(position);
        holder.titleTextView.setText(currentQuestionSet.getTitle());
        // Display the number of questions in the set
        if (currentQuestionSet.getQuestions() != null) {
            holder.questionCountTextView.setText(currentQuestionSet.getQuestions().size() + " câu hỏi");
        } else {
            holder.questionCountTextView.setText("0 câu hỏi");
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(currentQuestionSet);
            }
        });
    }

    @Override
    public int getItemCount() {
        return questionSets.size();
    }

    static class QuestionSetViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView questionCountTextView;

        public QuestionSetViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.text_view_question_set_title);
            questionCountTextView = itemView.findViewById(R.id.text_view_question_count);
        }
    }
} 