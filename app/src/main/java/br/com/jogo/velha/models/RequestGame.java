package br.com.jogo.velha.models;

/**
 * Created by paulo.
 * Date: 11/12/18
 * Time: 10:36
 */
public class RequestGame {

    private String key;
    private String playerOne;
    private String playerTwo;
    private String actualPlayer;
    private String empates;
    private String vitorias;
    private String derrotas;
    private String progress;
    private String simbolo;

    public RequestGame(String playerOne, String playerTwo, String actualPlayer,
                       String empates, String vitorias, String derrotas, String progress, String simbolo) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.actualPlayer = actualPlayer;
        this.empates = empates;
        this.vitorias = vitorias;
        this.derrotas = derrotas;
        this.progress = progress;
        this.simbolo = simbolo;
    }

    public RequestGame() {
    }

    public String getPlayerOne() {
        return playerOne;
    }

    public RequestGame setPlayerOne(String playerOne) {
        this.playerOne = playerOne;
        return this;
    }

    public String getPlayerTwo() {
        return playerTwo;
    }

    public RequestGame setPlayerTwo(String playerTwo) {
        this.playerTwo = playerTwo;
        return this;
    }

    public String getActualPlayer() {
        return actualPlayer;
    }

    public RequestGame setActualPlayer(String actualPlayer) {
        this.actualPlayer = actualPlayer;
        return this;
    }

    public String getEmpates() {
        return empates;
    }

    public RequestGame setEmpates(String empates) {
        this.empates = empates;
        return this;
    }

    public String getVitorias() {
        return vitorias;
    }

    public RequestGame setVitorias(String vitorias) {
        this.vitorias = vitorias;
        return this;
    }

    public String getDerrotas() {
        return derrotas;
    }

    public RequestGame setDerrotas(String derrotas) {
        this.derrotas = derrotas;
        return this;
    }

    public String getKey() {
        return key;
    }

    public RequestGame setKey(String key) {
        this.key = key;
        return this;
    }

    public String getProgress() {
        return progress;
    }

    public RequestGame setProgress(String progress) {
        this.progress = progress;
        return this;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public RequestGame setSimbolo(String simbolo) {
        this.simbolo = simbolo;
        return this;
    }
}
