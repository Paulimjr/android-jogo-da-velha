package br.com.jogo.velha.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by paulo.
 * Date: 10/12/18
 * Time: 20:40
 */
public class JsonResponse implements Serializable {

    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private User data;

    public JsonResponse(int status, String message, User data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public User getData() {
        return data;
    }
}
