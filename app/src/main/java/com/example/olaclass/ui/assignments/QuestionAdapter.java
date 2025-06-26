package com.example.olaclass.ui.assignments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.olaclass.R;
import com.example.olaclass.data.model.Question;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {
    private final List<Question> questions;

    public QuestionAdapter(List<Question> questions) {
        this.questions = questions;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question_attempt, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question question = questions.get(position);
        holder.bind(question, position);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    static class QuestionViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvQuestionContent;
        private final RadioGroup radioGroupOptions;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestionContent = itemView.findViewById(R.id.tv_question_content);
            radioGroupOptions = itemView.findViewById(R.id.radio_group_options);
        }

        public void bind(Question question, int position) {
            tvQuestionContent.setText((position + 1) + ". " + question.getContent());
            radioGroupOptions.removeAllViews();
            if (question.getOptions() != null) {
                for (int i = 0; i < question.getOptions().size(); i++) {
                    Question.Option option = question.getOptions().get(i);
                    RadioButton radioButton = new RadioButton(itemView.getContext());
                    radioButton.setText(option.getContent());
                    radioButton.setId(View.generateViewId());
                    radioGroupOptions.addView(radioButton);
                }
            }
        }
    }
} 