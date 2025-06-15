package com.example.olaclass.ui.classroom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.olaclass.R;
import com.example.olaclass.data.model.Classroom;

public class ClassroomListAdapter extends PagingDataAdapter<Classroom, ClassroomListAdapter.ClassroomViewHolder> {
    public ClassroomListAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ClassroomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_classroom, parent, false);
        return new ClassroomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassroomViewHolder holder, int position) {
        Classroom classroom = getItem(position);
        if (classroom != null) {
            holder.bind(classroom);
        }
    }

    static class ClassroomViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvDesc;
        ClassroomViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_class_name);
            tvDesc = itemView.findViewById(R.id.tv_class_desc);
        }
        void bind(Classroom classroom) {
            tvName.setText(classroom.getName());
            tvDesc.setText(classroom.getDescription());
        }
    }

    private static final DiffUtil.ItemCallback<Classroom> DIFF_CALLBACK = new DiffUtil.ItemCallback<Classroom>() {
        @Override
        public boolean areItemsTheSame(@NonNull Classroom oldItem, @NonNull Classroom newItem) {
            return oldItem.getId().equals(newItem.getId());
        }
        @Override
        public boolean areContentsTheSame(@NonNull Classroom oldItem, @NonNull Classroom newItem) {
            return oldItem.equals(newItem);
        }
    };
}
