package com.example.androidmobilestock;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.androidmobilestock.databinding.ActivityLoginBinding;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/*
Changes Made:
1. Added UOM to stock count list row
2. Refreshed info in settings after changes
 */
public class Login extends AppCompatActivity {
    InputStream inputStream;
    ACDatabase db;

    ActivityLoginBinding binding;
    AC_Class.Connection connection;
    AC_Class.Register reg;
    String versionNo;
    String url;
    String urlStr, urlOri;
    String id;
    String pwd;
    String uuid;
    private static String uniqueID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        getSupportActionBar().hide();

        // Placeholder
        versionNo = "3.9.35";

        reg = new AC_Class.Register();

        connection = new AC_Class.Connection();
        binding.setConSettings(connection);

        db = new ACDatabase(this);

        Cursor cuuid = db.getReg("8");
        if (cuuid.moveToFirst()) {
            uniqueID = cuuid.getString(cuuid.getColumnIndex("Value"));
        }

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String uuid = sharedPref.getString("UUID", "");


        if (uniqueID == null) {
            if (uuid.equals("")) {
                uniqueID = UUID.randomUUID().toString();
            } else {
                uniqueID = uuid;
            }
        }


        db.updateREG("8", uniqueID);
        binding.lblUUID.setText(uniqueID);
        binding.lblVersion.setText(versionNo);

        db.updateREG("1", versionNo);


        Cursor get = db.getReg("4");
        if (get != null) {
            binding.rmbCheckBox.setChecked(true);

            Cursor url1 = db.getReg("2");
            if (url1.moveToFirst()) {
                url = url1.getString(0);
            }

            Cursor urls = db.getReg("3");
            if (urls.moveToFirst()) {
                binding.txtURL.setText(urls.getString(urls.getColumnIndex("Value")));
                urlOri = urls.getString(urls.getColumnIndex("Value"));
            }


            Cursor Id = db.getReg("4");
            if (Id.moveToFirst()) {
                binding.txtID.setText(Id.getString(Id.getColumnIndex("Value")));
            }

            Cursor Psw = db.getReg("5");
            if (Psw.moveToFirst()) {
                binding.txtpw.setText(Psw.getString(Psw.getColumnIndex("Value")));
            }
        }

        if (Build.MODEL.equals("HC720")) {
            Snackbar.make(findViewById(android.R.id.content), "RFID Detected.", Snackbar.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {

        finish();
    }

    //On return of intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 7) {
            url = data.getStringExtra("URLKey");
            urlStr = data.getStringExtra("URLStr");
            Log.i("custDebug", url + ", " + urlStr);
            binding.txtURL.setText(urlStr);
            //if (!TextUtils.isEmpty(binding.txtURL.getText().toString())) {
            //new GetModules(Login.this).execute(url);
            //new GetLoginList(Login.this).execute(url);
            //new SetDevice(Login.this).execute(url);
            // }
        }
    }

    //Open connection settings
    public void btnSetClicked(View view) {
        Intent intent = new Intent(Login.this, ConnectionSettings.class);
        startActivityForResult(intent, 7);
    }

    //Profile Download
    public void profileDownload(View view) {
        new GetModules(Login.this).execute(url);
    }

    //Reset Database
    public void btnResetDbClicked(final View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset DB");
        builder.setMessage("Are you sure you want to reset the Database?");
        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (input.getText().toString().equals("presoftmobile")) {
                    db.close();
                    Cursor data = db.getReg("8");
                    if (data.moveToFirst()) {
                        uuid = data.getString(0);
                    }
                    SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("UUID", uuid);
                    editor.apply();
                    deleteDatabase("AutoCountDatabase");
                } else {
//                    Toast.makeText(getApplicationContext(), "Incorrect Password", Toast.LENGTH_SHORT);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    //Login button
    public void btnLoginBtnClicked(View view) {
        if (TextUtils.isEmpty(binding.txtURL.getText().toString())) {
            binding.txtURL.setError("This field can't be blank.");
            //return;
        }
        if (TextUtils.isEmpty(binding.txtID.getText().toString())) {
            binding.txtID.setError("This field can't be blank.");
            //return;
        }
        if (TextUtils.isEmpty(binding.txtpw.getText().toString())) {
            binding.txtpw.setError("This field can't be blank.");
            //return;
        }
        //Non-empty fields
        else {

            if (!TextUtils.isEmpty(binding.txtURL.getText().toString())) {
                new GetModules(Login.this).execute(url);
                //new GetLoginList(Login.this).execute(url);
                //new SetDevice(Login.this).execute(url);
            }


        }
    }

    //Check Connection status
    class GetModules extends AsyncTask<String, Void, Boolean> {
        Activity context;

        public GetModules(Activity context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(String... connUrl) {

            HttpURLConnection conn = null;
            BufferedReader reader;

            try {
                final java.net.URL url = new URL(connUrl[0] + "/getModules");
                conn = (HttpURLConnection) url.openConnection();
                conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(1000);
                conn.setReadTimeout(1000);

                InputStream inputStream = new BufferedInputStream(conn.getInputStream());

                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                reader.close();
                String status = sb.toString();

                JSONArray modules = new JSONArray(status);
                if (modules.length() >0){
                    db.deleteModules();

                    for (int i = 0; i < modules.length(); i++) {
                        JSONObject object = modules.getJSONObject(i);

                        String Name = object.getString("Name");
                        String Activate = object.getString("Activate");

                        boolean insert = db.insertModules(Name, Activate);
                        if (!insert) {
                            throw new Exception("Failed to download modules");
                        }
                    }
                }
                conn.disconnect();

            } catch (Exception ex) {
                Log.d("TryCatch", "get modules exception: " + ex);
                ex.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
            new GetLoginList(Login.this).execute(url);
        }

    }

    class SetDevice extends AsyncTask<String, Void, Boolean> {
        Activity context;

        public SetDevice(Activity context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(String... connUrl) {
            HttpURLConnection conn;
            Boolean returns = false;

            try {
                final java.net.URL url = new URL(connUrl[0] + "/setDevice");
                conn = (HttpURLConnection) url.openConnection();
                conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setReadTimeout(1000);
                conn.setConnectTimeout(1000);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write("\"" + uniqueID + "\"");
                writer.flush();
                writer.close();
                os.close();
                returns = true;

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                Log.i("custDebug", "setDevice - " + br.readLine());
                conn.disconnect();
            } catch (Exception ex) {
                Log.d("TryCatch", "set Device: " + ex);
                Log.i("custDebug", "setDevice - " + ex.toString());
            }
            return returns;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);

            if (s == true) {
                //Toast.makeText(Login.this, "Successful", Toast.LENGTH_SHORT).show();
                db.updateREG("3", urlStr);
                if (urlOri.equals("")) {
                    urlOri = urlStr;
                }
            } else {
                //Toast.makeText(Login.this, "Failed", Toast.LENGTH_SHORT).show();
            }

            Cursor checkData = db.loginValidate(binding.txtID.getText().toString(),
                    binding.txtpw.getText().toString().toUpperCase());

            if (checkData.getCount() > 0) {
                checkData.moveToNext();

                db.updateREG("2", url);

                urlStr = binding.txtURL.getText().toString();
                //db.updateREG("3", urlStr);

                if (binding.rmbCheckBox.isChecked()) {
                    id = binding.txtID.getText().toString();
                    db.updateREG("4", id);
                    pwd = binding.txtpw.getText().toString();
                    db.updateREG("5", pwd);
                }

                db.updateREG("9", String.valueOf(checkData.getInt(checkData.getColumnIndex("EnableSetting"))));
                //db.updateREG("14", String.valueOf(checkData.getInt(checkData.getColumnIndex("FilterByAgent"))));
                db.updateREG("10", String.valueOf(checkData.getInt(checkData.getColumnIndex("Sales"))));
                db.updateREG("11", String.valueOf(checkData.getInt(checkData.getColumnIndex("Transfer"))));
                db.updateREG("12", String.valueOf(checkData.getInt(checkData.getColumnIndex("Purchase"))));
                db.updateREG("19", String.valueOf(checkData.getInt(checkData.getColumnIndex("PackingList"))));
                db.updateREG("36", String.valueOf(checkData.getInt(checkData.getColumnIndex("Catalog"))));
                db.updateREG("45", String.valueOf(checkData.getInt(checkData.getColumnIndex("Analytics"))));
                db.updateREG("46", String.valueOf(checkData.getInt(checkData.getColumnIndex("Collection"))));
                db.updateREG("47", String.valueOf(checkData.getInt(checkData.getColumnIndex("StockAssembly"))));
                db.updateREG("48", String.valueOf(checkData.getInt(checkData.getColumnIndex("SellingPrice"))));
                db.updateREG("50", String.valueOf(checkData.getInt(checkData.getColumnIndex("PurchasePackingList"))));
                db.updateREG("62", String.valueOf(checkData.getInt(checkData.getColumnIndex("StockInquiry"))));
                db.updateREG("69", String.valueOf(checkData.getInt(checkData.getColumnIndex("Cost"))));
                db.updateREG("70", String.valueOf(checkData.getInt(checkData.getColumnIndex("JobSheet"))));
                db.updateREG("71", String.valueOf(checkData.getInt(checkData.getColumnIndex("StockReceive"))));


                if (url != null) {
                    try {
                        // Check Connection
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        intent.putExtra("URLKey", url);
                        intent.putExtra("URLStr", binding.txtURL.getText().toString());
                        intent.putExtra("id", id);
                        startActivity(intent);
                        finish();
                        Toast.makeText(Login.this, "Login successfully", Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        Log.d("TryCatch", "Login: " + e);
                        Log.i("custDebug", e.getMessage());
                        Toast.makeText(context, "Unable to connect to server", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(context, "Invalid login credentials", Toast.LENGTH_SHORT).show();
                binding.txtpw.setText(null);
            }

            checkData.close();
        }

    }

    //Download Login Data
    class GetLoginList extends AsyncTask<String, Void, Integer> {
        Activity context;
        ProgressDialog pd;

        public GetLoginList(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setMessage("Download Users...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Integer doInBackground(String... connUrl) {
            int myCount = getLoginData(connUrl[0]);
            return myCount;
        }

        @Override
        protected void onPostExecute(Integer s) {
            super.onPostExecute(s);

            if (pd.isShowing())
                pd.dismiss();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm",
                    Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());

            //db.insertULDL("Users", s, currentDateandTime, "download");

            new SetDevice(Login.this).execute(url);

        }


    }

    private Integer getLoginData(String connUrl) {
        int myCount = 0;
        HttpURLConnection conn = null;
        BufferedReader reader;

        try {
            final java.net.URL url = new URL(connUrl + "/getUsers");
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestMethod("GET");
            conn.setReadTimeout(10000);//10 sec
            conn.setConnectTimeout(10000); //10 sec
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            String status = sb.toString();

            JSONArray users = new JSONArray(status);
            if(users.length() > 0 && users!=null) {
                db.deleteUsers();

                for (int i = 0; i < users.length(); i++) {
                    JSONObject object = users.getJSONObject(i);
                    String Username = object.getString("Username");
                    String Pwd = object.getString("Pwd");
                    Boolean EnableSetting = object.getBoolean("EnableSetting");
                    Boolean StockInquiry = object.getBoolean("StockInquiry");
                    Boolean FilterByAgent = object.getBoolean("FilterByAgent");
                    Boolean Sales = object.getBoolean("Sales");
                    Boolean Purchase = object.getBoolean("Purchase");
                    Boolean Transfer = object.getBoolean("Transfer");
                    Boolean PackingList = object.getBoolean("PackingList");
                    Boolean Catalog = object.getBoolean("Catalog");
                    Boolean Collection = object.getBoolean("ARPayment");
                    Boolean StockAssembly = object.getBoolean("StockAssembly");
                    Boolean Analytics = object.getBoolean("Analytics");
                    Log.wtf("Username", object.getString("Username"));
                    Log.wtf("Pwd", object.getString("Pwd"));
                    Boolean StockTake = object.getBoolean("StockTake");
                    Boolean SellingPrice = object.getBoolean("SellingPrice");
                    Boolean Cost = object.getBoolean("Cost");
                    Boolean PurchasePackingList = object.getBoolean("PurchasePackingList");
                    Boolean JobSheet = object.getBoolean("JobSheet");
                    Boolean StockReceive = object.getBoolean("StockReceive");

                    boolean insert = db.insertUsers(Username, Pwd, EnableSetting, FilterByAgent, Sales, Purchase, Transfer, PackingList, Catalog, Collection, StockAssembly, Analytics, StockTake, SellingPrice, PurchasePackingList, StockInquiry, Cost, JobSheet, StockReceive);

                    if (!insert) {
                        throw new Exception("Failed to download users");
                    }
                    myCount++;
                }
            }
            conn.disconnect();

        } catch (Exception ex) {
            Log.d("TryCatch", "get login Data: " + ex);
            ex.printStackTrace();
        }
        return myCount;
    }

}