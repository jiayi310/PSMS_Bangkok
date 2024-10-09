package com.example.androidmobilestock;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidmobilestock.databinding.ActivityCartViewBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CartView extends AppCompatActivity {

    ActivityCartViewBinding binding;
    public CartView.MyClickHandler handler;
    RecyclerView view_listView;
    String Default_curr;
    ArrayList<String>itemgroup = new ArrayList<>();
    ArrayList<String> itemtype = new ArrayList<>();
    List<AC_Class.Item> s_item_new = new ArrayList<>();
    String Func;
    String docNo;
    AC_Class.Invoice invoice;
    ACDatabase db;
    TextView textCartItemCount;
    ArrayList<AC_Class.InvoiceDetails> itemlist = new ArrayList<>();
    private IntentIntegrator qrScan;
    int c = 0;
    List<AC_Class.Item> s_item = new ArrayList<>();
    AC_Class.InvoiceDetails invoiceDetails;
    AC_Class.Item item;
    String substring = "";
    EditText searchEditText;
    Intent pintent;
    Boolean TaxEnabled;
    android.widget.ImageView imageView;
    ArrayAdapter<String> adapter;
    List<AC_Class.Item> c_item = new ArrayList<>();
    int i = 0;
    LinearLayout linearLayout4;
    private CartViewListAdapter.RecyclerViewClickListener listener;

    Boolean isBatchNoEnabled = true;
    float balanceqty = 0f;
    Boolean NegativeInventory = true;
    double qty, t_qty;
    String salesOrderType = "";
    Boolean isSaleBatchEnabled = true;
    String loc;
    int FilterByItemGroup, FilterByItemType;
    String ItemGroupList, ItemTypeList;
    String SQLINCLAUSE = "";
    Boolean isAutoPrice = false;

    TextView tv_noProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart_view);

        // Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Catalog");
        actionBar.setDisplayHomeAsUpEnabled(true);
        searchEditText = (EditText) findViewById(R.id.searchField);
        tv_noProducts = findViewById(R.id.tv_noProducts);


        db = new ACDatabase(this);

        invoiceDetails = new AC_Class.InvoiceDetails();
        item = new AC_Class.Item();
        invoice = new AC_Class.Invoice();

        handler = new MyClickHandler(this);
        binding.setHandler(handler);

        view_listView = (RecyclerView) findViewById(R.id.GridView);
        imageView = findViewById(R.id.ItemImage);

        searchEditText.performClick();

        Cursor cursor7 = db.getReg("39");
        if(cursor7.moveToFirst()){
            isSaleBatchEnabled= Boolean.valueOf(cursor7.getString(0));
        }

        Cursor cursor5 = db.getReg("38");
        if(cursor5.moveToFirst()){
            isBatchNoEnabled = Boolean.valueOf(cursor5.getString(0));
        }

        Cursor cursor4 = db.getReg("22");
        if (cursor4.moveToFirst()) {
            TaxEnabled = Boolean.valueOf(cursor4.getString(0));
        }

        Cursor cursor8 = db.getReg("41");
        if (cursor8.moveToFirst()) {
            salesOrderType = cursor8.getString(0);
        }

        Cursor cursor9 = db.getReg("42");
        if(cursor9.moveToFirst()){
            NegativeInventory = Boolean.valueOf(cursor9.getString(0));
        }

        Cursor cursor10 = db.getReg("7");
        if (cursor10.moveToFirst()) {
            loc = cursor10.getString(0);
        }

        Cursor cursor11 = db.getReg("20");
        if(cursor11.moveToFirst()){
            isAutoPrice = Boolean.valueOf(cursor11.getString(0));
        }

        Cursor fig = db.getReg("58");
        if (fig.moveToFirst()){
            FilterByItemGroup = fig.getInt(0);
        }

        Cursor igl = db.getReg("59");
        if (igl.moveToFirst()){
            ItemGroupList = igl.getString(0).replace("\"","'");
        }

        if(FilterByItemGroup == 1){
            if(ItemGroupList !=null){
                SQLINCLAUSE += " AND (b.ItemGroup IN (" + ItemGroupList + ")";
            }

        }

        if(ItemGroupList !=null){
            String ItemGroupList1 = igl.getString(0).replace("\"","");
            String[] ItemGroup = ItemGroupList1.split(",");
            itemgroup = new ArrayList<String>(
                    Arrays.asList(ItemGroup));
        }

        Cursor fit = db.getReg("60");
        if (fit.moveToFirst()){
            FilterByItemType = fit.getInt(0);
        }

        Cursor itl = db.getReg("61");
        if (itl.moveToFirst()){
            ItemTypeList = itl.getString(0).replace("\"","'");
        }

        if(ItemTypeList !=null){
            String ItemTypeList1 = itl.getString(0).replace("\"","");
            String[] ItemType = ItemTypeList1.split(",");
            itemtype = new ArrayList<String>(
                    Arrays.asList(ItemType));
        }

        if(FilterByItemType == 1){
            if(ItemTypeList !=null){
                if(ItemGroupList !=null && FilterByItemGroup ==1) {
                    SQLINCLAUSE += " AND b.ItemType IN (" + ItemTypeList + "))";
                }else{
                    SQLINCLAUSE += " AND b.ItemType IN (" + ItemTypeList + ")";
                }
            }else{
                SQLINCLAUSE += ")";
            }
        }
        if(FilterByItemGroup == 1 && ItemGroupList !=null && FilterByItemType == 0) {
            SQLINCLAUSE += ")";
        }

        //searchEditText.requestFocus();
        getData("");

        //hideSoftKeyboard(binding.getRoot());
        pintent = getIntent();

        if (pintent != null) {
            docNo = pintent.getStringExtra("DocNoKey");
            Func = pintent.getStringExtra("FunctionKey");
        }

        if (Func != null) {
            getCurrentDataForEdit();

        }

        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                getData(s.toString().trim());
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ctlg_menu, menu);

        // TextView cart_badge = findViewById(R.id.cart_badge);

        //System.out.println(cart_badge.getText());

        final MenuItem menuItem = menu.findItem(R.id.cart);

        View actionView = MenuItemCompat.getActionView(menuItem);
        textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);
        textCartItemCount.setVisibility(View.VISIBLE);
        setupBadge();

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });


        return true;
    }

    private void setupBadge() {

        c = 0;
        for(int i =0;i<itemlist.size();i++){
            Double qty = itemlist.get(i).getQuantity();
            c += qty;
        }
        if (c > 0) {
            textCartItemCount.setVisibility(View.VISIBLE);
            textCartItemCount.setText(String.valueOf(c));
        } else {
            textCartItemCount.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onBackPressed() {
        //        if (CheckEmpty())
        //        {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CartView.super.onBackPressed();
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
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if( id == R.id.user){
            Intent intent_customer = new Intent(CartView.this, DebtorList.class);
            intent_customer.putExtra("isView", false);
            startActivityForResult(intent_customer, 1);
            return true;
        }
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (id == R.id.cart) {
            Intent new_intent = new Intent(CartView.this, CartList.class);
            new_intent.putParcelableArrayListExtra("invoiceDetail", itemlist);
            new_intent.putExtra("invoice", invoice);
            new_intent.putExtra("DocNoKey", docNo);
            if (Func != null) {
                if (Func.equals("Edit")) {
                    new_intent.putExtra("FunctionKey", "Edit");
                }
            }
            startActivityForResult(new_intent, 5);
            return true;
        }
        if (id == R.id.order) {
            Intent new_intent = new Intent(CartView.this, OrderList.class);
            //new_intent.putExtra("FunctionKey", "New");
            startActivityForResult(new_intent, 4);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();


        if (searchEditText == null) {
            if (itemtype == null) {
                if (itemgroup == null) {
                    getData("");
                }
            }
        }

    }

    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
        setupBadge();
    }

    public void getData(String substring) {
        Cursor data = db.getItemLike(substring, 4, SQLINCLAUSE);
        if (data.getCount() > 0) {
            s_item.clear();
            s_item_new.clear();
            while (data.moveToNext()) {
                try {
                    AC_Class.Item item = new AC_Class.Item(data.getString(0), data.getString(1), data.getString(2), data.getString(3), data.getString(4), data.getString(5), data.getString(6), data.getString(7), data.getString(8), data.getString(9), data.getFloat(10), data.getFloat(11), data.getFloat(12), data.getFloat(13), data.getFloat(14), data.getFloat(15), data.getString(16), data.getString(17), data.getFloat(18), data.getFloat(19),data.getFloat(20),data.getString(21));

                    s_item.add(item);




                    if(invoice.getDebtorCode()!=null) {
                        if (isAutoPrice) {
                            Cursor cursor_pc = db.getPriceCategory(invoice.getDebtorCode());
                            if (cursor_pc != null) {
                                if (cursor_pc.moveToFirst()) {
                                    Object myPCObj = cursor_pc.getString(0);

                                    if (myPCObj != null) {
                                        try {
                                            Integer myPC = 0;
                                            myPC = Integer.parseInt(myPCObj.toString());
                                            switch (myPC) {
                                                case 2:
                                                    item.setPrice(item.getPrice2());
                                                    break;
                                                case 3:
                                                    item.setPrice(item.getPrice3());
                                                    break;
                                                case 4:
                                                    item.setPrice(item.getPrice4());
                                                    break;
                                                case 5:
                                                    item.setPrice(item.getPrice5());
                                                    break;
                                                case 6:
                                                    item.setPrice(item.getPrice6());
                                                    break;
                                            }
                                        } catch (NumberFormatException e) {
                                            throw e;
                                        }
                                    }
                                }
                            }
                        }
                    }

                } catch (Exception e) {
                    Log.i("custDebug", "error reading image: " + e.getMessage());
                }


            }

            setOnClickListener();
            view_listView.setHasFixedSize(true);
            int mNoOfColumns = Utility.calculateNoOfColumns(this.getApplicationContext(), 200);
            view_listView.setLayoutManager(new GridLayoutManager(this, mNoOfColumns));
            CartViewListAdapter arrayAdapter = new CartViewListAdapter(this, s_item, listener);
            view_listView.setAdapter(arrayAdapter);

            Cursor dcurren = db.getReg("6");
            if (dcurren.moveToFirst()) {
                Default_curr = dcurren.getString(0);
            }
        }

        if (s_item.size() > 0) {
            tv_noProducts.setVisibility(View.GONE);
        } else {
            tv_noProducts.setVisibility(View.VISIBLE);
        }
    }

    double roundDouble(double x) {
        BigDecimal bd = new BigDecimal(Double.toString(x));
        bd = bd.setScale(3, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }

    private void setOnClickListener() {
        listener = new CartViewListAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {

                String itemcode = ((AC_Class.Item) s_item.get(position)).getItemCode();
                String uom = ((AC_Class.Item) s_item.get(position)).getUOM();
                AC_Class.Item sa = ((AC_Class.Item) s_item.get(position));
                sa.setImage(null);
                //Intent item_intent = new Intent();
                Intent item_intent = new Intent(CartView.this, ItemDetail.class);
                item_intent.putExtra("Item", sa);
                item_intent.putParcelableArrayListExtra("CartList", itemlist);

                setResult(4, item_intent);
                startActivityForResult(item_intent, 3);
            }


            @Override
            public void onImageClick(ImageView v, int position) {
                AC_Class.InvoiceDetails tmp = new AC_Class.InvoiceDetails();
                tmp.setItemCode(((AC_Class.Item) s_item.get(position)).getItemCode());
                tmp.setItemDescription(((AC_Class.Item) s_item.get(position)).getDescription());
                tmp.setUPrice(roundDouble(Double.valueOf(((AC_Class.Item) s_item.get(position)).getPrice())));
                tmp.setUOM(((AC_Class.Item) s_item.get(position)).getUOM());

                if(isBatchNoEnabled && s_item.get(position).getHasBatchNo().equals("true")){
                    if(isSaleBatchEnabled) {
                        getbatchNo(position, tmp);
                    }else{
                        Intent new_intent = new Intent(getApplicationContext(), ItemBatchList.class);
                        new_intent.putExtra("ItemCode", s_item.get(position).getItemCode());
                        new_intent.putExtra("UOM", s_item.get(position).getUOM());
                        new_intent.putExtra("Location", loc);
                        startActivityForResult(new_intent, 9);
                        invoiceDetails = tmp;
                    }
                }else{
                    Calculation(tmp);
                    insertItem(tmp);
                    setupBadge();
                }

                if (Func != null) {
                    if (Func.equals("Edit")) {
                        tmp.setDocNo(invoice.getDocNo());
                    }
                } else {
                    tmp.setDocNo(db.getNextNo());
                }


            }
        };
    }


    public void getGroupdata() {
        s_item.clear();
        s_item_new.clear();

        Cursor data;
//        if(itemgroup.size() == 0){
//            data = db.getAllItemType(itemtype);
//        }else if(itemtype.size() == 0){
//            data = db.getAllItemGroup(itemgroup);
//        }else {
            data = db.getAllItemGroupType(itemgroup, itemtype);
     //   }
//        for(int i =0 ;i<itemgroup.size();i++){
//            Cursor data = db.getAllItemGroup(itemgroup.get(i));
            if (data.getCount() > 0) {
                while (data.moveToNext()) {
                    try {
                        AC_Class.Item item = new AC_Class.Item(data.getString(0), data.getString(1), data.getString(2), data.getString(3), data.getString(4), data.getString(5), data.getString(6), data.getString(7), data.getString(8), data.getString(9), data.getFloat(10), data.getFloat(11), data.getFloat(12), data.getFloat(13), data.getFloat(14), data.getFloat(15), data.getString(16), data.getString(17), data.getFloat(18), data.getFloat(19),data.getFloat(20),data.getString(21));


                        if (item.getImage() == null) {
                            item.setImage(db.getItemImage(item.getItemCode()));
                        }
                        s_item.add(item);


                        if(invoice.getDebtorCode()!=null) {
                            if (isAutoPrice) {
                                Cursor cursor_pc = db.getPriceCategory(invoice.getDebtorCode());
                                if (cursor_pc != null) {
                                    if (cursor_pc.moveToFirst()) {
                                        Object myPCObj = cursor_pc.getString(0);

                                        if (myPCObj != null) {
                                            try {
                                                Integer myPC = 0;
                                                myPC = Integer.parseInt(myPCObj.toString());
                                                switch (myPC) {
                                                    case 2:
                                                        item.setPrice(item.getPrice2());
                                                        break;
                                                    case 3:
                                                        item.setPrice(item.getPrice3());
                                                        break;
                                                    case 4:
                                                        item.setPrice(item.getPrice4());
                                                        break;
                                                    case 5:
                                                        item.setPrice(item.getPrice5());
                                                        break;
                                                    case 6:
                                                        item.setPrice(item.getPrice6());
                                                        break;
                                                }
                                            } catch (NumberFormatException e) {
                                                throw e;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.i("custDebug", "error reading image: " + e.getMessage());
                    }
                }

            }

        if (s_item.size() > 0) {
            tv_noProducts.setVisibility(View.GONE);
        } else {
            tv_noProducts.setVisibility(View.VISIBLE);
        }

        setOnClickListener();
        view_listView.setHasFixedSize(true);
        int mNoOfColumns = Utility.calculateNoOfColumns(this.getApplicationContext(), 180);
        view_listView.setLayoutManager(new GridLayoutManager(this, mNoOfColumns));
        CartViewListAdapter arrayAdapter = new CartViewListAdapter(this, s_item_new, listener);
        view_listView.setAdapter(arrayAdapter);
        Cursor dcurren = db.getReg("6");
        if (dcurren.moveToFirst()) {
            Default_curr = dcurren.getString(0);
        }
    }

    public void sort(){
        if (s_item.size() > 0) {

            for (int i = 0; i < s_item.size(); i++) {
                boolean result = false;

                for (AC_Class.Item myDtl : s_item_new) {
                    if (s_item.get(i).getItemCode().equals(myDtl.getItemCode()) && s_item.get(i).getUOM().equals(myDtl.getUOM())) {
                        result = true;
                    }
                }

                if (!result) {
                    s_item_new.add(s_item.get(i));
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case 1:
                if (resultCode == 2) {
                    AC_Class.Debtor debtor = data.getParcelableExtra("DebtorsKey");
                    if (debtor != null) {
//                        binding.invheaderDebtorname.setVisibility(View.VISIBLE);
                        invoice.setDebtorCode(debtor.getAccNo());
                        invoice.setDebtorName(debtor.getCompanyName());
                        invoice.setTaxType(debtor.getTaxType());
                        invoice.setPhone(debtor.getPhone());
                        invoice.setFax(debtor.getFax());
                        invoice.setAttention(debtor.getAttention());
                        invoice.setAddress1(debtor.getADD1());
                        invoice.setAddress2(debtor.getADD2());
                        invoice.setAddress3(debtor.getADD3());
                        invoice.setAddress4(debtor.getADD4());
                            if (invoice.getAgent() == null) {
                                String debAgent = debtor.getSalesAgent();
                                if (debAgent != null && !debAgent.equals("")) {
                                    invoice.setAgent(debAgent);
                                }
                            }

                        getData("");
                    }

                }
                break;

            case 2:
                if (resultCode == 1) {
                    itemgroup = (ArrayList<String>) data.getSerializableExtra("GroupKey");
                    itemtype = (ArrayList<String>) data.getSerializableExtra("TypeKey");
                    if(itemgroup!=null || itemtype!=null) {
                        if (itemgroup.size() != 0 || itemtype.size() != 0) {
                            getGroupdata();
                        } else {
                            getData("");
                        }

                        sort();
                    }
                }
                if(resultCode == 2){
                    itemtype.clear();
                    itemgroup.clear();
                    getData("");
                }
                break;

            case 3:
                if (resultCode == 3) {
                    AC_Class.InvoiceDetails temp = data.getParcelableExtra("InvoiceDetail");

                    if (Func != null) {
                        if (Func.equals("Edit")) {
                            temp.setDocNo(invoice.getDocNo());
                        }
                    }
                    else {
                        temp.setDocNo(db.getNextNo());
                    }

                    Calculation(temp);

                    itemlist.add(temp);

                }

                break;

            case 5:
                if (resultCode == 0) {
                    itemlist.clear();
                    invoice = new AC_Class.Invoice();
                    getData("");
                }

                break;

            case 9:
                String batchNo = data.getStringExtra("ItemBatchNo");
                invoiceDetails.setBatchNo(batchNo);
                Calculation(invoiceDetails);
                insertItem(invoiceDetails);
                setupBadge();

            case IntentIntegrator.REQUEST_CODE:
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode,
                        resultCode, data);
                if (result != null) {
                    if (result.getContents() == null) {
                        Toast.makeText(this, "No result found.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        searchEditText.setText(result.getContents());
                        getData(result.getContents());
                    }
                } else {
                    super.onActivityResult(requestCode, resultCode, data);
                }
                break;


        }


        if (resultCode == 5) {
            AC_Class.Invoice invoice = data.getParcelableExtra("deleteInvoice");
            invoice.getInvoiceDetailsList();
            itemlist.clear();
            for (int i = 0; i < invoice.getInvoiceDetailsList().size(); i++) {
                itemlist.add(invoice.getInvoiceDetailsList().get(i));
            }
        }

    }

    public void getTypedata() {
        s_item.clear();

    }

    public class MyClickHandler {
        Context context;

        public MyClickHandler(Context context) {
            this.context = context;
        }

        public void OnFilterButtonClicked(View view) {

            Intent new_intent = new Intent(context, ItemGroup.class);

            if(itemgroup!=null){
                if(itemgroup.size()>0) {
                    new_intent.putExtra("CheckedGroup", itemgroup);
                }
            }

            if(itemtype!=null){
                if(itemtype.size()>0) {
                    new_intent.putExtra("CheckedType", itemtype);
                }
            }

            startActivityForResult(new_intent, 2);
        }



        public void OnItemCameraClicked(View view) {
            try {
//                Intent new_intent = new Intent(StockInquiry.this, activity_scanner.class);
//                startActivity(new_intent);
                qrScan = new IntentIntegrator(CartView.this);
                qrScan.setPrompt("Scan a Barcode/QRCode");
                qrScan.setCameraId(0);
                qrScan.setBeepEnabled(true);
                //qrScan.setBarcodeImageEnabled(false);
                qrScan.setOrientationLocked(false);
                Intent intent = qrScan.createScanIntent();
                startActivityForResult(intent, IntentIntegrator.REQUEST_CODE);

            } catch (Exception e) {
                Log.i("custDebug", "OnItemCameraClicked - " + e.getMessage());
            }
        }

    }

    private int checkInvoiceDetailList(AC_Class.InvoiceDetails invoiceDetails) {
        int result = -1;
        qty=0.0;

        for (int i = 0; i < itemlist.size(); i++) {
            AC_Class.InvoiceDetails tempDetail = itemlist.get(i);

            if(!isBatchNoEnabled) {
                if (tempDetail.getItemCode().equals(invoiceDetails.getItemCode()) && tempDetail.getUOM().equals(invoiceDetails.getUOM()) && tempDetail.getUPrice().equals(invoiceDetails.getUPrice())) {
                    qty += tempDetail.getQuantity();
                    result = i;
                }
            }else{
                    if(invoiceDetails.getBatchNo() !=null && tempDetail.getBatchNo()!=null) {

                        if (tempDetail.getItemCode().equals(invoiceDetails.getItemCode()) && tempDetail.getUOM().equals(invoiceDetails.getUOM()) && tempDetail.getUPrice().equals(invoiceDetails.getUPrice()) && tempDetail.getBatchNo().equals(invoiceDetails.getBatchNo())) {
                            qty += tempDetail.getQuantity();
                            result = i;
                        }
                    }else{
                        if (tempDetail.getItemCode().equals(invoiceDetails.getItemCode()) && tempDetail.getUOM().equals(invoiceDetails.getUOM()) && tempDetail.getUPrice().equals(invoiceDetails.getUPrice())) {
                            qty += tempDetail.getQuantity();
                            result = i;
                        }
                    }

            }
        }
        return result;
    }

    public void Calculation(AC_Class.InvoiceDetails tmp) {
        if (TaxEnabled == false) {
            // Inclusive
            tmp.setTotal_In((tmp.getQuantity() * tmp.getUPrice()) - tmp.getDiscount());
            // Calc. tax from total
            tmp.setTaxValue((tmp.getTotal_In() *
                    tmp.getTaxRate()) / (100 + tmp.getTaxRate()));
            tmp.setTotal_Ex(tmp.getTotal_In() - tmp.getTaxValue());
            tmp.setSubTotal((tmp.getTotal_Ex() + tmp.getDiscount()));
            Log.wtf("Subtotal", String.valueOf(tmp.getSubTotal()));

        } else {
            // Exclusive
            tmp.setSubTotal((tmp.getQuantity() * tmp.getUPrice()));
            tmp.setTotal_Ex(tmp.getSubTotal() - tmp.getDiscount());
            tmp.setTaxValue((tmp.getTotal_Ex() * tmp.getTaxRate()) / 100);
            tmp.setTotal_In(tmp.getTotal_Ex() + tmp.getTaxValue());
            Log.wtf("Subtotal", String.valueOf(tmp.getSubTotal()));
        }
    }


    public void getCurrentDataForEdit() {
        Cursor data;
        switch (Func) {

            case "Edit":

                data = db.getInvoiceHeadertoUpdate(docNo);
                if (data.getCount() == 1) {
                    data.moveToNext();
                    invoice = new AC_Class.Invoice(data.getString(1), data.getString(2), data.getString(3), data.getString(4), data.getString(5), data.getString(data.getColumnIndex("SalesAgent")), data.getString(data.getColumnIndex("TaxType")), data.getString(7),data.getString(data.getColumnIndex("Signature")), data.getString(data.getColumnIndex("Phone")), data.getString(data.getColumnIndex("Fax")),
                            data.getString(data.getColumnIndex("Attention")),data.getString(data.getColumnIndex("Address1")),data.getString(data.getColumnIndex("Address2")),data.getString(data.getColumnIndex("Address3")),data.getString(data.getColumnIndex("Address4")), data.getString(data.getColumnIndex("Remarks")), data.getString(data.getColumnIndex("Remarks2")),
                            data.getString(data.getColumnIndex("Remarks3")), data.getString(data.getColumnIndex("Remarks4")), data.getString(data.getColumnIndex("CreatedUser")),data.getString(data.getColumnIndex("CreditTerm")),data.getString(data.getColumnIndex("DetailDiscount")));
                    data = db.getInvoiceDetailtoUpdate(docNo);
                    while (data.moveToNext()) {
                        invoice.addInvoiceDetail(new AC_Class.InvoiceDetails(data.getInt(0),
                                data.getString(1), data.getString(2),
                                data.getString(3), data.getString(4),
                                data.getString(5), data.getDouble(6),
                                data.getDouble(7), data.getDouble(8),
                                data.getDouble(9), data.getString(10),
                                data.getDouble(11), data.getDouble(12),
                                data.getDouble(13), data.getDouble(14),
                                data.getString(15), data.getString(16),
                                data.getString(17),data.getString(18)));
                    }
//                    binding.invheaderDebtorname.setVisibility(View.VISIBLE);
                    for (int i = 0; i < invoice.getInvoiceDetailsList().size(); i++) {
                        itemlist.add(invoice.getInvoiceDetailsList().get(i));
                    }

                }
        }

    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    void insertItem(AC_Class.InvoiceDetails invoiceDetails) {

        int myResult = checkInvoiceDetailList(invoiceDetails);
        t_qty=0.0;
        if(myResult!= -1) {
            if(!isBatchNoEnabled) {
                Cursor temp;
                temp = db.getStockBalance(itemlist.get(myResult).getItemCode(), itemlist.get(myResult).getUOM());
                if (temp.getCount() > 0) {
                    temp.moveToNext();
                    balanceqty = temp.getFloat(0);
                }
            }else{
                if(itemlist.get(myResult).getBatchNo()!=null) {
                    Cursor temp3;
                    temp3 = db.getStockBalanceBatchNo(itemlist.get(myResult).getItemCode(), itemlist.get(myResult).getUOM(), itemlist.get(myResult).getBatchNo());
                    if (temp3.getCount() > 0) {
                        temp3.moveToNext();
                        balanceqty = temp3.getFloat(0);
                    }
                }
            }
            t_qty = itemlist.get(myResult).getQuantity();
        }


        if (myResult >= 0) {
            if(NegativeInventory) {
                itemlist.get(myResult).setQuantity(itemlist.get(myResult).getQuantity() + 1);
            }else{
                if(itemlist.get(myResult).getQuantity() > balanceqty){
                    Toast.makeText(getApplicationContext(), "Out of Stock", Toast.LENGTH_SHORT).show();
                }else{
                    if(t_qty >= balanceqty){
                        Toast.makeText(getApplicationContext(), "Out of Stock", Toast.LENGTH_SHORT).show();
                    }else {
                        itemlist.get(myResult).setQuantity(itemlist.get(myResult).getQuantity() + 1);
                    }
                }
            }
            updateInvDtls(itemlist.get(myResult));
        } else {
            itemlist.add(invoiceDetails);
        }
    }


    void updateInvDtls(AC_Class.InvoiceDetails invoiceDetails) {
        invoiceDetails.setSubTotal((invoiceDetails.getQuantity() * invoiceDetails.getUPrice()));
        invoiceDetails.setTotal_Ex(invoiceDetails.getSubTotal() - invoiceDetails.getDiscount());
        invoiceDetails.setTaxValue((invoiceDetails.getTotal_Ex() * invoiceDetails.getTaxRate()) / 100);
        invoiceDetails.setTotal_In(invoiceDetails.getTotal_Ex() + invoiceDetails.getTaxValue());
    }

    public static class Utility {
        public static int calculateNoOfColumns(Context context, float columnWidthDp) { // For example columnWidthdp=180
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
            int noOfColumns = (int) (screenWidthDp / columnWidthDp + 0.5); // +0.5 for correct rounding to int.
            return noOfColumns;
        }

    }

    public void getbatchNo(int position, AC_Class.InvoiceDetails tmp) {
        Cursor data2 = db.getAllItemBatch(s_item.get(position).getItemCode(), s_item.get(position).getUOM(), loc);
        Cursor data3 = db.getAllItemBatch(s_item.get(position).getItemCode(), s_item.get(position).getUOM(), loc);
        int newManudate = 0;
        int newExpirydate = 0;
        if (data3.moveToFirst()) {
            if(data3.getString(data3.getColumnIndex("ManufacturedDate"))!= null) {
                newManudate = Integer.parseInt(data3.getString(data3.getColumnIndex("ManufacturedDate")).replaceAll("[^0-9]", ""));
            }
            if(data3.getString(data3.getColumnIndex("ExpiryDate")) != null) {
                newExpirydate = Integer.parseInt(data3.getString(data3.getColumnIndex("ExpiryDate")).replaceAll("[^0-9]", ""));
            }
        }
        while (data2.moveToNext()) {
            String s_manudate = data2.getString(data2.getColumnIndex("ManufacturedDate"));
            int manudate = 0, expidate = 0;
            if(s_manudate != null) {
                manudate = Integer.parseInt(s_manudate.replaceAll("[^0-9]", ""));
            }

            String s_expidate = data2.getString(data2.getColumnIndex("ExpiryDate"));
            if(s_expidate !=null) {
                expidate = Integer.parseInt(s_expidate.replaceAll("[^0-9]", ""));
            }

            if (salesOrderType != "0" || salesOrderType != null) {
                if (salesOrderType.equals("Latest Manufacture Date")) {
                    int compare = Integer.compare(manudate, newManudate);
                    if (compare >= 0) {
                        newManudate = manudate;
                        tmp.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));
                    }
                } else if (salesOrderType.equals("Earliest Manufacture Date")) {
                    int compare = Integer.compare(manudate, newManudate);
                    if (compare <= 0) {
                        newManudate = manudate;
                        tmp.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));
                    }
                } else if (salesOrderType.equals("Latest Expiration Date")) {
                    int compare = Integer.compare(expidate, newExpirydate);
                    if (compare >= 0) {
                        newExpirydate = expidate;
                        tmp.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));
                    }
                } else if (salesOrderType.equals("Earliest Expiration Date")) {
                    int compare = Integer.compare(expidate, newExpirydate);
                    if (compare <= 0) {
                        newExpirydate = expidate;
                        tmp.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));
                    }
                }
            } else {
                tmp.setBatchNo(data2.getString(data2.getColumnIndex("BatchNo")));
            }

        }

    }

}