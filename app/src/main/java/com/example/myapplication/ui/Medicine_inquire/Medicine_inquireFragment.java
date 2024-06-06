package com.example.myapplication.ui.Medicine_inquire;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class Medicine_inquireFragment extends Fragment {
    // 添加暫存區屬性
    private String selectedTime;
    private String dosage;
    private RecyclerView recyclerView;
    private MedicineBoxAdapter adapter;
    private List<Medicine> medicineList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medicine_inquire, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.medicine_list);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Create dummy data
        medicineList = new ArrayList<>();
        int[] imageResIds = {
                R.drawable.medicine_1, R.drawable.medicine_2, R.drawable.medicine_3, R.drawable.medicine_4,
                R.drawable.medicine_5, R.drawable.medicine_6, R.drawable.medicine_7, R.drawable.medicine_8
        };
        String[] descriptions = {
                "medicine_1","medicine_2","medicine_3","medicine_4",
                "medicine_5","medicine_6","medicine_7","medicine_8"
        };
        for (int i = 0; i < 100; i++) {
            Medicine medicine = new Medicine("Medicine " + i, imageResIds[i % imageResIds.length], descriptions[i % descriptions.length]);
            medicineList.add(medicine);
        }

        // Initialize and set Adapter
        adapter = new MedicineBoxAdapter(medicineList);
        recyclerView.setAdapter(adapter);

        return view;
    }

    static class Medicine {
        private String name;
        private int imageResId;
        private String description;

        public Medicine(String name, int imageResId, String description) {
            this.name = name;
            this.imageResId = imageResId;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public int getImageResId() {
            return imageResId;
        }

        public String getDescription() {
            return description;
        }
    }

    class MedicineBoxAdapter extends RecyclerView.Adapter<MedicineBoxAdapter.ViewHolder> {
        private List<Medicine> medicineList;

        public MedicineBoxAdapter(List<Medicine> medicineList) {
            this.medicineList = medicineList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_medicine_box, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Medicine medicine = medicineList.get(position);
            holder.bind(medicine);
        }

        @Override
        public int getItemCount() {
            return medicineList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private ImageView imageView;
            private TextView textView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.medicine_image);
                textView = itemView.findViewById(R.id.medicine_name);
                itemView.setOnClickListener(this);
            }

            public void bind(Medicine medicine) {
                imageView.setImageResource(medicine.getImageResId());
                textView.setText(medicine.getDescription());
            }

            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                Medicine medicine = medicineList.get(position);
                showMedicineDetailDialog(medicine);
            }
        }
    }

    private void showMedicineDetailDialog(Medicine medicine) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(medicine.getName());
        builder.setMessage("許可證字號:\n衛署藥製字第001400號\n" +
                "藥物名稱:\n大豐普樂非林錠\n" +
                "適應症:\n氣喘及支氣管痙攣\n" );
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.medicine_detail_dialog, null);
        builder.setView(dialogView);

        // Get references to views in the dialog layout
        Spinner timeSpinner = dialogView.findViewById(R.id.timeSpinner);
        EditText dosageEditText = dialogView.findViewById(R.id.dosageEditText);

        // Set up spinner with options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.time_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(adapter);

        builder.setPositiveButton("加入個人藥物庫", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 在點擊確定按鈕時將數據存入暫存區
                selectedTime = timeSpinner.getSelectedItem().toString();
                dosage = dosageEditText.getText().toString();
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }


}
