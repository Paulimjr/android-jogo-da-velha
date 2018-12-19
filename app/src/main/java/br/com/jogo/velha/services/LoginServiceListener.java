package br.com.jogo.velha.services;

import br.com.jogo.velha.models.JsonResponse;

/**
 * Created by paulo.
 * Date: 10/12/18
 * Time: 20:36
 */
public interface LoginServiceListener {
    void showLoading();
    void hideLoading();
    void responseSuccess(JsonResponse response);
    void errorServer(String message);
}
