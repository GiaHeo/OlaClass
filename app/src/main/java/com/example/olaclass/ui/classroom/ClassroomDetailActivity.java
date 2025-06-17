package com.example.olaclass.ui.classroom;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.example.olaclass.ui.assignments.CreateQuizActivity;
import com.example.olaclass.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ClassroomDetailActivity extends AppCompatActivity {
    private String classroomId;
    
    public String getClassroomId() {
        return classroomId;
    }
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom_detail);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết lớp học");
        }

        // Lấy classroomId từ Intent
        classroomId = getIntent().getStringExtra("classroomId");
        if (classroomId == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy lớp học", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup ViewPager và TabLayout
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new ClassroomPagerAdapter(this, classroomId));
        
        // Setup Floating Action Button
        setupFloatingActionButton();

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Thông tin lớp");
                    break;
                case 1:
                    tab.setText("Học sinh");
                    break;
                case 2:
                    tab.setText("Bài tập & Lời mời");
                    break;
            }
        }).attach();
    }

    private void setupFloatingActionButton() {
        FloatingActionButton fabActions = findViewById(R.id.fab_actions);
        fabActions.setOnClickListener(v -> showClassroomActionsMenu(v));
    }
    
    private void showClassroomActionsMenu(View anchor) {
        // Tạo PopupMenu với style tùy chỉnh
        Context wrapper = new android.view.ContextThemeWrapper(this, R.style.CustomPopupMenu);
        PopupMenu popup = new PopupMenu(wrapper, anchor);
        popup.getMenuInflater().inflate(R.menu.classroom_actions, popup.getMenu());
        
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_create_announcement) {
                createNewAnnouncement();
                return true;
            } else if (id == R.id.action_create_quiz) {
                createNewQuiz();
                return true;
            } else if (id == R.id.action_delete_classroom) {
                // Đổi màu chữ menu thành đỏ
                android.view.MenuItem deleteItem = item;
                android.text.SpannableString s = new android.text.SpannableString(deleteItem.getTitle());
                s.setSpan(new android.text.style.ForegroundColorSpan(getResources().getColor(R.color.red)), 0, s.length(), 0);
                deleteItem.setTitle(s);
                showDeleteClassroomDialog();
                return true;
            }
            return false;
        });

        
        popup.show();
    }
    
    private void createNewAnnouncement() {
        // TODO: Mở màn hình tạo thông báo mới
        Toast.makeText(this, "Mở màn hình tạo thông báo cho lớp " + classroomId, 
            Toast.LENGTH_SHORT).show();
    }

    private void createNewQuiz() {
        // TODO: Mở màn hình tạo bài kiểm tra mới
        // Toast.makeText(this, "Mở màn hình tạo bài kiểm tra cho lớp " + classroomId,
        //     Toast.LENGTH_SHORT).show();
        
        Intent intent = new Intent(this, CreateQuizActivity.class);
        intent.putExtra("classroomId", classroomId);
        startActivity(intent);
    }

    private void showDeleteClassroomDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Xóa lớp học")
            .setMessage("Bạn có chắc chắn muốn xóa lớp học này? Hành động này không thể hoàn tác!")
            .setPositiveButton("Xóa", (dialog, which) -> deleteClassroom())
            .setNegativeButton("Hủy", null)
            .show();
    }

    private void deleteClassroom() {
        if (classroomId == null || classroomId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy lớp học để xóa", Toast.LENGTH_SHORT).show();
            return;
        }
        // Xóa classroom trong Firestore
        com.google.firebase.firestore.FirebaseFirestore.getInstance()
            .collection("classrooms").document(classroomId)
            .delete()
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Đã xóa lớp học", Toast.LENGTH_SHORT).show();
                finish();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Lỗi khi xóa lớp: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static class ClassroomPagerAdapter extends FragmentStateAdapter {
        private final String classroomId;
        public ClassroomPagerAdapter(FragmentActivity fa, String classroomId) {
            super(fa);
            this.classroomId = classroomId;
        }
        @Override
        public int getItemCount() {
            return 3;
        }
        @Override
        public Fragment createFragment(int position) {
            Bundle args = new Bundle();
            args.putString("classroomId", classroomId);
            switch (position) {
                case 0:
                    ClassroomInfoFragment infoFragment = new ClassroomInfoFragment();
                    infoFragment.setArguments(args);
                    return infoFragment;
                case 1:
                    StudentListFragment studentFragment = new StudentListFragment();
                    studentFragment.setArguments(args);
                    return studentFragment;
                case 2:
                default:
                    AssignmentInviteFragment assignFragment = new AssignmentInviteFragment();
                    assignFragment.setArguments(args);
                    return assignFragment;
            }
        }
    }
}

