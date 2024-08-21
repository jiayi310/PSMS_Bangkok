package com.example.androidmobilestock;

import static com.rscja.utility.StringUtility.TAG;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;

import androidx.core.content.FileProvider;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;

import com.example.androidmobilestock.tools.EnglishNumberToWords;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Environment;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Jobsheet_Details extends AppCompatActivity {

    TextView txt_total_lbl, txt_docNo, txt_currency, txt_totalIn, txt_docDate2, txt_agentName2, txt_debtorCode, txt_debtor_name2,
            txt_debtor_name3, txt_address1, txt_address2, txt_address3, txt_address4, txt_Attention2, txt_fax2, text_remark1, text_remark2, text_remark3,
            text_remark4, txt_remark_lbl, txt_remark2_lbl, txt_remark3_lbl, txt_remark4_lbl, txt_Attention, txt_fax;

    ACDatabase db;
    AC_Class.JobSheet jobSheet;
    TabLayout tabLayout;
    ViewPager viewPager;
    Jobsheet_Fragment_Adapter adapter;
    String docNo, debtorCode, func;
    PdfDocument document;
    List<AC_Class.JobSheetDetails> jobsheetDetailsArrayList = new ArrayList<>();
    String companyHeader;
    String defaultCurr;
    List<AC_Class.InvoiceDetails> invoiceDetailsList;
    AC_Class.Invoice invoice;
    String invoiceDocNo, currentDateandTime, dateTime;


    //InvoiceDtlMultipleTab.java

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobsheet_details);

        //txt_total_lbl = findViewById(R.id.txt_total_lbl);
        txt_docNo = findViewById(R.id.txt_docNo);
        //txt_currency = findViewById(R.id.txt_currency);
        //txt_totalIn = findViewById(R.id.txt_totalIn);
        txt_docDate2 = findViewById(R.id.txt_docDate2);
        txt_agentName2 = findViewById(R.id.txt_agentName2);
        txt_debtorCode = findViewById(R.id.txt_debtorCode);
        txt_debtor_name2 = findViewById(R.id.txt_debtor_name2);
        txt_debtor_name3 = findViewById(R.id.txt_debtor_name3);
        txt_address1 = findViewById(R.id.txt_address1);
        txt_address2 = findViewById(R.id.txt_address2);
        txt_address3 = findViewById(R.id.txt_address3);
        txt_address4 = findViewById(R.id.txt_address4);
        txt_Attention = findViewById(R.id.txt_Attention);
        txt_Attention2 = findViewById(R.id.txt_Attention2);
        txt_fax = findViewById(R.id.txt_fax);
        txt_fax2 = findViewById(R.id.txt_fax2);
        txt_remark_lbl = findViewById(R.id.txt_remark_lbl);
        text_remark1 = findViewById(R.id.text_remark1);
        txt_remark2_lbl = findViewById(R.id.txt_remark2_lbl);
        text_remark2 = findViewById(R.id.text_remark2);
        txt_remark3_lbl = findViewById(R.id.txt_remark3_lbl);
        text_remark3 = findViewById(R.id.text_remark3);
        txt_remark4_lbl = findViewById(R.id.txt_remark4_lbl);
        text_remark4 = findViewById(R.id.text_remark3);
        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.view_pager);

        db = new ACDatabase(this);

        Cursor header = db.getReg("16");
        if(header.moveToFirst()){
            companyHeader = header.getString(0);
        }

        Cursor cur = db.getReg("6");
        if(cur.moveToFirst()){
            defaultCurr = cur.getString(0);
        }


        docNo = getIntent().getStringExtra("docNo");
        debtorCode = getIntent().getStringExtra("debtorCode");
        func = getIntent().getStringExtra("FunctionKey");

        ActionBar actionBar = getSupportActionBar();
        try {
            actionBar.setTitle(getIntent().getStringExtra("docNo")+" Details");
            actionBar.setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            Log.i(TAG, "Job Sheet Details: "+e.getMessage());
        }

        db = new ACDatabase(this);

        tabLayout.setupWithViewPager(viewPager);
        adapter = new Jobsheet_Fragment_Adapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(Jobsheet_Fragment2.newInstance(docNo, debtorCode), "Job Details");
        adapter.addFragment(Jobsheet_Fragment1.newInstance(docNo, debtorCode), "Item Details");
        viewPager.setAdapter(adapter);

        displayHeaderData(docNo);
    }

    private void displayHeaderData(String docNo) {
        Cursor cursor1 = db.getJobSheetByDocNo(docNo);
        if (cursor1.getCount() > 0) {
            while (cursor1.moveToNext()){
                int indexDocNo = cursor1.getColumnIndex("DocNo");
                int indexDocDate = cursor1.getColumnIndex("DocDate");
                int indexDocType = cursor1.getColumnIndex("DocType");
                int indexDebtorCode = cursor1.getColumnIndex("DebtorCode");
                int indexDebtorName = cursor1.getColumnIndex("DebtorName");
                int indexDebtorName2 = cursor1.getColumnIndex("DebtorName2");
                int indexAddress1 = cursor1.getColumnIndex("Address1");
                int indexAddress2 = cursor1.getColumnIndex("Address2");
                int indexAddress3 = cursor1.getColumnIndex("Address3");
                int indexAddress4 = cursor1.getColumnIndex("Address4");
                int indexAgent = cursor1.getColumnIndex("SalesAgent");
                int indexTaxType = cursor1.getColumnIndex("TaxType");
                int indexPhone = cursor1.getColumnIndex("Phone");
                int indexFax = cursor1.getColumnIndex("Fax");
                int indexAttention = cursor1.getColumnIndex("Attention");
                int indexRemarks1 = cursor1.getColumnIndex("Remarks");
                int indexRemarks2 = cursor1.getColumnIndex("Remarks2");
                int indexRemarks3 = cursor1.getColumnIndex("Remarks3");
                int indexRemarks4 = cursor1.getColumnIndex("Remarks4");
                int indexTotalEx = cursor1.getColumnIndex("TotalEx");
                int indexTotalTax = cursor1.getColumnIndex("TaxValue");
                int indexTotalIn = cursor1.getColumnIndex("TotalIn");
                int indexCreatedTime = cursor1.getColumnIndex("CreatedTimeStamp");
                int indexCreatedUser = cursor1.getColumnIndex("CreatedUser");
                int indexLastModifiedTime = cursor1.getColumnIndex("LastModifiedDateTime");
                int indexLastModifiedUser = cursor1.getColumnIndex("LastModifiedUser");
                int indexDiscount = cursor1.getColumnIndex("Discount");
                int indexSalesNo = cursor1.getColumnIndex("SalesNo");
                int indexWorkType = cursor1.getColumnIndex("WorkType");
                int indexReplacType = cursor1.getColumnIndex("ReplacementType");
                int indexTimeIn = cursor1.getColumnIndex("TimeIn");
                int indexTimeOut = cursor1.getColumnIndex("TimeOut");
                int indexProblem = cursor1.getColumnIndex("Problem");
                int indexResolution = cursor1.getColumnIndex("Resolution");
                int indexJSRemarks = cursor1.getColumnIndex("JobSheetRemarks");
                int indexSignature = cursor1.getColumnIndex("Signature");

                String pDocNo = indexDocNo >= 0 ? cursor1.getString(indexDocNo) : "";
                String pDocDate = indexDocDate >= 0 ? cursor1.getString(indexDocDate) : "";
                String pDocType = indexDocType >= 0 ? cursor1.getString(indexDocType) : "";
                String pDebtorCode = indexDebtorCode >= 0 ? cursor1.getString(indexDebtorCode) : "";
                String pDebtorName = indexDebtorName >= 0 ? cursor1.getString(indexDebtorName) : "";
                String pDebtorName2 = indexDebtorName2 >= 0 ? cursor1.getString(indexDebtorName2) : "";
                String pAddress1 = indexAddress1 >= 0 ? cursor1.getString(indexAddress1) : "";
                String pAddress2 = indexAddress2 >= 0 ? cursor1.getString(indexAddress2) : "";
                String pAddress3 = indexAddress3 >= 0 ? cursor1.getString(indexAddress3) : "";
                String pAddress4 = indexAddress4 >= 0 ? cursor1.getString(indexAddress4) : "";
                String pAgent = indexAgent >= 0 ? cursor1.getString(indexAgent) : "";
                String pTaxType = indexTaxType >= 0 ? cursor1.getString(indexTaxType) : "";
                String pPhone = indexPhone >= 0 ? cursor1.getString(indexPhone) : "";
                String pFax = indexFax >= 0 ? cursor1.getString(indexFax) : "";
                String pAttention = indexAttention >= 0 ? cursor1.getString(indexAttention) : "";
                String pRemarks1 = indexRemarks1 >= 0 ? cursor1.getString(indexRemarks1) : "";
                String pRemarks2 = indexRemarks2 >= 0 ? cursor1.getString(indexRemarks2) : "";
                String pRemarks3 = indexRemarks3 >= 0 ? cursor1.getString(indexRemarks3) : "";
                String pRemarks4 = indexRemarks4 >= 0 ? cursor1.getString(indexRemarks4) : "";
                double pTotalEx = indexTotalEx >= 0 ? cursor1.getDouble(indexTotalEx) : 0;
                double pTotalTax = indexTotalTax >= 0 ? cursor1.getDouble(indexTotalTax) : 0;
                double pTotalIn = indexTotalIn >= 0 ? cursor1.getDouble(indexTotalIn) : 0;
                String pCreatedTime = indexCreatedTime >= 0 ? cursor1.getString(indexCreatedTime) : "";
                String pCreatedUser = indexCreatedUser >= 0 ? cursor1.getString(indexCreatedUser) : "";
                String pLastModifiedTime = indexLastModifiedTime >= 0 ? cursor1.getString(indexLastModifiedTime) : "";
                String pLastModifiedUser = indexLastModifiedUser >= 0 ? cursor1.getString(indexLastModifiedUser) : "";
                double pDiscount = indexDiscount >= 0 ? cursor1.getDouble(indexDiscount) : 0;
                String pSalesNo = indexSalesNo >= 0 ? cursor1.getString(indexSalesNo) : "";
                String pWorkType = indexWorkType >= 0 ? cursor1.getString(indexWorkType) : "";
                String pReplacType = indexReplacType >= 0 ? cursor1.getString(indexReplacType) : "";
                String pTimeIn = indexTimeIn >= 0 ? cursor1.getString(indexTimeIn) : "";
                String pTimeOut = indexTimeOut >= 0 ? cursor1.getString(indexTimeOut) : "";
                String pProblem = indexProblem >= 0 ? cursor1.getString(indexProblem) : "";
                String pResolution = indexResolution >= 0 ? cursor1.getString(indexResolution) : "";
                String pJSRemarks = indexJSRemarks >= 0 ? cursor1.getString(indexJSRemarks) : "";
                String pSignature = indexSignature >= 0 ? cursor1.getString(indexSignature) : "";

                jobSheet = new AC_Class.JobSheet();
                jobSheet.setDocNo(pDocNo);
                jobSheet.setDocDate(pDocDate);
                jobSheet.setDocType(pDocType);
                jobSheet.setDebtorCode(pDebtorCode);
                jobSheet.setDebtorName(pDebtorName);
                jobSheet.setDebtorName2(pDebtorName2);
                jobSheet.setAddress1(pAddress1);
                jobSheet.setAddress2(pAddress2);
                jobSheet.setAddress3(pAddress3);
                jobSheet.setAddress4(pAddress4);
                jobSheet.setAgent(pAgent);
                jobSheet.setTaxType(pTaxType);
                jobSheet.setPhone(pPhone);
                jobSheet.setFax(pFax);
                jobSheet.setAttention(pAttention);
                jobSheet.setRemarks(pRemarks1);
                jobSheet.setRemarks2(pRemarks2);
                jobSheet.setRemarks3(pRemarks3);
                jobSheet.setRemarks4(pRemarks4);
                jobSheet.setWorkType(pWorkType);
                jobSheet.setReplacementType(pReplacType);
                jobSheet.setTimeIn(pTimeIn);
                jobSheet.setTimeOut(pTimeOut);
                jobSheet.setProblem(pProblem);
                jobSheet.setResolution(pResolution);
                jobSheet.setJobSheetRemarks(pJSRemarks);
                jobSheet.setSalesNo(pSalesNo);
                jobSheet.setTotalTax(pTotalTax);
                jobSheet.setTotalEx(pTotalEx);
                jobSheet.setTotalIn(pTotalIn);
                jobSheet.setDiscount(pDiscount);
                jobSheet.setCreatedTimeStamp(pCreatedTime);
                jobSheet.setCreatedUser(pCreatedUser);
                jobSheet.setLastModifiedDateTime(pLastModifiedTime);
                jobSheet.setSignature(pSignature);

                txt_docNo.setText(pDocNo);
                txt_docDate2.setText(pDocDate);
                txt_debtorCode.setText(pDebtorCode);
                txt_debtor_name2.setText(pDebtorName);
                txt_debtor_name3.setText(pDebtorName2);
                txt_address1.setText(pAddress1);
                txt_Attention2.setText(pAttention);
                txt_fax2.setText(pFax);
                text_remark1.setText(pRemarks1);
                text_remark2.setText(pRemarks2);
                text_remark3.setText(pRemarks3);
                text_remark4.setText(pRemarks4);
                txt_agentName2.setText(pAgent);
                //txt_totalIn.setText(String.valueOf(pTotalIn));

                //Hide the attention and fax
                if(pAttention==null){
                    txt_Attention.setVisibility(View.GONE);
                    txt_Attention2.setVisibility(View.GONE);
                }

                if(pFax==null){
                    txt_fax.setVisibility(View.GONE);
                    txt_fax2.setVisibility(View.GONE);
                }

                if(pRemarks1 == null || pRemarks1.isEmpty()){
                    txt_remark_lbl.setVisibility(View.GONE);
                    text_remark1.setVisibility(View.GONE);
                }

                if(pRemarks2 == null || pRemarks2.isEmpty()){
                    txt_remark2_lbl.setVisibility(View.GONE);
                    text_remark2.setVisibility(View.GONE);
                }

                if(pRemarks3 == null || pRemarks3.isEmpty()){
                    txt_remark3_lbl.setVisibility(View.GONE);
                    text_remark3.setVisibility(View.GONE);
                }

                if(pRemarks4 == null || pRemarks4.isEmpty()){
                    txt_remark4_lbl.setVisibility(View.GONE);
                    text_remark4.setVisibility(View.GONE);
                }

            }
        } else {
            Log.d("JS Details", "Error loading menu details");
        }
        cursor1.close();

        Cursor cursor2 = db.getJobSheetDetailsByDocNo(docNo);
        if (cursor2 != null && cursor2.moveToFirst()){
            do {
                AC_Class.JobSheetDetails detail = new AC_Class.JobSheetDetails();
                int indexDocNo = cursor2.getColumnIndex("DocNo");
                int indexDocDate = cursor2.getColumnIndex("DocDate");
                int indexLocation = cursor2.getColumnIndex("Location");
                int indexItemCode = cursor2.getColumnIndex("ItemCode");
                int indexItemDescription = cursor2.getColumnIndex("ItemDescription");
                int indexUOM = cursor2.getColumnIndex("UOM");
                int indexQuantity = cursor2.getColumnIndex("Quantity");
                int indexUPrice = cursor2.getColumnIndex("UPrice");
                int indexDiscount = cursor2.getColumnIndex("Discount");
                int indexSubTotal = cursor2.getColumnIndex("SubTotal");
                int indexTaxValue = cursor2.getColumnIndex("TaxValue");
                int indexTotalEx = cursor2.getColumnIndex("TotalEx");
                int indexTotalIn = cursor2.getColumnIndex("TotalIn");
                int indexRemarks = cursor2.getColumnIndex("Remarks");
                int indexRemarks2 = cursor2.getColumnIndex("Remarks2");
                int indexBatchNo = cursor2.getColumnIndex("BatchNo");


                if (indexDocNo >= 0) detail.DocNo = cursor2.getString(indexDocNo);
                if (indexDocDate >= 0) detail.DocDate = cursor2.getString(indexDocDate);
                if (indexLocation >= 0) detail.Location = cursor2.getString(indexLocation);
                if (indexItemCode >= 0) detail.ItemCode = cursor2.getString(indexItemCode);
                if (indexItemDescription >= 0) detail.ItemDescription = cursor2.getString(indexItemDescription);
                if (indexUOM >= 0) detail.UOM = cursor2.getString(indexUOM);
                if (indexQuantity >= 0) detail.Quantity = cursor2.getDouble(indexQuantity);
                if (indexUPrice >= 0) detail.UPrice = cursor2.getDouble(indexUPrice);
                if (indexDiscount >= 0) detail.Discount = cursor2.getDouble(indexDiscount);
                if (indexSubTotal >= 0) detail.SubTotal = cursor2.getDouble(indexSubTotal);
                if (indexTaxValue >= 0) detail.TaxValue = cursor2.getDouble(indexTaxValue);
                if (indexTotalEx >= 0) detail.Total_Ex = cursor2.getDouble(indexTotalEx);
                if (indexTotalIn >= 0) detail.Total_In = cursor2.getDouble(indexTotalIn);
                if (indexRemarks >= 0) detail.Remarks = cursor2.getString(indexRemarks);
                if (indexRemarks2 >= 0) detail.Remarks2 = cursor2.getString(indexRemarks2);
                if (indexBatchNo >= 0) detail.BatchNo = cursor2.getString(indexBatchNo);

                jobsheetDetailsArrayList.add(detail);
            } while (cursor2.moveToNext());
            cursor1.close();
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Jobsheet.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear all activities on top of the class
        startActivity(intent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.jobsheet_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            onBackPressed();
        }

        if (id == R.id.pic)
        {
            Intent intent = new Intent(Jobsheet_Details.this, Jobsheet_ImageView.class);
            intent.putExtra("docNo", jobSheet.getDocNo());
            startActivity(intent);
            return true;
        }

        if (id == R.id.pdf){
            createMyPDF();

            // Path to your PDF file
            File pdfFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "PSMSExport/" + docNo + ".pdf");

            // Create an Intent to view the PDF file
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", pdfFile);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Create a chooser to let the user pick a PDF viewer
            Intent chooser = Intent.createChooser(intent, "Open File");
            startActivity(chooser);

//            // Verify the intent will resolve to at least one activity
//            if (intent.resolveActivity(getPackageManager()) != null) {
//                startActivity(chooser);
//            } else {
//                Toast.makeText(this, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
//            }
        }

        if (id == R.id.convert){
            if (jobSheet.getSalesNo() == null || jobSheet.getSalesNo().isEmpty()){
                if (jobSheet.getReplacementType().equals("Service")){
                    if (!jobsheetDetailsArrayList.isEmpty()){
                        convertSales();
                    } else {
                        Log.d("JSDetail", "item is empty");
                        Toast.makeText(this, "This job sheet cannot be converted to sales.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "This job sheet cannot be converted to sales.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "This job sheet is converted to sales.", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void convertSales() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to convert to sales?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //refer to jobsheet add item
                        jobSheet = db.getJobSheetOByDocNo(docNo);
                        convertToInvoice();
                        invoiceDetailsList = convertJobSheetDetailsToInvoiceDetails(jobsheetDetailsArrayList);

                        //save invoice
                        boolean insertHeader = db.insertInv(invoice);
                        if (invoice.getDocNo().equals(db.getNextNo())) {
                            db.IncrementAutoNumbering("S");
                        }
                        if (!insertHeader){
                            Toast.makeText(Jobsheet_Details.this, "Failed to insert Invoice to database", Toast.LENGTH_SHORT).show();
                        }

                        //Update Status
                        boolean jobSheetStatusUpdated = db.updateJobSheetStatus(jobSheet.getDocNo(), "Converted");
                        //Update Invoice No to jobsheet table
                        boolean salesNoUpdated = db.updateJSInvoiceDocNo(jobSheet.getDocNo(), invoiceDocNo);
                        //save invoice detail
                        boolean invoiceDetailsSaved = db.saveInvoiceDetails(invoiceDetailsList);
                        if (invoiceDetailsSaved && jobSheetStatusUpdated && salesNoUpdated) {
                            // Successfully saved
                            Toast.makeText(Jobsheet_Details.this, "Sales created successfully !", Toast.LENGTH_SHORT).show();

                            jobSheet.setSalesNo(invoiceDocNo);

                            final AlertDialog.Builder builder = new AlertDialog.Builder(Jobsheet_Details.this);
                            builder.setTitle("Attention!");
                            builder.setMessage("Do you want to view the sale invoice?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(Jobsheet_Details.this, InvoiceDtlMultipleTab.class);
                                    intent.putExtra("docNo", invoice.getDocNo());
                                    intent.putExtra("debtorCode", invoice.getDebtorCode());
                                    intent.putExtra("fromJobsheetDetails", true);
                                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    //finish();
                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog dialog2 = builder.create();
                            dialog2.show();

                        } else {
                            // Failed to save
                            Log.d("Invoice", "Failed to save invoice or details.");
                        }
                    }
                })
                .show();

    }

    private void convertToInvoice() {
        invoiceDocNo = db.getNextNo();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault());
        currentDateandTime = sdf.format(new Date());

        //set invoice
        invoice = new AC_Class.Invoice();
        invoice.setDocNo(invoiceDocNo);

        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateTime = sdf2.format(new Date());

        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        String currentDT = sdf3.format(new Date());

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

    int pageHeight = 842;
    int pageWidth = 595;
    int marginLeft = 50;
    int currentPage = 1;
    int y = 50;

    private void createMyPDF() {
        String extstoragedir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File folder = new File(extstoragedir, "PSMSExport");
        if (!folder.exists()) {
            folder.mkdirs();
        }


        try {
            // File path to save the PDF
            File file = new File(folder, docNo + ".pdf");
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);

            int listSize = jobsheetDetailsArrayList.size() * 150;

            // Create a new PdfDocument
            document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPage).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            printHeader(canvas);
            printJSDetails(canvas);
            printItemColumn(canvas);

            // Paint settings for text
            Paint myPaint = new Paint();
            myPaint.setTextAlign(Paint.Align.LEFT);
            myPaint.setColor(Color.BLACK);
            myPaint.setTextSize(8);

            int start = y+480;


            canvas.drawLine(marginLeft,740,pageWidth-marginLeft,740,myPaint);

            if (jobsheetDetailsArrayList.size() > 0){
                for (int i=0;i<jobsheetDetailsArrayList.size();i++){
                    if (start > 690){
                        document.finishPage(page);
                        currentPage++;
                        pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPage).create();
                        page = document.startPage(pageInfo);
                        canvas = page.getCanvas();
                        printHeader(canvas);
                        printItemColumn2(canvas);
                        start = y+200;
                    }

                    //item code

                    canvas.drawText(i + 1 + ".", 60, start, myPaint);
                    String mText2 = jobsheetDetailsArrayList.get(i).getItemCode();
                    TextPaint mTextPaint2 = new TextPaint();
                    StaticLayout mTextLayout2 = new StaticLayout(mText2, mTextPaint2, 100, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
                    mTextPaint2.setTextSize(8);
                    canvas.save();

                    int textX2 = 90;
                    int textY2 = start - 12;

                    canvas.translate(textX2, textY2);
                    mTextLayout2.draw(canvas);
                    canvas.restore();

                    //description

                    String mText = jobsheetDetailsArrayList.get(i).getItemDescription() ;

                    //batch No

                    if(jobsheetDetailsArrayList.get(i).getBatchNo() !=null && jobsheetDetailsArrayList.get(i).getBatchNo() != "" ){
                        mText += " ("+ jobsheetDetailsArrayList.get(i).getBatchNo()+")";
                    }
                    TextPaint mTextPaint = new TextPaint();
                    StaticLayout mTextLayout = new StaticLayout(mText, mTextPaint, 300, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
                    mTextPaint.setTextSize(8);
                    canvas.save();

                    int textX = 170;
                    int textY = start - 12;

                    canvas.translate(textX, textY);
                    mTextLayout.draw(canvas);
                    canvas.restore();

                    //qty
                    myPaint.setTextAlign(Paint.Align.RIGHT);
                    canvas.drawText(jobsheetDetailsArrayList.get(i).getQuantity().toString(), 464, start , myPaint);

                    //uom
                    String mText4 = jobsheetDetailsArrayList.get(i).getUOM();
                    TextPaint mTextPaint4 = new TextPaint();
                    StaticLayout mTextLayout4 = new StaticLayout(mText4, mTextPaint4, 100, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
                    mTextPaint4.setTextSize(8);
                    canvas.save();

                    int textX4 = 500;
                    int textY4 = start - 12;

                    canvas.translate(textX4, textY4);
                    mTextLayout4.draw(canvas);
                    canvas.restore();

//                    canvas.drawText(String.format("%.2f",jobsheetDetailsArrayList.get(i).getUPrice()), 430, start, myPaint);
//                    if(jobsheetDetailsArrayList.get(i).getDiscount()!=null && jobsheetDetailsArrayList.get(i).getDiscount()!=0) {
//                        canvas.drawText( String.format("%.2f", jobsheetDetailsArrayList.get(i).getDiscount()), 475, start, myPaint);
//                    }
//                    canvas.drawText(String.format("%.2f",jobsheetDetailsArrayList.get(i).getTotal_In()), pageWidth - marginLeft - 10, start, myPaint);


                    start += mTextLayout.getHeight() - 20;
                    if (mTextLayout.getHeight() < mTextLayout2.getHeight()) {
                        start += mTextLayout2.getHeight() - 10;
                    }

                    myPaint.setTextAlign(Paint.Align.LEFT);
                    myPaint.setTextSize(7);

                    float maxWidth = pageWidth - marginLeft - 160;

// Draw the first remark
                    if (jobsheetDetailsArrayList.get(i).getRemarks() != null && !jobsheetDetailsArrayList.get(i).getRemarks().isEmpty()) {
                        start += 15;
                        canvas.drawText("REMARK: ", 90, start, myPaint);
                        String truncatedRemark = truncateText(jobsheetDetailsArrayList.get(i).getRemarks(), 20, myPaint, maxWidth - 10);
                        canvas.drawText(truncatedRemark, 130, start, myPaint);
                    }

// Adjust start position for the second remark
                    if (jobsheetDetailsArrayList.get(i).getRemarks2() != null && !jobsheetDetailsArrayList.get(i).getRemarks2().isEmpty()) {
                        start += 10;
                        canvas.drawText("REMARK 2: ", 90, start, myPaint);
                        String truncatedRemark2 = truncateText(jobsheetDetailsArrayList.get(i).getRemarks2(), 20, myPaint, maxWidth - 10);
                        canvas.drawText(truncatedRemark2, 130, start, myPaint);
                    }

// Move to next section
                    start += 20;

                }
            }

            if(start > 740) {
                document.finishPage(page);
                currentPage++;
                pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPage).create();
                page = document.startPage(pageInfo);
                canvas = page.getCanvas();
                printHeader(canvas);
                printItemColumn(canvas);
                start = y+470;


            }

            int end = 700;
            canvas.drawLine(marginLeft,740,pageWidth-marginLeft,740,myPaint);

            // Finish the page
            document.finishPage(page);

            // Write the document content to the FileOutputStream
            document.writeTo(fOut);

            // Close the document
            document.close();

            // Flush and close the FileOutputStream
            fOut.flush();
            fOut.close();

            jobsheetDetailsArrayList.clear();

        } catch (IOException e) {
            Log.e("PDF Creation", "Error creating PDF", e);
            Toast.makeText(this, "Error creating PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void printHeader(Canvas canvas) {
        y = 50;
        Paint myPaint = new Paint();
        Paint titlePaint = new Paint();

        String[] myCompanyArray = companyHeader.split("\n");

        for (String myCompany: myCompanyArray)
        {
            myPaint.setTextAlign(Paint.Align.CENTER);
            myPaint.setTextSize(12);
            myPaint.setColor(Color.BLACK);
            canvas.drawText(myCompany , pageWidth/2,y,myPaint);
            y = y + 16;
        }

        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        titlePaint.setTextSize(19);
        canvas.drawLine(marginLeft,y,pageWidth-marginLeft,y,myPaint);
        canvas.drawText("Job Sheet", pageWidth/2,y+20,titlePaint);

        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setTextSize(10);
        canvas.drawLine(marginLeft,y+30,80,y+30,myPaint);
        canvas.drawLine(marginLeft,y+30,marginLeft,y+50,myPaint);

        canvas.drawLine(300,y+30,330,y+30,myPaint);
        canvas.drawLine(330,y+30,330,y+50,myPaint);

        canvas.drawText(jobSheet.getDebtorCode() , marginLeft + 10,y+50,myPaint);
        canvas.drawText(jobSheet.getDebtorName() , marginLeft + 10,y + 65,myPaint);

        if(jobSheet.getAddress1()!=null) {
            canvas.drawText(jobSheet.getAddress1(), marginLeft + 10, y+80, myPaint);
        }
        if(jobSheet.getAddress2()!=null) {
            canvas.drawText(jobSheet.getAddress2(), marginLeft + 10, y+95, myPaint);
        }
        if(jobSheet.getAddress3()!=null) {
            canvas.drawText(jobSheet.getAddress3(), marginLeft + 10, y+110, myPaint);
        }
        if(jobSheet.getAddress4()!=null) {
            canvas.drawText(jobSheet.getAddress4(), marginLeft + 10, y+125, myPaint);
        }
        canvas.drawText("TEL   :" , marginLeft + 10,y+140,myPaint);
        if(jobSheet.getPhone()!=null) {
            canvas.drawText(jobSheet.getPhone(), 95, y+140, myPaint);
        }
        canvas.drawText("FAX   :" , (pageWidth/4) + 40,y+140,myPaint);
        if(jobSheet.getFax()!=null) {
            canvas.drawText(jobSheet.getFax(), (pageWidth/4) + 75, y+140, myPaint);
        }

        canvas.drawLine(marginLeft,y+150,80,y+150,myPaint);
        canvas.drawLine(marginLeft,y+120,marginLeft,y+150,myPaint);

        canvas.drawLine(300,y+150,330,y+150,myPaint);
        canvas.drawLine(330,y+120,330,y+150,myPaint);

        myPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Your Ref.",380, y+50,myPaint);
        canvas.drawText("Attn",380, y+104,myPaint);
        canvas.drawText("Date",380, y+68,myPaint);
        canvas.drawText("Agent",380, y+86,myPaint);
        canvas.drawText("Page",380, y+122,myPaint);

        canvas.drawText(":",440, y+50,myPaint);
        canvas.drawText(":",440, y+68,myPaint);
        canvas.drawText(":",440, y+86,myPaint);
        canvas.drawText(":",440, y+104,myPaint);
        canvas.drawText(":",440, y+122,myPaint);

        myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        canvas.drawText(jobSheet.getDocNo(),460, y+50,myPaint);
        myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL));
        canvas.drawText(jobSheet.getDocDate(),460, y+68,myPaint);
        if(jobSheet.getAgent()!=null)
            canvas.drawText(jobSheet.getAgent(),460, y+86,myPaint);
        if(jobSheet.getAttention()!=null)
            canvas.drawText(jobSheet.getAttention(),460, y+104,myPaint);
        canvas.drawText(String.valueOf(currentPage),460, y+122,myPaint);

    }

    private void printJSDetails(Canvas canvas) {
        Paint myPaint = new Paint();
        //Paint titlePaint = new Paint();

        myPaint.setTextSize(10);

        //jobsheet box column
        canvas.drawLine(marginLeft,y+170,50,y+430,myPaint);  //left vertical line
        canvas.drawLine(pageWidth-marginLeft,y+170,pageWidth-marginLeft,y+430,myPaint); //right vertical line
        canvas.drawLine(marginLeft,y+170,pageWidth-marginLeft,y+170,myPaint); //top horizontal line
        canvas.drawLine(marginLeft,y+430,pageWidth-marginLeft,y+430,myPaint); //bottom horizontal line
        canvas.drawLine(150,y+170,150,y+430,myPaint); //center vertical line

        //worktype column
        canvas.drawLine(marginLeft,y+200,pageWidth-marginLeft,y+200,myPaint); //bottom horizontal line
        myPaint.setTextAlign(Paint.Align.LEFT);

        myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        canvas.drawText("Work Type :",60, y+190,myPaint);
        myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL));
        canvas.drawText(jobSheet.getWorkType(),158, y+190,myPaint);

        //Replacement Type Column
        canvas.drawLine(marginLeft,y+230,pageWidth-marginLeft,y+230,myPaint); //bottom horizontal line
        myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        canvas.drawText("Replacement Type :",60, y+220,myPaint);
        myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL));
        canvas.drawText(jobSheet.getReplacementType(),158, y+220,myPaint);

        //time In & time out Column
        canvas.drawLine(marginLeft,y+260,pageWidth-marginLeft,y+260,myPaint); //bottom horizontal line
        myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        canvas.drawText("Time In :",60, y+250,myPaint);
        myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL));
        canvas.drawText(jobSheet.getTimeIn(),158, y+250,myPaint);

        canvas.drawLine(pageWidth/2,y+230,pageWidth/2,y+260,myPaint); //right center vertical line

        canvas.drawLine(pageWidth/2 + 120,y+230,pageWidth/2 + 120,y+260,myPaint); //right center 2 vertical line
        myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        canvas.drawText("Time Out :",310, y+250,myPaint);
        myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL));
        canvas.drawText(jobSheet.getTimeOut(),430, y+250,myPaint);


        //problems column
        canvas.drawLine(marginLeft,y+310,pageWidth-marginLeft,y+310,myPaint); //bottom horizontal line
        myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        canvas.drawText("Problems :",60, y+280,myPaint);
        myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL));
        //canvas.drawText(jobSheet.getProblem(),180, y+280,myPaint);
        if (jobSheet.getProblem() != null) {
            drawWrappedText(canvas, jobSheet.getProblem(), 158, y + 274, pageWidth - marginLeft - 180, myPaint);
        }



        //resolution column
        canvas.drawLine(marginLeft,y+370,pageWidth/2 +120, y+370,myPaint); //bottom horizontal line
        myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        canvas.drawText("Resolutions :",60, y+330,myPaint);
        myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL));
        //canvas.drawText(jobSheet.getResolution(),180, y+330,myPaint);
        if (jobSheet.getResolution() != null){
            drawWrappedText(canvas, jobSheet.getResolution(), 158, y + 324, pageWidth - marginLeft - 280, myPaint);
        }


        //remarks column
        myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        canvas.drawText("Remarks :",60, y+390,myPaint);
        myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL));
        if (jobSheet.getJobSheetRemarks() != null) {
            drawWrappedText(canvas, jobSheet.getJobSheetRemarks(), 158, y + 384, pageWidth - marginLeft - 280, myPaint);
        }

        //sign
        canvas.drawLine(pageWidth/2 + 120,y+310,pageWidth/2 + 120,y+430,myPaint);
        myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        canvas.drawText("Customer Signature :",423, y+323,myPaint);
        if(jobSheet.getSignature()!=null){
            Bitmap decodedByte = BitmapFactory.decodeByteArray(Base64.decode(jobSheet.getSignature(), Base64.DEFAULT), 0,
                    Base64.decode(jobSheet.getSignature(), Base64.DEFAULT).length);

            Bitmap bitmapS = Bitmap.createScaledBitmap(decodedByte, 200, 180, false);

            if(bitmapS!=null){
                myPaint.setTextAlign(Paint.Align.LEFT);
                canvas.setDensity(100);
                myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL));
                canvas.drawBitmap(bitmapS, 430, 470, myPaint);
            }
        }

    }

    private void printItemColumn(Canvas canvas) {
        Paint myPaint = new Paint();
        //Paint titlePaint = new Paint();
        //item column
        myPaint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(marginLeft,y+440,pageWidth-marginLeft,y+440,myPaint);
        canvas.drawLine(marginLeft,y+465,pageWidth-marginLeft,y+465,myPaint);

        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setStyle(Paint.Style.FILL);
        myPaint.setTextSize(8);
        myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        canvas.drawText("No",60,y+453,myPaint);
        canvas.drawText("Item Code",90,y+453,myPaint);
        canvas.drawText("Description",170,y+453,myPaint);
        canvas.drawText("Qty",450,y+453,myPaint);
        canvas.drawText("UOM",500,y+453,myPaint);
//        canvas.drawText("U/ Price",400,y+442,myPaint);
//        canvas.drawText("Disc.",450,y+442,myPaint);
//        canvas.drawText("Total (" + defaultCurr + ")",500,y+442,myPaint);
        myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL));

    }

    private void printItemColumn2(Canvas canvas) {
        Paint myPaint = new Paint();

        //item column
        myPaint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(marginLeft,y+160,pageWidth-marginLeft,y+160,myPaint);
        canvas.drawLine(marginLeft,y+185,pageWidth-marginLeft,y+185,myPaint);

        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setStyle(Paint.Style.FILL);
        myPaint.setTextSize(8);
        myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        canvas.drawText("No",60,y+172,myPaint);
        canvas.drawText("Item Code",90,y+172,myPaint);
        canvas.drawText("Description",170,y+172,myPaint);
        canvas.drawText("Qty",450,y+172,myPaint);
        canvas.drawText("UOM",500,y+172,myPaint);
//        canvas.drawText("U Price",400,y+172,myPaint);
//        canvas.drawText("Disc.",450,y+172,myPaint);
//        canvas.drawText("Total (" + defaultCurr + ")",500,y+172,myPaint);
        myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL));

    }

    private void drawWrappedText(Canvas canvas, String text, float x, float y, float maxWidth, Paint paint) {
        int lineHeight = 10;
        int startY = (int) y;
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();

        for (String word : words) {
            if (paint.measureText(line + word) <= maxWidth) {
                line.append(word).append(" ");
            } else {
                canvas.drawText(line.toString().trim(), x, startY, paint);
                startY += lineHeight;
                line = new StringBuilder(word + " ");
            }
        }
        if (line.length() > 0) {
            canvas.drawText(line.toString().trim(), x, startY, paint);
        }
    }

    int maxChars = 50;
    // Function to truncate text if it exceeds the maximum length
    private String truncateText(String text, int maxChars, Paint paint, float maxWidth) {
        if (text == null || text.length() == 0) {
            return "";
        }

        String ellipsis = "...";
        String truncatedText = text;

        // Check if the text width with the ellipsis exceeds the maximum width
        if (paint.measureText(text) > maxWidth) {
            // Find the maximum length of text that fits within the maxWidth
            while (paint.measureText(truncatedText + ellipsis) > maxWidth) {
                truncatedText = truncatedText.substring(0, truncatedText.length() - 1);
            }
            truncatedText += ellipsis;
        }

        return truncatedText;
    }
}