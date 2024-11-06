package com.example.androidmobilestock_bangkok.ui.main;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.androidmobilestock_bangkok.ACDatabase;
import com.example.androidmobilestock_bangkok.AC_Class;
import com.example.androidmobilestock_bangkok.adapter.InvoiceListViewAdapter;
import com.example.androidmobilestock_bangkok.R;
import com.example.androidmobilestock_bangkok.databinding.FragmentInvoiceHistoryBinding;

import java.util.ArrayList;
import java.util.List;


public class InvoiceHistoryFragment extends Fragment {
    FragmentInvoiceHistoryBinding binding;
    InvoiceListViewAdapter arrayAdapter;
    ACDatabase db;
    String debtorCode;
    ListView listview;
    int searchMode = 0;

    private OnFragmentInteractionListener mListener;

    public InvoiceHistoryFragment() {
        // Required empty public constructor
    }

    public static InvoiceHistoryFragment newInstance(Bundle myBundle) {
        InvoiceHistoryFragment fragment = new InvoiceHistoryFragment();
        fragment.setArguments(myBundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new ACDatabase(getActivity());
        if (getArguments() != null) {
            debtorCode = getArguments().getString("debtorCode");
//            getHistData(debtorCode);
        } else {
            Log.i("custDebug", "ERROR, no args");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentInvoiceHistoryBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_invoice_history, container, false);
        final View view = binding.getRoot();
        listview = (ListView)binding.invmenuList;
        getHistData(debtorCode);

        try {
            TextView modeButton = binding.modeView2;
//            Log.i("custDebug", "whybreak");
            modeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnModeClicked(view);
                }
            });
        } catch (Exception e) { Log.i("custDebug", e.getMessage());}

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
                invoiceMenus.add(invoiceMenu);
            }

            String defcur = "";
            Cursor cur = db.getReg("6");
            if(cur.moveToFirst()){
                defcur = cur.getString(0);
            }

            arrayAdapter = new InvoiceListViewAdapter(getActivity(), invoiceMenus, defcur);
            listview.setAdapter(arrayAdapter);
        }
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
