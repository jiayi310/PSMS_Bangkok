package com.example.androidmobilestock_bangkok.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.androidmobilestock_bangkok.ACDatabase;
import com.example.androidmobilestock_bangkok.AC_Class;
import com.example.androidmobilestock_bangkok.InvoiceDtlList_Sales;
import com.example.androidmobilestock_bangkok.adapter.InvoiceListViewAdapter;
import com.example.androidmobilestock_bangkok.R;
import com.example.androidmobilestock_bangkok.databinding.FragmentInvoiceItemsBinding;

import java.util.ArrayList;
import java.util.List;


public class InvoiceItemsFragment extends Fragment {
    InvoiceListViewAdapter arrayAdapter;
    ACDatabase db;
    String debtorCode;
    String docNo;
    String def_curr;
    RecyclerView listview;
    int searchMode = 0;
    private InvoiceDtlList_Sales.RecyclerViewClickListener listener;

    private OnFragmentInteractionListener mListener;

    public InvoiceItemsFragment() {
        // Required empty public constructor
    }

    public static InvoiceItemsFragment newInstance(Bundle myBundle) {
        InvoiceItemsFragment fragment = new InvoiceItemsFragment();
        fragment.setArguments(myBundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new ACDatabase(getActivity());
        if (getArguments() != null) {
            debtorCode = getArguments().getString("debtorCode");
            docNo = getArguments().getString("docNo");
        } else {
            Log.i("custDebug", "ERROR, no args");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentInvoiceItemsBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_invoice_items, container, false);
        final View view = binding.getRoot();
        listview = binding.invdtllistItemlist;
        getHistData(debtorCode);
        getInvoiceDetailList(docNo);

        return view;
    }

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
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public void getHistData(String code)
    {
        Cursor data = db.getInvoiceHist(code, searchMode);
//        Log.i("custDebug", "hi");
        if(data.getCount() == 0)
        {
            Log.i("custDebug", "no entries");
            return;
        }
        else if (data.getCount() > 0)
        {
            List<AC_Class.InvoiceMenu> invoiceMenus = new ArrayList<>();
            while (data.moveToNext()) {
                AC_Class.InvoiceMenu invoiceMenu =  new AC_Class.InvoiceMenu(data.getString(0), data.getString(1), data.getString(2), data.getString(3), data.getString(4), data.getString(4), data.getString(5), data.getDouble(6), data.getDouble(7), data.getDouble(8), data.getInt(9),data.getString(10),data.getString(11),data.getString(12),data.getString(13),data.getString(14));
//                Log.i("custDebug", invoiceMenu.getDebtorName()+", "+invoiceMenu.getDocNo());
                invoiceMenus.add(invoiceMenu);
            }
            arrayAdapter = new InvoiceListViewAdapter(getActivity(), invoiceMenus,
                    def_curr);
            Cursor Currency = db.getReg("6");
            if(Currency.moveToFirst()){
                def_curr = Currency.getString(0);
            }
            //listview.setAdapter(arrayAdapter);
        }
    }

    public void getInvoiceDetailList(String docNo)
    {
        Cursor data = db.getInvoiceDetailtoUpdate(docNo);
        List<AC_Class.InvoiceDetails> invoiceDetailsArrayList = new ArrayList<>();
        if (data.getCount() > 0) {
            while (data.moveToNext()) {
                AC_Class.InvoiceDetails inv = new AC_Class.InvoiceDetails(data.getInt(0),
                        data.getString(1), data.getString(2), data.getString(3),
                        data.getString(4), data.getString(5), data.getDouble(6),
                        data.getDouble(7), data.getDouble(8), data.getDouble(9),
                        data.getString(10), data.getDouble(11), data.getDouble(12),
                        data.getDouble(13), data.getDouble(14), data.getString(15),
                        data.getString(data.getColumnIndex("Remarks")),
                        data.getString(data.getColumnIndex("BatchNo")),
                        data.getString(data.getColumnIndex("Remarks2")));
                invoiceDetailsArrayList.add(inv);
            }
        }
       // listview.setHasFixedSize(true);
       // listview.setLayoutManager(new LinearLayoutManager(getContext()));
        listview.setHasFixedSize(true);
        listview.setLayoutManager(new LinearLayoutManager(getContext()));
        listview.setItemViewCacheSize(20);
        InvoiceDtlList_Sales adapter = new InvoiceDtlList_Sales(getActivity(), invoiceDetailsArrayList,listener);
        listview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void OnModeClicked(View view) {
        try {
            final TextView modeView = view.findViewById(R.id.modeView2);
            final String[] methods = {"Most Recent", "Total Amount"};
            final AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
            builder.setTitle("Choose search mode:");
            builder.setItems(methods, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0: //Recent
                            searchMode = 0;
                            modeView.setText(methods[0]);
                            break;

                        case 1: //Amount
                            searchMode = 1;
                            modeView.setText(methods[1]);
                            break;
                    }
                    getHistData(debtorCode);
                    arrayAdapter.notifyDataSetChanged();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {Log.i("custDebug", e.getMessage());}
    }
}
