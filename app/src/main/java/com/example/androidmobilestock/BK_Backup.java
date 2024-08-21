package com.example.androidmobilestock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BK_Backup extends AppCompatActivity {

    CardView backup, restore;
    /*------For storage permission------*/
    private static final int STORAGE_REQUEST_CODE_EXPORT = 1;
    private static final int STORAGE_REQUEST_CODE_IMPORT = 2;
    private String[] storagePermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bk_activity_backup);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("BK_Backup/BK_Restore");
        actionBar.setDisplayHomeAsUpEnabled(true);
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        backup = findViewById(R.id.backup);
        restore = findViewById(R.id.restore);
        if (checkStoragePermission()) {
        } else {
            requestStoragePermissionExport();
        }
        backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(BK_Backup.this);
                builder.setCancelable(true);
                builder.setTitle("Are you sure want to backup?");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                exportCSV();
                                onBackPressed();
                            }
                        });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BK_Backup.this, BK_Restore.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkStoragePermission() {
        //check if storage permission is enabled or not and return true/false
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermissionImport() {
        //request storage permission for import
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE_IMPORT);
    }

    private void requestStoragePermissionExport() {
        //request storage permission for export
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE_EXPORT);
    }

    private void exportCSV() {

        Calendar now = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmssdd");
        String result = df.format(now.getTime()) + "_psmsbackup";

        //path of csv file
        File folder = new File(Environment.getExternalStorageDirectory() + "/" + "PSMSBackup");
        boolean isFolderCreated = false;
        if (!folder.exists()) {
            isFolderCreated = folder.mkdir();
        }

        Log.d("CSC_TAG", "exportCSV: " + isFolderCreated);
        try {
            File PSMSBackup = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (PSMSBackup.canWrite()) {
                String currentDBPath = "//data/data/com.example.androidmobilestock/databases";

                File folder2 = new File(folder + "/" + result);
                boolean isFolderCreated2 = false;
                if (!folder2.exists()) {
                    isFolderCreated2 = folder2.mkdir();
                }

                File directory = new File(currentDBPath);
                directory.listFiles();
                File[] files = directory.listFiles();
                Log.d("Files", "Size: " + files.length);
                for (int i = 0; i < files.length; i++) {
                    Log.d("Files", "FileName:" + files[i].getName());
                    String backupDBPath = "//PSMSBackup/" + result + "/" + files[i].getName();
                    File currentDB = new File(files[i].getAbsolutePath());
                    File backupDB = new File(PSMSBackup, backupDBPath);

                    if (currentDB.exists()) {
                        FileChannel src = new FileInputStream(currentDB).getChannel();
                        FileChannel dst = new FileOutputStream(backupDB).getChannel();

                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();
                    }
                }
                Toast.makeText(getApplicationContext(), "BK_Backup successfully", Toast.LENGTH_SHORT).show();
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //handle permission result
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case STORAGE_REQUEST_CODE_EXPORT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission granted
                    //exportCSV();
                    Toast.makeText(this, "Storage permission granted...", Toast.LENGTH_SHORT).show();
                } else {
                    //permission denied
                    Toast.makeText(this, "Storage permission required...", Toast.LENGTH_SHORT).show();
                }
            }
            break;

            case STORAGE_REQUEST_CODE_IMPORT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission granted
                    //importCSV();
                    Toast.makeText(this, "Storage permission granted...", Toast.LENGTH_SHORT).show();
                } else {
                    //permission denied
                    Toast.makeText(this, "Storage permission required...", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}