package com.example.olaclass.ui.teacher;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.olaclass.R;
import com.example.olaclass.ui.MainActivity;
import com.example.olaclass.ui.assignments.AssignmentsTeacherFragment;
import com.example.olaclass.ui.classroom.ClassroomListFragmentTeacher;
import com.example.olaclass.ui.profile.ProfileFragment;
import com.example.olaclass.utils.AuthUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MainActivityTeacher extends MainActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_teacher);
        
        setupBottomNavigation();
        loadInitialFragment();
    }

    @Override
    protected void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            
            if (itemId == R.id.nav_classrooms) {
                selectedFragment = ClassroomListFragmentTeacher.newInstance("teacher");
            } else if (itemId == R.id.nav_assignments) {
                selectedFragment = new AssignmentsTeacherFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }
            
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.teacher_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_logout) {
            new MaterialAlertDialogBuilder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Có", (dialog, which) -> {
                    AuthUtils.signOut(this);
                })
                .setNegativeButton("Không", null)
                .show();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void loadInitialFragment() {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, ClassroomListFragmentTeacher.newInstance("teacher"))
            .commit();
    }
}
