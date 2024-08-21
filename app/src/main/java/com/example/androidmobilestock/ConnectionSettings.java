package com.example.androidmobilestock;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import com.example.androidmobilestock.databinding.ActivityConnectionSettingsBinding;
import java.util.ArrayList;
import java.util.List;

public class ConnectionSettings extends AppCompatActivity {

    ACDatabase db;
    MyClickHandler handler;
    ActivityConnectionSettingsBinding binding;
    AC_Class.Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_connection_settings);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Connection Setting");
        actionBar.setDisplayHomeAsUpEnabled(true);
        connection = new AC_Class.Connection();
        binding.setConnection(connection);

        db = new ACDatabase(this);

        handler = new MyClickHandler(this);
        binding.setHandler(handler);

        GetData();

        binding.list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String selectedItem = ((AC_Class.Connection)parent.getItemAtPosition(position)).getURL();
                final String selectedItemStr = ((AC_Class.Connection)parent.getItemAtPosition(position)).getURLStr();
                Intent intent1 = new Intent(ConnectionSettings.this, Login.class);
                intent1.putExtra("URLKey", selectedItem);
                intent1.putExtra("URLStr", selectedItemStr);
                setResult(7, intent1);
                //db.resetDb();
                finish();
            }
        });
        binding.list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ConnectionSettings.this);
                builder.setTitle("Attention!");
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setMessage("Do you want to delete the URL?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Integer id = ((AC_Class.Connection)parent.getItemAtPosition(position)).getID();
                        db.deleteURL(String.valueOf(id));
                        GetData();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent1 = new Intent(ConnectionSettings.this, Login.class);
        setResult(0, intent1);
        finish();
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

    public class MyClickHandler {
        Context context;

        public MyClickHandler(Context context) {
            this.context = context;
        }

        public void OnAddBtnClicked(View view) {
            if (TextUtils.isEmpty(binding.txtaddress.getText().toString())) {
                binding.txtaddress.setError("This field cannot be blank!");
                return;
            }

            if (TextUtils.isEmpty(binding.txtPort.getText().toString())) {
                binding.txtPort.setError("This field cannot be blank!");
                return;
            }

            GetNewConnection();
        }
    }

    void GetNewConnection(){
        boolean insert = db.insertData( binding.txtaddress.getText().toString(), binding.txtPort.getText().toString());
        if (insert) {
            Toast.makeText(ConnectionSettings.this, "Insert Successfully", Toast.LENGTH_SHORT).show();
            connection = new AC_Class.Connection();
            binding.setConnection(connection);
            GetData();
        } else {
            Toast.makeText(ConnectionSettings.this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    public void GetData()
    {
        Cursor data = db.getUrl();
        List<AC_Class.Connection> listData = new ArrayList<>();
        if (data.getCount() > 0) {
            while (data.moveToNext()) {
                AC_Class.Connection connection = new AC_Class.Connection(
                        data.getInt(data.getColumnIndex("ID")),
                        data.getString(data.getColumnIndex("AddressURL")), data.getString(data.getColumnIndex("AddressStr")));
                listData.add(connection);
            }
        }
        ConnectionListAdapter adapter = new ConnectionListAdapter(this, listData);
        binding.list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
//        final ListAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
//        binding.list.setAdapter(listAdapter);
    }
}
