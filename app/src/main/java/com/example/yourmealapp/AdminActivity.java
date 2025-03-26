package com.example.yourmealapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Khai báo các Button
        Button btnManageMeals = findViewById(R.id.btnManageMeals);
        Button btnManageUsers = findViewById(R.id.btnManageUsers); // Nút quản lý người dùng
        Button btnLogout = findViewById(R.id.btnLogout);

        // Load mặc định là trang Quản lý Món Ăn
        loadFragment(new ManageMealsFragment());

        // Sự kiện cho nút Quản lý Món Ăn
        btnManageMeals.setOnClickListener(view -> loadFragment(new ManageMealsFragment()));

        // Sự kiện cho nút Quản lý Người Dùng
        btnManageUsers.setOnClickListener(view -> {
            // Chuyển đến ManageUsersActivity
            Intent intent = new Intent(AdminActivity.this, ManageUser.class);
            startActivity(intent);
        });

        // Sự kiện cho nút Đăng Xuất
        btnLogout.setOnClickListener(view -> {
            // Xử lý đăng xuất (chuyển về LoginActivity)
            finish();
        });
    }

    // Hàm để load fragment vào container
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }
}
