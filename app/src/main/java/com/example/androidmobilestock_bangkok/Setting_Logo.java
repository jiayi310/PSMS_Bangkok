package com.example.androidmobilestock_bangkok;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Setting_Logo extends AppCompatActivity {


    ACDatabase db;
    Bitmap bt;
    android.widget.ImageView harryImageView;
    String s1;
    byte[] imgCA;
    int destHeight;
    ProgressDialog progressDialog;
    Bitmap bitmap;
    byte[] img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_logo);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Logo");
        actionBar.setDisplayHomeAsUpEnabled(true);

        db = new ACDatabase(this);

        harryImageView = (android.widget.ImageView) findViewById(R.id.iv);

        Cursor ch = db.getReg("35");
        if (ch.moveToFirst()) {

            byte[] bt = ch.getBlob(0);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bt, 0, bt.length);
            harryImageView.setImageBitmap(bitmap);
        }

        Button get = findViewById(R.id.pick);


        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");

                startActivityForResult(Intent.createChooser(intent, "Pick an image"), 1);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart_menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            harryImageView = (android.widget.ImageView) findViewById(R.id.iv);

            Button save = findViewById(R.id.save);


            try {
                String type = getContentResolver().getType(data.getData());
                System.out.println(type);
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(inputStream);
                harryImageView.setImageBitmap(bitmap);

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (type.equals("image/jpeg")) {
                            new saveImage(Setting_Logo.this).execute();

                        } else {
                            Toast.makeText(Setting_Logo.this, "Image Not Saved. Please select JPEG image.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    class saveImage extends AsyncTask<Void, Void, byte[]> {
        Activity context;
        ProgressDialog pd;

        saveImage(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setMessage("Save Image...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected byte[] doInBackground(Void... params) {

            int MAX_IMAGE_SIZE = 2000 * 1024;
            int streamLength = MAX_IMAGE_SIZE;
            int compressQuality = 105;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            if (streamLength >= MAX_IMAGE_SIZE && compressQuality > 5) {
                while (streamLength >= MAX_IMAGE_SIZE && compressQuality > 5) {
                    try {
                        byteArrayOutputStream.flush();//to avoid out of memory error
                        byteArrayOutputStream.reset();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    compressQuality -= 5;
                    if (streamLength <= MAX_IMAGE_SIZE) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, compressQuality, byteArrayOutputStream);
                    } else {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, byteArrayOutputStream);
                    }
                    img = byteArrayOutputStream.toByteArray();
                    streamLength = img.length;
                    if (BuildConfig.DEBUG) {
                        Log.d("test upload", "Quality: " + compressQuality);
                        Log.d("test upload", "Size: " + streamLength);
                    }

                }
            }
            return img;
        }

        @Override
        protected void onPostExecute(byte[] s) {
            //super.onPostExecute(s);

            boolean insert = db.updateREG("35", img);
            if (insert == true) {
                onBackPressed();
                Toast.makeText(Setting_Logo.this, "Image Saved", Toast.LENGTH_SHORT).show();
                // progressDialog.dismiss();
            } else {
                Toast.makeText(Setting_Logo.this, "Image Not Saved", Toast.LENGTH_SHORT).show();
                //  progressDialog.dismiss();
            }

            if (pd.isShowing())
                pd.dismiss();

        }
    }
}