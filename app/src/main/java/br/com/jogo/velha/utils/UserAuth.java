package br.com.jogo.velha.utils;

import android.content.Context;
import android.content.SharedPreferences;

import br.com.jogo.velha.models.User;

/**
 * Created by paulo.
 * Date: 30/11/18
 * Time: 16:14
 */
public class UserAuth {

    private SharedPreferences preferences;
    private String userLogged;
    private static final String UserAuth = "UserAuth";

    public UserAuth(Context context) {
        this.preferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        loadValues();
    }

    public void save(String value){
        if ((value != null) && (!value.isEmpty())) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(UserAuth, value);
            editor.apply();
        }

        loadValues();
    }

    private String loadValue() {
        if (preferences.contains(UserAuth)) {
            return preferences.getString(UserAuth, "");
        }
        return "";
    }

    public void logout() {
        preferences.edit().remove(UserAuth).apply();
        preferences.edit().clear().apply();
    }

    private void loadValues() {
        userLogged = loadValue();
    }

    public String getUserLogged() {
        return userLogged;
    }

    public User getUser() {
        return Utils.jsonToUser(getUserLogged());
    }
}
