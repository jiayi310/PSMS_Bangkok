package com.example.androidmobilestock_bangkok;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.androidmobilestock_bangkok.databinding.ActivityArmultipleTabBinding;
import com.example.androidmobilestock_bangkok.tools.EnglishNumberToWords;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ARMultipleTab extends AppCompatActivity {

    ActivityArmultipleTabBinding binding;
    AC_Class.ARPayment arMenu = new AC_Class.ARPayment();
    List<AC_Class.ARPaymentDtl> arPaymentDtls = new ArrayList<>();
    ListView listview;
    ACDatabase db;
    PdfDocument document;
    String companyHeader,ReceiptFormat;
    String defaultCurr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_armultiple_tab);
        db = new ACDatabase(this);
        String docNo = getIntent().getStringExtra("arDoc");

        ActionBar actionBar = getSupportActionBar();
        try {
            actionBar.setTitle(docNo+" Details");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(new ColorDrawable(0xFFf39f61));
        } catch (Exception e) {
            Log.i("custDebug", "ARPayment Details: "+e.getMessage());}

        Cursor cur = db.getReg("6");
        if(cur.moveToFirst()){
            defaultCurr = cur.getString(0);
            binding.setDefaultCurr(cur.getString(0));;
        }
        Cursor header = db.getReg("16");
        if(header.moveToFirst()){
            companyHeader = header.getString(0);
        }
        Cursor sales = db.getReg("63");
        if(sales.moveToFirst()){
            ReceiptFormat = sales.getString(0);
        }
        binding.setAr(arMenu);
        listview = (ListView)binding.lvDetail;

//        Bitmap bitmap = db.getARImage(docNo);
//        if (bitmap != null) {
//            binding.imageView3.setImageBitmap(bitmap);
//        } else {
//            binding.imageView3.setVisibility(View.INVISIBLE);
//        }

        getData(docNo);

        if (arMenu.getRemark() == null || arMenu.getRemark().equals("")) {
            binding.txtRemark.setVisibility(View.INVISIBLE);
            binding.txtRemarkText.setVisibility(View.INVISIBLE);
        }
    }
    private void getData(String docNo){

        ACDatabase db = new ACDatabase(ARMultipleTab.this);

        if (docNo != null) {
            Cursor header = db.getARHeaderPrint(docNo);
            if (header.getCount()>0)
            {
                while (header.moveToNext()) {
                    arMenu.setDate(header.getString(header.getColumnIndex("Date")));
                    arMenu.setDocNo(docNo);
                    arMenu.setDebtorCode(header.getString(header.getColumnIndex("DebtorCode")));
                    arMenu.setDebtorName(header.getString(header.getColumnIndex("DebtorName")));
                    arMenu.setAmount(header.getDouble(header.getColumnIndex("Amount")));
                    arMenu.setRemark(header.getString(header.getColumnIndex("Remark")));
                }
            }

            Cursor data = db.getARDetailtoUpdate(docNo);
            List<AC_Class.ARPaymentDtl> dtlList = new ArrayList<>();
            if (data.getCount() > 0) {

                while (data.moveToNext()) {
                    AC_Class.ARPaymentDtl myDtil = new AC_Class.ARPaymentDtl(
                            data.getString(data.getColumnIndex("DocNo")),
                            data.getString(data.getColumnIndex("DocNumber")),
                            data.getString(data.getColumnIndex("DocDate")),
                            data.getDouble(data.getColumnIndex("PaymentAmount")),
                            data.getDouble(data.getColumnIndex("OrgAmt")));
                    dtlList.add(myDtil);
                }

                //arMenu.setAmount(total);
            }

            ARPaymentDtlListAdapter adapter = new ARPaymentDtlListAdapter(ARMultipleTab.this, dtlList);
            listview.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cash_payment, menu);
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

        if (id == R.id.pic)
        {
            Intent intent = new Intent(ARMultipleTab.this, ReceiptView.class);
            intent.putExtra("docNo", arMenu.getDocNo());
            startActivity(intent);
            return true;
        }

        if (id == R.id.pdf)
        {
            createMyPDF( );

            // Path to your PDF file
            File pdfFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "PSMSExport/" + arMenu.getDocNo() + ".pdf");

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
        return super.onOptionsItemSelected(item);
    }

    int pageHeight = 842/2;
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
            final File file = new File(folder, arMenu.getDocNo() + ".pdf");
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);

            getARDetailList(arMenu.getDocNo());
            int listSize = arPaymentDtls.size() *150;
//            int pageHeight=1697;
//            int pageHeight= 870 + listSize + 300;
//            int pageWidth = 1200;


            document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new
                    PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPage).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            printHeader(canvas);
            printDetail(canvas , y);
            printPaymentDetail(canvas, y-30);

            Paint myPaint = new Paint();
            myPaint.setTextAlign(Paint.Align.LEFT);
            myPaint.setTextSize(8);
            myPaint.setColor(Color.BLACK);

            int start = y+230;

            if(arPaymentDtls.size()>0){

                for(int i=0;i<arPaymentDtls.size();i++){

                    if(start > 358) {
                        document.finishPage(page);
                        currentPage++;
                        pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPage).create();
                        page = document.startPage(pageInfo);
                        canvas = page.getCanvas();
                        printHeader(canvas);
                        start = y + 115;
                        printPaymentDetail(canvas, y-140);
                    }
                    canvas.drawText(arPaymentDtls.get(i).getDocNumber(), 60, start, myPaint);
                    if(arPaymentDtls.get(i).getDocDate() !=null)
                        canvas.drawText(arPaymentDtls.get(i).getDocDate(), 130, start, myPaint);
                    myPaint.setTextAlign(Paint.Align.RIGHT);
                    if(arPaymentDtls.get(i).getOrgAmt() !=null) {
                        Double outstanding = arPaymentDtls.get(i).getOrgAmt() - arPaymentDtls.get(i).getPaymentAmount();
                        canvas.drawText(String.format("%.2f", arPaymentDtls.get(i).getOrgAmt()), 265, start, myPaint);
                        canvas.drawText(String.format("%.2f", outstanding), 355, start, myPaint);
                    }
                    if(arPaymentDtls.get(i).getPaymentAmount() !=null) {
                        canvas.drawText(String.format("%.2f", arPaymentDtls.get(i).getPaymentAmount()), 425, start, myPaint);
                    }
                    myPaint.setTextAlign(Paint.Align.LEFT);
                    start += 10;

                }
            }

            if(start > 338) {
                document.finishPage(page);
                currentPage++;
                pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPage).create();
                page = document.startPage(pageInfo);
                canvas = page.getCanvas();
                printHeader(canvas);
                start = y+210;
            }

            int end = 300;

            canvas.drawLine(marginLeft, end + 38, pageWidth - marginLeft, end + 38, myPaint);
            canvas.drawText("TOTAL: ", pageWidth - marginLeft -100, end + 48, myPaint);

            myPaint.setTypeface(Typeface.create("Tahoma", Typeface.BOLD));
            canvas.drawText(String.format("%.2f",arMenu.getAmount()), pageWidth - marginLeft -48, end + 48, myPaint);
            myPaint.setTextSize(6);
            myPaint.setTypeface(Typeface.create("Tahoma", Typeface.NORMAL));
            String[] myFormatArray = ReceiptFormat.split("\n");
            for (String myFormat: myFormatArray)
            {
                myPaint.setColor(Color.BLACK);
                canvas.drawText(myFormat , 60, end + 48 ,myPaint);
                end = end + 10;
            }
//            canvas.drawText("The official receipt is onliy valid upon the transaction been shown as cleared in our bank account.", 60, end + 48, myPaint);
//            canvas.drawText("NB", 60, end + 58 , myPaint);
//            canvas.drawText("Validity of This Receipt", 60, end + 68 , myPaint);
//            canvas.drawText("Subject to Clearing of Cheque", 60, end + 78 , myPaint);


            //            if(debtor.getSignature()!=null){
//                Bitmap decodedByte = BitmapFactory.decodeByteArray(Base64.decode(debtor.getSignature(), Base64.DEFAULT), 0,
//                        Base64.decode(debtor.getSignature(), Base64.DEFAULT).length);
//
//                Bitmap bitmapS = Bitmap.createScaledBitmap(decodedByte, 200, 200, false);
//
//                if(bitmapS!=null){
//                    myPaint.setTextAlign(Paint.Align.RIGHT);
//                    canvas.setDensity(100);
//                    canvas.drawText("Accepted by:",
//                            160, end + 68, myPaint);
//                    canvas.drawBitmap(bitmapS, 80, end + 75, myPaint);
//                    canvas.drawText("-------------------",
//                            180, end + 140, myPaint);
//                }
//            }
            document.finishPage(page);

            document.writeTo(fOut);
            document.close();
            arPaymentDtls.clear();

        }catch (IOException e){
            Log.i("error",e.getLocalizedMessage());
        }
    }

    public void getARDetailList(String docNo)
    {
        Cursor data = db.getARDetailtoUpdate(docNo);

        if (data.getCount() > 0) {
            while (data.moveToNext()) {
                AC_Class.ARPaymentDtl arPaymentDtl = new AC_Class.ARPaymentDtl(
                        data.getString(data.getColumnIndex("DocNo")),
                        data.getString(data.getColumnIndex("DocNumber")),
                        data.getString(data.getColumnIndex("DocDate")),
                        data.getDouble(data.getColumnIndex("PaymentAmount")),
                        data.getDouble(data.getColumnIndex("OrgAmt")));
                arPaymentDtls.add(arPaymentDtl);
            }
        }

    }

    public void printHeader(Canvas canvas) {
        y = 50;
        Paint myPaint = new Paint();
        Paint titlePaint = new Paint();

        String[] myCompanyArray = companyHeader.split("\n");


        for (String myCompany: myCompanyArray)
        {
            myPaint.setTextAlign(Paint.Align.CENTER);
            myPaint.setTextSize(8);
            myPaint.setColor(Color.BLACK);
            canvas.drawText(myCompany , pageWidth/2,y,myPaint);
            y = y + 10;
        }

        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create("Tahoma", Typeface.BOLD));
        titlePaint.setTextSize(16);
        canvas.drawText("OFFICIAL RECEIPT", pageWidth / 2, y + 20, titlePaint);

        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setTextSize(8);
        myPaint.setColor(Color.BLACK);
        myPaint.setTypeface(Typeface.create("Tahoma",Typeface.NORMAL));
        canvas.drawText("RECEIVED FROM ", marginLeft, y + 50, myPaint);
        canvas.drawText(arMenu.getDebtorName(), 180, y + 50, myPaint);

        myPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Voucher No.", 410, y + 50, myPaint);
        canvas.drawText("Date", 410, y + 68, myPaint);

        canvas.drawText(":", 460, y + 50, myPaint);
        canvas.drawText(":", 460, y + 68, myPaint);


        canvas.drawText(arMenu.getDocNo(), 475, y + 50, myPaint);
        canvas.drawText(arMenu.getDate(), 475, y + 68, myPaint);

        y += 18;
        canvas.drawText("RECEIVE THE SUM OF ", marginLeft, y + 65, myPaint);
        titlePaint.setFlags(Paint.UNDERLINE_TEXT_FLAG);
        titlePaint.setTypeface(Typeface.create("Tahoma", Typeface.NORMAL));
        titlePaint.setTextSize(8);
        titlePaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("RINGGIT MALAYSIA " + EnglishNumberToWords.convert(arMenu.getAmount()), 180, y + 65, titlePaint);


    }

    public void printDetail(Canvas canvas , int t){

        y = t + 14;
        Paint myPaint = new Paint();
        Paint titlePaint = new Paint();

        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setTextSize(8);
        myPaint.setTypeface(Typeface.create("Tahoma", Typeface.ITALIC));
        canvas.drawText("Payment Issued ", marginLeft + 10, y + 68, myPaint);
        y = y - 3;
        canvas.drawText("Paid For ", marginLeft + 10, y + 130, myPaint);

        myPaint.setTypeface(Typeface.create("Tahoma", Typeface.NORMAL));

        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(1);
        canvas.drawRect(marginLeft, y + 80, ((pageWidth - marginLeft) / 2) + 20, y + 100, myPaint);

        myPaint.setStyle(Paint.Style.FILL);
        myPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Payment By", 60, y + 90, myPaint);
        canvas.drawText("Cheque No.", 140, y + 90, myPaint);
        canvas.drawText("Payment Amount", 215, y + 90, myPaint);

        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(1);
        canvas.drawRect(marginLeft, y + 100, ((pageWidth - marginLeft) / 2) + 20, y + 120, myPaint);
        myPaint.setStyle(Paint.Style.FILL);
        canvas.drawText(String.format(Locale.getDefault(),
                " %.2f", arMenu.getAmount()), 250, y + 110, myPaint);

        canvas.drawLine(marginLeft, y + 140, pageWidth - marginLeft, y + 140, myPaint);

        canvas.drawText("Acc. No.", 60, y + 150, myPaint);
        canvas.drawText("Description", 150, y + 150, myPaint);
        canvas.drawText("Amount", pageWidth - marginLeft - 50, y + 150, myPaint);

        canvas.drawLine(marginLeft, y + 160, pageWidth - marginLeft, y + 160, myPaint);

        canvas.drawText(arMenu.getDebtorCode(), 60, y + 170, myPaint);
        if(arMenu.getRemark() != null) {
            canvas.drawText(arMenu.getRemark(), 150, y + 170, myPaint);
        }
        canvas.drawText(String.format(Locale.getDefault(),
                " %.2f", arMenu.getAmount()), pageWidth - marginLeft - 50, y + 170, myPaint);


    }

    public void printPaymentDetail(Canvas canvas, int t){
        y = t + 28;
        Paint myPaint = new Paint();
        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setTextSize(8);
        myPaint.setTypeface(Typeface.create("Tahoma", Typeface.ITALIC));
        canvas.drawText("Payment Details ", marginLeft + 10, y + 190, myPaint);
        myPaint.setTypeface(Typeface.create("Tahoma", Typeface.NORMAL));
        canvas.drawLine(marginLeft, y + 200, ((pageWidth - marginLeft)/2) + 160, y + 200, myPaint);
        canvas.drawLine(marginLeft, y + 215, ((pageWidth - marginLeft)/2) + 160, y + 215, myPaint);

        canvas.drawText("Doc. No. ", marginLeft + 10, y + 210, myPaint);
        canvas.drawText("Doc Date", 130, y + 210, myPaint);
        canvas.drawText("Org.Amt", 230, y + 210, myPaint);
        canvas.drawText("Outstanding", 305, y + 210, myPaint);
        canvas.drawText("Paid Amount", 375, y + 210, myPaint);

        canvas.drawLine(125,y+200,125,y+215,myPaint);
        canvas.drawLine(190,y+200,190,y+215,myPaint);
        canvas.drawLine(270,y+200,270,y+215,myPaint);
        canvas.drawLine(360,y+200,360,y+215,myPaint);
    }
}