package com.example.myapplication.ui.forget_password;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class forget_passwordFragment extends Fragment {
    private EditText emailEditText;
    private static final String RESET_PASSWORD_URL = "http://100.96.1.3/api_forget_password.php";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forget_password, container, false);

        emailEditText = view.findViewById(R.id.edit_email);
        Button submitButton = view.findViewById(R.id.button_submit);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                if (email.isEmpty()) {
                    Toast.makeText(getContext(), "請輸入郵箱", Toast.LENGTH_SHORT).show();
                } else {
                    sendResetPasswordRequest(email);
                }
            }
        });

        return view;
    }

    private void sendResetPasswordRequest(String email) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(RESET_PASSWORD_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json; utf-8");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setDoOutput(true);

                    JSONObject jsonInput = new JSONObject();
                    jsonInput.put("email", email);

                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = jsonInput.toString().getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "重置密碼請求成功，請檢查郵箱", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(requireView()).navigate(R.id.nav_login);
                        });
                    } else {
                        Log.e("ForgetPasswordFragment", "重置密碼請求失敗，響應碼: " + responseCode);
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "重置密碼請求失敗，請稍後重試", Toast.LENGTH_SHORT).show();
                        });
                    }
                    connection.disconnect();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "重置密碼請求失敗，請稍後重試", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }).start();
    }
}
