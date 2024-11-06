package com.example.androidmobilestock_bangkok;

import android.content.Intent;
import android.net.Uri;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.androidmobilestock_bangkok.ui.main.InvADBarcodeFragment;
import com.example.androidmobilestock_bangkok.ui.main.InvADManualFragment;

public class EmptyAD extends AppCompatActivity implements InvADManualFragment.OnFragmentInteractionListener {
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
                    actionBar.setTitle("Add Item to Sales");
                    break;

                case "Edit":
                    actionBar.setTitle("Edit Sales Item");
                    break;
            }
        } catch (Exception e) { Log.i("custDebug", e.getMessage()); }

        setContentView(R.layout.activity_empty_ad);
        int mode = getIntent().getIntExtra("mode", 0);
        if (mode == 0) {
            InvADManualFragment invADManualFragment = new InvADManualFragment();
            invADManualFragment.setArguments(bundle);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.frag_layout, invADManualFragment);
            transaction.commit();
        } else if (mode == 1) {
            InvADBarcodeFragment invADBarcodeFragment = new InvADBarcodeFragment();
            invADBarcodeFragment.setArguments(bundle);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.frag_layout, invADBarcodeFragment);
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
        mode = getIntent().getStringExtra("FunctionKey");
        bundle.putString("FunctionKey", mode);

        int ID = getIntent().getIntExtra("IDKey", 0);
        bundle.putInt("IDKey", ID);

//        String debtorcode = getIntent().getStringExtra("DebtorKey");
//        bundle.putString("DebtorKey", debtorcode);

        String DocNo = getIntent().getStringExtra("DocNoKey");
        bundle.putString("DocNoKey", DocNo);

//        if (mode.equals("New")) {
        AC_Class.Invoice invoice = getIntent().getParcelableExtra("Inv");
        bundle.putParcelable("Inv", invoice);

        AC_Class.Item item = getIntent().getParcelableExtra("Item");
        bundle.putParcelable("Item", item);

        if (mode.equals("Edit")) {
            AC_Class.InvoiceDetails invoiceDetails = getIntent().getParcelableExtra("invoiceDetails");
            bundle.putParcelable("invoiceDetails", invoiceDetails);
            Log.i("custDebug", "multTab: "+invoiceDetails.getQuantity());
        }

        return bundle;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {}
}
