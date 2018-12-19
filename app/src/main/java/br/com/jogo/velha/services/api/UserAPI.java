package br.com.jogo.velha.services.api;

import br.com.jogo.velha.models.JsonResponse;
import br.com.jogo.velha.models.RequestCreate;
import br.com.jogo.velha.models.RequestLogin;
import br.com.jogo.velha.models.UsersResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by paulo.
 * Date: 10/12/18
 * Time: 21:00
 */
public interface UserAPI {

    /**
     * Request for login in Application
     *
     * @param requestLogin
     * @return
     */
    @POST("login")
    Call<JsonResponse> login(@Body final RequestLogin requestLogin);

    /**
     * Request for login with Facebook and create an new user in Application
     *
     * @param requestCreate
     * @return
     */
    @POST("create")
    Call<JsonResponse> create(@Body final RequestCreate requestCreate);

    /**
     * Request for login with Facebook and create an new user in Application
     *
     * @return
     */
    @GET("users")
    Call<UsersResponse> users();

    /**
     * Request for login with Facebook and create an new user in Application
     *
     * @return
     */
    @GET("findByEmail")
    Call<JsonResponse> findByEmail(@Query("email") final String email);

    /**
     * Request for login with Facebook and create an new user in Application
     *
     * @return
     */
    @GET("updateUuid")
    Call<Void> updateUuid(@Query("email") final String email, @Query("uuid") final String uuid);
}
