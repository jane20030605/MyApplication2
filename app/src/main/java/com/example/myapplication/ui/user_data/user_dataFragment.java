package com.example.myapplication.ui.user_data;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentUserDataBinding;

public class user_dataFragment extends Fragment {
    private FragmentUserDataBinding binding;
    public static user_dataFragment newInstance() {
        return new user_dataFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,Bundle savedInstanceState) {

        UserDataViewModel userDataViewModel =
                new ViewModelProvider(this).get(UserDataViewModel.class);

        binding = FragmentUserDataBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final EditText editTextUsername = binding.editTextUsername;
        final EditText editTextEmail = binding.editTextEmail;
        final EditText editTextPhone = binding.editTextPhone;
        final EditText editTextAddress = binding.editTextAddress;
        final Button button_EmergencyContact = binding.buttonEmergencyContact;
        final TextView textViewTitle = binding.textViewTitle;
        final TextView textViewUsername = binding.textViewUsername;
        final TextView textViewEmail = binding.textViewEmail;
        final TextView textViewPhone = binding.textViewPhone;
        final TextView textViewAddress = binding.textViewAddress;
        final TextView textViewEmergencyContact = binding.textViewEmergencyContact;

        // 緊急連絡人按鈕的點擊事件
        button_EmergencyContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在這裡添加緊急連絡人邏輯
                // 範例中只是顯示一個Toast消息
                Toast.makeText(requireContext(), "新增緊急連絡人", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(v).navigate(R.id.nav_emergency_contact);
            }
        });

        return root;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        UserDataViewModel mViewModel = new ViewModelProvider(this).get(UserDataViewModel.class);
        // TODO: Use the ViewModel
    }

}