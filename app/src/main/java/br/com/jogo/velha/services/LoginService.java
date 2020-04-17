package br.com.jogo.velha.services;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import br.com.jogo.velha.VelhaApplication;
import br.com.jogo.velha.models.JsonResponse;
import br.com.jogo.velha.models.RequestCreate;
import br.com.jogo.velha.models.RequestLogin;
import br.com.jogo.velha.services.api.APIClient;
import br.com.jogo.velha.services.api.UserAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by paulo.
 * Date: 10/12/18
 * Time: 20:36
 */
public class LoginService implements ILoginService {

    private Activity activity;
    private LoginServiceListener listener;
    private APIClient apiClient;

    public LoginService(LoginServiceListener listener) {
        this.listener = listener;
        this.apiClient = VelhaApplication.getInstance().getApiClient();
    }

    @Override
    public void requestLogin(RequestLogin requestLogin) {

    }

    @Override
    public void requestCreate(RequestCreate requestCreate) {
        listener.showLoading();

        UserAPI userAPI = this.apiClient.getRetrofit().create(UserAPI.class);
        Call<JsonResponse> loginResponse = userAPI.create(requestCreate);

        loginResponse.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                listener.hideLoading();

                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    listener.responseSuccess(response.body());
                } else if (response.body() != null && response.body().getData() == null) {
                    listener.errorServer(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                listener.hideLoading();
                listener.errorServer(t.getMessage());
            }
        });
    }

    @Override
    public void findByEmail(final String email) {
        listener.showLoading();

        UserAPI userAPI = this.apiClient.getRetrofit().create(UserAPI.class);
        Call<JsonResponse> loginResponse = userAPI.findByEmail(email);

        loginResponse.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                listener.hideLoading();

                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    listener.responseSuccess(response.body());
                } else if (response.body() != null && response.body().getData() == null) {
                    listener.errorServer(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                listener.hideLoading();
                listener.errorServer(t.getMessage());
            }
        });
    }

    @Override
    public void updateUuid(String email, String uuid) {
        listener.showLoading();

        UserAPI userAPI = this.apiClient.getRetrofit().create(UserAPI.class);
        Call<Void> loginResponse = userAPI.updateUuid(email, uuid);

        loginResponse.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                listener.hideLoading();
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                listener.hideLoading();
                listener.errorServer(t.getMessage());
                Log.v("Error: ", t.getMessage());
            }
        });
    }
}
