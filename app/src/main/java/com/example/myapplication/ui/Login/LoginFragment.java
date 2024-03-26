// LoginFragment.java
package com.example.myapplication.ui.Login;

import android.os.Bundle;
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
import com.example.myapplication.databinding.FragmentLoginBinding;
import com.example.myapplication.models.User;
import com.example.myapplication.utils.SessionManager;
import com.example.myapplication.utils.UserManager;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private SessionManager sessionManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LoginViewModel loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        sessionManager = new SessionManager(requireContext());

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final EditText editText = binding.editText;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.button11;
        final Button registerButton = binding.button12;

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(requireContext(), "請輸入使用者名稱和密碼", Toast.LENGTH_SHORT).show();
                } else {
                    User user = UserManager.getInstance().getUser(username);
                    if (user != null && user.getPassword().equals(password)) {
                        Toast.makeText(requireContext(), "登入成功", Toast.LENGTH_SHORT).show();
                        sessionManager.login();
                        Navigation.findNavController(v).navigate(R.id.nav_home);
                    } else {
                        Toast.makeText(requireContext(), "使用者名稱或密碼錯誤", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireContext(), "進行註冊", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(v).navigate(R.id.nav_registration);
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