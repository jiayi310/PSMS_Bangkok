package com.example.androidmobilestock.fragment;

import android.content.Context;
import android.database.Cursor;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.androidmobilestock.ACDatabase;
import com.example.androidmobilestock.AC_Class;
import com.example.androidmobilestock.InvPymntAdapter;
import com.example.androidmobilestock.adapter.InvoiceListViewAdapter;
import com.example.androidmobilestock.R;
import com.example.androidmobilestock.databinding.FragmentInvoicePaymentsBinding;

import java.util.ArrayList;
import java.util.List;


public class InvoicePaymentsFragment extends Fragment {
    InvoiceListViewAdapter arrayAdapter;
    ACDatabase db;
    String debtorCode;
    String docNo;
    String def_curr;
    ListView listview;

    private OnFragmentInteractionListener mListener;

    public InvoicePaymentsFragment() {
        // Required empty public constructor
    }

    public static InvoicePaymentsFragment newInstance(Bundle myBundle) {
        InvoicePaymentsFragment fragment = new InvoicePaymentsFragment();
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
        FragmentInvoicePaymentsBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_invoice_payments, container, false);
        final View view = binding.getRoot();
        listview = (ListView)binding.invPaymentListItemlist;
        getInvoicePaymentList(docNo);

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

    public void getInvoicePaymentList(String docNo)
    {
        Cursor data = db.getPaymentToUpload(docNo);
        List<AC_Class.Payment> invoiceDetailsArrayList = new ArrayList<>();
        if (data.getCount() > 0) {
            while (data.moveToNext()) {
                AC_Class.Payment payments = new AC_Class.Payment(data.getString(1),
                        data.getString(2), data.getString(3),
                        data.getString(4), Double.parseDouble(data.getString(5)),
                        Double.parseDouble(data.getString(6)));
                invoiceDetailsArrayList.add(payments);
            }
        } else {
            invoiceDetailsArrayList.add(new AC_Class.Payment(docNo, null, null,
                    "PENDING", (double) 0, (double) 0));
        }
        InvPymntAdapter adapter = new InvPymntAdapter(getActivity(), invoiceDetailsArrayList,
                def_curr);
        Cursor Currency = db.getReg("6");
        if(Currency.moveToFirst()){
            def_curr = Currency.getString(0);
        }
        listview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
