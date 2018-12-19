package br.com.jogo.velha;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import javax.inject.Singleton;

import br.com.jogo.velha.services.api.APIClient;

/**
 * Created by paulo.
 * Date: 11/08/18
 * Time: 15:26
 */
@Singleton
public class VelhaApplication extends Application {

    private static VelhaApplication velhaApplication;
    private APIClient apiClient;

    public synchronized static VelhaApplication getInstance() {
        return velhaApplication;
    }

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        velhaApplication = this;
        apiClient = new APIClient(this, BuildConfig.BASE_URL);
    }

    public APIClient getApiClient() {
        return apiClient;
    }
}
