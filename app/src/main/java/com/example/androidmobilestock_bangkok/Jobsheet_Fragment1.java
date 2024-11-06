package com.example.androidmobilestock_bangkok;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class Jobsheet_Fragment1 extends Fragment {

    private static final String ARG_DOC_NO = "docNo";
    private static final String ARG_DEBTOR_CODE = "debtorCode";

    private String docNo;
    private String debtorCode;
    TextView no_history_textView;
    RecyclerView item_recyclerView;
    ACDatabase db;
    Jobsheet_Details_Item_Adapter adapter;
    //Button convert_btn;
    AC_Class.JobSheet jobSheet;
    AC_Class.JobSheetDetails jobsheetDetails;
    List<AC_Class.InvoiceDetails> invoiceDetailsList;
    AC_Class.Invoice invoice;
    AC_Class.InvoiceDetails invoiceDetails;
    String invoiceDocNo, currentDateandTime, dateTime;

    public static Jobsheet_Fragment1 newInstance(String docNo, String debtorCode) {
        Jobsheet_Fragment1 fragment = new Jobsheet_Fragment1();
        Bundle args = new Bundle();
        args.putString(ARG_DOC_NO, docNo);
        args.putString(ARG_DEBTOR_CODE, debtorCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            docNo = getArguments().getString(ARG_DOC_NO);
            debtorCode = getArguments().getString(ARG_DEBTOR_CODE);
        }
        db = new ACDatabase(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_jobsheet_1, container, false);

        no_history_textView = view.findViewById(R.id.no_history_textView);
        //convert_btn = view.findViewById(R.id.convert_btn);
        item_recyclerView = view.findViewById(R.id.item_recyclerView);
        item_recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        List<AC_Class.JobSheetDetails> jobSheetDetailsList = getJSDetails(docNo);

        /*
        boolean isConverted = db.isJobSheetConverted(docNo);
        if (isConverted) {
            convert_btn.setVisibility(View.GONE);
            Log.d("JobSheet", "JobSheet with docNo " + docNo + " is converted.");
        } else {
            convert_btn.setVisibility(View.VISIBLE);
            Log.d("JobSheet", "JobSheet with docNo " + docNo + " is not converted.");
        }

         */

        if (jobSheetDetailsList.size() > 0){
            no_history_textView.setVisibility(View.GONE);
            //convert_btn.setVisibility(View.VISIBLE);
            adapter = new Jobsheet_Details_Item_Adapter(getContext(), jobSheetDetailsList);
            item_recyclerView.setAdapter(adapter);
        } else {
            //convert_btn.setVisibility(View.GONE);
            no_history_textView.setVisibility(View.VISIBLE);
        }


        invoiceDocNo = db.getNextNo();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault());
        currentDateandTime = sdf.format(new Date());

//        convert_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new AlertDialog.Builder(requireContext())
//                        .setTitle("Confirmation")
//                        .setMessage("Are you sure you want to convert to sales?")
//                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                            }
//                        })
//                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                //refer to jobsheet add item
//                                jobSheet = db.getJobSheetOByDocNo(docNo);
//                                convertToInvoice();
//                                invoiceDetailsList = convertJobSheetDetailsToInvoiceDetails(jobSheetDetailsList);
//
//                                //save invoice
//                                boolean insertHeader = db.insertInv(invoice);
//                                if (invoice.getDocNo().equals(db.getNextNo())) {
//                                    db.IncrementAutoNumbering("S");
//                                    convert_btn.setVisibility(View.GONE);
//                                }
//                                if (!insertHeader){
//                                    Toast.makeText(getContext(), "Failed to insert Invoice to database", Toast.LENGTH_SHORT).show();
//                                }
//
//                                //Update Status
//                                boolean jobSheetStatusUpdated = db.updateJobSheetStatus(jobSheet.getDocNo(), "Converted");
//                                //Update Invoice No to jobsheet table
//                                boolean salesNoUpdated = db.updateJSInvoiceDocNo(jobSheet.getDocNo(), invoiceDocNo);
//                                //save invoice detail
//                                boolean invoiceDetailsSaved = db.saveInvoiceDetails(invoiceDetailsList);
//                                if (invoiceDetailsSaved && jobSheetStatusUpdated && salesNoUpdated) {
//                                    // Successfully saved
//                                    Log.d("Invoice", "Invoice and details saved successfully.");
//                                    Toast.makeText(getContext(), "Sales created successfully !", Toast.LENGTH_SHORT).show();
//
//                                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                                    builder.setTitle("Attention!");
//                                    builder.setMessage("Do you want to view the sale invoice?");
//                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            dialog.dismiss();
//                                            Intent intent = new Intent(getContext(), InvoiceDtlMultipleTab.class);
//                                            intent.putExtra("docNo", invoice.getDocNo());
//                                            intent.putExtra("debtorCode", invoice.getDebtorCode());
//                                            startActivity(intent);
//                                        }
//                                    });
//                                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            dialog.dismiss();
//                                        }
//                                    });
//                                    AlertDialog dialog2 = builder.create();
//                                    dialog2.show();
//
//
//
//                                } else {
//                                    // Failed to save
//                                    Log.d("Invoice", "Failed to save invoice or details.");
//                                }
//                            }
//                        })
//                        .show();
//
//            }
//        });

        return view;
    }

    private void convertToInvoice() {
        //set invoice
        invoice = new AC_Class.Invoice();
        invoice.setDocNo(invoiceDocNo);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateTime = sdf.format(new Date());

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        String currentDT = sdf2.format(new Date());

        invoice.setDocDate(dateTime);
        invoice.setCreatedTimeStamp(currentDT);
        invoice.setDocType("IV");
        invoice.setDebtorCode(jobSheet.getDebtorCode());
        invoice.setDebtorName(jobSheet.getDebtorName());
        invoice.setAddress1(jobSheet.getAddress1());
        invoice.setAddress2(jobSheet.getAddress2());
        invoice.setAddress3(jobSheet.getAddress3());
        invoice.setAddress4(jobSheet.getAddress4());
        invoice.setAgent(jobSheet.getAgent());
        invoice.setPhone(jobSheet.getPhone());
        invoice.setFax(jobSheet.getFax());
        invoice.setAttention(jobSheet.getAttention());
        invoice.setRemarks(jobSheet.getRemarks());
        invoice.setRemarks2(jobSheet.getRemarks2());
        invoice.setRemarks3(jobSheet.getRemarks3());
        invoice.setRemarks4(jobSheet.getRemarks4());
        invoice.setStatus("Pending");

        invoice.setCreateduser(jobSheet.getCreatedUser());
        invoice.setLastModifiedUser(jobSheet.getLastModifiedUser());
        invoice.setLastModifiedDateTime(jobSheet.getLastModifiedDateTime());

    }

    public List<AC_Class.InvoiceDetails> convertJobSheetDetailsToInvoiceDetails(List<AC_Class.JobSheetDetails> jobSheetDetailsList) {
        List<AC_Class.InvoiceDetails> invoiceDetailsList = new ArrayList<>();

        for (AC_Class.JobSheetDetails jobsheetDetails : jobSheetDetailsList) {
            AC_Class.InvoiceDetails invoiceDetails = new AC_Class.InvoiceDetails();

            invoiceDetails.setDocNo(invoiceDocNo);
            invoiceDetails.setDocDate(currentDateandTime);

            invoiceDetails.setLocation(jobsheetDetails.getLocation());
            invoiceDetails.setItemCode(jobsheetDetails.getItemCode());
            invoiceDetails.setItemDescription(jobsheetDetails.getItemDescription());
            invoiceDetails.setUOM(jobsheetDetails.getUOM());
            invoiceDetails.setQuantity(jobsheetDetails.getQuantity());
            invoiceDetails.setUPrice(jobsheetDetails.getUPrice());
            invoiceDetails.setDiscount(jobsheetDetails.getDiscount());
            invoiceDetails.setSubTotal(jobsheetDetails.getSubTotal());
            invoiceDetails.setTaxType(jobsheetDetails.getTaxType());
            invoiceDetails.setTaxRate(jobsheetDetails.getTaxRate());
            invoiceDetails.setTaxValue(jobsheetDetails.getTaxValue());
            invoiceDetails.setTotal_Ex(jobsheetDetails.getTotal_Ex());
            invoiceDetails.setTotal_In(jobsheetDetails.getTotal_In());
            invoiceDetails.setLine_No(jobsheetDetails.getLine_No());
            invoiceDetails.setRemarks(jobsheetDetails.getRemarks());
            invoiceDetails.setRemarks2(jobsheetDetails.getRemarks2());
            invoiceDetails.setBatchNo(jobsheetDetails.getBatchNo());

            invoiceDetailsList.add(invoiceDetails);
        }

        return invoiceDetailsList;
    }




    private List<AC_Class.JobSheetDetails> getJSDetails(String docNo) {
        List<AC_Class.JobSheetDetails> jobSheetDetailsList = new ArrayList<>();
        Cursor cursor = db.getJobSheetDetailsByDocNo(docNo);
        if (cursor != null && cursor.moveToFirst()){
            do {
                AC_Class.JobSheetDetails detail = new AC_Class.JobSheetDetails();
                detail.DocNo = cursor.getString(cursor.getColumnIndex("DocNo"));
                detail.DocDate = cursor.getString(cursor.getColumnIndex("DocDate"));
                detail.Location = cursor.getString(cursor.getColumnIndex("Location"));
                detail.ItemCode = cursor.getString(cursor.getColumnIndex("ItemCode"));
                detail.ItemDescription = cursor.getString(cursor.getColumnIndex("ItemDescription"));
                detail.UOM = cursor.getString(cursor.getColumnIndex("UOM"));
                detail.Quantity = cursor.getDouble(cursor.getColumnIndex("Quantity"));
                detail.UPrice = cursor.getDouble(cursor.getColumnIndex("UPrice"));
                detail.Discount = cursor.getDouble(cursor.getColumnIndex("Discount"));
                detail.SubTotal = cursor.getDouble(cursor.getColumnIndex("SubTotal"));
                detail.TaxType = cursor.getString(cursor.getColumnIndex("TaxType"));
                detail.TaxRate = cursor.getDouble(cursor.getColumnIndex("TaxRate"));
                detail.TaxValue = cursor.getDouble(cursor.getColumnIndex("TaxValue"));
                detail.Total_Ex = cursor.getDouble(cursor.getColumnIndex("TotalEx"));
                detail.Total_In = cursor.getDouble(cursor.getColumnIndex("TotalIn"));
                detail.Line_No = cursor.getString(cursor.getColumnIndex("LineNo"));
                detail.Remarks = cursor.getString(cursor.getColumnIndex("Remarks"));
                detail.Remarks2 = cursor.getString(cursor.getColumnIndex("Remarks2"));
                detail.BatchNo = cursor.getString(cursor.getColumnIndex("BatchNo"));
                jobSheetDetailsList.add(detail);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return  jobSheetDetailsList;
    }
}