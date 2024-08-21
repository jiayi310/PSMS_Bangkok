package com.example.androidmobilestock;

import android.content.Intent;
import android.content.IntentSender;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;

public class WelcomePage extends AppCompatActivity {
    int MY_REQUEST_CODE = 123;
    AppUpdateManager appUpdateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome_page);
        getSupportActionBar().hide();
        LogoLauncher logoLauncher = new LogoLauncher();
        logoLauncher.start();

        //IN - APP UPDATES
        // Update Listener
        InstallStateUpdatedListener installStateUpdatedListener = new InstallStateUpdatedListener() {
            @Override
            public void onStateUpdate(InstallState state) {
                if (state.installStatus() == InstallStatus.DOWNLOADED) {
                    popupSnackbarForCompleteUpdate();
                    Intent intent = new Intent(WelcomePage.this, Login.class);
                    startActivity(intent);
                    finish();
                } else if (state.installStatus() == InstallStatus.INSTALLED) {
                    if (appUpdateManager != null) {
                        appUpdateManager.unregisterListener(this);
                        Intent intent = new Intent(WelcomePage.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                    Log.i("custDebug", "Installed");
                } else {
                    Log.i("custDebug", "InstallStateUpdatedListener: state: " + state.installStatus());
                }
            }
        };
        // Creates instance of the manager.
        appUpdateManager = AppUpdateManagerFactory.create(this);
        // Register Listener to update manager
        appUpdateManager.registerListener(installStateUpdatedListener);
//        // Returns an intent object that you use to check for an update.
//        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
//        // Checks that the platform will allow the specified type of update.
//        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
//            @Override
//            public void onSuccess(AppUpdateInfo appUpdateInfo) {
//                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
//                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
//                    // Request the update.
//                    try {
//                        appUpdateManager.startUpdateFlowForResult(
//                                // Pass the intent that is returned by 'getAppUpdateInfo()'.
//                                appUpdateInfo,
//                                // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
//                                AppUpdateType.IMMEDIATE,
//                                // The current activity making the update request.
//                                WelcomePage.this,
//                                // Include a request code to later monitor this update request.
//                                MY_REQUEST_CODE);
//                    } catch (IntentSender.SendIntentException e) {
//                        Log.i("custDebug", e.getMessage());
//                    }
//                } else {
//                    Log.i("custDebug", "No Updates");
//                    popupSnackbarForCompleteUpdate();
//                }
//            }
//        });
    }

    // Snackbar "You updated, Good Job"
    private void popupSnackbarForCompleteUpdate(){
        Snackbar.make(this.findViewById(R.id.logo),
                "Update Successful", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        appUpdateManager.getAppUpdateInfo()
                .addOnSuccessListener( new OnSuccessListener<AppUpdateInfo>() {
                    @Override
                    public void onSuccess(AppUpdateInfo appUpdateInfo) {
                        if (appUpdateInfo.updateAvailability()
                                == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                            // If an in-app update is already running, resume the update.
                            try {
                                appUpdateManager.startUpdateFlowForResult(
                                        appUpdateInfo,
                                        AppUpdateType.IMMEDIATE,
                                        WelcomePage.this,
                                        MY_REQUEST_CODE);
                            } catch (IntentSender.SendIntentException e) {
                                Log.i("custDebug", e.getMessage());
                            }
                        }
                    }
                });
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
