package com.example.myapplication.ui.registration;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentRegistrationBinding;
import com.example.myapplication.models.User;
import com.example.myapplication.utils.UserManager;

public class RegistrationFragment extends Fragment {

    private FragmentRegistrationBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RegistrationViewModel registrationViewModel =
                new ViewModelProvider(this).get(RegistrationViewModel.class);

        binding = FragmentRegistrationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final EditText username = binding.username;
        final EditText password = binding.password;
        final EditText real_name = binding.realName;
        final EditText phone = binding.phone;
        final EditText email = binding.email;
        final EditText home = binding.home;
        final Button registerButton = binding.registerButton;

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredUsername = username.getText().toString().trim();
                String enteredPassword = password.getText().toString().trim();
                String enteredRealName = real_name.getText().toString().trim();
                String enteredPhone = phone.getText().toString().trim();
                String enteredEmail = email.getText().toString().trim();
                String enteredHome = home.getText().toString().trim();

                if (TextUtils.isEmpty(enteredUsername) || TextUtils.isEmpty(enteredPassword) ||
                        TextUtils.isEmpty(enteredRealName) || TextUtils.isEmpty(enteredPhone) ||
                        TextUtils.isEmpty(enteredEmail) || TextUtils.isEmpty(enteredHome)) {

                    Toast.makeText(requireContext(), "有資料未填寫，無法註冊", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (UserManager.getInstance().getUser(enteredUsername) != null) {
                    Toast.makeText(requireContext(), "用戶名已存在，請輸入其他用戶名", Toast.LENGTH_SHORT).show();
                    return;
                }

                User newUser = new User(enteredUsername, enteredPassword);
                UserManager.getInstance().addUser(newUser);

                Toast.makeText(requireContext(), "註冊成功，請重新登入", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(v).navigate(R.id.nav_login);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
