package br.com.jogo.velha.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by paulo.
 * Date: 10/12/18
 * Time: 20:39
 */
public class RequestLogin implements Serializable {

    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String password;

    public RequestLogin(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public RequestLogin setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public RequestLogin setPassword(String password) {
        this.password = password;
        return this;
    }
}
