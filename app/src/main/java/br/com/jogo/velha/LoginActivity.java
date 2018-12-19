package br.com.jogo.velha;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.jogo.velha.models.JsonResponse;
import br.com.jogo.velha.models.User;
import br.com.jogo.velha.services.LoginService;
import br.com.jogo.velha.services.LoginServiceListener;
import br.com.jogo.velha.utils.UserAuth;
import br.com.jogo.velha.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static br.com.jogo.velha.utils.Constants.PASSWORD_CRYPTO;

public class LoginActivity extends AppCompatActivity implements LoginServiceListener {

    @BindView(R.id.btnSignIn)
    Button mBtnSignIn;

    @BindView(R.id.txt_create_account)
    TextView mCreateAccount;

    @BindView(R.id.edt_username)
    EditText mUsername;

    @BindView(R.id.edt_password)
    EditText mPassword;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private User user;
    private UserAuth userAuth;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        ButterKnife.bind(this);
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.userAuth = new UserAuth(this);
        isLogged();
        this.dialog = new ProgressDialog(this);
    }

    private void isLogged() {
        if(new UserAuth(this).getUserLogged() != null
                && !new UserAuth(this).getUserLogged().equals("")) {
           openMain();
        }
    }

    @OnClick(R.id.txt_create_account)
    public void createAccount(View view) {
        openCreateAccount();
    }

    @OnClick(R.id.btnSignIn)
    public void signIn(View view) {
        validateLogin();
    }

    private void validateLogin() {
        if (mUsername.getText().toString().isEmpty()) {
            Utils.showMessage(this, getString(R.string.username_invalid));
            return;
        }

        if (mPassword.getText().toString().isEmpty()) {
            Utils.showMessage(this, getString(R.string.invalid_password_));
            return;
        }

        login();
    }

    private void login() {
        loading();
        user = new User(mUsername.getText().toString(), (mPassword.getText().toString()+PASSWORD_CRYPTO));
        this.firebaseAuth.signInWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideLoading();

                        if (task.isSuccessful())   {
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                saveDevice(firebaseUser);
                            } else {
                                Toast.makeText(LoginActivity.this, getString(R.string.error_login), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, getString(R.string.error_login), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void loading() {
        this.dialog.setMessage(getResources().getString(R.string.loagind));
        this.dialog.show();
    }

    @Override
    public void showLoading() {
        this.dialog.setMessage(getResources().getString(R.string.loagind));
        this.dialog.show();
    }

    @Override
    public void hideLoading() {
        this.dialog.dismiss();
    }

    @Override
    public void responseSuccess(JsonResponse response) {
        if (response != null) {
            this.userAuth.save(Utils.userToJson(response.getData()));
            openMain();
        }
    }

    @Override
    public void errorServer(String message) {

    }

    private void saveDevice(FirebaseUser firebaseUser) {
        this.user.setUuid(firebaseUser.getUid());
        new LoginService(this).findByEmail(firebaseUser.getEmail());
    }

    public void openMain() {
        startActivity(new Intent(LoginActivity.this, Main.class));
        finish();
    }

    public void openCreateAccount() {
        startActivity(new Intent(this, SignUp.class));
    }
}
