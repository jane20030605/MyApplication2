package com.example.myapplication.ui.emergency;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.utils.NetworkRequestManager;

import org.json.JSONObject;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private List<JSONObject> contactList;
    private Context context;

    public ContactAdapter(Context context, List<JSONObject> contactList) {
        this.context = context;
        this.contactList = contactList;
    }
    // 添加一个方法用于更新联系人列表
    @SuppressLint("NotifyDataSetChanged")
    public void updateContactList(List<JSONObject> newContactList) {
        contactList.clear();
        contactList.addAll(newContactList);
        notifyDataSetChanged();
    }
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

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, @SuppressLint("RecyclerView") int position) {
        JSONObject contact = contactList.get(position);

        // 設置聯絡人資訊
        holder.tvContactName.setText(contact.optString("contact_name")); // 聯絡人姓名
        holder.tvContactTel.setText(contact.optString("contact_tel")); // 聯絡人電話號碼
        holder.tvRelation.setText(contact.optString("relation")); // 與使用者的關係

        String contactId = contact.optString("contact_Id"); // 取得聯絡人 ID

        holder.btnEditContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("contactDetail", contact.toString());
                bundle.putString("contactId", contactId);
                Navigation.findNavController(holder.itemView).navigate(R.id.nav_emergency_contact, bundle);
            }
        });

        holder.btnDeleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String result = ContactDeleteClient.deleteContact(contactId);
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
                                    if (result.equals("Delete operation successful.")) {
                                        removeContact(position);
                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "刪除聯絡人時出錯: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
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

    private void deleteContact(String contactId, int position) {
        String deleteApiUrl = "http://100.96.1.3/api_delete_contact.php";
        try {
            NetworkRequestManager.getInstance(context).makePostRequest(deleteApiUrl, "contact_Id=" + contactId, new NetworkRequestManager.RequestListener() {
                @Override
                public void onSuccess(String response) {
                    removeContact(position);
                    Toast.makeText(context, "聯絡人已成功刪除", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(context, "刪除聯絡人時出錯: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "刪除聯絡人時出錯: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void removeContact(int position) {
        contactList.remove(position);
        notifyItemRemoved(position);
    }

}
