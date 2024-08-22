package com.example.androidmobilestock;

import android.content.Intent;
import android.content.IntentSender;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class WelcomePage extends AppCompatActivity {
    int MY_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome_page);
        getSupportActionBar().hide();
        LogoLauncher logoLauncher = new LogoLauncher();
        logoLauncher.start();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private class LogoLauncher extends Thread
    {
        public void run()
        {
            try{
                int SLEEP_TIMER = 2;
                sleep(1000 * SLEEP_TIMER);
            }
            catch(InterruptedException ex)
            {
                ex.printStackTrace();
            }

            Intent intent = new Intent(WelcomePage.this, Login.class);
            startActivity(intent);
            finish();
        }
    }
}
