package com.example.myapplication.ui.medicine_allbox;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class medicine_allboxFragment extends Fragment {

    private MedicineAllboxViewModel mViewModel;
    private RecyclerView recyclerView;
    private MedicineAllboxAdapter adapter;
    private List<MedicineAllbox> medicineList;

    public static medicine_allboxFragment newInstance() {
        return new medicine_allboxFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medicine_allbox, container, false);

        // 初始化 RecyclerView
        recyclerView = view.findViewById(R.id.medicine_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 創建假數據
        medicineList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            MedicineAllbox medicine = new MedicineAllbox("Medicine " + i);
            medicineList.add(medicine);
        }

        // 初始化並設置 Adapter
        adapter = new MedicineAllboxAdapter(medicineList);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MedicineAllboxViewModel.class);
        // TODO: Use the ViewModel
    }

    class MedicineAllbox {
        public MedicineAllbox(String s) {
            // 可以在此處初始化藥品信息
        }
    }
}
