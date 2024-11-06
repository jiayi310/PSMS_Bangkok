package com.example.androidmobilestock_bangkok.ui.main;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.androidmobilestock_bangkok.ACDatabase;
import com.example.androidmobilestock_bangkok.AC_Class;
import com.example.androidmobilestock_bangkok.R;
import com.example.androidmobilestock_bangkok.adapter.InvoiceDtlAdapter;
import com.example.androidmobilestock_bangkok.databinding.FragmentInvAdbarcodeBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class InvADBarcodeFragment extends Fragment {
    ACDatabase db;
    FragmentInvAdbarcodeBinding binding;
    AC_Class.InvoiceDetails invoiceDetails;
    ArrayList<AC_Class.InvoiceDetails> itemlist;
    InvoiceDtlAdapter adapter;
    Bundle args;
    int mode;
    Boolean isTaxEnabled = true;
    String defaultTax = "";
    Boolean isTaxInclusive;
    Boolean isAutoPrice = false;

    private IntentIntegrator qrScan;

    boolean isCustomBarcodeEnabled = false;
    int barcodeLength = 0;
    int itemStart = 0;
    int itemLength = 0;
    int qtyStart = 0;
    int qtyLength = 0;
    int qtyDecimal = 0;

    private InvADManualFragment.OnFragmentInteractionListener mListener;

    public InvADBarcodeFragment() {
        // Required empty public constructor
    }

    public static InvADBarcodeFragment newInstance(Bundle bundle) {
        InvADBarcodeFragment fragment = new InvADBarcodeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        args = getArguments();
        args.getInt("mode", mode);
        super.onCreate(savedInstanceState);
        invoiceDetails = new AC_Class.InvoiceDetails();
        this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        db = new ACDatabase(getContext());

        itemlist = new ArrayList<>();

        Cursor cursor1 = db.getReg("21");
        if(cursor1.moveToFirst()){
            defaultTax = cursor1.getString(0);
        }

        Cursor cursor3 = db.getReg("22");
        if(cursor3.moveToFirst()){
            isTaxEnabled = Boolean.valueOf(cursor3.getString(0));
        }

        Cursor cursor2 = db.getReg("13");
        if(cursor2.moveToFirst()){
            isTaxInclusive = Boolean.valueOf(cursor2.getString(0));
        }

        Cursor data = db.getReg("25");
        if(data.moveToFirst()){
            isCustomBarcodeEnabled = Boolean.valueOf(data.getString(0));
        }

        Cursor cursor4 = db.getReg("20");
        if(cursor4.moveToFirst()){
            isAutoPrice = Boolean.valueOf(cursor4.getString(0));
        }


        if (isCustomBarcodeEnabled) {

            Cursor data2 = db.getReg("26");
            if(data2.moveToFirst()){
                barcodeLength = Integer.valueOf(data2.getString(0));
            }

            Cursor data3 = db.getReg("27");
            if(data3.moveToFirst()){
                itemStart = Integer.valueOf(data3.getString(0));
            }

            Cursor data4 = db.getReg("28");
            if(data4.moveToFirst()){
                itemLength = Integer.valueOf(data4.getString(0));
            }

            Cursor data5 = db.getReg("29");
            if(data5.moveToFirst()){
                qtyStart = Integer.valueOf(data5.getString(0));
            }

            Cursor data6 = db.getReg("30");
            if(data6.moveToFirst()){
                qtyLength = Integer.valueOf(data6.getString(0));
            }

            Cursor data7 = db.getReg("31");
            if(data7.moveToFirst()){
                qtyDecimal = Integer.valueOf(data7.getString(0));
            }

        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_inv_adbarcode, container, false);

        // Listview Adapter
        binding.invadbarcodeItemlist.setHasFixedSize(true);
        binding.invadbarcodeItemlist.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new InvoiceDtlAdapter(this.getActivity(), itemlist);
        binding.invadbarcodeItemlist.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //MyClickHandler
        MyClickHandler handler = new MyClickHandler(getContext());
        binding.setHandler(handler);
        Log.i("custDebug", "Barcode Bound");

        final View view = binding.getRoot();
        AC_Class.Invoice invoiceHeader = args.getParcelable("Inv");
        binding.setDocNo(invoiceHeader.getDocNo());

        // Request focus
        binding.invdtlEditText.requestFocus();
        binding.invdtlEditText.setShowSoftInputOnFocus(false);

        binding.invdtlEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String myBarcode = s.toString();
                String default_location = "";
                double myQty = 1;

                invoiceDetails = new AC_Class.InvoiceDetails();
                getInvoiceDetailsFrom();

                Cursor dl = db.getReg("7");
                if(dl.moveToFirst()){
                    default_location = dl.getString(0);
                }
                invoiceDetails.setLocation(default_location);

                if (!myBarcode.equals("") && !TextUtils.isEmpty(binding.invdtlEditText.getText())) {
                    boolean isSkip = false;

                    if (isCustomBarcodeEnabled) {
                        String normalBarcode = binding.invdtlEditText.getText().toString();
                        Cursor data = db.getItemBC(normalBarcode);
                        if (data.getCount() == 0){
                            if (myBarcode.length() == barcodeLength) {
                                if (isNumeric(myBarcode)) {
                                    myQty = Double.valueOf(myBarcode.substring(qtyStart - 1, qtyStart - 1 + qtyLength));
                                    if (qtyDecimal > 0) {
                                        myQty = myQty / Math.pow(10,qtyDecimal);
                                    }
                                    myBarcode = myBarcode.substring(itemStart - 1, itemStart - 1 + itemLength);
                                } else {
                                    Toast.makeText(getContext(), "Invalid Barcode.",
                                            Toast.LENGTH_SHORT).show();
                                    isSkip = true;
                                }
                            }
                        }

                    }

                    if (!isSkip)
                    {
                        Cursor results = db.getItemBC(myBarcode);

                        int nItemCode = results.getColumnIndex("ItemCode");
                        int nDescription = results.getColumnIndex("Description");
                        int nUOM = results.getColumnIndex("UOM");
                        int nPrice = results.getColumnIndex("Price");
                        int nTaxType = results.getColumnIndex("TaxType");

                        if (results.getCount() == 0) {
                            Toast.makeText(getContext(), "Product not found.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            results.moveToNext();

                            invoiceDetails.setItemCode(results.getString(nItemCode));
                            invoiceDetails.setItemDescription(results.getString(nDescription));
                            invoiceDetails.setUOM(results.getString(nUOM));
                            invoiceDetails.setUPrice(results.getDouble(nPrice));

                            if (isAutoPrice)
                            {
                                Cursor cursor_pc = db.getPriceCategory(invoiceHeader.getDebtorCode());
                                if (cursor_pc != null) {
                                    if (cursor_pc.moveToFirst()) {
                                        Object myPCObj = cursor_pc.getString(0);

                                        if (myPCObj != null) {
                                            try {
                                                Integer myPC = 0;
                                                myPC = Integer.parseInt(myPCObj.toString());

                                                switch (myPC) {
                                                    case 2:
                                                        invoiceDetails.setUPrice(Double.valueOf(results.getString(results.getColumnIndex("Price2"))));
                                                        break;
                                                    case 3:
                                                        invoiceDetails.setUPrice(Double.valueOf(results.getString(results.getColumnIndex("Price3"))));
                                                        break;
                                                    case 4:
                                                        invoiceDetails.setUPrice(Double.valueOf(results.getString(results.getColumnIndex("Price4"))));
                                                        break;
                                                    case 5:
                                                        invoiceDetails.setUPrice(Double.valueOf(results.getString(results.getColumnIndex("Price5"))));
                                                        break;
                                                    case 6:
                                                        invoiceDetails.setUPrice(Double.valueOf(results.getString(results.getColumnIndex("Price6"))));
                                                        break;
                                                }
                                            } catch (NumberFormatException e) {
                                                throw e;
                                            }
                                        }
                                    }
                                }
                            }


                            invoiceDetails.setQuantity(myQty);
                            invoiceDetails.setTaxType(getTax(results.getString(nTaxType)));
                            Cursor data2 = db.getTaxTypeValue(invoiceDetails.getTaxType());
                            if (data2.moveToNext()) {
                                invoiceDetails.setTaxRate(data2.getDouble(0));
                            }
                            Calculation();

                            if (!CheckEmpty()) {
                                insertItem(invoiceDetails);
                            } else {
                                Toast.makeText(getActivity(), "Invalid Barcode", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                binding.invdtlEditText.removeTextChangedListener(this);
                binding.invdtlEditText.clearFocus();
                binding.invdtlEditText.requestFocus();
                hideSoftKeyboard(getView());
                binding.invdtlEditText.setText("");
                binding.invdtlEditText.addTextChangedListener(this);
            }
        });

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
        if (context instanceof InvADManualFragment.OnFragmentInteractionListener) {
            mListener = (InvADManualFragment.OnFragmentInteractionListener) context;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IntentIntegrator.REQUEST_CODE:
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode,
                        resultCode, data);
                if (result != null) {
                    if (result.getContents() == null) {
                        Toast.makeText(this.getContext(), "No result found.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        binding.invdtlEditText.setText(result.getContents());
                    }
                } else {
                    super.onActivityResult(requestCode, resultCode, data);
                }
                break;
        }
    }

    public class MyClickHandler {
        Context context;

        public MyClickHandler(Context context) {
            this.context = context;
        }

        public void onEditTextViewClicked(View view) {
            getActivity().findViewById(R.id.invdtl_editText).requestFocus();
            hideSoftKeyboard(view);
        }

        public void OnItemCameraClicked(View view) {
            try {
                qrScan = new IntentIntegrator(getActivity());
                qrScan.setPrompt("Scan a Barcode/QRCode");
                qrScan.setCameraId(0);
                qrScan.setBeepEnabled(true);
                qrScan.setOrientationLocked(false);
                Intent intent = qrScan.createScanIntent();
                startActivityForResult(intent, IntentIntegrator.REQUEST_CODE);
                //binding.invdtlEditText.setText("2501735075008");

            } catch (Exception e) { Log.i("custDebug", "OnItemCameraClicked - " + e.getMessage()); }
        }

        public void OnSaveBtnClicked(View view) {
            if (itemlist.size()<1) {
                Intent newIntent = new Intent();
                getActivity().setResult(10, newIntent);
                getActivity().finish();
            } else {
                Intent newIntent = new Intent();
                newIntent.putParcelableArrayListExtra("invDtlList", itemlist);
                getActivity().setResult(0, newIntent);
                getActivity().finish();
            }
        }

        public void OnCancelBtnClicked(View view) {
            getActivity().onBackPressed();
        }
    }

    public boolean CheckEmpty() {
        return (invoiceDetails.getLocation() == null ||
                invoiceDetails.getItemCode() == null ||
                invoiceDetails.getItemDescription() == null ||
                invoiceDetails.getUOM() == null ||
                invoiceDetails.getUPrice() == null ||
                invoiceDetails.getSubTotal() == null ||
                invoiceDetails.getTaxType() == null ||
                invoiceDetails.getTaxRate() == null ||
                invoiceDetails.getTaxValue() == null ||
                invoiceDetails.getTotal_Ex() == null ||
                invoiceDetails.getTotal_In() == null);
    }

    public void getInvoiceDetailsFrom()
    {
        AC_Class.Invoice invoice = args.getParcelable("Inv");
        invoiceDetails.setDocNo(invoice.getDocNo());
        invoiceDetails.setDocDate(invoice.getDocDate());
    }

    public void Calculation()
    {
        invoiceDetails.setSubTotal((invoiceDetails.getQuantity() * invoiceDetails.getUPrice()));
        invoiceDetails.setTotal_Ex(invoiceDetails.getSubTotal() - invoiceDetails.getDiscount());
        invoiceDetails.setTaxValue((invoiceDetails.getTotal_Ex() * invoiceDetails.getTaxRate()) / 100);
        invoiceDetails.setTotal_In(invoiceDetails.getTotal_Ex() + invoiceDetails.getTaxValue());
    }

    public String getTax(String itemTaxType) {
        if (isTaxEnabled) {
            AC_Class.Invoice invoice = args.getParcelable("Inv");
            if (invoice.getTaxType() != null && !invoice.getTaxType().isEmpty()) {
                return invoice.getTaxType();
            } else if (itemTaxType != null && !itemTaxType.isEmpty()) {
                return itemTaxType;
            } else {
                return defaultTax;
            }
        }
        return "None";
    }

    private int checkInvoiceDetailList(AC_Class.InvoiceDetails invoiceDetails){
        int result = -1;

        for(int i = 0; i < itemlist.size(); i++) {
            AC_Class.InvoiceDetails tempDetail = itemlist.get(i);

            if (tempDetail.getItemCode().equals(invoiceDetails.getItemCode()) && tempDetail.getUOM().equals(invoiceDetails.getUOM())){
                result = i;
            }
        }
        return result;
    }

    void insertItem(AC_Class.InvoiceDetails invoiceDetails) {
        if (!isCustomBarcodeEnabled) {
            int myResult = checkInvoiceDetailList(invoiceDetails);

            if (myResult >= 0) {
                itemlist.get(myResult).setQuantity(itemlist.get(myResult).getQuantity() + 1);
                updateInvDtls(itemlist.get(myResult));
            } else {
                itemlist.add(invoiceDetails);
            }
        }
        else {
            itemlist.add(invoiceDetails);
        }

//        if (locMap.containsKey(invoiceDetails.ItemCode)) {
//            Integer temp = locMap.get(invoiceDetails.ItemCode);
//            itemlist.get(temp).setQuantity(itemlist.get(temp).getQuantity()+1);
//            updateInvDtls(itemlist.get(temp));
//        } else {
//            locMap.put(invoiceDetails.ItemCode, itemlist.size());
//            itemlist.add(invoiceDetails);
//        }
        adapter.notifyDataSetChanged();
    }

    void updateInvDtls(AC_Class.InvoiceDetails invoiceDetails) {
        invoiceDetails.setSubTotal((invoiceDetails.getQuantity() * invoiceDetails.getUPrice()));
        invoiceDetails.setTotal_Ex(invoiceDetails.getSubTotal() - invoiceDetails.getDiscount());
        invoiceDetails.setTaxValue((invoiceDetails.getTotal_Ex() * invoiceDetails.getTaxRate()) / 100);
        invoiceDetails.setTotal_In(invoiceDetails.getTotal_Ex() + invoiceDetails.getTaxValue());
    }

    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager)getActivity().
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
