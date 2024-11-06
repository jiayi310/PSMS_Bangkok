package com.example.androidmobilestock_bangkok.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.androidmobilestock_bangkok.ACDatabase;
import com.example.androidmobilestock_bangkok.AC_Class;
import com.example.androidmobilestock_bangkok.R;
import com.example.androidmobilestock_bangkok.activity.UHFMainActivity;
import com.example.androidmobilestock_bangkok.activity.UHFSecondActivity;
import com.example.androidmobilestock_bangkok.tools.StringUtils;
import com.example.androidmobilestock_bangkok.tools.UIHelper;
import com.example.androidmobilestock_bangkok.widget.NoScrollViewPager;
import com.rscja.deviceapi.RFIDWithUHF;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class UHFReadTagFragment extends KeyDwonFragment {

    ACDatabase db;
    private boolean loopFlag = false;
    private int inventoryFlag = 1;
    Handler handler;
    public ArrayList<HashMap<String, String>> tagList,tagList2;
    AC_Class.RFIDDtlList itDtl;
    AC_Class.RFIDItemTag it;
    public SimpleAdapter adapter,adapter2;
    Button BtClear;
    public TextView count_text;
    public TextView txtItemQuantity,txtTotalItemCode;
    Button BtInventory,BtnBack;
    public ListView LvTags,LvTags2;
    SimpleAdapter sa;
    private Button btnFilter;//过滤
    public Button btnBack;
    private LinearLayout llContinuous,mainheader,tidheader,linearTotal,linearFilter;
    private UHFMainActivity mContext;
    private HashMap<String, String> map,map2;
    PopupWindow popFilter;

    public NoScrollViewPager pager;

    AC_Class.StockTake st,st_temp;
    AC_Class.StockTakeDetails std;

    int isSent = 0;
    int i=0;
    int itemCodeQty = 0;
    int isRefresh = 0;
    int recordedItemCodeQty = 0;
    int newrecordedQty = 0;

    ArrayList<String> epc_ = new ArrayList<String>();
    ArrayList<String>  tid_ = new ArrayList<String>();
    ArrayList<Integer> qty_ = new ArrayList<Integer>();

    ArrayList<String> TIDlist = new ArrayList<String>();
    ArrayList<String> EPClist = new ArrayList<String>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("MY", "UHFReadTagFragment.onCreateView");

        Bundle b = this.getArguments();

        return inflater
                .inflate(R.layout.uhf_readtag_fragment, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i("MY", "UHFReadTagFragment.onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        mContext = (UHFMainActivity) getActivity();
        tagList = new ArrayList<HashMap<String, String>>();
        tagList2 = new ArrayList<HashMap<String, String>>();
        BtClear = (Button) getView().findViewById(R.id.BtClear);
        BtnBack = (Button) getView().findViewById(R.id.BtBack);

        pager = (NoScrollViewPager) getView().findViewById(R.id.pager);

        db = new ACDatabase(getActivity());

        std = new AC_Class.StockTakeDetails();

        count_text = (TextView) getView().findViewById(R.id.txtItemQuantity);
        txtItemQuantity = (TextView) getView().findViewById(R.id.txtItemQuantity);
        txtTotalItemCode = (TextView) getView().findViewById(R.id.txtTotalItemCode);

        BtInventory = (Button) getView().findViewById(R.id.BtInventory);
        LvTags = (ListView) getView().findViewById(R.id.LvTagsMain);
        LvTags2 = (ListView) getView().findViewById(R.id.LvTags2);
        LvTags.setAdapter(sa);

        llContinuous = (LinearLayout) getView().findViewById(R.id.llContinuous);
        mainheader = (LinearLayout) getView().findViewById(R.id.layout4);
        tidheader = (LinearLayout) getView().findViewById(R.id.layout5);
        linearTotal = (LinearLayout) getView().findViewById(R.id.linearTotal);
        linearFilter = (LinearLayout) getView().findViewById(R.id.layout2);

        //Deactivate the filter button
        linearFilter.setVisibility(View.GONE);

        adapter = new SimpleAdapter(mContext, tagList, R.layout.listtag_items,
                new String[]{"tagUii", "tagCount","tagStatus"},
                new int[]{R.id.TvTagUii2, R.id.TvTagCount,R.id.tvTagStatus});

        BtClear.setOnClickListener(new BtClearClickListener());
        BtInventory.setOnClickListener(new BtInventoryClickListener());
        btnFilter = (Button) getView().findViewById(R.id.btnFilter);

        mContext.mReader.setPower(30);

        tidheader.setVisibility(View.GONE);
        linearTotal.setVisibility(View.VISIBLE);

        st = new AC_Class.StockTake();
        st_temp = new AC_Class.StockTake();
        st = ((UHFMainActivity) getActivity()).gteDocNo();
        st_temp = ((UHFMainActivity) getActivity()).getSTTemp();
        Log.wtf("Fragemnt First DocNo",st.getDocNo());
        Log.wtf("StockDetailList Start Length", String.valueOf(st.StockDetailListLength()));
        Log.wtf("StockDetailList Start Temp Length", String.valueOf(st_temp.StockDetailListLength()));

        if(st.StockDetailListLength() > 0)
        {
            isSent = 1;
        }
        else{
            isSent = 0;
        }

        btnBack = (Button) getActivity().findViewById(R.id.BtBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent();
                newIntent.putExtra("StockDtl", std);
                newIntent.putExtra("STHeader",st);
                newIntent.putExtra("isSent",isSent);
                if(st.StockDetailListLength() > 0)
                {
                    mContext.setResult(0,newIntent);
                }
                else{
                    mContext.setResult(99,newIntent);
                }
                mContext.finish();
                mContext.mReader.free();
            }
        });

        btnFilter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popFilter == null) {
                    View viewPop = LayoutInflater.from(mContext).inflate(R.layout.popwindow_filter, null);

                    popFilter = new PopupWindow(viewPop, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);

                    popFilter.setTouchable(true);
                    popFilter.setOutsideTouchable(true);
                    popFilter.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    popFilter.setBackgroundDrawable(new BitmapDrawable());

                    final EditText etLen = (EditText) viewPop.findViewById(R.id.etLen);
                    final EditText etPtr = (EditText) viewPop.findViewById(R.id.etPtr);
                    final EditText etData = (EditText) viewPop.findViewById(R.id.etData);
                    final RadioButton rbEPC = (RadioButton) viewPop.findViewById(R.id.rbEPC);
                    final RadioButton rbTID = (RadioButton) viewPop.findViewById(R.id.rbTID);
                    final RadioButton rbUser = (RadioButton) viewPop.findViewById(R.id.rbUser);
                    final Button btSet = (Button) viewPop.findViewById(R.id.btSet);

                    btSet.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String filterBank = "UII";
                            if (rbEPC.isChecked()) {
                                filterBank = "UII";
                            } else if (rbTID.isChecked()) {
                                filterBank = "TID";
                            } else if (rbUser.isChecked()) {
                                filterBank = "USER";
                            }
                            if (etLen.getText().toString() == null || etLen.getText().toString().isEmpty()) {
                                UIHelper.ToastMessage(mContext, "数据长度不能为空");
                                return;
                            }
                            if (etPtr.getText().toString() == null || etPtr.getText().toString().isEmpty()) {
                                UIHelper.ToastMessage(mContext, "起始地址不能为空");
                                return;
                            }
                            int ptr = StringUtils.toInt(etPtr.getText().toString(), 0);
                            int len = StringUtils.toInt(etLen.getText().toString(), 0);
                            String data = etData.getText().toString().trim();
                            if (len > 0) {
                                String rex = "[\\da-fA-F]*"; //匹配正则表达式，数据为十六进制格式
                                if (data == null || data.isEmpty() || !data.matches(rex)) {
                                    UIHelper.ToastMessage(mContext, "过滤的数据必须是十六进制数据");
//									mContext.playSound(2);
                                    return;
                                }

                                if (mContext.mReader.setFilter(RFIDWithUHF.BankEnum.valueOf(filterBank), ptr, len, data, false)) {
                                    UIHelper.ToastMessage(mContext, R.string.uhf_msg_set_filter_succ);
                                } else {
                                    UIHelper.ToastMessage(mContext, R.string.uhf_msg_set_filter_fail);
//									mContext.playSound(2);
                                }
                            } else {
                                //禁用过滤
                                String dataStr = "";
                                if (mContext.mReader.setFilter(RFIDWithUHF.BankEnum.valueOf("UII"), 0, 0, dataStr, false)
                                        && mContext.mReader.setFilter(RFIDWithUHF.BankEnum.valueOf("TID"), 0, 0, dataStr, false)
                                        && mContext.mReader.setFilter(RFIDWithUHF.BankEnum.valueOf("USER"), 0, 0, dataStr, false)) {
                                    UIHelper.ToastMessage(mContext, R.string.msg_disable_succ);
                                } else {
                                    UIHelper.ToastMessage(mContext, R.string.msg_disable_fail);
                                }
                            }
                        }
                    });
                    CheckBox cb_filter = (CheckBox) viewPop.findViewById(R.id.cb_filter);
                    rbEPC.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (rbEPC.isChecked()) {
                                etPtr.setText("32");
                            }
                        }
                    });
                    rbTID.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (rbTID.isChecked()) {
                                etPtr.setText("0");
                            }
                        }
                    });
                    rbUser.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (rbUser.isChecked()) {
                                etPtr.setText("0");
                            }
                        }
                    });

                    cb_filter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) { //启用过滤

                            } else { //禁用过滤

                            }
                            popFilter.dismiss();
                        }
                    });
                }
                if (popFilter.isShowing()) {
                    popFilter.dismiss();
                    popFilter = null;
                } else {
                    popFilter.showAsDropDown(view);
                }
            }
        });
        LvTags.setAdapter(adapter);
        LvTags2.setAdapter(adapter2);
        clearData();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String result = msg.obj + "";
                String[] strs = result.split("@");
                mContext.playSound(1);
            }

        };
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.wtf("First Fragment DocNo",st.getDocNo());
        Log.wtf("StockDetailList Fragment Length", String.valueOf(st.StockDetailListLength()));
        recordedItemCodeQty = st.StockDetailListLength();
    }

    @Override
    public void onPause() {
        Log.i("MY", "UHFReadTagFragment.onPause");
        super.onPause();

        // 停止识别
        stopInventory();
    }

    /**
     * 添加EPC到列表中
     *
     * /*@param epc
     */

    /*private void addEPCToList(String tid, String epc) {

        epc = checkRedundantPlace(epc);

        if (!TextUtils.isEmpty(epc)) {

            int index = checkIsExist(epc);
            map = new HashMap<String, String>();

            map.put("tagUii", epc);
            map.put("tagCount", String.valueOf(1));

            map2 = new HashMap<String, String>();

            map2.put("tagUii2", tid);
            map2.put("tagEmpty", " ");
            map2.put("tagUii", epc);
            // mContext.getAppContext().uhfQueue.offer(epc + "\t 1");

            if (index == -1 && TIDlist.contains(tid) == false) {
                tagList.add(map);
                tagList2.add(map2);
                LvTags.setAdapter(adapter);
                LvTags2.setAdapter(adapter2);
                tv_count.setText("" + adapter2.getCount());
                TIDlist.add(tid);

            } else if(index != -1 && TIDlist.contains(tid) == false){
                int tagcount = Integer.parseInt(
                        tagList.get(index).get("tagCount"), 10) + 1;

                map.put("tagCount", String.valueOf(tagcount));

                tagList.set(index, map);
                tagList2.add(map2);
                LvTags2.setAdapter(adapter2);
                TIDlist.add(tid);
            }

            adapter.notifyDataSetChanged();
        }
    }

    public String checkRedundantPlace(String text)
    {
        int length = text.length();
        String result = "";
        String temp_epc = "";

        char[] c_epc = text.toCharArray();

        for(int i=0; i<length ; i++)
        {
            if(c_epc[0] == c_epc[length-1])
            {
                result = text;
            }
            else{
                if(c_epc[i] == c_epc[length-1]){
                    for(int z=i ; z<length ; z++)

                    {
                        temp_epc = temp_epc + c_epc[z];
                    }

                    if(temp_epc.contains(String.valueOf(c_epc[i])))
                    {
                        int count = temp_epc.length() - temp_epc.replaceAll(String.valueOf(c_epc[i]),"").length();

                        if(count != temp_epc.length())
                        {
                            result = result + c_epc[i];
                        }
                        else {
                            continue;
                        }
                    }

                    else {
                        result = result + c_epc[i];
                    }
                }
                else if(c_epc[i] != c_epc[length-1]) {
                    result = result + c_epc[i];
                }
            }
        }

        return result;
    }*/

    public class BtClearClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            clearData();
        }
    }

    public class BtImportClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (BtInventory.getText().equals(
                    mContext.getString(R.string.btInventory))) {
                if(tagList.size()==0) {
                    UIHelper.ToastMessage(mContext, "无数据导出");
                    return;
                }
                boolean re = FileImport.daochu("", tagList);
                if (re) {
                    UIHelper.ToastMessage(mContext, "导出成功");

                    tagList.clear();
                    tagList2.clear();

                    Log.i("MY", "tagList.size " + tagList.size());

                    adapter.notifyDataSetChanged();
                    adapter2.notifyDataSetChanged();
                }
            }
            else
            {
                UIHelper.ToastMessage(mContext, "请停止扫描后再导出");
            }
        }
    }

    private void clearData() {
        tagList.clear();
        tagList2.clear();
        TIDlist.clear();
        LvTags.setAdapter(null);
        EPClist.clear();
        epc_.clear();
        tid_.clear();
        qty_.clear();
        i=0;
        itemCodeQty=0;
        std = null;

        if(recordedItemCodeQty > 0)
        {
            Log.wtf("Full Detail List Length", String.valueOf(recordedItemCodeQty));
            Log.wtf("New Recorded Detail List Length", String.valueOf(newrecordedQty));
            st.StockDetailListLengthRemove(newrecordedQty,recordedItemCodeQty);
        }

        st_temp = st;
        txtTotalItemCode.setText("Total Item Code: \n" + itemCodeQty);
        txtItemQuantity.setText("Quantity: \n" + i);

        Log.i("MY", "tagList.size " + tagList.size());
        Log.wtf("StockDetailList Clear Length", String.valueOf(st.StockDetailListLength()));
        Log.wtf("StockDetailList Clear Temp Length", String.valueOf(st_temp.StockDetailListLength()));

        adapter.notifyDataSetChanged();
    }

    public class RgInventoryCheckedListener implements OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            llContinuous.setVisibility(View.GONE);

                inventoryFlag = 1;
                btnFilter.setBackgroundResource(R.drawable.button_bg);
                btnFilter.setEnabled(true);
                llContinuous.setVisibility(View.VISIBLE);
        }
    }

    public class BtInventoryClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            readTag();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1)
        {
            if(resultCode == 0) {

                    epc_ = data.getStringArrayListExtra("Item Code");
                    tid_ = data.getStringArrayListExtra("TID Code");
                    qty_ = data.getIntegerArrayListExtra("Qty");
                    Log.wtf("ISSENT", String.valueOf(isSent));

                    st = new AC_Class.StockTake();
                    st_temp = new AC_Class.StockTake();
                    st = ((UHFMainActivity) getActivity()).gteDocNo();
                    st_temp = ((UHFMainActivity) getActivity()).getSTTemp();
                    Log.wtf("StockDetailList Resume Length", String.valueOf(st.StockDetailListLength()));
                    Log.wtf("StockDetailList Resume Temp Length", String.valueOf(st_temp.StockDetailListLength()));
                    recordedItemCodeQty = st.StockDetailListLength();

                    addtoList(epc_,tid_);
                }
            }

            if(resultCode == 99)
            {
                st_temp = ((UHFMainActivity) getActivity()).getSTTemp();
            }
    }

    private void readTag() {
        Log.wtf("KEYYYYY DOWN","GUN TRIGGERED");
        Intent i = new Intent(getActivity(), UHFSecondActivity.class);
        i.putExtra("STHeader", st);
        i.putExtra("STHeaderTemp",st_temp);
        i.putExtra("isSent",isSent);
        startActivityForResult(i,1);
    }

    private void setViewEnabled(boolean enabled) {
        btnFilter.setEnabled(enabled);
        BtClear.setEnabled(enabled);
        BtnBack.setEnabled(enabled);
    }


    /**
     * 停止识别
     */
    private void stopInventory() {

        BtnBack.setVisibility(View.VISIBLE);
        mainheader.setVisibility(View.VISIBLE);
        LvTags.setVisibility(View.VISIBLE);
        LvTags2.setVisibility(View.GONE);
        tidheader.setVisibility(View.GONE);
        BtClear.setVisibility(View.VISIBLE);
        linearFilter.setVisibility(View.GONE);
        BtnBack.setText("DONE");
        if (loopFlag) {
            loopFlag = false;
            setViewEnabled(true);
            if (mContext.mReader.stopInventory()) {
                BtInventory.setText(mContext.getString(R.string.btInventory));
            } else {
                UIHelper.ToastMessage(mContext,
                        R.string.uhf_msg_inventory_stop_fail);
            }
        }
    }

    @Override
    public void myOnKeyDwon() {
        readTag();
    }

    public void addtoList(ArrayList<String> epc,ArrayList<String> tid){

        Log.wtf("Add to List in First Fragmemt","Stated");
        if(epc != null)
        {
            for(String e:epc)
            {
                std = new AC_Class.StockTakeDetails();

                if(!TIDlist.contains(tid.get(i)) && !EPClist.contains(e))
                {
                    Log.wtf("Repassed Item Code", epc.get(i));
                    Log.wtf("Repassed Qty", tid.get(i));
                    map = new HashMap<String, String>();

                    int qty = 0;

                    for(String etmp:epc)
                    {
                        if(etmp.equals(e))
                        {
                            qty++;
                        }
                    }

                    map.put("tagUii",e);
                    map.put("tagCount", String.valueOf(qty));

                    Cursor c = db.getItemBC(e);
                    if(c.getCount() > 0)
                    {
                        String an = c.getString(c.getColumnIndex("ItemCode"));
                        if(!an.equals(""))
                        {
                            SimpleDateFormat dateFormat = new SimpleDateFormat(
                                    "dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                            Date date = new Date();
                            map.put("tagStatus","Success");
                            std.setItemCode(e);
                            Log.wtf("Selected EPC",std.getItemCode());
                            std.setItemDescription(c.getString(c.getColumnIndex("Description")));
                            Log.wtf("Selected Desc",std.getItemDescription());
                            std.setUOM(c.getString(c.getColumnIndex("BaseUOM")));
                            Log.wtf("Selected UOM",std.getUOM());
                            std.setQuantity(Double.valueOf(qty));
                            Log.wtf("Selected Qty", String.valueOf(std.getQuantity()));
                            std.setStockDocNo(st.getDocNo());
                            Log.wtf("Selected DocNo",std.getStockDocNo());
                            std.setCreatedTimeStamp(dateFormat.format(date));
                            Log.wtf("Date",std.getCreatedTimeStamp());
                            st.getStockTakeDtlList().add(std);
                            newrecordedQty++;
                        }
                    }
                    else{
                        map.put("tagStatus","Failed");
                    }

                    Log.wtf("Map Values", map.toString());

                    tagList.add(map);
                    LvTags.setAdapter(adapter);
                    TIDlist.add(tid.get(i));
                    EPClist.add(e);
                    itemCodeQty++;
                }
                i++;
            }
        }

        Log.wtf("Qty of recorded Item Code Existed", String.valueOf(recordedItemCodeQty));
        setText(itemCodeQty,i);
    }

    public void setText(int itemCodeQty, int i)
    {
        txtTotalItemCode.setText("Total Item Code: \n" + itemCodeQty);
        txtItemQuantity.setText("Quantity: \n" + i);
    }
}
