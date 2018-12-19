package br.com.jogo.velha.models;

import java.io.Serializable;

/**
 * Created by paulo.
 * Date: 11/12/18
 * Time: 20:06
 */
public class Value implements Serializable {

    private String pos;
    private String value;

    public String getPos() {
        return pos;
    }

    public Value setPos(String pos) {
        this.pos = pos;
        return this;
    }

    public String getValue() {
        return value;
    }

    public Value setValue(String value) {
        this.value = value;
        return this;
    }
}
