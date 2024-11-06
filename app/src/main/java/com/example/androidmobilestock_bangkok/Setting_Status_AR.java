package com.example.androidmobilestock_bangkok;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.androidmobilestock_bangkok.databinding.SettingActivityStatusPlBinding;

import java.util.ArrayList;
import java.util.List;

public class Setting_Status_AR extends AppCompatActivity {
    SettingActivityStatusPlBinding binding;
    List<AC_Class.ARPayment> myList = new ArrayList<>();
    List<AC_Class.ARPayment> filteredList = new ArrayList<>();
    ACDatabase db;
    ARPaymentAdapter arrayAdapter;
    String Default_curr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.setting_activity_status_pl);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Collection Status");

        db = new ACDatabase(Setting_Status_AR.this);

        Cursor dcurren = db.getReg("6");
        if(dcurren.moveToFirst()){
            Default_curr = dcurren.getString(0);
        }

        arrayAdapter = new ARPaymentAdapter(this,
                myList, Default_curr);
        binding.lvList.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
        getData();

        binding.lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AC_Class.ARPayment currItem = (AC_Class.ARPayment) parent.getItemAtPosition(position);
                ARPaymentAdapter adapter = (ARPaymentAdapter) binding.lvList.getAdapter();

                if (adapter.getSelectedItems().contains(currItem)) {
                    filteredList.remove(currItem);
                    adapter.deselectItem(currItem);
                } else {
                    filteredList.add(currItem);
                    adapter.selectItem(currItem);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reset_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.resetBttn:
                // Quick and Dirty
                final List<String> options = new ArrayList<>();
                options.add("True");
                options.add("False");
                final CharSequence[] cs = options.toArray(new CharSequence[2]);
                AlertDialog.Builder builder = new AlertDialog.Builder(Setting_Status_AR.this);
                builder.setTitle("Set Upload Status to:");
                builder.setItems(cs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int state=0;
                        switch (cs[which].toString()){
                            case "True":
                                state = 1;
                                break;
                            case "False":
                                state = 0;
                                break;
                        }
                        for (AC_Class.ARPayment currInv : filteredList) {
                            View currChild = binding.lvList
                                    .getChildAt(myList.indexOf(currInv));
//                            currChild.setBackgroundColor(getBaseContext()
//                                    .getResources().getColor(R.color.MintCream));
                            db.setARUploadedTo(currInv.getDocNo(), state);
                        }
                        // Update Available data
                        getData();

                        // Notify list changed
                        arrayAdapter.notifyDataSetChanged();
                        AlertDialog.Builder countNote = new AlertDialog.Builder(Setting_Status_AR.this);
                        countNote.setMessage("Changed "+filteredList.size()+" packing list upload status(es).");
                        countNote.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                filteredList.clear();
                                dialog.dismiss();
                            }
                        });
                        countNote.show();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }
        return false;
    }

    public void getData(){
        Cursor data = db.getARPaymentMenuDescLike("");
        myList.clear();
        while (data.moveToNext()){
            AC_Class.ARPayment arPayment = new AC_Class.ARPayment(data.getString(data.getColumnIndex("DocNo")),data.getString(data.getColumnIndex("Date")), data.getString(data.getColumnIndex("DebtorCode")),data.getString(data.getColumnIndex("DebtorName")),data.getDouble(data.getColumnIndex("Amount")),data.getInt(data.getColumnIndex("Uploaded")),data.getString(data.getColumnIndex("CreatedTimeStamp")),
                    data.getString(data.getColumnIndex("CreatedUser")), data.getString(data.getColumnIndex("Remark")));
            myList.add(arPayment);
            }
        arrayAdapter.notifyDataSetChanged();

    }

    public class MyClickHandler
    {
        Context context;

        public MyClickHandler(Context context){
            this.context = context;
        }

    }
}
