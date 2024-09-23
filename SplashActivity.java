package com.example.a3_termproject_steam;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler hd = new Handler();
        hd.postDelayed(new SplashHandler(), 1500);
    }

    private class SplashHandler implements Runnable {
        public void run() {
            startActivity(new Intent(getApplication(), LoginActivity.class));
            SplashActivity.this.finish();
        }
    }
}