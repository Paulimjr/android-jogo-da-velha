package br.com.jogo.velha;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.jogo.velha.models.JsonResponse;
import br.com.jogo.velha.models.RequestCreate;
import br.com.jogo.velha.models.User;
import br.com.jogo.velha.services.LoginService;
import br.com.jogo.velha.services.LoginServiceListener;
import br.com.jogo.velha.utils.Constants;
import br.com.jogo.velha.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static br.com.jogo.velha.utils.Constants.PASSWORD_CRYPTO;

public class SignUp extends AppCompatActivity implements LoginServiceListener {

    @BindView(R.id.btnCreateAccount)
    Button mCreateAccount;

    @BindView(R.id.edt_email)
    EditText mEmail;

    @BindView(R.id.edt_password)
    EditText mPassword;

    @BindView(R.id.edt_name)
    EditText mName;

    @BindView(R.id.progress_register)
    ProgressBar mProgressBar;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_sign_up);
        ButterKnife.bind(this);
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    @OnClick(R.id.btnCreateAccount)
    public void createAccount(View view) {
        validateFields();
    }


    void validateFields() {

        if (mName.getText().toString().isEmpty()) {
            Utils.showMessage(this, getString(R.string.name_invalid));
            return;
        }

        if (mEmail.getText().toString().isEmpty()) {
            Utils.showMessage(this, getString(R.string.email_invalid));
            return;
        }

        if (mPassword.getText().toString().isEmpty()) {
            Utils.showMessage(this, getString(R.string.invalid_password_));
            return;
        }

        saveRegister();
    }

    private void saveRegister() {
        new LoginService(this).requestCreate(
                new RequestCreate(mName.getText().toString(), mEmail.getText().toString(), (mPassword.getText().toString()+PASSWORD_CRYPTO)));
    }

    public void saveFireBase(final User user, final String message) {
        this.firebaseAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful())   {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if (firebaseUser != null) {

                            userId = databaseReference.push().getKey();
                            new LoginService(SignUp.this).updateUuid(user.getEmail(), userId);

                            user.setUuid(userId);
                            user.setRequestToGame(Constants.REQUEST_INACTIVE);
                            databaseReference.child("users").child(userId).setValue(user);
                            backLoginActivity(message);
                        }
                    }
                });
    }

    public void backLoginActivity(String message){
        Toast.makeText(SignUp.this, message, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void responseSuccess(JsonResponse response) {
        if (response != null && response.getData() != null) {
            saveFireBase(response.getData(), response.getMessage());
        }
    }

    @Override
    public void errorServer(String message) {
        if (message != null) {
            Toast.makeText(SignUp.this, message, Toast.LENGTH_SHORT).show();
        }
    }
}
