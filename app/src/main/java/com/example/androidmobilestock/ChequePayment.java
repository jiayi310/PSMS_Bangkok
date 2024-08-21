package com.example.androidmobilestock;

import android.content.DialogInterface;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.androidmobilestock.databinding.ActivityChequePaymentBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChequePayment extends AppCompatActivity {

    String key;
    Boolean IsEmpty = false;
    ActivityChequePaymentBinding binding;
    AC_Class.Payment payment;
    Boolean isCheque1 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheque_payment);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cheque_payment);
        payment = new AC_Class.Payment();
        binding.setPayment(payment);
        binding.tvChequeAmt.requestFocus();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Cheque Payment");
        actionBar.setDisplayHomeAsUpEnabled(true);
        PaymentSelection();
        binding.tvChequeAmt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String temp = s.toString();
                    if (!TextUtils.isEmpty(temp)) {
                        binding.tvChequeAmt.removeTextChangedListener(this);

                        temp = temp.replaceAll("[.]", "");
                        Log.i("custDebug", temp);
                        StringBuffer sb = new StringBuffer(temp);
                        sb.insert(0, "000");
                        sb.insert(sb.length()-2, ".");
                        temp = sb.toString();
                        Log.i("custDebug", temp);
                        Float payAmt = Float.parseFloat(temp);
                        binding.tvChequeAmt.setText(String.format(Locale.getDefault(),
                                "%.2f", payAmt));
                        payment.setPaymentAmt(Double.valueOf(payAmt));

                        binding.tvChequeAmt.addTextChangedListener(this);
                    } else {
                        payment.setPaymentAmt(0.00d);
                        binding.tvChequeAmt.setText(String.format(Locale.getDefault(),
                                "%.2f", payment.getPaymentAmt()));
                    }
                    binding.tvChequeAmt.setSelection(binding.tvChequeAmt.length());
                } catch (Exception e) {
                    Log.i("custDebug", e.getMessage());
                }
//                Calculation();
            }
        });
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

    // Bcast intent to close all activities
    private void closeAll(){
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.package.ACTION_LOGOUT");
        sendBroadcast(broadcastIntent);
        finish();
    }

    public void btnOKClicked(View view) {
        if (CheckEmpty() == true)
        {
            if (TextUtils.isEmpty(binding.tvChequeNo.getText().toString()))
            {
                binding.tvChequeNo.setError("This field cannot be blank.");
            }
            if (TextUtils.isEmpty(binding.tvChequeAmt.getText().toString()))
            {
                binding.tvChequeAmt.setError("This field cannot be blank.");
            }
        }
        else {
            if (binding.tvChequeNo.length() > 20) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChequePayment.this);
                builder.setTitle("Attention!");
                builder.setMessage("The maximum cheque no. is 20 digits only.");
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
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                String date = sdf.format(new Date());
                payment.setPaymentTime(date);
//                if (payment.getPaymentAmt() > payment.getOriginalAmt()) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                    builder.setTitle("Attention!");
//                    builder.setMessage("Amount exceeded. Please enter valid amount.");
//                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                            binding.tvChequeAmt.selectAll();
//                        }
//                    });
//                    AlertDialog dialog = builder.create();
//                    dialog.show();
//                }
//                else
//                {
                payment.setPaymentType("MultiPayment");
                payment.setPaymentMethod("Cheque");
                payment.setCashChanges(Double.valueOf(Float.valueOf(binding.tvNetTotal.getText().toString()) - Float.valueOf(binding.tvChequeAmt.getText().toString())));
                payment.setCCType(null);
                payment.setCCNo(null);
                payment.setCCExpiryDate(null);
                payment.setCCApprovalCode(null);
                payment.setPaymentAmt(Double.valueOf(Float.valueOf(binding.tvChequeAmt.getText().toString())));
                payment.setChequeNo(binding.tvChequeNo.getText().toString());
                Intent intent = new Intent(ChequePayment.this, MultiPayment.class);
                intent.putExtra("ChequePaymentKey", payment);
                setResult(RESULT_OK, intent);
                finish();
//                }
            }
        }
    }

    public boolean CheckEmpty()
    {
        if (TextUtils.isEmpty(binding.tvChequeNo.getText().toString()) || TextUtils.isEmpty(binding.tvChequeAmt.getText().toString()))
        {
            IsEmpty = true;
        }
        else
        {
            IsEmpty = false;
        }
        return IsEmpty;
    }

    private void PaymentSelection()
    {
        key = getIntent().getStringExtra("ChequeKey");
        if (key.equals("Cheque1"))
        {
            Double TotalAmt = getIntent().getDoubleExtra("TotalKey", 0);
            payment.setOriginalAmt(Double.valueOf(TotalAmt));
            String DocNo = getIntent().getStringExtra("MDocNoKey");
            payment.setDocNo(DocNo);
            isCheque1 = true;
        }
        else if (key.equals("Cheque2"))
        {
            Double TotalAmt = getIntent().getDoubleExtra("TotalKey", 0);
            payment.setOriginalAmt(Double.valueOf(TotalAmt));
            String DocNo = getIntent().getStringExtra("MDocNoKey");
            payment.setDocNo(DocNo);
            isCheque1 = false;
        }
    }
}
