package br.com.jogo.velha.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by paulo.
 * Date: 11/12/18
 * Time: 19:33
 */
public class Tabu implements Serializable {

    @SerializedName("tabu")
    private String[] tabu = new String[9];

    public String[] getTabu() {
        return tabu;
    }

    public Tabu setTabu(String[] tabu) {
        this.tabu = tabu;
        return this;
    }
}
