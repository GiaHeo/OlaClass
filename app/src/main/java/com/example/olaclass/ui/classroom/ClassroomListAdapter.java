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
    private Classroom selectedClassroom;
    
    public interface OnClassroomClickListener {
        void onClassroomClick(Classroom classroom);
        void onClassroomLongClick(View view, Classroom classroom);
    }
    
    public void setSelectedClassroom(Classroom classroom) {
        this.selectedClassroom = classroom;
    }
    
    public Classroom getSelectedClassroom() {
        return selectedClassroom;
    }
    private OnClassroomClickListener listener;
    public ClassroomListAdapter(OnClassroomClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClassroomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_classroom, parent, false);
        return new ClassroomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassroomViewHolder holder, int position) {
        try {
            Classroom classroom = getItem(position);
            if (classroom != null) {
                holder.bind(classroom, listener);
            } else {
                // Hiển thị view trống hoặc thông báo lỗi
                holder.clear();
            }
        } catch (Exception e) {
            android.util.Log.e("ClassroomListAdapter", "Lỗi khi bind view tại vị trí " + position + ": " + e.getMessage(), e);
            holder.clear();
        }
    }

    class ClassroomViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvDesc;
        
        public void clear() {
            tvName.setText("");
            tvDesc.setText("");
            itemView.setOnClickListener(null);
        }
        
        public ClassroomViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_class_name);
            tvDesc = itemView.findViewById(R.id.tv_class_desc);
        }
        
        void bind(Classroom classroom, OnClassroomClickListener listener) {
            tvName.setText(classroom.getName());
            tvDesc.setText(classroom.getDescription());
            
            // Đánh dấu item được chọn
            itemView.setSelected(selectedClassroom != null && 
                selectedClassroom.getId().equals(classroom.getId()));
            
            itemView.setOnClickListener(v -> {
                // Cập nhật lớp học được chọn
                if (ClassroomListAdapter.this.selectedClassroom == null || 
                    !ClassroomListAdapter.this.selectedClassroom.getId().equals(classroom.getId())) {
                    ClassroomListAdapter.this.selectedClassroom = classroom;
                    notifyDataSetChanged();
                }
                
                if (listener != null) {
                    listener.onClassroomClick(classroom);
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onClassroomLongClick(v, classroom);
                }
                return true;
            });
        }
    }

    private static final DiffUtil.ItemCallback<Classroom> DIFF_CALLBACK = new DiffUtil.ItemCallback<Classroom>() {
        @Override
        public boolean areItemsTheSame(@NonNull Classroom oldItem, @NonNull Classroom newItem) {
            return oldItem != null && newItem != null && 
                   oldItem.getId() != null && oldItem.getId().equals(newItem.getId());
        }
        
        @Override
        public boolean areContentsTheSame(@NonNull Classroom oldItem, @NonNull Classroom newItem) {
            if (oldItem == newItem) return true;
            if (oldItem == null || newItem == null) return false;
            return oldItem.equals(newItem);
        }
    };
}
