package br.com.jogo.velha.models;

import java.util.List;

/**
 * Created by paulo.
 * Date: 11/12/18
 * Time: 10:36
 */
public class RequestGame {

    private String key;
    private String playerOne;
    private String playerTwo;
    private String playerOneName;
    private String playerTwoName;
    private String actualPlayer;
    private Integer empates;
    private Integer vitorias;
    private Integer derrotas;
    private String progress;
    private String simbolo;
    private List<String> tabu;
    private Integer vitX;
    private Integer vitO;

    public RequestGame(String playerOneName, String playerTwoName, String playerOne, String playerTwo, String actualPlayer,
                       Integer empates, Integer vitorias, Integer derrotas, String progress, String simbolo,
                       List<String> tabu, Integer vitX, Integer vitO) {
        this.playerOneName = playerOneName;
        this.playerTwoName = playerTwoName;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.actualPlayer = actualPlayer;
        this.empates = empates;
        this.vitorias = vitorias;
        this.derrotas = derrotas;
        this.progress = progress;
        this.simbolo = simbolo;
        this.tabu = tabu;
        this.vitO = vitO;
        this.vitX = vitX;
    }

    public RequestGame() {

    }

    public String getKey() {
        return key;
    }

    public RequestGame setKey(String key) {
        this.key = key;
        return this;
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

    public Integer getEmpates() {
        return empates;
    }

    public RequestGame setEmpates(Integer empates) {
        this.empates = empates;
        return this;
    }

    public Integer getVitorias() {
        return vitorias;
    }

    public RequestGame setVitorias(Integer vitorias) {
        this.vitorias = vitorias;
        return this;
    }

    public Integer getDerrotas() {
        return derrotas;
    }

    public RequestGame setDerrotas(Integer derrotas) {
        this.derrotas = derrotas;
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

    public List<String> getTabu() {
        return tabu;
    }

    public RequestGame setTabu(List<String> tabu) {
        this.tabu = tabu;
        return this;
    }

    public Integer getVitX() {
        return vitX;
    }

    public RequestGame setVitX(Integer vitX) {
        this.vitX = vitX;
        return this;
    }

    public Integer getVitO() {
        return vitO;
    }

    public RequestGame setVitO(Integer vitO) {
        this.vitO = vitO;
        return this;
    }

    public String getPlayerOneName() {
        return playerOneName;
    }

    public String getPlayerTwoName() {
        return playerTwoName;
    }
}
