package com.example.androidmobilestock;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidmobilestock.Settings.UploadDownload;
import com.example.androidmobilestock.activity.UHFWriteActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.tasks.Task;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Menu#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Menu extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    GridView img_gridview;
    GridViewAdapter gridViewAdapter;
    NavigationView navigationView;
    FragmentContainerView fragmentContainerView;
    BottomNavigationView bottomnav;
    ArrayList<ImageModel> imageModelArrayList;
    ACDatabase db;
    TextView txtURL;
    TextView txtID;
    String URL;
    String URLStr;
    String id;
    Button btnUpdate;
    private int[] myImageList = new int[]{};
    private String[] myImageNameList = new String[]{};
    View v;

    int EnableSetting = 0;
    int Sales = 0;
    int Purchase = 0;
    int Transfer = 0;
    int PackingList = 0;
    int Cart = 0;
    int Analytics= 0;
    int AR = 0;
    int STASSEMBLY = 0;
    int Stock = 0;
    int PurchasePackingList = 0;
    int StockInquiry = 0;
    int JobSheet = 0;
    int StockReceive = 0;
    TextView cover;
    TextView date;
    AppUpdateManager appUpdateManager;
    Task<AppUpdateInfo> appUpdateInfoTask;

    android.widget.ImageView imageView;
    String user;

    ArrayList<String> myModules = new ArrayList<String>();
    ArrayList<String> activate = new ArrayList<String>();

    public Menu() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Menu.
     */
    // TODO: Rename and change types and number of parameters
    public static Menu newInstance(String param1, String param2) {
        Menu fragment = new Menu();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_menu, container, false);
        db = new ACDatabase(getActivity());
        Button btnUpdate = (Button) v.findViewById(R.id.btnUpdate);
        cover = v.findViewById(R.id.ttlsales);
        date = v.findViewById(R.id.date);
        btnUpdate.setVisibility(View.GONE);
        txtID = v.findViewById(R.id.welcome);

        imageView = v.findViewById(R.id.imageView1);
        Cursor ch = db.getReg("35");
        if (ch.moveToFirst()) {
            byte[] bt = ch.getBlob(0);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bt, 0, bt.length);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }

        Intent intent = getActivity().getIntent();
        URL = intent.getStringExtra("URLKey");
        URLStr = intent.getStringExtra("URLStr");
        id = intent.getStringExtra("id");
        if(id!=null){
            txtID.setText("Welcome " + id);
        }

        if (URL != null) {
            txtURL = (TextView) v.findViewById(R.id.nav_URL);
            txtURL.setText("Server: " + URLStr);
            db.updateREG("2", URL);
            db.updateREG("3", URLStr);

        } else {
            Cursor url1 = db.getReg("2");
            if (url1.moveToFirst()) {
                URL = url1.getString(0);
            }

            Cursor urls = db.getReg("3");
            if (urls.moveToFirst()) {
                URLStr = url1.getString(0);
            }
        }


        resetIconsList();
        //  getDataSales("");
        getModules();
        // getAllModules();

        Cursor es = db.getReg("9");
        if (es.moveToFirst()) {
            EnableSetting = es.getInt(0);
        }
        Cursor sale = db.getReg("10");
        if (sale.moveToFirst()) {
            Sales = sale.getInt(0);
        }
        Cursor tran = db.getReg("11");
        if (tran.moveToFirst()) {
            Transfer = tran.getInt(0);
        }
        Cursor pur = db.getReg("12");
        if (pur.moveToFirst()) {
            Purchase = pur.getInt(0);
        }

        Cursor pack = db.getReg("19");
        if (pack.moveToFirst()) {
            PackingList = pack.getInt(0);
        }

        Cursor cart = db.getReg("36");
        if (cart.moveToFirst()) {
            Cart = cart.getInt(0);
        }

        Cursor an = db.getReg("45");
        if (an.moveToFirst()) {
            Analytics = an.getInt(0);
        }

        Cursor arpayment = db.getReg("46");
        if (arpayment.moveToFirst()) {
            AR = arpayment.getInt(0);
        }

        Cursor stockassembly = db.getReg("47");
        if (stockassembly.moveToFirst()) {
            STASSEMBLY = stockassembly.getInt(0);
        }

        Cursor ppack = db.getReg("50");
        if (ppack.moveToFirst()) {
            PurchasePackingList = ppack.getInt(0);
        }

        Cursor us = db.getReg("4");
        if (us.moveToFirst()) {
            user = us.getString(0);
        }

        Cursor st = db.getUsersST(user);
        if (st.moveToFirst()) {
            Stock = st.getInt(1);
        }

        Cursor si = db.getReg("62");
        if (si.moveToFirst()) {
            StockInquiry = si.getInt(0);
        }

        Cursor js = db.getReg("70");
        if (js.moveToNext()){
            JobSheet = js.getInt(0);
        }

        Cursor sr = db.getReg("71");
        if (sr.moveToNext()){
            StockReceive = sr.getInt(0);
        }

        img_gridview = (GridView) v.findViewById(R.id.main_gridview);
        imageModelArrayList = populateList();
        gridViewAdapter = new GridViewAdapter(getActivity().getApplicationContext(), imageModelArrayList);
        img_gridview.setAdapter(gridViewAdapter);
        img_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                switch (imageModelArrayList.get(position).getName()) {
                    case "Stock Count": //Stock Count
                        if (myModules.contains("STOCKTAKE")) {
                            if (Stock == 1) {
                                final Intent intent = new Intent(getActivity(),
                                        StockCountHome.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getActivity(), "Unauthorized access. Please refer to administrator.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Module not activated.", Toast.LENGTH_LONG).show();
                        }
                        break;

                    case "Stock Inquiry":  //Stock Inquiry
                        Intent intent1 = new Intent(getActivity(), StockInquiry.class);
                        startActivity(intent1);
                        break;

                    case "Customer":  //Customer
                        if (myModules.contains("SALES")) {
                            if (Sales == 1) {
                                Intent intent_customer = new Intent(getActivity(), DebtorList.class);
                                intent_customer.putExtra("isView", true);
                                startActivity(intent_customer);
                            } else {
                                Toast.makeText(getActivity(), "Unauthorized access. Please refer to administrator.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Module not activated.", Toast.LENGTH_LONG).show();
                        }
                        break;

                    case "Sales":  //Sales
                        if (myModules.contains("SALES")) {
                            if (Sales == 1) {
                                Intent intent2 = new Intent(getActivity(), InvoiceList.class);
                                startActivity(intent2);
                            } else {
                                Toast.makeText(getActivity(), "Unauthorized access. Please refer to administrator.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Module not activated.", Toast.LENGTH_LONG).show();
                        }
                        break;

                    case "Packing List":  //Packing List
                        if (myModules.contains("PACKING")) {
                            if (PackingList == 1) {
                                Intent intent2 = new Intent(getActivity(), PL_PLList.class);
                                startActivity(intent2);
                            } else {
                                Toast.makeText(getActivity(), "Unauthorized access. Please refer to administrator.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Module not activated.", Toast.LENGTH_LONG).show();
                        }
                        break;

                    case "Purchase":  //Purchase

                        if (myModules.contains("PURCHASE")) {
                            if (Purchase == 1) {
                                Intent intent2 = new Intent(getActivity(), PUR_PurchaseList.class);
                                startActivity(intent2);
                            } else {
                                Toast.makeText(getActivity(), "Unauthorized access. Please refer to administrator.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Module not activated.", Toast.LENGTH_LONG).show();
                        }
                        break;

                    case "Transfer":  //Transfer
                        if (myModules.contains("TRANSFER")) {
                            if (Transfer == 1) {
                                Intent intent_transfer = new Intent(getActivity(), TransferList.class);
                                startActivity(intent_transfer);
                            } else {
                                Toast.makeText(getActivity(), "Unauthorized access. Please refer to administrator.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Module not activated.", Toast.LENGTH_LONG).show();
                        }
                        break;

                    case "Download":  //Download
                        Intent dlIntent = new Intent(getActivity(), UploadDownload.class);
                        dlIntent.putExtra("mode", "download");
                        startActivity(dlIntent);
                        break;

                    case "Upload":  //Upload
                        Intent ulIntent = new Intent(getActivity(), UploadDownload.class);
                        ulIntent.putExtra("mode", "upload");
                        startActivity(ulIntent);
                        break;

                    case "About":  //About
                        Intent intent3 = new Intent(getActivity(), About.class);
                        startActivity(intent3);
                        break;

                    case "Catalog":  //cart
                        if (myModules.contains("CATALOG")) {
                            if (Cart == 1) {
                                Intent intent6 = new Intent(getActivity(), CartView.class);
                                startActivity(intent6);
                            } else {
                                Toast.makeText(getActivity(), "Unauthorized access. Please refer to administrator.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Module not activated.", Toast.LENGTH_LONG).show();
                        }
                        break;

                    case "Backup":  //backup

                        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
                        builder.setTitle("Message");
                        builder.setMessage("Support Password?");
                        final EditText input = new EditText(getActivity());

                        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        builder.setView(input);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (input.getText().toString().equals("presoftmobile")) {
                                    Intent intent7 = new Intent(getActivity(), BK_Backup.class);
                                    startActivity(intent7);
                                } else {
//                    Toast.makeText(getApplicationContext(), "Incorrect Password", Toast.LENGTH_SHORT);
                                }
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();


                        break;

                    case "Log Out":  //logout
                        logOutDialog();
                        break;

                    case "RFID":
                        Intent rfidwriteIntent = new Intent(getActivity(), UHFWriteActivity.class);
                        rfidwriteIntent.putExtra("mode", 3);
                        startActivity(rfidwriteIntent);
                        break;

                    case "Collection":  //AR Payment
                        if (myModules.contains("ARPAYMENT")) {
                            if (AR == 1) {
                                Intent intent8 = new Intent(getActivity(), ARPaymentList.class);
                                startActivity(intent8);
                            } else {
                                Toast.makeText(getActivity(), "Unauthorized access. Please refer to administrator.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Module not activated.", Toast.LENGTH_LONG).show();
                        }
                        break;

                    case "Statement":  //Statement
                        if (myModules.contains("ARPAYMENT")) {
                            if (AR == 1) {
                                Intent intent9 = new Intent(getActivity(), AgingStatement.class);
                                startActivity(intent9);
                            } else {
                                Toast.makeText(getActivity(), "Unauthorized access. Please refer to administrator.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Module not activated.", Toast.LENGTH_LONG).show();
                        }
                        break;

                    case "Stock Assembly":  //Stock Assembly
                        if (myModules.contains("STOCKASSEMBLY")) {
                            if (STASSEMBLY == 1) {
                                Intent intent10 = new Intent(getActivity(), StockAssemblyList.class);
                                startActivity(intent10);
                            } else {
                                Toast.makeText(getActivity(), "Unauthorized access. Please refer to administrator.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Module not activated.", Toast.LENGTH_LONG).show();
                        }
                        break;

                    case "Analytics":


                        if (myModules.contains("ANALYTICS")) {
                            if (Analytics == 1) {
                                Intent intent11 = new Intent(getActivity(), Analytics.class);
                                startActivity(intent11);
                            } else {
                                Toast.makeText(getActivity(), "Unauthorized access. Please refer to administrator.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Module not activated.", Toast.LENGTH_LONG).show();
                        }

                        break;

                    case "Purchase Receiving":
                        if (myModules.contains("PURCHASEPACKING")) {
                            if (PurchasePackingList == 1) {
                                Intent intent12 = new Intent(getActivity(), PPL_PLList.class);
                                startActivity(intent12);
                            } else {
                                Toast.makeText(getActivity(), "Unauthorized access. Please refer to administrator.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Module not activated.", Toast.LENGTH_LONG).show();
                        }

                        break;

                    case "Job Sheet":
                        if (myModules.contains("JOBSHEET")){
                            if (JobSheet == 1){
                                Intent intent13 = new Intent(getActivity(), Jobsheet.class);
                                startActivity(intent13);
                            } else {
                                Toast.makeText(getActivity(), "Unauthorized access. Please refer to administrator.", Toast.LENGTH_LONG).show();
                            }
                        }
                        break;

                    case "Stock Receive":
                        if (myModules.contains("STOCKRECEIVE")){
                            if (StockReceive == 1){
                                Intent intent14 = new Intent(getActivity(), StockReceive.class);
                                startActivity(intent14);
                            } else {
                                Toast.makeText(getActivity(), "Unauthorized access. Please refer to administrator.", Toast.LENGTH_LONG).show();
                            }
                        }
                        break;


                }
            }
        });

        return v;
    }


    public void resetIconsList() {
        if (getPermission()) {
            myImageList = new int[]{R.drawable.stock_count3, R.drawable.stock_inquiry3, R.drawable.customer3, R.drawable.sales3, R.drawable.packinglist3, R.drawable.purpackinglist, R.drawable.purchase3, R.drawable.transfer3, R.drawable.cart3,R.drawable.arpayment3,R.drawable.statement3, R.drawable.stockassembly, R.drawable.stockreceive_icon, R.drawable.jobsheet_icon , R.drawable.analysis, R.drawable.download3, R.drawable.upload6, R.drawable.backup3, R.drawable.edit, R.drawable.about3, R.drawable.logout3};
            myImageNameList = new String[]{getString(R.string.menu_stock_count), getString(R.string.menu_stock_inquiry), getString(R.string.menu_customer), getString(R.string.menu_invoice), getString(R.string.menu_packinglist), getString(R.string.menu_purpackinglist), getString(R.string.menu_purchase), getString(R.string.menu_transfer), getString(R.string.menu_catalog), getString(R.string.menu_arpayment), getString(R.string.menu_statement), getString(R.string.menu_assembly), getString(R.string.menu_stockreceive), getString(R.string.menu_jobsheet),getString(R.string.menu_analytic), getString(R.string.menu_download), getString(R.string.menu_upload), getString(R.string.menu_backup), getString(R.string.menu_rfid), getString(R.string.menu_about), getString(R.string.menu_exit)};
        } else {
            myImageList = new int[]{R.drawable.stock_count3, R.drawable.stock_inquiry3, R.drawable.customer3, R.drawable.sales3, R.drawable.packinglist3, R.drawable.purpackinglist, R.drawable.purchase3, R.drawable.transfer3, R.drawable.cart3,R.drawable.arpayment3,R.drawable.statement3, R.drawable.stockassembly, R.drawable.stockreceive_icon, R.drawable.jobsheet_icon , R.drawable.analysis, R.drawable.download3, R.drawable.upload6, R.drawable.backup3, R.drawable.about3, R.drawable.logout3};
            myImageNameList = new String[]{getString(R.string.menu_stock_count), getString(R.string.menu_stock_inquiry), getString(R.string.menu_customer), getString(R.string.menu_invoice), getString(R.string.menu_packinglist), getString(R.string.menu_purpackinglist), getString(R.string.menu_purchase), getString(R.string.menu_transfer), getString(R.string.menu_catalog),getString(R.string.menu_arpayment),getString(R.string.menu_statement), getString(R.string.menu_assembly), getString(R.string.menu_stockreceive), getString(R.string.menu_jobsheet),getString(R.string.menu_analytic), getString(R.string.menu_download), getString(R.string.menu_upload), getString(R.string.menu_backup), getString(R.string.menu_about), getString(R.string.menu_exit)};
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

    private ArrayList<ImageModel> populateList() {
        ArrayList<ImageModel> list = new ArrayList<>();

        for (int i = 0; i < myImageNameList.length; i++) {

            if (i == 0) {
                if (Stock == 1) {
                    if (myModules.contains("STOCKTAKE")) {
                        ImageModel imageModel = new ImageModel();
                        imageModel.setName(myImageNameList[i]);
                        imageModel.setImage_drawable(myImageList[i]);
                        list.add(imageModel);
                    }
                }
            }else if (i == 1) {
                if (StockInquiry == 1) {
                    // if (myModules.contains("STOCKINQUIRY")) { //Customer
                    ImageModel imageModel = new ImageModel();
                    imageModel.setName(myImageNameList[i]);
                    imageModel.setImage_drawable(myImageList[i]);
                    list.add(imageModel);
                    //}
                }
            }
            else if (i == 2) {
                if (Sales == 1) {
                    if (myModules.contains("SALES")) { //Customer
                        ImageModel imageModel = new ImageModel();
                        imageModel.setName(myImageNameList[i]);
                        imageModel.setImage_drawable(myImageList[i]);
                        list.add(imageModel);
                    }
                }
            } else if (i == 3) {
                //if i==3, check if the module contains Sales
                if (Sales == 1) {
                    if (myModules.contains("SALES")) { //Sales
                        ImageModel imageModel = new ImageModel();
                        imageModel.setName(myImageNameList[i]);
                        imageModel.setImage_drawable(myImageList[i]);
                        list.add(imageModel);
                    }
                }
            } else if (i == 4) {
                if (PackingList == 1) {
                    if (myModules.contains("PACKING")) { //Packing List
                        ImageModel imageModel = new ImageModel();
                        imageModel.setName(myImageNameList[i]);
                        imageModel.setImage_drawable(myImageList[i]);
                        list.add(imageModel);
                    }
                }
            }else if (i == 5) {
                if (PurchasePackingList == 1) {
                    if (myModules.contains("PURCHASEPACKING")) { //Purchase Packing List
                        ImageModel imageModel = new ImageModel();
                        imageModel.setName(myImageNameList[i]);
                        imageModel.setImage_drawable(myImageList[i]);
                        list.add(imageModel);
                    }
                }
            } else if (i == 6) {
                if (Purchase == 1) {
                    if (myModules.contains("PURCHASE")) { //Purchase
                        ImageModel imageModel = new ImageModel();
                        imageModel.setName(myImageNameList[i]);
                        imageModel.setImage_drawable(myImageList[i]);
                        list.add(imageModel);
                    }
                }
            } else if (i == 7) {
                if (Transfer == 1) {
                    if (myModules.contains("TRANSFER")) { //Transfer
                        ImageModel imageModel = new ImageModel();
                        imageModel.setName(myImageNameList[i]);
                        imageModel.setImage_drawable(myImageList[i]);
                        list.add(imageModel);
                    }
                }
            } else if (i == 8) {
                if (Sales == 1) {
                    if (myModules.contains("SALES")) { //Catalog
                        ImageModel imageModel = new ImageModel();
                        imageModel.setName(myImageNameList[i]);
                        imageModel.setImage_drawable(myImageList[i]);
                        list.add(imageModel);
                    }
                }
            } else if (i == 9) {
                if (AR == 1) {
                    if (myModules.contains("ARPAYMENT")) { //Collection
                        ImageModel imageModel = new ImageModel();
                        imageModel.setName(myImageNameList[i]);
                        imageModel.setImage_drawable(myImageList[i]);
                        list.add(imageModel);
                    }
                }
            }else if (i == 10){
                if (AR == 1) {
                    if (myModules.contains("ARPAYMENT")) { //Statement
                        ImageModel imageModel = new ImageModel();
                        imageModel.setName(myImageNameList[i]);
                        imageModel.setImage_drawable(myImageList[i]);
                        list.add(imageModel);
                    }
                }

            }else if (i == 11){
                if (STASSEMBLY == 1) {
                    if (myModules.contains("STOCKASSEMBLY")) { //Stock Assembly
                        ImageModel imageModel = new ImageModel();
                        imageModel.setName(myImageNameList[i]);
                        imageModel.setImage_drawable(myImageList[i]);
                        list.add(imageModel);
                    }
                }

            }else if (i == 14) {
                if (Analytics == 1) {
                    if (myModules.contains("ANALYTICS")) { //Stock Assembly
                        ImageModel imageModel = new ImageModel();
                        imageModel.setName(myImageNameList[i]);
                        imageModel.setImage_drawable(myImageList[i]);
                        list.add(imageModel);

                    }
                }
            } else if (i == 13) {
                if (JobSheet == 1){
                    if (myModules.contains("JOBSHEET")){
                        ImageModel imageModel = new ImageModel();
                        imageModel.setName(myImageNameList[i]);
                        imageModel.setImage_drawable(myImageList[i]);
                        list.add(imageModel);
                    }
                }
            } else if (i == 12) {
                if (StockReceive == 1){
                    if (myModules.contains("STOCKRECEIVE")){
                        ImageModel imageModel = new ImageModel();
                        imageModel.setName(myImageNameList[i]);
                        imageModel.setImage_drawable(myImageList[i]);
                        list.add(imageModel);
                    }
                }
            }else {
                ImageModel imageModel = new ImageModel();
                imageModel.setName(myImageNameList[i]);
                imageModel.setImage_drawable(myImageList[i]);
                list.add(imageModel);
            }

        }
        return list;
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

//    private void getAllModules() {
//        Cursor data = db.getAllModules();
//        if (data.getCount() > 0) {
//            while (data.moveToNext()) {
//                activate.add(data.getString(data.getColumnIndex("Activate")));
//                ARPayment = Boolean.parseBoolean(activate.get(6));
//            }
//        }
//        data.close();
//    }

    private void logOutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.exit);
        builder.setTitle("Logging Out");
        builder.setMessage("Are you sure you want to log out?");
        builder.setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getActivity().getApplicationContext(), Login.class);
                intent.putExtra("URLKey", URL);
                intent.putExtra("URLStr", URLStr);
                db.close();
                startActivity(intent);
                getActivity().finish();
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

    @Override
    public void onResume() {
        super.onResume();
        resetIconsList();
        //getDataSales("");

        imageModelArrayList = populateList();
        gridViewAdapter = new GridViewAdapter(getActivity().getApplicationContext(), imageModelArrayList);
        img_gridview.setAdapter(gridViewAdapter);
    }

}