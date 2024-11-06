package com.example.androidmobilestock_bangkok;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

public class PUR_EmptyAD extends AppCompatActivity implements PUR_PurchaseDtl_AddManual.OnFragmentInteractionListener {
    String mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = prepareBundle();

        // Action Bar
        ActionBar actionBar = getSupportActionBar();
        try {
            actionBar.setDisplayHomeAsUpEnabled(true);
            switch(mode) {
                case "New":
                    actionBar.setTitle("Add Detail");
                    actionBar.setBackgroundDrawable(new ColorDrawable(Color.RED));
                    break;

                case "Edit":
                    actionBar.setTitle("Edit Detail");
                    actionBar.setBackgroundDrawable(new ColorDrawable(Color.RED));
                    break;
            }
        } catch (Exception e) { Log.i("custDebug", e.getMessage()); }

        setContentView(R.layout.pur_activity_emptyad);
        int mode = getIntent().getIntExtra("iMode", 0);
        if (mode == 0) {
            PUR_PurchaseDtl_AddManual manualFragment = new PUR_PurchaseDtl_AddManual();
            manualFragment.setArguments(bundle);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.frag_layout, manualFragment);
            transaction.commit();
        } else if (mode == 1) {
            PUR_PurchaseDtl_AddBarcode barcodeFragment = new PUR_PurchaseDtl_AddBarcode();
            barcodeFragment.setArguments(bundle);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.frag_layout, barcodeFragment);
            transaction.commit();
        }
    }

    @Override
    public void onBackPressed(){
        Intent newIntent = new Intent();
        setResult(10, newIntent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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

    public Bundle prepareBundle() {
        Bundle bundle = new Bundle();
        mode = getIntent().getStringExtra("iFunc");
        bundle.putString("iFunc", mode);

        int ID = getIntent().getIntExtra("iID", 0);
        bundle.putInt("iID", ID);

        String DocNo = getIntent().getStringExtra("iDocNo");
        bundle.putString("iDocNo", DocNo);

//        if (mode.equals("New")) {
        AC_Class.Purchase doc = getIntent().getParcelableExtra("iDoc");
        bundle.putParcelable("iDoc", doc);

        if (mode.equals("Edit")) {
            AC_Class.PurchaseDetails docDtl = getIntent().getParcelableExtra("iDocDtl");
            bundle.putParcelable("iDocDtl", docDtl);
            Log.i("custDebug", "multTab: "+docDtl.getQuantity());
        }

        return bundle;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {}
}
