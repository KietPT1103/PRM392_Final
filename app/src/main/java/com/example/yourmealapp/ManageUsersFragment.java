package com.example.yourmealapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yourmealapp.models.User;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ManageUsersFragment} factory method to
 * create an instance of this fragment.
 */
public class ManageUsersFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;

    public ManageUsersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manage_users, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Lấy danh sách người dùng từ DB
        DBHelper dbHelper = new DBHelper(getContext());
        userList = dbHelper.getAllUsers();

        // Khởi tạo adapter và gán cho RecyclerView
        userAdapter = new UserAdapter(userList);
        recyclerView.setAdapter(userAdapter);

        return view;
    }
}
