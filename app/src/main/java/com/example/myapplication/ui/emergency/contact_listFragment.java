package com.example.myapplication.ui.emergency;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class contact_listFragment extends Fragment {

    private ContactListViewModel mViewModel;
    private RecyclerView mRecyclerView;
    private ContactAdapter mContactAdapter;

    public static contact_listFragment newInstance() {
        return new contact_listFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // 載入布局
        View rootView = inflater.inflate(R.layout.fragment_contact_list, container, false);

        // 初始化 RecyclerView
        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 初始化 ViewModel
        mViewModel = new ViewModelProvider(requireActivity()).get(ContactListViewModel.class);

        // 獲取緊急聯絡人列表
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String accountId = "Account"; // 使用者帳號ID，根據實際情況設置
                    String response = ContactGetClient.getContact(accountId);
                    JSONArray contactArray = new JSONArray(response);

                    List<JSONObject> contactList = new ArrayList<>();
                    for (int i = 0; i < contactArray.length(); i++) {
                        contactList.add(contactArray.getJSONObject(i));
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mContactAdapter = new ContactAdapter(getContext(), contactList);
                            mRecyclerView.setAdapter(mContactAdapter);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "獲取聯絡人時出錯: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

}

