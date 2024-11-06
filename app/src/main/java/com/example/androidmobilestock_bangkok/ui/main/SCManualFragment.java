package com.example.androidmobilestock_bangkok.ui.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.androidmobilestock_bangkok.ACDatabase;
import com.example.androidmobilestock_bangkok.AC_Class;
import com.example.androidmobilestock_bangkok.Item_List;
import com.example.androidmobilestock_bangkok.R;
import com.example.androidmobilestock_bangkok.databinding.FragmentScmanualBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SCManualFragment extends Fragment {
    ACDatabase db;
    private MyClickHandler handler;
//    FragmentScmanualBinding binding;
    private AC_Class.StockCount stockCount;
//    List<AC_Class.Item> itemlist;
    String temp;
    int mode;
    FragmentScmanualBinding binding;
    private IntentIntegrator qrScan;
    private ArrayList _ic = new ArrayList<>();
    private ArrayList _dsc = new ArrayList<>();
    private ArrayList _loc = new ArrayList<>();
    private ArrayList _uom = new ArrayList<>();
    private ArrayList _qty = new ArrayList<>();

    private OnFragmentInteractionListener mListener;

    public SCManualFragment() {
        // Required empty public constructor
    }

    public static SCManualFragment newInstance(Bundle bundle) {
        SCManualFragment fragment = new SCManualFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        String default_location = "";
        getArguments().getInt("mode", mode);
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_scmanual,
                container, false);

        //MyClickHandler
        handler = new MyClickHandler(getContext());
        binding.setHandler(handler);

        final View view = binding.getRoot();
        binding.setStockCount(stockCount);

        hideSoftKeyboard(view);

        binding.itemEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                temp = s.toString().trim();
                if (!temp.equals("")) {
                    stockCount = db.getItemBC(temp, stockCount);

                    if (stockCount.Location != null && stockCount.getItemCode() != null) {
                        //Set date
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
                        String date = sdf.format(new Date());
                        stockCount.setDocDate(date);

                        boolean scExists = db.isExists(stockCount.getItemCode(), stockCount.getUOM());
                        if (scExists) {
                            //Update
                            Cursor oldQty = db.getAllStockCount(stockCount.getItemCode(), stockCount.getUOM());
                            oldQty.moveToNext();
                            stockCount.setQty(oldQty.getFloat(oldQty.getColumnIndex("Qty")));
                        } else {
                            stockCount.setQty(1.00f);
                        }
                        //prev. binding
                    } else {
                        Toast.makeText(getContext(), "Product not found",
                                Toast.LENGTH_SHORT).show();
                    }
                    binding.itemEditText.removeTextChangedListener(this);
                    binding.itemEditText.clearFocus();
                    binding.itemEditText.setText("");
                    binding.itemEditText.requestFocus();
                    hideSoftKeyboard(getView());
                    binding.itemEditText.addTextChangedListener(this);
                }
            }
        });

        binding.tvQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    stockCount.setQty(Float.parseFloat(s.toString()));
                }
            }
        });

        binding.itemEditText.clearFocus();
        binding.itemEditText.requestFocus();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 4) {
            if (data != null) {
                AC_Class.Item item = data.getParcelableExtra("ItemsKey");
                if (item != null) {
                    stockCount.setItemCode(item.getItemCode());
                    stockCount.setDescription(item.getDescription());
                    stockCount.setUOM(item.getUOM());
                    //Get Qty
                    boolean scExists = db.isExists(stockCount.getItemCode(), stockCount.getUOM());
                    if (scExists) {
                        //Update
                        Cursor oldQty = db.getAllStockCount(stockCount.getItemCode(), stockCount.getUOM());
                        oldQty.moveToNext();
                        stockCount.setQty(oldQty.getFloat(oldQty.getColumnIndex("Qty")));
                    } else {
                        stockCount.setQty(1.00f);
                    }
                }
                //getActivity().findViewById(R.id.itemEditText).setVisibility(View.INVISIBLE);
                //getActivity().findViewById(R.id.itemItemCode).setVisibility(View.VISIBLE);
            }
            else
            {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }

        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode, data);
            if (result != null) {
                if (result.getContents() == null) {
                    Toast.makeText(this.getContext(), "No result found.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    binding.itemEditText.setText(result.getContents());
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);

            Log.wtf("All IC onButtonPressed:", String.valueOf(_ic.get(0)));
        }
    }

    public interface OnDataPass
    {
        public void onDataPass(ArrayList<String> ic, ArrayList<String> dsc, ArrayList<String> loc, ArrayList<String> uom, ArrayList<String> qty);


    }

    OnDataPass dataPasser;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPasser = (OnDataPass) context;

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

    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void readSCTable(){
        Cursor data = db.getStockCount();
        while (data.moveToNext()) {
            Log.i("custDebug", data.getString(data.getColumnIndex("ItemCode"))+", "+
                    data.getString(data.getColumnIndex("DocDate"))+", "+
                    data.getString(data.getColumnIndex("ItemCode"))+", "+
                    data.getString(data.getColumnIndex("Description"))+", "+
                    data.getString(data.getColumnIndex("Location"))+", "+
                    data.getString(data.getColumnIndex("UOM"))+", "+
                    data.getFloat(data.getColumnIndex("Qty")));
        }
    }

    public class MyClickHandler
    {
        Context context;

        public MyClickHandler(Context context) {
            this.context = context;
        }

        public void OnItemTxtViewClicked(View view)
        {
            Intent new_intent = new Intent(getActivity(), Item_List.class);
            new_intent.putExtra("substring", "");
            startActivityForResult(new_intent, 4);
        }

        public void OnEditTxtViewClicked(View view)
        {
            //getActivity().findViewById(R.id.itemItemCode).setVisibility(View.INVISIBLE);
            //getActivity().findViewById(R.id.itemEditText).setVisibility(View.VISIBLE);
            //getActivity().findViewById(R.id.itemEditText).requestFocus();
            hideSoftKeyboard(view);
        }

        public void OnItemCameraClicked(View view) {
//            Intent new_intent = new Intent(getActivity(), Item_List.class);
//            new_intent.putExtra("substring", "");
//            startActivityForResult(new_intent, 7);

            try {
//                Intent new_intent = new Intent(StockInquiry.this, activity_scanner.class);
//                startActivity(new_intent);
                qrScan = new IntentIntegrator(getActivity());
                qrScan.setPrompt("Scan a Barcode/QRCode");
                qrScan.setCameraId(0);
                qrScan.setBeepEnabled(true);
                qrScan.setOrientationLocked(false);
                //qrScan.initiateScan();
                Intent intent = qrScan.createScanIntent();
                startActivityForResult(intent, IntentIntegrator.REQUEST_CODE);

            } catch (Exception e) { Log.i("custDebug", "OnItemCameraClicked - " + e.getMessage()); }
        }

        public void BtnIncClicked(View view)
        {
            stockCount.setQty(stockCount.getQty()+1);
        }

        public void BtnDecClicked(View view)
        {
            stockCount.setQty(stockCount.getQty() <= 1 ? 1 : stockCount.getQty()-1);
        }

        public void SaveData(View view)
        {
            if (stockCount.Location != null && stockCount.getItemCode() != null &&
                    stockCount.getDescription() != null)
            {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
                String date = sdf.format(new Date());
                stockCount.setDocDate(date);
                boolean check = db.isExists(stockCount.getItemCode(), stockCount.getUOM());
                if (check) {
                    //Update
                    Cursor oldQty = db.getAllStockCount(stockCount.getItemCode(), stockCount.getUOM());
                    oldQty.moveToNext();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Overwrite Data?");
                    builder.setMessage("Do you want to overwrite the quantity for " +
                            stockCount.getItemCode() + ", \n" +
                            stockCount.getDescription() + "?\n" +
                            "Previous value: " +
                            String.format(Locale.getDefault(), "%.0f",
                                    oldQty.getFloat(oldQty.getColumnIndex("Qty")))
                            + "\n" + "New value: " +
                            String.format(Locale.getDefault(), "%.0f", stockCount.getQty()));

                    builder.setNeutralButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Cursor temp = db.getAllStockCount(stockCount.getItemCode(), stockCount.getUOM());
                            if (temp.moveToNext()) {
                                stockCount.setQty(stockCount.getQty()+
                                        temp.getFloat(temp.getColumnIndex("Qty"))
                                );
                            }
                            db.UpdateStockCount(stockCount);
                            stockCount.reset();
                        }
                    });

                    builder.setNegativeButton("Overwrite", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.UpdateStockCount(stockCount);
                            stockCount.reset();
                        }
                    });

                    builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    boolean insert = db.insertSC(stockCount.getDocDate(), stockCount.getItemCode(),
                            stockCount.getDescription(), stockCount.getLocation(), stockCount.getUOM(),
                            stockCount.getQty());
                    if (insert) {
                        Toast.makeText(getContext(), "Insert Successfully", Toast.LENGTH_SHORT).show();
                        _ic.add(stockCount.getItemCode());
                        _dsc.add(stockCount.getDescription());
                        _loc.add(stockCount.getLocation());
                        _uom.add(stockCount.getUOM());
                        _qty.add(String.valueOf(stockCount.getQty()));

                        for(int i=0 ; i<_ic.size() ; i++)
                        {
                            Log.wtf("IC", String.valueOf(_ic.get(i)));
                            Log.wtf("DSC", String.valueOf(_dsc.get(i)));
                            Log.wtf("LOC", String.valueOf(_loc.get(i)));
                            Log.wtf("UOM", String.valueOf(_uom.get(i)));
                            Log.wtf("QTY", String.valueOf(_qty.get(i)));
                        }
                        dataPasser.onDataPass(_ic,_dsc,_loc,_uom,_qty);

                        stockCount.reset();
                    } else {
                        Toast.makeText(getContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(getContext(), "Please fill in all fields",
                        Toast.LENGTH_SHORT).show();
            }
        }

//        public void onCheckBoxClicked(View view)
//        {
//            boolean checked = ((CheckBox)view).isChecked();
//            switch (view.getId())
//            {
//                case R.id.checkBoxBarcode:
//                    if (!checked) //no check
//                    {
//                        binding.checkBoxAuto.setEnabled(false);
//                        binding.itemTxt1.setVisibility(View.VISIBLE);
//                        binding.itemEditText.setVisibility(View.INVISIBLE);
//                    }
//                    else
//                    {
//                        binding.checkBoxAuto.setEnabled(true);
//                        binding.itemTxt1.setVisibility(View.INVISIBLE);
//                        binding.itemTxtDetail.setVisibility(View.INVISIBLE);
//                        binding.itemEditText.setVisibility(View.VISIBLE);
//                        binding.itemEditText.requestFocus();
//                    }
//                    break;
//
//                case R.id.checkBoxAuto:
//                    if (!checked)
//                    {
//                        binding.itemTxt1.requestFocus();
//                        binding.lblQty.setVisibility(View.VISIBLE);
//                        binding.btnInc.setVisibility(View.VISIBLE);
//                        binding.btnDec.setVisibility(View.VISIBLE);
//                        binding.tvQty.setVisibility(View.VISIBLE);
//                        binding.divider2.setVisibility(View.VISIBLE);
//                        binding.btnSave.setVisibility(View.VISIBLE);
//                        binding.checkBoxBarcode.setEnabled(true);
//                    }
//                    else
//                    {
//                        binding.checkBoxBarcode.setEnabled(false);
//                        binding.checkBoxBarcode.setEnabled(true);
//                        binding.itemTxt1.requestFocus();
//                        binding.lblQty.setVisibility(View.INVISIBLE);
//                        binding.btnInc.setVisibility(View.INVISIBLE);
//                        binding.btnDec.setVisibility(View.INVISIBLE);
//                        binding.tvQty.setVisibility(View.INVISIBLE);
//                        binding.divider2.setVisibility(View.INVISIBLE);
//                        binding.btnSave.setVisibility(View.INVISIBLE);
//                    }
//                    break;
//            }
//        }
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}