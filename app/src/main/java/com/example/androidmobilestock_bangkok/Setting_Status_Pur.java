package com.example.androidmobilestock_bangkok;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import androidx.databinding.DataBindingUtil;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.example.androidmobilestock_bangkok.databinding.SettingActivityStatusPurBinding;

import java.util.ArrayList;
import java.util.List;

public class Setting_Status_Pur extends AppCompatActivity {
    SettingActivityStatusPurBinding binding;
    List<AC_Class.PurchaseMenu> myList = new ArrayList<>();
    List<AC_Class.PurchaseMenu> filteredList = new ArrayList<>();
    ACDatabase db;
    PUR_PurchaseListViewAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.setting_activity_status_pur);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Purchase Status");

        db = new ACDatabase(Setting_Status_Pur.this);

        arrayAdapter = new PUR_PurchaseListViewAdapter(this, myList, "");
        binding.lvList.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
        getData();

        binding.lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AC_Class.PurchaseMenu currItem = (AC_Class.PurchaseMenu) parent.getItemAtPosition(position);
                PUR_PurchaseListViewAdapter adapter = (PUR_PurchaseListViewAdapter) binding.lvList.getAdapter();

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
                AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Setting_Status_Pur.this);
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
                        for (AC_Class.PurchaseMenu currInv : filteredList) {
                            View currChild = binding.lvList
                                    .getChildAt(myList.indexOf(currInv));
//                            currChild.setBackgroundColor(getBaseContext()
//                                    .getResources().getColor(R.color.MintCream));
                            db.setPurchaseUploadedTo(currInv.getDocNo(), state);
                        }
                        // Update Available data
                        getData();

                        // Notify list changed
                        arrayAdapter.notifyDataSetChanged();
                        AlertDialog.Builder countNote = new AlertDialog.Builder(Setting_Status_Pur.this);
                        countNote.setMessage("Changed "+filteredList.size()+" purchase upload status(es).");
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
        Cursor data = db.getPurchaseMenuDescLike("");
        myList.clear();
        while (data.moveToNext()){

            myList.add(new AC_Class.PurchaseMenu(data.getString(0), data.getString(1), data.getString(2), data.getString(3), data.getString(4), data.getDouble(5), data.getDouble(6), data.getDouble(7), data.getInt(8), data.getString(9), data.getString(10), data.getString(11), data.getString(12)));
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
