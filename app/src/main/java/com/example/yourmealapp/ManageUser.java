package com.example.yourmealapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourmealapp.models.User;

import java.util.List;

public class ManageUser extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_user);

        // Ánh xạ RecyclerView từ layout
        recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Lấy danh sách người dùng từ DB
        DBHelper dbHelper = new DBHelper(this);
        userList = dbHelper.getAllUsers();

        // Kiểm tra xem danh sách người dùng có dữ liệu không
        if (userList.isEmpty()) {
            Toast.makeText(this, "Không có người dùng", Toast.LENGTH_SHORT).show();
        }

        // Khởi tạo adapter và gán cho RecyclerView
        userAdapter = new UserAdapter(userList);
        recyclerView.setAdapter(userAdapter);

    }

    public void backToAdPage(View view) {
        Intent intent = new Intent();
        finish();
    }
}
