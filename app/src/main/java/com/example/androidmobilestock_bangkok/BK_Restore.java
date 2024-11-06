package com.example.androidmobilestock_bangkok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;

public class BK_Restore extends AppCompatActivity {

    /*------For storage permission------*/
    private static final int STORAGE_REQUEST_CODE_IMPORT = 2;
    private String[] storagePermissions;
    ListView listView;
    String filename;
    ArrayAdapter arrayAdapter;
    String path;
    ACDatabase db;
    String uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bk_activity_restore);
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("BK_Restore");
        actionBar.setDisplayHomeAsUpEnabled(true);
        db = new ACDatabase(this);

        listView = (ListView) findViewById(R.id.listviewfile);
        ArrayList<String> arrayList = new ArrayList<>();

        path = Environment.getExternalStorageDirectory() + "/PSMSBackup";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: " + files.length);
        for (int i = 0; i < files.length; i++) {
            Log.d("Files", "FileName:" + files[i].getName());
            arrayList.add(files[i].getName());
        }
        Collections.sort(arrayList);
        Collections.reverse(arrayList);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                filename = arrayList.get(position).toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(BK_Restore.this);
                builder.setCancelable(true);
                builder.setTitle("Confirm BK_Restore file " + filename + " ?");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (checkStoragePermission()) {
                                    importCSV(filename);
                                    onBackPressed();
                                    Toast.makeText(getApplicationContext(), "Restored successfully", Toast.LENGTH_SHORT).show();
                                    Cursor data = db.getReg("8");
                                    if(data.moveToFirst()){
                                        uuid = data.getString(0);
                                    }
                                    SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putString("UUID",uuid);
                                    editor.apply();
                                } else {
                                    requestStoragePermissionImport();
                                }

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

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                filename = arrayList.get(position).toString();

                AlertDialog.Builder builder = new AlertDialog.Builder(BK_Restore.this);
                builder.setCancelable(true);
                builder.setTitle("Delete " + filename + " ?");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String file = Environment.getExternalStorageDirectory().getAbsolutePath();
                                String filenametodelete = "//PSMSBackup/" + filename;
                                File currentfile = new File(file, filenametodelete);
                                if (currentfile.exists()) {
                                    try {
                                        FileUtils.deleteDirectory(currentfile);
                                        arrayList.remove(position);
                                        arrayAdapter.notifyDataSetChanged();
                                        Toast.makeText(getApplicationContext(), "Delete file successfully", Toast.LENGTH_SHORT).show();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
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

    private void importCSV(String file_name) {
        try {
            File PSMSBackup = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (PSMSBackup.canWrite()) {
                String currentDBPath = "//data/data/com.example.androidmobilestock/databases";
                String backupDBPath = "//PSMSBackup/" + file_name;
                File directory = new File(PSMSBackup,backupDBPath);
                directory.listFiles();
                File[] files = directory.listFiles();
                Log.d("Files", "Size: " + files.length);

                File directory2 = new File(currentDBPath);
                directory2.listFiles();
                File[] files2 = directory2.listFiles();

                for (int i = 0; i < files2.length; i++) {
                    files2[i].delete();
                }

                for (int i = 0; i < files.length; i++) {
                    Log.d("Files", "FileName:" + files[i].getName());
                    //String backupDBPath = "//PSMSBackup/" + file_name + "/" + files[i].getName();
                    File currentDB = new File(currentDBPath,files[i].getName());
                    File backupDB = new File(directory,files[i].getName());

                            FileChannel src = new FileInputStream(backupDB).getChannel();

                            FileChannel dst = new FileOutputStream(currentDB).getChannel();

                            dst.transferFrom(src, 0, src.size());
                            src.close();
                            dst.close();


                }
                Toast.makeText(getApplicationContext(), "Database Restored successfully", Toast.LENGTH_SHORT).show();
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


            case STORAGE_REQUEST_CODE_IMPORT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission granted
                    importCSV(filename);
                } else {
                    //permission denied
                    Toast.makeText(this, "Storage permission required...", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}