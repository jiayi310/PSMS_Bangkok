package com.example.androidmobilestock_bangkok.Settings;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.androidmobilestock_bangkok.ACDatabase;
import com.example.androidmobilestock_bangkok.AC_Class;
import com.example.androidmobilestock_bangkok.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class UploadDownload extends AppCompatActivity {
    String mode;
    String url;
    String uniqueID;
    boolean isActivated = false;
    boolean connected;
    //int count;
    ACDatabase db;
    ListView uldl_listView;
    ULDLListViewAdapter arrayAdapter;
    List<String> selectedList;
    boolean history = false;
    Boolean OnlyTallyUploaded = true;
    Boolean OnlyTallyUploadedPR = true;
    String TerminalDevice = "";
    String DocPrefix;
    private long mLastClickTime = 0;
    String doctype = null;
    //ProgressDialog progressDialog;

    //InputStream inputStream;
    JSONObject jsonObject;
    int dwnTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_download);
        final Intent intent = getIntent();
        mode = intent.getStringExtra("mode");
        arrayAdapter = new ULDLListViewAdapter(this, new AC_Class.UploadDownload[0]);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        switch (mode) {
            case "upload":
                actionBar.setTitle("Upload");
                break;
            case "download":
                actionBar.setTitle("Download");
                break;
        }



        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        dwnTime = sharedPref.getInt("DownloadTime", 0);

        uldl_listView = (ListView)findViewById(R.id.uldl_ListView);
        db = new ACDatabase(this);

        Cursor Hybrid_History = db.getReg("32");
        if (Hybrid_History.moveToFirst()){
            history = Boolean.valueOf(Hybrid_History.getString(0));
        }

        Cursor cursor = db.getReg("43");
        if(cursor.moveToFirst()){
            OnlyTallyUploaded = Boolean.valueOf(cursor.getString(0));
        }

        Cursor cursor2 = db.getReg("55");
        if(cursor2.moveToFirst()){
            TerminalDevice = cursor2.getString(0);
        }

        Cursor cursor3 = db.getReg("66");
        if(cursor3.moveToFirst()){
            OnlyTallyUploadedPR = Boolean.valueOf(cursor3.getString(0));
        }


        addEmpty();
        getTable();

        new CheckLicense(UploadDownload.this).execute(url);

        selectedList = new ArrayList<>();
        uldl_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AC_Class.UploadDownload currItem = (AC_Class.UploadDownload) parent.getItemAtPosition(position);
                int index = selectedList.indexOf(currItem.getTableName());
                if (index != -1) {
                    selectedList.remove(index);
                    view.setBackgroundColor(Color.parseColor("#f0f8ff"));
                } else {
                    selectedList.add(currItem.getTableName());
                    view.setBackgroundColor(Color.parseColor("#ffe4e1"));
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inv_menu, menu);
        switch (mode) {
            case "upload":
                menu.findItem(R.id.addStockCount).setIcon(R.drawable.file_upload);
                break;

            case "download":
                menu.findItem(R.id.addStockCount).setIcon(R.drawable.file_download);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        if (id == R.id.addStockCount)
        {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 5000){
                return false;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            getData();
            //selectedList = new ArrayList<>();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Add empty items to table
    public void addEmpty() {
        switch(mode) {
            case "download":
                //Downloads
                db.insertULDL("Users", mode);
                db.insertULDL("Debtor", mode);
                db.insertULDL("Creditor", mode);
                db.insertULDL("Sales Agents", mode);
                db.insertULDL("Purchase Agents", mode);
                db.insertULDL("Items", mode);
                db.insertULDL("UTDCost", mode);
                db.insertULDL("Location", mode);
                db.insertULDL("Price Histories", mode);
                db.insertULDL("Tax Types", mode);
                db.insertULDL("Stock Balance", mode);
                db.insertULDL("Item Images", mode);
                db.insertULDL("Item Batch", mode);
                db.insertULDL("Sales Status", mode);
                db.insertULDL("Item BOM", mode);
                db.insertULDL("Profile", mode);
//                db.insertULDL("Sales Order", mode);
                break;

            case "upload":
                //Uploads
                //db.insertULDL("Stock Count", mode);
//                db.insertULDL("Payment", mode);
                break;
        }
    }

    public void getTable() {
        Cursor get = db.getReg("4");
            if (get != null) {
                Cursor url1 = db.getReg("2");
                if(url1.moveToFirst()){
                    url = url1.getString(0);
                }
                Cursor uniId = db.getReg("8");
                if(uniId.moveToFirst()){
                    uniqueID = uniId.getString(0);
                }
        }

        Cursor data = db.getULDL(mode);
        List<AC_Class.UploadDownload> uldlList = new ArrayList<>();
        while (data.moveToNext()) {
            AC_Class.UploadDownload temp = new AC_Class.UploadDownload(data.getString(0),
                    data.getInt(1), data.getString(2), data.getString(3));
            Log.wtf("Column 1",data.getString(0));
            Log.wtf("Column 2",data.getString(1));
            Log.wtf("Column 3",data.getString(2));
            Log.wtf("Column 4",data.getString(3));
            uldlList.add(temp);
        }
        arrayAdapter = new ULDLListViewAdapter(this,
                (AC_Class.UploadDownload[]) uldlList.toArray(new AC_Class.UploadDownload[0]));
        uldl_listView.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
    }

    public void getData() {
        new CheckLicense(UploadDownload.this).execute(url);

        Cursor get = db.getReg("4");
        if (get != null) {
            Cursor url1 = db.getReg("2");
            if (url1.moveToFirst()) {
                url = url1.getString(0);
            }
            Cursor uniId = db.getReg("8");
            if (uniId.moveToFirst()) {
                uniqueID = uniId.getString(0);
            }
        }

        uploadDownload();
        Cursor data = db.getULDL(mode);

        List<AC_Class.UploadDownload> uldlList = new ArrayList<>();
        while (data.moveToNext()) {
            AC_Class.UploadDownload temp = new AC_Class.UploadDownload(data.getString(0),
                    data.getInt(1), data.getString(2), data.getString(3));
            uldlList.add(temp);
        }
        arrayAdapter = new ULDLListViewAdapter(this,
                (AC_Class.UploadDownload[]) uldlList.toArray(new AC_Class.UploadDownload[0]));
        uldl_listView.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
    }

    private void uploadDownload() {
        if (isActivated) {

            new CheckVersion(UploadDownload.this).execute(url);

        }
        else {
            Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
        }
    }

    //Check Connection status
    class CheckConnection extends AsyncTask<String, Void, Boolean> {
        Activity context;

        public CheckConnection(Activity context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(String... connUrl) {
            HttpURLConnection conn;
            boolean status;
            try {
                final java.net.URL url = new URL(connUrl[0]+"/CheckConnection");
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(2000);
                conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
                conn.setRequestMethod("GET");
                InputStream inputStream = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                reader.close();
                status = Boolean.parseBoolean(sb.toString().trim());
            } catch (Exception ex) {
                Log.i("custDebug", ex.getMessage());
                status = false;
            }
            return status;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            if (bool) {
                Toast.makeText(UploadDownload.this, "Connection successful.",
                        Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(UploadDownload.this, "Connection failed.",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    //Check Connection status
    class CheckVersion extends AsyncTask<String, Void, String> {
        Activity context;

        public CheckVersion(Activity context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... connUrl) {
            HttpURLConnection conn;
            String status = null;
            try {
                final java.net.URL url = new URL(connUrl[0]+"/getVersion");
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(2000);
                conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
                conn.setRequestMethod("GET");
                InputStream inputStream = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                reader.close();
                status = sb.toString().trim();
            } catch (Exception ex) {
                Log.i("custDebug", ex.getMessage());
            }
            return status;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            String PSVersion = null;
            Cursor cursor = db.getReg("1");
            if (cursor.moveToFirst()) {
                PSVersion = cursor.getString(0);
            }

//            String[] yourArray = PSVersion.split("\\.");
//            PSVersion = yourArray[0] + "." + yourArray[1];
            String SyncVersion = s.replace("\"", "");


            Log.d("Check Version", "psversion: " + PSVersion);
            Log.d("Check Version", "syncVersion: " + SyncVersion);

            if (PSVersion.equals(SyncVersion)) {

                switch (mode) {
                    case "download":

                        if (selectedList.size() == 0) {
//                    count = db.getULDL(mode).getCount() - 1;
//                new CheckConnection(UploadDownload.this).execute(url);

                            new GetLoginList(UploadDownload.this).execute(url);
                            new GetDebtorList(UploadDownload.this).execute(url);
                            new GetCreditorList(UploadDownload.this).execute(url);
                            new GetSalesAgentList(UploadDownload.this).execute(url);
                            new GetPurchaseAgentList(UploadDownload.this).execute(url);
                            new GetItemList(UploadDownload.this).execute(url);
                            new GetUTDCost(UploadDownload.this).execute(url);
                            new GetLocationList(UploadDownload.this).execute(url);
                            if (!history)
                                new GetHistoryPriceList(UploadDownload.this).execute(url);
                            new GetTaxTypeList(UploadDownload.this).execute(url);
                            new GetItemBalanceList(UploadDownload.this).execute(url);
                            new GetAllItemBatch(UploadDownload.this).execute(url);
                            new GetSalesStatus(UploadDownload.this).execute(url);
                            new GetAllItemBOM(UploadDownload.this).execute(url);
                            new GetCreditTerm(UploadDownload.this).execute(url);


                            if(dwnTime == 0){

                                new GetItemImageList(UploadDownload.this).execute(url);
                                new GetMobileRN(UploadDownload.this).execute(url);
                            }


                        } else {
//                    count = selectedList.size();
                            for (int i = 0; i < selectedList.size(); i++) {
                                switch (selectedList.get(i)) {

                                    case "Users":
                                        new GetLoginList(UploadDownload.this).execute(url);
                                        break;
                                    case "Debtor":
                                        new GetDebtorList(UploadDownload.this).execute(url);
                                        break;
                                    case "Creditor":
                                        new GetCreditorList(UploadDownload.this).execute(url);
                                        break;
                                    case "Sales Agents":
                                        new GetSalesAgentList(UploadDownload.this).execute(url);
                                        break;
                                    case "Purchase Agents":
                                        new GetPurchaseAgentList(UploadDownload.this).execute(url);
                                        break;
                                    case "Items":
                                        //count += 1;
                                        new GetItemList(UploadDownload.this).execute(url);
                                        break;
                                    case "UTDCost":
                                        //count += 1;
                                        new GetUTDCost(UploadDownload.this).execute(url);
                                        break;
                                    case "Location":
                                        new GetLocationList(UploadDownload.this).execute(url);
                                        break;
                                    case "Price Histories":
                                        new GetHistoryPriceList(UploadDownload.this).execute(url);
                                        break;
                                    case "Tax Types":
                                        new GetTaxTypeList(UploadDownload.this).execute(url);
                                        break;
                                    case "Stock Balance":
                                        new GetItemBalanceList(UploadDownload.this).execute(url);
                                        break;
                                    case "Item Images":
                                        new GetItemImageList(UploadDownload.this).execute(url);
                                        break;
                                    case "Item Batch":
                                        new GetAllItemBatch(UploadDownload.this).execute(url);
                                        break;
                                    case "Sales Status":
                                        new GetSalesStatus(UploadDownload.this).execute(url);
                                        break;
                                    case "Item BOM":
                                        new GetAllItemBOM(UploadDownload.this).execute(url);
                                        break;
                                    case "Profile":
                                        //new GetProfile(UploadDownload.this).execute(url);
                                        new GetMobileRN(UploadDownload.this).execute(url);
                                        break;
                                    case "Credit Term":
                                        new GetCreditTerm(UploadDownload.this).execute(url);
                                        break;
//                                case "Sales Order":
//                                    new GetSalesOrderList(UploadDownload.this).execute(url);
//                                    break;
                                }
                            }
                        }
                        break;

                    case "upload":
                        if (selectedList.size() == 0) {
//                    count = db.getULDL(mode).getCount();
                            new CheckStockTakeData(UploadDownload.this).execute(url);
                            new CheckInvoiceData(UploadDownload.this).execute(url);
                            new CheckPaymentData(UploadDownload.this).execute(url);
                            new CheckPurchaseData(UploadDownload.this).execute(url);
                            new CheckStockTransferData(UploadDownload.this).execute(url);
                            new CheckPackingListData(UploadDownload.this).execute(url);
                            new CheckARPaymentData(UploadDownload.this).execute(url);
                            new CheckStockAssemblyData(UploadDownload.this).execute(url);
                            new CheckPurchasePLData(UploadDownload.this).execute(url);
                            new CheckJobSheetData(UploadDownload.this).execute(url);
                            new CheckStockReceiveData(UploadDownload.this).execute(url);
                        } else {
//                    count = selectedList.size();
                            for (int i = 0; i < selectedList.size(); i++) {
                                switch (selectedList.get(i)) {
                                    case "Stock Take":
                                        new CheckStockTakeData(UploadDownload.this).execute(url);
                                        break;
                                    case "Invoice": {
                                        new CheckInvoiceData(UploadDownload.this).execute(url);
                                        new CheckPaymentData(UploadDownload.this).execute(url);
                                    }
                                    case "Purchase":
                                        new CheckPurchaseData(UploadDownload.this).execute(url);
                                    case "Transfer":
                                        new CheckStockTransferData(UploadDownload.this).execute(url);
                                        break;
                                    case "Packing List":
                                        new CheckPackingListData(UploadDownload.this).execute(url);
                                        break;
                                    case "Collection":
                                        new CheckARPaymentData(UploadDownload.this).execute(url);
                                        break;
                                    case "Stock Assembly":
                                        new CheckStockAssemblyData(UploadDownload.this).execute(url);
                                        break;
                                    case "Purchase Receiving":
                                        new CheckPurchasePLData(UploadDownload.this).execute(url);
                                        break;
                                    case "Job Sheet":
                                        new CheckJobSheetData(UploadDownload.this).execute(url);
                                        break;
                                    case "Stock Receive":
                                        new CheckStockReceiveData(UploadDownload.this).execute(url);
                                        break;


                                }
                            }
                        }
                        break;
                }
            } else {
                new AlertDialog.Builder(context)
                        .setTitle("Alert")
                        .setMessage("The current PSMS version is " + PSVersion + " which is dismatch with the sync version " + s + ". Please contact administrator.")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                //new GetItemImageList(UploadDownload.this).execute(url);
                //db.printItemTables();
            }

            selectedList = new ArrayList<>();
        }
    }

    class CheckLicense extends AsyncTask<String, Boolean, Boolean> {
        Activity context;

        public CheckLicense(Activity context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(String... connUrl) {
            HttpURLConnection conn;
            boolean status;
            try {
                final java.net.URL url = new URL(connUrl[0]+ "/getDevice/" + uniqueID);
                conn = (HttpURLConnection) url.openConnection();
                conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
                conn.setRequestMethod("GET");
                InputStream inputStream = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                return Boolean.parseBoolean(reader.readLine());
            } catch (Exception ex) {
                Log.i("custDebug", ex.getMessage());
                status = false;
            }
            return status;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
            if (s) {
                isActivated = true;
            }
        }

    }

    //DOWNLOAD DATA

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

            // Datetime
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm",
                    Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());

            db.insertULDL("Users", s, currentDateandTime, "download");
            getTable();
        }
    }

    private Integer getLoginData(String connUrl)
    {
        int myCount = 0;
        HttpURLConnection conn = null;
        BufferedReader reader;
        db.deleteUsers();

        try {
            final java.net.URL url = new URL(connUrl + "/getUsers");
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestMethod("GET");
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
            //db.deleteUsers();
            for (int i = 0; i < users.length(); i++) {
                JSONObject object = users.getJSONObject(i);
                String Username = object.getString("Username");
                String Pwd = object.getString("Pwd");
                Boolean EnableSetting = object.getBoolean("EnableSetting");
                Boolean FilterByAgent = object.getBoolean("FilterByAgent");
                Boolean Sales = object.getBoolean("Sales");
                Boolean Purchase = object.getBoolean("Purchase");
                Boolean Transfer = object.getBoolean("Transfer");
                Boolean PackingList = object.getBoolean("PackingList");
                Boolean Catalog = object.getBoolean("Catalog");
                Boolean Collection = object.getBoolean("ARPayment");
                Boolean StockAssembly = object.getBoolean("StockAssembly");
                Boolean Analytics = object.getBoolean("Analytics");
                Boolean StockTake = object.getBoolean("StockTake");
                Boolean SellingPrice = object.getBoolean("SellingPrice");
                Boolean PurchasePackingList = object.getBoolean("PurchasePackingList");
                Boolean StockInquiry = object.getBoolean("StockInquiry");
                Boolean Cost = object.getBoolean("Cost");
                Boolean JobSheet = object.getBoolean("JobSheet");
                Boolean StockReceive = object.getBoolean("StockReceive");
                Boolean CustomA = object.getBoolean("CustomA");
                Boolean CustomB = object.getBoolean("CustomB");


                boolean insert = db.insertUsers(Username, Pwd, EnableSetting, FilterByAgent, Sales, Purchase, Transfer, PackingList, Catalog, Collection, StockAssembly, Analytics, StockTake, SellingPrice, PurchasePackingList, StockInquiry, Cost, JobSheet, StockReceive, CustomA, CustomB);
                if (!insert) {
                    throw new Exception("Failed to download users");
                }
                myCount++;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return myCount;
    }


    //Download Debtor Data
    class GetDebtorList extends AsyncTask<String, Void, Integer> {
        Activity context;
        ProgressDialog pd;

        GetDebtorList(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setMessage("Download Debtors...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Integer doInBackground(String... connUrl) {
            int myCount = getDebtorData(connUrl[0],0,0);
            return myCount;
        }

        @Override
        protected void onPostExecute(Integer s) {
            super.onPostExecute(s);

            if (pd.isShowing())
                pd.dismiss();

            // Datetime
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm",
                    Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());

            db.insertULDL("Debtor", s, currentDateandTime, "download");
            getTable();
        }
    }

    private Integer getDebtorData(String connUrl, int currIndex, int myCount)
    {
        HttpURLConnection conn;
        BufferedReader reader;

        try {
            java.net.URL url = new URL(connUrl + "/getAllDebtor");
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            // Send index to indicate no. of received chunks
            byte[] sendb = Integer.toString(currIndex).getBytes("UTF-8");
            conn.getOutputStream().write(sendb);
            conn.getOutputStream().flush();
            conn.getOutputStream().close();

            // Receive chunk of data
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            String status = sb.toString();

            if (status != null)
            {
                JSONArray debtor = new JSONArray(status);

                if (debtor.length() > 0) {
                    if (currIndex == 0) db.deleteDebtor();

                    boolean insert = true;
                    for (int i = 0; i < debtor.length(); i++) {
                        JSONObject object = debtor.getJSONObject(i);

                        String AccNo = null;
                        String CompanyName = null;
                        String Desc2 = null;
                        String Address1 = null;
                        String Address2 = null;
                        String Address3 = null;
                        String Address4 = null;
                        String SalesAgent = null;
                        String TaxType = null;
                        String Phone = null;
                        String Fax = null;
                        String Attention = null;
                        String EmailAddress = null;
                        String DebtorType = null;
                        String AreaCode = null;
                        String CurrencyCode = null;
                        String DisplayTerm = null;
                        String Phone2 = null;
                        String PriceCategory = null;
                        String DetailDiscount = null;

                        AccNo = object.getString("AccNo");
                        if (!object.isNull("CompanyName"))
                            CompanyName = object.getString("CompanyName");
                        if (!object.isNull("Name2"))
                            Desc2 = object.getString("Name2");
                        if (!object.isNull("Add1"))
                            Address1 = object.getString("Add1");
                        if (!object.isNull("Add2"))
                            Address2 = object.getString("Add2");
                        if (!object.isNull("Add3"))
                            Address3 = object.getString("Add3");
                        if (!object.isNull("Add4"))
                            Address4 = object.getString("Add4");
                        if (!object.isNull("SalesAgent"))
                            SalesAgent = object.getString("SalesAgent");
                        if (!object.isNull("TaxType"))
                            TaxType = object.getString("TaxType");
                        if (!object.isNull("Phone"))
                            Phone = object.getString("Phone");
                        if (!object.isNull("Fax"))
                            Fax = object.getString("Fax");
                        if (!object.isNull("Attention"))
                            Attention = object.getString("Attention");
                        if (!object.isNull("EmailAddress"))
                            EmailAddress = object.getString("EmailAddress");
                        if (!object.isNull("DebtorType"))
                            DebtorType = object.getString("DebtorType");
                        if (!object.isNull("AreaCode"))
                            AreaCode = object.getString("AreaCode");
                        if (!object.isNull("CurrencyCode"))
                            CurrencyCode = object.getString("CurrencyCode");
                        if (!object.isNull("DisplayTerm"))
                            DisplayTerm = object.getString("DisplayTerm");
                        if (!object.isNull("Phone2"))
                            Phone2 = object.getString("Phone2");
                        if (!object.isNull("PriceCategory"))
                            PriceCategory = object.getString("PriceCategory");
                        if (!object.isNull("DetailDiscount"))
                            DetailDiscount = object.getString("DetailDiscount");


                        insert = db.insertDebtor(AccNo, CompanyName, Desc2, Address1,
                                Address2, Address3, Address4, SalesAgent, TaxType, Phone, Fax, Attention, EmailAddress, DebtorType, AreaCode, CurrencyCode, DisplayTerm, Phone2, PriceCategory, DetailDiscount) && insert;
                        object = null;
                        myCount++;
                    }
                    if (insert) currIndex++;
                    debtor = null;
                    myCount = getDebtorData(connUrl, currIndex, myCount);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i("custDebug", ex.getMessage());
        }
        return myCount;
    }


    class GetCreditTerm extends AsyncTask<String, Void, Integer> {
        Activity context;
        ProgressDialog pd;

        GetCreditTerm(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setMessage("Download Credit Terms...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Integer doInBackground(String... connUrl) {
            int myCount = getCreditTermData(connUrl[0],0,0);
            return myCount;
        }

        @Override
        protected void onPostExecute(Integer s) {
            super.onPostExecute(s);

            if (pd.isShowing())
                pd.dismiss();

            // Datetime
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm",
                    Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());

            db.insertULDL("CreditTerm", s, currentDateandTime, "download");
            getTable();
        }
    }

    private Integer getCreditTermData(String connUrl, int currIndex, int myCount)
    {
        HttpURLConnection conn;
        BufferedReader reader;

        try {
            java.net.URL url = new URL(connUrl + "/getAllCreditTerm");
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            // Send index to indicate no. of received chunks
            byte[] sendb = Integer.toString(currIndex).getBytes("UTF-8");
            conn.getOutputStream().write(sendb);
            conn.getOutputStream().flush();
            conn.getOutputStream().close();

            // Receive chunk of data
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            String status = sb.toString();

            if (status != null)
            {
                JSONArray creditTerms = new JSONArray(status);

                if (creditTerms.length() > 0) {
                    if (currIndex == 0) db.deleteCreditTerm();

                    boolean insert = true;
                    for (int i = 0; i < creditTerms.length(); i++) {
                        JSONObject object = creditTerms.getJSONObject(i);

                        String DisplayTerm = null;
                        String Terms = null;
                        String TermType = null;
                        String TermDays = null;
                        String DiscountDays = null;
                        String DiscountPercent = null;


                        if (!object.isNull("DisplayTerm"))
                            DisplayTerm = object.getString("DisplayTerm");
                        if (!object.isNull("Terms"))
                            Terms = object.getString("Terms");
                        if (!object.isNull("TermType"))
                            TermType = object.getString("TermType");
                        if (!object.isNull("TermDays"))
                            TermDays = object.getString("TermDays");
                        if (!object.isNull("DiscountDays"))
                            DiscountDays = object.getString("DiscountDays");
                        if (!object.isNull("DiscountPercent"))
                            DiscountPercent = object.getString("DiscountPercent");


                        insert = db.insertCreditTerms(DisplayTerm, Terms, TermType, TermDays,
                                DiscountDays, DiscountPercent) && insert;
                        object = null;
                        myCount++;
                    }
                    if (insert) currIndex++;
                    creditTerms = null;
                    myCount = getDebtorData(connUrl, currIndex, myCount);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i("custDebug", ex.getMessage());
        }
        return myCount;
    }

    //Download Creditor Data
    class GetCreditorList extends AsyncTask<String, Void, Integer> {
        Activity context;
        ProgressDialog pd;

        GetCreditorList(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setMessage("Download Creditors...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Integer doInBackground(String... connUrl) {
            int myCount = getCreditorData(connUrl[0],0,0);
            return myCount;
        }

        @Override
        protected void onPostExecute(Integer s) {
            super.onPostExecute(s);

            if (pd.isShowing())
                pd.dismiss();

            // Datetime
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm",
                    Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());

            db.insertULDL("Creditor", s, currentDateandTime, "download");
            getTable();
        }
    }

    private Integer getCreditorData(String connUrl, int currIndex, int myCount)
    {
        HttpURLConnection conn;
        BufferedReader reader;

        try {
            java.net.URL url = new URL(connUrl + "/getAllCreditor");
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            // Send index to indicate no. of received chunks
            byte[] sendb = Integer.toString(currIndex).getBytes("UTF-8");
            conn.getOutputStream().write(sendb);
            conn.getOutputStream().flush();
            conn.getOutputStream().close();

            // Receive chunk of data
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            String status = sb.toString();

            if (status != null)
            {
                JSONArray creditor = new JSONArray(status);

                if (creditor.length() > 0) {
                    if (currIndex == 0) db.deleteCreditor();

                    boolean insert = true;
                    for (int i = 0; i < creditor.length(); i++) {
                        JSONObject object = creditor.getJSONObject(i);

                        String AccNo = null;
                        String CompanyName = null;
                        String Desc2 = null;
                        String Address1 = null;
                        String Address2 = null;
                        String Address3 = null;
                        String Address4 = null;
                        String PurchaseAgent = null;
                        String TaxType = null;
                        String Phone = null;
                        String Fax = null;
                        String Attention = null;
                        String EmailAddress = null;
                        String CreditorType = null;
                        String AreaCode = null;
                        String CurrencyCode = null;
                        String DisplayTerm = null;
                        String Phone2 = null;

                        AccNo = object.getString("AccNo");
                        if (!object.isNull("CompanyName"))
                            CompanyName = object.getString("CompanyName");
                        if (!object.isNull("Name2"))
                            Desc2 = object.getString("Name2");
                        if (!object.isNull("Add1"))
                            Address1 = object.getString("Add1");
                        if (!object.isNull("Add2"))
                            Address2 = object.getString("Add2");
                        if (!object.isNull("Add3"))
                            Address3 = object.getString("Add3");
                        if (!object.isNull("Add4"))
                            Address4 = object.getString("Add4");
                        if (!object.isNull("SalesAgent"))
                            PurchaseAgent = object.getString("SalesAgent");
                        if (!object.isNull("TaxType"))
                            TaxType = object.getString("TaxType");
                        if (!object.isNull("Phone"))
                            Phone = object.getString("Phone");
                        if (!object.isNull("Fax"))
                            Fax = object.getString("Fax");
                        if (!object.isNull("Attention"))
                            Attention = object.getString("Attention");
                        if (!object.isNull("EmailAddress"))
                            EmailAddress = object.getString("EmailAddress");
                        if (!object.isNull("DebtorType"))
                            CreditorType = object.getString("DebtorType");
                        if (!object.isNull("AreaCode"))
                            AreaCode = object.getString("AreaCode");
                        if (!object.isNull("CurrencyCode"))
                            CurrencyCode = object.getString("CurrencyCode");
                        if (!object.isNull("DisplayTerm"))
                            DisplayTerm = object.getString("DisplayTerm");
                        if (!object.isNull("Phone2"))
                            Phone2 = object.getString("Phone2");

                        insert = db.insertCreditor(AccNo, CompanyName, Desc2, Address1,
                                Address2, Address3, Address4, PurchaseAgent, TaxType, Phone, Fax, Attention, EmailAddress, CreditorType, AreaCode, CurrencyCode, DisplayTerm, Phone2) && insert;
                        object = null;
                        myCount++;
                    }
                    if (insert) currIndex++;
                    creditor = null;
                    myCount = getCreditorData(connUrl, currIndex, myCount);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i("custDebug", ex.getMessage());
        }
        return myCount;
    }

    //Download Item Data
    class GetItemList extends AsyncTask<String, Void, Boolean> {
        Activity context;
        ProgressDialog pd;

        GetItemList(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setMessage("Download Items...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Boolean doInBackground(String... connUrl) {
            getItemData(connUrl[0], 0);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
            if (s) {
                if (pd.isShowing()) pd.dismiss();
                new GetItemUOMList(UploadDownload.this).execute(url);
            }
        }
    }

    private void getItemData(String connUrl, int currIndex)
    {
        HttpURLConnection conn;
        BufferedReader reader;
        try {
            java.net.URL url = new URL(connUrl + "/getAllItems");
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            // Send index to indicate no. of received chunks
            byte[] sendb = Integer.toString(currIndex).getBytes("UTF-8");
            conn.getOutputStream().write(sendb);
            conn.getOutputStream().flush();
            conn.getOutputStream().close();

            // Receive chunk of data
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            String status = sb.toString();

            if (status != null) {
                try {
                    JSONArray items = new JSONArray(status);

                    if (items.length() > 0) {
                        if (currIndex == 0) db.deleteItem();

                        boolean insert = true;
                        for (int i = 0; i < items.length(); i++) {
                            JSONObject object1 = items.getJSONObject(i);
                            String ItemCode = object1.getString("ItemCode");
                            String Description = object1.getString("Description");
                            String Desc2 = object1.getString("Desc2"); //Follow Database Data Member Name
                            String ItemGroup = object1.getString("ItemGroup");
                            String ItemType = object1.getString("ItemType");
                            String TaxType = object1.getString("TaxType");
                            String PurchaseTaxType = object1.getString("PurchaseTaxType");
                            String BaseUOM = object1.getString("BaseUOM");
                            String SalesUOM = object1.getString("SalesUOM");
                            String PurchaseUOM = object1.getString("PurchaseUOM");

                            BigDecimal price = new BigDecimal(object1.getString("Price"));
                            BigDecimal price2 = new BigDecimal(object1.getString("Price2"));

                            String BarCode = object1.getString("BarCode");
                            String Image = object1.getString("Image");
                            String ItemCode2 = object1.getString("ItemCode2");
                            String hasBatchNo = object1.getString("HasBatchNo");
                            String isSalesItem = object1.getString("IsSalesItem");
                            String isPurchaseItem = object1.getString("IsPurchaseItem");
                            String isRawMaterialItem = object1.getString("IsRawMaterialItem");
                            String isFinishGoodsItem = object1.getString("IsFinishGoodsItem");

                            String UDF_UDF1 = object1.getString("UDF_UDF1");
                            String UDF_UDF2 = object1.getString("UDF_UDF2");
                            String UDF_UDF3 = object1.getString("UDF_UDF3");
                            String UDF_UDF4 = object1.getString("UDF_UDF4");
                            String UDF_UDF5 = object1.getString("UDF_UDF5");


                            insert = db.insertItem(ItemCode, Description, Desc2, ItemGroup,
                                    ItemType, TaxType, PurchaseTaxType, BaseUOM, price, price2,
                                    BarCode, Image, ItemCode2, hasBatchNo, SalesUOM, PurchaseUOM, isSalesItem, isPurchaseItem, isRawMaterialItem, isFinishGoodsItem, UDF_UDF1, UDF_UDF2, UDF_UDF3, UDF_UDF4, UDF_UDF5) && insert;

                            object1 = null;
                        }
                        if (insert) currIndex++;
                        //getTable();

                        items = null;
                        getItemData(connUrl, currIndex);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.i("custDebut", ex.getMessage());
                }
            }
            else {
                //Toast.makeText(UploadDownload.this, "Could not get any data.", Toast.LENGTH_LONG).show();
                //getTable();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i("custDebug", ex.getMessage());
        }
    }

    class GetItemImageList extends AsyncTask<String, Void, Integer> {
        Activity context;
        ProgressDialog pd;

        public GetItemImageList(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(UploadDownload.this);
            pd.setMessage("Download Item Images...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Integer doInBackground(String... connUrl) {
            int myCount = getItemImagesData(connUrl[0], 0);
            return myCount;
        }

        @Override
        protected void onPostExecute(Integer s) {
            super.onPostExecute(s);

            if (pd.isShowing()) pd.dismiss();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm",
                    Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());

            db.insertULDL("Item Images", s,
                    currentDateandTime, "download");
            getTable();

            SharedPreferences sharedPref = context.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("DownloadTime", dwnTime + 1);
            editor.apply();
        }
    }

    private int getItemImagesData(String connUrl, int currIndex)
    {
        int myCount = 0;
        HttpURLConnection conn;
        BufferedReader reader;

        try {
            java.net.URL url = new URL(connUrl + "/getItemImagesCount");
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String myResult = br.readLine();
            int imageLength = 0;
            if (myResult != null) {
                imageLength = Integer.parseInt(myResult);
            }

            if (imageLength > 0) {
                for (int j = 1; j <= imageLength; j++) {
                    try {
                        url = new URL(connUrl + "/getItemImages");
                        conn = (HttpURLConnection) url.openConnection();
                        conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
                        conn.setRequestMethod("POST");
                        conn.setDoOutput(true);
                        conn.setDoInput(true);

                        // Send index to indicate no. of received chunks
                        byte[] sendb = Integer.toString(j).getBytes("UTF-8");
                        conn.getOutputStream().write(sendb);
                        conn.getOutputStream().flush();
                        conn.getOutputStream().close();

                        // Receive chunk of data
                        InputStream inputStream = new BufferedInputStream(conn.getInputStream());
                        reader = new BufferedReader(new InputStreamReader(inputStream));

                        StringBuilder sb = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        inputStream.close();
                        inputStream = null;
                        reader.close();
                        String status = sb.toString();
                        sb.setLength(0);

                        if (status != null) {
                            try {
                                JSONArray items = new JSONArray(status);

                                if (items.length() > 0) {
                                    if (j == 0) db.resetAllItemImages();

                                    boolean insert = true;
                                    for (int i = 0; i < items.length(); i++) {
                                        JSONObject object1 = items.getJSONObject(i);
                                        String ItemCode = object1.getString("ItemCode");
                                        String Image = object1.getString("Image");

                                        insert = db.updateItemImages(ItemCode, Image) && insert;
                                        insert =db.insertItemImage(ItemCode, Image) && insert;
                                        myCount++;                                    }
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                Log.i("custDebug", ex.getMessage());
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return myCount;
    }

    //Download ItemUOM Data
    class GetItemUOMList extends AsyncTask<String, Void, Integer> {
        Activity context;
        ProgressDialog pd;

        GetItemUOMList(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setMessage("Download ItemUOMs...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Integer doInBackground(String... connUrl) {
            int myCount = getItemUOMData(connUrl[0],0,0);
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

            db.insertULDL("Items", s, currentDateandTime, "download");
            getTable();
        }
    }

    private Integer getItemUOMData(String connUrl, int currIndex, int myCount)
    {
        HttpURLConnection conn;
        BufferedReader reader;

        try {
            final java.net.URL url = new URL(connUrl + "/getAllItemUOM");
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            // Send index to indicate no. of received chunks
            byte[] sendb = Integer.toString(currIndex).getBytes("UTF-8");
            conn.getOutputStream().write(sendb);
            conn.getOutputStream().flush();
            conn.getOutputStream().close();

            // Receive chunk of data
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            String status = sb.toString();

            if (status != null) {
                JSONArray uom = new JSONArray(status);

                if (uom.length() > 0) {
                    if (currIndex == 0) db.deleteItemUOM();

                    boolean insert = true;
                    for (int i = 0; i < uom.length(); i++) {
                        JSONObject object = uom.getJSONObject(i);
                        String ItemCode = object.getString("ItemCode");
                        String UOM = object.getString("UOM");

                        BigDecimal price = new BigDecimal(object.getString("Price"));
                        BigDecimal price2 = new BigDecimal(object.getString("Price2"));
                        String BarCode = object.getString("BarCode");
                        String Shelf = object.getString("Shelf");
                        Float BalQty = Float.parseFloat(String.valueOf(object.getDouble("BalQty")));
                        Float Rate = Float.parseFloat(String.valueOf(object.getDouble("Rate")));

                        BigDecimal minPrice = new BigDecimal(object.getString("MinPrice"));
                        BigDecimal maxPrice = new BigDecimal(object.getString("MaxPrice"));
                        BigDecimal price3 = new BigDecimal(object.getString("Price3"));
                        BigDecimal price4 = new BigDecimal(object.getString("Price4"));
                        BigDecimal price5 = new BigDecimal(object.getString("Price5"));
                        BigDecimal price6 = new BigDecimal(object.getString("Price6"));


                        insert = db.insertUOM(ItemCode, UOM, price, price2, Shelf,
                                BarCode, BalQty, Rate, minPrice, maxPrice, price3, price4, price5, price6) && insert;
                        object = null;
                        myCount++;
                    }
                    if (insert) currIndex++;

                    myCount = getItemUOMData(connUrl, currIndex, myCount);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i("custDebug", ex.getMessage());
        }
        return myCount;
    }

    //Download Agent Data
    class GetSalesAgentList extends AsyncTask<String, Void, Integer> {
        Activity context;
        ProgressDialog pd;

        public GetSalesAgentList(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setMessage("Download SalesAgents...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Integer doInBackground(String... connUrl) {
            int myCount = getSalesAgentData(connUrl[0]);
            return myCount;
        }

        @Override
        protected void onPostExecute(Integer s) {
            super.onPostExecute(s);

            if (pd.isShowing())
                pd.dismiss();

            // Datetime
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm",
                    Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());

            db.insertULDL("Sales Agents", s, currentDateandTime, "download");
            getTable();
        }
    }

    private Integer getSalesAgentData(String connUrl) {
        int myCount = 0;
        HttpURLConnection conn = null;
        BufferedReader reader;

        try {
            final java.net.URL url = new URL(connUrl + "/getAllSalesAgent");
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestMethod("GET");

            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            String status = sb.toString();

            if (status != null)
            {
                JSONArray agent = new JSONArray(status);

                db.deleteAgent();
                for (int i = 0; i < agent.length(); i++) {
                    JSONObject object = agent.getJSONObject(i);
                    String SalesAgent = object.getString("Agent");
                    String Description = object.getString("Description");

                    db.insertSalesAgent(SalesAgent, Description);
                    object = null;
                    myCount++;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i("custDebug", ex.getMessage());
        }
        return myCount;
    }

    //Download PurchaseAgent Data
    class GetPurchaseAgentList extends AsyncTask<String, Void, Integer> {
        Activity context;
        ProgressDialog pd;

        public GetPurchaseAgentList(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setMessage("Download PurchaseAgents...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Integer doInBackground(String... connUrl) {
            int myCount = getPurchaseAgentData(connUrl[0]);
            return myCount;
        }

        @Override
        protected void onPostExecute(Integer s) {
            super.onPostExecute(s);

            if (pd.isShowing())
                pd.dismiss();

            // Datetime
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm",
                    Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());

            db.insertULDL("Purchase Agents", s, currentDateandTime, "download");
            getTable();
        }
    }

    private Integer getPurchaseAgentData(String connUrl) {
        int myCount = 0;
        HttpURLConnection conn = null;
        BufferedReader reader;

        try {
            final java.net.URL url = new URL(connUrl + "/getAllPurchaseAgent");
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestMethod("GET");

            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            String status = sb.toString();

            if (status != null)
            {
                JSONArray agent = new JSONArray(status);

                db.deletePurchaseAgent();
                for (int i = 0; i < agent.length(); i++) {
                    JSONObject object = agent.getJSONObject(i);
                    String PurchaseAgent = object.getString("Agent");
                    String Description = object.getString("Description");

                    db.insertPurchaseAgent(PurchaseAgent, Description);
                    object = null;
                    myCount++;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i("custDebug", ex.getMessage());
        }
        return myCount;
    }

    //Download Location Data
    class GetLocationList extends AsyncTask<String, Void, Integer> {
        Activity context;
        ProgressDialog pd;

        public GetLocationList(Activity context) {
            this.context = context;
        }

        @Override
        protected Integer doInBackground(String... connUrl) {
            int myCount = getLocationData(connUrl[0]);
            return myCount;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setMessage("Download Locations...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected void onPostExecute(Integer s) {
            super.onPostExecute(s);

            if (pd.isShowing())
                pd.dismiss();

            // Datetime
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm",
                    Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());

            db.insertULDL("Location", s, currentDateandTime, "download");
            getTable();
        }
    }

    private Integer getLocationData(String connUrl)
    {
        int myCount = 0;
        HttpURLConnection conn = null;
        BufferedReader reader;

        try {
            final java.net.URL url = new URL(connUrl + "/getAllLocation");
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestMethod("GET");

            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            String status = sb.toString();

            if(status != null)
            {
                JSONArray location = new JSONArray(status);

                db.deleteLocation();
                for (int i = 0; i < location.length(); i++) {
                    JSONObject object = location.getJSONObject(i);
                    String Location = object.getString("Location");
                    String Description = object.getString("Description");



                    db.insertLocation(Location, Description);
                    object = null;
                    myCount++;
                }
                // Datetime
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i("custDebug", ex.getMessage());
        }
        return myCount;
    }

//    class GetSalesOrderList extends AsyncTask<String, Void, Integer> {
//        Activity context;
//        ProgressDialog pd;
//
//        GetSalesOrderList(Activity context) {
//            this.context = context;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pd = new ProgressDialog(context);
//            pd.setMessage("Download SalesOrder...");
//            pd.setCancelable(false);
//            pd.show();
//        }
//
//        @Override
//        protected Integer doInBackground(String... connUrl) {
//            int s = getSalesOrderData(connUrl[0]);
//            return s;
//        }
//
//        @Override
//        protected void onPostExecute(Integer s) {
//            super.onPostExecute(s);
//
//            if (pd.isShowing())
//                pd.dismiss();
//
//            // Datetime
//            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm",
//                    Locale.getDefault());
//            String currentDateandTime = sdf.format(new Date());
//
//            db.insertULDL("Sales Order", s, currentDateandTime, "download");
//            getTable();
//
//        }
//    }
//
//    private Integer getSalesOrderData(String connUrl)
//    {
//        int myCount = 0;
//        HttpURLConnection conn;
//        BufferedReader reader;
//        try {
//            java.net.URL url = new URL(connUrl + "/getSalesOrder");
//            conn = (HttpURLConnection) url.openConnection();
//            conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
//            conn.setRequestMethod("GET");
////            conn.setDoOutput(true);
////            conn.setDoInput(true);
//
//            // Receive chunk of data
//            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
//            reader = new BufferedReader(new InputStreamReader(inputStream));
//
//            StringBuilder sb = new StringBuilder();
//            String line = null;
//
//            while ((line = reader.readLine()) != null) {
//                sb.append(line + "\n");
//            }
//            reader.close();
//            String status = sb.toString();
//
//            if(status != null)
//            {
//                JSONArray location = new JSONArray(status);
//
////                db.deleteSalesOrder();
//
//                for (int i = 0; i < location.length(); i++) {
//                    JSONObject object = location.getJSONObject(i);
//                    Integer ID = object.getInt("MKey");
//                    String DocNo = object.getString("DocNo");
//                    String DocDate = object.getString("DocDate");
//                    String DebtorCode = object.getString("DebtorCode");
//                    String DebtorName = object.getString("DebtorName");
//                    String SalesAgent = object.getString("SalesAgent");
//                    String Phone = object.getString("Phone");
//                    String Fax = object.getString("Fax");
//                    String Attention = object.getString("Attention");
//                    Integer LineNo = object.getInt("LineNo");
//                    String ItemCode = object.getString("ItemCode");
//                    String ItemDescription = object.getString("ItemDescription");
//                    String Location = object.getString("Location");
//                    Double Qty = object.getDouble("Qty");
//                    String UOM = object.getString("UOM");
//
//                    db.insertSalesOrder(ID, DocNo, DocDate, DebtorCode, DebtorName, SalesAgent, Phone, Fax, Attention, LineNo, ItemCode, ItemDescription, Location, Qty, UOM);
//                    object = null;
//                    myCount++;
//                }
//                // Datetime
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            Log.i("custDebug", ex.getMessage());
//        }
//        return myCount;
//    }


    //Download HistoryPrice Data
    class GetHistoryPriceList extends AsyncTask<String, Void, Integer> {
        Activity context;
        ProgressDialog pd;

        GetHistoryPriceList(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(context);
            pd.setMessage("Download HistoryPrices...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Integer doInBackground(String... connUrl) {
            int myCount = getHistoryPriceData(connUrl[0],0,0);
            return myCount;
        }

        @Override
        protected void onPostExecute(Integer s) {
            super.onPostExecute(s);

            if (pd.isShowing())
                pd.dismiss();

            // After all chunks downloaded
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());

            db.insertULDL("Price Histories", s, currentDateandTime, "download");
            getTable();
        }
    }

    private Integer getHistoryPriceData(String connUrl, int currIndex, int myCount)
    {
        HttpURLConnection conn;
        BufferedReader reader;

        try {
            final java.net.URL url = new URL(connUrl + "/getAllHistoryPrice");
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            // Send index to indicate no. of received chunks
            byte[] sendb = Integer.toString(currIndex).getBytes("UTF-8");
            conn.getOutputStream().write(sendb);
            conn.getOutputStream().flush();
            conn.getOutputStream().close();

            // Receive chunk of data
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            String status = sb.toString();

            if (status != null) {
                JSONArray history_Price = new JSONArray(status);

                if (history_Price.length() > 0) {
                    if (currIndex == 0) db.deleteHistoryPrice();

                    boolean insert = true;
                    for (int i = 0; i < history_Price.length(); i++) {
                        JSONObject object = history_Price.getJSONObject(i);
                        String DocType = object.getString("DocType");
                        String AccNo = object.getString("AccNo");
                        String ItemCode = object.getString("ItemCode");
                        String DocNo = object.getString("DocNo");
                        String DocDate = object.getString("DocDate");
                        String Location = object.getString("Location");
                        String Agent = object.getString("Agent");
                        String Description = object.getString("Description");
                        Float Qty = BigDecimal.valueOf(object.getDouble("Qty")).floatValue();
                        String UOM = object.getString("UOM");
                        Float UnitPrice = BigDecimal.valueOf(object.getDouble("UnitPrice")).floatValue();
                        Float SubTotal = BigDecimal.valueOf(object.getDouble("SubTotal")).floatValue();

                        insert = db.insertHistoryPrice(DocType, AccNo, ItemCode, DocNo,
                                DocDate, Location, Agent, Description, Qty, UOM, UnitPrice,
                                0.00f, SubTotal) && insert;
                        object = null;
                        myCount++;
                    }
                    if (insert) currIndex++;
                    history_Price = null;

                    myCount = getHistoryPriceData(connUrl, currIndex, myCount);

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i("custDebug", ex.getMessage());
        }
        return myCount;
    }

    //Download TaxType Data
    class GetTaxTypeList extends AsyncTask<String, Void, Integer> {
        Activity context;
        ProgressDialog pd;

        public GetTaxTypeList(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(context);
            pd.setMessage("Download TaxTypes...");
            pd.setCancelable(false);
            pd.show();

        }

        @Override
        protected Integer doInBackground(String... connUrl) {
            int myCount = getTaxTypeData(connUrl[0]);
            return myCount;
        }

        @Override
        protected void onPostExecute(Integer s) {
            super.onPostExecute(s);

            if (pd.isShowing())
                pd.dismiss();

            // Datetime
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());

            db.insertULDL("Tax Types", s, currentDateandTime, "download");
            getTable();
        }
    }

    private Integer getTaxTypeData(String connUrl)
    {
        int myCount = 0;
        HttpURLConnection conn = null;
        BufferedReader reader;

        try {
            final java.net.URL url = new URL(connUrl + "/getAllTaxType");
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestMethod("GET");

            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            String status = sb.toString();

            if (status != null) {
                try {
                    JSONArray taxtype = new JSONArray(status);

                    db.deleteTaxType();
                    for (int i = 0; i < taxtype.length(); i++) {
                        JSONObject object = taxtype.getJSONObject(i);
                        String TaxType = object.getString("TaxType");
                        String Description = object.getString("Description");
                        Float TaxRate = BigDecimal.valueOf(object.getDouble("TaxRate")).floatValue();

                        HashMap<String, String> taxtypeList = new HashMap<String, String>();
                        taxtypeList.put("TaxType", TaxType);
                        taxtypeList.put("Description", Description);
                        taxtypeList.put("TaxRate", String.valueOf(TaxRate));

                        ArrayList<HashMap<String, String>> TaxTypeList = new ArrayList<HashMap<String, String>>();
                        TaxTypeList.add(taxtypeList);
                        boolean insert = db.insertTaxType(TaxType, Description, TaxRate);
                        myCount++;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.i("custDebug", ex.getMessage());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return myCount;
    }

    //Download ItemBalance Data
    class GetItemBalanceList extends AsyncTask<String, Void, Integer> {
        Activity context;
        ProgressDialog pd;

        GetItemBalanceList(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(context);
            pd.setMessage("Download ItemBalances...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Integer doInBackground(String... connUrl) {
            int myCount = getItemBalanceData(connUrl[0],0,0);
            return myCount;
        }

        @Override
        protected void onPostExecute(Integer s) {
            super.onPostExecute(s);

            if (pd.isShowing())
                pd.dismiss();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());

            db.insertULDL("Stock Balance", s, currentDateandTime, "download");
            getTable();
        }
    }

    private Integer getItemBalanceData(String connUrl, int currIndex, int myCount)
    {
        HttpURLConnection conn;
        BufferedReader reader;
        try {
            final java.net.URL url = new URL(connUrl + "/getAllItemBalance");
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            // Send index to indicate no. of received chunks
            byte[] sendb = Integer.toString(currIndex).getBytes("UTF-8");
            conn.getOutputStream().write(sendb);
            conn.getOutputStream().flush();
            conn.getOutputStream().close();

            // Receive chunk of data
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            String status = sb.toString();

            if (status != null) {
                JSONArray itembalance = new JSONArray(status);
                if (currIndex == 0) db.deleteStockBalance();
                if (itembalance.length() > 0) {


                    boolean insert = true;
                    for (int i = 0; i < itembalance.length(); i++) {
                        JSONObject object = itembalance.getJSONObject(i);
                        String ItemCode = object.getString("ItemCode");
                        String UOM = object.getString("UOM");
                        //Follow Database Data Member Name
                        String Location = object.getString("Location");
                        Float BalQty = BigDecimal.valueOf(object.getDouble("BalQty")).floatValue();
                        String batchNo = object.getString("BatchNo");

//                        HashMap<String, String> itemList = new HashMap<String, String>();
//                        itemList.put("ItemCode", ItemCode);
//                        itemList.put("UOM", UOM);
//                        itemList.put("Location", Location);
//                        itemList.put("BalQty", String.valueOf(BalQty));
//
//                        ArrayList<HashMap<String, String>> ItemBalanceList = new ArrayList<HashMap<String, String>>();
//                        ItemBalanceList.add(itemList);

                        insert = db.insertStockBalance(ItemCode, UOM, Location, BalQty, batchNo)
                                && insert;
                        object = null;
                        myCount++;
                    }

                    if (insert) currIndex++;
                    itembalance = null;

                    myCount = getItemBalanceData(connUrl, currIndex, myCount);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i("custDebug", ex.getMessage());
        }
        return myCount;
    }

    //Download ItemBatch Data
    class GetAllItemBatch extends AsyncTask<String, Void, Integer> {
        Activity context;
        ProgressDialog pd;

        GetAllItemBatch(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(context);
            pd.setMessage("Download ItemBatch...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Integer doInBackground(String... connUrl) {
            int myCount = getItemBatchData(connUrl[0],0,0);
            return myCount;
        }

        @Override
        protected void onPostExecute(Integer s) {
            super.onPostExecute(s);

            if (pd.isShowing())
                pd.dismiss();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());

            db.insertULDL("Item Batch", s, currentDateandTime, "download");
            getTable();
        }
    }

    private Integer getItemBatchData(String connUrl, int currIndex, int myCount)
    {
        HttpURLConnection conn;
        BufferedReader reader;
        try {
            final java.net.URL url = new URL(connUrl + "/getAllItemBatch");
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            // Send index to indicate no. of received chunks
            byte[] sendb = Integer.toString(currIndex).getBytes("UTF-8");
            conn.getOutputStream().write(sendb);
            conn.getOutputStream().flush();
            conn.getOutputStream().close();

            // Receive chunk of data
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            String status = sb.toString();

            if (status != null) {
                JSONArray itembatch = new JSONArray(status);

                if (itembatch.length() > 0) {
                    if (currIndex == 0) db.deleteItemBatch();

                    String Description = null;
                    String ManuDate = null;
                    String ExpiDate = null;

                    boolean insert = true;
                    for (int i = 0; i < itembatch.length(); i++) {
                        JSONObject object = itembatch.getJSONObject(i);
                        String ItemCode = object.getString("ItemCode");
                        String BatchNo = object.getString("BatchNo");
                        //Follow Database Data Member Name
                        if (!object.isNull("Description"))
                            Description = object.getString("Description");
                        else Description = null;

                        if (!object.isNull("ManufacturedDate"))
                            ManuDate = object.getString("ManufacturedDate");
                        else ManuDate = null;

                        if (!object.isNull("ExpiryDate"))
                            ExpiDate = object.getString("ExpiryDate");
                        else ExpiDate = null;


                        insert = db.insertItemBatch(ItemCode, BatchNo, Description, ManuDate,ExpiDate)
                                && insert;
                        object = null;
                        myCount++;
                    }

                    if (insert) currIndex++;
                    itembatch = null;

                    myCount = getItemBatchData(connUrl, currIndex, myCount);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i("custDebug", ex.getMessage());
        }
        return myCount;
    }

    //UPLOAD DATA
    class CheckInvoiceData extends AsyncTask<String, String, String> {
        Activity context;
        ProgressDialog progressDialog;

        public CheckInvoiceData(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(UploadDownload.this);
            progressDialog.setMessage("Upload Invoice...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... connUrl) {
            int i;
            int count = 0;
            String returns = "";

            try {
                List<JSONObject> jsonList = GetchkInvoiceDataToUpload();
                if (jsonList != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Invoice: ");
                    for (i = 0; i < jsonList.size(); i++) {
                        String send = jsonList.get(i).toString();
                        byte[] sendb = send.getBytes("UTF-8");
                        URL tUrl = new URL(connUrl[0] + "/checkSalesDup");
                        Log.wtf("ConnURL Stock Take", connUrl[0]);
                        final HttpURLConnection httpURLConnection = (HttpURLConnection) tUrl.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setRequestProperty("Content-Type", "application/json");
                        httpURLConnection.setRequestProperty("Accept", "application/json");
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.connect();
                        httpURLConnection.getOutputStream().write(sendb);
                        httpURLConnection.getOutputStream().flush();
                        httpURLConnection.getOutputStream().close();
                        BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        sb.append(br.readLine() + "\t");
                        count += 1;
                        br.close();
                    }

                }
                return Integer.toString(count);
            } catch (Exception e) {
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                new SendInvoiceData(UploadDownload.this).execute(url);
            } else {
                Toast.makeText(UploadDownload.this, "Failed", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }

    public List<JSONObject> GetchkInvoiceDataToUpload() {
        Cursor data = db.getUploadchkInvoice();
        List<JSONObject> jsonList = new ArrayList<JSONObject>();
        if (data.getCount() > 0) {
            while (data.moveToNext()) {
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("DocNo", data.getString(data.getColumnIndex("DocNo")));
                    jsonList.add(jsonObject);
//                    Log.i("custDebug", jsonObject.toString());
                } catch (Exception e) {
                    Log.i("custDebug", e.getMessage());
                }
            }
        }
        return jsonList;
    }

    class CheckPaymentData extends AsyncTask<String, String, String> {
        Activity context;
        ProgressDialog progressDialog;

        public CheckPaymentData(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(UploadDownload.this);
            progressDialog.setMessage("Upload Payment...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... connUrl) {
            int i;
            int count = 0;
            String returns = "";

            try {
                List<JSONObject> jsonList = GetchkPaymentDataToUpload();
                if (jsonList != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Payment: ");
                    for (i = 0; i < jsonList.size(); i++) {
                        String send = jsonList.get(i).toString();
                        byte[] sendb = send.getBytes("UTF-8");
                        URL tUrl = new URL(connUrl[0] + "/checkPaymentDup");
                        Log.wtf("ConnURL Stock Take", connUrl[0]);
                        final HttpURLConnection httpURLConnection = (HttpURLConnection) tUrl.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setRequestProperty("Content-Type", "application/json");
                        httpURLConnection.setRequestProperty("Accept", "application/json");
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.connect();
                        httpURLConnection.getOutputStream().write(sendb);
                        httpURLConnection.getOutputStream().flush();
                        httpURLConnection.getOutputStream().close();
                        BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        sb.append(br.readLine() + "\t");
                        count += 1;
                        br.close();
                    }

                }
                return Integer.toString(count);
            } catch (Exception e) {
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                new SendPaymentData(UploadDownload.this).execute(url);
            } else {
                Toast.makeText(UploadDownload.this, "Failed", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }

    public List<JSONObject> GetchkPaymentDataToUpload() {
        Cursor data = db.getUploadchkPayment();
        List<JSONObject> jsonList = new ArrayList<JSONObject>();
        if (data.getCount() > 0) {
            while (data.moveToNext()) {
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("DocNo", data.getString(data.getColumnIndex("DocNo")));
                    jsonList.add(jsonObject);
//                    Log.i("custDebug", jsonObject.toString());
                } catch (Exception e) {
                    Log.i("custDebug", e.getMessage());
                }
            }
        }
        return jsonList;
    }

    //Upload StockCount Data
    class SendStockCountData extends AsyncTask<String, String, String> {
        Activity context;
        ProgressDialog progressDialog;

        public SendStockCountData(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(UploadDownload.this);
            progressDialog.setMessage("Upload StockCount...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null)
            {
                db.setStockCountUploaded();
//                count -= 1;
//                if (count == 0) {
//                    finishDialog();
//                }
                getTable();
            }
            else
            {
                Toast.makeText(UploadDownload.this, "Failed", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }

        @Override
        protected String doInBackground(String...connUrl) {
            int i;
            int count = 0;
            String returns = "";

            try {
                List<JSONObject> jsonList = GetStockCountDatatoUpload();
                if (jsonList != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("StockCount: ");
                    for (i=0; i<jsonList.size(); i++) {
                        String send = jsonList.get(i).toString();
                        byte[] sendb = send.getBytes("UTF-8");
                        URL tUrl = new URL(connUrl[0] + "/setStockTake");
                        final HttpURLConnection httpURLConnection = (HttpURLConnection) tUrl.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setRequestProperty("Content-Type", "application/json");
                        httpURLConnection.setRequestProperty("Accept", "application/json");
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.connect();
                        httpURLConnection.getOutputStream().write(sendb);
                        httpURLConnection.getOutputStream().flush();
                        httpURLConnection.getOutputStream().close();
                        BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        sb.append(br.readLine()+"\t");
                        count += 1;
                        br.close();
                    }
                    // Datetime
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm",
                            Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());

                    db.insertULDL("Stock Count", count,
                            currentDateandTime, "upload");
                    returns = sb.toString();
                    //Log.i("custDebug", returns);
                }
                return Integer.toString(count);
            } catch (Exception e) { return e.toString(); }
        }
    }

    public List<JSONObject> GetStockCountDatatoUpload() {
        Cursor data = db.getUploadStockTake();
        data.moveToFirst();
        List<JSONObject> jsonList = new ArrayList<JSONObject>();
        if (data.getCount() > 0) {
            while (data.moveToNext()) {
                try {
                    // Doc Date
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm",
                    Locale.getDefault());
                    Date dateTime = dateFormat
                            .parse(data.getString(data.getColumnIndex("DocDate"))
                            .substring(0, 12));

                    jsonObject = new JSONObject();
                    jsonObject.put("DocNo", data.getString(data.getColumnIndex("DocNo")));
                    Log.wtf("Value: ",data.getString(data.getColumnIndex("DocNo")));
                    jsonObject.put("DocDate", data.getString(data.getColumnIndex("DocDate")));
                    Log.wtf("Value: ",data.getString(data.getColumnIndex("DocDate")));
                    jsonObject.put("Location", data.getString(data.getColumnIndex("Location")));
                    Log.wtf("Value: ",data.getString(data.getColumnIndex("Location")));
                    jsonObject.put("SalesAgent", data.getString(data.getColumnIndex("SalesAgent")));
                    Log.wtf("Value: ",data.getString(data.getColumnIndex("SalesAgent")));
                    jsonObject.put("Remarks", data.getString(data.getColumnIndex("Remarks")));
                    Log.wtf("Value: ",data.getString(data.getColumnIndex("Remarks")));
                    jsonObject.put("CreatedTimeStamp", data.getString(data.getColumnIndex("CreatedTimeStamp")));
                    Log.wtf("Value: ",data.getString(data.getColumnIndex("CreatedTimeStamp")));
                    jsonObject.put("ItemCode", data.getString(data.getColumnIndex("ItemCode")));
                    Log.wtf("Value: ",data.getString(data.getColumnIndex("ItemCode")));
                    jsonObject.put("Description", data.getString(data.getColumnIndex("Description")));
                    Log.wtf("Value: ",data.getString(data.getColumnIndex("Description")));
                    jsonObject.put("UOM", data.getString(data.getColumnIndex("UOM")));
                    Log.wtf("Value: ",data.getString(data.getColumnIndex("UOM")));
                    jsonObject.put("Qty", data.getString(data.getColumnIndex("Qty")));
                    Log.wtf("Value: ",data.getString(data.getColumnIndex("Qty")));
                    jsonObject.put("Exported", data.getString(data.getColumnIndex("Exported")));
                    Log.wtf("Value: ",data.getString(data.getColumnIndex("Exported")));
                    jsonObject.put("ExportedDocNo", data.getString(data.getColumnIndex("ExportedDocNo")));
                    Log.wtf("Value: ",data.getString(data.getColumnIndex("ExportedDocNo")));
                    jsonList.add(jsonObject);
                } catch (Exception e) { Log.i("custDebug", e.getMessage()); }
            }
        }
        return jsonList;
    }

    //Upload Payment
    class SendPaymentData extends AsyncTask<String, String, String> {
        Activity context;
        ProgressDialog progressDialog;

        public SendPaymentData(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(UploadDownload.this);
            progressDialog.setMessage("Upload Payment...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null)
            {
               // db.setSalesUploaded();
//                count -= 1;
//                if (count == 0) {
//                    finishDialog();
//                }
                //getTable();
            }
            else
            {
                Toast.makeText(UploadDownload.this, "Failed", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }

        @Override
        protected String doInBackground(String...connUrl) {
            int i;
            int count = 0;
            String returns = null;

            try {
                List<JSONObject> jsonList = GetPaymentDataToUpload();
                if (jsonList != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Payment: ");

                    for (i=0; i<jsonList.size(); i++) {
                        String send = jsonList.get(i).toString();
                        //Log.i("custDebug", send);

                        byte[] sendb = send.getBytes("UTF-8");
                        URL tUrl = new URL(connUrl[0] + "/setSalesPayments");
                        final HttpURLConnection httpURLConnection = (HttpURLConnection) tUrl.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setRequestProperty("Content-Type", "application/json");
                        httpURLConnection.setRequestProperty("Accept", "application/json");
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.connect();
                        httpURLConnection.getOutputStream().write(sendb);
                        httpURLConnection.getOutputStream().flush();
                        httpURLConnection.getOutputStream().close();
                        //BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        BufferedReader br = null;
                        if (httpURLConnection.getResponseCode() == 200) {
                            br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                            db.setPaymentUploaded();
                        } else {
                            br = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
                        }
                        sb.append(br.readLine()+"\t");
                        count += 1;
                        br.close();
                    }
                }
                return Integer.toString(count);
            } catch (Exception e) {
                Log.i("custDebug", e.getMessage());
                return e.toString();
            }
        }
    }

    public List<JSONObject> GetPaymentDataToUpload() {
        Cursor data = db.getPaymentToUpload();
        List<JSONObject> jsonList = new ArrayList<JSONObject>();
        if (data.getCount() > 0) {
            while(data.moveToNext()) {
                try {
                    // Payment time
                    String dateTxt = data.getString(2);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss",
                            Locale.getDefault());
                    Date paymentTime = dateFormat.parse(dateTxt);
//                    long date = Date.UTC(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay(),
//                            dateTime.getHours(), dateTime.getMinutes(), dateTime.getSeconds());
//                    String sendDate = "/Date("+date+")/";

                    jsonObject = new JSONObject();
                    jsonObject.put("DocNo", data.getString(1));
                    jsonObject.put("PaymentTime", parseDateToJSON(paymentTime));
                    jsonObject.put("PaymentType", data.getString(3));
                    jsonObject.put("PaymentMethod", data.getString(4));
                    jsonObject.put("OriAmt", Float.parseFloat(data.getString(5)));
                    jsonObject.put("PaymentAmt", Float.parseFloat(data.getString(6)));
                    if (data.getString(7)!=null) {
                        jsonObject.put("CashChanges", Float.parseFloat(data.getString(7)));
                    } else {
                        jsonObject.put("CashChanges", 0.00f);
                    }
                    if (data.getString(8)!=null) {
                        jsonObject.put("CCType", data.getString(8));
                    } else {
                        jsonObject.put("CCType", "");
                    }
                    if (data.getString(10)!=null) {
                        jsonObject.put("CCExpiryDate", data.getString(10).substring(3,7));
                    } else {
                        jsonObject.put("CCExpiryDate", "");
                    }
                    if (data.getString(11)!=null) {
                        jsonObject.put("CCApprovalCode", data.getString(11));
                    } else {
                        jsonObject.put("CCApprovalCode", "");
                    }
                    if (data.getString(12)!=null) {
                        jsonObject.put("Remarks", data.getString(12));
                    } else {
                        jsonObject.put("Remarks", "");
                    }
                    jsonList.add(jsonObject);
//                    Log.i("custDebug", jsonObject.toString());
                } catch (Exception e) { Log.i("custDebug", e.getMessage()); }
            }
        }
        return jsonList;
    }

    //Upload Invoices
    class SendInvoiceData extends AsyncTask<String, String, String> {
        Activity context;
        ProgressDialog progressDialog;

        public SendInvoiceData(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(UploadDownload.this);
            progressDialog.setMessage("Upload Invoice...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String...connUrl) {
            int i;
            int count = 0;
            String returns = "";

            try {
                List<JSONObject> jsonList = GetInvoiceDataToUpload();
                if (jsonList != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Invoice: ");
                    for (i=0; i<jsonList.size(); i++) {
                        String send = jsonList.get(i).toString();
                        byte[] sendb = send.getBytes("UTF-8");
                        URL tUrl = new URL(connUrl[0] + "/setSales");
                        Log.wtf("ConnURL Stock Take",connUrl[0]);
                        final HttpURLConnection httpURLConnection = (HttpURLConnection) tUrl.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setRequestProperty("Content-Type", "application/json");
                        httpURLConnection.setRequestProperty("Accept", "application/json");
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.connect();
                        httpURLConnection.getOutputStream().write(sendb);
                        httpURLConnection.getOutputStream().flush();
                        httpURLConnection.getOutputStream().close();
                       // BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        BufferedReader br = null;
                        if (httpURLConnection.getResponseCode() == 200) {
                            br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                            db.setSalesUploaded();
                        } else {
                            br = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
                        }

                        sb.append(br.readLine()+"\t");
                        count += 1;
                        br.close();
                    }
                    // Datetime
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm",
                            Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());

                    db.insertULDL("Invoice", count,
                            currentDateandTime, "upload");
                    returns = sb.toString();
//                    Log.i("custDebug", returns);
                }
                return Integer.toString(count);
            } catch (Exception e) { return e.toString(); }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null)
            {
             //   new CheckPaymentData(UploadDownload.this).execute(url);
//                count -= 1;
//                if (count == 0) {
//                    finishDialog();
//                }
                getTable();
            }
            else
            {
                Toast.makeText(UploadDownload.this, "Failed", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }

    public List<JSONObject> GetInvoiceDataToUpload() {
        Cursor data = db.getUploadInvoice();
        List<JSONObject> jsonList = new ArrayList<JSONObject>();
        if (data.getCount() > 0) {
            while(data.moveToNext()) {
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("DocNo", data.getString(data.getColumnIndex("DocNo")));
                    jsonObject.put("DocDate", data.getString(data.getColumnIndex("DocDate")));
                    doctype =  data.getString(data.getColumnIndex("DocType"));
                    jsonObject.put("DocType", data.getString(data.getColumnIndex("DocType")));
                    jsonObject.put("DebtorCode", data.getString(data.getColumnIndex("DebtorCode")));
                    jsonObject.put("DebtorName", data.getString(data.getColumnIndex("DebtorName")));
                    jsonObject.put("SalesAgent", data.getString(data.getColumnIndex("SalesAgent")));
                    jsonObject.put("Phone", data.getString(data.getColumnIndex("Phone")));
                    jsonObject.put("Fax", data.getString(data.getColumnIndex("Fax")));
                    jsonObject.put("Attention", data.getString(data.getColumnIndex("Attention")));
                    jsonObject.put("Location", data.getString(data.getColumnIndex("Location")));
                    jsonObject.put("ItemCode", data.getString(data.getColumnIndex("ItemCode")));
                    jsonObject.put("ItemDescription", data.getString(data.getColumnIndex("ItemDescription")));
                    jsonObject.put("Qty", data.getFloat(data.getColumnIndex("Qty")));
                    jsonObject.put("UOM", data.getString(data.getColumnIndex("UOM")));
                    jsonObject.put("UPrice", data.getFloat(data.getColumnIndex("UPrice")));
                    jsonObject.put("SubTotal", data.getFloat(data.getColumnIndex("SubTotal")));
                    jsonObject.put("Discount", data.getFloat(data.getColumnIndex("Discount")));
                    jsonObject.put("TaxType", data.getString(data.getColumnIndex("TaxType")));
                    jsonObject.put("TaxRate", data.getFloat(data.getColumnIndex("TaxRate")));
                    jsonObject.put("TaxValue", data.getFloat(data.getColumnIndex("TaxValue")));
                    jsonObject.put("TotalEx", data.getFloat(data.getColumnIndex("Totalex")));
                    jsonObject.put("Total", data.getFloat(data.getColumnIndex("TotalIn")));
                    jsonObject.put("LineNo", data.getInt(data.getColumnIndex("LineNo")));
                    jsonObject.put("Address1", data.getString(data.getColumnIndex("Address1")));
                    jsonObject.put("Address2", data.getString(data.getColumnIndex("Address2")));
                    jsonObject.put("Address3", data.getString(data.getColumnIndex("Address3")));
                    jsonObject.put("Address4", data.getString(data.getColumnIndex("Address4")));
                    jsonObject.put("Remark", data.getString(data.getColumnIndex("Remarks")));
                    jsonObject.put("Remark2", data.getString(data.getColumnIndex("Remarks2")));
                    jsonObject.put("Remark3", data.getString(data.getColumnIndex("Remarks3")));
                    jsonObject.put("Remark4", data.getString(data.getColumnIndex("Remarks4")));
                    jsonObject.put("DtlRemark", data.getString(data.getColumnIndex("DtlRemarks")));
                    jsonObject.put("DtlRemark2", data.getString(data.getColumnIndex("DtlRemarks2")));
                    jsonObject.put("BatchNo", data.getString(data.getColumnIndex("BatchNo")));
                    jsonObject.put("CreatedUser", data.getString(data.getColumnIndex("CreatedUser")));
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss",
                            Locale.getDefault());
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd",
                            Locale.getDefault());
                    String currentDateandTime = data.getString(data.getColumnIndex("CreatedTimeStamp"));
                    Date d = sdf2.parse(currentDateandTime);
                    jsonObject.put("CreatedDateTime", sdf.format(d));
                    jsonObject.put("LastModifiedUser", data.getString(data.getColumnIndex("LastModifiedUser")));
                    jsonObject.put("LastModifiedDateTime", data.getString(data.getColumnIndex("LastModifiedDateTime")));
                    jsonObject.put("CreditTerm", data.getString(data.getColumnIndex("CreditTerm")));
                    jsonObject.put("DiscountText", data.getString(data.getColumnIndex("DiscountText")));
                    jsonObject.put("FinalSubTotal", data.getDouble(data.getColumnIndex("FinalSubTotal")));
                    jsonObject.put("FinalTotalDiscount", data.getDouble(data.getColumnIndex("FinalTotalDiscount")));
                    jsonObject.put("FinalTotalTax", data.getDouble(data.getColumnIndex("FinalTotalTax")));
                    Boolean value = data.getDouble(data.getColumnIndex("IsRound")) > 0;
                    jsonObject.put("IsRound", value);
                    jsonObject.put("RoundingAdj", data.getDouble(data.getColumnIndex("RoundingAdj")));
                    jsonObject.put("FinalTotalIn", data.getDouble(data.getColumnIndex("FinalTotalIn")));
                    jsonObject.put("FinalTotalEx", data.getDouble(data.getColumnIndex("FinalTotalEx")));
                    jsonList.add(jsonObject);
//                    Log.i("custDebug", jsonObject.toString());
                } catch (Exception e) {
                    Log.i("custDebug", e.getMessage());
                }
            }
        }
        return jsonList;
    }

    //Upload Invoices
    class SendPurchaseData extends AsyncTask<String, String, String> {
        Activity context;
        ProgressDialog progressDialog;

        public SendPurchaseData(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(UploadDownload.this);
            progressDialog.setMessage("Upload Purchase...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String...connUrl) {
            int i;
            int count = 0;
            String returns = "";

            try {
                List<JSONObject> jsonList = GetPurchaseDataToUpload();
                if (jsonList != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Purchase: ");
                    for (i=0; i<jsonList.size(); i++) {
                        String send = jsonList.get(i).toString();
                        byte[] sendb = send.getBytes("UTF-8");
                        URL tUrl = new URL(connUrl[0] + "/setPurchase");
                        final HttpURLConnection httpURLConnection = (HttpURLConnection) tUrl.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setRequestProperty("Content-Type", "application/json");
                        httpURLConnection.setRequestProperty("Accept", "application/json");
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.connect();
                        httpURLConnection.getOutputStream().write(sendb);
                        httpURLConnection.getOutputStream().flush();
                        httpURLConnection.getOutputStream().close();
                        BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        sb.append(br.readLine()+"\t");
                        count += 1;
                        br.close();
                    }
                    // Datetime
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm",
                            Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());

                    db.insertULDL("Purchase", count,
                            currentDateandTime, "upload");
                    returns = sb.toString();
                }
                return Integer.toString(count);
            } catch (Exception e) { return e.toString(); }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null)
            {
                db.setPurchaseUploaded();
                getTable();
            }
            else
            {
                Toast.makeText(UploadDownload.this, "Failed", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }

    public List<JSONObject> GetPurchaseDataToUpload() {
        Cursor data = db.getUploadPurchase();
        List<JSONObject> jsonList = new ArrayList<JSONObject>();
        if (data.getCount() > 0) {
            while(data.moveToNext()) {
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("DocNo", data.getString(data.getColumnIndex("DocNo")));
                    jsonObject.put("DocDate", data.getString(data.getColumnIndex("DocDate")));
                    jsonObject.put("CreditorCode", data.getString(data.getColumnIndex("CreditorCode")));
                    jsonObject.put("CreditorName", data.getString(data.getColumnIndex("CreditorName")));
                    jsonObject.put("PurchaseAgent", data.getString(data.getColumnIndex("PurchaseAgent")));
                    jsonObject.put("Phone", data.getString(data.getColumnIndex("Phone")));
                    jsonObject.put("Fax", data.getString(data.getColumnIndex("Fax")));
                    jsonObject.put("Attention", data.getString(data.getColumnIndex("Attention")));
                    jsonObject.put("Location", data.getString(data.getColumnIndex("Location")));
                    jsonObject.put("ItemCode", data.getString(data.getColumnIndex("ItemCode")));
                    jsonObject.put("ItemDescription", data.getString(data.getColumnIndex("ItemDescription")));
                    jsonObject.put("Qty", data.getFloat(data.getColumnIndex("Qty")));
                    jsonObject.put("UOM", data.getString(data.getColumnIndex("UOM")));
                    jsonObject.put("UPrice", data.getFloat(data.getColumnIndex("UPrice")));
                    jsonObject.put("SubTotal", data.getFloat(data.getColumnIndex("SubTotal")));
                    jsonObject.put("Discount", data.getFloat(data.getColumnIndex("Discount")));
                    jsonObject.put("TaxType", data.getString(data.getColumnIndex("TaxType")));
                    jsonObject.put("TaxRate", data.getFloat(data.getColumnIndex("TaxRate")));
                    jsonObject.put("TaxValue", data.getFloat(data.getColumnIndex("TaxValue")));
                    jsonObject.put("TotalEx", data.getFloat(data.getColumnIndex("Totalex")));
                    jsonObject.put("Total", data.getFloat(data.getColumnIndex("TotalIn")));
                    jsonObject.put("LineNo", data.getInt(data.getColumnIndex("LineNo")));
                    jsonObject.put("BatchNo", data.getString(data.getColumnIndex("BatchNo")));
                    jsonObject.put("Remark", data.getString(data.getColumnIndex("Remarks")));
                    jsonObject.put("Remark2", data.getString(data.getColumnIndex("Remarks2")));
                    jsonObject.put("Remark3", data.getString(data.getColumnIndex("Remarks3")));
                    jsonObject.put("Remark4", data.getString(data.getColumnIndex("Remarks4")));
                    jsonObject.put("DtlRemark", data.getString(data.getColumnIndex("DtlRemarks")));
                    jsonObject.put("DtlRemark2", data.getString(data.getColumnIndex("DtlRemarks2")));
                    jsonObject.put("LastModifiedUser", data.getString(data.getColumnIndex("LastModifiedUser")));
                    jsonObject.put("LastModifiedDateTime", data.getString(data.getColumnIndex("LastModifiedDateTime")));
                    jsonList.add(jsonObject);
                } catch (Exception e) { Log.i("custDebug", e.getMessage()); }
            }
        }
        return jsonList;
    }

    //Upload StockTake
    class SendStockTakeData extends AsyncTask<String, String, String> {
        Activity context;
        ProgressDialog progressDialog;

        public SendStockTakeData(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(UploadDownload.this);
            progressDialog.setMessage("Upload Stock Take...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String...connUrl) {
            int i;
            int count = 0;
            String returns = "";

            try {
                List<JSONObject> jsonList = GetStockTakeDataToUpload();
                if (jsonList != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("StockTake: ");
                    for (i=0; i<jsonList.size(); i++) {
                        String send = jsonList.get(i).toString();
                        byte[] sendb = send.getBytes("UTF-8");
                        URL tUrl = new URL(connUrl[0] + "/setStockTake");
                        Log.wtf("ConnURL Stock Take",connUrl[0]);
                        final HttpURLConnection httpURLConnection = (HttpURLConnection) tUrl.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setRequestProperty("Content-Type", "application/json");
                        httpURLConnection.setRequestProperty("Accept", "application/json");
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.connect();
                        httpURLConnection.getOutputStream().write(sendb);
                        httpURLConnection.getOutputStream().flush();
                        httpURLConnection.getOutputStream().close();
                        BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        sb.append(br.readLine()+"\t");
                        count += 1;
                        br.close();
                    }
                    // Datetime
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm",
                            Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());

                    db.insertULDL("Stock Take", count,
                            currentDateandTime, "upload");
                    returns = sb.toString();
//                    Log.i("custDebug", returns);
                }
                return Integer.toString(count);
            } catch (Exception e) { return e.toString(); }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null)
            {
                db.setStockTakeUploaded();
//                count -= 1;
//                if (count == 0) {
//                    finishDialog();
//                }
                getTable();
            }
            else
            {
                Toast.makeText(UploadDownload.this, "Failed", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }

    public List<JSONObject> GetStockTakeDataToUpload() {
        Log.wtf("Upload Stock Take Data Status:","Started");
        Cursor data = db.getUploadStockTake();

        List<JSONObject> jsonList = new ArrayList<JSONObject>();
        if (data.getCount() > 0) {
            while(data.moveToNext()) {
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("DocNo", data.getString(data.getColumnIndex("DocNo")));
                    jsonObject.put("DocDate", data.getString(data.getColumnIndex("DocDate")));
                    jsonObject.put("Location", data.getString(data.getColumnIndex("Location")));
                    jsonObject.put("SalesAgent", data.getString(data.getColumnIndex("SalesAgent")));
                    jsonObject.put("Remarks", data.getString(data.getColumnIndex("Remarks")));
                    jsonObject.put("CreatedTimeStamp", data.getString(data.getColumnIndex("CreatedTimeStamp")));
                    jsonObject.put("ItemCode", data.getString(data.getColumnIndex("ItemCode")));
                    jsonObject.put("Description", data.getString(data.getColumnIndex("Description")));
                    jsonObject.put("UOM", data.getString(data.getColumnIndex("UOM")));
                    jsonObject.put("Qty", data.getString(data.getColumnIndex("Qty")));
                    jsonObject.put("BatchNo", data.getString(data.getColumnIndex("BatchNo")));
                    jsonObject.put("LineNo", data.getInt(data.getColumnIndex("LineNo")));
                    jsonObject.put("CreatedUser", data.getString(data.getColumnIndex("CreatedUser")));
                    jsonObject.put("LastModifiedUser", data.getString(data.getColumnIndex("LastModifiedUser")));
                    jsonObject.put("LastModifiedDateTime", data.getString(data.getColumnIndex("LastModifiedDateTime")));

                    jsonList.add(jsonObject);
                } catch (Exception e) { Log.i("custDebug", e.getMessage()); }
            }
        }
        return jsonList;
    }

    //Upload Transfer
    class SendTransferData extends AsyncTask<String, String, String> {
        Activity context;
        ProgressDialog progressDialog;

        public SendTransferData(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(UploadDownload.this);
            progressDialog.setMessage("Upload Transfer...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String...connUrl) {
            int i;
            int count = 0;
            String returns = "";

            try {
                List<JSONObject> jsonList = GetTransferDataToUpload();
                if (jsonList != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Transfer: ");
                    for (i=0; i<jsonList.size(); i++) {
                        String send = jsonList.get(i).toString();
                        byte[] sendb = send.getBytes("UTF-8");
                        URL tUrl = new URL(connUrl[0] + "/setTransfer");
                        Log.wtf("ConnURL Transfer",connUrl[0]);
                        final HttpURLConnection httpURLConnection = (HttpURLConnection) tUrl.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setRequestProperty("Content-Type", "application/json");
                        httpURLConnection.setRequestProperty("Accept", "application/json");
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.connect();
                        httpURLConnection.getOutputStream().write(sendb);
                        httpURLConnection.getOutputStream().flush();
                        httpURLConnection.getOutputStream().close();
                        BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        sb.append(br.readLine()+"\t");
                        count += 1;
                        br.close();
                    }
                    // Datetime
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm",
                            Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());

                    db.insertULDL("Transfer", count,
                            currentDateandTime, "upload");
                    returns = sb.toString();
//                    Log.i("custDebug", returns);
                }
                return Integer.toString(count);
            } catch (Exception e) { return e.toString(); }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null)
            {
                db.setTransferUploaded();
                getTable();
            }
            else
            {
                Toast.makeText(UploadDownload.this, "Failed", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }

    public List<JSONObject> GetTransferDataToUpload() {
        Cursor data = db.getUploadTransfer();
        List<JSONObject> jsonList = new ArrayList<JSONObject>();
        if (data.getCount() > 0) {
            while(data.moveToNext()) {
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("DocNo", data.getString(data.getColumnIndex("DocNo")));
                    jsonObject.put("DocDate", data.getString(data.getColumnIndex("DocDate")));
                    jsonObject.put("Reason", data.getString(data.getColumnIndex("Reason")));
                    jsonObject.put("LocationFrom", data.getString(data.getColumnIndex("LocationFrom")));
                    jsonObject.put("LocationTo", data.getString(data.getColumnIndex("LocationTo")));
                    jsonObject.put("CreatedUser", data.getString(data.getColumnIndex("CreatedUser")));
                    jsonObject.put("ItemCode", data.getString(data.getColumnIndex("ItemCode")));
                    jsonObject.put("Description", data.getString(data.getColumnIndex("Description")));
                    jsonObject.put("Qty", data.getFloat(data.getColumnIndex("Qty")));
                    jsonObject.put("UOM", data.getString(data.getColumnIndex("UOM")));
                    jsonObject.put("BatchNo", data.getString(data.getColumnIndex("BatchNo")));
                    jsonObject.put("LastModifiedUser", data.getString(data.getColumnIndex("LastModifiedUser")));
                    jsonObject.put("LastModifiedDateTime", data.getString(data.getColumnIndex("LastModifiedDateTime")));
                    jsonList.add(jsonObject);
//                    Log.i("custDebug", jsonObject.toString());
                } catch (Exception e) { Log.i("custDebug", e.getMessage()); }
            }
        }
        return jsonList;
    }

    //Upload PackingList
    class SendPackingListData extends AsyncTask<String, String, String> {
        Activity context;
        ProgressDialog progressDialog;

        public SendPackingListData(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(UploadDownload.this);
            progressDialog.setMessage("Upload Packing List...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String...connUrl) {
            int i;
            int count = 0;
            String returns = "";

            try {
                List<JSONObject> jsonList = GetPackingListDataToUpload();
                if (jsonList != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Packing List: ");
                    for (i=0; i<jsonList.size(); i++) {
                        String send = jsonList.get(i).toString();
                        byte[] sendb = send.getBytes("UTF-8");
                        URL tUrl = new URL(connUrl[0] + "/setPackingList");
                        Log.wtf("ConnURL PackingList",connUrl[0]);
                        final HttpURLConnection httpURLConnection = (HttpURLConnection) tUrl.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setRequestProperty("Content-Type", "application/json");
                        httpURLConnection.setRequestProperty("Accept", "application/json");
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.connect();
                        httpURLConnection.getOutputStream().write(sendb);
                        httpURLConnection.getOutputStream().flush();
                        httpURLConnection.getOutputStream().close();
                        BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        sb.append(br.readLine()+"\t");
                        count += 1;
                        br.close();
                    }
                    // Datetime
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm",
                            Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());

                    db.insertULDL("Packing List", count,
                            currentDateandTime, "upload");
                    returns = sb.toString();
                }
                return Integer.toString(count);
            } catch (Exception e) { return e.toString(); }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null)
            {
                if(OnlyTallyUploaded){
                    db.setPackingListUploadedTally();
                }else{
                    db.setPackingListUploaded();
                }
                getTable();
            }
            else
            {
                Toast.makeText(UploadDownload.this, "Failed", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }

    public List<JSONObject> GetPackingListDataToUpload() {
        Cursor data = db.getUploadPackingList();

        List<JSONObject> jsonList = new ArrayList<JSONObject>();
        if (data.getCount() > 0) {
            while(data.moveToNext()) {
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("DocNo", data.getString(data.getColumnIndex("DocNo")));
                    jsonObject.put("FromDocNo", data.getString(data.getColumnIndex("FromDocNo")));
                    jsonObject.put("DocDate", data.getString(data.getColumnIndex("DocDate")));
                    jsonObject.put("DebtorCode", data.getString(data.getColumnIndex("DebtorCode")));
                    jsonObject.put("DebtorName", data.getString(data.getColumnIndex("DebtorName")));
                    jsonObject.put("SalesAgent", data.getString(data.getColumnIndex("SalesAgent")));
                    jsonObject.put("Phone", data.getString(data.getColumnIndex("Phone")));
                    jsonObject.put("Fax", data.getString(data.getColumnIndex("Fax")));
                    jsonObject.put("Attention", data.getString(data.getColumnIndex("Attention")));
                    jsonObject.put("CreatedUser", data.getString(data.getColumnIndex("CreatedUser")));
                    jsonObject.put("Location", data.getString(data.getColumnIndex("Location")));
                    jsonObject.put("ItemCode", data.getString(data.getColumnIndex("ItemCode")));
                    jsonObject.put("ItemDescription", data.getString(data.getColumnIndex("ItemDescription")));
                    jsonObject.put("Qty", data.getDouble(data.getColumnIndex("Qty")));
                    jsonObject.put("UOM", data.getString(data.getColumnIndex("UOM")));
                    jsonObject.put("LineNo", data.getInt(data.getColumnIndex("LineNo")));
                    jsonObject.put("Remark", data.getString(data.getColumnIndex("Remarks")));
                    jsonObject.put("DtlRemark", data.getString(data.getColumnIndex("DtlRemarks")));
                    jsonObject.put("BatchNo", data.getString(data.getColumnIndex("BatchNo")));
                    jsonObject.put("DocType", data.getString(data.getColumnIndex("DocType")));
                    Boolean value = data.getInt(data.getColumnIndex("IsTally")) > 0;
                    jsonObject.put("IsTally", value);
                    if (!data.isNull(data.getColumnIndex("DtlKey")))
                        jsonObject.put("MKey", data.getInt(data.getColumnIndex("DtlKey")));
                    else
                        jsonObject.put("MKey", Integer.valueOf(0));
                    jsonObject.put("LastModifiedUser", data.getString(data.getColumnIndex("LastModifiedUser")));
                    jsonObject.put("LastModifiedDateTime", data.getString(data.getColumnIndex("LastModifiedDateTime")));

                    if(OnlyTallyUploaded.equals(true)) {
                        if(value.equals(true)){
                            jsonList.add(jsonObject);
                        }
                    }else{
                        jsonList.add(jsonObject);
                    }
                } catch (Exception e) { Log.i("custDebug", e.getMessage()); }
            }
        }
        return jsonList;
    }

    private String parseDateToJSON(Date dateTime) {
        try {
            long date = Date.UTC(dateTime.getYear(), dateTime.getMonth(), dateTime.getDate(),
                    dateTime.getHours(), dateTime.getMinutes(), dateTime.getSeconds());

            return "/Date(" + date + ")/";
        } catch (Exception e) { return  null; }
    }

    class GetSalesStatus extends AsyncTask<String, Void, Integer> {
        Activity context;
        ProgressDialog pd;
        String user;
        int count = 0;

        GetSalesStatus(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setMessage("Download Sales Status...");
            //pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Integer doInBackground(String... params) {

            String urlString = params[0] + "/getSalesStatus"; // URL to call
            String response = "";

            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                Cursor debtor = db.getReg("4");
                if(debtor.moveToFirst()){
                    user = debtor.getString(0);
                }

                JSONObject root = new JSONObject();
                root.put("User", user);
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

                    for (int i = 0; i < location.length(); i++) {
                        JSONObject object = location.getJSONObject(i);
                        String DocNo = object.getString("DocNo");
                        String Status = object.getString("Status");
                        String Remark = object.getString("Remark");
                        if(Remark.equals("null")){
                            Remark = null;
                        }

                        Boolean status = db.UpdateSalesStatusIV(DocNo,Status,Remark);
                        if(status == true){
                            count++;
                        }


                    }

                } else {

                    response = "";
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            return count;
        }

        @Override
        protected void onPostExecute(Integer s) {
            super.onPostExecute(s);

            if (pd.isShowing())
                pd.dismiss();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());

            db.insertULDL("Sales Status", s, currentDateandTime, "download");
            getTable();

        }
    }

    class GetAllItemBOM extends AsyncTask<String, Void, Integer> {
        Activity context;
        ProgressDialog pd;

        GetAllItemBOM(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(context);
            pd.setMessage("Download ItemBOM...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Integer doInBackground(String... connUrl) {
            int myCount = getItemBOMData(connUrl[0],0,0);
            return myCount;
        }

        @Override
        protected void onPostExecute(Integer s) {
            super.onPostExecute(s);

            if (pd.isShowing())
                pd.dismiss();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());

            db.insertULDL("Item BOM", s, currentDateandTime, "download");
            getTable();
        }
    }

    private Integer getItemBOMData(String connUrl, int currIndex, int myCount)
    {
        HttpURLConnection conn;
        BufferedReader reader;
        try {
            final java.net.URL url = new URL(connUrl + "/getAllItemBOM");
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            // Send index to indicate no. of received chunks
            byte[] sendb = Integer.toString(currIndex).getBytes("UTF-8");
            conn.getOutputStream().write(sendb);
            conn.getOutputStream().flush();
            conn.getOutputStream().close();

            // Receive chunk of data
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            String status = sb.toString();

            if (status != null) {
                JSONArray itembom = new JSONArray(status);

                if (itembom.length() > 0) {
                    if (currIndex == 0) db.deleteItemBOM();

                    boolean insert = true;
                    for (int i = 0; i < itembom.length(); i++) {
                        JSONObject object = itembom.getJSONObject(i);
                        String ItemCode = object.getString("ItemCode");
                        String SubItemCode = object.getString("SubItemCode");
                        Double qty = object.getDouble("Qty");

                        insert = db.insertItemBOM(ItemCode, SubItemCode, qty)
                                && insert;
                        object = null;
                        myCount++;
                    }

                    if (insert) currIndex++;
                    itembom = null;

                    myCount = getItemBOMData(connUrl, currIndex, myCount);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i("custDebug", ex.getMessage());
        }
        return myCount;
    }

    //Upload PackingList
    class SendStockAssemblyData extends AsyncTask<String, String, String> {
        Activity context;
        ProgressDialog progressDialog;

        public SendStockAssemblyData(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(UploadDownload.this);
            progressDialog.setMessage("Upload Stock Assembly...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String...connUrl) {
            int i;
            int count = 0;
            String returns = "";

            try {
                List<JSONObject> jsonList = GetStockAssemblyListDataToUpload();
                if (jsonList != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Stock Assembly: ");
                    for (i=0; i<jsonList.size(); i++) {
                        String send = jsonList.get(i).toString();
                        byte[] sendb = send.getBytes("UTF-8");
                        URL tUrl = new URL(connUrl[0] + "/setStockAssembly");
                        Log.wtf("ConnURL StockAssembly",connUrl[0]);
                        final HttpURLConnection httpURLConnection = (HttpURLConnection) tUrl.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setRequestProperty("Content-Type", "application/json");
                        httpURLConnection.setRequestProperty("Accept", "application/json");
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.connect();
                        httpURLConnection.getOutputStream().write(sendb);
                        httpURLConnection.getOutputStream().flush();
                        httpURLConnection.getOutputStream().close();
                        BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        sb.append(br.readLine()+"\t");
                        count += 1;
                        br.close();
                    }
                    // Datetime
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm",
                            Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());

                    db.insertULDL("Stock Assembly", count,
                            currentDateandTime, "upload");
                    returns = sb.toString();
                }
                return Integer.toString(count);
            } catch (Exception e) { return e.toString(); }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null)
            {
                db.setSAUploaded();
                getTable();
            }
            else
            {
                Toast.makeText(UploadDownload.this, "Failed", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }

    public List<JSONObject> GetStockAssemblyListDataToUpload() {
        Cursor data = db.getUploadSAList();

        List<JSONObject> jsonList = new ArrayList<JSONObject>();
        if (data.getCount() > 0) {
            while(data.moveToNext()) {
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("DocNo", data.getString(data.getColumnIndex("DocNo")));
                    jsonObject.put("DocDate", data.getString(data.getColumnIndex("Date")));
                    jsonObject.put("Description", data.getString(data.getColumnIndex("Description")));
                    jsonObject.put("FGItemCode", data.getString(data.getColumnIndex("FGItemCode")));
                    jsonObject.put("FGQty", data.getDouble(data.getColumnIndex("FGQty")));
                    jsonObject.put("FGLocation", data.getString(data.getColumnIndex("FGLocation")));
                    jsonObject.put("FGBatchNo", data.getString(data.getColumnIndex("FGBatchNo")));
                    jsonObject.put("Remark", data.getString(data.getColumnIndex("Remark")));
                    jsonObject.put("CreatedUser", data.getString(data.getColumnIndex("CreatedUser")));
                    jsonObject.put("CreatedDateTime", data.getString(data.getColumnIndex("CreatedTimeStamp")));
                    jsonObject.put("LastModifiedUser", data.getString(data.getColumnIndex("LastModifiedUser")));
                    jsonObject.put("LastModifiedDateTime", data.getString(data.getColumnIndex("LastModifiedDateTime")));

                    jsonList.add(jsonObject);
                } catch (Exception e) { Log.i("custDebug", e.getMessage()); }
            }
        }
        return jsonList;
    }

    class SendARData extends AsyncTask<String, String, String> {
        Activity context;
        ProgressDialog progressDialog;

        public SendARData(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(UploadDownload.this);
            progressDialog.setMessage("Upload Collection...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String...connUrl) {
            int i;
            int count = 0;
            String returns = "";

            try {
                List<JSONObject> jsonList = GetARListDataToUpload();
                if (jsonList != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Collection: ");
                    for (i=0; i<jsonList.size(); i++) {
                        String send = jsonList.get(i).toString();
                        byte[] sendb = send.getBytes("UTF-8");
                        URL tUrl = new URL(connUrl[0] + "/setARPayment");
                        Log.wtf("ConnURL ARPayment",connUrl[0]);
                        final HttpURLConnection httpURLConnection = (HttpURLConnection) tUrl.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setRequestProperty("Content-Type", "application/json");
                        httpURLConnection.setRequestProperty("Accept", "application/json");
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.connect();
                        httpURLConnection.getOutputStream().write(sendb);
                        httpURLConnection.getOutputStream().flush();
                        httpURLConnection.getOutputStream().close();
                        //BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                        BufferedReader br = null;
                        if (httpURLConnection.getResponseCode() == 200) {
                            br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                            db.setARUploaded();
                        } else {
                            br = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
                        }
                        sb.append(br.readLine()+"\t");
                        count += 1;
                        br.close();
                    }
                    // Datetime
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm",
                            Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());

                    db.insertULDL("Collection", count,
                            currentDateandTime, "upload");
                    returns = sb.toString();
                }
                return Integer.toString(count);
            } catch (Exception e) { return e.toString(); }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null)
            {
                //db.setARUploaded();
                getTable();
            }
            else
            {
                Toast.makeText(UploadDownload.this, "Failed", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }

    public List<JSONObject> GetARListDataToUpload() {
        Cursor data = db.getUploadARList();

        List<JSONObject> jsonList = new ArrayList<JSONObject>();
        if (data.getCount() > 0) {
            while(data.moveToNext()) {
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("DocNo", data.getString(data.getColumnIndex("DocNo")));
                    jsonObject.put("DocDate", data.getString(data.getColumnIndex("Date")));
                    jsonObject.put("DebtorCode", data.getString(data.getColumnIndex("DebtorCode")));
                    jsonObject.put("DebtorName", data.getString(data.getColumnIndex("DebtorName")));
                    if(data.getString(data.getColumnIndex("Image")) !=null) {
                        jsonObject.put("Image", data.getString(data.getColumnIndex("Image")));
                    }
                    jsonObject.put("Amount", data.getDouble(data.getColumnIndex("Amount")));
                    jsonObject.put("Remark", data.getString(data.getColumnIndex("Remark")));
                    jsonObject.put("KODocNo", data.getString(data.getColumnIndex("DocNumber")));
                    jsonObject.put("KOAmount", data.getDouble(data.getColumnIndex("PaymentAmount")));
                    jsonObject.put("CreatedUser", data.getString(data.getColumnIndex("CreatedUser")));
                    jsonObject.put("CreatedDateTime", data.getString(data.getColumnIndex("CreatedTimeStamp")));
                    jsonObject.put("LastModifiedUser", data.getString(data.getColumnIndex("LastModifiedUser")));
                    jsonObject.put("LastModifiedDateTime", data.getString(data.getColumnIndex("LastModifiedDateTime")));

                    jsonList.add(jsonObject);
                } catch (Exception e) { Log.i("custDebug", e.getMessage()); }
            }
        }
        return jsonList;
    }

    //Upload PackingList
    class SendPurPackingListData extends AsyncTask<String, String, String> {
        Activity context;
        ProgressDialog progressDialog;

        public SendPurPackingListData(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(UploadDownload.this);
            progressDialog.setMessage("Upload Purchase Receiving...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String...connUrl) {
            int i;
            int count = 0;
            String returns = "";

            try {
                List<JSONObject> jsonList = GetPurPackingListDataToUpload();
                if (jsonList != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Purchase Receiving: ");
                    for (i=0; i<jsonList.size(); i++) {
                        String send = jsonList.get(i).toString();
                        byte[] sendb = send.getBytes("UTF-8");
                        URL tUrl = new URL(connUrl[0] + "/setPurchasePackingList");
                        Log.wtf("ConnURL PackingList",connUrl[0]);
                        final HttpURLConnection httpURLConnection = (HttpURLConnection) tUrl.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setRequestProperty("Content-Type", "application/json");
                        httpURLConnection.setRequestProperty("Accept", "application/json");
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.connect();
                        httpURLConnection.getOutputStream().write(sendb);
                        httpURLConnection.getOutputStream().flush();
                        httpURLConnection.getOutputStream().close();
                        BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        sb.append(br.readLine()+"\t");
                        count += 1;
                        br.close();
                    }
                    // Datetime
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm",
                            Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());

                    db.insertULDL("Purchase Receiving", count,
                            currentDateandTime, "upload");
                    returns = sb.toString();
                }
                return Integer.toString(count);
            } catch (Exception e) { return e.toString(); }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null)
            {
                if(OnlyTallyUploadedPR){
                    db.setPurPackingListUploadedTally();
                }else{
                    db.setPurPackingListUploaded();
                }
                getTable();
            }
            else
            {
                Toast.makeText(UploadDownload.this, "Failed", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }

    public List<JSONObject> GetPurPackingListDataToUpload() {
        Cursor data = db.getUploadPurPackingList();

        List<JSONObject> jsonList = new ArrayList<JSONObject>();
        if (data.getCount() > 0) {
            while(data.moveToNext()) {
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("DocNo", data.getString(data.getColumnIndex("DocNo")));
                    jsonObject.put("FromDocNo", data.getString(data.getColumnIndex("FromDocNo")));
                    jsonObject.put("DocDate", data.getString(data.getColumnIndex("DocDate")));
                    jsonObject.put("CreditorCode", data.getString(data.getColumnIndex("CreditorCode")));
                    jsonObject.put("CreditorName", data.getString(data.getColumnIndex("CreditorName")));
                    jsonObject.put("PurchaseAgent", data.getString(data.getColumnIndex("PurchaseAgent")));
                    jsonObject.put("Phone", data.getString(data.getColumnIndex("Phone")));
                    jsonObject.put("Fax", data.getString(data.getColumnIndex("Fax")));
                    jsonObject.put("Attention", data.getString(data.getColumnIndex("Attention")));
                    jsonObject.put("CreatedUser", data.getString(data.getColumnIndex("CreatedUser")));
                    jsonObject.put("Location", data.getString(data.getColumnIndex("Location")));
                    jsonObject.put("ItemCode", data.getString(data.getColumnIndex("ItemCode")));
                    jsonObject.put("ItemDescription", data.getString(data.getColumnIndex("ItemDescription")));
                    jsonObject.put("Qty", data.getDouble(data.getColumnIndex("Qty")));
                    jsonObject.put("UOM", data.getString(data.getColumnIndex("UOM")));
                    jsonObject.put("LineNo", data.getInt(data.getColumnIndex("LineNo")));
                    jsonObject.put("Remark", data.getString(data.getColumnIndex("Remarks")));
                    jsonObject.put("DtlRemark", data.getString(data.getColumnIndex("DtlRemarks")));
                    jsonObject.put("BatchNo", data.getString(data.getColumnIndex("BatchNo")));
                    jsonObject.put("DocType", data.getString(data.getColumnIndex("DocType")));
                    Boolean value = data.getInt(data.getColumnIndex("IsTally")) > 0;
                    jsonObject.put("IsTally", value);
                    if (!data.isNull(data.getColumnIndex("DtlKey")))
                        jsonObject.put("MKey", data.getInt(data.getColumnIndex("DtlKey")));
                    else
                        jsonObject.put("MKey", Integer.valueOf(0));
                    jsonObject.put("LastModifiedUser", data.getString(data.getColumnIndex("LastModifiedUser")));
                    jsonObject.put("LastModifiedDateTime", data.getString(data.getColumnIndex("LastModifiedDateTime")));

                    if(OnlyTallyUploadedPR.equals(true)) {
                        if(value.equals(true)){
                            jsonList.add(jsonObject);
                        }
                    }else{
                        jsonList.add(jsonObject);
                    }
                } catch (Exception e) { Log.i("custDebug", e.getMessage()); }
            }
        }
        return jsonList;
    }

    class GetProfile extends AsyncTask<String, Void, Integer> {
        Activity context;
        ProgressDialog pd;
        String user;
        int count = 0;

        GetProfile(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setMessage("Download Profile...");
            //pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Integer doInBackground(String... params) {

            String urlString = params[0] + "/getProfile"; // URL to call
            String response = "";

            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                Cursor debtor = db.getReg("8");
                if(debtor.moveToFirst()){
                    user = debtor.getString(0).toUpperCase().replace("-", "");
                }

                JSONObject root = new JSONObject();
                root.put("User", user);
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

                    for (int i = 0; i < location.length(); i++) {
                        JSONObject object = location.getJSONObject(i);
                        String CompanyLogo = object.getString("CompanyLogo");
                        //byte[] imageInByte= new BigInteger(CompanyLogo.replace("-", ""), 16).toByteArray();
                        try {
                            if (CompanyLogo != null && !CompanyLogo.equals("")) {
                                byte[] imageInByte = hexStringToByteArray(CompanyLogo.replace("-", ""));
                                db.updateREG("35", imageInByte);
                            }
                        }catch (Exception e){
                            System.out.println(e.getMessage());
                        }
                        String CompanyHeader = object.getString("CompanyHeader");
                        String Address1 = object.getString("Address1");
                        String Address2 = object.getString("Address2");
                        String Address3 = object.getString("Address3");
                        String Address4 = object.getString("Address4");
                        String Footer = object.getString("Footer");
                        db.updateREG("16", CompanyHeader + "\n" + Address1 + "\n" + Address2 + "\n" + Address3 + "\n" + Address4);
                        db.updateREG("64", Footer);
                        DocPrefix = object.getString("DocPrefix");
                        if(TerminalDevice.equals("new")) {
                            String StockCount = object.getString("StockCount");
                            db.setStockTakeANPrefix(DocPrefix + "-" + StockCount + "-");
                            String Sales = object.getString("Sales");
                            db.setANPrefix(DocPrefix + "-" + Sales + "-");
                            String PackingList = object.getString("PackingList");
                            db.setPLANPrefix(DocPrefix + "-" + PackingList + "-");
                            String PurchasePackingList = object.getString("PurchasePackingList");
                            db.setPPLANPrefix(DocPrefix + "-" + PurchasePackingList + "-");
                            String Purchase = object.getString("Purchase");
                            db.setPurchaseANPrefix(DocPrefix + "-" + Purchase + "-");
                            String Transfer = object.getString("Transfer");
                            db.setTransferANPrefix(DocPrefix + "-" + Transfer + "-");
                            String Collection = object.getString("Collection");
                            db.setARANPrefix(DocPrefix + "-" + Collection + "-");
                            String StockAssembly = object.getString("StockAssembly");
                            db.setSAANPrefix(DocPrefix + "-" + StockAssembly + "-");
                        }
                        db.updateREG("55", DocPrefix);
                        Boolean EnableTax = object.getBoolean("EnableTax");
                        db.updateREG("22", EnableTax.toString());
                        String DefaultTaxType = object.getString("DefaultTaxType");
                        db.updateREG("21", DefaultTaxType);
                        Boolean TaxInclusive = object.getBoolean("TaxInclusive");
                        db.updateREG("13", TaxInclusive.toString());
                        Boolean EnableBarcode = object.getBoolean("EnableBarcode");
                        db.updateREG("25", EnableBarcode.toString());
                        String BarcodeTotalLength = object.getString("BarcodeTotalLength");
                        db.updateREG("26", BarcodeTotalLength);
                        String ItemCodeStartDigit = object.getString("ItemCodeStartDigit");
                        db.updateREG("27", ItemCodeStartDigit);
                        String ItemCodeLength = object.getString("ItemCodeLength");
                        db.updateREG("28", ItemCodeLength);
                        String QtyStartDigit = object.getString("QtyStartDigit");
                        db.updateREG("29", QtyStartDigit);
                        String QtyLength = object.getString("QtyLength");
                        db.updateREG("30", QtyLength);
                        String QtyDecimal = object.getString("QtyDecimal");
                        db.updateREG("31", QtyDecimal);
                        String DtlRemarkStartDigit = object.getString("DtlRemarkStartDigit");
                        db.updateREG("51", DtlRemarkStartDigit);
                        String DtlRemarkLength = object.getString("DtlRemarkLength");
                        db.updateREG("52", DtlRemarkLength);
                        String DtlRemark2StartDigit = object.getString("DtlRemark2StartDigit");
                        db.updateREG("53", DtlRemark2StartDigit);
                        String DtlRemark2Length = object.getString("DtlRemark2Length");
                        db.updateREG("54", DtlRemark2Length);
                        Boolean EnableRFID = object.getBoolean("EnableRFID");
                        if(EnableRFID == true){
                           db.editPermissionGranted("RFID Permission");
                        }else
                        {
                            db.editPermissionDeny("RFID Permission");
                        }
                        Boolean EnableAutoPrice = object.getBoolean("EnableAutoPrice");
                        db.updateREG("20", String.valueOf(EnableAutoPrice));
                        Boolean EnableHybridMode = object.getBoolean("EnableHybridMode");
                        db.updateREG("32", String.valueOf(EnableHybridMode));
                        String HistoryDay = object.getString("HistoryDay");
                        db.updateREG("33", HistoryDay);
                        Boolean EnableBatchNo = object.getBoolean("EnableBatchNo");
                        db.updateREG("38", String.valueOf(EnableBatchNo));
                        String BatchNoType = object.getString("BatchNoType");
                        db.updateREG("37", BatchNoType);
                        Boolean EnableBatchPurchase = object.getBoolean("EnableBatchPurchase");
                        db.updateREG("40", String.valueOf(EnableBatchPurchase));
                        Boolean EnableBatchSales = object.getBoolean("EnableBatchSales");
                        db.updateREG("39", String.valueOf(EnableBatchSales));
                        String SalesOrderType = object.getString("SalesOrderType");
                        db.updateREG("41", SalesOrderType);
                        Boolean EnableNegInventory = object.getBoolean("EnableNegInventory");
                        db.updateREG("42",String.valueOf(EnableNegInventory));
                        Boolean EnableOnlyTally = object.getBoolean("EnableOnlyTally");
                        db.updateREG("43", String.valueOf(EnableOnlyTally));
                        Boolean EnableBatchCompare = object.getBoolean("EnableBatchCompare");
                        db.updateREG("44", String.valueOf(EnableBatchCompare));
                        Boolean EnableLocationCompare = object.getBoolean("EnableLocationCompare");
                        db.updateREG("49", String.valueOf(EnableLocationCompare));
                        Boolean FilterbyAgent = object.getBoolean("FilterbyAgent");
                        db.updateREG("14", String.valueOf(boolToInt(FilterbyAgent)));
                        String AgentList = object.getString("AgentList");
                        db.updateREG("56", AgentList);
                        Boolean FilterbyItemGroup = object.getBoolean("FilterbyItemGroup");
                        db.updateREG("58", String.valueOf(boolToInt(FilterbyItemGroup)));
                        String ItemGroupList = object.getString("ItemGroupList");
                        db.updateREG("59", ItemGroupList);
                        Boolean FilterbyItemType = object.getBoolean("FilterbyItemType");
                        db.updateREG("60", String.valueOf(boolToInt(FilterbyItemType)));
                        String ItemTypeList = object.getString("ItemTypeList");
                        db.updateREG("61", ItemTypeList);


                        String SCLength = object.getString("SCLength");
                        db.setStockTakeANLength(SCLength);
                        String SalesLength = object.getString("SalesLength");
                        db.setANLength(SalesLength);
                        String PLLength = object.getString("PLLength");
                        db.setPLANLength(PLLength);
                        String PPLLength = object.getString("PPLLength");
                        db.setPPLANLength(PPLLength);
                        String PURLength = object.getString("PURLength");
                        db.setPurchaseANLength(PURLength);
                        String TRLength = object.getString("TRLength");
                        db.setTransferANLength(TRLength);
                        String ARLength = object.getString("ARLength");
                        db.setARANLength(ARLength);
                        String SALength = object.getString("SALength");
                        db.setSAANLength(SALength);

                    }

                } else {

                    response = "";
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            return count;
        }

        @Override
        protected void onPostExecute(Integer s) {
            super.onPostExecute(s);

            if (pd.isShowing())
                pd.dismiss();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());

            db.insertULDL("Profile", 1, currentDateandTime, "download");
            getTable();


        }
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public int boolToInt(boolean b) {
        return b ? 1 : 0;
    }

    class GetMobileRN extends AsyncTask<String, Void, Integer> {
        Activity context;
        ProgressDialog pd;
        String user;
        int count = 0;

        GetMobileRN(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setMessage("Download MobileRN...");
            //pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Integer doInBackground(String... params) {

            String urlString = params[0] + "/getMobileRN"; // URL to call
            String response = "";

            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                Cursor debtor = db.getReg("8");
                if(debtor.moveToFirst()){
                    user = debtor.getString(0).toUpperCase().replace("-", "");
                }
                JSONObject root = new JSONObject();
                root.put("ID", user);
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

                    for (int i = 0; i < location.length(); i++) {
                        JSONObject object = location.getJSONObject(i);
                        DocPrefix = object.getString("MobileRN");
                        db.updateREG("55", DocPrefix);
                    }

                } else {

                    response = "";
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            return count;
        }

        @Override
        protected void onPostExecute(Integer s) {
            super.onPostExecute(s);
            if (pd.isShowing())
                pd.dismiss();
            new GetProfile(UploadDownload.this).execute(url);
        }
    }

    class CheckPurchaseData extends AsyncTask<String, String, String> {
        Activity context;
        ProgressDialog progressDialog;

        public CheckPurchaseData(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(UploadDownload.this);
            progressDialog.setMessage("Upload Purchase...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... connUrl) {
            int i;
            int count = 0;
            String returns = "";

            try {
                List<JSONObject> jsonList = GetchkPurchaseDataToUpload();
                if (jsonList != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Invoice: ");
                    for (i = 0; i < jsonList.size(); i++) {
                        String send = jsonList.get(i).toString();
                        byte[] sendb = send.getBytes("UTF-8");
                        URL tUrl = new URL(connUrl[0] + "/checkPurchaseDup");
                        final HttpURLConnection httpURLConnection = (HttpURLConnection) tUrl.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setRequestProperty("Content-Type", "application/json");
                        httpURLConnection.setRequestProperty("Accept", "application/json");
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.connect();
                        httpURLConnection.getOutputStream().write(sendb);
                        httpURLConnection.getOutputStream().flush();
                        httpURLConnection.getOutputStream().close();
                        BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        sb.append(br.readLine() + "\t");
                        count += 1;
                        br.close();
                    }

                }
                return Integer.toString(count);
            } catch (Exception e) {
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                new SendPurchaseData(UploadDownload.this).execute(url);
            } else {
                Toast.makeText(UploadDownload.this, "Failed", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }

    public List<JSONObject> GetchkPurchaseDataToUpload() {
        Cursor data = db.getUploadchkPurchase();
        List<JSONObject> jsonList = new ArrayList<JSONObject>();
        if (data.getCount() > 0) {
            while (data.moveToNext()) {
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("DocNo", data.getString(data.getColumnIndex("DocNo")));
                    jsonList.add(jsonObject);
//                    Log.i("custDebug", jsonObject.toString());
                } catch (Exception e) {
                    Log.i("custDebug", e.getMessage());
                }
            }
        }
        return jsonList;
    }


    class CheckARPaymentData extends AsyncTask<String, String, String> {
        Activity context;
        ProgressDialog progressDialog;

        public CheckARPaymentData(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(UploadDownload.this);
            progressDialog.setMessage("Upload ARPayment...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... connUrl) {
            int i;
            int count = 0;
            String returns = "";

            try {
                List<JSONObject> jsonList = GetchkARPaymentDataToUpload();
                if (jsonList != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Invoice: ");
                    for (i = 0; i < jsonList.size(); i++) {
                        String send = jsonList.get(i).toString();
                        byte[] sendb = send.getBytes("UTF-8");
                        URL tUrl = new URL(connUrl[0] + "/checkARPaymentDup");
                        final HttpURLConnection httpURLConnection = (HttpURLConnection) tUrl.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setRequestProperty("Content-Type", "application/json");
                        httpURLConnection.setRequestProperty("Accept", "application/json");
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.connect();
                        httpURLConnection.getOutputStream().write(sendb);
                        httpURLConnection.getOutputStream().flush();
                        httpURLConnection.getOutputStream().close();
                        BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        sb.append(br.readLine() + "\t");
                        count += 1;
                        br.close();
                    }

                }
                return Integer.toString(count);
            } catch (Exception e) {
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                new SendARData(UploadDownload.this).execute(url);
            } else {
                Toast.makeText(UploadDownload.this, "Failed", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }

    public List<JSONObject> GetchkARPaymentDataToUpload() {
        Cursor data = db.getUploadchkARPayment();
        List<JSONObject> jsonList = new ArrayList<JSONObject>();
        if (data.getCount() > 0) {
            while (data.moveToNext()) {
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("DocNo", data.getString(data.getColumnIndex("DocNo")));
                    jsonList.add(jsonObject);
//                    Log.i("custDebug", jsonObject.toString());
                } catch (Exception e) {
                    Log.i("custDebug", e.getMessage());
                }
            }
        }
        return jsonList;
    }

    class CheckPackingListData extends AsyncTask<String, String, String> {
        Activity context;
        ProgressDialog progressDialog;

        public CheckPackingListData(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(UploadDownload.this);
            progressDialog.setMessage("Upload PackingList...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... connUrl) {
            int i;
            int count = 0;
            String returns = "";

            try {
                List<JSONObject> jsonList = GetchkPLDataToUpload();
                if (jsonList != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Invoice: ");
                    for (i = 0; i < jsonList.size(); i++) {
                        String send = jsonList.get(i).toString();
                        byte[] sendb = send.getBytes("UTF-8");
                        URL tUrl = new URL(connUrl[0] + "/checkPLDup");
                        final HttpURLConnection httpURLConnection = (HttpURLConnection) tUrl.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setRequestProperty("Content-Type", "application/json");
                        httpURLConnection.setRequestProperty("Accept", "application/json");
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.connect();
                        httpURLConnection.getOutputStream().write(sendb);
                        httpURLConnection.getOutputStream().flush();
                        httpURLConnection.getOutputStream().close();
                        BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        sb.append(br.readLine() + "\t");
                        count += 1;
                        br.close();
                    }

                }
                return Integer.toString(count);
            } catch (Exception e) {
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                new SendPackingListData(UploadDownload.this).execute(url);
            } else {
                Toast.makeText(UploadDownload.this, "Failed", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }

    public List<JSONObject> GetchkPLDataToUpload() {
        Cursor data = db.getUploadchkPL();
        List<JSONObject> jsonList = new ArrayList<JSONObject>();
        if (data.getCount() > 0) {
            while (data.moveToNext()) {
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("DocNo", data.getString(data.getColumnIndex("DocNo")));
                    jsonList.add(jsonObject);
//                    Log.i("custDebug", jsonObject.toString());
                } catch (Exception e) {
                    Log.i("custDebug", e.getMessage());
                }
            }
        }
        return jsonList;
    }

    class CheckPurchasePLData extends AsyncTask<String, String, String> {
        Activity context;
        ProgressDialog progressDialog;

        public CheckPurchasePLData(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(UploadDownload.this);
            progressDialog.setMessage("Upload PurchasePL...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... connUrl) {
            int i;
            int count = 0;
            String returns = "";

            try {
                List<JSONObject> jsonList = GetchkPPLDataToUpload();
                if (jsonList != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Invoice: ");
                    for (i = 0; i < jsonList.size(); i++) {
                        String send = jsonList.get(i).toString();
                        byte[] sendb = send.getBytes("UTF-8");
                        URL tUrl = new URL(connUrl[0] + "/checkPPLDup");
                        final HttpURLConnection httpURLConnection = (HttpURLConnection) tUrl.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setRequestProperty("Content-Type", "application/json");
                        httpURLConnection.setRequestProperty("Accept", "application/json");
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.connect();
                        httpURLConnection.getOutputStream().write(sendb);
                        httpURLConnection.getOutputStream().flush();
                        httpURLConnection.getOutputStream().close();
                        BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        sb.append(br.readLine() + "\t");
                        count += 1;
                        br.close();
                    }

                }
                return Integer.toString(count);
            } catch (Exception e) {
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                new SendPurPackingListData(UploadDownload.this).execute(url);
            } else {
                Toast.makeText(UploadDownload.this, "Failed", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }

    public List<JSONObject> GetchkPPLDataToUpload() {
        Cursor data = db.getUploadchkPPL();
        List<JSONObject> jsonList = new ArrayList<JSONObject>();
        if (data.getCount() > 0) {
            while (data.moveToNext()) {
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("DocNo", data.getString(data.getColumnIndex("DocNo")));
                    jsonList.add(jsonObject);
//                    Log.i("custDebug", jsonObject.toString());
                } catch (Exception e) {
                    Log.i("custDebug", e.getMessage());
                }
            }
        }
        return jsonList;
    }

    class CheckStockAssemblyData extends AsyncTask<String, String, String> {
        Activity context;
        ProgressDialog progressDialog;

        public CheckStockAssemblyData(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(UploadDownload.this);
            progressDialog.setMessage("Upload Stock Assembly...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... connUrl) {
            int i;
            int count = 0;
            String returns = "";

            try {
                List<JSONObject> jsonList = GetchkStockAssemblyDataToUpload();
                if (jsonList != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Invoice: ");
                    for (i = 0; i < jsonList.size(); i++) {
                        String send = jsonList.get(i).toString();
                        byte[] sendb = send.getBytes("UTF-8");
                        URL tUrl = new URL(connUrl[0] + "/checkStockAssemblyDup");
                        final HttpURLConnection httpURLConnection = (HttpURLConnection) tUrl.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setRequestProperty("Content-Type", "application/json");
                        httpURLConnection.setRequestProperty("Accept", "application/json");
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.connect();
                        httpURLConnection.getOutputStream().write(sendb);
                        httpURLConnection.getOutputStream().flush();
                        httpURLConnection.getOutputStream().close();
                        BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        sb.append(br.readLine() + "\t");
                        count += 1;
                        br.close();
                    }

                }
                return Integer.toString(count);
            } catch (Exception e) {
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                new SendStockAssemblyData(UploadDownload.this).execute(url);
            } else {
                Toast.makeText(UploadDownload.this, "Failed", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }

    public List<JSONObject> GetchkStockAssemblyDataToUpload() {
        Cursor data = db.getUploadchkStockAssembly();
        List<JSONObject> jsonList = new ArrayList<JSONObject>();
        if (data.getCount() > 0) {
            while (data.moveToNext()) {
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("DocNo", data.getString(data.getColumnIndex("DocNo")));
                    jsonList.add(jsonObject);
//                    Log.i("custDebug", jsonObject.toString());
                } catch (Exception e) {
                    Log.i("custDebug", e.getMessage());
                }
            }
        }
        return jsonList;
    }

    class CheckStockTakeData extends AsyncTask<String, String, String> {
        Activity context;
        ProgressDialog progressDialog;

        public CheckStockTakeData(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(UploadDownload.this);
            progressDialog.setMessage("Upload Stock Take...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... connUrl) {
            int i;
            int count = 0;
            String returns = "";

            try {
                List<JSONObject> jsonList = GetchkStockTakeDataToUpload();
                if (jsonList != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Invoice: ");
                    for (i = 0; i < jsonList.size(); i++) {
                        String send = jsonList.get(i).toString();
                        byte[] sendb = send.getBytes("UTF-8");
                        URL tUrl = new URL(connUrl[0] + "/checkStockTakeDup");
                        final HttpURLConnection httpURLConnection = (HttpURLConnection) tUrl.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setRequestProperty("Content-Type", "application/json");
                        httpURLConnection.setRequestProperty("Accept", "application/json");
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.connect();
                        httpURLConnection.getOutputStream().write(sendb);
                        httpURLConnection.getOutputStream().flush();
                        httpURLConnection.getOutputStream().close();
                        BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        sb.append(br.readLine() + "\t");
                        count += 1;
                        br.close();
                    }

                }
                return Integer.toString(count);
            } catch (Exception e) {
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                new SendStockTakeData(UploadDownload.this).execute(url);
            } else {
                Toast.makeText(UploadDownload.this, "Failed", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }

    public List<JSONObject> GetchkStockTakeDataToUpload() {
        Cursor data = db.getUploadchkStockTake();
        List<JSONObject> jsonList = new ArrayList<JSONObject>();
        if (data.getCount() > 0) {
            while (data.moveToNext()) {
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("DocNo", data.getString(data.getColumnIndex("DocNo")));
                    jsonList.add(jsonObject);
//                    Log.i("custDebug", jsonObject.toString());
                } catch (Exception e) {
                    Log.i("custDebug", e.getMessage());
                }
            }
        }
        return jsonList;
    }

    //Upload Stock Assembly
    class CheckStockTransferData extends AsyncTask<String, String, String> {
        Activity context;
        ProgressDialog progressDialog;

        public CheckStockTransferData(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(UploadDownload.this);
            progressDialog.setMessage("Upload Stock Assembly...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... connUrl) {
            int i;
            int count = 0;
            String returns = "";

            try {
                List<JSONObject> jsonList = GetchkTransferDataToUpload();
                if (jsonList != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Invoice: ");
                    for (i = 0; i < jsonList.size(); i++) {
                        String send = jsonList.get(i).toString();
                        byte[] sendb = send.getBytes("UTF-8");
                        URL tUrl = new URL(connUrl[0] + "/checkTransferDup");
                        final HttpURLConnection httpURLConnection = (HttpURLConnection) tUrl.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setRequestProperty("Content-Type", "application/json");
                        httpURLConnection.setRequestProperty("Accept", "application/json");
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.connect();
                        httpURLConnection.getOutputStream().write(sendb);
                        httpURLConnection.getOutputStream().flush();
                        httpURLConnection.getOutputStream().close();
                        BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        sb.append(br.readLine() + "\t");
                        count += 1;
                        br.close();
                    }

                }
                return Integer.toString(count);
            } catch (Exception e) {
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                new SendTransferData(UploadDownload.this).execute(url);
            } else {
                Toast.makeText(UploadDownload.this, "Failed", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }

    public List<JSONObject> GetchkTransferDataToUpload() {
        Cursor data = db.getUploadchkTransfer();
        List<JSONObject> jsonList = new ArrayList<JSONObject>();
        if (data.getCount() > 0) {
            while (data.moveToNext()) {
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("DocNo", data.getString(data.getColumnIndex("DocNo")));
                    jsonList.add(jsonObject);
//                    Log.i("custDebug", jsonObject.toString());
                } catch (Exception e) {
                    Log.i("custDebug", e.getMessage());
                }
            }
        }
        return jsonList;
    }

    //Download Up To Date Cost
    class GetUTDCost extends AsyncTask<String, Void, Integer> {
        Activity context;
        ProgressDialog pd;

        GetUTDCost(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(context);
            pd.setMessage("Download UTDCost...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Integer doInBackground(String... connUrl) {
            int myCount = getUTDCostData(connUrl[0],0,0);
            return myCount;
        }

        @Override
        protected void onPostExecute(Integer s) {
            super.onPostExecute(s);

            if (pd.isShowing())
                pd.dismiss();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());

            db.insertULDL("UTDCost", s, currentDateandTime, "download");
            getTable();
        }
    }

    private Integer getUTDCostData(String connUrl, int currIndex, int myCount)
    {
        HttpURLConnection conn;
        BufferedReader reader;
        try {
            final java.net.URL url = new URL(connUrl + "/getUTDCost");
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            // Send index to indicate no. of received chunks
            byte[] sendb = Integer.toString(currIndex).getBytes("UTF-8");
            conn.getOutputStream().write(sendb);
            conn.getOutputStream().flush();
            conn.getOutputStream().close();

            // Receive chunk of data
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            String status = sb.toString();

            if (status != null) {
                JSONArray itemcost = new JSONArray(status);

                if (itemcost.length() > 0) {
                    if (currIndex == 0) db.deleteUTDCost();


                    boolean insert = true;
                    for (int i = 0; i < itemcost.length(); i++) {
                        JSONObject object = itemcost.getJSONObject(i);
                        String ItemCode = object.getString("ItemCode");
                        String BatchNo = object.getString("BatchNo");
                        String UOM = object.getString("UOM");
                        String Location = object.getString("Location");
                        Double UTDQty = object.getDouble("UTDQty");
                        Double UTDCost = object.getDouble("UTDCost");



                        insert = db.insertUTDCost(ItemCode, BatchNo, UOM, Location, UTDQty, UTDCost)
                                && insert;
                        object = null;
                        myCount++;
                    }

                    if (insert) currIndex++;
                    itemcost = null;

                    myCount = getUTDCostData(connUrl, currIndex, myCount);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i("custDebug", ex.getMessage());
        }
        return myCount;
    }

    //Upload Job Sheet
    class CheckJobSheetData extends AsyncTask<String, String, String> {
        Activity context;
        ProgressDialog progressDialog;

        public CheckJobSheetData(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(UploadDownload.this);
            progressDialog.setMessage("Upload Job Sheet...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... connUrl) {
            int i;
            int count = 0;
            String returns = "";

            try {
                List<JSONObject> jsonList = GetchkJobSheetDataToUpload();
                if (jsonList != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("JobSheet: ");
                    for (i = 0; i < jsonList.size(); i++) {
                        String send = jsonList.get(i).toString();
                        byte[] sendb = send.getBytes("UTF-8");
                        URL tUrl = new URL(connUrl[0] + "/checkJobSheetDup");
                        final HttpURLConnection httpURLConnection = (HttpURLConnection) tUrl.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setRequestProperty("Content-Type", "application/json");
                        httpURLConnection.setRequestProperty("Accept", "application/json");
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.connect();
                        httpURLConnection.getOutputStream().write(sendb);
                        httpURLConnection.getOutputStream().flush();
                        httpURLConnection.getOutputStream().close();
                        BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        sb.append(br.readLine() + "\t");
                        count += 1;
                        br.close();
                    }

                }
                return Integer.toString(count);
            } catch (Exception e) {
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                new SendJobSheetData(UploadDownload.this).execute(url);
            } else {
                Toast.makeText(UploadDownload.this, "Failed", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }

    public List<JSONObject> GetchkJobSheetDataToUpload() {
        Cursor data = db.getUploadchkJobSheet();
        List<JSONObject> jsonList = new ArrayList<JSONObject>();
        if (data.getCount() > 0) {
            while (data.moveToNext()) {
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("DocNo", data.getString(data.getColumnIndex("DocNo")));
                    jsonList.add(jsonObject);
                } catch (Exception e) {
                    Log.i("custDebug", e.getMessage());
                }
            }
        }
        return jsonList;
    }

    class SendJobSheetData extends AsyncTask<String, String, String> {
        Activity context;
        ProgressDialog progressDialog;

        public SendJobSheetData(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(UploadDownload.this);
            progressDialog.setMessage("Upload Job Sheet...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String...connUrl) {
            int i;
            int count = 0;
            String returns = "";

            try {
                List<JSONObject> jsonList = GetJobSheetDataToUpload();
                if (jsonList != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("JobSheet: ");
                    for (i=0; i<jsonList.size(); i++) {
                        String send = jsonList.get(i).toString();
                        byte[] sendb = send.getBytes("UTF-8");
                        URL tUrl = new URL(connUrl[0] + "/setJobSheet");
                        Log.wtf("ConnURL Job Sheet",connUrl[0]);
                        final HttpURLConnection httpURLConnection = (HttpURLConnection) tUrl.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setRequestProperty("Content-Type", "application/json");
                        httpURLConnection.setRequestProperty("Accept", "application/json");
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.connect();
                        httpURLConnection.getOutputStream().write(sendb);
                        httpURLConnection.getOutputStream().flush();
                        httpURLConnection.getOutputStream().close();
                        BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        sb.append(br.readLine()+"\t");
                        count += 1;
                        br.close();
                    }
                    // Datetime
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm",
                            Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());

                    db.insertULDL("JobSheet", count,
                            currentDateandTime, "upload");
                    returns = sb.toString();
                } else {
                    Log.d("Check789", "jsonList2 is null");
                }
                return Integer.toString(count);
            } catch (Exception e) { return e.toString(); }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null)
            {
                db.setJobSheetUploaded();
                getTable();
            }
            else
            {
                Toast.makeText(UploadDownload.this, "Failed", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }

    public List<JSONObject> GetJobSheetDataToUpload() {
        Cursor data = db.getUploadJobSheet();
        List<JSONObject> jsonList = new ArrayList<JSONObject>();
        if (data.getCount() > 0) {
            while(data.moveToNext()) {
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("DocNo", data.getString(data.getColumnIndex("DocNo")));
                    jsonObject.put("DocDate", data.getString(data.getColumnIndex("DocDate")));
                    jsonObject.put("DocType", data.getString(data.getColumnIndex("DocType")));
                    jsonObject.put("DebtorCode", data.getString(data.getColumnIndex("DebtorCode")));
                    jsonObject.put("DebtorName", data.getString(data.getColumnIndex("DebtorName")));
                    jsonObject.put("DebtorName2", data.getString(data.getColumnIndex("DebtorName2")));
                    jsonObject.put("SalesAgent", data.getString(data.getColumnIndex("SalesAgent")));
                    jsonObject.put("Phone", data.getString(data.getColumnIndex("Phone")));
                    jsonObject.put("Fax", data.getString(data.getColumnIndex("Fax")));
                    jsonObject.put("Attention", data.getString(data.getColumnIndex("Attention")));
                    jsonObject.put("Address1", data.getString(data.getColumnIndex("Address1")));
                    jsonObject.put("Address2", data.getString(data.getColumnIndex("Address2")));
                    jsonObject.put("Address3", data.getString(data.getColumnIndex("Address3")));
                    jsonObject.put("Address4", data.getString(data.getColumnIndex("Address4")));
                    jsonObject.put("Remark", data.getString(data.getColumnIndex("Remarks")));
                    jsonObject.put("Remark2", data.getString(data.getColumnIndex("Remarks2")));
                    jsonObject.put("Remark3", data.getString(data.getColumnIndex("Remarks3")));
                    jsonObject.put("Remark4", data.getString(data.getColumnIndex("Remarks4")));
                    jsonObject.put("WorkType", data.getString(data.getColumnIndex("WorkType")));
                    jsonObject.put("ReplacementType", data.getString(data.getColumnIndex("ReplacementType")));
                    jsonObject.put("TimeIn", data.getString(data.getColumnIndex("TimeIn")));
                    jsonObject.put("TimeOut", data.getString(data.getColumnIndex("TimeOut")));
                    jsonObject.put("Problem", data.getString(data.getColumnIndex("Problem")));
                    jsonObject.put("Resolution", data.getString(data.getColumnIndex("Resolution")));
                    jsonObject.put("JobSheetRemarks", data.getString(data.getColumnIndex("JobSheetRemarks")));
                    jsonObject.put("SalesNo", data.getString(data.getColumnIndex("SalesNo")));
                    jsonObject.put("CreatedUser", data.getString(data.getColumnIndex("CreatedUser")));
                    jsonObject.put("CreatedDateTime", data.getString(data.getColumnIndex("CreatedTimeStamp")));
                    jsonObject.put("LastModifiedUser", data.getString(data.getColumnIndex("LastModifiedUser")));
                    jsonObject.put("LastModifiedDateTime", data.getString(data.getColumnIndex("LastModifiedDateTime")));
                    if(data.getString(data.getColumnIndex("Signature")) !=null) {
                        jsonObject.put("Signature", data.getString(data.getColumnIndex("Signature")));
                    }
                    if(data.getString(data.getColumnIndex("Image")) !=null) {
                        jsonObject.put("Image", data.getString(data.getColumnIndex("Image")));
                    }
                    jsonObject.put("ItemCode", data.getString(data.getColumnIndex("ItemCode")));
                    jsonObject.put("ItemDescription", data.getString(data.getColumnIndex("ItemDescription")));
                    jsonObject.put("Location", data.getString(data.getColumnIndex("Location")));
                    jsonObject.put("Qty", data.getFloat(data.getColumnIndex("Quantity")));
                    jsonObject.put("UOM", data.getString(data.getColumnIndex("UOM")));
                    jsonObject.put("UPrice", data.getFloat(data.getColumnIndex("UPrice")));
                    jsonObject.put("SubTotal", data.getFloat(data.getColumnIndex("SubTotal")));
                    jsonObject.put("Discount", data.getFloat(data.getColumnIndex("Discount")));
                    jsonObject.put("TaxType", data.getString(data.getColumnIndex("TaxType")));
                    jsonObject.put("TaxRate", data.getFloat(data.getColumnIndex("TaxRate")));
                    jsonObject.put("TaxValue", data.getFloat(data.getColumnIndex("TaxValue")));
                    jsonObject.put("TotalEx", data.getFloat(data.getColumnIndex("TotalEx")));
                    jsonObject.put("TotalIn", data.getFloat(data.getColumnIndex("TotalIn")));
                    jsonObject.put("LineNo", data.getInt(data.getColumnIndex("LineNo")));
                    jsonObject.put("DtlRemark", data.getString(data.getColumnIndex("DtlRemarks")));
                    jsonObject.put("DtlRemark2", data.getString(data.getColumnIndex("DtlRemarks2")));
                    jsonObject.put("BatchNo", data.getString(data.getColumnIndex("BatchNo")));

                    jsonList.add(jsonObject);
                } catch (Exception e) {
                    Log.i("custDebug", e.getMessage());
                }
            }
        }
        return jsonList;
    }

    //Upload Stock Receive
    class CheckStockReceiveData extends AsyncTask<String, String, String> {
        Activity context;
        ProgressDialog progressDialog;

        public CheckStockReceiveData(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(UploadDownload.this);
            progressDialog.setMessage("Upload StockReceive...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... connUrl) {
            int i;
            int count = 0;
            String returns = "";

            try {
                List<JSONObject> jsonList = GetchkStockReceiveDataToUpload();
                if (jsonList != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("StockReceive: ");
                    for (i = 0; i < jsonList.size(); i++) {
                        String send = jsonList.get(i).toString();
                        byte[] sendb = send.getBytes("UTF-8");
                        URL tUrl = new URL(connUrl[0] + "/checkStockReceiveDup");
                        final HttpURLConnection httpURLConnection = (HttpURLConnection) tUrl.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setRequestProperty("Content-Type", "application/json");
                        httpURLConnection.setRequestProperty("Accept", "application/json");
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.connect();
                        httpURLConnection.getOutputStream().write(sendb);
                        httpURLConnection.getOutputStream().flush();
                        httpURLConnection.getOutputStream().close();
                        BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        sb.append(br.readLine() + "\t");
                        count += 1;
                        br.close();
                    }

                }
                return Integer.toString(count);
            } catch (Exception e) {
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                new SendStockReceiveData(UploadDownload.this).execute(url);
            } else {
                Toast.makeText(UploadDownload.this, "Failed", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }

    public List<JSONObject> GetchkStockReceiveDataToUpload() {
        Cursor data = db.getUploadchkStockReceive();
        List<JSONObject> jsonList = new ArrayList<JSONObject>();
        if (data.getCount() > 0) {
            while (data.moveToNext()) {
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("DocNo", data.getString(data.getColumnIndex("DocNo")));
                    jsonList.add(jsonObject);
//                    Log.i("custDebug", jsonObject.toString());
                } catch (Exception e) {
                    Log.i("custDebug", e.getMessage());
                }
            }
        }
        return jsonList;
    }

    class SendStockReceiveData extends AsyncTask<String, String, String> {
        Activity context;
        ProgressDialog progressDialog;

        public SendStockReceiveData(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(UploadDownload.this);
            progressDialog.setMessage("Upload Stock Receive...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String...connUrl) {
            int i;
            int count = 0;
            String returns = "";

            try {
                List<JSONObject> jsonList = GetStockReceiveDataToUpload();
                if (jsonList != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Stock Receiving: ");
                    for (i=0; i<jsonList.size(); i++) {
                        String send = jsonList.get(i).toString();
                        byte[] sendb = send.getBytes("UTF-8");
                        URL tUrl = new URL(connUrl[0] + "/setStockReceive");
                        Log.wtf("ConnURL StockReceiveList",connUrl[0]);
                        final HttpURLConnection httpURLConnection = (HttpURLConnection) tUrl.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setRequestProperty("Content-Type", "application/json");
                        httpURLConnection.setRequestProperty("Accept", "application/json");
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.connect();
                        httpURLConnection.getOutputStream().write(sendb);
                        httpURLConnection.getOutputStream().flush();
                        httpURLConnection.getOutputStream().close();
                        BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        sb.append(br.readLine()+"\t");
                        count += 1;
                        br.close();
                    }
                    // Datetime
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm",
                            Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());

                    db.insertULDL("Stock Receive", count,
                            currentDateandTime, "upload");
                    returns = sb.toString();
                }

                return Integer.toString(count);
            } catch (Exception e) { return e.toString(); }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null)
            {
                db.setStockReceiveUploaded();
                getTable();
            }
            else
            {
                Toast.makeText(UploadDownload.this, "Failed", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }

    public List<JSONObject> GetStockReceiveDataToUpload() {
        Cursor data = db.getUploadStockReceive();

        List<JSONObject> jsonList = new ArrayList<JSONObject>();

        if (data.getCount() > 0) {
            while(data.moveToNext()) {
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("DocNo", data.getString(data.getColumnIndex("DocNo")));
                    jsonObject.put("DocDate", data.getString(data.getColumnIndex("DocDate")));
                    jsonObject.put("Description", data.getString(data.getColumnIndex("Description")));
                    jsonObject.put("Remarks", data.getString(data.getColumnIndex("Remarks")));
                    jsonObject.put("CreatedUser", data.getString(data.getColumnIndex("CreatedUser")));
                    jsonObject.put("Location", data.getString(data.getColumnIndex("Location")));
                    jsonObject.put("ItemCode", data.getString(data.getColumnIndex("ItemCode")));
                    jsonObject.put("ItemDescription", data.getString(data.getColumnIndex("ItemDescription")));
                    jsonObject.put("Qty", data.getDouble(data.getColumnIndex("Qty")));
                    jsonObject.put("UOM", data.getString(data.getColumnIndex("UOM")));
                    jsonObject.put("LineNo", data.getInt(data.getColumnIndex("LineNo")));
                    jsonObject.put("DtlRemark", data.getString(data.getColumnIndex("DtlRemarks")));
                    jsonObject.put("DtlRemark2", data.getString(data.getColumnIndex("DtlRemarks2")));
                    jsonObject.put("BatchNo", data.getString(data.getColumnIndex("BatchNo")));
                    jsonObject.put("DocType", data.getString(data.getColumnIndex("DocType")));
                    jsonObject.put("UTDCost", data.getDouble(data.getColumnIndex("UTD_Cost")));
                    jsonObject.put("Total", data.getDouble(data.getColumnIndex("SubTotal")));
                    jsonObject.put("LastModifiedUser", data.getString(data.getColumnIndex("LastModifiedUser")));
                    jsonObject.put("LastModifiedDateTime", data.getString(data.getColumnIndex("LastModifiedDateTime")));


                    jsonList.add(jsonObject);

                } catch (Exception e) { Log.i("custDebug", e.getMessage()); }
            }
        } else {
            Log.d("Checking123", "No data");
        }

        return jsonList;
    }
}
