package com.example.myapplication.ui.medicine_allbox;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
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
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // 創建假數據
        medicineList = new ArrayList<>();
        int[] imageResIds = {
                R.drawable.medicine_1, R.drawable.medicine_2, R.drawable.medicine_3, R.drawable.medicine_4,
                R.drawable.medicine_5, R.drawable.medicine_6, R.drawable.medicine_7, R.drawable.medicine_8
        };
        for (int i = 0; i < 100; i++) {
            MedicineAllbox medicine = new MedicineAllbox("Medicine " + i, imageResIds[i % imageResIds.length]);
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

    static class MedicineAllbox {
        private String name;
        private int imageResId;

        public MedicineAllbox(String name, int imageResId) {
            this.name = name;
            this.imageResId = imageResId;
        }

        public String getName() {
            return name;
        }

        public int getImageResId() {
            return imageResId;
        }
    }

    class MedicineAllboxAdapter extends RecyclerView.Adapter<MedicineAllboxAdapter.ViewHolder> {
        private List<MedicineAllbox> medicineList;

        public MedicineAllboxAdapter(List<MedicineAllbox> medicineList) {
            this.medicineList = medicineList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_medicine, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            MedicineAllbox medicine = medicineList.get(position);
            holder.bind(medicine);
        }

        @Override
        public int getItemCount() {
            return medicineList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView nameTextView;
            private ImageView imageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                nameTextView = itemView.findViewById(R.id.medicine_name);
                imageView = itemView.findViewById(R.id.medicine_image);
                itemView.setOnClickListener(this);
            }

            public void bind(MedicineAllbox medicine) {
                nameTextView.setText(medicine.getName());
                imageView.setImageResource(medicine.getImageResId());
            }

            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                MedicineAllbox medicine = medicineList.get(position);
                showMedicineDetailDialog(medicine);
            }
        }
    }

    private void showMedicineDetailDialog(MedicineAllbox medicine) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(medicine.getName());
        builder.setMessage("許可證字號:\n衛署藥製字第001400號\n" +
                "藥物名稱:\n大豐普樂非林錠\n" +
                "製造廠商:\n大豐製藥股份有限公司\n" +
                "適應症:\n氣喘及支氣管痙攣\n" +
                "形狀:圓形\n" + "顏色:黃\n" +"標記:TF TA");
        builder.setPositiveButton("確定", null);
        builder.show();
    }
}
