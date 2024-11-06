package com.example.androidmobilestock_bangkok;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.androidmobilestock_bangkok.databinding.ActivityArpayBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ARPayment extends AppCompatActivity {

    ActivityArpayBinding binding;
    public MyClickHandler handler;
    private DatePickerDialog.OnDateSetListener mDataSetListener;
    private AC_Class.ARPayment arPayment;
    ACDatabase db;
    String docNo;

    String def_Debtor;
    String def_Agent;
    android.widget.ImageView imageView;
    Bitmap bitmap;
    byte[] img;
    String encodedImage;
    BroadcastReceiver exitReceiver;
    String typeFP;
    String user;
    String Func;
    String CollectionDetails = "false";

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    Bitmap bitmapC;

    static final String DEBTORCODE = "debtorcode";
    static final String DEBTORNAME = "debtorname";
    static final String AMOUNT = "amount";
    static final String REMARK = "remark";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
        }
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_arpay);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFFf39f61));
        actionBar.setDisplayHomeAsUpEnabled(true);
        imageView = (android.widget.ImageView) findViewById(R.id.iv);
        db = new ACDatabase(this);

        Cursor debtor = db.getReg("17");
        if (debtor.moveToFirst()) {
            def_Debtor = debtor.getString(0);
        }

        arPayment = new AC_Class.ARPayment();
        binding.setAR(arPayment);

        db = new ACDatabase(this);
        handler = new MyClickHandler(this);
        binding.setHandler(handler);

        Cursor debtor2 = db.getReg("4");
        if(debtor2.moveToFirst()){
            user = debtor2.getString(0);
        }



//        if(CollectionDetails.equals("true")){
//            binding.invheaderBtnNext.setVisibility(View.VISIBLE);
//            binding.invheaderBtnConfirm.setVisibility(View.GONE);
//        }

        getCurrentDataForEdit();
        binding.invheaderDate.clearFocus();
        binding.invheaderDebtorcode.clearFocus();


        Bitmap bitmap2 = db.getARImage(arPayment.getDocNo());
        if (bitmap2 != null) {
            binding.iv.setImageBitmap(bitmap2);
            bitmap = bitmap2;
        }
        binding.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
//                startActivityForResult(Intent.createChooser(intent, "Pick an image"), 1);

                final List<String> orderType = new ArrayList<>();
                orderType.add("Take Photo");
                orderType.add("Pick an image from gallery");

                final CharSequence[] ttArray = orderType.toArray(new CharSequence[orderType.size()]);

                AlertDialog.Builder ttOptions = new AlertDialog.Builder(ARPayment.this);
                ttOptions.setTitle("Select an action:");
                ttOptions.setItems(ttArray, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String option = orderType.get(which);
                        if (option.equals("Take Photo")) {
                            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                            } else {
//                                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                                ImagePicker.with(ARPayment.this)
                                        .cameraOnly()	    			//Crop image(Optional), Check Customization for more option//Final image size will be less than 1 MB(Optional)
                                        .maxResultSize(1080, 1080) //Final image resolution will be less than 1080 x 1080(Optional)
                                        .compress(1024)
                                        .start();
                            }
                        } else {
//                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                            intent.setType("image/jpeg");
//                            startActivityForResult(Intent.createChooser(intent, "Pick an image"), 1);
                            ImagePicker.with(ARPayment.this)
                                    .galleryOnly()	    			//Crop image(Optional), Check Customization for more option
                                    .maxResultSize(1080, 1080)
                                    .compress(1024)			//Final image size will be less than 1 MB(Optional)
                                    .start();
                        }
                        dialog.dismiss();
                    }
                });
                ttOptions.show();
            }
        });

        // Broadcast Receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_LOGOUT");

        exitReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                unregisterReceiver(this);
                finish();
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        {
            registerReceiver(exitReceiver, intentFilter, RECEIVER_NOT_EXPORTED);
        }
        else {
            registerReceiver(exitReceiver, intentFilter);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    public void onResume() {

        super.onResume();
    }
    @Override
    public void onBackPressed() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ARPayment.super.onBackPressed();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 2:
                if (data != null) {
                    AC_Class.Debtor debtor = data.getParcelableExtra("DebtorsKey");
                    if (debtor != null) {
                        arPayment.setDebtorCode(debtor.getAccNo());
                        arPayment.setDebtorName(debtor.getCompanyName());
                    }
                }
                break;

        }

        if (resultCode == RESULT_OK && requestCode == 1) {
            imageView = (android.widget.ImageView) findViewById(R.id.iv);

            try {
                String type = getContentResolver().getType(data.getData());
                System.out.println(type);
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (resultCode == -1) {
            imageView = (android.widget.ImageView) findViewById(R.id.iv);

            String type = getContentResolver().getType(data.getData());
            System.out.println(type);
            InputStream inputStream = null;
            try {
                inputStream = getContentResolver().openInputStream(data.getData());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            bitmap = BitmapFactory.decodeStream(inputStream);
            imageView.setImageBitmap(bitmap);
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {

            imageView = (android.widget.ImageView) findViewById(R.id.iv);
            bitmap = (Bitmap) data.getExtras().get("data");

            imageView.setImageBitmap(bitmap);
        }

        if (resultCode ==  1) {
            arPayment = data.getParcelableExtra("iPackingList");
            binding.amount.setText(String.format(Locale.getDefault(),
                    " %.2f", arPayment.getAmount()));
        }
    }

    public void getCurrentDataForEdit() {
        Cursor data;
        ActionBar actionBar = getSupportActionBar();
        docNo = getIntent().getStringExtra("DocNoKey");
        Func = getIntent().getStringExtra("FunctionKey");
        switch (Func) {
            case "New":
                // "Add invoice" title
                actionBar.setTitle("Add Collection");

                arPayment.setDocNo(db.getNextARNo());

                // Insert datetime
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());
                arPayment.setDate(currentDateandTime);

                if (!def_Debtor.equals("None")) {
                    arPayment.setDebtorCode(def_Debtor);
                    AC_Class.Invoice myInvoice = db.getDebtorInfo(def_Debtor);
                    if (myInvoice != null) {
                        arPayment.setDebtorName(myInvoice.getDebtorName());
                    }
                }
                arPayment.setCreatedUser(user);
                arPayment.setLastModifiedUser(user);


                break;

            case "Edit":
                // "Edit invoice" title
                actionBar.setTitle("Edit Collection");

                data = db.getARHeaderPrint(docNo);
                if (data.getCount() > 0) {
                    data.moveToNext();
                    arPayment = new AC_Class.ARPayment(data.getString(data.getColumnIndex("DocNo")),
                            data.getString(data.getColumnIndex("Date")), data.getString(data.getColumnIndex("DebtorCode")),
                            data.getString(data.getColumnIndex("DebtorName")),
                            data.getDouble(data.getColumnIndex("Amount")),
                            data.getInt(data.getColumnIndex("Uploaded")),data.getString(data.getColumnIndex("CreatedTimeStamp")),
                            data.getString(data.getColumnIndex("CreatedUser")), data.getString(data.getColumnIndex("Remark")));
                    data = db.getARDetailtoUpdate(docNo);
                    while (data.moveToNext()) {
                        AC_Class.ARPaymentDtl myDtil = new AC_Class.ARPaymentDtl(
                                data.getString(data.getColumnIndex("DocNo")),
                                data.getString(data.getColumnIndex("DocNumber")),
                                data.getString(data.getColumnIndex("DocDate")),
                                data.getDouble(data.getColumnIndex("PaymentAmount")),
                                data.getDouble(data.getColumnIndex("OrgAmt")));
                        arPayment.addARPaymentDtl(myDtil);
                    }
                    arPayment.setLastModifiedUser(user);
                    binding.setAR(arPayment);
                }
        }
    }

    public class MyClickHandler {
        Context context;

        public MyClickHandler(Context context) {
            this.context = context;
        }

        public void OnDebtorTxtViewClicked(View view) {
            Intent new_intent = new Intent(ARPayment.this, DebtorList.class);

            startActivityForResult(new_intent, 2);

        }

        public void OnAmountTxtViewClicked(View view) {
            binding.amount.requestFocus();
            binding.amount.setSelectAllOnFocus(true);
        }

        public void OnImageButtonClicked(View view) {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            mDataSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    month = month + 1;
                    String monthString = String.valueOf(month);
                    String dateString = String.valueOf(dayOfMonth);

                    if (monthString.length() == 1) {
                        monthString = "0" + monthString;
                    }
                    if (dateString.length() == 1) {
                        dateString = "0" + dateString;
                    }
                    String date = dateString + "/" + monthString + "/" + year;
                    arPayment.setDate(date);
                }
            };

            DatePickerDialog datePickerDialog = new DatePickerDialog(ARPayment.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDataSetListener, year, month, day);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        }


        public void OnNextButtonClicked(View view) {
            if (TextUtils.isEmpty(binding.invheaderDate.getText().toString())) {
                binding.invheaderDate.requestFocus();
                binding.invheaderDate.setError("This field cannot be blank!", null);
                return;
            }

            if (TextUtils.isEmpty(binding.invheaderDebtorcode.getText().toString())) {
                binding.invheaderDebtorcode.requestFocus();
                binding.invheaderDebtorcode.setError("This field cannot be blank!", null);
                return;
            } else if (!TextUtils.isEmpty(binding.invheaderDebtorcode.getText().toString())) {
                binding.invheaderDebtorcode.setError(null);
                binding.invheaderDebtorcode.clearFocus();
            }


            if (TextUtils.isEmpty(binding.amount.getText().toString())) {
                binding.amount.requestFocus();
                binding.amount.setError("This field cannot be blank!", null);
                return;
            } else if (!TextUtils.isEmpty(binding.amount.getText().toString())) {
                binding.amount.setError(null);
                binding.amount.clearFocus();
                arPayment.setAmount(Double.parseDouble(binding.amount.getText().toString()));
                if (arPayment.getAmount() == 0) {
                    binding.amount.requestFocus();
                    binding.amount.setError("This field cannot be 0!", null);
                    return;
                }
            }
            final AlertDialog.Builder builder = new AlertDialog.Builder(ARPayment.this);
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setMessage("Do you want to knock off invoice?");

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CollectionDetails = "false";
                                if (bitmap != null) {
                new saveImage(ARPayment.this).execute();
            }else {
                SaveData();
                Intent new_intent = new Intent(ARPayment.this, ARMultipleTab.class);
                new_intent.putExtra("DataFromARHeader", arPayment);
                //new_intent.putExtra("FunctionKey", key);
                new_intent.putExtra("arDoc", arPayment.getDocNo());
                startActivityForResult(new_intent, 1);
            }
                }
            });

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CollectionDetails = "true";
                    if (bitmap != null) {
                        new saveImage(ARPayment.this).execute();
                    }else {

                        SharedPreferences sharedPreferences = getSharedPreferences("CollectionImage", MODE_PRIVATE);
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                        myEdit.putString("image", null);
                        myEdit.commit();
                        String key = getIntent().getStringExtra("FunctionKey");
                        //SaveData();
                        Intent new_intent = new Intent(ARPayment.this, ARPaymentDtlList.class);
                        new_intent.putExtra("DataFromARHeader", arPayment);
                        new_intent.putExtra("FunctionKey", key);
                        new_intent.putExtra("arDoc", arPayment.getDocNo());
                        startActivityForResult(new_intent, 1);
                    }
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        }

//        public void OnConfirmButtonClicked(View view) {
//            if (TextUtils.isEmpty(binding.invheaderDate.getText().toString())) {
//                binding.invheaderDate.requestFocus();
//                binding.invheaderDate.setError("This field cannot be blank!", null);
//                return;
//            }
//
//            if (TextUtils.isEmpty(binding.invheaderDebtorcode.getText().toString())) {
//                binding.invheaderDebtorcode.requestFocus();
//                binding.invheaderDebtorcode.setError("This field cannot be blank!", null);
//                return;
//            } else if (!TextUtils.isEmpty(binding.invheaderDebtorcode.getText().toString())) {
//                binding.invheaderDebtorcode.setError(null);
//                binding.invheaderDebtorcode.clearFocus();
//            }
//
//
//            if (TextUtils.isEmpty(binding.amount.getText().toString())) {
//                binding.amount.requestFocus();
//                binding.amount.setError("This field cannot be blank!", null);
//                return;
//            } else if (!TextUtils.isEmpty(binding.amount.getText().toString())) {
//                binding.amount.setError(null);
//                binding.amount.clearFocus();
//                arPayment.setAmount(Double.parseDouble(binding.amount.getText().toString()));
//                if (arPayment.getAmount() == 0) {
//                    binding.amount.requestFocus();
//                    binding.amount.setError("This field cannot be 0!", null);
//                    return;
//                }
//            }
//
//
//            if (bitmap != null) {
//                new saveImage(ARPayment.this).execute();
//            }else {
//                SaveData();
//                Intent new_intent = new Intent(ARPayment.this, ARMultipleTab.class);
//                new_intent.putExtra("DataFromARHeader", arPayment);
//                //new_intent.putExtra("FunctionKey", key);
//                new_intent.putExtra("arDoc", arPayment.getDocNo());
//                startActivityForResult(new_intent, 1);
//            }
//
//        }
    }


    public byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, stream);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        byte[] byteFormat = stream.toByteArray();
        return byteFormat;
    }


    class saveImage extends AsyncTask<Void, Void, byte[]> {
        Activity context;
        ProgressDialog pd;

        saveImage(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setMessage("Save Image...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected byte[] doInBackground(Void... params) {

            int MAX_IMAGE_SIZE = 2000 * 1024;
            int streamLength = MAX_IMAGE_SIZE;
            int compressQuality = 105;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            if (streamLength >= MAX_IMAGE_SIZE && compressQuality > 5) {
                while (streamLength >= MAX_IMAGE_SIZE && compressQuality > 5) {
                    try {
                        byteArrayOutputStream.flush();//to avoid out of memory error
                        byteArrayOutputStream.reset();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    compressQuality -= 5;
                    if (streamLength <= MAX_IMAGE_SIZE) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, compressQuality, byteArrayOutputStream);
                    } else {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, byteArrayOutputStream);
                    }
                    img = byteArrayOutputStream.toByteArray();
                    streamLength = img.length;
                    if (BuildConfig.DEBUG) {
                        Log.d("test upload", "Quality: " + compressQuality);
                        Log.d("test upload", "Size: " + streamLength);
                    }

                }
            }
            return img;
        }

        @Override
        protected void onPostExecute(byte[] s) {
            //super.onPostExecute(s);

            encodedImage = Base64.encodeToString(img, Base64.DEFAULT);
            arPayment.setImage(encodedImage);

            if(CollectionDetails.equals("true")){
                SharedPreferences sharedPreferences = getSharedPreferences("CollectionImage", MODE_PRIVATE);
                if(encodedImage!=null) {
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();

                    myEdit.putString("image", encodedImage);

                    myEdit.commit();
                }else{
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();

                    myEdit.putString("image", null);

                    myEdit.commit();
                }

                String key = getIntent().getStringExtra("FunctionKey");
                //SaveData();
                Intent new_intent = new Intent(ARPayment.this, ARPaymentDtlList.class);
                arPayment.setImage("");
                new_intent.putExtra("DataFromARHeader", arPayment);
                new_intent.putExtra("FunctionKey", key);
                new_intent.putExtra("arDoc", arPayment.getDocNo());
                startActivityForResult(new_intent, 1);
            }else{
                SaveData();
                Intent new_intent = new Intent(ARPayment.this, ARMultipleTab.class);
                arPayment.setImage("");
                new_intent.putExtra("DataFromARHeader", arPayment);
                //new_intent.putExtra("FunctionKey", key);
                new_intent.putExtra("arDoc", arPayment.getDocNo());
                startActivityForResult(new_intent, 1);
            }
           // SaveData();
//            Intent new_intent = new Intent(ARPayment.this, ARMultipleTab.class);
//            new_intent.putExtra("arDoc", arPayment.getDocNo());
//            startActivityForResult(new_intent, 1);

            if (pd.isShowing())
                pd.dismiss();

        }
    }

    public void SaveData(){

        if (!Func.equals("Edit")) {
            arPayment.setDocNo(db.getNextARNo());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
            String date = sdf.format(new Date());
            arPayment.setCreatedTimeStamp(date);
        }
        // Broadcast intent to close other activities
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.package.ACTION_LOGOUT");
        sendBroadcast(broadcastIntent);


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        String date = sdf.format(new Date());
        arPayment.setLastModifiedDateTime(date);

        db.UpdateARDetail(arPayment);
        db.insertAR(arPayment);

        if (arPayment.getDocNo().equals(db.getNextNoAR())) {
            db.IncrementAutoNumbering("AR");
        }

    }

}
