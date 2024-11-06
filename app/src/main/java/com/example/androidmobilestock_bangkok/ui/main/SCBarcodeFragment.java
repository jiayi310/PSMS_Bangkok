package com.example.androidmobilestock_bangkok.ui.main;

import android.content.Context;
import android.database.Cursor;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.androidmobilestock_bangkok.ACDatabase;
import com.example.androidmobilestock_bangkok.AC_Class;
import com.example.androidmobilestock_bangkok.R;
import com.example.androidmobilestock_bangkok.databinding.FragmentScbarcodeBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SCBarcodeFragment extends Fragment {
    ACDatabase db;
    private MyClickHandler handler;
    FragmentScbarcodeBinding binding;
    private AC_Class.StockCount stockCount;
    String temp;
//    List<AC_Class.Item> itemlist;

    private OnFragmentInteractionListener mListener;

    public SCBarcodeFragment() {
        // Required empty public constructor
    }

    public static SCBarcodeFragment newInstance(Bundle bundle) {
        SCBarcodeFragment fragment = new SCBarcodeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        String default_location = "";
        super.onCreate(savedInstanceState);
        stockCount = new AC_Class.StockCount();

        Cursor dl = db.getReg("");
        if(dl.moveToFirst()){
            default_location = dl.getString(0);
        }
        stockCount.setLocation(default_location);

        db = new ACDatabase(getContext());

//        getData();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        final FragmentScbarcodeBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_scbarcode, container, false);

        //MyClickHandler
        handler = new MyClickHandler(getContext());
        binding.setHandler(handler);

        final View view = binding.getRoot();
        binding.setStockCount(stockCount);

        hideSoftKeyboard(view);

        binding.itemEditTextB.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                temp = s.toString().trim();
                if (!temp.equals("") && !TextUtils.isEmpty(binding.itemEditTextB.getText())) {

                    stockCount = db.getItemBC(temp, stockCount);

                    if (stockCount.Location != null && stockCount.getItemCode() != null)
                    {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
                        String date = sdf.format(new Date());
                        stockCount.setDocDate(date);
                        boolean check = db.isExists(stockCount.getItemCode(), stockCount.getUOM());
                        if (check) {
                            //Log.i("custDebug", "+1, old");
                            //Update
                            Cursor oldQty = db.getAllStockCount(stockCount.getItemCode(), stockCount.getUOM());
                            oldQty.moveToNext();
                            stockCount.setQty(oldQty.getFloat(oldQty.getColumnIndex("Qty")) + 1);
                            db.UpdateStockCount(stockCount);
                        } else {
                            //Log.i("custDebug", "+1, new");
                            stockCount.setQty(1.00f);
                            boolean insert = db.insertSC(stockCount.getDocDate(), stockCount.getItemCode(),
                                    stockCount.getDescription(), stockCount.getLocation(), stockCount.getUOM(),
                                    stockCount.getQty());
                            if (insert) {
                                Toast.makeText(getContext(), "Insert Successful",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Something Went Wrong",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        //Quantity textViews
                        binding.lblQty.setVisibility(View.VISIBLE);
                        binding.tvQty.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(getContext(), "B - Product not found",
                                Toast.LENGTH_SHORT).show();
                        //Quantity textViews
                        binding.lblQty.setVisibility(View.INVISIBLE);
                        binding.tvQty.setVisibility(View.INVISIBLE);
                    }
                }
                //Clear EditText & reset
                binding.itemEditTextB.removeTextChangedListener(this);
                binding.itemEditTextB.clearFocus();
                binding.itemEditTextB.setText("");
                binding.itemEditTextB.requestFocus();
                hideSoftKeyboard(getView());
                binding.itemEditTextB.addTextChangedListener(this);
            }
        });

//        binding.itemEditTextB.clearFocus();
//        binding.itemEditTextB.requestFocus();

        return view;
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 4) {
//            if (data != null) {
//                AC_Class.Item item = data.getParcelableExtra("ItemsKey");
//                if (item != null) {
//                    //binding.itemTxtDetail.setVisibility(View.VISIBLE);
//                    stockCount.setItemCode(item.getItemCode());
//                    stockCount.setDescription(item.getDescription());
//                    Cursor data1 = db.getItemUOM(item.getItemCode());
//                    while (data1.moveToNext()) {
//                        stockCount.setUOM(data1.getString(1));
//                    }
//                }
//            }
//
//        }
//    }

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
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public class MyClickHandler
    {
        Context context;

        public MyClickHandler(Context context) {
            this.context = context;
        }


    }

//    public void getData()
//    {
//        Cursor data = db.getItem();
//        if (data.getCount() > 0)
//        {
//            itemlist = new ArrayList<>();
//            while (data.moveToNext())
//            {
//                AC_Class.Item items = new AC_Class.Item(data.getString(0),
//                        data.getString(1), data.getString(2),
//                        data.getString(3), data.getString(4),
//                        data.getString(5), data.getString(6),
//                        data.getString(7), data.getFloat(8),
//                        data.getFloat(9), data.getString(10));
//                itemlist.add(items);
//            }
//        }
//    }

    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
