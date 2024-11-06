package com.example.androidmobilestock_bangkok;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.androidmobilestock_bangkok.databinding.PlActivitySoSearchBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PL_SO_Search extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    PlActivitySoSearchBinding binding;
    MyClickHandler handler;
    ACDatabase db;
    private IntentIntegrator qrScan;

    private AC_Class.DO packingList;
    BroadcastReceiver exitReceiver;
    String plType;
    SOListViewAdapter arrayAdapter;
    List<AC_Class.SOMenu> soMenuList = new ArrayList<>();
    private SOListViewAdapter.RecyclerViewClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.pl_activity_so_search);

        // Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("My Packing List");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFFed820e));

        packingList = new AC_Class.DO();

        handler = new MyClickHandler(this);
        binding.setHandler(handler);
        db = new ACDatabase(this);
        getSOData("");

//            arrayAdapter = new SOListViewAdapter(this, soMenuList);
//            binding.listItem.setAdapter(arrayAdapter);

//            binding.listItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    AC_Class.SOMenu i = (AC_Class.SOMenu) parent.getItemAtPosition(position);
//
//                    Intent intent = new Intent(PL_SO_Search.this, PL_SODtlList.class);
//                    intent.putExtra("mySO", i.DocNo);
//                    startActivityForResult(intent, 0);
//                }
//            });
//
//            binding.listItem.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//                @Override
//                public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
//                                               long id) {
//
//                    final AlertDialog.Builder builder = new AlertDialog.Builder(PL_SO_Search.this);
//                    builder.setIcon(android.R.drawable.ic_dialog_alert);
//                    builder.setMessage("What would you like to do?");
//
//                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                            final AlertDialog.Builder delDialog = new AlertDialog.Builder(PL_SO_Search.this);
//                            delDialog.setTitle("Delete?");
//                            delDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    String DocNo = ((AC_Class.SOMenu) parent.getItemAtPosition(position)).getDocNo();
//                                    boolean delete = db.deleteSOPackingList(DocNo);
//                                    getSOData("");
//                                    if (!delete) {
//                                        Toast.makeText(PL_SO_Search.this, "Delete failed",
//                                                Toast.LENGTH_SHORT).show();
//                                    }
//                                    dialog.dismiss();
//                                }
//                            });
//                            delDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            });
//                            delDialog.show();
//                        }
//                    });
//                    AlertDialog dialog = builder.create();
//                    dialog.show();
//                    return true;
//                }
//            });


        binding.searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                getSOData(s.toString().trim());
            }
        });
        hideSoftKeyboard(binding.getRoot());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.PL_List, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_item_text);
        binding.spinner.setAdapter(adapter);
        binding.spinner.setOnItemSelectedListener(this);

        getData();

        // Broadcast Receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_LOGOUT");

        exitReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent){
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
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
        String pl_type = parent.getItemAtPosition(i).toString();
        hideSoftKeyboard(view);

        if(!pl_type.equals("Download New Packing List")) {
            if (packingList != null) {
                Intent intent = new Intent(PL_SO_Search.this, PL_SO_DownloadList.class);
                intent.putExtra("PLType", pl_type);
                startActivityForResult(intent, 4);
            } else {
                Snackbar.make(view, "No SO selected.", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        return;
    }

    public class MyClickHandler {
        Context context;

        public MyClickHandler(Context context) {
            this.context = context;
        }

        public void OnItemTxtViewClicked(View view) {
            try {
                Intent new_intent = new Intent(PL_SO_Search.this, PL_SOList.class);
                startActivityForResult(new_intent, 4);
            } catch (Exception e) { Log.i("custDebug", "OnItemTxtViewClicked - " + e.getMessage()); }
        }

        public void OnItemCameraClicked(View view) {
            try {
                qrScan = new IntentIntegrator(PL_SO_Search.this);
                qrScan.setPrompt("Scan a Barcode/QRCode");
                qrScan.setCameraId(0);
                qrScan.setBeepEnabled(true);
                //qrScan.setBarcodeImageEnabled(false);
                qrScan.setOrientationLocked(false);
                Intent intent = qrScan.createScanIntent();
                startActivityForResult(intent, IntentIntegrator.REQUEST_CODE);

            } catch (Exception e) { Log.i("custDebug", "OnItemCameraClicked - " + e.getMessage()); }
        }

        public void onSearchBtnClicked(View view)
        {
            hideSoftKeyboard(view);

        }
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
        //Return from item list
        if (requestCode == 4) {
            String mySO = data.getStringExtra("mySO");
            plType = data.getStringExtra("plType");
            if (!mySO.isEmpty()) {
                binding.searchField.setText("");
            }
            else {
                super.onActivityResult(requestCode, resultCode, data);
            }
            binding.spinner.setSelection(0);
        }

        if(requestCode == 0){
            if(resultCode == 5){
                binding.spinner.setSelection(0);
            }else {
                if (packingList != null) {
                    plType = data.getStringExtra("plType");
                    String mySO = data.getStringExtra("mySO");
                    packingList.setDocType(plType);
                    packingList.setFromDocNo(mySO);
                    Intent intent = new Intent(PL_SO_Search.this, PL_PL.class);
                    intent.putExtra("iPackingList", packingList);
                    intent.putExtra("FunctionKey", "New");
                    startActivity(intent);
                } else {
                    // Snackbar.make(View bi, "No SO selected.", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    Toast.makeText(this,"No Packing List Selected",Toast.LENGTH_SHORT).show();
                }
            }
        }

        if(resultCode ==99){
            getSOData("");
        }

        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode,
                    resultCode, data);
            if (result != null) {
                if (result.getContents() == null) {
                    Snackbar.make(findViewById(android.R.id.content), "No scanned data found.", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                } else {
                    binding.searchField.setText(result.getContents());
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(view.getWindowToken()!=null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void getData() {
        packingList.setDocNo(db.getNextPackingListNo());

        // Insert datetime
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        packingList.setDocDate(currentDateandTime);
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

    }

    public void getSOData(String substring) {
        soMenuList.clear();
        Cursor data = db.getSOLike(substring);
        if (data.getCount() > 0){
            while (data.moveToNext()) {
                try {
                    AC_Class.SOMenu so = new AC_Class.SOMenu(data.getString(0), data.getString(1), data.getString(2), data.getString(3), null, data.getString(4), data.getString(5), data.getString(6));
                    soMenuList.add(so);
                } catch (Exception e) { Log.i("custDebug", "error "+e.getMessage()); }
            }
        }

        setOnClickListener();
        binding.listItem.setHasFixedSize(true);
        binding.listItem.setLayoutManager(new LinearLayoutManager(this));

        arrayAdapter = new SOListViewAdapter(this, soMenuList,listener);
        binding.listItem.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
    }

    public void setOnClickListener() {
        listener = new SOListViewAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                AC_Class.SOMenu i = (AC_Class.SOMenu) soMenuList.get(position);

                Intent intent = new Intent(PL_SO_Search.this, PL_SODtlList.class);
                intent.putExtra("mySO", i.DocNo);
                startActivityForResult(intent, 0);
            }

            @Override
            public void onLongClick(View v, int position) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(PL_SO_Search.this);
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setMessage("What would you like to do?");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        final AlertDialog.Builder delDialog = new AlertDialog.Builder(PL_SO_Search.this);
                        delDialog.setTitle("Delete?");
                        delDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String DocNo = soMenuList.get(position).getDocNo();
                                boolean delete = db.deleteSOPackingList(DocNo);
                                getSOData("");
                                if (!delete) {
                                    Toast.makeText(PL_SO_Search.this, "Delete failed",
                                            Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                            }
                        });
                        delDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        delDialog.show();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }

        };
    }
}
