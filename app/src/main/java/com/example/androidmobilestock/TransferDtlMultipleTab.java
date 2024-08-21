package com.example.androidmobilestock;

import androidx.databinding.DataBindingUtil;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.androidmobilestock.databinding.ActivityTransferDtlMultipleTabBinding;

public class TransferDtlMultipleTab extends AppCompatActivity {

    ActivityTransferDtlMultipleTabBinding binding;
    AC_Class.TransferMenu transferMenu = new AC_Class.TransferMenu();
    AC_Class.Transfer transfer = new AC_Class.Transfer();
    String TAG = "custDebug";

    ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_transfer_dtl_multiple_tab);

        transferMenu = getIntent().getParcelableExtra("TransferMenu");

        ActionBar actionBar = getSupportActionBar();
        try {
            actionBar.setTitle(transferMenu.getDocNo()+" Details");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(new ColorDrawable(0xFFf98b88));
        } catch (Exception e) {
            Log.i(TAG, "Transfer Details: "+e.getMessage());}


        binding.setTransferMenu(transferMenu);
        listview = (ListView)binding.lvDetail;

        getData(transferMenu.getDocNo());
    }

    private void getData(String docNo){
        ACDatabase db = new ACDatabase(TransferDtlMultipleTab.this);

        if (docNo != null) {
            transfer = db.getTransfer(docNo);
            TransferDtlAdapter adapter = new TransferDtlAdapter(TransferDtlMultipleTab.this, transfer.getTransferDtlList());
            listview.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}