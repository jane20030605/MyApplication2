package com.example.myapplication.ui.registration;

import com.example.myapplication.ui.registration.RegistrationApiService;
import com.example.myapplication.ui.registration.RegistrationRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistrationApiClient {
    private static final String BASE_URL = "http://100.96.1.3/";

    private RegistrationApiService apiService;

    public RegistrationApiClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(RegistrationApiService.class);
    }

    public void registerUser(RegistrationRequest request, final RegistrationCallback callback) {
        Call<Void> call = apiService.registerUser(request);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess("註冊成功");
                } else {
                    callback.onError("註冊失敗，請稍後再試");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("註冊失敗，請稍後再試");
            }
        });
    }

    public interface RegistrationCallback {
        void onSuccess(String message);
        void onError(String message);
    }
}
