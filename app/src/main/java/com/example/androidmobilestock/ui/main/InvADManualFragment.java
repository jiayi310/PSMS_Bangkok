package com.example.androidmobilestock.ui.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
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

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.androidmobilestock.ACDatabase;
import com.example.androidmobilestock.AC_Class;
import com.example.androidmobilestock.Check_List;
import com.example.androidmobilestock.Discount_List;
import com.example.androidmobilestock.HistoryPrice;
import com.example.androidmobilestock.ItemBatchList;
import com.example.androidmobilestock.ItemUOMList;
import com.example.androidmobilestock.Item_List;
import com.example.androidmobilestock.Item_List_Sales;
import com.example.androidmobilestock.R;
import com.example.androidmobilestock.TaxType_List;
import com.example.androidmobilestock.databinding.FragmentInvAdmanualBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class InvADManualFragment extends Fragment {
    ACDatabase db;
    FragmentInvAdmanualBinding binding;
    AC_Class.InvoiceDetails invoiceDetails;
    AC_Class.Invoice invoiceHeader;
    Bundle args;
    String key;
    int mode;
    int i;
    int FilterByAgent = 0;
    int LineNo = 0;
    String Agent;
    Boolean isTaxEnabled = true;
    String defaultTax = "";
    Boolean isTaxInclusive;
    Boolean isAutoPrice = false;
    String defaultBatchNo = "";
    Boolean isBatchNoEnabled = true;
    Boolean isSaleBatchEnabled = true;
    String salesOrderType = "";
    float baseQty = 0f;
    float balanceqty = 0f;
    Boolean NegativeInventory = true;
    Boolean MinSellingPrice = true;
    EditText etQty;
    private IntentIntegrator qrScan;

    private OnFragmentInteractionListener mListener;

    Double dis;
    Double disNumber;
    String isPercentage, isChecked;
    Boolean hasBatchNoItem = false;
    NumberFormat nf = new DecimalFormat("##.###");

    AC_Class.Item item = new AC_Class.Item();


    public InvADManualFragment() {
        // Required empty public constructor
    }

    public static InvADManualFragment newInstance(Bundle bundle) {
        InvADManualFragment fragment = new InvADManualFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        String default_location = "";
        args = getArguments();
        args.getInt("mode", mode);
        super.onCreate(savedInstanceState);
        invoiceDetails = new AC_Class.InvoiceDetails();
        this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        db = new ACDatabase(getContext());

        Cursor dl = db.getReg("7");
        if (dl.moveToFirst()) {
            default_location = dl.getString(0);
        }
        invoiceDetails.setLocation(default_location);

        Cursor data2 = db.getReg("14");
        if (data2.moveToFirst()) {
            FilterByAgent = Integer.valueOf(data2.getString(0));
        }

        Cursor data3 = db.getReg("4");
        if (data3.moveToFirst()) {
            Agent = data3.getString(0);
        }

        Cursor cursor1 = db.getReg("21");
        if (cursor1.moveToFirst()) {
            defaultTax = cursor1.getString(0);
        }

        Cursor cursor3 = db.getReg("22");
        if (cursor3.moveToFirst()) {
            isTaxEnabled = Boolean.valueOf(cursor3.getString(0));
        }

        Cursor cursor2 = db.getReg("13");
        if (cursor2.moveToFirst()) {
            isTaxInclusive = Boolean.valueOf(cursor2.getString(0));
        }

        Cursor cursor4 = db.getReg("20");
        if (cursor4.moveToFirst()) {
            isAutoPrice = Boolean.valueOf(cursor4.getString(0));
        }

        Cursor cursor5 = db.getReg("38");
        if (cursor5.moveToFirst()) {
            isBatchNoEnabled = Boolean.valueOf(cursor5.getString(0));
        }

        Cursor cursor6 = db.getReg("37");
        if (cursor6.moveToFirst()) {
            defaultBatchNo = cursor6.getString(0);
        }

        Cursor cursor7 = db.getReg("39");
        if (cursor7.moveToFirst()) {
            isSaleBatchEnabled = Boolean.valueOf(cursor7.getString(0));
        }

        Cursor cursor8 = db.getReg("41");
        if (cursor8.moveToFirst()) {
            salesOrderType = cursor8.getString(0);
        }

        Cursor cursor9 = db.getReg("42");
        if (cursor9.moveToFirst()) {
            NegativeInventory = Boolean.valueOf(cursor9.getString(0));
        }

        Cursor cursor10 = db.getReg("73");
        if (cursor10.moveToFirst()) {
            MinSellingPrice = Boolean.valueOf(cursor10.getString(0));
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_inv_admanual, container, false);

        binding.lblBatchNo.setVisibility(View.GONE);
        binding.invdtlBatchnoTxt.setVisibility(View.GONE);
        binding.batchnoList.setVisibility(View.GONE);

        getInvoiceDetailsFrom();

        item = args.getParcelable("Item");

        if(item !=null) {
            getItem();
        }
        //MyClickHandler
        MyClickHandler handler = new MyClickHandler(getContext());
        binding.setHandler(handler);
        Log.i("custDebug", "Manual Bound");

        final View view = binding.getRoot();
        binding.setInvoiceDetail(invoiceDetails);

        if (isBatchNoEnabled && invoiceDetails.getBatchNo() != null) {
            binding.invdtlBatchnoTxt.setVisibility(View.VISIBLE);
            binding.lblBatchNo.setVisibility(View.VISIBLE);
            binding.batchnoList.setVisibility(View.VISIBLE);
        } else {
            binding.invdtlBatchnoTxt.setVisibility(View.GONE);
            binding.lblBatchNo.setVisibility(View.GONE);
            binding.batchnoList.setVisibility(View.GONE);
        }

        switch (key) {
            case "New":
                binding.invdtlItemTextView.setVisibility(View.INVISIBLE);
                break;
            case "Edit":

                if (invoiceDetails.getBatchNo() != null) {
                    Cursor temp2;
                    temp2 = db.getStockBalanceBatchNo(invoiceDetails.getItemCode(),
                            invoiceDetails.getUOM(), invoiceDetails.getBatchNo());
                    if (temp2.getCount() > 0) {
                        temp2.moveToNext();
                        balanceqty = temp2.getFloat(0);
                    }
                } else {
                    Cursor temp2;
                    temp2 = db.getStockBalance(invoiceDetails.getItemCode(),
                            invoiceDetails.getUOM());
                    if (temp2.getCount() > 0) {
                        temp2.moveToNext();
                        balanceqty = temp2.getFloat(0);
                    }
                }
                binding.invdtlStockTxt.setText("Remaining Balance: " + nf.format(balanceqty));
                binding.invdtlBarcodeEditText.setVisibility(View.INVISIBLE);
                break;
        }

        // Change format if tax enabled/disabled
        if (!isTaxEnabled) {
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
        }


        //Barcode editText
        binding.invdtlBarcodeEditText.requestFocus();
        binding.invdtlBarcodeEditText.setShowSoftInputOnFocus(false);

        binding.invdtlBarcodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    String temp = s.toString();
                    Cursor data = db.getItemBC(temp);
                    if (data.moveToNext()) {
                        invoiceDetails.setItemCode(data.getString(data.getColumnIndex("ItemCode")));
                        invoiceDetails.setItemDescription(data.getString(data.getColumnIndex(
                                "Description")));
                        invoiceDetails.setUOM(data.getString(data.getColumnIndex("UOM")));
                        invoiceDetails.setUPrice(data.getDouble(data.getColumnIndex("Price")));
                        String hasBatch = data.getString(data.getColumnIndex("HasBatchNo"));

                        //BatchNo
                        if (isBatchNoEnabled && hasBatch.equals("true")) {
                            hasBatchNoItem = true;
                            binding.lblBatchNo.setVisibility(View.VISIBLE);
                            binding.invdtlBatchnoTxt.setVisibility(View.VISIBLE);
                            binding.batchnoList.setVisibility(View.VISIBLE);
                            if (isSaleBatchEnabled) {

                                if (salesOrderType != "0" || salesOrderType != null) {

                                    if (salesOrderType.equals("Latest Manufacture Date")) {

                                        Cursor data2 =
                                                db.getLateManuBatch(invoiceDetails.getItemCode(),
                                                        invoiceDetails.getUOM(),
                                                        invoiceDetails.getLocation());

                                        if (data2.moveToFirst()) {

                                            invoiceDetails.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));

                                        } else {
                                            invoiceDetails.setBatchNo(null);
                                        }
                                    } else if (salesOrderType.equals("Earliest Manufacture Date")) {
                                        Cursor data2 =
                                                db.getEarManuBatch(invoiceDetails.getItemCode(),
                                                        invoiceDetails.getUOM(),
                                                        invoiceDetails.getLocation());

                                        if (data2.moveToFirst()) {
                                            invoiceDetails.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));
                                        } else {
                                            invoiceDetails.setBatchNo(null);
                                        }
                                    } else if (salesOrderType.equals("Latest Expiration Date")) {
                                        Cursor data2 =
                                                db.getLateExpBatch(invoiceDetails.getItemCode(),
                                                        invoiceDetails.getUOM(),
                                                        invoiceDetails.getLocation());

                                        if (data2.moveToFirst()) {

                                            invoiceDetails.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));

                                        } else {
                                            invoiceDetails.setBatchNo(null);
                                        }
                                    } else if (salesOrderType.equals("Earliest Expiration Date")) {
                                        Cursor data2 =
                                                db.getEarExpBatch(invoiceDetails.getItemCode(),
                                                        invoiceDetails.getUOM(),
                                                        invoiceDetails.getLocation());

                                        if (data2.moveToFirst()) {

                                            invoiceDetails.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));

                                        } else {
                                            invoiceDetails.setBatchNo(null);
                                        }
                                    }

                                } else {
                                    Cursor data2 =
                                            db.getEarManuBatch(invoiceDetails.getItemCode(),
                                                    invoiceDetails.getUOM(),
                                                    invoiceDetails.getLocation());

                                    if (data2.moveToFirst()) {

                                        invoiceDetails.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));

                                    } else {
                                        invoiceDetails.setBatchNo(null);
                                    }
                                    invoiceDetails.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));
                                }


                                if (invoiceDetails.getBatchNo() != null) {
                                    Cursor temp2;
                                    temp2 = db.getStockBalanceBatchNo(invoiceDetails.getItemCode(), invoiceDetails.getUOM(), invoiceDetails.getBatchNo());
                                    if (temp2.getCount() > 0) {
                                        temp2.moveToNext();
                                        balanceqty = temp2.getFloat(0);
                                    }
                                    binding.invdtlStockTxt.setText("Remaining Balance: " + nf.format(balanceqty));
                                }
                            } else {
                                Intent new_intent = new Intent(getContext(), ItemBatchList.class);
                                new_intent.putExtra("ItemCode", invoiceDetails.getItemCode());
                                new_intent.putExtra("UOM", invoiceDetails.getUOM());
                                new_intent.putExtra("Location", invoiceDetails.getLocation());
                                startActivityForResult(new_intent, 9);
                            }


                            binding.invdtlBatchnoTxt.setText(invoiceDetails.getBatchNo());
                        } else {
                            hasBatchNoItem = false;
                            binding.lblBatchNo.setVisibility(View.GONE);
                            binding.invdtlBatchnoTxt.setVisibility(View.GONE);
                            binding.batchnoList.setVisibility(View.GONE);
                        }

                        // TAX
                        invoiceDetails.setTaxType(getTax(data.getString(data.getColumnIndex(
                                "TaxType"))));
                        Cursor data2 = db.getTaxTypeValue(invoiceDetails.getTaxType());
                        Log.i("custDebug", getTax("None"));
                        while (data2.moveToNext()) {
                            invoiceDetails.setTaxRate(data2.getDouble(0));
                        }
                        Calculation();
                        binding.invdtlBarcodeEditText.setVisibility(View.INVISIBLE);
                        binding.invdtlItemTextView.setVisibility(View.VISIBLE);


                    } else {
                        final ToneGenerator tg =
                                new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    invoiceDetails.setDiscount(0.00d);
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
                    result = text.replace(",", "");
                    double disc = Double.parseDouble(result);
                    if (disc <= invoiceDetails.getSubTotal()) {
                        invoiceDetails.setDiscount(disc);
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

                if (qty.length() > 0 && !qty.equals("-")) {
                    invoiceDetails.setQuantity(Double.valueOf(qty));
                    Calculation();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.invdtlUnitpriceTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    invoiceDetails.setUPrice(0.00d);
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
                    result = text.replace(",", "");
                    invoiceDetails.setUPrice(Double.parseDouble(result));
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

    private void getItem() {
        if (item != null) {
            invoiceDetails.setItemCode(item.getItemCode());
            invoiceDetails.setItemDescription(item.getDescription());
            invoiceDetails.setUOM(item.getUOM());
            invoiceDetails.setDiscount(0.00d);
            invoiceDetails.setDiscountText(invoiceHeader.getDetailDiscount());
            invoiceDetails.setUPrice(Double.valueOf(item.getPrice()));

            //stock balance
            Cursor temp;
            temp = db.getStockBalance(invoiceDetails.getItemCode(),
                    invoiceDetails.getUOM());
            if (temp.getCount() > 0) {
                temp.moveToNext();
                balanceqty = temp.getFloat(0);
            }
            binding.invdtlStockTxt.setText("Remaining Balance: " + nf.format(balanceqty));

            if (isAutoPrice) {
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
                                        invoiceDetails.setUPrice(Double.valueOf(item.getPrice2()));
                                        break;
                                    case 3:
                                        invoiceDetails.setUPrice(Double.valueOf(item.getPrice3()));
                                        break;
                                    case 4:
                                        invoiceDetails.setUPrice(Double.valueOf(item.getPrice4()));
                                        break;
                                    case 5:
                                        invoiceDetails.setUPrice(Double.valueOf(item.getPrice5()));
                                        break;
                                    case 6:
                                        invoiceDetails.setUPrice(Double.valueOf(item.getPrice6()));
                                        break;
                                }
                            } catch (NumberFormatException e) {
                                throw e;
                            }
                        }
                    }
                }
            }


            if (isBatchNoEnabled && item.getHasBatchNo().equals("true")) {
                hasBatchNoItem = true;
                binding.lblBatchNo.setVisibility(View.VISIBLE);
                binding.invdtlBatchnoTxt.setVisibility(View.VISIBLE);
                binding.batchnoList.setVisibility(View.VISIBLE);
                if (isSaleBatchEnabled) {

                    if (salesOrderType != "0" || salesOrderType != null) {

                        if (salesOrderType.equals("Latest Manufacture Date")) {

                            Cursor data2 = db.getLateManuBatch(item.getItemCode(),
                                    invoiceDetails.getUOM(), invoiceDetails.getLocation());

                            if (data2.moveToFirst()) {
                                invoiceDetails.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));
                            } else {
                                invoiceDetails.setBatchNo(null);
                            }
                        } else if (salesOrderType.equals("Earliest Manufacture Date")) {
                            Cursor data2 = db.getEarManuBatch(item.getItemCode(),
                                    item.getUOM(), invoiceDetails.getLocation());

                            if (data2.moveToFirst()) {
                                invoiceDetails.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));
                            } else {
                                invoiceDetails.setBatchNo(null);
                            }
                        } else if (salesOrderType.equals("Latest Expiration Date")) {
                            Cursor data2 = db.getLateExpBatch(item.getItemCode(),
                                    item.getUOM(), invoiceDetails.getLocation());

                            if (data2.moveToFirst()) {
                                invoiceDetails.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));
                            } else {
                                invoiceDetails.setBatchNo(null);
                            }
                        } else if (salesOrderType.equals("Earliest Expiration Date")) {
                            Cursor data2 = db.getEarExpBatch(item.getItemCode(),
                                    item.getUOM(), invoiceDetails.getLocation());

                            if (data2.moveToFirst()) {
                                invoiceDetails.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));
                            } else {
                                invoiceDetails.setBatchNo(null);
                            }
                        }

                    } else {
                        Cursor data2 = db.getEarManuBatch(item.getItemCode(),
                                item.getUOM(), invoiceDetails.getLocation());

                        if (data2.moveToFirst()) {
                            invoiceDetails.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));
                        } else {
                            invoiceDetails.setBatchNo(null);
                        }
                        invoiceDetails.setBatchNo(data2.getString(data2.getColumnIndex(
                                "BatchNo")));
                    }


                    if (invoiceDetails.getBatchNo() != null) {
                        Cursor temp2;
                        temp2 = db.getStockBalanceBatchNo(invoiceDetails.getItemCode(),
                                invoiceDetails.getUOM(), invoiceDetails.getBatchNo());
                        if (temp2.getCount() > 0) {
                            temp2.moveToNext();
                            balanceqty = temp2.getFloat(0);
                        }
                        binding.invdtlStockTxt.setText("Remaining Balance: " + nf.format(balanceqty));
                    }
                } else {
                    Intent new_intent = new Intent(getContext(), ItemBatchList.class);
                    new_intent.putExtra("ItemCode", invoiceDetails.getItemCode());
                    new_intent.putExtra("UOM", invoiceDetails.getUOM());
                    new_intent.putExtra("Location", invoiceDetails.getLocation());
                    startActivityForResult(new_intent, 9);
                }


                binding.invdtlBatchnoTxt.setText(invoiceDetails.getBatchNo());
            } else {
                hasBatchNoItem = false;
                binding.lblBatchNo.setVisibility(View.GONE);
                binding.invdtlBatchnoTxt.setVisibility(View.GONE);
                binding.batchnoList.setVisibility(View.GONE);
                invoiceDetails.setBatchNo(null);
            }

            invoiceDetails.setTaxType(getTax(item.getTaxType()));

            Cursor data2 = db.getTaxTypeValue(invoiceDetails.getTaxType());
            while (data2.moveToNext()) {
                invoiceDetails.setTaxRate(data2.getDouble(0));
            }

            Calculation();

            binding.invdtlBarcodeEditText.setText(invoiceDetails.getItemCode());
        }
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
                    invoiceDetails.setItemCode(item.getItemCode());
                    invoiceDetails.setItemDescription(item.getDescription());
                    invoiceDetails.setUOM(item.getUOM());
                    invoiceDetails.setDiscount(0.00d);
                    invoiceDetails.setDiscountText(invoiceHeader.getDetailDiscount());
                    invoiceDetails.setUPrice(Double.valueOf(item.getPrice()));

                    //stock balance
                    Cursor temp;
                    temp = db.getStockBalance(invoiceDetails.getItemCode(),
                            invoiceDetails.getUOM());
                    if (temp.getCount() > 0) {
                        temp.moveToNext();
                        balanceqty = temp.getFloat(0);
                    }
                    binding.invdtlStockTxt.setText("Remaining Balance: " + nf.format(balanceqty));

                    if (isAutoPrice) {
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
                                                invoiceDetails.setUPrice(Double.valueOf(item.getPrice2()));
                                                break;
                                            case 3:
                                                invoiceDetails.setUPrice(Double.valueOf(item.getPrice3()));
                                                break;
                                            case 4:
                                                invoiceDetails.setUPrice(Double.valueOf(item.getPrice4()));
                                                break;
                                            case 5:
                                                invoiceDetails.setUPrice(Double.valueOf(item.getPrice5()));
                                                break;
                                            case 6:
                                                invoiceDetails.setUPrice(Double.valueOf(item.getPrice6()));
                                                break;
                                        }
                                    } catch (NumberFormatException e) {
                                        throw e;
                                    }
                                }
                            }
                        }
                    }


                    if (isBatchNoEnabled && item.getHasBatchNo().equals("true")) {
                        hasBatchNoItem = true;
                        binding.lblBatchNo.setVisibility(View.VISIBLE);
                        binding.invdtlBatchnoTxt.setVisibility(View.VISIBLE);
                        binding.batchnoList.setVisibility(View.VISIBLE);
                        if (isSaleBatchEnabled) {

                            if (salesOrderType != "0" || salesOrderType != null) {

                                if (salesOrderType.equals("Latest Manufacture Date")) {

                                    Cursor data2 = db.getLateManuBatch(item.getItemCode(),
                                            invoiceDetails.getUOM(), invoiceDetails.getLocation());

                                    if (data2.moveToFirst()) {
                                        invoiceDetails.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));
                                    } else {
                                        invoiceDetails.setBatchNo(null);
                                    }
                                } else if (salesOrderType.equals("Earliest Manufacture Date")) {
                                    Cursor data2 = db.getEarManuBatch(item.getItemCode(),
                                            item.getUOM(), invoiceDetails.getLocation());

                                    if (data2.moveToFirst()) {
                                        invoiceDetails.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));
                                    } else {
                                        invoiceDetails.setBatchNo(null);
                                    }
                                } else if (salesOrderType.equals("Latest Expiration Date")) {
                                    Cursor data2 = db.getLateExpBatch(item.getItemCode(),
                                            item.getUOM(), invoiceDetails.getLocation());

                                    if (data2.moveToFirst()) {
                                        invoiceDetails.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));
                                    } else {
                                        invoiceDetails.setBatchNo(null);
                                    }
                                } else if (salesOrderType.equals("Earliest Expiration Date")) {
                                    Cursor data2 = db.getEarExpBatch(item.getItemCode(),
                                            item.getUOM(), invoiceDetails.getLocation());

                                    if (data2.moveToFirst()) {
                                        invoiceDetails.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));
                                    } else {
                                        invoiceDetails.setBatchNo(null);
                                    }
                                }

                            } else {
                                Cursor data2 = db.getEarManuBatch(item.getItemCode(),
                                        item.getUOM(), invoiceDetails.getLocation());

                                if (data2.moveToFirst()) {
                                    invoiceDetails.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));
                                } else {
                                    invoiceDetails.setBatchNo(null);
                                }
                                invoiceDetails.setBatchNo(data2.getString(data2.getColumnIndex(
                                        "BatchNo")));
                            }


                            if (invoiceDetails.getBatchNo() != null) {
                                Cursor temp2;
                                temp2 = db.getStockBalanceBatchNo(invoiceDetails.getItemCode(),
                                        invoiceDetails.getUOM(), invoiceDetails.getBatchNo());
                                if (temp2.getCount() > 0) {
                                    temp2.moveToNext();
                                    balanceqty = temp2.getFloat(0);
                                }
                                binding.invdtlStockTxt.setText("Remaining Balance: " + nf.format(balanceqty));
                            }
                        } else {
                            Intent new_intent = new Intent(getContext(), ItemBatchList.class);
                            new_intent.putExtra("ItemCode", invoiceDetails.getItemCode());
                            new_intent.putExtra("UOM", invoiceDetails.getUOM());
                            new_intent.putExtra("Location", invoiceDetails.getLocation());
                            startActivityForResult(new_intent, 9);
                        }


                        binding.invdtlBatchnoTxt.setText(invoiceDetails.getBatchNo());
                    } else {
                        hasBatchNoItem = false;
                        binding.lblBatchNo.setVisibility(View.GONE);
                        binding.invdtlBatchnoTxt.setVisibility(View.GONE);
                        binding.batchnoList.setVisibility(View.GONE);
                        invoiceDetails.setBatchNo(null);
                    }

                    invoiceDetails.setTaxType(getTax(item.getTaxType()));

                    Cursor data2 = db.getTaxTypeValue(invoiceDetails.getTaxType());
                    while (data2.moveToNext()) {
                        invoiceDetails.setTaxRate(data2.getDouble(0));
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
                        invoiceDetails.setTaxType(taxtype.getTaxType());
                        invoiceDetails.setTaxRate(Double.valueOf(taxtype.getTaxRate()));
                        Calculation();
                    }
                }
                break;

            //Return from UOM selection
            case 6:
                if (data != null) {
                    AC_Class.ItemUOM itemUOM = data.getParcelableExtra("UOMKey");
                    if (itemUOM != null) {
                        invoiceDetails.setUOM(itemUOM.getUOM());
                        invoiceDetails.setUPrice(Double.valueOf(itemUOM.getPrice()));
                        Calculation();
                    }
                }
                break;

            case 7:
                if (data != null) {
                    AC_Class.SellingPrice sellingPrice = data.getParcelableExtra("price");

                    if (sellingPrice != null) {
                        if (sellingPrice.getPrice() != 0) {
                            invoiceDetails.setUPrice(Double.valueOf(sellingPrice.getPrice()));
                            Calculation();
                        }
                    }
                }
                break;

            case 8:
                if (data != null) {
                    dis = data.getDoubleExtra("Discount", 0.00);

                    if (dis != null && dis != 0.00) {

                        isPercentage = data.getStringExtra("IsPercentage");
                        isChecked = data.getStringExtra("IsChecked");

                        if (isPercentage.equals("True")) {
                            dis = dis / 100;
                            disNumber = invoiceDetails.getUPrice() * invoiceDetails.getQuantity() * dis;
                        } else {
                            if (isChecked.equals("True")) {
                                disNumber = dis * invoiceDetails.getQuantity();

                            } else {
                                disNumber = dis;
                            }
                        }

                        invoiceDetails.setDiscount(disNumber);
                        Calculation();
                    }
                }
                break;

            case 9:
                if (data != null) {
                    String batchNo = data.getStringExtra("ItemBatchNo");
                    if (batchNo != null) {
                        invoiceDetails.setBatchNo(batchNo);
                        binding.invdtlBatchnoTxt.setText(batchNo);
                        if (isBatchNoEnabled) {
                            if (invoiceDetails.getBatchNo() != null) {
                                //stock balance
                                Cursor temp2;
                                temp2 = db.getStockBalanceBatchNo(invoiceDetails.getItemCode(),
                                        invoiceDetails.getUOM(), invoiceDetails.getBatchNo());
                                if (temp2.getCount() > 0) {
                                    temp2.moveToNext();
                                    balanceqty = temp2.getFloat(0);
                                }
                                binding.invdtlStockTxt.setText("Remaining Balance: " + nf.format(balanceqty));
                            }
                        }

                    }
                }
                break;


            case 10:
                if(data != null) {
                    Float price = data.getFloatExtra("Price", 0.0f);
                    if (price != null) {
                        invoiceDetails.setUPrice(Double.valueOf(price));
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
            Intent new_intent = new Intent(view.getContext(), Item_List_Sales.class);
            new_intent.putExtra("debtorcode", invoiceHeader.getDebtorCode());
            new_intent.putExtra("substring", "");
            startActivityForResult(new_intent, 4);
        }

        public void OnItemTextViewClicked(View view) {
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

            } catch (Exception e) {
                Log.i("custDebug", "OnItemCameraClicked - " + e.getMessage());
            }
        }

        public void BtnIncClicked(View view) {
            invoiceDetails.setQuantity(invoiceDetails.getQuantity() + 1);
            etQty = (EditText) getView().findViewById(R.id.editTextNumber2);
            etQty.setText(String.format("%.0f", invoiceDetails.getQuantity()));
            Calculation();
        }

        public void BtnDecClicked(View view) {
            invoiceDetails.setQuantity(invoiceDetails.getQuantity() - 1);
            etQty = (EditText) getView().findViewById(R.id.editTextNumber2);
            etQty.setText(String.format("%.0f", invoiceDetails.getQuantity()));
            Calculation();
        }

        public void onTaxTypeTxtViewClicked(View view) {
            Intent new_intent = new Intent(context, TaxType_List.class);
            startActivityForResult(new_intent, 5);
        }

        public void onCheckPriceClicked(View view) {
            if (invoiceDetails.getItemCode() != null) {
                Intent new_intent = new Intent(context, Check_List.class);
                new_intent.putExtra("ItemCode", invoiceDetails.getItemCode());
                new_intent.putExtra("ItemUOMKey", invoiceDetails.getUOM());
                startActivityForResult(new_intent, 7);
            }

        }

        public void onCheckDiscountClicked(View view) {
            if (invoiceDetails.getItemCode() != null) {
                Intent new_intent = new Intent(context, Discount_List.class);
                new_intent.putExtra("ItemCode", invoiceDetails.getItemCode());
                startActivityForResult(new_intent, 8);
            }

        }

        public void onBatchNoListClicked(View view) {
            if (invoiceDetails.getItemCode() != null) {
                Intent new_intent = new Intent(context, ItemBatchList.class);
                new_intent.putExtra("ItemCode", invoiceDetails.getItemCode());
                new_intent.putExtra("UOM", invoiceDetails.getUOM());
                new_intent.putExtra("Location", invoiceDetails.getLocation());
                startActivityForResult(new_intent, 9);
            }
        }

        public void onHistoryPriceViewClicked(View view) {
            if (invoiceDetails.getItemCode() != null) {
                Intent new_intent = new Intent(context, HistoryPrice.class);
                new_intent.putExtra("DebtorKey", invoiceHeader.getDebtorCode());
                new_intent.putExtra("ItemKey", invoiceDetails.getItemCode());
                new_intent.putExtra("ItemUOMKey", invoiceDetails.getUOM());
                new_intent.putExtra("Agent", Agent);
                new_intent.putExtra("FilterByAgent", FilterByAgent);
                startActivityForResult(new_intent, 10);
            }
        }

        public void onUOMTxtViewClicked(View view) {
            if (invoiceDetails.getItemCode() != null) {
                Intent new_intent = new Intent(context, ItemUOMList.class);
                new_intent.putExtra("ItemKey", invoiceDetails.getItemCode());
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
            } else if (MinSellingPrice.equals(true)) {
                if (CheckPrice()) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Please make sure unit price is more than minimum selling " +
                            "price.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                }
                else if (invoiceDetails.getQuantity() > balanceqty) {
                    if (NegativeInventory.equals(true)) {
                        if (isBatchNoEnabled && hasBatchNoItem) {
                            String batch = binding.invdtlBatchnoTxt.getText().toString();
                            if (batch == null || batch.equals(""))
                                Toast.makeText(context, "Please fill in batch no",
                                        Toast.LENGTH_SHORT).show();
                            else
                                SaveData();
                        } else {
                            SaveData();
                        }
                    } else {
                        Toast.makeText(context, "Out of Stock", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    SaveData();
                }
            } else if (invoiceDetails.getQuantity() > balanceqty) {
                if (NegativeInventory.equals(true)) {
                    if (isBatchNoEnabled && hasBatchNoItem) {
                        String batch = binding.invdtlBatchnoTxt.getText().toString();
                        if (batch == null || batch.equals(""))
                            Toast.makeText(context, "Please fill in batch no",
                                    Toast.LENGTH_SHORT).show();
                        else
                            SaveData();
                    } else {
                        SaveData();
                    }
                } else {
                    Toast.makeText(context, "Out of Stock", Toast.LENGTH_SHORT).show();
                }
            } else {
                SaveData();
            }
        }

        public void OnCancelBtnClicked(View view) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//            builder.setTitle("Attention!");
//            builder.setMessage("Are you sure to cancel all details?");
//            builder.setPositiveButton("Yes", new DialogInterface
//            .onHistoryPriceViewClicked}Listener() {
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
//        Log.i("custDebug", invoiceDetails.getLocation()+"-\n"+ invoiceDetails.getItemCode()+"-\n"+
//                invoiceDetails.getItemDescription()+"-\n"+ invoiceDetails.getUOM()+"-\n"+
//                invoiceDetails.getUPrice()+"-\n"+ invoiceDetails.getSubTotal()+"-\n"+
//                invoiceDetails.getTaxType()+"-\n"+ invoiceDetails.getTaxRate()+"-\n"+
//                invoiceDetails.getTaxValue()+"-\n"+ invoiceDetails.getTotal_Ex()+"-\n"+
//                invoiceDetails.getTotal_In()+"-");
//        Log.i("custDebug", String.valueOf(invoiceDetails.TaxValue==null));

        return (invoiceDetails.getLocation() == null ||
                invoiceDetails.getItemCode() == null ||
                invoiceDetails.getItemDescription() == null ||
                invoiceDetails.getUOM() == null ||
                invoiceDetails.getUPrice() == null ||
                invoiceDetails.getSubTotal() == null ||
                //invoiceDetails.getTaxType() == null ||
                invoiceDetails.getTaxRate() == null ||
                invoiceDetails.getTaxValue() == null ||
                invoiceDetails.getTotal_Ex() == null ||
                invoiceDetails.getTotal_In() == null ||
                invoiceDetails.getQuantity() <= 0);
    }

    public boolean CheckPrice() {
        Double price = null;
        Double price1 = null;
        Cursor data = db.getItem(invoiceDetails.ItemCode, invoiceDetails.UOM);
        while (data.moveToNext()) {
            price = data.getDouble(19);
        }

        return (invoiceDetails.getUPrice() < price);
    }

    public void Calculation() {
        if (isTaxInclusive) {
            // Inclusive
            invoiceDetails.setTotal_In(invoiceDetails.getQuantity() * invoiceDetails.getUPrice() - invoiceDetails.getDiscount());
            // Calc. tax from total
            invoiceDetails.setTaxValue((invoiceDetails.getTotal_In() *
                    invoiceDetails.getTaxRate()) / (100 + invoiceDetails.getTaxRate()));
            invoiceDetails.setTotal_Ex(invoiceDetails.getTotal_In() - invoiceDetails.getTaxValue());
            invoiceDetails.setSubTotal((invoiceDetails.getTotal_Ex() + invoiceDetails.getDiscount()));
            Log.wtf("Subtotal", String.valueOf(invoiceDetails.getSubTotal()));

        } else {
            if (invoiceDetails.getDiscountText() != null && !invoiceDetails.getDiscountText().isEmpty()) {

                double discountTextValue = Double.parseDouble(invoiceDetails.getDiscountText());

                double subtotalAfterCustomerDiscount = (invoiceDetails.getQuantity() * invoiceDetails.getUPrice()) - discountTextValue;

                double finalSubtotal = subtotalAfterCustomerDiscount - invoiceDetails.getDiscount();


                invoiceDetails.setSubTotal(finalSubtotal);
            } else {
                invoiceDetails.setSubTotal((invoiceDetails.getQuantity() * invoiceDetails.getUPrice()) - invoiceDetails.getDiscount());
            }

            invoiceDetails.setTotal_Ex(invoiceDetails.getSubTotal());
            invoiceDetails.setTaxValue((invoiceDetails.getTotal_Ex() * invoiceDetails.getTaxRate()) / 100);
            invoiceDetails.setTotal_In(invoiceDetails.getTotal_Ex() + invoiceDetails.getTaxValue());

            Log.wtf("Subtotal", String.valueOf(invoiceDetails.getSubTotal()));

        }
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

    public void SaveData() {
        invoiceDetails.setRemarks(binding.invdtlRemarkTxt.getText().toString());
        invoiceDetails.setRemarks2(binding.invdtlRemarkTxt2.getText().toString());
        Intent newIntent = new Intent();
        newIntent.putExtra("invoiceDetails", invoiceDetails);
        getActivity().setResult(0, newIntent);
        getActivity().finish();
    }

    public void getInvoiceDetailsFrom() {
        key = args.getString("FunctionKey");
        invoiceHeader = args.getParcelable("Inv");

        Log.wtf("Qty on edit", String.valueOf(invoiceDetails.getQuantity()));

        switch (key) {
            case "Edit":
                invoiceDetails = args.getParcelable("invoiceDetails");
                if (isBatchNoEnabled) {
                    binding.invdtlBatchnoTxt.setVisibility(View.VISIBLE);
                    binding.lblBatchNo.setVisibility(View.VISIBLE);
                    binding.invdtlBatchnoTxt.setText(invoiceDetails.getBatchNo());
                }

                binding.editTextNumber2.setText(new DecimalFormat("#.###").format(invoiceDetails.getQuantity()));
                break;

            case "New":
                invoiceDetails.setDocNo(invoiceHeader.getDocNo());
                invoiceDetails.setDocDate(invoiceHeader.getDocDate());
                break;
        }
    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager imm =
                (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


}

