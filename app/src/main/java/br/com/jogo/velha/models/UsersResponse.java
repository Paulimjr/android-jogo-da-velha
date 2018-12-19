package br.com.jogo.velha.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by paulo.
 * Date: 10/12/18
 * Time: 21:48
 */
public class UsersResponse implements Serializable {

    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<User> data;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<User> getData() {
        return data;
    }
}
