package com.example.yourmealapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourmealapp.adapters.MealHistoryAdapter;
import com.example.yourmealapp.models.Meal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class HomeActivity extends AppCompatActivity {
    private TextView mealTextView;
    private EditText searchEditText;
    private ImageButton btnSearch, btnSuggestMeal, btnAcceptMeal, btnLogout;
    private RecyclerView historyRecyclerView, favoriteRecyclerView;
    private MealHistoryAdapter mealHistoryAdapter, favoriteAdapter;
    private DBHelper dbHelper;
    private String currentUsername;
    private Meal currentMeal;

    private List<Meal> historyList, filteredHistoryList;
    private Handler handler = new Handler();
    private int scrollPosition = 0;
    private Runnable autoScrollRunnable;

    private Handler searchHandler = new Handler();
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // Ánh xạ View
        mealTextView = findViewById(R.id.mealTextView);
        mealTextView.setVisibility(View.VISIBLE);
        mealTextView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

        searchEditText = findViewById(R.id.searchEditText);
        btnSearch = findViewById(R.id.btnSearch);
        btnSuggestMeal = findViewById(R.id.btnSuggestMeal);
        btnAcceptMeal = findViewById(R.id.btnAcceptMeal);
        btnLogout = findViewById(R.id.btnLogout);
        historyRecyclerView = findViewById(R.id.historyRecyclerView);
        favoriteRecyclerView = findViewById(R.id.favoriteRecyclerView);

        dbHelper = new DBHelper(this);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentUsername = sharedPreferences.getString("username", null);

        if (currentUsername != null) {
            suggestMeal();
            loadMealHistory();
        } else {
            mealTextView.setText("Lỗi: Không lấy được thông tin user!");
        }

        setButtonEffect(btnSuggestMeal);
        setButtonEffect(btnAcceptMeal);
        btnSuggestMeal.setOnClickListener(v -> suggestMeal());
        btnAcceptMeal.setOnClickListener(v -> acceptMeal());
        btnLogout.setOnClickListener(v -> showLogoutDialog());

        loadFavoriteMeals();

        // Xử lý tìm kiếm
        btnSearch.setOnClickListener(v -> performSearch());

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchHandler.removeCallbacks(searchRunnable); // Hủy tìm kiếm cũ
                searchRunnable = () -> performSearch(); // Tạo tìm kiếm mới
                searchHandler.postDelayed(searchRunnable, 300); // Hoãn tìm kiếm 300ms
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> performLogout())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void performLogout() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        prefs.edit().clear().apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void suggestMeal() {
        if (currentUsername != null) {
            currentMeal = dbHelper.suggestMeal(currentUsername, 10);
            if (currentMeal != null) {
                int likes = getRandomLikes(); // Lấy số lượt thích ngẫu nhiên
                mealTextView.setText("Gợi ý hôm nay: " + currentMeal.getName() + " (" + likes + " like)");
            } else {
                mealTextView.setText("Không có món ăn phù hợp!");
            }
        }
    }

    private void acceptMeal() {
        if (currentMeal != null) {
            boolean success = dbHelper.saveMealHistory(currentUsername, currentMeal.getId(), getCurrentDate());
            if (success) {
                Toast.makeText(this, "Bạn đã chọn: " + currentMeal.getName(), Toast.LENGTH_SHORT).show();
                loadMealHistory();
            } else {
                Toast.makeText(this, "Lỗi khi lưu lịch sử món ăn!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Chưa có món ăn nào được chọn!", Toast.LENGTH_SHORT).show();
        }
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void loadMealHistory() {
        historyList = dbHelper.getUserMealHistory(currentUsername);
        filteredHistoryList = new ArrayList<>(historyList);

        mealHistoryAdapter = new MealHistoryAdapter(filteredHistoryList, this, currentUsername, false, this::loadFavoriteMeals);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        historyRecyclerView.setAdapter(mealHistoryAdapter);
        startAutoScroll();
    }

    private void startAutoScroll() {
        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                if (scrollPosition < mealHistoryAdapter.getItemCount()) {
                    historyRecyclerView.smoothScrollToPosition(scrollPosition);
                    scrollPosition++;
                } else {
                    scrollPosition = 0;
                }
                handler.postDelayed(this, 3000);
            }
        };
        handler.postDelayed(autoScrollRunnable, 3000);
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchHandler.removeCallbacks(searchRunnable); // Xóa các tìm kiếm cũ
                searchRunnable = () -> performSearch(s.toString().trim().toLowerCase(Locale.ROOT));
                searchHandler.postDelayed(searchRunnable, 300); // Đợi 300ms mới thực hiện tìm kiếm
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    private void performSearch() {
        String keyword = searchEditText.getText().toString().trim().toLowerCase(Locale.ROOT);
        performSearch(keyword);
    }

    private void performSearch(String keyword) {
        List<Meal> newFilteredList = new ArrayList<>();

        // Loại bỏ dấu và chuyển về chữ thường từ chuỗi tìm kiếm
        String processedKeyword = removeAccents(keyword.toLowerCase(Locale.ROOT));

        if (processedKeyword.isEmpty()) {
            newFilteredList.addAll(historyList);
        } else {
            String[] keywords = processedKeyword.split("\\s+");

            for (Meal meal : historyList) {
                // Loại bỏ dấu và chuyển về chữ thường từ tên món ăn
                String processedMealName = removeAccents(meal.getName().toLowerCase(Locale.ROOT));
                boolean match = true;

                for (String word : keywords) {
                    if (!processedMealName.contains(word)) {
                        match = false;
                        break;
                    }
                }

                if (match) {
                    newFilteredList.add(meal);
                }
            }
        }

        // Chỉ cập nhật danh sách nếu có thay đổi
        if (!newFilteredList.equals(filteredHistoryList)) {
            filteredHistoryList.clear();
            filteredHistoryList.addAll(newFilteredList);
            mealHistoryAdapter.notifyDataSetChanged();
        }

        if (filteredHistoryList.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy món ăn phù hợp!", Toast.LENGTH_SHORT).show();
        }
    }

    // Hàm loại bỏ dấu tiếng Việt
    private String removeAccents(String str) {
        String nfdNormalizedString = java.text.Normalizer.normalize(str, java.text.Normalizer.Form.NFD);
        return nfdNormalizedString.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(autoScrollRunnable);
    }

    private void setButtonEffect(ImageButton button) {
        button.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_click_effect));
            }
            return false;
        });
    }

    public void userInfo(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        if (username != null) {
            Intent intent = new Intent(HomeActivity.this, UserProfile.class);
            intent.putExtra("username", username);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadFavoriteMeals() {
        List<Meal> favorites = dbHelper.getFavoriteMeals(currentUsername);
        favoriteAdapter = new MealHistoryAdapter(favorites, this, currentUsername, true, null);
        favoriteRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        favoriteRecyclerView.setAdapter(favoriteAdapter);
    }

    private int getRandomLikes() {
        Random random = new Random();
        return random.nextInt(1000); // Tạo số lượt thích ngẫu nhiên từ 0 đến 999
    }


}
