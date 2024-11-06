package com.example.androidmobilestock_bangkok;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;

import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

public class Jobsheet_AddNew2 extends AppCompatActivity {

    TextView js_docNo, tv_workType, tv_replacementType, tv_timeIn, tv_timeOut, tv_DocDate;
    EditText editText_workType, editText_problem, editText_resolution, editText_remarks;
    ImageButton btn_timeIn, btn_timeOut, btn_DocDate;
    Button btn_Create, btn_Item, btnSignature;
    ImageView ivSign, photo;
    ACDatabase db;
    String func;
    AC_Class.JobSheet jobSheet;
    private static final int REQUEST_SELECT_ITEM = 1;
    ArrayList<AC_Class.JobSheetDetails> itemList;
    private DatePickerDialog.OnDateSetListener mDataSetListener;
    String nDocNo;
    String user;
    Button mGetSign, mClear, mCancel;
    Dialog myDialog;
    Signature mySignature;
    LinearLayout mContent;
    byte[] imgCA;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    Bitmap bitmap;
    byte[] img;
    String encodedImage;
    String CollectionDetails = "false";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobsheet_add_new2);

        db = new ACDatabase(this);

        func = getIntent().getStringExtra("FunctionKey");

        js_docNo = findViewById(R.id.js_docNo);
        tv_workType = findViewById(R.id.tv_workType);
        editText_workType = findViewById(R.id.editText_workType);
        tv_replacementType = findViewById(R.id.tv_replacementType);
        tv_timeIn = findViewById(R.id.tv_timeIn);
        btn_timeIn = findViewById(R.id.btn_timeIn);
        tv_timeOut = findViewById(R.id.tv_timeOut);
        btn_timeOut = findViewById(R.id.btn_timeOut);
        editText_problem = findViewById(R.id.editText_problem);
        editText_resolution = findViewById(R.id.editText_resolution);
        editText_remarks = findViewById(R.id.editText_remarks);
        btn_Create = findViewById(R.id.btn_Create);
        btn_Item = findViewById(R.id.btn_Item);
        tv_DocDate = findViewById(R.id.tv_DocDate);
        btn_DocDate = findViewById(R.id.btn_DocDate);
        btnSignature = findViewById(R.id.btnSignature);
        ivSign = findViewById(R.id.ivSign);
        photo = findViewById(R.id.photo);
        myDialog = new Dialog(Jobsheet_AddNew2.this);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.dialog_signature);
        myDialog.setCancelable(true);
        mContent = (LinearLayout) myDialog.findViewById(R.id.linearLayout);
        mySignature = new Signature(getApplicationContext(), null);
        mySignature.setBackgroundColor(Color.WHITE);
        mContent.addView(mySignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mClear = (Button) myDialog.findViewById(R.id.clear);
        mGetSign = (Button) myDialog.findViewById(R.id.getsign);
        mCancel = (Button) myDialog.findViewById(R.id.cancel);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Cursor debtor2 = db.getReg("4");
        if(debtor2.moveToFirst()){
            user = debtor2.getString(0);
        }

        getFuncToEditData();


        //calendar feature
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
                tv_DocDate.setText(date);
                jobSheet.setDocDate(date);
            }
        };

        btn_DocDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Jobsheet_AddNew2.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDataSetListener, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        tv_DocDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Jobsheet_AddNew2.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDataSetListener, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });



        tv_workType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWorkTypeDialog();
            }
        });

        tv_replacementType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReplacementDialog();
            }
        });

        btn_timeIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimeDialog(true);
            }
        });

        btn_timeOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimeDialog(false);
            }
        });

        editText_problem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                jobSheet.setProblem(editText_problem.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText_resolution.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                jobSheet.setResolution(editText_resolution.getText().toString());
            }
        });

        editText_remarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                jobSheet.setJobSheetRemarks(editText_remarks.getText().toString());
            }
        });

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<String> orderType = new ArrayList<>();
                orderType.add("Take Photo");
                orderType.add("Pick an image from gallery");

                final CharSequence[] ttArray = orderType.toArray(new CharSequence[orderType.size()]);

                AlertDialog.Builder ttOptions = new AlertDialog.Builder(Jobsheet_AddNew2.this);
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

                                ImagePicker.with(Jobsheet_AddNew2.this)
                                        .cameraOnly()	    			//Crop image(Optional), Check Customization for more option//Final image size will be less than 1 MB(Optional)
                                        .maxResultSize(1080, 1080) //Final image resolution will be less than 1080 x 1080(Optional)
                                        .compress(1024)
                                        .start();
                            }
                        } else {

                            ImagePicker.with(Jobsheet_AddNew2.this)
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

        btn_Item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isConverted = db.isJobSheetConverted(jobSheet.getDocNo());

                if (isConverted) {
                    new AlertDialog.Builder(Jobsheet_AddNew2.this)
                            .setMessage("You cannot add items to this job sheet anymore as it has already been converted to an invoice.")
                            .setPositiveButton("OK", null)
                            .show();

                } else {
                    if (func.equals("New")){
                        Intent intent = new Intent(Jobsheet_AddNew2.this, Jobsheet_AddNewItem.class);
                        intent.putParcelableArrayListExtra("itemList", itemList);
                        intent.putExtra("JobSheet", jobSheet);
                        intent.putExtra("FunctionKey", "New");
                        startActivityForResult(intent, REQUEST_SELECT_ITEM);
                    } else if (func.equals("Edit")) {
                        Intent intent = new Intent(Jobsheet_AddNew2.this, Jobsheet_AddNewItem.class);
                        intent.putParcelableArrayListExtra("itemList", itemList);
                        intent.putExtra("JobSheet", jobSheet);
                        intent.putExtra("FunctionKey", "Edit");
                        startActivityForResult(intent, REQUEST_SELECT_ITEM);

                    }

                }

            }
        });

        btnSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasImage(ivSign)) {
                    dialog_action();
                } else {
                    dialog_recreate();
                }
            }
        });

        btn_Create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (areFieldsValid()) {
                    String message = func.equals("New") ?
                            "Are you sure you want to save this Job Sheet?" :
                            "Are you sure you want to update this Job Sheet?";

                    new AlertDialog.Builder(Jobsheet_AddNew2.this)
                            .setTitle("Confirmation")
                            .setMessage(message)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (bitmap != null) {
                                        new saveImage(Jobsheet_AddNew2.this).execute();
                                    } else {
                                        saveJSToDatabase();
                                    }

                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                } else {
                    new AlertDialog.Builder(Jobsheet_AddNew2.this)
                            .setTitle("Error")
                            .setMessage("Please fill in the necessary fields.")
                            .setPositiveButton("OK", null)
                            .show();
                }
            }
        });


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
            jobSheet.setImage(encodedImage);

            if (pd.isShowing())
                pd.dismiss();

            saveJSToDatabase();

        }
    }

    private void saveJSToDatabase() {
        db.beginTransaction();
        try {
            if (func.equals("New")) {

                // Save job sheet
                boolean isJSInserted = db.insertJobSheet(jobSheet);
                if (!isJSInserted) {
                    Toast.makeText(Jobsheet_AddNew2.this, "Failed to insert JobSheet to database", Toast.LENGTH_SHORT).show();
                    db.endTransaction();
                    return;
                }

                // Save job sheet details
                if (itemList.size() > 0) {
                    boolean isJSDInserted = db.insertJobSheetDetails(itemList);
                    if (!isJSDInserted) {
                        Toast.makeText(Jobsheet_AddNew2.this, "Failed to insert JobSheetDetails to database", Toast.LENGTH_SHORT).show();
                        db.endTransaction();
                        return;
                    }
                }

                db.setTransactionSuccessful();
                Toast.makeText(Jobsheet_AddNew2.this, "JobSheet Added Successfully", Toast.LENGTH_SHORT).show();

                if (jobSheet.getDocNo().equals(db.getJSNextDocNo())) {
                    db.IncrementAutoNumbering("JS");
                }

                Intent new_intent = new Intent(Jobsheet_AddNew2.this, Jobsheet_Details.class);
                new_intent.putExtra("docNo", jobSheet.getDocNo());
                new_intent.putExtra("debtorCode", jobSheet.getDebtorCode());
                startActivity(new_intent);
                finish();
            } else {
                boolean dltJSDetail = db.deleteJobSheetDetails(nDocNo);
                boolean dltJS = db.deleteJobSheet(nDocNo);

                if (dltJS) {
                    // Set last modified time and user
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());
                    jobSheet.setLastModifiedDateTime(currentDateandTime);
                    jobSheet.setLastModifiedUser(user);

                    for (AC_Class.JobSheetDetails item : itemList) {
                        item.setDocDate(jobSheet.getLastModifiedDateTime());
                    }

                    // Save job sheet
                    boolean isJSInserted = db.insertJobSheet(jobSheet);
                    if (!isJSInserted) {
                        Toast.makeText(Jobsheet_AddNew2.this, "Failed to update JobSheet to database", Toast.LENGTH_SHORT).show();
                        db.endTransaction();
                        return;
                    }

                    // Save job sheet details
                    if (itemList.size() > 0) {
                        boolean isJSDInserted = db.insertJobSheetDetails(itemList);
                        if (!isJSDInserted) {
                            Toast.makeText(Jobsheet_AddNew2.this, "Failed to update JobSheetDetails to database", Toast.LENGTH_SHORT).show();
                            db.endTransaction();
                            return;
                        }
                    }

                    db.setTransactionSuccessful();
                    Toast.makeText(Jobsheet_AddNew2.this, "JobSheet Updated Successfully", Toast.LENGTH_SHORT).show();

                    Intent new_intent = new Intent(Jobsheet_AddNew2.this, Jobsheet_Details.class);
                    new_intent.putExtra("docNo", jobSheet.getDocNo());
                    new_intent.putExtra("debtorCode", jobSheet.getDebtorCode());
                    startActivity(new_intent);
                    finish();

                } else {
                    Toast.makeText(Jobsheet_AddNew2.this, "Delete failed. Please try again.", Toast.LENGTH_SHORT).show();
                    Log.d("JS Add New2", "Jobsheet delete failed.");
                }
            }
        } finally {
            db.endTransaction();
        }
    }

    public class Signature extends View {

        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();
        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public Signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void clear() {
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            mGetSign.setEnabled(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;
                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;
            return true;
        }

        private void debug(String string) {

            Log.v("log_tag", string);

        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }

    private boolean hasImage(android.widget.ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable) drawable).getBitmap() != null;
        }

        return hasImage;
    }

    public void dialog_action() {
        mGetSign.setEnabled(false);
        mClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mySignature.clear();
                mySignature.setBackgroundColor(Color.WHITE);
                mContent.destroyDrawingCache();
                mGetSign.setEnabled(false);
            }
        });
        mGetSign.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    mContent.setDrawingCacheEnabled(true);
                    myDialog.findViewById(R.id.linearLayout).buildDrawingCache();
                    myDialog.dismiss();
                    Bitmap bitmap1 = myDialog.findViewById(R.id.linearLayout).getDrawingCache();
                    ivSign.setImageBitmap(bitmap1);
                    ivSign.setVisibility(View.VISIBLE);

                    Bitmap bitmapC = Bitmap.createScaledBitmap(bitmap1, 200, 200, false);
                    imgCA = bitmapToByte(bitmapC);
                    jobSheet.setSignature(Base64.encodeToString(imgCA, 0, imgCA.length, Base64.DEFAULT));
                } catch (Exception e) {
                    final String msg = e.toString();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.show();
    }

    public void dialog_recreate() {
        ivSign.setImageDrawable(null);
        ivSign.setImageBitmap(null);
        myDialog.dismiss();
    }

    public byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteFormat = stream.toByteArray();
        return byteFormat;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle result from item selection
        if (requestCode == REQUEST_SELECT_ITEM) {
            if (resultCode == RESULT_OK && data != null) {
                itemList = data.getParcelableArrayListExtra("itemList");
                String subTotal = data.getStringExtra("subTotal");
                String discount = data.getStringExtra("discount");
                String taxValue = data.getStringExtra("taxValue");
                String totalIn = data.getStringExtra("totalIn");

                if (itemList != null) {
                    for (AC_Class.JobSheetDetails item : itemList) {
                        item.setDocDate(jobSheet.getDocDate());
                        btn_Item.setText("Add Item(" + itemList.size() + ")");
                        jobSheet.setTaxType(item.getTaxType());

                        // Handling discount
                        if (discount == null || discount.isEmpty()) {
                            jobSheet.setDiscount(0.00);
                        } else {
                            try {
                                jobSheet.setDiscount(Double.valueOf(discount));
                            } catch (NumberFormatException e) {
                                jobSheet.setDiscount(0.00);
                                Log.e("Jobsheet_AddNew2", "Invalid discount value: " + discount, e);
                            }
                        }

                        // Handling taxValue
                        if (taxValue == null || taxValue.isEmpty()) {
                            jobSheet.setTotalTax(0.00);
                        } else {
                            try {
                                jobSheet.setTotalTax(Double.valueOf(taxValue));
                            } catch (NumberFormatException e) {
                                jobSheet.setTotalTax(0.00);
                                Log.e("Jobsheet_AddNew2", "Invalid tax value: " + taxValue, e);
                            }
                        }

                        // Handling subTotal
                        if (subTotal == null || subTotal.isEmpty()) {
                            jobSheet.setTotalEx(0.00);
                        } else {
                            try {
                                jobSheet.setTotalEx(Double.valueOf(subTotal));
                            } catch (NumberFormatException e) {
                                jobSheet.setTotalEx(0.00);
                                Log.e("Jobsheet_AddNew2", "Invalid sub total value: " + subTotal, e);
                            }
                        }

                        // Handling totalIn
                        if (totalIn == null || totalIn.isEmpty()) {
                            jobSheet.setTotalIn(0.00);
                        } else {
                            try {
                                jobSheet.setTotalIn(Double.valueOf(totalIn));
                            } catch (NumberFormatException e) {
                                jobSheet.setTotalIn(0.00);
                                Log.e("Jobsheet_AddNew2", "Invalid total in value: " + totalIn, e);
                            }
                        }
                    }
                }
            } else {
                Log.d("Job Sheet", "Error getting back");
            }
        }

        // Handle result from photo taking or gallery selection
        else if (requestCode == CAMERA_REQUEST || requestCode == ImagePicker.REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                try {
                    // For camera
                    if (requestCode == CAMERA_REQUEST) {
                        bitmap = (Bitmap) data.getExtras().get("data");
                        photo.setImageBitmap(bitmap);
                    }
                    // For gallery
                    else {
                        InputStream inputStream = getContentResolver().openInputStream(data.getData());
                        bitmap = BitmapFactory.decodeStream(inputStream);
                        photo.setImageBitmap(bitmap);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d("Job Sheet", "Unhandled request code: " + requestCode);
        }
    }



    private boolean areFieldsValid() {
        if (tv_workType.getText().toString().trim().isEmpty()) {
            tv_workType.setError("This field cannot be empty");
            return false;
        }
        if (tv_replacementType.getText().toString().trim().isEmpty()) {
            tv_replacementType.setError("This field cannot be empty");
            return false;
        }
        return true;
    }

    private void showWorkTypeDialog() {
        final String[] workTypes = {"Service Complaint", "Installation", "Others"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(workTypes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedType = workTypes[which];
                tv_workType.setText(selectedType);
                jobSheet.setWorkType(selectedType);

                if (selectedType.equals("Others")){
                    editText_workType.setVisibility(View.VISIBLE);
                    editText_workType.requestFocus();
                    editText_workType.addTextChangedListener(workTypeTextWatcher);
                } else {
                    editText_workType.setVisibility(View.GONE);
                    editText_workType.removeTextChangedListener(workTypeTextWatcher);
                }
            }
        });

        builder.create().show();
    }

    private TextWatcher workTypeTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            jobSheet.setWorkType("Others: " + editText_workType.getText().toString());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private void showReplacementDialog() {
        final String[] replacementTypes = {"Warranty", "Service"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(replacementTypes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tv_replacementType.setText(replacementTypes[which]);
                jobSheet.setReplacementType(tv_replacementType.getText().toString());
                jobSheet.setReplacementType(tv_replacementType.getText().toString());
            }
        });

        builder.create().show();
    }

    private void openTimeDialog(final boolean isTimeIn) {
        String timeText = isTimeIn ? tv_timeIn.getText().toString() : tv_timeOut.getText().toString();

        int hour, minute;
        if (timeText.isEmpty()) {
            //default current time
            Calendar calendar = Calendar.getInstance();
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
        } else {
            String[] timeParts = timeText.split(":");
            hour = Integer.parseInt(timeParts[0]);
            minute = Integer.parseInt(timeParts[1]);
        }

        TimePickerDialog picker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (isTimeIn) {
                    tv_timeIn.setText(String.format("%02d:%02d", hourOfDay, minute));
                    jobSheet.setTimeIn(tv_timeIn.getText().toString());
                } else {
                    tv_timeOut.setText(String.format("%02d:%02d", hourOfDay, minute));
                    jobSheet.setTimeOut(tv_timeOut.getText().toString());
                }
            }
        }, hour, minute, true);

        picker.show();
    }


    private void getFuncToEditData() {
        ActionBar actionBar = getSupportActionBar();
        switch (func){
            case "New":
                actionBar.setTitle("New Job Sheet");
                jobSheet = getIntent().getParcelableExtra("JobSheet");

                // Insert datetime
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());
                tv_DocDate.setText(currentDateandTime);

                js_docNo.setText(jobSheet.getDocNo());
                jobSheet.setDocNo(js_docNo.getText().toString());
                jobSheet.setDocDate(currentDateandTime);

                //get current time
                Calendar calendar = Calendar.getInstance();
                int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                // Format the time to 24-hour format
                String formattedTime = String.format("%02d:%02d", hourOfDay, minute);
                jobSheet.setTimeIn(formattedTime);
                tv_timeIn.setText(formattedTime);
                jobSheet.setTimeOut(formattedTime);
                tv_timeOut.setText(formattedTime);

                itemList = new ArrayList<>();

                break;

            case "Edit":
                actionBar.setTitle("Edit Job Sheet");
                nDocNo = getIntent().getStringExtra("docNo");

                jobSheet = db.getJobSheetOByDocNo(nDocNo);
                if (jobSheet != null) {
                    js_docNo.setText(jobSheet.getDocNo());
                    tv_DocDate.setText(jobSheet.getDocDate());
                    tv_workType.setText(jobSheet.getWorkType());
                    tv_replacementType.setText(jobSheet.getReplacementType());
                    tv_timeIn.setText(jobSheet.getTimeIn());
                    tv_timeOut.setText(jobSheet.getTimeOut());
                    editText_problem.setText(jobSheet.getProblem());
                    editText_resolution.setText(jobSheet.getResolution());
                    editText_remarks.setText(jobSheet.getJobSheetRemarks());

                    String encodedImage = jobSheet.getImage();
                    if (encodedImage != null && !encodedImage.isEmpty()) {
                        byte[] decodedImage = Base64.decode(encodedImage, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);

                        photo.setImageBitmap(bitmap);
                    } else {
                        photo.setImageResource(R.drawable.uploadimage);
                    }

                    String encodedSignature = jobSheet.getSignature();
                    if (encodedSignature != null && !encodedSignature.isEmpty()) {
                        byte[] decodedSignature = Base64.decode(encodedSignature, Base64.DEFAULT);
                        Bitmap signatureBitmap = BitmapFactory.decodeByteArray(decodedSignature, 0, decodedSignature.length);

                        // Set signature bitmap to your ImageView or SignatureView
                        ivSign.setImageBitmap(signatureBitmap);
                    } else {
                     
                    }


                } else {
                    Log.i("custDebug", "No records found with docNo: " + nDocNo);
                }

                int recordCount = db.getJSRecordsByDocNo(nDocNo);
                if (recordCount == 0){
                    btn_Item.setText("Add Item");
                }else {
                    btn_Item.setText("Add Item(" + recordCount + ")");
                }

                btn_Create.setText("Update Job Sheet");


                // Step 2: Create ArrayList to hold JobSheetDetails objects
                itemList = new ArrayList<>();

                // Step 3: Iterate through the Cursor to retrieve data and create objects
                Cursor cursor1 = db.getJobSheetDetailsByDocNo(nDocNo);
                if (cursor1 != null && cursor1.moveToFirst()){
                    do {
                        AC_Class.JobSheetDetails detail = new AC_Class.JobSheetDetails();
                        detail.DocNo = cursor1.getString(cursor1.getColumnIndex("DocNo"));
                        detail.DocDate = cursor1.getString(cursor1.getColumnIndex("DocDate"));
                        detail.Location = cursor1.getString(cursor1.getColumnIndex("Location"));
                        detail.ItemCode = cursor1.getString(cursor1.getColumnIndex("ItemCode"));
                        detail.ItemDescription = cursor1.getString(cursor1.getColumnIndex("ItemDescription"));
                        detail.UOM = cursor1.getString(cursor1.getColumnIndex("UOM"));
                        detail.Quantity = cursor1.getDouble(cursor1.getColumnIndex("Quantity"));
                        detail.UPrice = cursor1.getDouble(cursor1.getColumnIndex("UPrice"));
                        detail.Discount = cursor1.getDouble(cursor1.getColumnIndex("Discount"));
                        detail.SubTotal = cursor1.getDouble(cursor1.getColumnIndex("SubTotal"));
                        detail.TaxType = cursor1.getString(cursor1.getColumnIndex("TaxType"));
                        detail.TaxValue = cursor1.getDouble(cursor1.getColumnIndex("TaxValue"));
                        detail.Total_Ex = cursor1.getDouble(cursor1.getColumnIndex("TotalEx"));
                        detail.Total_In = cursor1.getDouble(cursor1.getColumnIndex("TotalIn"));
                        detail.Remarks = cursor1.getString(cursor1.getColumnIndex("Remarks"));
                        detail.Remarks2 = cursor1.getString(cursor1.getColumnIndex("Remarks2"));
                        itemList.add(detail);
                    } while (cursor1.moveToNext());
                    cursor1.close();
                }


                break;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            {
                onBackPressed();
                return true;
            }
        }
        return false;
    }
    @Override
    public void onBackPressed() {
        Jobsheet_AddNew2.super.onBackPressed();
    }
}