package com.example.rfiddemo.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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
import android.widget.Spinner;

import com.example.rfiddemo.R;
import com.example.rfiddemo.activity.BaseTabFragmentActivity;
import com.example.rfiddemo.activity.UHFMainActivity;
import com.example.rfiddemo.tools.StringUtils;
import com.example.rfiddemo.tools.UIHelper;
import com.rscja.deviceapi.RFIDWithUHF.BankEnum;
import com.rscja.utility.StringUtility;

public class UHFWriteFragment extends KeyDwonFragment implements OnClickListener{

    private static final String TAG = "UHFWriteFragment";

    private UHFMainActivity mContext;

    Spinner SpinnerBank_Write;
    EditText EtPtr_Write;
    EditText EtLen_Write;
    EditText EtData_Write;
    EditText EtAccessPwd_Write;
    EditText etLen_filter_wt;

    Button BtWrite;

    CheckBox cb_filter_wt,cb_QT_W;
    EditText etPtr_filter_wt;
    EditText etData_filter_wt;
    RadioButton rbEPC_filter_wt;
    RadioButton rbTID_filter_wt;
    RadioButton rbUser_filter_wt;
    LinearLayout filterWrite,valueFilter,passwordFilter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.uhf_write_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = (UHFMainActivity) getActivity();
        SpinnerBank_Write = (Spinner) getView().findViewById(R.id.SpinnerBank_Write);
        EtPtr_Write = (EditText) getView().findViewById(R.id.EtPtr_Write);
        EtLen_Write = (EditText) getView().findViewById(R.id.EtLen_Write);
        EtData_Write = (EditText) getView().findViewById(R.id.EtData_Write);
        EtAccessPwd_Write = (EditText) getView().findViewById(R.id.EtAccessPwd_Write);
        etLen_filter_wt= (EditText) getView().findViewById(R.id.etLen_filter_wt);
        BtWrite = (Button) getView().findViewById(R.id.BtWrite);

        cb_QT_W= (CheckBox) getView().findViewById(R.id.cb_QT_W);
        cb_filter_wt = (CheckBox) getView().findViewById(R.id.cb_filter_wt);
        etPtr_filter_wt = (EditText) getView().findViewById(R.id.etPtr_filter_wt);
        etData_filter_wt = (EditText) getView().findViewById(R.id.etData_filter_wt);
        rbEPC_filter_wt = (RadioButton) getView().findViewById(R.id.rbEPC_filter_wt);
        rbTID_filter_wt = (RadioButton) getView().findViewById(R.id.rbTID_filter_wt);
        rbUser_filter_wt = (RadioButton) getView().findViewById(R.id.rbUser_filter_wt);

        filterWrite = (LinearLayout) getView().findViewById(R.id.writeFilter);
        valueFilter = (LinearLayout) getView().findViewById(R.id.valueFilter);
        passwordFilter = (LinearLayout) getView().findViewById(R.id.passwordFilter);

        //Deactivate Write Filter
        filterWrite.setVisibility(View.GONE);
        valueFilter.setVisibility(View.GONE);
        passwordFilter.setVisibility(View.GONE);

        rbEPC_filter_wt.setOnClickListener(this);
        rbTID_filter_wt.setOnClickListener(this);
        rbUser_filter_wt.setOnClickListener(this);
        BtWrite.setOnClickListener(this);

        mContext.mReader.setPower(10);

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
                if(element.equals("EPC")){
                    EtPtr_Write.setText("2");
                }else{
                    EtPtr_Write.setText("0");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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
    }

 private void write(){

     String strPtr = EtPtr_Write.getText().toString().trim();

     if (StringUtils.isEmpty(strPtr)) {
         UIHelper.ToastMessage(mContext, R.string.uhf_msg_addr_not_null);
         return;
     } else if (!StringUtility.isDecimal(strPtr)) {
         UIHelper.ToastMessage(mContext,
                 R.string.uhf_msg_addr_must_decimal);
         return;
     }

     String strPWD = EtAccessPwd_Write.getText().toString().trim();// 访问密码
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

     String strData = EtData_Write.getText().toString().trim();// 要写入的内容

     if (StringUtils.isEmpty(strData)) {
         UIHelper.ToastMessage(mContext,
                 R.string.uhf_msg_write_must_not_null);
         return;
     }else if (!mContext.vailHexInput(strData)) {
         UIHelper.ToastMessage(mContext,
                 "Please enter value contain only A-F and 0-9.");
         return;
     }

     // 多字单次
     String cntStr = EtLen_Write.getText().toString().trim();
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
     String Bank="";
     if(SpinnerBank_Write.getSelectedItemPosition()==0){
         Bank="UII";
     }else{
         Bank=SpinnerBank_Write.getSelectedItem().toString();
     }
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
         if(cb_QT_W.isChecked()){
            r= mContext.mReader.writeDataWithQT_Ex(strPWD,
                    BankEnum.valueOf(Bank),
                    Integer.parseInt(strPtr),
                    Integer.valueOf(cntStr),
                    fillSpaces(strData));
         }else{
                     r= mContext.mReader.writeData_Ex(strPWD,
                     BankEnum.valueOf(Bank),
                     Integer.parseInt(strPtr),
                     Integer.valueOf(cntStr),fillSpaces(strData));// 返回的UII
         }

         if (r) {
             result=true;
             UIHelper.ToastMessage(mContext, getString(R.string.uhf_msg_write_succ));
         } else {
             UIHelper.ToastMessage(mContext, R.string.uhf_msg_write_fail);
         }
     }
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

    public void myOnKeyDwon() {
        write();
    }
}
