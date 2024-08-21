package com.example.androidmobilestock.ui.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.example.androidmobilestock.ACDatabase;
import com.example.androidmobilestock.AC_Class;
import com.example.androidmobilestock.R;
import com.example.androidmobilestock.databinding.FragmentCustomerHistoryPriceBinding;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CustomerHistoryPriceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CustomerHistoryPriceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerHistoryPriceFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    AC_Class.HistoryPrice historyPrice = new AC_Class.HistoryPrice();
    AC_Class.Item item = new AC_Class.Item();
    ACDatabase db;
    HistoryPriceListAdapter historyPriceListAdapter;
    List<AC_Class.HistoryPrice> historyPrices = new ArrayList<>();
    ListView hist_listview;
    EditText searchEditText;
    String substring = "";
    int FilterByAgent;
    String myUser;
    boolean isHybrid = false;
    int historyDay = 0;
    String url;
    String Agent;

    private OnFragmentInteractionListener mListener;

    public CustomerHistoryPriceFragment() {
        // Required empty public constructor
    }

    public static CustomerHistoryPriceFragment newInstance(Bundle myBundle) {
        CustomerHistoryPriceFragment fragment = new CustomerHistoryPriceFragment();
        fragment.setArguments(myBundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new ACDatabase(getActivity());


        if (getArguments() != null) {
            item = getArguments().getParcelable("ItemDetailKey");
        }

        Cursor cur = db.getReg("14");
        if(cur.moveToFirst()){
            FilterByAgent = cur.getInt(0);
        }

        Cursor agent = db.getReg("56");
        if (agent.moveToFirst()){
            Agent = agent.getString(0).replace("\"","'");
        }

        String DefaultCurr="";

        Cursor cur2 = db.getReg("6");
        if(cur2.moveToFirst()){
            DefaultCurr = cur2.getString(0);
        }

        Cursor cur3 = db.getReg("4");
        if(cur3.moveToFirst()){
            myUser = cur3.getString(0);
        }

        Cursor cur4 = db.getReg("32");
        if(cur4.moveToFirst()){
            isHybrid = Boolean.valueOf(cur4.getString(0));
        }

        Cursor cur6 = db.getReg("33");
        if(cur6.moveToFirst()){
            historyDay = cur6.getInt(0);
        }

        Cursor cur5 = db.getReg("2");
        if(cur5.moveToFirst()){
            url = cur5.getString(0);
        }

        historyPriceListAdapter = new HistoryPriceListAdapter(getActivity(), historyPrices, DefaultCurr);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try
        {
            // Inflate the layout for this fragment
            FragmentCustomerHistoryPriceBinding binding = DataBindingUtil.inflate(inflater,
                    R.layout.fragment_customer_history_price, container, false);
            View view = binding.getRoot();
            binding.setHistprice(historyPrice);
            // Table and header
            hist_listview = (ListView) binding.historypricelist;
            // Header
            View headerView = getLayoutInflater()
                    .inflate(R.layout.history_price_list_header, null);
            hist_listview.addHeaderView(headerView);
            hist_listview.setAdapter(historyPriceListAdapter);
            //getData(substring);

            searchEditText = (EditText) view.findViewById(R.id.searchField);
            searchEditText.requestFocus();
            searchEditText.setVisibility(View.GONE);
            substring = searchEditText.getText().toString();
            getData(substring);

            //Text listener
            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                    getData(s.toString().trim());
                }
            });

            return view;
        }
        catch (Exception e)
        {
            Log.i("custDebug",
                    new Throwable().getStackTrace()[0].getLineNumber()+" | "+e.getMessage());
        }

        return null;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            // Else what??
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void getData(String substring)
    {
        if (isHybrid) {
            new GetMyHistoryPriceList(this.getActivity()).execute(url);
        }
        else {
            try {

                Cursor data;

                if (FilterByAgent == 1) {
                    data = db.getHistoryPriceByAgentLike(item.getItemCode(), item.getUOM(), substring, Agent);
                } else {
                    data = db.getHistoryPriceLike(item.getItemCode(), item.getUOM(), substring);
                }

                if (data.getCount() > 0) {
//            Log.i("custDebug", "HP data size: " + data.getCount());
                    historyPrices.clear();
                    while (data.moveToNext()) {
                        //Add price history to list
                        AC_Class.HistoryPrice hp = new AC_Class.HistoryPrice(data.getString(1),
                                data.getString(2), data.getString(data.getColumnIndex("CompanyName")), data.getString(3),
                                data.getString(4), data.getString(5),
                                data.getString(6), data.getString(7),
                                data.getString(8), data.getFloat(9), data.getString(10),
                                data.getFloat(11), data.getFloat(12),
                                data.getFloat(13));
                            historyPrices.add(hp);
                    }
                    historyPriceListAdapter.notifyDataSetChanged();
                }
                else{
                    historyPrices.clear();
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
            myFilter.put("ItemCode", item.getItemCode());
            myFilter.put("UOM", item.getUOM());
            myFilter.put("AccNo", null);
            if (FilterByAgent == 1)
                myFilter.put("Agent", myUser);
            else
                myFilter.put("Agent", null);
            myFilter.put("Days", historyDay);

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
