package com.example.androidmobilestock_bangkok;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemGroup extends AppCompatActivity {
    private GridView listView,listView2;
    private Button button,reset;
    ACDatabase db;
    ArrayList<String> checkGroup =new ArrayList<>();
    ArrayList<String> checkType =new ArrayList<>();
    int FilterByItemGroup, FilterByItemType;
    String ItemGroupList, ItemTypeList;

    String[] ItemGroup, ItemType;
    ArrayList<String> strItemGroupList,strItemTypeList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_group);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Category");
        actionBar.setDisplayHomeAsUpEnabled(true);

        this.listView = (GridView) findViewById(R.id.listView);
        this.listView2 = (GridView) findViewById(R.id.listView2);
        this.button = (Button)findViewById(R.id.button);
        this.reset = (Button) findViewById(R.id.buttonreset);
        this.listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        this.listView2.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        db = new ACDatabase(this);
        checkGroup = (ArrayList<String>) getIntent().getSerializableExtra("CheckedGroup");
        checkType = (ArrayList<String>) getIntent().getSerializableExtra("CheckedType");

        Cursor fig = db.getReg("58");
        if (fig.moveToFirst()){
            FilterByItemGroup = fig.getInt(0);
        }

        Cursor igl = db.getReg("59");
        if (igl.moveToFirst()){
            ItemGroupList = igl.getString(0).replace("\"","");
        }

        if(ItemGroupList !=null){
            ItemGroup = ItemGroupList.split(",");
            strItemGroupList = new ArrayList<String>(
                    Arrays.asList(ItemGroup));
        }

        Cursor fit = db.getReg("60");
        if (fit.moveToFirst()){
            FilterByItemType = fit.getInt(0);
        }

        Cursor itl = db.getReg("61");
        if (itl.moveToFirst()){
            ItemTypeList = itl.getString(0).replace("\"","");
        }

        if(ItemTypeList !=null){
            ItemType = ItemTypeList.split(",");
            strItemTypeList = new ArrayList<String>(
                    Arrays.asList(ItemType));
        }

        getGroup();
        getType();

        this.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectedItems();
            }
        });

        this.reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(2, intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
//        Intent intent = new Intent();
//        setResult(1, intent);
//        finish();
        SelectedItems();
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

    public void getGroup() {
        List<String> group = null;
        if(FilterByItemGroup == 1){
            group = strItemGroupList;
        }else {
            group = db.getAllGroup();
            for (int i = 0; i < group.size(); i++) {
                if (group.get(i).equals("")) {
                    group.remove(i);
                }
            }
        }
        ArrayAdapter<String> arrayAdapter
                = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked , group);
        //ItemTypeAdapter arrayAdapter = new ItemTypeAdapter(this, item);
        listView.setAdapter(arrayAdapter);
        if(checkGroup!=null) {
            if (checkGroup.size() > 0) {
                for (int i = 0; i < checkGroup.size(); i++) {
                    if (group.contains(checkGroup.get(i))) {
                        listView.setItemChecked(group.indexOf(checkGroup.get(i)), true);
                    }
                }
            }
        }

    }

    public void getType() {
        List<String> type = null;

        if(FilterByItemType == 1){
            type = strItemTypeList;
        }else {
            type = db.getAllType();
            for (int i = 0; i < type.size(); i++) {
                if (type.get(i).equals("")) {
                    type.remove(i);
                }
            }
        }


        ArrayAdapter<String> arrayAdapter2
                = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked , type);

        //ItemTypeAdapter arrayAdapter = new ItemTypeAdapter(this, item);
        listView2.setAdapter(arrayAdapter2);
        if(checkType!=null) {
            if (checkType.size() > 0) {
                for (int i = 0; i < checkType.size(); i++) {
                    if (type.contains(checkType.get(i))) {
                        listView2.setItemChecked(type.indexOf(checkType.get(i)), true);
                    }
                }
            }
        }
    }

    public void SelectedItems()  {

        int len = listView.getCount();

        SparseBooleanArray sp = listView.getCheckedItemPositions();

        StringBuilder sb= new StringBuilder();
        ArrayList<String> s = new ArrayList();


        for(int i=0;i<len;i++){
            if(sp.get(i)){

                //AC_Class.ItemType it =(AC_Class.ItemType) listView.getItemAtPosition(i);
                s.add((listView.getItemAtPosition(i).toString()));

            }
        }

        int len2 = listView2.getCount();

        SparseBooleanArray sp2 = listView2.getCheckedItemPositions();
        ArrayList<String> s2 = new ArrayList();
        for(int i=0;i<len2;i++){
            if(sp2.get(i)){
                //AC_Class.ItemType it =(AC_Class.ItemType) listView.getItemAtPosition(i);
                s2.add((listView2.getItemAtPosition(i).toString()));
            }
        }
        Intent intent = new Intent();
        intent.putExtra("GroupKey", s);
        intent.putExtra("TypeKey", s2);
        setResult(1, intent);
        finish();
    }

}