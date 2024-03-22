package com.example.myapplication.ui.medicine_allbox;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class MedicineAllboxAdapter extends RecyclerView.Adapter<MedicineAllboxAdapter.ViewHolder> {

    private List<medicine_allboxFragment.MedicineAllbox> medicineList;

    public MedicineAllboxAdapter(List<medicine_allboxFragment.MedicineAllbox> medicineList) {
        this.medicineList = medicineList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicine, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 在此處綁定藥品信息到 ViewHolder 中的視圖元件上
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // 宣告視圖元件

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // 初始化視圖元件
        }
    }
}
