package com.example.androidmobilestock_bangkok;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.androidmobilestock_bangkok.databinding.ActivityArpaymentOutstandingListBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class ARPayment_OutstandingList extends AppCompatActivity {

    ActivityArpaymentOutstandingListBinding binding;
    String DebtorCode, DocNo, DocNoList;
    private RecyclerView recyclerView;
    ACDatabase db;
    List<AC_Class.AROutstanding> outstandingList = new ArrayList<>();
    ArrayList<AC_Class.ARPaymentDtl> arPaymentDtls = new ArrayList<>();
    AC_Class.ARPaymentDtl arPaymentDtl;
    private ARPayment_OutstandingListAdapter.RecyclerViewClickListener listener;
    String url;
    AC_Class.AROutstanding arOutstanding;
    ARPayment_OutstandingListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arpayment_outstanding_list);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_arpayment_outstanding_list);

        Intent pintent = getIntent();
        DebtorCode = pintent.getStringExtra("DebtorCode");
        DocNo = pintent.getStringExtra("DocNo");
        DocNoList = pintent.getStringExtra("DocNoList");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Outstanding List of " + DebtorCode);
        actionBar.setDisplayHomeAsUpEnabled(true);

        db = new ACDatabase(this);
        recyclerView = findViewById(R.id.outstandinglist);
        hideSoftKeyboard(binding.getRoot());

        Cursor cur5 = db.getReg("2");
        if (cur5.moveToFirst()) {
            url = cur5.getString(0);
        }

        new GetOutstandingList(ARPayment_OutstandingList.this).execute(url);

        listener = new ARPayment_OutstandingListAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {

                arPaymentDtl = new AC_Class.ARPaymentDtl();
                arPaymentDtl.setDocNo(DocNo);
                arPaymentDtl.setDocNumber(((AC_Class.AROutstanding) outstandingList.get(position)).getDocNo());
                arPaymentDtl.setDocDate(((AC_Class.AROutstanding) outstandingList.get(position)).getDocDate());
                arPaymentDtl.setPaymentAmount(((AC_Class.AROutstanding) outstandingList.get(position)).getOutstanding());
                arPaymentDtl.setOrgAmt(((AC_Class.AROutstanding) outstandingList.get(position)).getNetTotal());
                arPaymentDtls.add(arPaymentDtl);

            }

            @Override
            public void onFalseClick(View v, int position) {

                String removedoc = ((AC_Class.AROutstanding) outstandingList.get(position)).getDocNo();
                for(int i = 0; i < arPaymentDtls.size(); i++){
                    if(arPaymentDtls.get(i).getDocNumber().equals(removedoc)){
                        arPaymentDtls.remove(arPaymentDtls.get(i));
                    }
                }

            }
        };

        binding.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent item_intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("arPaymentList", arPaymentDtls);
                item_intent.putExtras(bundle);
                setResult(0, item_intent);
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

    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    class GetOutstandingList extends AsyncTask<String, Void, Double> {
        Activity context;
        ProgressDialog pd;

        GetOutstandingList(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setMessage("Get Outstanding List...");
            //pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Double doInBackground(String... params) {

            String urlString = params[0] + "/getOutstanding"; // URL to call
            String response = "";
            Double total = 0.00;

            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                JSONObject root = new JSONObject();
                root.put("DebtorCode", DebtorCode);
                root.put("DocNo", DocNoList);
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
                        String DocDate = object.getString("DocDate");
                        Double NetTotal = object.getDouble("NetTotal");
                        Double Outstanding = object.getDouble("Outstanding");
                        String DueDate = object.getString("DueDate");

                        total += Outstanding;

                        arOutstanding = new AC_Class.AROutstanding(DocNo, DocDate, NetTotal, Outstanding, DueDate);

                        outstandingList.add(arOutstanding);
                    }
                }else {

                    response = "";
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            return total;
        }

        @Override
        protected void onPostExecute(Double bool) {
            super.onPostExecute(bool);

            if (pd.isShowing())
                pd.dismiss();

            setAdapter();
            int size = outstandingList.size();
            binding.totaldoc.setText(Integer.toString(size));
            binding.totalamount.setText("RM " + String.format(Locale.getDefault(),
                    " %.2f", bool));

        }
    }

    private void setAdapter() {
        adapter = new ARPayment_OutstandingListAdapter(this,  outstandingList, listener);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(99, intent);
        finish();
    }
}