package com.example.androidmobilestock_bangkok;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.androidmobilestock_bangkok.databinding.PurPurchasedtlAddmanualBinding;
import com.example.androidmobilestock_bangkok.widget.BatchNo;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PUR_PurchaseDtl_AddManual extends Fragment {
    ACDatabase db;
    PurPurchasedtlAddmanualBinding binding;
    AC_Class.PurchaseDetails docDtl;
    AC_Class.Purchase doc;
    Bundle args;
    String key;
    int mode;
    int i;
    String Agent;
    Boolean isTaxEnabled = true;
    String defaultTax = "";
    Boolean isTaxInclusive;
    String defaultBatchNo = "";
    Boolean isBatchNoEnabled = true;
    Boolean isPurBatchEnabled = true;
    Boolean hasBatchNoItem = false;

    EditText etQty;
    private IntentIntegrator qrScan;

    private OnFragmentInteractionListener mListener;

    public PUR_PurchaseDtl_AddManual() {
        // Required empty public constructor
    }

    public static PUR_PurchaseDtl_AddManual newInstance(Bundle bundle) {
        PUR_PurchaseDtl_AddManual fragment = new PUR_PurchaseDtl_AddManual();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        String default_location = "";
        args = getArguments();
        //args.getInt("iMode", mode);
        super.onCreate(savedInstanceState);


        docDtl = new AC_Class.PurchaseDetails();
        this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        db = new ACDatabase(getContext());

        Cursor dl = db.getReg("7");
        if(dl.moveToFirst()){
            default_location = dl.getString(0);
        }
        docDtl.setLocation(default_location);

        Cursor agent = db.getReg("24");
        if(agent.moveToFirst()){
            Agent  = agent.getString(0);
        }

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

        Cursor cursor4 = db.getReg("38");
        if(cursor4.moveToFirst()){
            isBatchNoEnabled = Boolean.valueOf(cursor4.getString(0));
        }

        Cursor cursor5 = db.getReg("37");
        if(cursor5.moveToFirst()){
            defaultBatchNo = cursor5.getString(0);
        }
        Cursor cursor6 = db.getReg("40");
        if(cursor6.moveToFirst()){
            isPurBatchEnabled= Boolean.valueOf(cursor6.getString(0));
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        binding = DataBindingUtil.inflate(inflater,
                R.layout.pur_purchasedtl_addmanual, container, false);

        binding.lblBatchNo.setVisibility(View.GONE);
        binding.invdtlBatchnoTxt.setVisibility(View.GONE);


        getDetailsFrom();

        //MyClickHandler
        MyClickHandler handler = new MyClickHandler(getContext());
        binding.setHandler(handler);
        Log.i("custDebug", "Manual Bound");

        final View view = binding.getRoot();
        binding.setDtl(docDtl);

        switch (key){
            case "New":
                binding.invdtlItemTextView.setVisibility(View.INVISIBLE);
                break;
            case "Edit":
                if(docDtl.getBatch_No()==null || docDtl.getBatch_No().equals("")){
                    binding.invdtlBatchnoTxt.setVisibility(View.GONE);
                    binding.lblBatchNo.setVisibility(View.GONE);
                }else{
                    binding.invdtlBatchnoTxt.setVisibility(View.VISIBLE);
                    binding.lblBatchNo.setVisibility(View.VISIBLE);
                }
                binding.invdtlBarcodeEditText.setVisibility(View.INVISIBLE);
                break;
        }

        // Change format if tax enabled/disabled
        if (!isTaxEnabled){
            binding.invdtlLblSubtotal.setVisibility(View.GONE);
            binding.invdtlSubtotalTxt.setVisibility(View.GONE);
            binding.invdtlLblTaxtype.setVisibility(View.GONE);
            binding.invdtlTaxtypeTxt.setVisibility(View.GONE);
            binding.invdtlLblTaxrate.setVisibility(View.GONE);
            binding.invdtlTaxrateTxt.setVisibility(View.GONE);
            binding.invdtlLblTaxValue.setVisibility(View.GONE);
            binding.invdtlTaxvalueTxt.setVisibility(View.GONE);
            binding.invdtlLblTotalEx.setVisibility(View.GONE);
            binding.invdtlTotalexTxt.setVisibility(View.GONE);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(binding.relativeLayout15);
            constraintSet.connect(R.id.lbl_total_in, ConstraintSet.TOP,
                    R.id.invdtl_discount_txt, ConstraintSet.BOTTOM);
            constraintSet.applyTo(binding.relativeLayout15);
            constraintSet.applyTo(binding.relativeLayout15);
        }

        //Barcode editText
        binding.invdtlBarcodeEditText.requestFocus();
        binding.invdtlBarcodeEditText.setShowSoftInputOnFocus(false);

        binding.invdtlBarcodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    String temp = s.toString();
                    Cursor data = db.getItemBC(temp);
                    if (data.moveToNext()) {
                        docDtl.setItemCode(data.getString(data.getColumnIndex("ItemCode")));
                        docDtl.setItemDescription(data.getString(data.getColumnIndex("Description")));
                        docDtl.setUOM(data.getString(data.getColumnIndex("UOM")));
                        docDtl.setUPrice(0.00d);

                        String hasBatch = data.getString(data.getColumnIndex("HasBatchNo"));
                        //BatchNo
                        if(isBatchNoEnabled && hasBatch.equals("true")){
                            hasBatchNoItem = true;
                            binding.lblBatchNo.setVisibility(View.VISIBLE);
                            binding.invdtlBatchnoTxt.setVisibility(View.VISIBLE);

                            if(isPurBatchEnabled) {
                                Date d = new Date();
                                String mm = new SimpleDateFormat("MM").format(d);
                                String yy = new SimpleDateFormat("yy").format(d);
                                String yyyy = new SimpleDateFormat("yyyy").format(d);

                                if (defaultBatchNo.contains("{MM}")) {
                                    defaultBatchNo = defaultBatchNo.replace("{MM}", mm);
                                }
                                if (defaultBatchNo.contains("{YY}")) {
                                    defaultBatchNo = defaultBatchNo.replace("{YY}", yy);
                                }
                                if (defaultBatchNo.contains("{YYYY}")) {
                                    defaultBatchNo = defaultBatchNo.replace("{YYYY}", yyyy);
                                }
                                binding.invdtlBatchnoTxt.setText(defaultBatchNo);
                            }

                        }else{
                            hasBatchNoItem = false;
                            binding.lblBatchNo.setVisibility(View.GONE);
                            binding.invdtlBatchnoTxt.setVisibility(View.GONE);
                        }
                        docDtl.setBatch_No(binding.invdtlBatchnoTxt.getText().toString());
                        // TAX
                        docDtl.setTaxType(getTax(data.getString(data.getColumnIndex("TaxType"))));
                        Cursor data2 = db.getTaxTypeValue(docDtl.getTaxType());
                        Log.i("custDebug", getTax("None"));
                        while (data2.moveToNext()) {
                            docDtl.setTaxRate(data2.getDouble(0));
                        }
                        Calculation();
                        binding.invdtlBarcodeEditText.setVisibility(View.INVISIBLE);
                        binding.invdtlItemTextView.setVisibility(View.VISIBLE);
                    } else {
                        final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                        tg.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                        alertDialog.setTitle("Alert");
                        alertDialog.setMessage("Product not found.");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                    binding.invdtlBarcodeEditText.removeTextChangedListener(this);
                    binding.invdtlBarcodeEditText.clearFocus();
                    binding.invdtlBarcodeEditText.setText("");
                    binding.invdtlBarcodeEditText.addTextChangedListener(this);
                    binding.invdtlBarcodeEditText.requestFocus();
                    hideSoftKeyboard(getView());
                }
            }
        });

        binding.invdtlDiscountTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    docDtl.setDiscount(0.00d);
                    return;
                }

                binding.invdtlDiscountTxt.removeTextChangedListener(this);

                /*
                 * Clear input to get clean text before format
                 * '\u0020' is empty character
                 */
                String text = s.toString();
                String result = "";
                // thanks for: https://stackoverflow.com/a/10372905/1595442
                text = text.replaceAll("[^\\d]", "");

                try {
                    text = format(text);
                    result = text.replace("," ,"");
                    double disc = Double.parseDouble(result);
                    if (disc <= docDtl.getSubTotal()) {
                        docDtl.setDiscount(disc);
                        Calculation();
                    } else {
                        binding.invdtlDiscountTxt.setError("Discount cannot exceed subtotal");
                    }
                } catch (NumberFormatException e) {
                    Log.e(getClass().getCanonicalName(), e.getMessage());
                } catch (NullPointerException e) {
                    Log.e(getClass().getCanonicalName(), e.getMessage());
                }

                binding.invdtlDiscountTxt.setText(result);
                binding.invdtlDiscountTxt.setSelection(result.length());

                binding.invdtlDiscountTxt.addTextChangedListener(this);
            }

            @Override
            public void afterTextChanged(Editable s) {
                /*if (s.length() > 0) {
                    float disc = Float.parseFloat(s.toString());
                    if (disc <= invoiceDetails.getSubTotal()) {
                        invoiceDetails.setDiscount(disc);
                       Calculation();
                    } else {
                        binding.invdtlDiscountTxt.setError("Discount cannot exceed subtotal");
                        binding.invdtlDiscountTxt.setText(String.format(Locale.getDefault(),
                                "%.2f", invoiceDetails.getDiscount()));
                    }
               }*/
            }
        });

        binding.editTextNumber2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etQty = (EditText) getView().findViewById(R.id.editTextNumber2);

                String qty = etQty.getText().toString();

                if(qty.length() > 0)
                {
                    docDtl.setQuantity(Double.valueOf(qty));
                    Calculation();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.invdtlUnitpriceTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (s.length() == 0) {
                    docDtl.setUPrice(0.00d);
                    return;
                }

                binding.invdtlUnitpriceTxt.removeTextChangedListener(this);

                /*
                 * Clear input to get clean text before format
                 * '\u0020' is empty character
                 */
                String text = s.toString();
                String result = "";
                text = text.replaceAll("[^\\d]", "");

                try {
                    text = format(text);
                    result = text.replace("," ,"");
                    docDtl.setUPrice(Double.parseDouble(result));
                    Calculation();
                } catch (NumberFormatException e) {
                    Log.e(getClass().getCanonicalName(), e.getMessage());
                } catch (NullPointerException e) {
                    Log.e(getClass().getCanonicalName(), e.getMessage());
                }

                binding.invdtlUnitpriceTxt.setText(result);
                binding.invdtlUnitpriceTxt.setSelection(result.length());

                binding.invdtlUnitpriceTxt.addTextChangedListener(this);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }

    private String format(String text) throws NumberFormatException, NullPointerException {
        DecimalFormat numberFormat = new DecimalFormat();
        numberFormat.setMinimumFractionDigits(2);
        return numberFormat.format(Double.parseDouble(text) / Math.pow(10, 2));
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().findViewById(R.id.invdtl_barcode_editText).requestFocus();
        hideSoftKeyboard(getActivity().findViewById(R.id.invdtl_barcode_editText));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //Return from item selection
            case 4:
                AC_Class.Item item = data.getParcelableExtra("ItemsKey");
                if (item != null) {
                    docDtl.setItemCode(item.getItemCode());
                    docDtl.setItemDescription(item.getDescription());
                    docDtl.setUOM(item.getUOM());
                    docDtl.setDiscount(0.00d);
                    docDtl.setUPrice(0.00d);
                    docDtl.setTaxType(getTax(item.getTaxType()));

                    if(isBatchNoEnabled && item.getHasBatchNo().equals("true")){
                        hasBatchNoItem = true;
                        binding.lblBatchNo.setVisibility(View.VISIBLE);
                        binding.invdtlBatchnoTxt.setVisibility(View.VISIBLE);

                        if(isPurBatchEnabled) {
                            Date d = new Date();
                            String mm = new SimpleDateFormat("MM").format(d);
                            String yy = new SimpleDateFormat("yy").format(d);
                            String yyyy = new SimpleDateFormat("yyyy").format(d);

                            if (defaultBatchNo.contains("{MM}")) {
                                defaultBatchNo = defaultBatchNo.replace("{MM}", mm);
                            }
                            if (defaultBatchNo.contains("{YY}")) {
                                defaultBatchNo = defaultBatchNo.replace("{YY}", yy);
                            }
                            if (defaultBatchNo.contains("{YYYY}")) {
                                defaultBatchNo = defaultBatchNo.replace("{YYYY}", yyyy);
                            }
                            binding.invdtlBatchnoTxt.setText(defaultBatchNo);
                        }

                    }else{
                        hasBatchNoItem = false;
                        binding.lblBatchNo.setVisibility(View.GONE);
                        binding.invdtlBatchnoTxt.setVisibility(View.GONE);
                    }


                    Cursor data2 = db.getTaxTypeValue(docDtl.getTaxType());
                    while (data2.moveToNext()) {
                        docDtl.setTaxRate(data2.getDouble(0));
                    }
                    Calculation();
                    getActivity().findViewById(R.id.invdtl_item_textView).setVisibility(View.VISIBLE);
                    getActivity().findViewById(R.id.invdtl_barcode_editText).setVisibility(View.INVISIBLE);
                }
                break;

            //Return from tax selection
            case 5:
                if (data != null) {
                    AC_Class.TaxType taxtype = data.getParcelableExtra("TaxTypesKey");
                    if (taxtype != null) {
                        docDtl.setTaxType(taxtype.getTaxType());
                        docDtl.setTaxRate(Double.valueOf(taxtype.getTaxRate()));
                        Calculation();
                    }
                }
                break;

            //Return from UOM selection
            case 6:
                if(data != null) {
                    AC_Class.ItemUOM itemUOM = data.getParcelableExtra("UOMKey");
                    if (itemUOM != null) {
                        docDtl.setUOM(itemUOM.getUOM());
                        docDtl.setUPrice(0.00d);
                        Calculation();
                    }
                }
                break;



            case IntentIntegrator.REQUEST_CODE:
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode,
                        resultCode, data);
                if (result != null) {
                    if (result.getContents() == null) {
                        Toast.makeText(this.getContext(), "No result found.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        binding.invdtlBarcodeEditText.setText(result.getContents());

                    }
                } else {
                    super.onActivityResult(requestCode, resultCode, data);
                }
                break;
        }
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
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public class MyClickHandler {
        Context context;

        public MyClickHandler(Context context) {
            this.context = context;
        }

        public void onItemTxtViewClicked(View view) {
            Intent new_intent = new Intent(view.getContext(), Item_List.class);
            new_intent.putExtra("substring", "");
            new_intent.putExtra("key", "FromPurchase");
            startActivityForResult(new_intent, 4);
        }

        public void OnItemTextViewClicked(View view)
        {
            getActivity().findViewById(R.id.invdtl_item_textView).setVisibility(View.INVISIBLE);
            getActivity().findViewById(R.id.invdtl_barcode_editText).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.invdtl_barcode_editText).requestFocus();
            hideSoftKeyboard(view);
        }

        public void OnItemCameraClicked(View view) {
            try {
//                Intent new_intent = new Intent(StockInquiry.this, activity_scanner.class);
//                startActivity(new_intent);
                qrScan = new IntentIntegrator(getActivity());
                qrScan.setPrompt("Scan a Barcode/QRCode");
                qrScan.setCameraId(0);
                qrScan.setBeepEnabled(true);
                //qrScan.setBarcodeImageEnabled(false);
                qrScan.setOrientationLocked(false);
                Intent intent = qrScan.createScanIntent();
                startActivityForResult(intent, IntentIntegrator.REQUEST_CODE);

            } catch (Exception e) { Log.i("custDebug", "OnItemCameraClicked - " + e.getMessage()); }
        }

        public void BtnIncClicked(View view) {
            docDtl.setQuantity(docDtl.getQuantity() + 1);
            etQty = (EditText) getView().findViewById(R.id.editTextNumber2);
            etQty.setText(String.format("%.0f",docDtl.getQuantity()));
            Calculation();
        }

        public void BtnDecClicked(View view) {
            docDtl.setQuantity(docDtl.getQuantity() - 1);
            etQty = (EditText) getView().findViewById(R.id.editTextNumber2);
            etQty.setText(String.format("%.0f",docDtl.getQuantity()));
            Calculation();
        }

        public void onBatchNoClicked(View view) {
            Intent new_intent = new Intent(context, BatchNo.class);
        }

        public void onTaxTypeTxtViewClicked(View view) {
            Intent new_intent = new Intent(context, TaxType_List.class);
            startActivityForResult(new_intent, 5);
        }


        public void onUOMTxtViewClicked(View view) {
            if (docDtl.getItemCode() != null) {
                Intent new_intent = new Intent(context, ItemUOMList.class);
                new_intent.putExtra("ItemKey", docDtl.getItemCode());
                startActivityForResult(new_intent, 6);
            }
        }


        public void OnSaveBtnClicked(View view) {
            if (CheckEmpty()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Please make sure that all the details have been filled.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else {
                docDtl.setBatch_No(binding.invdtlBatchnoTxt.getText().toString());

                if(isBatchNoEnabled && hasBatchNoItem){
                    String batch = binding.invdtlBatchnoTxt.getText().toString();
                    if(batch == null || batch.equals(""))
                        Toast.makeText(context, "Please fill in batch no", Toast.LENGTH_SHORT).show();
                    else
                        SaveData();
                }else {
                    SaveData();
                }

            }
        }

        public void OnCancelBtnClicked(View view) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//            builder.setTitle("Attention!");
//            builder.setMessage("Are you sure to cancel all details?");
//            builder.setPositiveButton("Yes", new DialogInterface.onHistoryPriceViewClicked}Listener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    getActivity().onBackPressed();
//                }
//            });
//            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            });
//            AlertDialog dialog = builder.create();
//            dialog.show();
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
                docDtl.getTotal_In() == null || docDtl.getQuantity()==0);
    }

    public void Calculation()
    {
        if (isTaxInclusive){
            // Inclusive
            docDtl.setTotal_In(docDtl.getQuantity() * docDtl.getUPrice());
            // Calc. tax from total
            docDtl.setTaxValue((docDtl.getTotal_In() *
                    docDtl.getTaxRate()) / (100+docDtl.getTaxRate()));
            docDtl.setTotal_Ex(docDtl.getTotal_In() - docDtl.getTaxValue());
            docDtl.setSubTotal((docDtl.getTotal_Ex() + docDtl.getDiscount()));
            Log.wtf("Subtotal", String.valueOf(docDtl.getSubTotal()));

        } else {
            // Exclusive
            docDtl.setSubTotal((docDtl.getQuantity() * docDtl.getUPrice()));
            docDtl.setTotal_Ex(docDtl.getSubTotal() - docDtl.getDiscount());
            docDtl.setTaxValue((docDtl.getTotal_Ex() * docDtl.getTaxRate()) / 100);
            docDtl.setTotal_In(docDtl.getTotal_Ex() + docDtl.getTaxValue());
            Log.wtf("Subtotal", String.valueOf(docDtl.getSubTotal()));
        }
    }

    public String getTax(String itemTaxType) {
        if (isTaxEnabled) {
            AC_Class.Purchase invoice = args.getParcelable("iDoc");
            if (invoice.getTaxType() != null) {
                return invoice.getTaxType();
            } else if (itemTaxType != null) {
                return itemTaxType;
            } else {
                return defaultTax;
            }
        }
        return "None";
    }

    public void SaveData()
    {
        Intent newIntent = new Intent();
        newIntent.putExtra("iDocDtl", docDtl);
        getActivity().setResult(0, newIntent);
        getActivity().finish();
    }

    public void getDetailsFrom()
    {
        key = args.getString("iFunc");
        doc = args.getParcelable("iDoc");

        Log.wtf("Qty on edit", String.valueOf(docDtl.getQuantity()));

        switch (key) {
            case "Edit":
                docDtl = args.getParcelable("iDocDtl");
                if(docDtl.getBatch_No()==null || docDtl.getBatch_No().equals("")){
                    binding.invdtlBatchnoTxt.setVisibility(View.GONE);
                    binding.lblBatchNo.setVisibility(View.GONE);
                }else{
                    binding.invdtlBatchnoTxt.setVisibility(View.VISIBLE);
                    binding.lblBatchNo.setVisibility(View.VISIBLE);
                    binding.invdtlBatchnoTxt.setText(docDtl.getBatch_No());
                }
                binding.editTextNumber2.setText(new DecimalFormat("#.###").format(docDtl.getQuantity()));
                break;

            case "New":
                docDtl.setDocNo(doc.getDocNo());
                docDtl.setDocDate(doc.getDocDate());
                break;
        }
    }

    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}

