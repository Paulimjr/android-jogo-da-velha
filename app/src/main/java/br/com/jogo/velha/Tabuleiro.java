package br.com.jogo.velha;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.jogo.velha.models.RequestGame;
import br.com.jogo.velha.models.Tabu;
import br.com.jogo.velha.utils.Constants;
import br.com.jogo.velha.utils.TabuValidate;
import br.com.jogo.velha.utils.UserAuth;
import br.com.jogo.velha.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Tabuleiro extends AppCompatActivity {

    private TabuValidate tabuValidate;
    private Tabu tabu;
    private String actualPlayers;
    private int empates = 0;

    @BindView(R.id.tab_one) TextView mValue0;
    @BindView(R.id.tab_two) TextView mValue1;
    @BindView(R.id.tab_three) TextView mValue2;
    @BindView(R.id.tab_four) TextView mValue3;
    @BindView(R.id.tab_five) TextView mValue4;
    @BindView(R.id.tab_six) TextView mValue5;
    @BindView(R.id.tab_seven) TextView mValue6;
    @BindView(R.id.tab_eight) TextView mValue7;
    @BindView(R.id.tab_nine) TextView mValue8;
    @BindView(R.id.btn_left_game) Button btnLeftGame;

    //PLAYERS
    @BindView(R.id.name_player_one) TextView mPlayerOne;
    @BindView(R.id.name_player_two) TextView mPlayerTwo;
    //Empate
    @BindView(R.id.text_name_empate_value) TextView mEmpates;


    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private UserAuth userAuth;

    private ProgressDialog dialog;
    private FirebaseDatabase firebaseDatabase;
    private final String PARTIDAS = "partidas";

    private final String CLOSE = "CLOSE";
    private final String OPEN = "OPEN";
    private final String WAITING = "WAITING";
    private final String ACCEPTED = "ACCEPTED";
    private final String CANCEL = "CANCEL";

    private String keyGame;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_tabuleiro);
        ButterKnife.bind(this);
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this.databaseReference = this.firebaseDatabase.getReference(PARTIDAS);
        this.firebaseAuth = FirebaseAuth.getInstance();

        this.tabuValidate = new TabuValidate(this);
        this.tabu = this.tabuValidate.initial();
        listeners();
        this.mPlayerOne.setText(new UserAuth(this).getUser().getName());
        setupPlayers();
        this.btnLeftGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leftGame();
            }
        });
        updatePartidas();
    }


    public void efetuarJogada(int position) {
        boolean playOk = this.tabuValidate.validateJogada(position, this.tabu.getTabu());
        if(playOk) {
            this.tabu.setTabu(this.tabuValidate.efetuarJogada(position, this.actualPlayers, this.tabu.getTabu()));
            updatePlayers(this.tabu);
        }
    }

    private void updatePlayers(Tabu tabu) {
        updateView(tabu);
        int hasWinner = this.tabuValidate.hasWinner(this.tabu.getTabu(), this.actualPlayers);
        if (hasWinner == 1) { // GANHADORES
            if (this.actualPlayers.equals(Constants.PLAY_X))
                playerWinner(this.actualPlayers);
            if (this.actualPlayers.equals(Constants.PLAY_O))
                playerWinner(this.actualPlayers);
            this.tabuValidate.clearWinners();
        }

        hasWinner = this.tabuValidate.verificarNenhumVencedor(this.tabu.getTabu());
        if (hasWinner == 3) {
            empates++;
            Toast.makeText(this, "Nenhum jogador venceu!", Toast.LENGTH_SHORT).show();
            this.tabuValidate.clearWinners();
            this.mEmpates.setText(String.valueOf(empates));
            this.tabuValidate.clearWinners();
            this.clearValues();
        }

        this.updateActualPlayers();
    }

    private void updateActualPlayers() {
        if (this.actualPlayers.equals(Constants.PLAY_X)) {
            this.actualPlayers = Constants.PLAY_O;
        } else if (this.actualPlayers.equals(Constants.PLAY_O)) {
            this.actualPlayers = Constants.PLAY_X;
        }
    }

    private void playerWinner(String actualPlayers) {
        Toast.makeText(this, "O Jogador "+actualPlayers+" VENCEU!", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               clearValues();
            }
        }, 600);
    }

    private void clearValues() {
        this.tabu = this.tabuValidate.initial();
        updateView(this.tabu);
    }

    private void listeners() {

        //listener 0
        this.mValue0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                efetuarJogada(Constants.POS_0);
            }
        });

        //listener 1
        this.mValue1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                efetuarJogada(Constants.POS_1);
            }
        });

        //listener 2
        this.mValue2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                efetuarJogada(Constants.POS_2);
            }
        });

        //listener 3
        this.mValue3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                efetuarJogada(Constants.POS_3);
            }
        });

        //listener 4
        this.mValue4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                efetuarJogada(Constants.POS_4);
            }
        });

        //listener 5
        this.mValue5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                efetuarJogada(Constants.POS_5);
            }
        });

        //listener 6
        this.mValue6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                efetuarJogada(Constants.POS_6);
            }
        });

        //listener 7
        this.mValue7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                efetuarJogada(Constants.POS_7);
            }
        });

        //listener 7
        this.mValue8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                efetuarJogada(Constants.POS_8);
            }
        });
    }

    public void updateView(Tabu tabu) {
        if (tabu != null && tabu.getTabu() != null) {
            mValue0.setText(tabu.getTabu()[0]);
            mValue1.setText(tabu.getTabu()[1]);
            mValue2.setText(tabu.getTabu()[2]);
            mValue3.setText(tabu.getTabu()[3]);
            mValue4.setText(tabu.getTabu()[4]);
            mValue5.setText(tabu.getTabu()[5]);
            mValue6.setText(tabu.getTabu()[6]);
            mValue7.setText(tabu.getTabu()[7]);
            mValue8.setText(tabu.getTabu()[8]);
        }
    }

    private void leftGame() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        confirmLeftGame();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Deseja realmente deixar a partida ?").setPositiveButton("Sim",
                dialogClickListener)
                .setNegativeButton("Não", dialogClickListener).show();
    }


    private void confirmLeftGame() {
        this.databaseReference.setValue(null);
        startActivity(new Intent(this, Main.class));
        finish();
    }

    private void setupPlayers() {

    }

    public void updatePartidas() {
        this.databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                RequestGame game = dataSnapshot.getValue(RequestGame.class);
                if (game != null)
                    if (game.getProgress().equals(ACCEPTED)) {
                       updateUi(game);
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateUi(RequestGame game) {
        if (game != null) {
            this.mPlayerOne.setText(game.getPlayerOne());
            this.mPlayerTwo.setText(game.getPlayerTwo());
            this.actualPlayers = game.getSimbolo();
        }
    }
}
