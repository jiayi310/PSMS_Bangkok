package com.example.androidmobilestock_bangkok.fragment;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.androidmobilestock_bangkok.AC_Class;
import com.example.androidmobilestock_bangkok.R;
import com.example.androidmobilestock_bangkok.activity.UHFSecondActivity;
import com.example.androidmobilestock_bangkok.tools.StringUtils;
import com.example.androidmobilestock_bangkok.tools.UIHelper;
import com.rscja.deviceapi.RFIDWithUHF;

import java.util.ArrayList;
import java.util.HashMap;

public class UHFReadTagFragment2 extends KeyDwonFragment {

    private boolean loopFlag = false;
    private int inventoryFlag = 1;
    Handler handler;
    private ArrayList<HashMap<String, String>> tagList, tagList2;
    SimpleAdapter adapter2;
    AC_Class.RFIDItemTag it;
    AC_Class.RFIDDtlList itDtl;
    TextView tv_count, count_text;
    Button BtInventory, BtnImport;
    ListView LvTags2;

    AC_Class.StockTake st;
    AC_Class.StockTake st_temp;

//    ArrayList<String> epc_ = new ArrayList<String>();
//    ArrayList<String>  tid_ = new ArrayList<String>();
//    ArrayList<Integer> qty_ = new ArrayList<Integer>();
    ArrayList<AC_Class.RFIDItemTag> myItemTag = new ArrayList();

    int i = 0;
    boolean isRepeated = false;

    private Button btnFilter;//过滤
    private LinearLayout llContinuous, tidheader, linearTotal, linearFilter;
    private UHFSecondActivity mContext;
    private HashMap<String, String> map, map2;
    PopupWindow popFilter;

    int rfidQTY = 0;
    ArrayList<String> TIDlist = new ArrayList<String>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("MY", "UHFReadTagFragment.onCreateView");

        Bundle bundle = getArguments();
        if (bundle != null) {
            String test = bundle.getString("Test");
            Log.wtf("Test Token", test);
        }

        return inflater
                .inflate(R.layout.uhf_readtag_fragment2, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i("MY", "UHFReadTagFragment.onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        mContext = (UHFSecondActivity) getActivity();

        tagList = new ArrayList<HashMap<String, String>>();
        tagList2 = new ArrayList<HashMap<String, String>>();
        BtnImport = (Button) getView().findViewById(R.id.BtImport);

        st = new AC_Class.StockTake();
        st_temp = new AC_Class.StockTake();
        //st = ((UHFSecondActivity) getActivity()).gteDocNo();
        //st_temp = ((UHFSecondActivity) getActivity()).getSTTemp();
        tv_count = (TextView) getView().findViewById(R.id.tv_count);
        count_text = (TextView) getView().findViewById(R.id.txtItemQuantity);

        BtInventory = (Button) getView().findViewById(R.id.BtInventory);
        LvTags2 = (ListView) getView().findViewById(R.id.LvTags2);
        LvTags2.setVisibility(View.VISIBLE);

        llContinuous = (LinearLayout) getView().findViewById(R.id.llContinuous);
        tidheader = (LinearLayout) getView().findViewById(R.id.layout5);
        linearTotal = (LinearLayout) getView().findViewById(R.id.linearTotal);
        linearFilter = (LinearLayout) getView().findViewById(R.id.layout2);

        //Deactivate the filter button
        linearFilter.setVisibility(View.GONE);
        tidheader.setVisibility(View.VISIBLE);

        it = new AC_Class.RFIDItemTag();
        itDtl = new AC_Class.RFIDDtlList();

        adapter2 = new SimpleAdapter(mContext, tagList2, R.layout.listtag_items2,
                new String[]{"tagUii2", "tagUii"},
                new int[]{R.id.TvTagUii2, R.id.tvTagUii});

        BtInventory.setOnClickListener(new BtInventoryClickListener());
        btnFilter = (Button) getView().findViewById(R.id.btnFilter);
        mContext.mReader.setPower(20);

        count_text.setText("Unique Item Code");

        linearTotal.setVisibility(View.VISIBLE);

        BtnImport.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                itDtl.getRFIDDtlList().add(it);
                Intent newIntent = new Intent();
                newIntent.putExtra("myRFIDItemTag", myItemTag);


//                newIntent.putStringArrayListExtra("Item Code",epc_);
//                newIntent.putStringArrayListExtra("TID Code",tid_);
//                newIntent.putIntegerArrayListExtra("Qty",qty_);
                //newIntent.putExtra("STHeader", st);
                //newIntent.putExtra("STHeaderTemp", st_temp);
                //newIntent.putExtra("isSent","1");
                mContext.setResult(0,newIntent);
                mContext.finish();
                clearData();
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

        LvTags2.setAdapter(adapter2);
        clearData();
        Log.i("MY", "UHFReadTagFragment.EtCountOfTags=" + tv_count.getText());
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String result = msg.obj + "";
                String[] strs = result.split("@");
                if(strs[0] != null && strs[1] != null)
                {
                    addEPCToList(strs[0], strs[1]);
                    Log.wtf("Repeated", String.valueOf(isRepeated));
                    mContext.playSound(1);
                }
            }

        };
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
     * @param epc
     */

    private void addEPCToList(String tid, String epc) {

        if(!epc.equals("") && !tid.equals(""))
        {
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

                if(!epc.equals(""))
                {
                    if (index == -1 && TIDlist.contains(tid) == false) {
                        Log.wtf("Scan Type", "Repeated Scan Is Not Repeated");
                        tagList.add(map);
                        tagList2.add(map2);
                        LvTags2.setAdapter(adapter2);
                        tv_count.setText("" + adapter2.getCount());
                        TIDlist.add(tid);

//                        epc_.add(epc);
//                        tid_.add(tid);
//                        qty_.add(Integer.parseInt(map.get("tagCount")));
                        myItemTag.add(new AC_Class.RFIDItemTag(epc, tid, Integer.parseInt(map.get("tagCount"))));

                        Log.wtf("Item Code", epc);
                        Log.wtf("TID Code", tid);
                        Log.wtf("Qty", String.valueOf(map.get("tagCount")));

                        itDtl.setQuantity(rfidQTY);
                        rfidQTY++;
                        isRepeated = false;

                    } else if (index != -1 && TIDlist.contains(tid) == false) {
                        Log.wtf("Scan Type", "Repeated Scan Is Repeated");

                        int tagcount = Integer.parseInt(
                                tagList.get(index).get("tagCount"), 10) + 1;

                        tagList2.add(map2);
                        LvTags2.setAdapter(adapter2);
                        tv_count.setText("" + adapter2.getCount());

                        map.put("tagCount", String.valueOf(tagcount));

                        TIDlist.add(tid);

//                      epc_.add(epc);
//                      tid_.add(tid);
//                      qty_.add(tagcount);
                        myItemTag.add(new AC_Class.RFIDItemTag(epc, tid, tagcount));

                        Log.wtf("Item Code", epc);
                        Log.wtf("TID Code", tid);
                        Log.wtf("Qty", String.valueOf(tagcount));

                        rfidQTY++;


                        itDtl.setQuantity(rfidQTY);
                        isRepeated = true;
                    }

                }
            }
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

    public class BtImportClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (BtInventory.getText().equals(
                    mContext.getString(R.string.btInventory))) {
                if (tagList.size() == 0) {
                    UIHelper.ToastMessage(mContext, "无数据导出");
                    return;
                }
                boolean re = FileImport.daochu("", tagList);
                if (re) {
                    UIHelper.ToastMessage(mContext, "导出成功");

                    tv_count.setText("0");

                    tagList.clear();
                    tagList2.clear();

                    Log.i("MY", "tagList.size " + tagList.size());

                    adapter2.notifyDataSetChanged();
                }
            } else {
                UIHelper.ToastMessage(mContext, "请停止扫描后再导出");
            }
        }


    }

    private void clearData() {
        tv_count.setText("0");

        tagList.clear();
        tagList2.clear();
        TIDlist.clear();
//        epc_.clear();
//        tid_.clear();
//        qty_.clear();
        myItemTag.clear();

        Log.i("MY", "tagList.size " + tagList.size());

        adapter2.notifyDataSetChanged();
    }

    public class BtInventoryClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            readTag();
        }
    }

    private void readTag() {
        BtnImport.setVisibility(View.VISIBLE);
        LvTags2.setVisibility(View.VISIBLE);
        tidheader.setVisibility(View.VISIBLE);
        linearFilter.setVisibility(View.GONE);
        count_text.setText("Total Count");

        if (BtInventory.getText().equals(
                mContext.getString(R.string.btInventory)))// 识别标签))
        {
            switch (inventoryFlag) {
                case 0:// 单步
                {
                    String[] res = mContext.mReader.inventorySingleTagEPC_TID_USER();

                    if (res.length == 2) {
                        tv_count.setText("" + adapter2.getCount());
                    } else {
                        UIHelper.ToastMessage(mContext, R.string.uhf_msg_inventory_fail);
//					mContext.playSound(2);
                        break;
                    }
                }
                break;
                case 1:// 单标签循环  .startInventoryTag((byte) 0, (byte) 0))
                {
                      mContext.mReader.setEPCTIDMode(true);
                        if (mContext.mReader.startInventoryTag(0, 0)) {
                            BtInventory.setText(mContext
                                    .getString(R.string.title_stop_Inventory));
                            loopFlag = true;
                            setViewEnabled(false);
                            new TagThread().start();
                            tv_count.setText("" + adapter2.getCount());
                        } else {
                            mContext.mReader.stopInventory();
                            UIHelper.ToastMessage(mContext,
                                    R.string.uhf_msg_inventory_open_fail);
//					mContext.playSound(2);
                        }
                }
                break;
                default:
                    break;
            }
        } else {// 停止识别
            stopInventory();
        }
    }

    private void setViewEnabled(boolean enabled) {
        btnFilter.setEnabled(enabled);
        BtnImport.setEnabled(enabled);
    }

    /**
     * 停止识别
     */
    private void stopInventory() {

        BtnImport.setVisibility(View.VISIBLE);
        linearFilter.setVisibility(View.GONE);
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

    /**
     * 判断EPC是否在列表中
     *
     * @param strEPC 索引
     * @return
     */
    public int checkIsExist(String strEPC) {
        int existFlag = -1;
        if (StringUtils.isEmpty(strEPC)) {
            return existFlag;
        }
        String tempStr = "";
        for (int i = 0; i < tagList.size(); i++) {
            HashMap<String, String> temp = new HashMap<String, String>();
            temp = tagList.get(i);
            tempStr = temp.get("tagUii");
            if (strEPC.equals(tempStr)) {
                existFlag = i;
                break;
            }
        }
        return existFlag;
    }

    class TagThread extends Thread {
        public void run() {
            String strTid;
            String strResult = "Empty";
            String[] res = null;

            while (loopFlag) {
                res = mContext.mReader.readTagFromBuffer();
                if (res != null) {
                    strTid = res[0];
                    if (strTid.length() != 0) {
                        strResult = strTid;
                    } else {
                        strResult = "Empty";
                    }
                    Log.i("data", "EPC:" + res[1] + "|" + strResult);
                    Message msg = handler.obtainMessage();

                    msg.obj = strResult + "@" + mContext.mReader.convertUiiToEPC(res[1]) + "@" + res[2];

                    handler.sendMessage(msg);
                    tv_count.setText("" + adapter2.getCount());
                }
            }
        }
    }

    @Override
    public void myOnKeyDwon()
    {
        readTag();
    }

}
