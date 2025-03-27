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
        // Gán layout cho activity này
        setContentView(R.layout.activity_admin);

        // Khai báo các Button và liên kết với ID trong layout
        Button btnManageMeals = findViewById(R.id.btnManageMeals);  // Nút quản lý món ăn
        Button btnManageUsers = findViewById(R.id.btnManageUsers);  // Nút quản lý người dùng
        Button btnLogout = findViewById(R.id.btnLogout);  // Nút đăng xuất

        // Load mặc định là fragment quản lý món ăn
        loadFragment(new ManageMealsFragment());

        // Sự kiện cho nút Quản lý Món Ăn
        btnManageMeals.setOnClickListener(view -> loadFragment(new ManageMealsFragment()));

        // Sự kiện cho nút Quản lý Người Dùng
        btnManageUsers.setOnClickListener(view -> {
            // Chuyển đến Activity quản lý người dùng
            Intent intent = new Intent(AdminActivity.this, ManageUser.class);
            startActivity(intent);
        });

        // Sự kiện cho nút Đăng Xuất
        btnLogout.setOnClickListener(view -> {
            // Xử lý đăng xuất, quay lại màn hình đăng nhập (LoginActivity)
            finish();  // Kết thúc Activity hiện tại và quay lại màn hình trước
        });
    }

    // Hàm để load fragment vào container (chỗ chứa fragment)
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);  // Thay thế fragment hiện tại bằng fragment mới
        transaction.commit();  // Xác nhận và thực thi giao dịch
    }
}
