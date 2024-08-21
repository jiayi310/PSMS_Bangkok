package com.example.androidmobilestock;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.databinding.DataBindingUtil;

import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import com.example.androidmobilestock.databinding.ActivityMultiPaymentBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MultiPayment extends AppCompatActivity {
    MyClickHandler handler;
    ActivityMultiPaymentBinding binding;
    ACDatabase db;
    AC_Class.Payment payment;
    Double OutStanding;
    List<AC_Class.Payment> payments;
    Double OriginalAmt;
    AC_Class.CheckOut checkOut;
    AC_Class.Invoice invoice;
    MPaymentListAdapter mAdapter;
    String companyHeader;
    String user;
    String jobsheetDocNO;
    int func;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_multi_payment);
        payment = new AC_Class.Payment();
        binding.setPayment(payment);
        payments = new ArrayList<>();
        mAdapter = new MPaymentListAdapter(this, payments);
        binding.listPayment.setAdapter(mAdapter);

        db = new ACDatabase(this);

        // Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Multi Payment");
        actionBar.setDisplayHomeAsUpEnabled(true);

        Cursor header = db.getReg("16");
        if(header.moveToFirst()){
            companyHeader = header.getString(0);
        }

        // Get invoice header
        invoice = getIntent().getParcelableExtra("Inv");
        func = getIntent().getIntExtra("Func", 0);
        jobsheetDocNO = getIntent().getStringExtra("JobsheetDocNo");


        View headerView = getLayoutInflater().inflate(R.layout.multipayment_listheader, null);
        binding.listPayment.addHeaderView(headerView);

        // Delete Payments
        binding.listPayment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                if (position != 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MultiPayment.this);
//                    builder.setMessage("Delete payment?");
                    builder.setMessage("What would you like to do?");
                    builder.setNeutralButton("Edit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String method = payments.get(position - 1).getPaymentMethod();
                            // Edit payment
                            payments.remove(position - 1);
                            mAdapter.notifyDataSetChanged();
                            Calculation();
                            // Open payment
                            MyClickHandler clickHandler = new MyClickHandler(getBaseContext());
                            switch (method) {
                                case "Cash":
                                    clickHandler.OnCashButtonClicked(view);
                                    break;
                                case "Credit Card":
                                    clickHandler.OnCcardButtonClicked(view);
                                    break;
                                case "Cheque":
                                    clickHandler.OnChequeButtonClicked(view);
                                    break;
                            }
                        }
                    });
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            payments.remove(position - 1);
                            mAdapter.notifyDataSetChanged();
                            Calculation();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        handler = new MyClickHandler(this);
        binding.setHandler(handler);
        checkSize();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    payment = data.getParcelableExtra("CashPaymentKey");
                    payments.add(payment);
                    mAdapter.notifyDataSetChanged();
                    Calculation();
                }
                break;

            case 2:
                if (resultCode == RESULT_OK) {
                    payment = data.getParcelableExtra("CardPaymentKey");
                    payments.add(payment);
                    mAdapter.notifyDataSetChanged();
                    Calculation();
                }
                break;

            case 3:
                if (resultCode == RESULT_OK) {
                    payment = data.getParcelableExtra("ChequePaymentKey");
                    payments.add(payment);
                    mAdapter.notifyDataSetChanged();
                    Calculation();
                }
                break;
        }
    }

    public class MyClickHandler {
        Context context;

        public MyClickHandler(Context context) {
            this.context = context;
        }

        public void OnCashButtonClicked(View view) {
            Intent cash_intent = new Intent(MultiPayment.this, CashPayment.class);
            cash_intent.putExtra("CashKey", "Cash");
            if (!checkSize()) {
                cash_intent.putExtra("TotalKey", payment.getOriginalAmt()); //Amt that needs to be paid
            } else {
                cash_intent.putExtra("TotalKey", OutStanding); //Amt that needs to be paid
            }
            cash_intent.putExtra("MDocNoKey", payment.getDocNo()); // Doc No.
            startActivityForResult(cash_intent, 1);
//            if (checkSize()) {
//                Intent cash_intent = new Intent(MultiPayment.this, CashPayment.class);
//                cash_intent.putExtra("CashKey", "Cash");
//                cash_intent.putExtra("TotalKey", payment.getOriginalAmt()); //Amt that need to be paid
//                cash_intent.putExtra("MDocNoKey", payment.getDocNo()); // Doc No.
//                startActivityForResult(cash_intent, 1);
//            } else {
//                Intent cash_intent = new Intent(MultiPayment.this, CashPayment.class);
//                cash_intent.putExtra("CashKey", "Cash");
//                cash_intent.putExtra("TotalKey", NeedPayAmt); //Amt that need to be paid
//                cash_intent.putExtra("MDocNoKey", payment.getDocNo());
//                startActivityForResult(cash_intent, 1);
//            }
        }

        public void OnCcardButtonClicked(View view) {
            Intent card_intent = new Intent(MultiPayment.this, CreditCardPayment.class);
            card_intent.putExtra("CCardKey", "CreditCard");
            if (!checkSize()) {
                card_intent.putExtra("TotalKey", payment.getOriginalAmt());
            }
            else
            {
//                Intent card_intent = new Intent(MultiPayment.this, CreditCardPayment.class);
//                card_intent.putExtra("CCardKey", "CreditCard");
                card_intent.putExtra("TotalKey", OutStanding); //Amt that need to be paid
//                card_intent.putExtra("MDocNoKey", payment.getDocNo());
//                startActivityForResult(card_intent, 2);
            }
            card_intent.putExtra("MDocNoKey", payment.getDocNo());
            startActivityForResult(card_intent, 2);
        }

        public void OnChequeButtonClicked(View view) {
            Intent cheque_intent = new Intent(MultiPayment.this, ChequePayment.class);
            cheque_intent.putExtra("ChequeKey", "Cheque1");
            if (!checkSize()) {
                cheque_intent.putExtra("TotalKey", payment.getOriginalAmt());
            }
            else
            {
//                Intent cheque_intent = new Intent(MultiPayment.this, ChequePayment.class);
//                cheque_intent.putExtra("ChequeKey", "Cheque2");
                cheque_intent.putExtra("TotalKey", OutStanding);
//                cheque_intent.putExtra("MDocNoKey", payment.getDocNo());
//                startActivityForResult(cheque_intent, 3);
            }
            cheque_intent.putExtra("MDocNoKey", payment.getDocNo());
            startActivityForResult(cheque_intent, 3);
        }

        public void OnOKButtonClicked(View view) {
//            if (!String.format(Locale.getDefault(), "%.2f", OutStanding).equals("0.00"))
//            {
           // Log.i("custDebug", String.format("%.2f, %.2f", OriginalAmt - totalCcCheque(), OutStanding));
            if(OutStanding != null && OriginalAmt != null) {
                if (OutStanding == 0.00f && totalCcCheque() <= OriginalAmt) {
                    int size = payments.size();
                    // Commit details
                    boolean commitDetails = db.UpdateInvoiceDetail(invoice);
                    // Insert header
                    invoice.setDocType("CS");

                    Cursor debtor = db.getReg("4");
                    if (debtor.moveToFirst()) {
                        user = debtor.getString(0);
                    }

                    boolean insertHeader = db.insertInv(invoice);
                    if (invoice.getDocNo().equals(db.getNextNo())) {
                        db.IncrementAutoNumbering("S");
                    }
                    //Insert price history
                    //boolean tks = db.insertHistoryPrice(invoice);
                    //Log.i("custDebug", String.valueOf(tks));
                    // Insert payment
                    boolean insertPayment = true;
                    for (int i = 0; i < size; i++) {
                        payment = ((AC_Class.Payment) (payments.get(i)));
                        // Check all payments == true
                        insertPayment = db.insertPayment(payment.getDocNo(), payment.getPaymentTime(),
                                payment.getPaymentType(), payment.getPaymentMethod(), OriginalAmt,
                                payment.getPaymentAmt(), payment.getCashChanges(), payment.getCCType(),
                                payment.getCCNo(), payment.getCCExpiryDate(), payment.getCCApprovalCode(),
                                payment.getChequeNo()) && insertPayment;
                    }
                    // Print Receipt
                    if (insertHeader && commitDetails && insertPayment) {
//                    final AlertDialog.Builder builder = new AlertDialog.Builder(MultiPayment.this);
//                    builder.setTitle("Attention!");
//                    builder.setMessage("Do you want to print the receipt?");
//                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            AC_Class.PrintInvoice pi = new AC_Class.PrintInvoice(getApplicationContext(), payment.getDocNo(), companyHeader);
//                            pi.FindBluetoothDevice();
//                            try {
//                                pi.openBluetoothPrinter();
//                                pi.printData();
//                                //pi.disconnectBT();
//                            }
//                            catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            closeAll();
//                        }
//                    });
//                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                            closeAll();
//                        }
//                    });
//                    AlertDialog dialog = builder.create();
//                    dialog.show();
                        closeAll();
                    } else {
                        Toast.makeText(MultiPayment.this, "Error Updating Payment", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MultiPayment.this);
                    builder.setTitle("Attention!");
                    builder.setMessage("Please make sure that your amount paid is correct.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(MultiPayment.this);
                builder.setTitle("Attention!");
                builder.setMessage("Make at least 1 payment.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
//            else if (!checkQty().equals("")) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(MultiPayment.this);
//                builder.setTitle("Balance Exceeded");
//                builder.setMessage(checkQty());
//                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//                AlertDialog dialog = builder.create();
//                dialog.show();
//            }
//            else {
//                int size = payments.size();
//                // Commit details
//                boolean commitDetails = db.UpdateInvoiceDetail(invoice);
//                // Insert header
//                invoice.setDocType("CS");
//                boolean insertHeader = db.insertInv(invoice);
//                if (invoice.getDocNo().equals(db.getNextNo())) {
//                    db.IncrementAutoNumbering();
//                }
//                //Insert price history
//                boolean tks = db.insertHistoryPrice(invoice);
//                Log.i("custDebug", String.valueOf(tks));
//                // Insert payment
//                boolean insertPayment = true;
//                for (int i = 0; i < size; i++) {
//                    payment = ((AC_Class.Payment) (payments.get(i)));
//                    // Check all payments == true
//                    insertPayment = db.insertPayment(payment.getDocNo(), payment.getPaymentTime(),
//                            payment.getPaymentType(), payment.getPaymentMethod(), OriginalAmt,
//                            payment.getPaymentAmt(), payment.getCashChanges(), payment.getCCType(),
//                            payment.getCCNo(), payment.getCCExpiryDate(), payment.getCCApprovalCode(),
//                            payment.getChequeNo()) && insertPayment;
//                }
//                // Print Receipt
//                if (insertHeader && commitDetails && insertPayment) {
//                    final AlertDialog.Builder builder = new AlertDialog.Builder(MultiPayment.this);
//                    builder.setTitle("Attention!");
//                    builder.setMessage("Do you want to print the receipt?");
//                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            AC_Class.PrintInvoice pi = new AC_Class.PrintInvoice(getApplicationContext(), payment.getDocNo());
//                            pi.FindBluetoothDevice();
//                            try {
//                                pi.openBluetoothPrinter();
//                                pi.printData();
//                                pi.disconnectBT();
//                            }
//                            catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                            closeAll();
//                        }
//                    });
//                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                            closeAll();
//                        }
//                    });
//                    AlertDialog dialog = builder.create();
//                    dialog.show();
//                }
//                else {
//                    Toast.makeText(MultiPayment.this, "Error Updating Payment", Toast.LENGTH_SHORT).show();
//                }
//            }
        }

        public void OnCancelButtonClicked(View view)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MultiPayment.this);
            builder.setTitle("Attention!");
            builder.setMessage("Are you sure to cancel the payment?");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    Intent intent = new Intent(MultiPayment.this, CheckOut.class);
//                    startActivity(intent);
                    onBackPressed();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    // Bcast intent to close all activities
    private void closeAll(){
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.package.ACTION_LOGOUT");
        sendBroadcast(broadcastIntent);

        if (func == 1){
            Intent new_intent = new Intent(MultiPayment.this, Jobsheet_Details.class);
            new_intent.putExtra("docNo", jobsheetDocNO);
            new_intent.putExtra("debtorCode", invoice.getDebtorCode());
            new_intent.putExtra("isFresh", true);
            startActivity(new_intent);
            finish();
        } else {
            // Open inv details
            Intent new_intent = new Intent(MultiPayment.this,
                    InvoiceDtlMultipleTab.class);
            new_intent.putExtra("docNo", invoice.getDocNo());
            new_intent.putExtra("debtorCode", invoice.getDebtorCode());
            new_intent.putExtra("isFresh", true);
            startActivity(new_intent);
            finish();
        }


    }

    public void Calculation()
    {
//        if (checkSize())
//        {
        OutStanding = OriginalAmt;
        int size = payments.size();
        // Deduct existing payment amts
        for (int i = 0; i<size; i++) {
            OutStanding -= ((AC_Class.Payment) (payments.get(i))).PaymentAmt;
        }
        if (OutStanding == 0)
        { Log.i("custDebug", "OutStanding == 0: "+OutStanding); }
        else { Log.i("custDebug", "OutStanding != 0: "+OutStanding); }
//        }
        binding.AmtLeft.setText(String.format(Locale.getDefault(), "Amount Left: %.2f", OutStanding));
    }

    // Get total of Cc and Cheque payments
    float totalCcCheque(){
        float total = 0.00f;
        for (int i=0; i<payments.size(); i++) {
            AC_Class.Payment currPayment = payments.get(i);
            if (currPayment.getPaymentMethod().equals("Credit Card") ||
                    currPayment.getPaymentMethod().equals("Cheque")) {
                total += currPayment.PaymentAmt;
            }
        }

        Log.i("custDebug", String.format("%.2f", total));
        return total;
    }

    // Checks if prior payment methods have been added
    public Boolean checkSize()
    {
        checkOut = getIntent().getParcelableExtra("CheckOutKey");
        if (payments.size() == 0){
//            checkOut = getIntent().getParcelableExtra("CheckOutKey");
            payment.setDocNo(checkOut.getDocNo());
            payment.setOriginalAmt(checkOut.getTotal());
            OriginalAmt = payment.getOriginalAmt();
            binding.AmtLeft.setText(String.format(Locale.getDefault(), "Amount Left: %.2f", payment.getOriginalAmt()));
            return false;
        }
        else
        {
            OriginalAmt = checkOut.getTotal();
            return true;
        }
    }
}
