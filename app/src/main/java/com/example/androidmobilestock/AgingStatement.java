package com.example.androidmobilestock;



import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidmobilestock.databinding.ActivityAgingStatementBinding;

import org.json.JSONArray;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class AgingStatement extends AppCompatActivity {

    RecyclerView recyclerView;
    AgingStatementAdapter adapter;
    List<AC_Class.Statement> statement = new ArrayList<>();
    ACDatabase db;
    private DatePickerDialog.OnDateSetListener mDataSetListener;
    String url;
    public MyClickHandler handler;
    ActivityAgingStatementBinding binding;
    String defaultCurr;
    String debtorcode;
    File file;
    private AgingStatementAdapter.RecyclerViewClickListener listener;
    String def_Debtor;
    String toDate;

    /*------For storage permission------*/
    private static final int STORAGE_REQUEST_CODE_IMPORT = 2;
    private String[] storagePermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_aging_statement);
        db = new ACDatabase(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Unpaid Bill");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFFCD5C5C));
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (checkStoragePermission()) {
        } else {
            requestStoragePermissionImport();
        }
        handler = new MyClickHandler(this);
        binding.setHandler(handler);

        Cursor cur5 = db.getReg("2");
        if (cur5.moveToFirst()) {
            url = cur5.getString(0);
        }

        Cursor cur = db.getReg("6");
        if (cur.moveToFirst()) {
            defaultCurr = cur.getString(0);
            binding.setDefaultCurr(cur.getString(0));
        }
        Cursor debtor2 = db.getReg("17");
        if(debtor2.moveToFirst()){
            def_Debtor = debtor2.getString(0);
            AC_Class.Invoice myInvoice = db.getDebtorInfo(def_Debtor);

            if(!def_Debtor.equals("None") && myInvoice!=null) {
                binding.debtor.setText(def_Debtor + " " + myInvoice.getDebtorName());
                debtorcode = def_Debtor;
            }else{
                debtorcode = null;
            }
        }

        setDate();

        binding.debtor.clearFocus();
        recyclerView = findViewById(R.id.recycler_view_statement);
        setRecyclerView();

        binding.inquiryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(binding.debtor.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Please fill in Debtor Code", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!TextUtils.isEmpty(binding.debtor.getText().toString())) {
                    binding.debtor.setError(null);
                    binding.debtor.clearFocus();
                }
                if (TextUtils.isEmpty(binding.fromdate.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Please fill in From Date", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(binding.todate.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Please fill in To Date", Toast.LENGTH_SHORT).show();
                    return;
                }

                statement.clear();
                new GetStatement(AgingStatement.this).execute(url);

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart_menu_history, menu);
        return true;
    }

    private void setRecyclerView() {
        setOnClickListener();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AgingStatementAdapter(this, statement, listener);
        recyclerView.setAdapter(adapter);
    }

    private List<AC_Class.Statement> getList() {
        return statement;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
            case R.id.download: {
                if (TextUtils.isEmpty(binding.debtor.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Please fill in Debtor Code", Toast.LENGTH_SHORT).show();
                    return false;
                } else if (!TextUtils.isEmpty(binding.debtor.getText().toString())) {
                    binding.debtor.setError(null);
                    binding.debtor.clearFocus();
                }
                if (TextUtils.isEmpty(binding.fromdate.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Please fill in From Date", Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (TextUtils.isEmpty(binding.todate.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Please fill in To Date", Toast.LENGTH_SHORT).show();
                    return false;
                }
                new DownloadStatement(AgingStatement.this).execute(url);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    class GetStatement extends AsyncTask<String, Void, ArrayList> {
        Activity context;
        ProgressDialog pd;

        GetStatement(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setMessage("Inquiry Unpaid Bill List...");
            //pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected ArrayList doInBackground(String... params) {

            String urlString = params[0] + "/getUnpaidSales"; // URL to call
            OutputStream out = null;
            String response = "";

            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                JSONObject root = new JSONObject();
                root.put("DateFrom", binding.fromdate.getText().toString());
                root.put("DateTo", binding.todate.getText().toString());
                root.put("DebtorCode", debtorcode);
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

                    JSONArray location = new JSONArray(response);

                    Double total = 0.00;
                    for (int i = 0; i < location.length(); i++) {
                        JSONObject object = location.getJSONObject(i);
                        String DocNo = object.getString("DocNo");
                        String DocDate = object.getString("DocDate");
                        String DebtorCode = object.getString("DebtorCode");
                        String DebtorName = object.getString("DebtorName");
                        String DocType = object.getString("DocType");
                        Double balance = object.getDouble("Balance");
                        total += balance;

                        AC_Class.Statement so = new AC_Class.Statement(DocType, DocDate, DocNo, DebtorCode, DebtorName, balance);
                        statement.add(so);
                    }

                    binding.totaldue.setText(String.format(Locale.getDefault(),
                            " %.2f", total));

                } else {

                    response = "";
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            return (ArrayList) statement;
        }

        @Override
        protected void onPostExecute(ArrayList bool) {
            super.onPostExecute(bool);

            if (pd.isShowing())
                pd.dismiss();

            recyclerView.smoothScrollToPosition(0);
            adapter.notifyDataSetChanged();

//            if (!bool)
//                Snackbar.make(this.context.findViewById(android.R.id.content), "Server connection failed.", Snackbar.LENGTH_SHORT).show();

            //arrayAdapter.notifyDataSetChanged();
        }
    }


    public class MyClickHandler {
        Context context;

        public MyClickHandler(Context context) {
            this.context = context;
        }

        public void onDebtorTxtViewClicked(View view) {
            Intent new_intent = new Intent(AgingStatement.this, DebtorList.class);
            startActivityForResult(new_intent, 2);
        }

        public void onFromDateViewClicked(View view) {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            mDataSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    month = month + 1;
                    String monthString = String.valueOf(month);
                    String dateString = String.valueOf(dayOfMonth);

                    if (monthString.length() == 1) {
                        monthString = "0" + monthString;
                    }
                    if (dateString.length() == 1) {
                        dateString = "0" + dateString;
                    }
                    String date = dateString + "/" + monthString + "/" + year;
                    binding.fromdate.setText(date);
                }
            };
            DatePickerDialog datePickerDialog = new DatePickerDialog(AgingStatement.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDataSetListener, year, month, day);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        }

        public void onToDateViewClicked(View view) {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            mDataSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    month = month + 1;
                    String monthString = String.valueOf(month);
                    String dateString = String.valueOf(dayOfMonth);

                    if (monthString.length() == 1) {
                        monthString = "0" + monthString;
                    }
                    if (dateString.length() == 1) {
                        dateString = "0" + dateString;
                    }
                    String date = dateString + "/" + monthString + "/" + year;
                    binding.todate.setText(date);
                }
            };
            DatePickerDialog datePickerDialog = new DatePickerDialog(AgingStatement.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDataSetListener, year, month, day);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        }
    }

    private void setDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));

        Date lastDayOfMonth = cal.getTime();
        DateFormat sdf = new SimpleDateFormat("dd");

        month = month + 1;
        String monthString = String.valueOf(month);

        if (monthString.length() == 1) {
            monthString = "0" + monthString;
        }
        String date = "01" + "/" + monthString + "/" + year;
        String date2 = sdf.format(lastDayOfMonth) + "/" + monthString + "/" + year;
        binding.fromdate.setText(date);
        binding.todate.setText(date2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            // Return from debtor list
            case 2:
                if (data != null) {
                    AC_Class.Debtor debtor = data.getParcelableExtra("DebtorsKey");
                    if (debtor != null) {
                        debtorcode = debtor.getAccNo();
                        binding.debtor.setText(debtor.getAccNo() + " " + debtor.getCompanyName());
                    }
                }
                binding.debtor.clearFocus();
                break;
        }
    }


    class DownloadStatement extends AsyncTask<String, Void, Void> {
        Activity context;
        ProgressDialog pd;

        DownloadStatement(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setMessage("Download Unpaid Bill...");

            String extstoragedir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            File folder = new File(extstoragedir, "PSMSExport");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            toDate = binding.todate.getText().toString();
            toDate = toDate.replace("/","");

            debtorcode = debtorcode.replaceAll("[;\\/:*?\"<>|&']","");
            file = new File(folder, debtorcode + toDate + ".pdf");
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

            String urlString = params[0] + "/getUnpaidSalesPDF"; // URL to call

            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");

                JSONObject root = new JSONObject();
                root.put("DateFrom", binding.fromdate.getText().toString());
                root.put("DateTo", binding.todate.getText().toString());
                root.put("DebtorCode", debtorcode);
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
                            //File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/PSMSExport/" + debtorcode + toDate + ".pdf");  // -> filename = maven.pdf
                            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "PSMSExport/" + debtorcode + toDate + ".pdf");
                            if(file.length()> 0) {
                                // Get the URI for the file using FileProvider
                                Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);

                                // Create an intent to view the PDF file
                                Intent target = new Intent(Intent.ACTION_VIEW);
                                target.setDataAndType(uri, "application/pdf");
                                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                Intent intent = Intent.createChooser(target, "Open File");
                                try {
                                    startActivity(intent);
                                } catch (ActivityNotFoundException e) {
                                    // Instruct the user to install a PDF reader here, or something
                                    Toast.makeText(context, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(AgingStatement.this, "Empty File",
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

    private void setOnClickListener() {
        listener = new AgingStatementAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                String docNo = statement.get(position).getDocNo();
                String docType = statement.get(position).getDocType();
                Intent in = new Intent(AgingStatement.this, AgingStatementDtl.class);
                in.putExtra("docNo", docNo);
                in.putExtra("docType", docType);
                startActivityForResult(in, 1);
            }

        };
    }

    private void requestStoragePermissionImport() {
        //request storage permission for import
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE_IMPORT);
    }

    private boolean checkStoragePermission() {
        //check if storage permission is enabled or not and return true/false
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

}