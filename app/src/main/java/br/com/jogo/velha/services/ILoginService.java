package br.com.jogo.velha.services;

import br.com.jogo.velha.models.RequestCreate;
import br.com.jogo.velha.models.RequestLogin;

/**
 * Created by paulo.
 * Date: 10/12/18
 * Time: 20:38
 */
public interface ILoginService {
    void requestLogin(RequestLogin requestLogin);
    void requestCreate(RequestCreate requestCreate);
    void findByEmail(final String email);
    void updateUuid(final String email, final String uuid);
}
