package com.example.androidmobilestock;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.androidmobilestock.databinding.PlFragmentMultipletabCompareBinding;
import com.example.androidmobilestock.databinding.PplFragmentMultipletabCompareBinding;

import java.util.ArrayList;
import java.util.List;

public class PPL_MultipleTab_CompareFragment extends Fragment {
    ACDatabase db;
    ListView listview;
    PplFragmentMultipletabCompareBinding binding;
    AC_Class.DO packingList;

    private OnFragmentInteractionListener mListener;

    List<AC_Class.DODtl> newDetail = new ArrayList<>();
    Boolean BatchComparison = true;
    Boolean LocationComparison = true;

    public PPL_MultipleTab_CompareFragment() {
        // Required empty public constructor
    }

    public static PPL_MultipleTab_CompareFragment newInstance(Bundle myBundle) {
        PPL_MultipleTab_CompareFragment fragment = new PPL_MultipleTab_CompareFragment();
        fragment.setArguments(myBundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new ACDatabase(getActivity());
        if (getArguments() != null) {
            packingList = getArguments().getParcelable("iPackingList");
        } else {
            Log.i("custDebug", "ERROR, no args");
        }

        Cursor cursor2 = db.getReg("44");
        if(cursor2.moveToFirst()){
            BatchComparison = Boolean.valueOf(cursor2.getString(0));
        }

        Cursor cursor3 = db.getReg("49");
        if (cursor3.moveToFirst()) {
            LocationComparison = Boolean.valueOf(cursor3.getString(0));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.ppl_fragment_multipletab_compare, container, false);
        final View view = binding.getRoot();
        listview = (ListView)binding.lvDetail;
        getData();

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void getData(){
        if (packingList != null) {

            sortPackingList();

            List<AC_Class.SODtl> mySODtlList = new ArrayList<AC_Class.SODtl>();
            List<AC_Class.SODODtl> mySODODtlList = new ArrayList<AC_Class.SODODtl>();

            Cursor data;
            if(!BatchComparison && !LocationComparison) {
                data = db.getPODtl(packingList.getFromDocNo());
            }else {
                data = db.getPODtl2(packingList.getFromDocNo());
            }
            if (data.getCount() > 0)
            {
                while (data.moveToNext())
                {
                    AC_Class.SODtl newSODtl = new AC_Class.SODtl();
                    newSODtl.setID(data.getInt(0));
                    if (!data.getString(1).equals("null"))
                        newSODtl.setLocation(data.getString(1));
                    newSODtl.setItemCode(data.getString(2));
                    if (!data.getString(3).equals("null"))
                        newSODtl.setItemDescription(data.getString(3));
                    if (!data.getString(4).equals("null"))
                        newSODtl.setUOM(data.getString(4));
                    newSODtl.setQty(data.getDouble(5));
                    newSODtl.setLine_No(data.getInt(6));
                    if (!data.getString(9).equals("null"))
                        newSODtl.setBatchNo(data.getString(9));

                    mySODtlList.add(newSODtl);
                }
            }

            for (AC_Class.SODtl iSODtl : mySODtlList)
            {
                boolean isFound = false;
                for (AC_Class.DODtl iDtl : newDetail)
                {

                    if (!BatchComparison && !LocationComparison) {//if batch x, location x
                        if (iDtl.getItemCode().equals(iSODtl.getItemCode()) && iDtl.getUOM().equals(iSODtl.getUOM())) {
                            isFound = true;
                            AC_Class.SODODtl tempSODODtl = new AC_Class.SODODtl();
                            tempSODODtl.setID(iSODtl.getID());
                            tempSODODtl.setLocation(iDtl.getLocation());
                            tempSODODtl.setItemCode(iDtl.getItemCode());
                            tempSODODtl.setItemDescription(iDtl.getItemDescription());
                            tempSODODtl.setUOM(iDtl.getUOM());
                            tempSODODtl.setSOQty(iSODtl.getQty());
                            tempSODODtl.setDOQty(iDtl.getQty());
                            tempSODODtl.setSOBatch(iDtl.getBatchNo());

                            mySODODtlList.add(tempSODODtl);

                            iDtl.setDtlKey(iSODtl.getID());
                        }
                    } else if (!BatchComparison && LocationComparison) //if batch x, location /
                    {
                        if (iDtl.getItemCode().equals(iSODtl.getItemCode()) && iDtl.getUOM().equals(iSODtl.getUOM()) && iDtl.getLocation().equals(iSODtl.getLocation())) {
                            isFound = true;
                            AC_Class.SODODtl tempSODODtl = new AC_Class.SODODtl();
                            tempSODODtl.setID(iSODtl.getID());
                            tempSODODtl.setLocation(iDtl.getLocation());
                            tempSODODtl.setItemCode(iDtl.getItemCode());
                            tempSODODtl.setItemDescription(iDtl.getItemDescription());
                            tempSODODtl.setUOM(iDtl.getUOM());
                            tempSODODtl.setSOQty(iSODtl.getQty());
                            tempSODODtl.setDOQty(iDtl.getQty());
                            tempSODODtl.setSOBatch(iDtl.getBatchNo());

                            mySODODtlList.add(tempSODODtl);

                            iDtl.setDtlKey(iSODtl.getID());
                        }
                    } else if (BatchComparison && LocationComparison) //if batch /, location /
                    {

                        if (iDtl.getBatchNo() == null) {
                            iDtl.setBatchNo("-");
                        }
                        if (iSODtl.getBatchNo() == null) {
                            iSODtl.setBatchNo("-");
                        }

                        if (iDtl.getItemCode().equals(iSODtl.getItemCode()) && iDtl.getUOM().equals(iSODtl.getUOM()) && iDtl.getLocation().equals(iSODtl.getLocation()) && iDtl.getBatchNo().equals(iSODtl.getBatchNo())) {
                            isFound = true;
                            AC_Class.SODODtl tempSODODtl = new AC_Class.SODODtl();
                            tempSODODtl.setID(iSODtl.getID());
                            tempSODODtl.setLocation(iDtl.getLocation());
                            tempSODODtl.setItemCode(iDtl.getItemCode());
                            tempSODODtl.setItemDescription(iDtl.getItemDescription());
                            tempSODODtl.setUOM(iDtl.getUOM());
                            tempSODODtl.setSOQty(iSODtl.getQty());
                            tempSODODtl.setDOQty(iDtl.getQty());
                            tempSODODtl.setSOBatch(iDtl.getBatchNo());
                            tempSODODtl.setDOBatch(iDtl.getBatchNo());

                            mySODODtlList.add(tempSODODtl);

                            iDtl.setDtlKey(iSODtl.getID());
                        }
                    } else { //if batch /, location x
                        if (iDtl.getBatchNo() == null) {
                            iDtl.setBatchNo("-");
                        }
                        if (iSODtl.getBatchNo() == null) {
                            iSODtl.setBatchNo("-");
                        }
                        if (iDtl.getItemCode().equals(iSODtl.getItemCode()) && iDtl.getUOM().equals(iSODtl.getUOM()) && iDtl.getBatchNo().equals(iSODtl.getBatchNo())) {
                            isFound = true;
                            AC_Class.SODODtl tempSODODtl = new AC_Class.SODODtl();
                            tempSODODtl.setID(iSODtl.getID());
                            tempSODODtl.setLocation(iDtl.getLocation());
                            tempSODODtl.setItemCode(iDtl.getItemCode());
                            tempSODODtl.setItemDescription(iDtl.getItemDescription());
                            tempSODODtl.setUOM(iDtl.getUOM());
                            tempSODODtl.setSOQty(iSODtl.getQty());
                            tempSODODtl.setDOQty(iDtl.getQty());
                            tempSODODtl.setSOBatch(iSODtl.getBatchNo());
                            tempSODODtl.setDOBatch(iDtl.getBatchNo());

                            mySODODtlList.add(tempSODODtl);

                            iDtl.setDtlKey(iSODtl.getID());
                        }

                    }

                }
                if (!isFound)
                {
                    AC_Class.SODODtl tempSODODtl = new AC_Class.SODODtl();
                    tempSODODtl.setID(0);
                    tempSODODtl.setLocation(iSODtl.getLocation());
                    tempSODODtl.setItemCode(iSODtl.getItemCode());
                    tempSODODtl.setItemDescription(iSODtl.getItemDescription());
                    tempSODODtl.setUOM(iSODtl.getUOM());
                    tempSODODtl.setSOQty(iSODtl.getQty());
                    tempSODODtl.setDOQty(Double.valueOf(0));
                    tempSODODtl.setSOBatch(iSODtl.getBatchNo());

                    mySODODtlList.add(tempSODODtl);
                }
            }

            for (AC_Class.DODtl iDtl : newDetail)
            {
                boolean isFound = false;
                for (AC_Class.SODtl iSODtl : mySODtlList)
                {
                    if (!BatchComparison && !LocationComparison) {
                        if (iDtl.getItemCode().equals(iSODtl.getItemCode()) && iDtl.getUOM().equals(iSODtl.getUOM())) {
                            isFound = true;
                        }
                    } else if(!BatchComparison && LocationComparison){
                        if (iDtl.getItemCode().equals(iSODtl.getItemCode()) && iDtl.getUOM().equals(iSODtl.getUOM()) && iDtl.getLocation().equals(iSODtl.getLocation())) {
                            isFound = true;
                        }
                    }else if(BatchComparison && LocationComparison){
                        if (iDtl.getItemCode().equals(iSODtl.getItemCode()) && iDtl.getUOM().equals(iSODtl.getUOM()) && iDtl.getLocation().equals(iSODtl.getLocation())  && iDtl.getBatchNo().equals(iSODtl.getBatchNo())) {
                            isFound = true;
                        }
                    }
                    else {
                        if (iDtl.getItemCode().equals(iSODtl.getItemCode()) && iDtl.getUOM().equals(iSODtl.getUOM()) && iDtl.getBatchNo().equals(iSODtl.getBatchNo())) {
                            isFound = true;
                        }
                    }
                }
                if (!isFound)
                {
                    AC_Class.SODODtl tempSODODtl = new AC_Class.SODODtl();
                    tempSODODtl.setID(0);
                    tempSODODtl.setLocation(iDtl.getLocation());
                    tempSODODtl.setItemCode(iDtl.getItemCode());
                    tempSODODtl.setItemDescription(iDtl.getItemDescription());
                    tempSODODtl.setUOM(iDtl.getUOM());
                    tempSODODtl.setSOQty(Double.valueOf(0));
                    tempSODODtl.setDOQty(iDtl.getQty());
                    tempSODODtl.setDOBatch(iDtl.getBatchNo());

                    mySODODtlList.add(tempSODODtl);
                }
            }


            PL_CompareListViewAdapter adapter = new PL_CompareListViewAdapter(getContext(), mySODODtlList);
            binding.lvDetail.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            boolean varianceFlag = false;

            if (mySODODtlList.size() > 0)
            {
                for (AC_Class.SODODtl iSODODtl: mySODODtlList)
                {
                    if (iSODODtl.SOQty - iSODODtl.DOQty != 0)
                    {
                        varianceFlag = true;
                    }
                }
            }

            if (varianceFlag)
            {
                binding.tvTally.setText("NOT TALLY");
                packingList.setIsTally(false);
            }
            else
            {
                binding.tvTally.setText("TALLY");
                packingList.setIsTally(true);
            };
        }
    }

    private void sortPackingList() {
        if (packingList.getDODtlList().size() > 0) {
            for (int i = 0; i < packingList.getDODtlList().size(); i++) {
                boolean result = false;

                if(!LocationComparison && !BatchComparison) {
                    for (AC_Class.DODtl myDtl : newDetail) {
                        if (packingList.getDODtlList().get(i).getItemCode().equals(myDtl.getItemCode()) && packingList.getDODtlList().get(i).getUOM().equals(myDtl.getUOM())) {
                            myDtl.setQty(myDtl.getQty() + packingList.getDODtlList().get(i).getQty());
                            result = true;
                        }
                    }
                }else if(LocationComparison && BatchComparison) {
                    for (AC_Class.DODtl myDtl : newDetail) {
                        if (packingList.getDODtlList().get(i).getItemCode().equals(myDtl.getItemCode()) && packingList.getDODtlList().get(i).getUOM().equals(myDtl.getUOM()) && packingList.getDODtlList().get(i).getLocation().equals(myDtl.getLocation()) && packingList.getDODtlList().get(i).getBatchNo().equals(myDtl.getBatchNo())) {
                            myDtl.setQty(myDtl.getQty() + packingList.getDODtlList().get(i).getQty());
                            result = true;
                        }
                    }
                }else if(!LocationComparison && BatchComparison) {
                    for (AC_Class.DODtl myDtl : newDetail) {
                        if (packingList.getDODtlList().get(i).getItemCode().equals(myDtl.getItemCode()) && packingList.getDODtlList().get(i).getUOM().equals(myDtl.getUOM()) && packingList.getDODtlList().get(i).getBatchNo().equals(myDtl.getBatchNo())) {
                            myDtl.setQty(myDtl.getQty() + packingList.getDODtlList().get(i).getQty());
                            result = true;
                        }
                    }
                }
                else{
                    for (AC_Class.DODtl myDtl : newDetail) {
                        if (packingList.getDODtlList().get(i).getItemCode().equals(myDtl.getItemCode()) && packingList.getDODtlList().get(i).getUOM().equals(myDtl.getUOM()) && packingList.getDODtlList().get(i).getLocation().equals(myDtl.getLocation())) {
                            myDtl.setQty(myDtl.getQty() + packingList.getDODtlList().get(i).getQty());
                            result = true;
                        }
                    }
                }

                if (!result) {
                    newDetail.add(new AC_Class.DODtl(packingList.getDODtlList().get(i)));
                }
            }
        }
    }


}
