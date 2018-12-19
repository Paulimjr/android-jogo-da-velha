package br.com.jogo.velha.utils;

import android.app.Activity;

import br.com.jogo.velha.models.Tabu;

/**
 * Created by paulo.
 * Date: 11/12/18
 * Time: 19:31
 */
public class TabuValidate {

    private Activity activity;
    private int GANHOU = 0;

    public TabuValidate(Activity activity) {
        this.activity = activity;
    }

    public boolean validateJogada(int pos, String[] values) {
        boolean valid = true;

        if (values != null) {
            for (int i=0; i < values.length; i++) {
                if ((i == pos) && (!values[i].isEmpty())) {
                    valid = false;
                }
            }
        }

        return valid;
    }

    public String[] efetuarJogada(int pos, String value, String[] values) {
        values[pos] = value;
        return values;
    }

    public Tabu initial() {
        Tabu tabu = new Tabu();
        String[] pos = new String[9];

        pos[0] = "";
        pos[1] = "";
        pos[2] = "";
        pos[3] = "";
        pos[4] = "";
        pos[5] = "";
        pos[6] = "";
        pos[7] = "";
        pos[8] = "";

        tabu.setTabu(pos);
        return tabu;
    }

    public int hasWinner(String[] tabu, String simbolo){
        if (tabu != null) {
            if (tabu[0].equals(simbolo) && tabu[1].equals(simbolo) && tabu[2].equals(simbolo)) {
                GANHOU = 1;
            }
            //verificando 2 linha
            if (tabu[3].equals(simbolo) && tabu[4].equals(simbolo) && tabu[5].equals(simbolo)) {
                GANHOU = 1;
            }
            //verificando 3 linha
            if (tabu[6].equals(simbolo) && tabu[7].equals(simbolo) && tabu[8].equals(simbolo)) {
                GANHOU = 1;
            }
            //////////////////////////
            //verificando na horizontal
            //1 coluna
            if (tabu[0].equals(simbolo) && tabu[3].equals(simbolo) && tabu[6].equals(simbolo)) {
                GANHOU = 1;
            }
            //2 coluna
            if (tabu[1].equals(simbolo) && tabu[4].equals(simbolo) && tabu[7].equals(simbolo)) {
                GANHOU = 1;
            }
            //3 coluna
            if (tabu[2].equals(simbolo) && tabu[5].equals(simbolo) && tabu[8].equals(simbolo)) {
                GANHOU = 1;
            }

            //////////////////////////
            // verificando na diagonal
            if (tabu[0].equals(simbolo) && tabu[4].equals(simbolo) && tabu[8].equals(simbolo)) {
                GANHOU = 1;
            }
            //3 coluna
            if (tabu[2].equals(simbolo) && tabu[4].equals(simbolo) && tabu[6].equals(simbolo)) {
                GANHOU = 1;
            }
        }

        return GANHOU;
    }

    public int verificarNenhumVencedor(String[] tabu) {
        if (!tabu[0].equals("")
                && !tabu[1].equals("")
                && !tabu[2].equals("")
                && !tabu[3].equals("")
                && !tabu[4].equals("")
                && !tabu[5].equals("")
                && !tabu[6].equals("")
                && !tabu[7].equals("")
                && !tabu[8].equals("")) {

            GANHOU = 3;
        }
        return GANHOU;
    }

    public void clearWinners() {
        GANHOU = 0;
    }
}
