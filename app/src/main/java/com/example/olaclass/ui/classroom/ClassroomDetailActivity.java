package com.example.olaclass.ui.classroom;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import androidx.core.content.ContextCompat;
import com.example.olaclass.ui.classroom.AssignmentsTeacherClassroomFragment;
import com.example.olaclass.ui.classroom.AssignmentsStudentClassroomFragment;

public class ClassroomDetailActivity extends AppCompatActivity {
    private String classroomId;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentUserRole; // To store the role of the current user
    private FloatingActionButton fabActions; // Declare FAB here
    
    private static final String TAG = "ClassroomDetailActivity"; // Define TAG here
    
    public String getClassroomId() {
        return classroomId;
    }
    
    public String getCurrentUserRole() {
        return currentUserRole;
    }
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom_detail);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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
        
        // Setup Floating Action Button
        fabActions = findViewById(R.id.fab_actions); // Initialize FAB here
        fabActions.setVisibility(View.GONE); // Initially hide the FAB until role is determined
        fabActions.setOnClickListener(v -> showClassroomActionsMenu(v));

        // Fetch user role and invalidate options menu
        fetchUserRole(viewPager, tabLayout);
    }

    private void fetchUserRole(ViewPager2 viewPager, TabLayout tabLayout) {
        if (mAuth.getCurrentUser() != null) {
            db.collection("users").document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        currentUserRole = documentSnapshot.getString("role");
                        Log.d(TAG, "Vai trò người dùng đã tải: " + currentUserRole);
                        Log.d(TAG, "ClassroomDetailActivity: currentUserRole after fetch: " + currentUserRole);

                        // Set up ViewPager adapter after role is fetched
                        viewPager.setAdapter(new ClassroomPagerAdapter(this, classroomId, currentUserRole));
                        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                            switch (position) {
                                case 0:
                                    tab.setText("Thông tin lớp");
                                    break;
                                case 1:
                                    tab.setText("Danh sách lớp");
                                    break;
                                case 2:
                                    tab.setText("Bài tập trên lớp");
                                    break;
                            }
                        }).attach();

                        // Now that role is known, set FAB visibility based on role
                        if ("student".equals(currentUserRole)) {
                            fabActions.setVisibility(View.GONE);
                        } else if ("teacher".equals(currentUserRole)) {
                            fabActions.setVisibility(View.VISIBLE);
                        } else {
                            fabActions.setVisibility(View.GONE); // Default to hiding if role is unknown
                        }
                        // Invalidate the options menu to redraw it with updated visibility
                        invalidateOptionsMenu(); 
                    } else {
                        // User document not found, hide FAB and disable viewpager
                        fabActions.setVisibility(View.GONE);
                        viewPager.setEnabled(false);
                        Log.w(TAG, "Không tìm thấy tài liệu người dùng cho ID: " + mAuth.getCurrentUser().getUid());
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi tải vai trò người dùng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Lỗi khi tải vai trò người dùng: " + e.getMessage(), e);
                    // On error, hide FAB and disable viewpager
                    fabActions.setVisibility(View.GONE);
                    viewPager.setEnabled(false);
                });
        } else {
            // No current user, hide FAB and disable viewpager
            fabActions.setVisibility(View.GONE);
            viewPager.setEnabled(false);
            Log.w(TAG, "Không có người dùng hiện tại, ẩn FAB.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.classroom_actions, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG, "onPrepareOptionsMenu: Vai trò người dùng hiện tại: " + currentUserRole);
        MenuItem createAnnouncement = menu.findItem(R.id.action_create_announcement);
        MenuItem createQuiz = menu.findItem(R.id.action_create_quiz);
        MenuItem deleteClassroom = menu.findItem(R.id.action_delete_classroom);
        MenuItem leaveClassroom = menu.findItem(R.id.action_leave_classroom);

        if ("teacher".equals(currentUserRole)) {
            createAnnouncement.setVisible(true);
            createQuiz.setVisible(true);
            deleteClassroom.setVisible(true);
            leaveClassroom.setVisible(false);
            Log.d(TAG, "Đặt menu cho giáo viên.");
        } else if ("student".equals(currentUserRole)) {
            createAnnouncement.setVisible(false);
            createQuiz.setVisible(false);
            deleteClassroom.setVisible(false);
            leaveClassroom.setVisible(true);
            Log.d(TAG, "Đặt menu cho học sinh.");
        } else {
            // Default to hiding all actions if role is unknown or not set
            createAnnouncement.setVisible(false);
            createQuiz.setVisible(false);
            deleteClassroom.setVisible(false);
            leaveClassroom.setVisible(false);
            Log.d(TAG, "Ẩn tất cả các mục menu do vai trò không xác định hoặc chưa được đặt.");
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void showClassroomActionsMenu(View anchor) {
        Log.d(TAG, "showClassroomActionsMenu: Vai trò người dùng hiện tại: " + currentUserRole);
        // Tạo PopupMenu với style tùy chỉnh
        Context wrapper = new android.view.ContextThemeWrapper(this, R.style.CustomPopupMenu);
        PopupMenu popup = new PopupMenu(wrapper, anchor);
        popup.getMenuInflater().inflate(R.menu.classroom_actions, popup.getMenu());
        
        // Re-apply visibility logic for PopupMenu
        MenuItem createAnnouncement = popup.getMenu().findItem(R.id.action_create_announcement);
        MenuItem createQuiz = popup.getMenu().findItem(R.id.action_create_quiz);
        MenuItem deleteClassroom = popup.getMenu().findItem(R.id.action_delete_classroom);
        MenuItem leaveClassroom = popup.getMenu().findItem(R.id.action_leave_classroom);

        if ("teacher".equals(currentUserRole)) {
            createAnnouncement.setVisible(true);
            createQuiz.setVisible(true);
            deleteClassroom.setVisible(true);
            leaveClassroom.setVisible(false);
            // Set delete option to red for teacher's menu
            android.text.SpannableString s = new android.text.SpannableString(deleteClassroom.getTitle());
            s.setSpan(new android.text.style.ForegroundColorSpan(ContextCompat.getColor(this, R.color.red)), 0, s.length(), 0);
            deleteClassroom.setTitle(s);
            Log.d(TAG, "Đặt PopupMenu cho giáo viên. Đã đặt màu đỏ cho Xóa lớp học.");
        } else if ("student".equals(currentUserRole)) {
            createAnnouncement.setVisible(false);
            createQuiz.setVisible(false);
            deleteClassroom.setVisible(false);
            leaveClassroom.setVisible(true);
            Log.d(TAG, "Đặt PopupMenu cho học sinh.");
        } else {
            // Default to hiding all actions if role is unknown or not set
            createAnnouncement.setVisible(false);
            createQuiz.setVisible(false);
            deleteClassroom.setVisible(false);
            leaveClassroom.setVisible(false);
            Log.d(TAG, "Ẩn tất cả các mục PopupMenu do vai trò không xác định hoặc chưa được đặt.");
        }

        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_create_announcement) {
                createNewAnnouncement();
                return true;
            } else if (id == R.id.action_create_quiz) {
                createNewQuiz();
                return true;
            } else if (id == R.id.action_delete_classroom) {
                showDeleteClassroomDialog();
                return true;
            } else if (id == R.id.action_leave_classroom) {
                showLeaveClassroomConfirmation();
                return true;
            }
            return false;
        });

        
        popup.show();
    }
    
    private void createNewAnnouncement() {
        // Open CreateAnnouncementActivity with the classroom ID
        Intent intent = new Intent(this, CreateAnnouncementActivity.class);
        intent.putExtra("classroomId", classroomId);
        startActivity(intent);
    }

    private void createNewQuiz() {
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
        db.collection("classrooms").document(classroomId) // Use db instance
            .delete()
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Đã xóa lớp học", Toast.LENGTH_SHORT).show();
                finish();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Lỗi khi xóa lớp: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
    }

    private void showLeaveClassroomConfirmation() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Rời khỏi lớp học")
                .setMessage("Bạn có chắc chắn muốn rời khỏi lớp học này không?")
                .setPositiveButton("Rời khỏi", (dialog, which) -> leaveClassroom())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void leaveClassroom() {
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (userId == null || classroomId == null || classroomId.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không thể rời khỏi lớp học", Toast.LENGTH_SHORT).show();
            return;
        }

        // Remove student from the classroom's 'students' array
        db.collection("classrooms").document(classroomId)
                .update("students", FieldValue.arrayRemove(userId)) // Use FieldValue
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Đã rời khỏi lớp học", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity after leaving the classroom
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi rời khỏi lớp học: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
        private final String currentUserRole;
        public ClassroomPagerAdapter(FragmentActivity fa, String classroomId, String currentUserRole) {
            super(fa);
            this.classroomId = classroomId;
            this.currentUserRole = currentUserRole;
        }
        @Override
        public int getItemCount() {
            return 3;
        }
        @Override
        public Fragment createFragment(int position) {
            Log.d(TAG, "ClassroomPagerAdapter: creating fragment for position " + position + " with role: " + currentUserRole);
            Bundle args = new Bundle();
            args.putString("classroomId", classroomId);

            switch (position) {
                case 0:
                    ClassroomInfoFragment infoFragment = new ClassroomInfoFragment();
                    infoFragment.setArguments(args);
                    return infoFragment;
                case 1:
                    return StudentListFragment.newInstance(classroomId);
                case 2:
                    // Dynamic fragment loading based on role
                    if ("teacher".equals(currentUserRole)) {
                        AssignmentsTeacherClassroomFragment teacherAssignmentsFragment = new AssignmentsTeacherClassroomFragment();
                        teacherAssignmentsFragment.setArguments(args);
                        return teacherAssignmentsFragment;
                    } else if ("student".equals(currentUserRole)) {
                        return AssignmentsStudentClassroomFragment.newInstance(classroomId);
                    } else {
                        return null;
                    }
                default:
                    return null;
            }
        }
    }
}

