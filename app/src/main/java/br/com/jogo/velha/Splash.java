package br.com.jogo.velha;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_splash);
        openMainActivity();
    }

    private void openMainActivity() {
        new Handler().postDelayed(() -> {
//            startActivity(new Intent(Splash.this, Tabuleiro.class));
                startActivity(new Intent(Splash.this, LoginActivity.class));
            finish();
        }, 2400);
    }
}
