package com.example.yourmealapp;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.yourmealapp.adapters.MealAdapter;
import com.example.yourmealapp.adapters.MealAdapterListener;
import com.example.yourmealapp.models.Meal;

import java.util.ArrayList;

public class MealListFragment extends Fragment {
    private DBHelper dbHelper; // Đối tượng DBHelper để truy cập cơ sở dữ liệu
    private RecyclerView recyclerViewMeals; // RecyclerView để hiển thị danh sách các món ăn
    private MealAdapter mealAdapter; // Adapter để quản lý và hiển thị các món ăn trong RecyclerView

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho Fragment
        View view = inflater.inflate(R.layout.fragment_meal_list, container, false);

        // Khởi tạo DBHelper để truy cập cơ sở dữ liệu
        dbHelper = new DBHelper(getContext());

        // Liên kết RecyclerView từ layout
        recyclerViewMeals = view.findViewById(R.id.recyclerViewMeals);
        recyclerViewMeals.setLayoutManager(new LinearLayoutManager(getContext())); // Sử dụng LinearLayoutManager để hiển thị danh sách theo chiều dọc

        // Liên kết SearchView từ layout
        SearchView searchView = view.findViewById(R.id.searchView);

        // Thiết lập sự kiện cho ô tìm kiếm
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Khi người dùng nhấn Enter, lọc danh sách món ăn theo từ khóa tìm kiếm
                filterMeals(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Khi người dùng thay đổi văn bản trong ô tìm kiếm, lọc danh sách món ăn theo từ khóa tìm kiếm
                filterMeals(newText);
                return true;
            }
        });

        // Khởi tạo MealAdapter và thiết lập các listener cho các hành động chỉnh sửa và xóa món ăn
        mealAdapter = new MealAdapter(new ArrayList<>(), new MealAdapterListener() {
            @Override
            public void onEditMeal(Meal meal) {
                // Khi người dùng nhấn "Chỉnh sửa", mở dialog để chỉnh sửa món ăn
                showEditMealDialog(meal);
            }

            @Override
            public void onDeleteMeal(Meal meal) {
                // Khi người dùng nhấn "Xóa", mở dialog xác nhận xóa món ăn
                showDeleteConfirmationDialog(meal);
            }
        });

        // Gán adapter vào RecyclerView
        recyclerViewMeals.setAdapter(mealAdapter);

        // Lấy danh sách món ăn từ cơ sở dữ liệu và cập nhật vào adapter
        ArrayList<Meal> meals = getListMeals();
        mealAdapter.updateData(meals);

        return view; // Trả về view của fragment
    }

    // Hàm lấy danh sách tất cả các món ăn từ cơ sở dữ liệu
    private ArrayList<Meal> getListMeals() {
        return dbHelper.getAllMeals(); // Truy vấn tất cả món ăn từ cơ sở dữ liệu
    }

    // Hàm lọc các món ăn theo từ khóa tìm kiếm
    private void filterMeals(String query) {
        ArrayList<Meal> filteredList = new ArrayList<>();
        // Duyệt qua tất cả các món ăn và kiểm tra nếu tên món ăn chứa từ khóa tìm kiếm
        for (Meal meal : getListMeals()) {
            if (meal.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(meal); // Nếu có, thêm vào danh sách lọc
            }
        }
        // Cập nhật adapter với danh sách món ăn đã lọc
        mealAdapter.updateData(filteredList);
    }

    // Hàm hiển thị hộp thoại xác nhận xóa món ăn
    private void showDeleteConfirmationDialog(Meal meal) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xoá") // Tiêu đề của hộp thoại
                .setMessage("Bạn có chắc chắn muốn xoá " + meal.getName() + "?") // Thông điệp hỏi người dùng
                .setPositiveButton("Xoá", (dialog, which) -> {
                    // Nếu người dùng chọn "Xoá", xóa món ăn khỏi danh sách
                    mealAdapter.deleteMeal(meal);
                })
                .setNegativeButton("Huỷ", null) // Nếu người dùng chọn "Huỷ", không làm gì
                .show(); // Hiển thị hộp thoại
    }

    // Hàm hiển thị hộp thoại chỉnh sửa món ăn
    private void showEditMealDialog(Meal meal) {
        // Tạo một dialog để chỉnh sửa món ăn và truyền vào MealAdapter để cập nhật dữ liệu
        EditMealDialogFragment dialog = new EditMealDialogFragment(meal, mealAdapter);
        // Hiển thị dialog chỉnh sửa
        dialog.show(getParentFragmentManager(), "EditMealDialog");
    }
}
