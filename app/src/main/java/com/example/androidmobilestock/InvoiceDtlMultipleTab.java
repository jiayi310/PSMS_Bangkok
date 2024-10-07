package com.example.androidmobilestock;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;

import com.example.androidmobilestock.tools.EnglishNumberToWords;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidmobilestock.databinding.ActivityInvoiceDetailsMultipleTabBinding;
import com.example.androidmobilestock.adapter.InvoiceSectionsPagerAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDtlMultipleTab extends AppCompatActivity {
    ActivityInvoiceDetailsMultipleTabBinding binding;
    AC_Class.InvoiceMenu invoice = new AC_Class.InvoiceMenu();
    AC_Class.Invoice debtor = new AC_Class.Invoice();
    AC_Class.Payment payment = new AC_Class.Payment();
    List<AC_Class.InvoiceDetails> invoiceDetailsArrayList = new ArrayList<>();
    ACDatabase db;
    String TAG = "custDebug";
    String companyHeader, salesReceiptFormat;
    boolean isFresh;
    String salesFormat;
    TextView attention,attention2,fax,fax2,remark,remark2,remark3,remark4,remark5,remark6,remark7,remark8;
    Bitmap invoiceheader,scaledheader;
    PdfDocument document;
    String defaultCurr;

    private static final int BLUETOOTH_REQUEST_CODE_IMPORT = 2;
    private String[] bluetoothPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
//        Log.i(TAG, "Got into Invoice Mult Tab");
        binding = DataBindingUtil.setContentView(this, R.layout.activity_invoice_details_multiple_tab);
        db = new ACDatabase(this);

        //TextView
        attention = findViewById(R.id.txt_Attention);
        attention2 = findViewById(R.id.txt_Attention2);
        fax = findViewById(R.id.txt_fax);
        fax2 = findViewById(R.id.txt_fax2);
        remark = findViewById(R.id.txt_remark);
        remark2 = findViewById(R.id.txt_remark2);
        remark3 = findViewById(R.id.txt_remark3);
        remark4 = findViewById(R.id.txt_remark4);
        remark5 = findViewById(R.id.txt_remark5);
        remark6 = findViewById(R.id.text_remark3);
        remark7 = findViewById(R.id.txt_remark7);
        remark8 = findViewById(R.id.text_remark4);
        invoiceheader = BitmapFactory.decodeResource(getResources(),R.drawable.logo_presoft);
        scaledheader = Bitmap.createScaledBitmap(invoiceheader,1200,518,false);

        Cursor header = db.getReg("16");
        if(header.moveToFirst()){
            companyHeader = header.getString(0);
        }

        Cursor sales = db.getReg("64");
        if(sales.moveToFirst()){
            salesReceiptFormat = sales.getString(0);
        }

        Cursor cur = db.getReg("6");
        if(cur.moveToFirst()){
            defaultCurr = cur.getString(0);
            binding.setDefaultCurr(cur.getString(0));;
        }
        // Action Bar
        ActionBar actionBar = getSupportActionBar();
        try {
            actionBar.setTitle(getIntent().getStringExtra("docNo")+" Details");
            actionBar.setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {Log.i(TAG, "Invoice Details: "+e.getMessage());}


        // Data Binding
        String docNo = getIntent().getStringExtra("docNo");
        String debtorCode = getIntent().getStringExtra("debtorCode");
        isFresh = getIntent().getBooleanExtra("isFresh", false);
        //fromJobsheetDetails = getIntent().getBooleanExtra("fromJobsheetDetails", false);
        getData(docNo);
        getDebtor(docNo);
        binding.setInvoice(invoice);
        binding.setPayment(payment);

        //Hide the attention and fax
        if(debtor.getAttention()==null){
            attention.setVisibility(View.GONE);
            attention2.setVisibility(View.GONE);
        }

        if(debtor.getFax()==null){
            fax.setVisibility(View.GONE);
            fax2.setVisibility(View.GONE);
        }

        if(debtor.getRemarks() == null || debtor.getRemarks().equals("")){
            remark.setVisibility(View.GONE);
            remark2.setVisibility(View.GONE);
        }

        if(debtor.getRemarks2() == null || debtor.getRemarks2().equals("")){
            remark3.setVisibility(View.GONE);
            remark4.setVisibility(View.GONE);
        }

        if(debtor.getRemarks3() == null || debtor.getRemarks3().equals("")){
            remark5.setVisibility(View.GONE);
            remark6.setVisibility(View.GONE);
        }

        if(debtor.getRemarks4() == null || debtor.getRemarks4().equals("")){
            remark7.setVisibility(View.GONE);
            remark8.setVisibility(View.GONE);
        }

        // Pass intents (code and docNo) as bundle
        Bundle bundle = new Bundle();
        bundle.putString("docNo", docNo);
        bundle.putString("debtorCode", debtorCode);
        // Create and set adapter for tabs
        InvoiceSectionsPagerAdapter sectionsPagerAdapter =
                new InvoiceSectionsPagerAdapter(this, getSupportFragmentManager(), bundle);
        Log.i(TAG, "Got into Invoice Multi Tab");
        ViewPager viewPager = findViewById(R.id.view_pager2);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs2);
        tabs.setupWithViewPager(viewPager);

        if (isFresh){
            printMessage();
        }

        Cursor cur2 = db.getReg("34");
        if(cur2.moveToFirst()){
            salesFormat = cur2.getString(0);
        }

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
    }

    private void getDebtor(String docNo) {

        Cursor data;
        data = db.getInvoiceHeadertoUpdate(docNo);


        if (data.getCount() == 1) {
            data.moveToNext();
            debtor = new AC_Class.Invoice(data.getString(1), data.getString(2), data.getString(3), data.getString(4), data.getString(5), data.getString(6), data.getString(data.getColumnIndex("TaxType")), data.getString(7), data.getString(data.getColumnIndex("Signature")), data.getString(data.getColumnIndex("Phone")), data.getString(data.getColumnIndex("Fax")),
                    data.getString(data.getColumnIndex("Attention")), data.getString(data.getColumnIndex("Address1")), data.getString(data.getColumnIndex("Address2")), data.getString(data.getColumnIndex("Address3")), data.getString(data.getColumnIndex("Address4")),data.getString(data.getColumnIndex("Remarks")), data.getString(data.getColumnIndex("Remarks2")), data.getString(data.getColumnIndex("Remarks3"))
                    , data.getString(data.getColumnIndex("Remarks4")), data.getString(data.getColumnIndex("CreatedUser")),data.getString(data.getColumnIndex("CreditTerm")), data.getString(data.getColumnIndex("DetailDiscount")));

            binding.setInv(debtor);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.invdtl_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        if (id == R.id.checkout)
        {
            printMessage();
        }

        if (id == R.id.pdf) {
            createMyPDF();

            // Path to your PDF file
            File pdfFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "PSMSExport/" + invoice.getDocNo() + ".pdf");

            if (pdfFile.length() > 0) {
                // Create an Intent to view the PDF file
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", pdfFile);
                intent.setDataAndType(uri, "application/pdf");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                // Create a chooser to let the user pick a PDF viewer
                Intent chooser = Intent.createChooser(intent, "Open File");
                startActivity(chooser);
            } else{
                Toast.makeText(InvoiceDtlMultipleTab.this, "Empty File",
                        Toast.LENGTH_SHORT).show();
            }

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){



        /*
        Intent intent = new Intent();
        intent.putExtra("ItemKey", "");
        setResult(RESULT_OK, intent);
        super.onBackPressed();

         */

        if (getIntent().getBooleanExtra("fromJobsheetDetails", false)) {
            // Navigate back to Jobsheet_Details
            super.onBackPressed();
        } else {
            Intent intent = new Intent(this, InvoiceList.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear all activities on top of the class
            startActivity(intent);
            finish();
        }
    }

    private void printMessage()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Attention!");
        builder.setMessage("Do you want to print the receipt?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (checkBlueToothPermission()) {
                    printInvoice();
                }else {
                    requestBluetoothPermissionImport();
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    AC_Class.PrintInvoice pi = null;
    void printInvoice (){

        if (pi != null)
        {
            try {
                pi.disconnectBT();
                pi = null;
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        pi = new AC_Class.PrintInvoice(getApplicationContext(),
                invoice.getDocNo(), companyHeader);

        if (pi.FindBluetoothDevice()) {
            try {
                if (pi.openBluetoothPrinter())
                {
                    pi.printData(salesFormat);
                }
                //pi.disconnectBT();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            AlertDialog.Builder warning =
                    new AlertDialog.Builder(InvoiceDtlMultipleTab.this);
            warning.setTitle("Printer not Found");
            warning.setMessage("A suitable printer could not be found for printing. " +
                    "Ensure one is connected and try again.");
            warning.setNegativeButton("Try Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    printInvoice();
                }
            });
            warning.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            warning.show();
        }
    }

    private void getData(String docNo){
        ACDatabase db = new ACDatabase(InvoiceDtlMultipleTab.this);
        Cursor inv;
        Cursor invDetails;
        Cursor paymentDetails;
        if (docNo != null) {
            inv = db.getInvoiceHeaderPrint(docNo);
            invDetails = db.getInvoiceDetailsPrint(docNo);
            paymentDetails = db.getPaymentToUpload(docNo);


            // Read from table INV
            while (inv.moveToNext()) {
                invoice.setDocType(inv.getString(inv.getColumnIndex("DocType")));
                invoice.setDocDate(inv.getString(inv.getColumnIndex("DocDate")));
                invoice.setDocNo(docNo);
                invoice.setDebtorCode(inv.getString(inv.getColumnIndex("DebtorCode")));
                invoice.setDebtorName(inv.getString(inv.getColumnIndex("DebtorName")));
                invoice.setAgent(inv.getString(inv.getColumnIndex("SalesAgent")));

            }
            // Read from table INV_DTL
            try {
                Double totalEx = 0.00d;
                Double totalValue = 0.00d;
                Double totalIn = 0.00d;
                // Sum total
                while (invDetails.moveToNext()) {
                    // Hardcoded
                    totalEx += invDetails.getDouble(13);
                    totalValue += invDetails.getDouble(12);
                    totalIn += invDetails.getDouble(14);
                }
                invoice.setTotalEx(totalEx);
                invoice.setTotalTax(totalValue);
                invoice.setTotalIn(totalIn);
            } catch (Exception e) {
                Log.i("custDebug", e.getMessage());
            }

            // Read from table Payment
            StringBuilder stringBuilder = new StringBuilder();
            boolean first = true;
            while (paymentDetails.moveToNext()) {
                try {
//                    Log.i("custDebug", "size: " + paymentDetails.getCount());
                    payment = new AC_Class.Payment(paymentDetails.getString(1),
                            paymentDetails.getString(2), paymentDetails.getString(3),
                            paymentDetails.getString(4), paymentDetails.getDouble(5),
                            paymentDetails.getDouble(6), paymentDetails.getDouble(7),
                            paymentDetails.getString(8), paymentDetails.getString(9),
                            paymentDetails.getString(10), paymentDetails.getString(11),
                            paymentDetails.getString(12));
                    if (!first) { stringBuilder.append("/"); }
                    else { first = false; }
                    stringBuilder.append(paymentDetails.getString(4));
                } catch (Exception e) { Log.i("custDebug", e.getMessage()); }
            }
//            paymentMethod = stringBuilder.toString();
        }
    }

//    protected void onDestroy() {
//        super.onDestroy();
//        try {
//            if(bluetoothSocket!= null){
//                bluetoothSocket.close();
//                bluetoothSocket = null;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    protected void onDestroy() {
        super.onDestroy();
        try {
            if(pi != null){
                pi.disconnectBT();
                pi = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void printHeader(Canvas canvas)
    {
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
        canvas.drawText("SALES ORDER", pageWidth/2,y+20,titlePaint);


        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setTextSize(10);
        canvas.drawLine(marginLeft,y+30,80,y+30,myPaint);
        canvas.drawLine(marginLeft,y+30,marginLeft,y+50,myPaint);

        canvas.drawLine(300,y+30,330,y+30,myPaint);
        canvas.drawLine(330,y+30,330,y+50,myPaint);

        canvas.drawText(invoice.getDebtorCode() , marginLeft + 10,y+50,myPaint);
        canvas.drawText(invoice.getDebtorName() , marginLeft + 10,y + 65,myPaint);

        if(debtor.getAddress1()!=null) {
            canvas.drawText(debtor.getAddress1(), marginLeft + 10, y+80, myPaint);
        }
        if(debtor.getAddress2()!=null) {
            canvas.drawText(debtor.getAddress2(), marginLeft + 10, y+95, myPaint);
        }
        if(debtor.getAddress3()!=null) {
            canvas.drawText(debtor.getAddress3(), marginLeft + 10, y+110, myPaint);
        }
        if(debtor.getAddress4()!=null) {
            canvas.drawText(debtor.getAddress4(), marginLeft + 10, y+125, myPaint);
        }
        canvas.drawText("TEL   :" , marginLeft + 10,y+140,myPaint);
        if(debtor.getPhone()!=null) {
            canvas.drawText(debtor.getPhone(), 95, y+140, myPaint);
        }
        canvas.drawText("FAX   :" , (pageWidth/4) + 40,y+140,myPaint);
        if(debtor.getFax()!=null) {
            canvas.drawText(debtor.getFax(), (pageWidth/4) + 75, y+140, myPaint);
        }

        canvas.drawLine(marginLeft,y+150,80,y+150,myPaint);
        canvas.drawLine(marginLeft,y+120,marginLeft,y+150,myPaint);

        canvas.drawLine(300,y+150,330,y+150,myPaint);
        canvas.drawLine(330,y+120,330,y+150,myPaint);

        myPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Your Ref.",380, y+50,myPaint);

        canvas.drawText("Date",380, y+68,myPaint);
        canvas.drawText("Agent",380, y+86,myPaint);
        canvas.drawText("Attn",380, y+104,myPaint);
        canvas.drawText("Term",380, y+122,myPaint);
        canvas.drawText("Page",380, y+140,myPaint);

        canvas.drawText(":",440, y+50,myPaint);
        canvas.drawText(":",440, y+68,myPaint);
        canvas.drawText(":",440, y+86,myPaint);
        canvas.drawText(":",440, y+104,myPaint);
        canvas.drawText(":",440, y+122,myPaint);
        canvas.drawText(":",440, y+140,myPaint);

        myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        canvas.drawText(debtor.getDocNo(),460, y+50,myPaint);
        myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL));
        canvas.drawText(invoice.getDocDate(),460, y+68,myPaint);
        if(invoice.getAgent()!=null)
            canvas.drawText(invoice.getAgent(),460, y+86,myPaint);
        if(debtor.getAttention()!=null)
            canvas.drawText(debtor.getAttention(),460, y+104,myPaint);
        if(debtor.getCreditTerm()!=null)
            canvas.drawText(debtor.getCreditTerm(),460, y+122,myPaint);
        canvas.drawText(String.valueOf(currentPage),460, y+140,myPaint);

        myPaint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(marginLeft,y+160,pageWidth-marginLeft,y+160,myPaint);
        canvas.drawLine(marginLeft,y+185,pageWidth-marginLeft,y+185,myPaint);

        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setStyle(Paint.Style.FILL);
        myPaint.setTextSize(8);
        myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        canvas.drawText("No",60,y+173,myPaint);
        canvas.drawText("Item Code",90,y+173,myPaint);
        canvas.drawText("Description",170,y+173,myPaint);
        canvas.drawText("Qty",302,y+173,myPaint);
        canvas.drawText("UOM",350,y+173,myPaint);
        canvas.drawText("U/ Price",400,y+173,myPaint);
        canvas.drawText("Disc.",450,y+173,myPaint);
        canvas.drawText("Total (" + defaultCurr + ")",500,y+173,myPaint);
        myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL));

    }

    int pageHeight = 842;
    int pageWidth = 595;
    int marginLeft = 50;
    int currentPage = 1;
    int y = 50;

    public void createMyPDF() {

        String extstoragedir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File folder = new File(extstoragedir, "PSMSExport");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        try {
            final File file = new File(folder, invoice.getDocNo() + ".pdf");
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);

            getInvoiceDetailList(invoice.getDocNo());
            int listSize = invoiceDetailsArrayList.size() *150;
//            int pageHeight=1697;
//            int pageHeight= 870 + listSize + 300;
//            int pageWidth = 1200;


            document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new
                    PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPage).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            printHeader(canvas);

            Paint myPaint = new Paint();
            myPaint.setTextAlign(Paint.Align.LEFT);
            myPaint.setTextSize(8);
            myPaint.setColor(Color.BLACK);

            int start = y+210;

            if(invoiceDetailsArrayList.size()>0){

                for(int i=0;i<invoiceDetailsArrayList.size();i++){

                    if(start > 630) {
                        document.finishPage(page);
                        currentPage++;
                        pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPage).create();
                        page = document.startPage(pageInfo);
                        canvas = page.getCanvas();
                        printHeader(canvas);
                        start = y+210;
                    }

                    canvas.drawText(i + 1 + ".", 60, start, myPaint);
                    //canvas.drawText(invoiceDetailsArrayList.get(i).getItemCode(), 80, start, myPaint);
                    String mText2 = invoiceDetailsArrayList.get(i).getItemCode();
                    TextPaint mTextPaint2 = new TextPaint();
                    StaticLayout mTextLayout2 = new StaticLayout(mText2, mTextPaint2, 100, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
                    mTextPaint2.setTextSize(8);
                    canvas.save();

                    int textX2 = 80;
                    int textY2 = start - 12;

                    canvas.translate(textX2, textY2);
                    mTextLayout2.draw(canvas);
                    canvas.restore();

                    String mText = invoiceDetailsArrayList.get(i).getItemDescription() ;

                    if(invoiceDetailsArrayList.get(i).getBatchNo() !=null && invoiceDetailsArrayList.get(i).getBatchNo() != "" ){
                        mText += " ("+ invoiceDetailsArrayList.get(i).getBatchNo()+")";
                    }
                    TextPaint mTextPaint = new TextPaint();
                    StaticLayout mTextLayout = new StaticLayout(mText, mTextPaint, 150, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
                    mTextPaint.setTextSize(8);
                    canvas.save();

                    int textX = 170;
                    int textY = start - 12;

                    canvas.translate(textX, textY);
                    mTextLayout.draw(canvas);
                    canvas.restore();

                    myPaint.setTextAlign(Paint.Align.RIGHT);
                    canvas.drawText(invoiceDetailsArrayList.get(i).getQuantity().toString(), 320, start , myPaint);
                    canvas.drawText(invoiceDetailsArrayList.get(i).getUOM(), 370, start, myPaint);
                    canvas.drawText(String.format("%.2f",invoiceDetailsArrayList.get(i).getUPrice()), 430, start, myPaint);

                    //discountText
                    if(invoiceDetailsArrayList.get(i).getDiscount()!=null && invoiceDetailsArrayList.get(i).getDiscount()!=0) {

                        if (invoiceDetailsArrayList.get(i).getDiscountText() != null) {
                            String mText3 = invoiceDetailsArrayList.get(i).getDiscountText();

                            TextPaint mTextPaint3 = new TextPaint();
                            mTextPaint3.setTextSize(8);

                            // Measure the width of the text
                            float textWidth = mTextPaint3.measureText(mText3);

                            // Calculate the X position to center the text within the given width (60 pixels)
                            int textWidthLimit = 60;
                            int textX3 = 447 + (textWidthLimit - (int) textWidth) / 2;

                            // Create the StaticLayout for the text
                            StaticLayout mTextLayout3 = new StaticLayout(mText3, mTextPaint3, textWidthLimit, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);

                            canvas.save();
                            int textY3 = start - 8;

                            canvas.translate(textX3, textY3);
                            mTextLayout3.draw(canvas);
                            canvas.restore();
                        }


                    }

                    canvas.drawText(String.format("%.2f",invoiceDetailsArrayList.get(i).getTotal_In()), pageWidth - marginLeft - 10, start, myPaint);

                    start += mTextLayout.getHeight() - 20;
                    if(mTextLayout.getHeight() < mTextLayout2.getHeight())
                        start += mTextLayout2.getHeight()- 10;
                    myPaint.setTextAlign(Paint.Align.LEFT);
                    mTextPaint.setTextSize(10);
                    if(invoiceDetailsArrayList.get(i).getRemarks()!=null && !invoiceDetailsArrayList.get(i).getRemarks().equals("")) {
                        start += 15;
                        canvas.drawText( "REMARK: " + invoiceDetailsArrayList.get(i).getRemarks(), 80, start, myPaint);
                    }
                    if(invoiceDetailsArrayList.get(i).getRemarks2()!=null && !invoiceDetailsArrayList.get(i).getRemarks2().equals("")) {
                        start += 15;
                        canvas.drawText("REMARK 2: " +invoiceDetailsArrayList.get(i).getRemarks2(), 80, start, myPaint);
                    }

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
                start = y+210;
            }

            int end = 600;
            //myPaint.setColor(Color.rgb(247, 147, 30));
            canvas.drawLine(marginLeft,end+20,pageWidth-marginLeft,end+20 ,myPaint);
            //canvas.drawRect(380, end+20, pageWidth - marginLeft, end + 50, myPaint);
            canvas.drawText("RINGGIT MALAYSIA " + EnglishNumberToWords.convert(invoice.getTotalIn()), marginLeft + 10, end + 38, myPaint);
            myPaint.setTextSize(6);
            canvas.drawText("Notes:", marginLeft + 10, end + 58, myPaint);
            String[] myFormatArray = salesReceiptFormat.split("\n");


            myPaint.setColor(Color.BLACK);
            myPaint.setTextSize(8);
            myPaint.setTextAlign(Paint.Align.LEFT);
            myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
            canvas.drawText("Total", 420, end + 38, myPaint);
            myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL));
            myPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawLine(450, end+27, pageWidth - marginLeft, end + 27, myPaint);
            canvas.drawLine(450, end+27, 450, end + 40, myPaint);
            canvas.drawLine(450, end+40, pageWidth - marginLeft, end + 40, myPaint);
            canvas.drawLine(pageWidth - marginLeft, end+27, pageWidth - marginLeft, end + 40, myPaint);
            myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
            canvas.drawText(String.format("%.2f",invoice.getTotalIn()), pageWidth - marginLeft -10, end + 38, myPaint);
            myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL));
            if(debtor.getSignature()!=null){
                Bitmap decodedByte = BitmapFactory.decodeByteArray(Base64.decode(debtor.getSignature(), Base64.DEFAULT), 0,
                        Base64.decode(debtor.getSignature(), Base64.DEFAULT).length);

                Bitmap bitmapS = Bitmap.createScaledBitmap(decodedByte, 150, 150, false);

                if(bitmapS!=null){
                    myPaint.setTextAlign(Paint.Align.LEFT);
                    canvas.setDensity(100);
                    myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
                    canvas.drawText("Authorised Signature",
                            70, end + 220, myPaint);
                    myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL));
                    canvas.drawBitmap(bitmapS, 100, end + 163, myPaint);
                    canvas.drawLine(marginLeft, end+205, 210, end + 205, myPaint);;
                }
            }else{
                myPaint.setTextAlign(Paint.Align.LEFT);
                canvas.setDensity(100);
                myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
                canvas.drawText("Authorised Signature",
                        70, end + 220, myPaint);
                myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL));
                canvas.drawLine(marginLeft, end+205, 210, end + 205, myPaint);;
            }
            myPaint.setTextSize(6);
            myPaint.setTextAlign(Paint.Align.LEFT);
            for (String myFormat: myFormatArray)
            {
                myPaint.setColor(Color.BLACK);
                canvas.drawText(myFormat , marginLeft + 10, end + 68 ,myPaint);
                end = end + 10;
            }

            document.finishPage(page);
            document.writeTo(fOut);
            document.close();
            invoiceDetailsArrayList.clear();


        }catch (IOException e){
            Log.i("error",e.getLocalizedMessage());
        }
    }
    public void getInvoiceDetailList(String docNo)
    {
        Cursor data = db.getInvoiceDetailtoUpdate(docNo);

        if (data.getCount() > 0) {
            while (data.moveToNext()) {
                AC_Class.InvoiceDetails inv = new AC_Class.InvoiceDetails(data.getInt(0),
                        data.getString(1), data.getString(2), data.getString(3),
                        data.getString(4), data.getString(5), data.getDouble(6),
                        data.getDouble(7), data.getDouble(8), data.getDouble(9),
                        data.getString(10), data.getDouble(11), data.getDouble(12),
                        data.getDouble(13), data.getDouble(14), data.getString(15),
                        data.getString(16), data.getString(data.getColumnIndex("BatchNo")),
                        data.getString(data.getColumnIndex("Remarks2")),
                        data.getString(data.getColumnIndex("DiscountText")));
                invoiceDetailsArrayList.add(inv);
            }
        }

    }

    private boolean checkBlueToothPermission() {
        //check if storage permission is enabled or not and return true/false
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestBluetoothPermissionImport() {
        //request storage permission for import
        ActivityCompat.requestPermissions(this, bluetoothPermissions, BLUETOOTH_REQUEST_CODE_IMPORT);
    }


}