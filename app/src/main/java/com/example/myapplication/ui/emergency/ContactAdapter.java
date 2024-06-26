package com.example.myapplication.ui.emergency;

// 導入必要的包
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
    private final List<JSONObject> contactList;

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        public TextView tvContactName;
        public TextView tvContactTel;
        public TextView tvRelation;
        public TextView tvContactMail; // 新增欄位
        public Button btnEditContact;
        public Button btnDeleteContact;

        public ContactViewHolder(View itemView) {
            super(itemView);
            tvContactName = itemView.findViewById(R.id.tvContactName);
            tvContactTel = itemView.findViewById(R.id.tvContactTel);
            tvRelation = itemView.findViewById(R.id.tvRelation);
            tvContactMail = itemView.findViewById(R.id.tvContactMail); // 綁定新的 TextView
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

        // 設置聯絡人信息
        holder.tvContactName.setText(contact.optString("contact_name"));
        holder.tvContactTel.setText(contact.optString("contact_tel"));
        holder.tvRelation.setText(mapEnglishToChinese(contact.optString("relation")));
        holder.tvContactMail.setText(contact.optString("contact_mail")); // 設置聯絡人郵箱

        String contact_Id = contact.optString("contact_Id");

        // 根據當前日夜間模式設置文本顏色
        int textColor = getTextColor(holder.itemView.getContext());
        holder.tvContactName.setTextColor(textColor);
        holder.tvContactTel.setTextColor(textColor);
        holder.tvRelation.setTextColor(textColor);
        holder.tvContactMail.setTextColor(textColor); // 設置文本顏色

        holder.btnEditContact.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("contact_Id", contact_Id);
            bundle.putString("account", contact.optString("account"));
            bundle.putString("relation", contact.optString("relation"));
            bundle.putString("contact_tel", contact.optString("contact_tel"));
            bundle.putString("contact_name", contact.optString("contact_name"));
            bundle.putString("contact_mail", contact.optString("contact_mail")); // 傳遞聯絡人郵箱
            Navigation.findNavController(holder.itemView).navigate(R.id.nav_contact_update, bundle);
        });

        holder.btnDeleteContact.setOnClickListener(v -> {
            int positionToDelete = holder.getAdapterPosition();
            deleteContact(positionToDelete, contact_Id, v);
        });
    }


    private int getTextColor(Context context) {
        int currentNightMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            return context.getResources().getColor(R.color.white); // 夜间模式下的文本颜色
        } else {
            return context.getResources().getColor(R.color.textColorPrimary); // 日间模式下的文本颜色
        }
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    // 添加联系人到列表
    public void addContact(JSONObject event) {
        contactList.add(event); // 将联系人加入列表
        notifyItemInserted(contactList.size() - 1); // 通知适配器有新项目插入
    }

    // 删除联系人
    private void deleteContact(int position, String contact_Id, View view) {
        new Thread(() -> {
            try {
                String deleteApiUrl = "http://100.96.1.3/api_delete_contact.php";
                // 向 API 发送 POST 请求删除联系人
                JSONObject postData = new JSONObject();
                postData.put("contact_Id", contact_Id);

                NetworkRequestManager.getInstance(view.getContext()).makePostRequest(
                        deleteApiUrl, postData.toString(), new NetworkRequestManager.RequestListener() {
                            @Override
                            public void onSuccess(String response) {
                                // 确保在主线程上更新 UI
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    contactList.remove(position);
                                    notifyItemRemoved(position);
                                    Toast.makeText(view.getContext(), "聯絡人已删除", Toast.LENGTH_SHORT).show();
                                });
                            }

                            @Override
                            public void onError(String error) {
                                // 确保在主线程上显示错误信息
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    Toast.makeText(view.getContext(), "删除失敗：" + error, Toast.LENGTH_LONG).show();
                                });
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
                // 确保在主线程上显示错误信息
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(view.getContext(), "無法删除聯絡人：" + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    // 将英文关系映射为中文显示
    private String mapEnglishToChinese(String english) {
        switch (english) {
            case "":
                return "請選擇";
            case "children":
                return "兒女";
            case "relatives":
                return "親戚";
            case "spouse":
                return "配偶";
            case "friend":
                return "朋友";
            case "other":
                return "其他";
            default:
                return ""; // 或者处理未知情况的默认值
        }
    }
}
