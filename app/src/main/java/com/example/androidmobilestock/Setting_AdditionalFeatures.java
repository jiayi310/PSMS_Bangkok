package com.example.androidmobilestock;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Setting_AdditionalFeatures extends AppCompatActivity {

    TextView reorder_button,  noOfDays_text;
    LinearLayout layout68;
    ACDatabase db;
    SharedPreferences sharedPreferences;
    String noOfDays;

    CharSequence[] days = {"Last 30 days", "Last 2 months", "Last 3 months", "Last 6 months"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_additional_features);

        reorder_button = findViewById(R.id.reorder_button);
        layout68 = findViewById(R.id.layout68);
        noOfDays_text = findViewById(R.id.noOfDays_text);

        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Additional Features");
        } catch (Exception e) {
            Log.i("custDebug", e.getMessage());
        }

        sharedPreferences = getSharedPreferences("FeaturesEnable", Context.MODE_PRIVATE);

        updateUI();

        reorder_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reorder_button.getText().toString().equals("Disable")) {
                    new AlertDialog.Builder(Setting_AdditionalFeatures.this)
                            .setTitle("Confirmation")
                            .setMessage("Are you sure you want to enable this feature?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    enableReorderFeature();
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                } else {
                    new AlertDialog.Builder(Setting_AdditionalFeatures.this)
                            .setTitle("Confirmation")
                            .setMessage("Are you sure you want to disable this feature?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    disableReorderFeature();
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            }
        });
        layout68.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noOfDays = sharedPreferences.getString("reorder_days", "Last 30 days"); // Default to "Last 30 days" if no value is saved
                int defaultSelection = 0; // Default to the first item if no match is found

                for (int i = 0; i < days.length; i++) {
                    if (days[i].toString().equals(noOfDays)) {
                        defaultSelection = i;
                        break;
                    }
                }

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Setting_AdditionalFeatures.this);
                alertDialogBuilder.setTitle("Show History Within");
                alertDialogBuilder.setSingleChoiceItems(days, defaultSelection, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String selectedDays = days[i].toString();
                        noOfDays_text.setText(selectedDays);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("reorder_days", selectedDays);
                        editor.apply();

                        dialogInterface.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        boolean isReorderEnabled = sharedPreferences.getBoolean("reorder_enabled", false);
        noOfDays = sharedPreferences.getString("reorder_days", "Last 30 days");

        if (isReorderEnabled) {
            reorder_button.setText("Enable");
        } else {
            reorder_button.setText("Disable");
            noOfDays_text.setVisibility(View.GONE);
        }

        noOfDays_text.setText(noOfDays);
    }

    private void enableReorderFeature() {
        reorder_button.setText("Enable");
        noOfDays_text.setVisibility(View.VISIBLE);
        noOfDays_text.setText(noOfDays);

        // Save enable status to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("reorder_enabled", true);
        editor.apply();
    }

    private void disableReorderFeature() {
        reorder_button.setText("Disable");
        noOfDays_text.setText("");
        noOfDays_text.setVisibility(View.GONE);

        // Save disable status to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("reorder_enabled", false);
        editor.apply();

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}