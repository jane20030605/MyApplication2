package com.example.myapplication.ui.emergency;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.utils.NetworkRequestManager;

import org.json.JSONObject;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private final List<JSONObject> contactList;

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        public TextView tvContactName;
        public TextView tvContactTel;
        public TextView tvRelation;
        public Button btnEditContact;
        public Button btnDeleteContact;

        public ContactViewHolder(View itemView) {
            super(itemView);
            tvContactName = itemView.findViewById(R.id.tvContactName);
            tvContactTel = itemView.findViewById(R.id.tvContactTel);
            tvRelation = itemView.findViewById(R.id.tvRelation);
            btnEditContact = itemView.findViewById(R.id.btnEditContact);
            btnDeleteContact = itemView.findViewById(R.id.btnDeleteContact);
        }
    }
    public ContactAdapter(List<JSONObject> contactList) {
        this.contactList = contactList;
    }
    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        JSONObject contact = contactList.get(position);

        // 設置聯絡人資訊
        holder.tvContactName.setText(contact.optString("contact_name")); // 聯絡人姓名
        holder.tvContactTel.setText(contact.optString("contact_tel")); // 聯絡人電話號碼
        holder.tvRelation.setText(contact.optString("relation")); // 與使用者的關係

        String contactId = contact.optString("contact_Id"); // 取得聯絡人 ID

        holder.btnEditContact.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("contactDetail", contact.toString());
            bundle.putString("contact_Id", contactId);
            Navigation.findNavController(holder.itemView).navigate(R.id.nav_emergency_contact, bundle);
        });

        holder.btnDeleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String deleteApiUrl = "http://100.96.1.3/api_delete_contact.php";
                            // 向 API 發送 POST 請求刪除聯絡人
                            NetworkRequestManager.getInstance(v.getContext()).makePostRequest(
                                    deleteApiUrl, "contact_Id=" + contactId, new NetworkRequestManager.RequestListener() {
                                        @Override
                                        public void onSuccess(String response) {
                                            deleteContact(position); // 從清單中移除聯絡人
                                        }

                                        @Override
                                        public void onError(String error) {

                                        }
                                    });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    // 新增聯絡人到清單
    public void addContact(JSONObject event) {
        contactList.add(event); // 將聯絡人加入清單
        notifyItemInserted(contactList.size() - 1); // 通知適配器有新項目插入
    }

    private void deleteContact(int position) {
        contactList.remove(position);
        notifyItemRemoved(position);
    }
}
