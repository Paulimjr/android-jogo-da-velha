package br.com.jogo.velha;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    @BindView(R.id.text_name_player_one) TextView mPlayOneEffect;
    @BindView(R.id.text_name_player_two) TextView mPlayTwoEffect;

    //PLAYERS
    @BindView(R.id.name_player_one) TextView mPlayerOne;
    @BindView(R.id.name_player_two) TextView mPlayerTwo;
    //Empate
    @BindView(R.id.text_name_empate_value) TextView mEmpates;
    @BindView(R.id.point_player_one) TextView mVitoriasO;
    @BindView(R.id.point_player_two) TextView mVitoriasX;
    @BindView(R.id.timeToPlay) TextView mTimeToPlay;
    @BindView(R.id.frameWinner) View frameWinner;
    @BindView(R.id.tvWinner) TextView tvWinner;
    @BindView(R.id.lav_actionBar) LottieAnimationView animWinner;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private final String PARTIDAS = "partidas";

    private final String CLOSE = "CLOSE";
    private final String OPEN = "OPEN";
    private final String WAITING = "WAITING";
    private final String ACCEPTED = "ACCEPTED";
    private final String PROGRESS = "PROGRESS";
    private final String CANCEL = "CANCEL";

    private String keyGame;

    private String F_ACTUAL_PLAYER = "actualPlayer";
    private String F_TABU = "tabu";
    private String F_PROGRESS = "progress";
    private String F_VIT_X = "vitX";
    private String F_VIT_O = "vitO";
    private String F_EMPATES = "empates";
    private ObjectAnimator animator;
    private UserAuth userAuth;
    private boolean isWinner = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_tabuleiro);
        ButterKnife.bind(this);
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this.userAuth = new UserAuth(this);

        this.databaseReference = this.firebaseDatabase.getReference(PARTIDAS).child(userAuth.loadGameKey());
        this.firebaseAuth = FirebaseAuth.getInstance();

        this.tabuValidate = new TabuValidate(this);
        this.tabu = this.tabuValidate.initial();
        this.btnLeftGame.setOnClickListener(view -> leftGame());

        if (this.userAuth.getUser() != null) {
            this.mPlayerOne.setText(userAuth.getUser().getName());
        }

        listeners();
        checkIfHasGameRunning();
        updatePartidas();
    }

    private void checkIfHasGameRunning() {
        if (this.userAuth.loadGameKey() != null && !this.userAuth.loadGameKey().isEmpty())
            this.databaseReference.child(F_PROGRESS).setValue(PROGRESS);
    }

    public void efetuarJogada(int position) {
        boolean playOk = this.tabuValidate.validateJogada(position, this.tabu.getTabu());
        if(playOk && isActualPlayer()) {
            this.tabu.setTabu(this.tabuValidate.efetuarJogada(position, this.actualPlayers, this.tabu.getTabu()));
            this.databaseReference.child(F_TABU).setValue(Arrays.asList(this.tabu.getTabu()));
            updateSharedPreferences(this.tabu);
            updatePlayers(this.tabu);
        }
    }

    private void updateSharedPreferences(Tabu tabu) {
        userAuth.saveTabu(Utils.tabuListToJson(Arrays.asList(tabu.getTabu())));
    }

    private void updatePlayers(Tabu tabu) {
        int hasWinner = this.tabuValidate.hasWinner(this.tabu.getTabu(), this.actualPlayers);
        if (hasWinner == 1) { // GANHADORES
            isWinner = true;
            playerWinner(this.actualPlayers);
            this.tabuValidate.clearWinners();
        }

        hasWinner = this.tabuValidate.verificarNenhumVencedor(this.tabu.getTabu());

        if (hasWinner == 3) {
            isWinner = false;
            Toast.makeText(this, "Nenhum jogador venceu!", Toast.LENGTH_SHORT).show();
            this.tabuValidate.clearWinners();
            this.clearValues();
            this.updateEmpates();
        }

        this.updateActualPlayers();
    }

    private void updateEmpates() {
        int oldValue = Integer.parseInt(mEmpates.getText().toString());
        oldValue = oldValue + 1;
        this.databaseReference.child(F_EMPATES).setValue(oldValue);
        this.databaseReference.child(F_TABU).setValue(Arrays.asList(this.tabuValidate.initial().getTabu()));
    }

    private void updateActualPlayers() {
        if (this.actualPlayers.equals(Constants.PLAY_X)) {
            this.actualPlayers = Constants.PLAY_O;
        } else if (this.actualPlayers.equals(Constants.PLAY_O)) {
            this.actualPlayers = Constants.PLAY_X;
        }

        this.databaseReference.child(F_ACTUAL_PLAYER).setValue(this.actualPlayers);
    }

    @SuppressLint("WrongConstant")
    private void effectActualPlayer(TextView view) {

        if (animator != null) {
            animator.cancel();
        }

        animator = ObjectAnimator.ofInt(view, "backgroundColor", getResources().getColor(R.color.colorPrimaryDark), Color.RED);
        animator.setDuration(800);
        animator.setEvaluator(new ArgbEvaluator());
        animator.setRepeatMode(Animation.REVERSE);
        animator.setRepeatCount(Animation.INFINITE);
        animator.start();
    }

    private void playerWinner(String actualPlayers) {
//        Toast.makeText(this, "O Jogador "+actualPlayers+" VENCEU!", Toast.LENGTH_SHORT).show();
//        new Handler().postDelayed(this::clearValues, 800);

        this.databaseReference.child(F_TABU).setValue(Arrays.asList(this.tabuValidate.initial().getTabu()));

        if (this.actualPlayers.equals(Constants.PLAY_X)) {
            int oldValue = Integer.parseInt(mVitoriasX.getText().toString());
            oldValue = oldValue + 1;
            this.databaseReference.child(F_VIT_X).setValue(oldValue);
        }

        if (this.actualPlayers.equals(Constants.PLAY_O)) {
            int oldValue = Integer.parseInt(mVitoriasO.getText().toString());
            oldValue = oldValue + 1;
            this.databaseReference.child(F_VIT_O).setValue(oldValue);
        }


        //Show winner animation
        frameWinner.setVisibility(View.VISIBLE);
        frameWinner.bringToFront();

        if (userAuth.loadPlayer().equalsIgnoreCase(this.actualPlayers)) {
            showPlayerWinner();
        }

        new Handler().postDelayed(this::clearValues, 1300);
    }

    private void showPlayerWinner() {
        this.tvWinner.setVisibility(View.VISIBLE);
        this.animWinner.setVisibility(View.VISIBLE);
    }

    private void clearValues() {
        this.tabu = this.tabuValidate.initial();
        updateView(Arrays.asList(this.tabu.getTabu()));

        this.frameWinner.setVisibility(View.GONE);
        this.tvWinner.setVisibility(View.GONE);
        this.animWinner.setVisibility(View.GONE);
        this.isWinner = false;
    }

    public boolean isActualPlayer() {
        String player = new UserAuth(this).player();
        return player.equals(this.actualPlayers);
    }

    private void listeners() {
        //listener 0
        this.mValue0.setOnClickListener(view -> efetuarJogada(Constants.POS_0));
        //listener 1
        this.mValue1.setOnClickListener(view -> efetuarJogada(Constants.POS_1));
        //listener 2
        this.mValue2.setOnClickListener(view -> efetuarJogada(Constants.POS_2));
        //listener 3
        this.mValue3.setOnClickListener(view -> efetuarJogada(Constants.POS_3));
        //listener 4
        this.mValue4.setOnClickListener(view -> efetuarJogada(Constants.POS_4));
        //listener 5
        this.mValue5.setOnClickListener(view -> efetuarJogada(Constants.POS_5));
        //listener 6
        this.mValue6.setOnClickListener(view -> efetuarJogada(Constants.POS_6));
        //listener 7
        this.mValue7.setOnClickListener(view -> efetuarJogada(Constants.POS_7));
        //listener 7
        this.mValue8.setOnClickListener(view -> efetuarJogada(Constants.POS_8));
    }

    public void updateView(List<String> tabu) {
        if (tabu != null ) {
            mValue0.setText(tabu.get(0));
            mValue1.setText(tabu.get(1));
            mValue2.setText(tabu.get(2));
            mValue3.setText(tabu.get(3));
            mValue4.setText(tabu.get(4));
            mValue5.setText(tabu.get(5));
            mValue6.setText(tabu.get(6));
            mValue7.setText(tabu.get(7));
            mValue8.setText(tabu.get(8));
        }
    }

    private void leftGame() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    confirmLeftGame();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:

                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Deseja realmente deixar a partida ?").setPositiveButton("Sim",
                dialogClickListener)
                .setNegativeButton("Não", dialogClickListener).show();
    }
    
    private void confirmLeftGame() {
        this.firebaseDatabase.getReference(PARTIDAS).child(userAuth.loadGameKey()).setValue(null);
        this.userAuth.remove(userAuth.getGameKey());
    }

    public void updatePartidas() {
        this.databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                RequestGame game = dataSnapshot.getValue(RequestGame.class);
                if (game != null) {
                    if (game.getProgress().equals(PROGRESS)) {
                        updateUi(game);
                    }
                } else {//TODO remove this line
                    startActivity(new Intent(Tabuleiro.this, Main.class));
                    finish();
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
            this.mPlayOneEffect.setText(game.getPlayerOneName());
            this.mPlayTwoEffect.setText(game.getPlayerTwoName());

            this.actualPlayers = game.getActualPlayer();
            this.mEmpates.setText(String.valueOf(game.getEmpates()));
            this.mVitoriasX.setText(String.valueOf(game.getVitX()));
            this.mVitoriasO.setText(String.valueOf(game.getVitO()));

            if (game.getTabu() != null) {

                this.updateView(game.getTabu());
                String[] values = new String[game.getTabu().size()];

                for (int i = 0; i < game.getTabu().size(); i++) {
                    values[i] = game.getTabu().get(i);
                }

                this.tabu.setTabu(values);
            }

            //Remove all animations
            if (animator != null) {
                animator.cancel();
            }

            if (this.actualPlayers != null) {
                // Change animation
                if (this.actualPlayers.equals(Constants.PLAY_O)) {
                    effectActualPlayer(this.mPlayOneEffect);
                } else {
                    effectActualPlayer(this.mPlayTwoEffect);
                }
            }
        }
    }
}
