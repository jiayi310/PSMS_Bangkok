package com.example.androidmobilestock.ui.main;

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
import com.example.androidmobilestock.R;
import com.example.androidmobilestock.databinding.FragmentStockBalanceByLocationBatchNoBinding;
import com.example.androidmobilestock.databinding.FragmentStockBalanceByLocationBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StockBalanceByLocationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StockBalanceByLocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StockBalanceByLocationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    AC_Class.StockBalance stockBalance = new AC_Class.StockBalance();
    AC_Class.Item item = new AC_Class.Item();
    List<AC_Class.StockBalance> stockBalances = new ArrayList<>();
    ACDatabase db;
    ListView listview;
    Boolean isBatchNoEnabled = true;

    private OnFragmentInteractionListener mListener;

    public StockBalanceByLocationFragment() {
        // Required empty public constructor
    }

    public static StockBalanceByLocationFragment newInstance(Bundle myBundle) {
        StockBalanceByLocationFragment fragment = new StockBalanceByLocationFragment();
        fragment.setArguments(myBundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new ACDatabase(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() != null) {
            item = getArguments().getParcelable("ItemDetailKey");
            item.setItemCode(item.getItemCode());
            try {
                Cursor data = db.getAllStockBalance(item.getItemCode(), item.getUOM());
                while (data.moveToNext()) {
                    stockBalance.setItemCode(data.getString(0));
                    stockBalance.setLocation(data.getString(2));
                    stockBalance.setBalQty(data.getFloat(3));
                }
            } catch (Exception e) { Log.i("custDebug", new Throwable().getStackTrace()[0]
                    .getLineNumber()+" | "+e.getMessage()); }
        }

        Cursor cursor = db.getReg("38");
        if(cursor.moveToFirst()){
            isBatchNoEnabled = Boolean.valueOf(cursor.getString(0));
        }

        try {
            View view = null;
            if(!isBatchNoEnabled) {
                    FragmentStockBalanceByLocationBinding binding;
                    // Inflate the layout for this fragment
                    binding = DataBindingUtil.inflate(inflater,
                            R.layout.fragment_stock_balance_by_location, container, false);
                    view = binding.getRoot();
                    binding.setStockbalance(stockBalance);
                listview = (ListView) view.findViewById(R.id.stockbalancelist);
                getData2();
                }else{
                    FragmentStockBalanceByLocationBatchNoBinding binding2;
                    // Inflate the layout for this fragment
                    binding2 = DataBindingUtil.inflate(inflater,
                            R.layout.fragment_stock_balance_by_location_batch_no, container, false);
                    view = binding2.getRoot();
                    binding2.setStockbalance(stockBalance);
                listview = (ListView) view.findViewById(R.id.stockbalancelist);
                getData();
                }

            return view;


        } catch (Exception e) { Log.i("custDebug", new Throwable().getStackTrace()[0]
                .getLineNumber()+" | "+e.getMessage()); }
        return null;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void getData()
    {
        stockBalances.clear();
        Cursor data = db.getAllStockBalance(item.getItemCode(), item.getUOM());
        if (data.getCount() > 0) {

            while (data.moveToNext()) {
//                Log.i("custDebug", "SB: "+DatabaseUtils.dumpCursorToString(data));
                try {

                    AC_Class.StockBalance sb = new AC_Class.StockBalance(
                            data.getString(data.getColumnIndex("ItemCode")),
                            data.getString(data.getColumnIndex("UOM")),
                            data.getString(data.getColumnIndex("Location")),
                            data.getFloat(data.getColumnIndex("BalQty")),
                            data.getString(data.getColumnIndex("BatchNo")));

                    //if(sb.getBalQty()!=0) {
                        stockBalances.add(sb);
                   // }

                } catch (Exception e) {
                    Log.i("custDebug", e.getMessage());
                }
            }
            StockBalanceListAdapter adapter = new StockBalanceListAdapter(getActivity(), stockBalances);
            listview.setAdapter(adapter);
        }
        else{
            stockBalances.clear();
            StockBalanceListAdapter adapter = new StockBalanceListAdapter(getActivity(), stockBalances);
            listview.setAdapter(adapter);
        }
    }

    public void getData2()
    {
        stockBalances.clear();
        Cursor data = db.getAllStockBalance2(item.getItemCode(), item.getUOM());
        if (data.getCount() > 0) {

            while (data.moveToNext()) {
//                Log.i("custDebug", "SB: "+DatabaseUtils.dumpCursorToString(data));
                try {

                    AC_Class.StockBalance sb = new AC_Class.StockBalance(
                            data.getString(data.getColumnIndex("ItemCode")),
                            data.getString(data.getColumnIndex("UOM")),
                            data.getString(data.getColumnIndex("Location")),
                            data.getFloat(data.getColumnIndex("Total")));
                    stockBalances.add(sb);
                } catch (Exception e) {
                    Log.i("custDebug", e.getMessage());
                }
            }
            StockBalanceListAdapter adapter = new StockBalanceListAdapter(getActivity(), stockBalances);
            listview.setAdapter(adapter);
        }
        else{
            stockBalances.clear();
            StockBalanceListAdapter adapter = new StockBalanceListAdapter(getActivity(), stockBalances);
            listview.setAdapter(adapter);
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

    @Override
    public void onResume() {
        super.onResume();
        if(!isBatchNoEnabled){
            getData2();
        }
        else {
            getData();
        }
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
}
