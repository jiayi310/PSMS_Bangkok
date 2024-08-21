package com.example.androidmobilestock;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.androidmobilestock.databinding.SettingActivityStatusPlBinding;

import java.util.ArrayList;
import java.util.List;

public class Setting_Status_PPL extends AppCompatActivity {
    SettingActivityStatusPlBinding binding;
    List<AC_Class.DO> myList = new ArrayList<>();
    List<AC_Class.DO> filteredList = new ArrayList<>();
    ACDatabase db;
    PL_PLListViewAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.setting_activity_status_pl);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Purchase Packing List Status");

        db = new ACDatabase(Setting_Status_PPL.this);

        arrayAdapter = new PL_PLListViewAdapter(this,
                myList);
        binding.lvList.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
        getData();

        binding.lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AC_Class.DO currItem = (AC_Class.DO) parent.getItemAtPosition(position);
                PL_PLListViewAdapter adapter = (PL_PLListViewAdapter) binding.lvList.getAdapter();

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
                AlertDialog.Builder builder = new AlertDialog.Builder(Setting_Status_PPL.this);
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
                        for (AC_Class.DO currInv : filteredList) {
                            View currChild = binding.lvList
                                    .getChildAt(myList.indexOf(currInv));
//                            currChild.setBackgroundColor(getBaseContext()
//                                    .getResources().getColor(R.color.MintCream));
                            db.setPurchasePackingListUploadedTo(currInv.getDocNo(), state);
                        }
                        // Update Available data
                        getData();

                        // Notify list changed
                        arrayAdapter.notifyDataSetChanged();
                        AlertDialog.Builder countNote = new AlertDialog.Builder(Setting_Status_PPL.this);
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
        Cursor data = db.getPIDescLike("");
        myList.clear();
        while (data.moveToNext()){
            boolean value = data.getInt(9) > 0;
            myList.add(new AC_Class.DO(data.getString(0), data.getString(1), data.getString(2), data.getString(3), data.getString(4), data.getString(5), data.getInt(6), data.getString(7), data.getString(8), value));
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
