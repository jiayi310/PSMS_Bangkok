package com.example.androidmobilestock_bangkok.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.androidmobilestock_bangkok.ACDatabase;
import com.example.androidmobilestock_bangkok.AC_Class;
import com.example.androidmobilestock_bangkok.Item_List;
import com.example.androidmobilestock_bangkok.R;
import com.example.androidmobilestock_bangkok.StockInquiryMultipleTab;
import com.example.androidmobilestock_bangkok.activity.UHFWriteActivity;
import com.example.androidmobilestock_bangkok.tools.StringUtils;
import com.example.androidmobilestock_bangkok.tools.UIHelper;
import com.google.android.material.snackbar.Snackbar;
import com.rscja.deviceapi.RFIDWithUHF.BankEnum;
import com.rscja.deviceapi.entity.SimpleRFIDEntity;
import com.rscja.utility.StringUtility;

public class UHFWriteFragment extends KeyDwonFragment implements OnClickListener{

    private static final String TAG = "UHFWriteFragment";

    private UHFWriteActivity mContext;

    private RadioGroup radioOperationGroup, radioWriteTypeGroup;
    private RadioButton radioOperationButton, radioWriteTypeButton;

    Spinner SpinnerBank_Write;
    EditText EtPtr_Write;
    EditText EtLen_Write;
    //EditText EtData_Write;
    EditText EtAccessPwd_Write;
    EditText etLen_filter_wt;

    Button BtWrite,BtRead,BtSearch, BtDetail;

    CheckBox cb_filter_wt,cb_QT_W;
    EditText etPtr_filter_wt;
    EditText etData_filter_wt;
    RadioButton rbEPC_filter_wt;
    RadioButton rbTID_filter_wt;
    RadioButton rbUser_filter_wt;
    LinearLayout bankSelectionLayout, filterWrite,valueFilter,passwordFilter;
    TextView tv_item, tv_itemDescription, tv_itemDesc2, tv_itemUOM, tv_scan, tv_itemBarcode;

    protected View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.uhf_write_fragment, container, false);
        mView = view;
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //getActivity().getActionBar().hide();

        mContext = (UHFWriteActivity) getActivity();
        radioOperationGroup = (RadioGroup)getView().findViewById(R.id.radioGroup);
        radioWriteTypeGroup = (RadioGroup)getView().findViewById(R.id.radioGroup2);

        SpinnerBank_Write = (Spinner) getView().findViewById(R.id.SpinnerBank_Write);
        //sp_type = (Spinner) getView().findViewById(R.id.sp_type);

        EtPtr_Write = (EditText) getView().findViewById(R.id.EtPtr_Write);
        EtLen_Write = (EditText) getView().findViewById(R.id.EtLen_Write);
        //EtData_Write = (EditText) getView().findViewById(R.id.EtData_Write);
        EtAccessPwd_Write = (EditText) getView().findViewById(R.id.EtAccessPwd_Write);
        etLen_filter_wt= (EditText) getView().findViewById(R.id.etLen_filter_wt);
        BtWrite = (Button) getView().findViewById(R.id.BtWrite);
        BtRead = (Button) getView().findViewById(R.id.BtRead);
        BtSearch = (Button) getView().findViewById(R.id.BtSearch);
        BtDetail = (Button) getView().findViewById(R.id.BtDetail);

        tv_item = (TextView) getView().findViewById(R.id.tv_item);
        tv_itemUOM = (TextView) getView().findViewById(R.id.tv_itemUOM);
        tv_itemDescription = (TextView) getView().findViewById(R.id.tv_itemDescription);
        tv_itemDesc2 = (TextView) getView().findViewById(R.id.tv_itemDesc2);
        tv_itemBarcode = (TextView) getView().findViewById(R.id.tv_itemBarcode);
        tv_scan = (TextView) getView().findViewById(R.id.tv_scan);

        cb_QT_W= (CheckBox) getView().findViewById(R.id.cb_QT_W);
        cb_filter_wt = (CheckBox) getView().findViewById(R.id.cb_filter_wt);
        etPtr_filter_wt = (EditText) getView().findViewById(R.id.etPtr_filter_wt);
        etData_filter_wt = (EditText) getView().findViewById(R.id.etData_filter_wt);
        rbEPC_filter_wt = (RadioButton) getView().findViewById(R.id.rbEPC_filter_wt);
        rbTID_filter_wt = (RadioButton) getView().findViewById(R.id.rbTID_filter_wt);
        rbUser_filter_wt = (RadioButton) getView().findViewById(R.id.rbUser_filter_wt);

        bankSelectionLayout = (LinearLayout) getView().findViewById(R.id.bankSelectionLayout);
        filterWrite = (LinearLayout) getView().findViewById(R.id.writeFilter);
        valueFilter = (LinearLayout) getView().findViewById(R.id.valueFilter);
        passwordFilter = (LinearLayout) getView().findViewById(R.id.passwordFilter);

        //Deactivate Write Filter
        filterWrite.setVisibility(View.GONE);
        valueFilter.setVisibility(View.GONE);
        passwordFilter.setVisibility(View.GONE);
        cb_QT_W.setVisibility(View.GONE);
        bankSelectionLayout.setVisibility(View.GONE);

        rbEPC_filter_wt.setOnClickListener(this);
        rbTID_filter_wt.setOnClickListener(this);
        rbUser_filter_wt.setOnClickListener(this);
        BtWrite.setOnClickListener(this);
        BtRead.setOnClickListener(this);
        BtSearch.setOnClickListener(this);
        BtDetail.setOnClickListener(this);

        //tv_item.setOnClickListener(this);

        //SET POWER
        mContext.mReader.setPower(8);

        cb_filter_wt.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    String data = etData_filter_wt.getText().toString().trim();
                    String rex = "[\\da-fA-F]*"; //匹配正则表达式，数据为十六进制格式
                    if(data==null || data.isEmpty() || !data.matches(rex)) {
                        UIHelper.ToastMessage(mContext,"过滤的数据必须是十六进制数据");
                        cb_filter_wt.setChecked(false);
                        return;
                    }
                }
            }
        });
        SpinnerBank_Write.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String element = adapterView.getItemAtPosition(i).toString();// 得到spanner的值
                if(element.equals("EPC") || SpinnerBank_Write.getSelectedItemPosition()==0 ){
                    EtPtr_Write.setText("2");
                    EtLen_Write.setText("6");
                }else{
                    EtPtr_Write.setText("0");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void cleardata()
    {
        tv_scan.setText("");
        tv_item.setText("");
        tv_itemUOM.setText("");
        tv_itemDescription.setText("");
        tv_itemDesc2.setText("");
        tv_itemBarcode.setText("");
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.rbEPC_filter_wt) {
            etPtr_filter_wt.setText("32");
        } else if (id == R.id.rbTID_filter_wt) {
            etPtr_filter_wt.setText("0");
        } else if (id == R.id.rbUser_filter_wt) {
            etPtr_filter_wt.setText("0");
        } else if (id == R.id.BtWrite) {
            write();
        }
        else if (id == R.id.BtRead) {
            read();
        }
        else if (id == R.id.BtSearch)
        {
            Intent new_intent = new Intent(view.getContext(), Item_List.class);
            new_intent.putExtra("substring", "");
            startActivityForResult(new_intent, 1);
        }
        else if (id == R.id.BtDetail)
        {
            if (!tv_scan.getText().toString().isEmpty())
            {
                AC_Class.Item myItem = getItemData2(tv_scan.getText().toString());
                if (myItem != null) {
                    Intent intent = new Intent(view.getContext(), StockInquiryMultipleTab.class);
                    intent.putExtra("ItemDetailKey", myItem);
                    startActivity(intent);
                } else {
                    Snackbar.make(view, "No Item selected.", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //Return from item selection
            case 1:
                AC_Class.Item item = data.getParcelableExtra("ItemsKey");
                if (item != null){
                    cleardata();
                    tv_scan.setText(item.getItemCode());
                    tv_item.setText(item.getItemCode());
                    tv_itemDescription.setText(item.getDescription());
                    tv_itemDesc2.setText(item.getDesc2());
                    tv_itemUOM.setText(item.getUOM());
                    tv_itemBarcode.setText(item.getBarCode());
                }
                break;
        }
    }

    public AC_Class.Item getItemData2(String substring) {
        ACDatabase db = new ACDatabase(mContext);
        AC_Class.Item item = new AC_Class.Item();

        Cursor data = db.getItemLike(substring, 3, "");
        if (data.getCount() > 0) {
            if (data.moveToNext()) {
                try {
                    item = new AC_Class.Item();
                    //Decode Image
                    byte[] decodedString;
                    decodedString = Base64.decode(data.getString(11), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0,
                            decodedString.length);
                    item.setItemCode(data.getString(0));
                    item.setDescription(data.getString(1));
                    item.setDesc2(data.getString(2));
                    item.setItemGroup(data.getString(3));
                    item.setItemType(data.getString(4));
                    item.setTaxType(data.getString(5));
                    item.setPurchaseTaxType(data.getString(6));
                    item.setBaseUOM(data.getString(7));
                    item.setPrice(data.getFloat(8));
                    item.setPrice2(data.getFloat(9));
                    item.setBarCode(data.getString(10));
                    item.setImage(decodedByte);
                } catch (Exception e) {
                    Snackbar.make(mView, e.getMessage(), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
            } else {
                Snackbar.make(mView, "Product not found", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        }
        return item;
    }


    private void write(){

     //String strPtr = EtPtr_Write.getText().toString().trim();
    String strPtr = "2";

     if (StringUtils.isEmpty(strPtr)) {
         UIHelper.ToastMessage(mContext, R.string.uhf_msg_addr_not_null);
         return;
     } else if (!StringUtility.isDecimal(strPtr)) {
         UIHelper.ToastMessage(mContext,
                 R.string.uhf_msg_addr_must_decimal);
         return;
     }

     //String strPWD = EtAccessPwd_Write.getText().toString().trim();// 访问密码
    String strPWD = "00000000";

     if (StringUtils.isNotEmpty(strPWD)) {
         if (strPWD.length() != 8) {
             UIHelper.ToastMessage(mContext,
                     R.string.uhf_msg_addr_must_len8);
             return;
         } else if (!mContext.vailHexInput(strPWD)) {
             UIHelper.ToastMessage(mContext,
                     "Please enter value contain only A-F and 1-9.");
             return;
         }
     } else {
         strPWD = "00000000";
     }

     //String strData = EtData_Write.getText().toString().trim();// 要写入的内容
    String strData = "";

    //strData = tv_scan.getText().toString().trim();

        int selectedId = radioWriteTypeGroup.getCheckedRadioButtonId();
        radioWriteTypeButton = (RadioButton) getView().findViewById(selectedId);

        if (radioWriteTypeButton.getText().equals("Barcode"))
        {
            if (!tv_itemBarcode.getText().toString().isEmpty())
            {
                strData = tv_itemBarcode.getText().toString().trim();
            }
        }
        else
        {
            strData = tv_item.getText().toString().trim();
        }

//    if(sp_type.getSelectedItemPosition() == 0)
//    {
//        if (!tv_itemBarcode.getText().toString().isEmpty())
//        {
//            strData = tv_itemBarcode.getText().toString().trim();
//        }
//    }
//    else
//    {
//        strData = tv_scan.getText().toString().trim();
//    }

     if (StringUtils.isEmpty(strData)) {
         UIHelper.ToastMessage(mContext,
                 "Please select a valid item or write data type.");
         return;
     }else if (!mContext.vailHexInput(fillSpaces(strData))) {
         UIHelper.ToastMessage(mContext,
                 "Please enter value contain only A-F and 0-9.");
         return;
     }

     // 多字单次
     //String cntStr = EtLen_Write.getText().toString().trim();
     String cntStr = "6";
     Log.i("Entered Data",cntStr);

     if (StringUtils.isEmpty(cntStr)) {
         UIHelper.ToastMessage(mContext, R.string.uhf_msg_len_not_null);
         return;
     } else if (!StringUtility.isDecimal(cntStr)) {
         UIHelper.ToastMessage(mContext,
                 R.string.uhf_msg_len_must_decimal);
         return;
     }

     //Comment section for check if value entered is hexdecimal format
     /*if ((strData.length()) % 4 != 0) {
         UIHelper.ToastMessage(mContext,
                 R.string.uhf_msg_write_must_len4x);

         return;
     } else if (!mContext.vailHexInput(strData)) {
         UIHelper.ToastMessage(mContext, R.string.rfid_mgs_error_nohex);
         return;
     }*/


     boolean result=false;
     String Bank="UII";

     if (cb_filter_wt.isChecked())// 指定标签
     {
         if(etPtr_filter_wt.getText().toString()==null || etPtr_filter_wt.getText().toString().isEmpty()){
             etPtr_filter_wt.setText("0");
         }
         if(etLen_filter_wt.getText().toString()==null || etLen_filter_wt.getText().toString().isEmpty()){
             UIHelper.ToastMessage(mContext,"过滤数据长度不能为空");
             return;
         }

         int filterPtr=Integer.parseInt(etPtr_filter_wt.getText().toString());
         String filterData=etData_filter_wt.getText().toString();
         int filterCnt=Integer.parseInt(etLen_filter_wt.getText().toString());
         String filterBank="UII";
         if(rbEPC_filter_wt.isChecked()){
             filterBank="UII";
         }else if(rbTID_filter_wt.isChecked()){
             filterBank="TID";
         }else if(rbUser_filter_wt.isChecked()){
             filterBank="USER";
         }

         boolean r=false;
         if(cb_QT_W.isChecked()){
               r =mContext.mReader.writeDataWithQT(strPWD,
                     BankEnum.valueOf(filterBank),
                     filterPtr,
                     filterCnt,
                     filterData,
                     BankEnum.valueOf(Bank),
                     Integer.parseInt(strPtr),
                     Integer.parseInt(cntStr),
                     fillSpaces(strData)
             );
         }else{
            r=mContext.mReader.writeData(strPWD,
                     BankEnum.valueOf(filterBank),
                     filterPtr,
                     filterCnt,
                     filterData,
                     BankEnum.valueOf(Bank),
                     Integer.parseInt(strPtr),
                     Integer.parseInt(cntStr),
                     fillSpaces(strData)
             );
         }

         result= r;

     } else {
         boolean r=false;
         Log.wtf("Bool status", String.valueOf(r));
         if(cb_QT_W.isChecked()){
             Log.wtf("Im in CBQTW checked", "Blyat");
            r= mContext.mReader.writeDataWithQT_Ex(strPWD,
                    BankEnum.valueOf(Bank),
                    Integer.parseInt(strPtr),
                    Integer.valueOf(cntStr),
                    fillSpaces(strData));
             Log.wtf("Bool status", String.valueOf(r));
         }else{
             Log.wtf("Im in CBQTW unchecked", "Blyat");
                     r= mContext.mReader.writeData_Ex(strPWD,
                     BankEnum.valueOf(Bank),
                     Integer.parseInt(strPtr),
                     Integer.valueOf(cntStr), fillSpaces(strData));// 返回的UII
             Log.wtf("Bool status", String.valueOf(r));
         }

         if (r) {
             result=true;
             UIHelper.ToastMessage(mContext, getString(R.string.uhf_msg_write_succ));
         } else {
             UIHelper.ToastMessage(mContext, R.string.uhf_msg_write_fail);
         }
     }

        //EtData_Write.setText("");

     if(!result){
         mContext.playSound(2);
     }else{
         mContext.playSound(1);
     }
 }

    private String fillSpaces(String strData) {
        String result = strData;
        String[] list = {"A","B","C","D","E","F","0","1","2","3","4","5","6","7","8","9"};

        for(int i=0 ; i < list.length ; i++)
        {
            if(!strData.contains(list[i]))
                while( result.length() < 24)
                {
                    result = result + list[i];
                }
        }

        return result;
    }

    private void read() {
        cleardata();

        //String ptrStr = EtPtr_Write.getText().toString().trim();
        String ptrStr = "2";
        if (ptrStr.equals("")) {
            UIHelper.ToastMessage(mContext, R.string.uhf_msg_addr_not_null);
            return;
        } else if (!TextUtils.isDigitsOnly(ptrStr)) {
            UIHelper.ToastMessage(mContext, R.string.uhf_msg_addr_must_decimal);
            return;
        }

        //String cntStr = EtLen_Write.getText().toString().trim();
        String cntStr = "6";
        if (cntStr.equals("")) {
            UIHelper.ToastMessage(mContext, R.string.uhf_msg_len_not_null);
            return;
        } else if (!TextUtils.isDigitsOnly(cntStr)) {
            UIHelper.ToastMessage(mContext, R.string.uhf_msg_len_must_decimal);
            return;
        }

        String pwdStr = EtAccessPwd_Write.getText().toString().trim();
        if (!TextUtils.isEmpty(pwdStr)) {
            if (pwdStr.length() != 8) {
                UIHelper.ToastMessage(mContext, R.string.uhf_msg_addr_must_len8);
                return;
            } else if (!mContext.vailHexInput(pwdStr)) {
                UIHelper.ToastMessage(mContext, R.string.rfid_mgs_error_nohex);
                return;
            }
        } else {
            pwdStr = "00000000";
        }

        boolean result=false;
        String Bank="";
        if(SpinnerBank_Write.getSelectedItemPosition()==0){
            Bank="UII";
        }else{
            Bank=SpinnerBank_Write.getSelectedItem().toString();
        }
        if (cb_filter_wt.isChecked())//  过滤
        {
            if(etPtr_filter_wt.getText().toString()==null || etPtr_filter_wt.getText().toString().isEmpty()){
                UIHelper.ToastMessage(mContext, "过滤数据的起始地址不能为空");
                return;
            }
            if(etData_filter_wt.getText().toString()==null || etData_filter_wt.getText().toString().isEmpty()){
                UIHelper.ToastMessage(mContext, "过滤数据不能为空");
                return;
            }
            if(etLen_filter_wt.getText().toString()==null || etLen_filter_wt.getText().toString().isEmpty()){
                UIHelper.ToastMessage(mContext, "过滤数据长度不能为空");
                return;
            }


            int filterPtr=Integer.parseInt(etPtr_filter_wt.getText().toString());
            String filterData=etData_filter_wt.getText().toString();
            int filterCnt=Integer.parseInt(etLen_filter_wt.getText().toString());
            String filterBank="UII";
            if(rbEPC_filter_wt.isChecked()){
                filterBank="UII";
            }else if(rbTID_filter_wt.isChecked()){
                filterBank="TID";
            }else if(rbUser_filter_wt.isChecked()){
                filterBank="USER";
            }

            String data="";
            Log.wtf("Data String Before", data);
            if(cb_QT_W.isChecked()) {
                data = mContext.mReader.readDataWithQT(pwdStr,
                        BankEnum.valueOf(filterBank),
                        filterPtr,
                        filterCnt,
                        filterData,
                        BankEnum.valueOf(Bank),
                        Integer.parseInt(ptrStr),
                        Integer.parseInt(cntStr)
                );
                Log.wtf("Data String on CBQTW checked", data);
            }else{
                data = mContext.mReader.readData(pwdStr,
                        BankEnum.valueOf(filterBank),
                        filterPtr,
                        filterCnt,
                        filterData,
                        BankEnum.valueOf(Bank),
                        Integer.parseInt(ptrStr),
                        Integer.parseInt(cntStr)
                );
                Log.wtf("Data String on CBQTW unchecked", data);
            }
            result= data != null && data.length() > 0;
            //EtData_Write.setText(data);

        } else {
            SimpleRFIDEntity entity;
            if(cb_QT_W.isChecked()){
                entity= mContext.mReader.readDataWithQT(pwdStr,
                        BankEnum.valueOf(Bank),
                        Integer.parseInt(ptrStr),
                        Integer.parseInt(cntStr));
            }else {
                entity = mContext.mReader.readData(pwdStr,
                        BankEnum.valueOf(Bank),
                        Integer.parseInt(ptrStr),
                        Integer.parseInt(cntStr));
            }
            if (entity != null) {
                result = true;
                String data = checkRedundantPlace(entity.getData());
                //EtData_Write.setText(data);
                if (data != null)
                {
                    if (!data.isEmpty())
                    {
                        getItemData(data);
                    }
                }
            } else {
                result = false;
                UIHelper.ToastMessage(mContext, R.string.uhf_msg_read_data_fail);
                //EtData_Write.setText("");

            }
        }
        if(!result){
            mContext.playSound(2);
        }else{
            mContext.playSound(1);
        }
    }

    private void getItemData(String substring)
    {
        ACDatabase db = new ACDatabase(mContext);

        Cursor data = db.getItemBC(substring);
        if (data.getCount() > 0)
        {
            data.moveToNext();
            try {
                tv_scan.setText(substring);
                tv_item.setText(data.getString(data.getColumnIndex("ItemCode")));
                tv_itemUOM.setText(data.getString(data.getColumnIndex("UOM")));
                tv_itemDescription.setText(data.getString(data.getColumnIndex("Description")));
                tv_itemDesc2.setText(data.getString(data.getColumnIndex("Desc2")));
                tv_itemBarcode.setText(data.getString(data.getColumnIndex("Barcode")));
            }
            catch (Exception e)
            {
                Log.i("custDebug", "error reading image: "+e.getMessage());
            }
        }
        else
        {
            tv_scan.setText(substring);
            Snackbar.make(mView, "Item not found.", Snackbar.LENGTH_SHORT).show();
        }
    }

    public String checkRedundantPlace(String text) {
        int length = text.length();
        String result = "";
        String temp_epc = "";

        char[] c_epc = text.toCharArray();

        for (int i = 0; i < length; i++) {
            if (c_epc[0] == c_epc[length - 1]) {
                result = text;
            } else {
                if (c_epc[i] == c_epc[length - 1]) {
                    for (int z = i; z < length; z++) {
                        temp_epc = temp_epc + c_epc[z];
                    }

                    if (temp_epc.contains(String.valueOf(c_epc[i]))) {
                        int count = temp_epc.length() - temp_epc.replaceAll(String.valueOf(c_epc[i]), "").length();

                        if (count != temp_epc.length()) {
                            result = result + c_epc[i];
                        } else {
                            continue;
                        }
                    } else {
                        result = result + c_epc[i];
                    }
                } else if (c_epc[i] != c_epc[length - 1]) {
                    result = result + c_epc[i];
                }
            }
        }

        return result;
    }

    public void myOnKeyDwon() {
        int selectedId = radioOperationGroup.getCheckedRadioButtonId();
        radioOperationButton = (RadioButton) getView().findViewById(selectedId);

        if (radioOperationButton.getText().equals("Read"))
        {
            read();
        }
        else
        {
            write();
        }
    }
}
