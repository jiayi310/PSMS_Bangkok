package com.example.androidmobilestock;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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

import com.example.androidmobilestock.databinding.PurFragmentPurchasedtlAddbarcodeBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class PUR_PurchaseDtl_AddBarcode extends Fragment {
    ACDatabase db;
    PurFragmentPurchasedtlAddbarcodeBinding binding;
    AC_Class.PurchaseDetails docDtl;
    ArrayList<AC_Class.PurchaseDetails> docDtllist;
    PUR_PurchaseDtlListViewAdapter adapter;
    Bundle args;
    int mode;
    Boolean isTaxEnabled = true;
    String defaultTax = "";
    Boolean isTaxInclusive;

    private IntentIntegrator qrScan;

    boolean isCustomBarcodeEnabled = false;
    int barcodeLength = 0;
    int itemStart = 0;
    int itemLength = 0;
    int qtyStart = 0;
    int qtyLength = 0;
    int qtyDecimal = 0;

    private PUR_PurchaseDtl_AddManual.OnFragmentInteractionListener mListener;

    public PUR_PurchaseDtl_AddBarcode() {
        // Required empty public constructor
    }

    public static PUR_PurchaseDtl_AddBarcode newInstance(Bundle bundle) {
        PUR_PurchaseDtl_AddBarcode fragment = new PUR_PurchaseDtl_AddBarcode();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        args = getArguments();
        //args.getInt("iMode", mode);
        super.onCreate(savedInstanceState);
        docDtl = new AC_Class.PurchaseDetails();
        this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        db = new ACDatabase(getContext());

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

        docDtllist = new ArrayList<>();
//        locMap = new HashMap<>();

        Cursor data = db.getReg("25");
        if(data.moveToFirst()){
            isCustomBarcodeEnabled = Boolean.valueOf(data.getString(0));
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
        binding = DataBindingUtil.inflate(inflater,
                R.layout.pur_fragment_purchasedtl_addbarcode, container, false);

        // Listview Adapter
        adapter = new PUR_PurchaseDtlListViewAdapter(this.getActivity(), docDtllist);
        binding.invadbarcodeItemlist.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //MyClickHandler
        MyClickHandler handler = new MyClickHandler(getContext());
        binding.setHandler(handler);
        Log.i("custDebug", "Barcode Bound");

        final View view = binding.getRoot();
        AC_Class.Purchase doc = args.getParcelable("iDoc");
        binding.setDocNo(doc.getDocNo());

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

                docDtl = new AC_Class.PurchaseDetails();
                getDetailsFrom();

                Cursor dl = db.getReg("7");
                if(dl.moveToFirst()){
                    default_location = dl.getString(0);
                }
                docDtl.setLocation(default_location);

                if (!myBarcode.equals("") && !TextUtils.isEmpty(binding.invdtlEditText.getText())) {
                    boolean isSkip = false;

                    if (isCustomBarcodeEnabled)
                    {
                        if (myBarcode.length() == barcodeLength)
                        {
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

                    if (!isSkip)
                    {
                        Cursor results = db.getItemBC(myBarcode);
                        if (results.getCount() == 0) {
                            Toast.makeText(getContext(), "Product not found.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            results.moveToNext();

                            String hasBatch = results.getString(results.getColumnIndex("HasBatchNo"));

                            docDtl.setItemCode(results.getString(results.getColumnIndex("ItemCode")));
                            docDtl.setItemDescription(results.getString(results.getColumnIndex("Description")));
                            docDtl.setUOM(results.getString(results.getColumnIndex("UOM")));
                            //docDtl.setUPrice(results.getDouble(4));

                            docDtl.setQuantity(myQty);



                            // TAX
                            docDtl.setTaxType(getTax(results.getString(results.getColumnIndex("TaxType"))));
                            Cursor data2 = db.getTaxTypeValue(docDtl.getTaxType());
                            if (data2.moveToNext()) {
                                docDtl.setTaxRate(data2.getDouble(0));
                            }
                            Calculation();

                            if (!CheckEmpty()) {
                                insertItem(docDtl);
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
        if (context instanceof PUR_PurchaseDtl_AddManual.OnFragmentInteractionListener) {
            mListener = (PUR_PurchaseDtl_AddManual.OnFragmentInteractionListener) context;
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

            } catch (Exception e) { Log.i("custDebug", "OnItemCameraClicked - " + e.getMessage()); }
        }

        public void OnSaveBtnClicked(View view) {
            if (docDtllist.size()<1) {
                Intent newIntent = new Intent();
                getActivity().setResult(10, newIntent);
                getActivity().finish();
            } else {
                Intent newIntent = new Intent();
                newIntent.putParcelableArrayListExtra("iDocDtlList", docDtllist);
                getActivity().setResult(0, newIntent);
                getActivity().finish();
            }
        }

        public void OnCancelBtnClicked(View view) {
            getActivity().onBackPressed();
        }
    }

    public boolean CheckEmpty() {
        return (docDtl.getLocation() == null ||
                docDtl.getItemCode() == null ||
                docDtl.getItemDescription() == null ||
                docDtl.getUOM() == null ||
                docDtl.getUPrice() == null ||
                docDtl.getSubTotal() == null ||
                docDtl.getTaxType() == null ||
                docDtl.getTaxRate() == null ||
                docDtl.getTaxValue() == null ||
                docDtl.getTotal_Ex() == null ||
                docDtl.getTotal_In() == null);
    }

    public void getDetailsFrom()
    {
        AC_Class.Purchase doc = args.getParcelable("iDoc");
        docDtl.setDocNo(doc.getDocNo());
        docDtl.setDocDate(doc.getDocDate());
    }

    public void Calculation()
    {
        docDtl.setSubTotal((docDtl.getQuantity() * docDtl.getUPrice()));
        docDtl.setTotal_Ex(docDtl.getSubTotal() - docDtl.getDiscount());
        docDtl.setTaxValue((docDtl.getTotal_Ex() * docDtl.getTaxRate()) / 100);
        docDtl.setTotal_In(docDtl.getTotal_Ex() + docDtl.getTaxValue());
    }

    public String getTax(String itemTaxType) {
        if (isTaxEnabled) {
            AC_Class.Purchase doc = args.getParcelable("iDoc");
            if (doc.getTaxType() != null) {
                return doc.getTaxType();
            } else if (itemTaxType != null) {
                return itemTaxType;
            } else {
                return defaultTax;
            }
        }
        return "None";
    }

    private int checkDetailList(AC_Class.PurchaseDetails dtlFP){
        int result = -1;

        for(int i = 0; i < docDtllist.size(); i++) {
            AC_Class.PurchaseDetails tempDetail = docDtllist.get(i);

            if (tempDetail.getItemCode().equals(dtlFP.getItemCode()) && tempDetail.getUOM().equals(dtlFP.getUOM())){
                result = i;
            }
        }
        return result;
    }

    void insertItem(AC_Class.PurchaseDetails dtlFP) {
        if (!isCustomBarcodeEnabled) {
            int myResult = checkDetailList(dtlFP);
            if (myResult >= 0) {
                docDtllist.get(myResult).setQuantity(docDtllist.get(myResult).getQuantity()+1);
                updateDtl(docDtllist.get(myResult));
            } else {
                docDtllist.add(dtlFP);
            }
        }
        else {
            docDtllist.add(dtlFP);
        }

        adapter.notifyDataSetChanged();
    }

    void updateDtl(AC_Class.PurchaseDetails dtlFP) {
        dtlFP.setSubTotal((dtlFP.getQuantity() * dtlFP.getUPrice()));
        dtlFP.setTotal_Ex(dtlFP.getSubTotal() - dtlFP.getDiscount());
        dtlFP.setTaxValue((dtlFP.getTotal_Ex() * dtlFP.getTaxRate()) / 100);
        dtlFP.setTotal_In(dtlFP.getTotal_Ex() + dtlFP.getTaxValue());
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
