package com.example.androidmobilestock;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

import android.view.MenuItem;

import com.example.androidmobilestock.Settings.SettingsHome;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    NavigationView navigationView;
    ACDatabase db;
    String URL;
    String URLStr;
    private int[] myImageList = new int[]{};
    private String[] myImageNameList = new String[]{};

    int Sales = 0;
    int Purchase = 0;
    int Transfer = 0;
    int PackingList = 0;
    int Cart = 0;
    int EnableSetting = 0;

    android.widget.ImageView imageView;

    ArrayList<String> myModules = new ArrayList<String>();

    ACDatabase dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new ACDatabase(this);
        int dbVersion = dbHelper.getDatabaseVersion();
        Log.d("Database Version", "Current database version: " + dbVersion);

        BottomNavigationView bottomnav = findViewById(R.id.bottomNavigationView);
        bottomnav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new com.example.androidmobilestock.Menu()).commit();

        // Toolbar & Drawer
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new ACDatabase(this);

        Cursor es = db.getReg("9");
        if (es.moveToFirst()) {
            EnableSetting = es.getInt(0);
        }

        Intent intent = getIntent();
        URL = intent.getStringExtra("URLKey");
        URLStr = intent.getStringExtra("URLStr");


        CheckConnection checkConnection = new CheckConnection(MainActivity.this);
        checkConnection.execute(URL);

        getModules();
    }

    private void getModules() {
        Cursor data = db.getModules();
        if (data.getCount() > 0) {
            while (data.moveToNext()) {
                myModules.add(data.getString(data.getColumnIndex("Name")));
            }
        }
        data.close();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check which fragment is currently displayed
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        // Check if the current fragment is the menu fragment
        if (currentFragment instanceof com.example.androidmobilestock.Menu) {
            BottomNavigationView bottomnav = findViewById(R.id.bottomNavigationView);
            bottomnav.setSelectedItemId(R.id.menu);
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            logOutDialog();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_exit) {
            logOutDialog();
        }
        return super.onOptionsItemSelected(item);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Intent intent;
        int id = item.getItemId();

        switch (id) {
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logOutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setIcon(R.drawable.exit);
        builder.setTitle("Logging Out");
        builder.setMessage("Are you sure you want to log out?");
        builder.setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                intent.putExtra("URLKey", URL);
                intent.putExtra("URLStr", URLStr);
                db.close();
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void resetIconsList() {
        if (getPermission()) {
            myImageList = new int[]{R.drawable.stock_count3, R.drawable.stock_inquiry3, R.drawable.customer3, R.drawable.sales3, R.drawable.packinglist3, R.drawable.purchase3, R.drawable.transfer3, R.drawable.cart3, R.drawable.download3, R.drawable.upload6, R.drawable.backup3, R.drawable.set3, R.drawable.edit, R.drawable.about3, R.drawable.logout3};
            myImageNameList = new String[]{getString(R.string.menu_stock_count), getString(R.string.menu_stock_inquiry), getString(R.string.menu_customer), getString(R.string.menu_invoice), getString(R.string.menu_packinglist), getString(R.string.menu_purchase), getString(R.string.menu_transfer), getString(R.string.menu_catalog), getString(R.string.menu_download), getString(R.string.menu_upload), getString(R.string.menu_backup), getString(R.string.menu_setting), getString(R.string.menu_rfid), getString(R.string.menu_about), getString(R.string.menu_exit)};
        } else {
            myImageList = new int[]{R.drawable.stock_count3, R.drawable.stock_inquiry3, R.drawable.customer3, R.drawable.sales3, R.drawable.packinglist3, R.drawable.purchase3, R.drawable.transfer3, R.drawable.cart3, R.drawable.download3, R.drawable.upload6, R.drawable.backup3, R.drawable.set3, R.drawable.about3, R.drawable.logout3};
            myImageNameList = new String[]{getString(R.string.menu_stock_count), getString(R.string.menu_stock_inquiry), getString(R.string.menu_customer), getString(R.string.menu_invoice), getString(R.string.menu_packinglist), getString(R.string.menu_purchase), getString(R.string.menu_transfer), getString(R.string.menu_catalog), getString(R.string.menu_download), getString(R.string.menu_upload), getString(R.string.menu_backup), getString(R.string.menu_setting), getString(R.string.menu_about), getString(R.string.menu_exit)};
        }
    }

    public boolean getPermission() {
        boolean result = false;
        if (android.os.Build.MODEL.equals("HC720")) {
            Cursor data = db.getSelectedPermission("RFID Permission");
            data.moveToNext();

            if (data.getString(0).equals("Granted")) {
                result = true;
            }
        }
        return result;
    }

    class CheckConnection extends AsyncTask<String, Boolean, Boolean> {
        Activity context;

        public CheckConnection(Activity context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(String... connUrl) {
            HttpURLConnection conn;
            boolean status;
            try {
                Log.wtf("URL", connUrl[0]);
                final java.net.URL url = new URL(connUrl[0] + "/test");
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(2000);
                conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
                conn.setRequestMethod("GET");
                InputStream inputStream = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                reader.close();
                status = Boolean.parseBoolean(sb.toString().trim());
                conn.disconnect();
            } catch (Exception ex) {
                Log.i("custDebug", ex.getMessage());
                status = false;
            }
            return status;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            if (bool) {
                Toast.makeText(MainActivity.this, "Connection successful", Toast.LENGTH_LONG).show();

            } else {
//                Snackbar snack =Snackbar.make(findViewById(android.R.id.content), "Connection failed.", Snackbar.LENGTH_SHORT);
//                View view = snack.getView();
//                FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
//                params.gravity = Gravity.TOP;
//                view.setLayoutParams(params);
//                snack.show();

                Toast.makeText(MainActivity.this, "Connection failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void getDataSales(String substring) {
        Cursor data = db.getInvoiceMenuLike(substring);
        List<AC_Class.InvoiceMenu> invoiceMenus = new ArrayList<>();
        //SharedPreferences prefs = getSharedPreferences("com.presoft.androidmobilestock", Context.MODE_PRIVATE);

        String todaydate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        Double sales = 0.0;
        if (data.getCount() > 0) {
//            Log.i("custDebug", "debug");
            while (data.moveToNext()) {
                AC_Class.InvoiceMenu invoiceMenu = new AC_Class.InvoiceMenu(data.getString(0), data.getString(1), data.getString(2), data.getString(3), data.getString(4), data.getDouble(8));
//                Log.i("custDebug", invoiceMenu.getDebtorName()+", "+invoiceMenu.getDocNo());
                if (todaydate.equals(data.getString(1))) {
                    sales += data.getDouble(8);
                }
                invoiceMenus.add(invoiceMenu);
            }
        }

        Cursor cur = db.getReg("6");
        if (cur.moveToFirst()) {
            cur.getString(0);
        }
        TextView cover = findViewById(R.id.ttlsales);
        cover.setText(cur.getString(0) + " " + String.format("%.2f", sales));
        TextView date = findViewById(R.id.date);
        date.setText(todaydate);
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.logout:
                    logOutDialog();
                    break;

                case R.id.settings:

                    if (EnableSetting == 1)
                    {
                        Intent intent4 = new Intent(MainActivity.this, SettingsHome.class);
                        intent4.putExtra("URLKey", URL);
                        intent4.putExtra("URLStr", URLStr);
                        startActivity(intent4);
                    }else {
                        Toast.makeText(MainActivity.this, "Unauthorized access. Please refer to administrator.", Toast.LENGTH_LONG).show();
                    }

                    return true;

                case R.id.menu:
                    selectedFragment = new com.example.androidmobilestock.Menu();
                    break;
            }
if(selectedFragment!=null) {
    getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, selectedFragment).commit();
}

            return true;
        }
    };

}