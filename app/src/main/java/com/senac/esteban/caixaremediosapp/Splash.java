package com.senac.esteban.caixaremediosapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class Splash extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                finish();

                Intent intent = new Intent();
                intent.setClass(Splash.this, PrincipalActivity.class);
                startActivity(intent);
            }
        }, 2000);
    }
}
