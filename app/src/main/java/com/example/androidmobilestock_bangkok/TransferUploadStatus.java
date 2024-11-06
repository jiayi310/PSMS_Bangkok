package com.example.androidmobilestock_bangkok;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import androidx.databinding.DataBindingUtil;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.example.androidmobilestock_bangkok.databinding.ActivityTransferUploadStatusBinding;

import java.util.ArrayList;
import java.util.List;

public class TransferUploadStatus extends AppCompatActivity {
    ActivityTransferUploadStatusBinding binding;
    ArrayList<AC_Class.TransferMenu> mylist;
    List<AC_Class.TransferMenu> filteredList;
    ACDatabase db;
    TransferListViewAdapter arrayAdapter;
    TransferUploadStatus.MyClickHandler clickHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_transfer_upload_status);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Transfer Status");
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFFf98b88));

        db = new ACDatabase(TransferUploadStatus.this);

        clickHandler = new MyClickHandler(this);
        binding.setClickHandler(clickHandler);

        mylist = new ArrayList<>();
        arrayAdapter = new TransferListViewAdapter(this, mylist);
        binding.transferMenuList.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
        getData();

        filteredList = new ArrayList<>();
        binding.transferMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AC_Class.TransferMenu currItem = (AC_Class.TransferMenu) parent.getItemAtPosition(position);
                TransferListViewAdapter adapter = (TransferListViewAdapter) binding.transferMenuList.getAdapter();

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
                AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(TransferUploadStatus.this);
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
                        for (AC_Class.TransferMenu currInv : filteredList) {
                            View currChild = binding.transferMenuList
                                    .getChildAt(mylist.indexOf(currInv));
                            currChild.setBackgroundColor(getBaseContext()
                                    .getResources().getColor(R.color.MintCream));
                            db.setTransferUploadedTo(currInv.getDocNo(), state);
                        }
                        // Update Available data
                        getData();

                        // Notify list changed
                        arrayAdapter.notifyDataSetChanged();
                        AlertDialog.Builder countNote = new AlertDialog.Builder(TransferUploadStatus.this);
                        countNote.setMessage("Changed "+filteredList.size()+" transfer upload status(es).");
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
        Cursor data = db.getTransferMenuDescLike("");
        mylist.clear();
        while (data.moveToNext()){

            mylist.add(new AC_Class.TransferMenu(
                    data.getString(0), data.getString(1),
                    data.getString(2), data.getString(3),
                    data.getString(4), data.getFloat(5),
                    data.getInt(6)
            ));
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
