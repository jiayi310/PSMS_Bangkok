package com.example.androidmobilestock_bangkok;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.androidmobilestock_bangkok.adapter.HistoryPriceListAdapter;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HistoryPrice extends AppCompatActivity {

    ListView histprice_listView;
    ACDatabase db;
    String debtorcode;
    String Item;
    String ItemUOM;
    int FilterByAgent;
    String Agent;
    boolean isHybrid = false;
    String url;
    HistoryPriceListAdapter historyPriceListAdapter;
    List<AC_Class.HistoryPrice> historyPrices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_price);

        histprice_listView = (ListView) findViewById(R.id.list_price);
        db = new ACDatabase(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("List of History Price");
        actionBar.setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        debtorcode = intent.getStringExtra("DebtorKey");
        Item = intent.getStringExtra("ItemKey");
        ItemUOM = intent.getStringExtra("ItemUOMKey");
        FilterByAgent = intent.getIntExtra("FilterByAgent", 0);
        Cursor agent = db.getReg("56");
        if (agent.moveToFirst()){
            Agent = agent.getString(0).replace("\"","'");
        }
        //getHistoryPrice();

        Cursor cur4 = db.getReg("32");
        if (cur4.moveToFirst()) {
            isHybrid = Boolean.valueOf(cur4.getString(0));
        }

        Cursor cur5 = db.getReg("2");
        if (cur5.moveToFirst()) {
            url = cur5.getString(0);
        }

        historyPriceListAdapter = new HistoryPriceListAdapter(this, historyPrices);
        histprice_listView.setAdapter(historyPriceListAdapter);

        getData();


        histprice_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Float price = historyPrices.get(position).getUnitPrice();
                Intent intent1 = new Intent();
                intent1.putExtra("Price", price);
                setResult(10, intent1);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
        }
        return false;
    }


    private void getData() {
        if (isHybrid) {
            new GetMyHistoryPriceList(HistoryPrice.this).execute(url);
        } else {
            try {


                Cursor data;

                if (FilterByAgent == 1) {
                    data = db.getHistoryPriceByDebtorByAgent(Item, ItemUOM, debtorcode, Agent);
                } else {
                    data = db.getHistoryPriceByDebtor(Item, ItemUOM, debtorcode);
                }

                if (data.getCount() > 0) {
                    while (data.moveToNext()) {
                        AC_Class.HistoryPrice historyPrice = new AC_Class.HistoryPrice(data.getString(1),
                                data.getString(2), data.getString(data.getColumnIndex("CompanyName")), data.getString(3),
                                data.getString(4), data.getString(5),
                                data.getString(6), data.getString(7),
                                data.getString(8), data.getFloat(9), data.getString(10),
                                data.getFloat(11), data.getFloat(12),
                                data.getFloat(13));
                        historyPrices.add(historyPrice);
                    }

                    historyPriceListAdapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                Log.i("custDebug", e.getMessage());
            }
        }
    }

    //Inquiry HistoryPrice Data
    class GetMyHistoryPriceList extends AsyncTask<String, Void, Boolean> {
        Activity context;
        ProgressDialog pd;

        GetMyHistoryPriceList(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(context);
            pd.setMessage("Inquiry HistoryPrices...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Boolean doInBackground(String... connUrl) {
            return getHistoryPriceData(connUrl[0]);
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);

            if (pd.isShowing())
                pd.dismiss();

            if (!bool)
                Snackbar.make(this.context.findViewById(android.R.id.content), "Server connection failed.", Snackbar.LENGTH_SHORT).show();

            historyPriceListAdapter.notifyDataSetChanged();
        }
    }

    private Boolean getHistoryPriceData(String connUrl)
    {
        Boolean result = false;

        HttpURLConnection conn;
        BufferedReader reader;

        try {
            final java.net.URL url = new URL(connUrl + "/getHistory");
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            JSONObject myFilter = new JSONObject();
            myFilter.put("ItemCode", Item);
            myFilter.put("UOM", ItemUOM);
            myFilter.put("AccNo", debtorcode);
            if (FilterByAgent == 1)
                myFilter.put("Agent", Agent);
            else
                myFilter.put("Agent", null);
            myFilter.put("Days", 30);

            String send = myFilter.toString();
            byte[] sendb = send.getBytes("UTF-8");

            conn.connect();
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
                    historyPrices.clear();

                    boolean insert = true;
                    for (int i = 0; i < history_Price.length(); i++) {
                        JSONObject object = history_Price.getJSONObject(i);
                        String DocType = object.getString("DocType");
                        String AccNo = object.getString("AccNo");
                        String CompanyName = object.getString("CompanyName");
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

                        AC_Class.HistoryPrice hp = new AC_Class.HistoryPrice(DocType, AccNo, CompanyName, ItemCode, Description, DocNo, DocDate, Location, Agent, Qty, UOM, UnitPrice, 0.0f, SubTotal);

                        historyPrices.add(hp);
                    }

                }
            }

            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i("custDebug", ex.getMessage());
        }
        return result;
    }
}
