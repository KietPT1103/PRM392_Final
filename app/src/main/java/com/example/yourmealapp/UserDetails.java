package com.example.yourmealapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class UserDetails extends AppCompatActivity {

    TextView tvUsername, tvFullname, tvEmail, tvPhone, tvRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        tvUsername = findViewById(R.id.tvUsername);
        tvFullname = findViewById(R.id.tvFullname);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvRole = findViewById(R.id.tvRole);

        // Lấy dữ liệu từ Intent
        String username = getIntent().getStringExtra("username");
        String fullname = getIntent().getStringExtra("fullname");
        String email = getIntent().getStringExtra("email");
        String phone = getIntent().getStringExtra("phone");
        String role = getIntent().getStringExtra("role");

        // Gán lên giao diện
        tvUsername.setText(username);
        tvFullname.setText(fullname);
        tvEmail.setText(email);
        tvPhone.setText(phone);
        tvRole.setText(role);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish()); // đóng activity, quay lại
    }
}
