package br.com.jogo.velha.utils;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;

import br.com.jogo.velha.models.Tabu;
import br.com.jogo.velha.models.User;

/**
 * Created by paulo.
 * Date: 30/11/18
 * Time: 14:26
 */
public class Utils {

    public static void showMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static String userToJson(User user) {
        if (user != null) {
            return new Gson().toJson(user);
        } else{
            return "";
        }
    }

    public static User jsonToUser(String user) {
        if (user != null) {
            return new Gson().fromJson(user, User.class);
        } else{
            return null;
        }
    }

    public static Tabu jsonToTabu(String tabu) {
        if (tabu != null) {
            return new Gson().fromJson(tabu, Tabu.class);
        } else{
            return null;
        }
    }

    public static String tabuToJson(Tabu tabu) {
        if (tabu != null) {
            return new Gson().toJson(tabu);
        } else{
            return "";
        }
    }
}
