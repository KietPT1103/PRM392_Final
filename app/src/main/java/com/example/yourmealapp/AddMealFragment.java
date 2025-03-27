package com.example.yourmealapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.yourmealapp.models.Category;
import com.example.yourmealapp.models.Meal;

import java.util.ArrayList;

public class AddMealFragment extends Fragment {

    private DBHelper dbHelper; // Đối tượng DBHelper để tương tác với cơ sở dữ liệu
    private EditText edtMealName, edtMealIngredient; // Các trường nhập liệu: tên món ăn và nguyên liệu
    private Spinner spinnerMealCategory; // Spinner để chọn danh mục món ăn
    private Button btnSaveMeal; // Nút lưu món ăn
    private ArrayList<Category> categoryList; // Danh sách các danh mục món ăn
    private ArrayAdapter<Category> categoryAdapter; // Adapter để kết nối danh sách danh mục với Spinner

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("AddMealFragment", "onCreateView called");
        // Inflate layout cho Fragment này
        View view = inflater.inflate(R.layout.fragment_add_meal, container, false);

        // Khởi tạo DBHelper để sử dụng trong suốt Fragment
        dbHelper = new DBHelper(getContext());

        // Liên kết các thành phần giao diện với các ID trong layout
        edtMealName = view.findViewById(R.id.edtMealName);
        spinnerMealCategory = view.findViewById(R.id.spinnerMealCategory);
        edtMealIngredient = view.findViewById(R.id.edtMealIngredient);
        btnSaveMeal = view.findViewById(R.id.btnSaveMeal);

        // Tải danh mục vào Spinner
        loadCategories();

        // Sự kiện click cho nút Lưu món ăn
        btnSaveMeal.setOnClickListener(v -> saveMeal());

        return view; // Trả về view của Fragment
    }

    // Hàm tải danh mục món ăn vào Spinner
    private void loadCategories() {
        // Lấy tất cả danh mục món ăn từ cơ sở dữ liệu
        categoryList = dbHelper.getAllCategories();

        // Tạo adapter để kết nối dữ liệu danh mục với Spinner
        categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categoryList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Cập nhật cách hiển thị tên danh mục trong Spinner
                View view = super.getView(position, convertView, parent);
                ((TextView) view).setText(categoryList.get(position).getName());
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                // Cập nhật cách hiển thị tên danh mục trong dropdown của Spinner
                View view = super.getDropDownView(position, convertView, parent);
                ((TextView) view).setText(categoryList.get(position).getName());
                return view;
            }
        };

        // Thiết lập tài nguyên hiển thị cho dropdown của Spinner
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Gán adapter vào Spinner
        spinnerMealCategory.setAdapter(categoryAdapter);
    }

    // Hàm để lưu món ăn vào cơ sở dữ liệu
    private void saveMeal() {
        // Lấy tên món ăn và nguyên liệu từ các trường nhập liệu
        String mealName = edtMealName.getText().toString().trim();
        String mainIngredient = edtMealIngredient.getText().toString().trim();

        // Kiểm tra nếu thông tin chưa được nhập đầy đủ
        if (mealName.isEmpty() || mainIngredient.isEmpty()) {
            // Thông báo yêu cầu người dùng nhập đầy đủ thông tin
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return; // Dừng thực hiện nếu thiếu thông tin
        }

        // Lấy đối tượng Category từ Spinner
        Category selectedCategory = (Category) spinnerMealCategory.getSelectedItem();

        // Kiểm tra nếu danh mục chưa được chọn
        if (selectedCategory == null) {
            // Thông báo nếu danh mục không hợp lệ
            Toast.makeText(getContext(), "Danh mục không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo đối tượng Meal mới với thông tin đã nhập
        Meal meal = new Meal(mealName, selectedCategory.getId(), mainIngredient);

        // Thêm món ăn vào cơ sở dữ liệu
        boolean isInserted = dbHelper.addMeal(meal);

        // Kiểm tra kết quả việc lưu món ăn
        if (isInserted) {
            // Thông báo thành công
            Toast.makeText(getContext(), "Đã thêm: " + mealName, Toast.LENGTH_SHORT).show();
            // Quay lại màn hình trước (tức là popBackStack)
            getParentFragmentManager().popBackStack();
        } else {
            // Thông báo nếu thêm món ăn thất bại
            Toast.makeText(getContext(), "Thêm thất bại!", Toast.LENGTH_SHORT).show();
        }
    }
}
