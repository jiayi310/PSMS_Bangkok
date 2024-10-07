package com.example.androidmobilestock;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.androidmobilestock.databinding.ActivityAgingStatementDtlBinding;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class AgingStatementDtl extends AppCompatActivity {

    String docNo, docType;
    String url;
    ActivityAgingStatementDtlBinding binding;
    AC_Class.Statement so;
    ACDatabase db;
    String defaultCurr;
    ActionBar actionBar;
    File file;
    String toDate,debtorcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_aging_statement_dtl);

        //Action bar
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFFCD5C5C));

        db = new ACDatabase(this);
        so = new AC_Class.Statement();

        Cursor cur5 = db.getReg("2");
        if (cur5.moveToFirst()) {
            url = cur5.getString(0);
        }
        Cursor cur = db.getReg("6");
        if (cur.moveToFirst()) {
            defaultCurr = cur.getString(0);
            binding.setDefaultCurr(cur.getString(0));
        }

        docNo = getIntent().getStringExtra("docNo");
        docType = getIntent().getStringExtra("docType");

        new GetStatementDtl(AgingStatementDtl.this).execute(url);
    }

    class GetStatementDtl extends AsyncTask<String, Void, AC_Class.Statement> {
        Activity context;
        ProgressDialog pd;

        GetStatementDtl(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setMessage("Inquiry Invoice Details...");
            //pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected AC_Class.Statement doInBackground(String... params) {

            String urlString = params[0] + "/getUnpaidSalesDetail"; // URL to call
            String response = "";

            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                JSONObject root = new JSONObject();
                root.put("DocType", docType);
                root.put("DocNo", docNo);
                String str = root.toString();
                byte[] outputBytes = str.getBytes("UTF-8");
                OutputStream os = urlConnection.getOutputStream();
                os.write(outputBytes);

                int responseCode = urlConnection.getResponseCode();



                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            urlConnection.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }

                    JSONObject object = new JSONObject(response);

                    String DocNo = object.getString("DocNo");
                    String DocDate = object.getString("DocDate");
                    String DebtorCode = object.getString("DebtorCode");
                    String DebtorName = object.getString("DebtorName");
                    String DocType = object.getString("DocType");
                    Double Balance = object.getDouble("Balance");
                    Double Paid = object.getDouble("Paid");
                    Double Total = object.getDouble("Total");
                    String SalesAgent = object.getString("SalesAgent");
                    int SourceKey = object.getInt("SourceKey");
                    String SourceType = object.getString("SourceType");
                    String Terms = object.getString("Terms");

                    so = new AC_Class.Statement(DocType, DocDate, DocNo, DebtorCode, DebtorName, SalesAgent, Paid, Balance, Total, SourceKey, SourceType, Terms);


                } else {

                    response = "";
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            return so;
        }

        @Override
        protected void onPostExecute(AC_Class.Statement bool) {
            super.onPostExecute(bool);

            if (pd.isShowing())
                pd.dismiss();

            binding.setStateDtl(so);
            actionBar.setTitle(so.getDocNo()+" Statement");
            //recyclerView.smoothScrollToPosition(0);
            //adapter.notifyDataSetChanged();


        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
            case R.id.download: {
                new DownloadStatementDtl(AgingStatementDtl.this).execute(url);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart_menu_history, menu);
        return true;
    }

    class DownloadStatementDtl extends AsyncTask<String, Void, Void> {
        Activity context;
        ProgressDialog pd;

        DownloadStatementDtl(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setMessage("Download Invoice...");
            String extstoragedir = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extstoragedir, "PSMSExport");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            toDate = so.getDocDate();
            toDate = toDate.replace("/","");
            debtorcode = so.getDebtorCode();

            debtorcode = debtorcode.replaceAll("[;\\/:*?\"<>|&']","");
            file = new File(folder, "Invoice_" + debtorcode + toDate + ".pdf");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            String urlString = params[0] + "/getUnpaidSalesDetailPDF"; // URL to call

            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");

                JSONObject root = new JSONObject();
                root.put("DocNo", so.getDocNo());
                root.put("DocKey", so.getSourceKey());
                root.put("DocType", so.getSourceType());
                String str = root.toString();
                byte[] outputBytes = str.getBytes("UTF-8");
                OutputStream os = urlConnection.getOutputStream();
                os.write(outputBytes);

                int responseCode = urlConnection.getResponseCode();


                FileOutputStream fos = new FileOutputStream(file);//Get OutputStream for NewFile Location

                InputStream is = urlConnection.getInputStream();//Get InputStream for connection

                byte[] buffer = new byte[1024];//Set buffer type
                int len1 = 0;//init length
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);//Write new file
                }

                //Close all connection after doing task
                fos.close();
                is.close();

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {

            if (pd.isShowing())
                pd.dismiss();
            try {
                if (file != null) {
                    ContextThemeWrapper ctw = new ContextThemeWrapper(context, R.style.AppTheme);
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw);
                    alertDialogBuilder.setTitle("Document  ");
                    alertDialogBuilder.setMessage("Document Downloaded Successfully ");
                    alertDialogBuilder.setCancelable(false);
                    alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

                    alertDialogBuilder.setNegativeButton("Open report", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            debtorcode = debtorcode.replaceAll("[;\\/:*?\"<>|&']","");


//                            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/PSMSExport/" + "Invoice_" + debtorcode + toDate + ".pdf");


                            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "PSMSExport/" + "Invoice_" + debtorcode + toDate + ".pdf");

                            // -> filename = maven.pdf
                            if(file.length()> 0) {
                                Intent target = new Intent(Intent.ACTION_VIEW);
                                target.setDataAndType(FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file), "application/pdf");
                                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                Intent intent = Intent.createChooser(target, "Open File");
                                try {
                                    startActivity(intent);
                                } catch (ActivityNotFoundException e) {
                                    // Instruct the user to install a PDF reader here, or something
                                    Toast.makeText(context, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(AgingStatementDtl.this, "Empty File",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                    alertDialogBuilder.show();
//                    Toast.makeText(context, "Document Downloaded Successfully", Toast.LENGTH_SHORT).show();
                } else {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                        }
                    }, 3000);



                }
            } catch (Exception e) {
                e.printStackTrace();

                //Change button text if exception occurs

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    }
                }, 3000);


            }
            super.onPostExecute(v);


        }
    }

}