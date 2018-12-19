package br.com.jogo.velha;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.jogo.velha.models.JsonResponse;
import br.com.jogo.velha.models.RequestGame;
import br.com.jogo.velha.models.User;
import br.com.jogo.velha.services.LoginService;
import br.com.jogo.velha.services.LoginServiceListener;
import br.com.jogo.velha.utils.Constants;
import br.com.jogo.velha.utils.UserAuth;
import br.com.jogo.velha.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Main extends AppCompatActivity
implements LoginServiceListener {

    AlertDialog.Builder alert;

    @BindView(R.id.text_logout) TextView mLogout;
    @BindView(R.id.edt_search_user) EditText mEdtEmailSearch;
    @BindView(R.id.btn_search_email) Button btnSearch;
    @BindView(R.id.text_logged) TextView mUserName;
    @BindView(R.id.text_logged_email) TextView mUserEmail;

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
        setContentView(R.layout.act_main);
        ButterKnife.bind(this);
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this.databaseReference = this.firebaseDatabase.getReference(PARTIDAS);

        this.firebaseAuth = FirebaseAuth.getInstance();
        this.dialog = new ProgressDialog(this);
        this.mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userAuth = new UserAuth(Main.this);
                userAuth.logout();
                startActivity(new Intent(Main.this, LoginActivity.class));
                finish();
            }
        });

        this.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateFieldEmail();
            }
        });
        getUserLogged();
        updatePartidas();
    }

    private void getUserLogged() {
        User user = new UserAuth(this).getUser();
        mUserName.setText(user.getName());
        mUserEmail.setText(user.getEmail());
    }

    private void validateFieldEmail() {
        if (mEdtEmailSearch.getText().toString().isEmpty()) {
            Utils.showMessage(this, getString(R.string.email_invalid));
            return;
        } else {
            if (new UserAuth(this).getUser().getEmail()
                    .equals(mEdtEmailSearch.getText().toString())) {
                Utils.showMessage(this, getString(R.string.no_search_myself));
                return;
            }
        }

        searchByEmail();
    }

    private void searchByEmail() {
        new LoginService(this).findByEmail(mEdtEmailSearch.getText().toString());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void showLoading() {
        this.dialog.setMessage(getResources().getString(R.string.searching));
        this.dialog.show();
    }

    @Override
    public void hideLoading() {
        this.dialog.dismiss();
    }

    @Override
    public void responseSuccess(JsonResponse response) {
        if (response.getData() != null) {
            requestGameToUser(response.getData());
        } else {
            Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void requestGameToUser(final User data) {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        callServiceRequestUser(data);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        mEdtEmailSearch.setText("");
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Deseja notificar o jogador "+data.getEmail()+" para jogar com você ?").setPositiveButton("Sim",
                dialogClickListener)
                .setNegativeButton("Não", dialogClickListener).show();
    }

    private void callServiceRequestUser(User data) {
        User playOne = new UserAuth(this).getUser();
        RequestGame requestGame = new RequestGame(playOne.getEmail(), data.getEmail(), playOne.getEmail(), "0", "0", "0", WAITING, Constants.PLAY_O);
        keyGame = this.databaseReference.push().getKey();
        requestGame.setKey(keyGame);
        this.databaseReference.setValue(requestGame);
    }

    private void updatePartidas() {
        this.databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                RequestGame game = dataSnapshot.getValue(RequestGame.class);
                if (game != null)
                    if (game.getProgress().equals(WAITING)) {
                        if (game.getActualPlayer() != null) {
                             if (game.getPlayerTwo().equals(new UserAuth(VelhaApplication.getInstance()).getUser().getEmail())) {
                                showAlertConfirmation();
                            }
                        }
                    } else if (game.getProgress().equals(ACCEPTED)) {
                        startActivity(new Intent(Main.this, Tabuleiro.class));
                        finish();
                    } else if (game.getProgress().equals(CANCEL)) {
                        if (game.getActualPlayer() != null && !new UserAuth(VelhaApplication.getInstance()).getUser().getEmail().equals(game.getPlayerTwo())) {
                            Toast.makeText(Main.this, "Jogador não aceitou sua solicitação!", Toast.LENGTH_SHORT).show();
                            removeSolicitation();
                        }
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void removeSolicitation() {
        this.databaseReference.setValue(null);
    }

    private void showAlertConfirmation() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        confirmGame();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        cancel();
                        mEdtEmailSearch.setText("");
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Aceitar solicitação de Jogo?").setPositiveButton("Sim",
                dialogClickListener)
                .setNegativeButton("Não", dialogClickListener).show();
    }

    private void confirmGame() {
        this.databaseReference.child("progress").setValue(ACCEPTED);
    }

    private void cancel() {
        this.databaseReference.child("progress").setValue(CANCEL);
    }

    @Override
    public void errorServer(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void waitingOtherPlayer() {
        this.dialog.setMessage("Aguardando jogador...");
        this.dialog.show();
        this.dialog.setCancelable(false);
    }

}
