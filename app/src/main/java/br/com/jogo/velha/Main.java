package br.com.jogo.velha;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

import br.com.jogo.velha.models.JsonResponse;
import br.com.jogo.velha.models.RequestGame;
import br.com.jogo.velha.models.Tabu;
import br.com.jogo.velha.models.User;
import br.com.jogo.velha.services.LoginService;
import br.com.jogo.velha.services.LoginServiceListener;
import br.com.jogo.velha.utils.Constants;
import br.com.jogo.velha.utils.TabuValidate;
import br.com.jogo.velha.utils.UserAuth;
import br.com.jogo.velha.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Main extends AppCompatActivity
        implements LoginServiceListener {

    AlertDialog.Builder alert;

    @BindView(R.id.text_logout)
    TextView mLogout;
    @BindView(R.id.edt_search_user)
    EditText mEdtEmailSearch;
    @BindView(R.id.btn_search_email)
    Button btnSearch;
    @BindView(R.id.text_logged)
    TextView mUserName;
    @BindView(R.id.text_logged_email)
    TextView mUserEmail;

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
    private final String PROGRESS = "PROGRESS";
    private final String CANCEL = "CANCEL";

    private String keyGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        ButterKnife.bind(this);
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        userAuth = new UserAuth(Main.this);

        this.firebaseAuth = FirebaseAuth.getInstance();
        this.dialog = new ProgressDialog(this);
        this.mLogout.setOnClickListener(view -> {
            userAuth.logout();
            startActivity(new Intent(Main.this, LoginActivity.class));
            finish();
        });

        this.btnSearch.setOnClickListener(view -> validateFieldEmail());
        getUserLogged();
        updatePartidas();
    }

    private void getUserLogged() {
        User user = new UserAuth(this).getUser();
        if (user != null) {
            mUserName.setText(user.getName());
            mUserEmail.setText(user.getEmail());
        }
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

        if (!Utils.isNetworkConnected()) {
            Toast.makeText(this, getString(R.string.default_no_internet), Toast.LENGTH_SHORT).show();
        } else {
            searchByEmail();
        }
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

        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    callServiceRequestUser(data);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    mEdtEmailSearch.setText("");
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Deseja notificar o jogador " + data.getEmail() + " para jogar com você ?").setPositiveButton("Sim",
                dialogClickListener)
                .setNegativeButton("Não", dialogClickListener).show();
    }

    private void callServiceRequestUser(User data) {
        waitingOtherPlayer();

        User playOne = new UserAuth(this).getUser();
        Tabu tabu = new TabuValidate(this).initial();

        RequestGame requestGame = new RequestGame(playOne.getName(), data.getName(), playOne.getEmail(), data.getEmail(),
                Constants.PLAY_O, 0, 0, 0, WAITING, Constants.PLAY_O, Arrays.asList(tabu.getTabu()), 0, 0);
        keyGame = this.databaseReference.push().getKey();
        requestGame.setKey(keyGame);
        this.databaseReference.child(PARTIDAS).child(keyGame).setValue(requestGame);
        this.userAuth.save(userAuth.getGameKey(), keyGame);

        saveMyPlay(Constants.PLAY_O);
    }

    private void saveMyPlay(String value) {
        new UserAuth(this).savePlayer(value);
    }

    private void updatePartidas() {
        DatabaseReference stageList = this.databaseReference.child(PARTIDAS);
        stageList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                RequestGame game = null;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    game = child.getValue(RequestGame.class);

                    if (game != null && !game.getProgress().equalsIgnoreCase(PROGRESS)) {

                        if (game.getPlayerTwo().equals(new UserAuth(VelhaApplication.getInstance()).getUser().getEmail())) {
                            break;
                        }
                    }

                }

                if (game != null && game.getProgress() != null && !game.getProgress().equalsIgnoreCase(PROGRESS)) {
                    switch (game.getProgress()) {
                        case WAITING:
                            if (game.getActualPlayer() != null) {
                                if (keyGame == null) {
                                    keyGame = game.getKey();
                                }
                                if (game.getPlayerTwo().equals(new UserAuth(getBaseContext()).getUser().getEmail())) {
                                    showAlertConfirmation();
                                    break;
                                }
                            }
                            break;
                        case ACCEPTED:
                            startActivity(new Intent(Main.this, Tabuleiro.class));
                            finish();
                            break;
                        case CANCEL:
                            if (game.getActualPlayer() != null && !new UserAuth(getBaseContext()).getUser().getEmail().equals(game.getPlayerTwo())) {
                                Toast.makeText(Main.this, "Jogador não aceitou sua solicitação!", Toast.LENGTH_SHORT).show();
                                cancelWaitingOtherPlayer();
                                removeSolicitation();
                            }
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void removeSolicitation() {
        if (keyGame != null) {
            this.databaseReference.child(PARTIDAS).child(keyGame).setValue(null);
        }

        userAuth.remove(userAuth.getGameKey());
    }

    private void showAlertConfirmation() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    confirmGame();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    cancel();
                    mEdtEmailSearch.setText("");
                    break;
            }
        };

        if (!isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage("Aceitar solicitação de Jogo?").setPositiveButton("Sim",
                    dialogClickListener)
                    .setNegativeButton("Não", dialogClickListener).show();
        }
    }

    private void confirmGame() {
        saveMyPlay(Constants.PLAY_X);
        userAuth.save(userAuth.getGameKey(), keyGame);

        this.databaseReference.child(PARTIDAS).child(keyGame).child("progress").setValue(ACCEPTED);
    }

    private void cancel() {
        this.databaseReference.child(PARTIDAS).child(keyGame).child("progress").setValue(CANCEL);
        userAuth.remove(userAuth.getGameKey());
    }

    @Override
    public void errorServer(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void waitingOtherPlayer() {
        this.dialog.setMessage("Aguardando jogador aceitar...");
        this.dialog.show();
        this.dialog.setCancelable(false);
    }

    private void cancelWaitingOtherPlayer() {
        if (this.dialog != null && this.dialog.isShowing()) {
            this.dialog.cancel();
        }
    }
}
