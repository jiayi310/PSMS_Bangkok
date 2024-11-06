package com.example.androidmobilestock_bangkok.ui.main;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import androidx.databinding.DataBindingUtil;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.androidmobilestock_bangkok.ACDatabase;
import com.example.androidmobilestock_bangkok.AC_Class;
import com.example.androidmobilestock_bangkok.ImageView;
import com.example.androidmobilestock_bangkok.ItemUOMList;
import com.example.androidmobilestock_bangkok.R;
import com.example.androidmobilestock_bangkok.databinding.FragmentStockDetailsBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StockDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StockDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StockDetailsFragment extends Fragment {
    AC_Class.Item item = new AC_Class.Item();
    AC_Class.ItemUOM itemUOM = new AC_Class.ItemUOM();
    String defaultCurr;
    ACDatabase db;
    int SellingPrice = 0;
    int CostPermission = 0;
    private OnFragmentInteractionListener mListener;
    ListView listview;
    List<AC_Class.UTDCost> utdCosts = new ArrayList<>();

    public StockDetailsFragment() {
        // Required empty public constructor
    }

    public static StockDetailsFragment newInstance(Bundle myBundle) {
        StockDetailsFragment fragment = new StockDetailsFragment();
        fragment.setArguments(myBundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new ACDatabase(getActivity());
        if (getArguments() != null) {
            item = getArguments().getParcelable("ItemDetailKey");
            getBalance();
        }

        Cursor dc = db.getReg("6");
        if(dc.moveToFirst()){
            defaultCurr  = dc.getString(0);
        }


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentStockDetailsBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_stock_details, container, false);

        MyClickHandler handler = new MyClickHandler(getContext());
        binding.setHandler(handler);

        View view = binding.getRoot();
        binding.setItem(item);
        binding.setItemuom(itemUOM);
        binding.setDefaultCurr(defaultCurr);

        listview = (ListView) view.findViewById(R.id.utdcostlist);
//        if (item.getImage() != null){
//            binding.imageView2.setImageBitmap(item.getImage());
//        } else { Log.i("custDebug", "no image."); }

        Cursor sale = db.getReg("48");
        if (sale.moveToFirst()) {
            SellingPrice = sale.getInt(0);
        }
        if(SellingPrice == 0){
            binding.priceRow1.setVisibility(View.GONE);
            binding.priceRow2.setVisibility(View.GONE);
            binding.priceRow3.setVisibility(View.GONE);
            binding.priceRow4.setVisibility(View.GONE);
        }


        Cursor cost = db.getReg("69");
        if (cost.moveToFirst()) {
            CostPermission = cost.getInt(0);
        }

        Bitmap itemImg = db.getItemImage(item.getItemCode());


        if (itemImg != null)
            binding.imageView2.setImageBitmap(itemImg);

        binding.imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imageIntent = new Intent(getContext(), ImageView.class);
                imageIntent.putExtra("itemCode", item.getItemCode());
                getContext().startActivity(imageIntent);
            }
        });

        if(CostPermission == 1) {

            getUTDCostData();

            listview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            // Disallow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            break;

                        case MotionEvent.ACTION_UP:
                            // Allow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }

                    // Handle ListView touch events.
                    v.onTouchEvent(event);
                    return true;
                }
            });
        }else{
            listview.setVisibility(View.GONE);
            LinearLayout linear = (LinearLayout) view.findViewById(R.id.linear_utd);
            linear.setVisibility(View.GONE);

        }
        return view;
    }

    public void getUTDCostData()
    {
        utdCosts.clear();
        Cursor data = db.getAllUTDCost(item.getItemCode(), item.getUOM());
        if (data.getCount() > 0) {

            while (data.moveToNext()) {
//                Log.i("custDebug", "SB: "+DatabaseUtils.dumpCursorToString(data));
                try {

                    AC_Class.UTDCost sb = new AC_Class.UTDCost(
                            data.getString(data.getColumnIndex("ItemCode")),
                            data.getString(data.getColumnIndex("BatchNo")),
                            data.getString(data.getColumnIndex("Location")),
                            data.getString(data.getColumnIndex("UOM")),
                            data.getDouble(data.getColumnIndex("UTDQty")),
                            data.getDouble(data.getColumnIndex("UTDCost"))
                            );

                    //if(sb.getBalQty()!=0) {
                    utdCosts.add(sb);
                    // }

                } catch (Exception e) {
                    Log.i("custDebug", e.getMessage());
                }
            }
            UTDCostListAdapter adapter = new UTDCostListAdapter(getActivity(), utdCosts);
            listview.setAdapter(adapter);
        }
        else{
            utdCosts.clear();
            UTDCostListAdapter adapter = new UTDCostListAdapter(getActivity(), utdCosts);
            listview.setAdapter(adapter);
        }
    }


    public void getBalance()
    {
        float qty = 0f;
        float baseQty = 0f;
        float rate = 0f;
        Cursor temp,temp2,temp3,temp4,temp5;

        temp = db.getStockBalance(item.getItemCode(), item.getUOM());
        if (temp.getCount() > 0)
        {
            temp.moveToNext();
            qty = temp.getFloat(0);
        }

        temp2 = db.getBaseStockBalance(item.getItemCode());
        if (temp2.getCount() > 0)
        {
            temp2.moveToNext();
            baseQty = temp2.getFloat(0);
        }

        temp4 = db.getBaseStockBalancebyUOM(item.getItemCode());
        if (temp4.getCount() > 0)
        {
            temp4.moveToNext();
            baseQty = temp4.getFloat(0);
        }

        temp3 = db.getSalesDtlHistoryPrice(item.getItemCode(), item.getUOM());
        if (temp3.getCount() > 0)
        {
            temp3.moveToNext();
            qty -= temp3.getFloat(0);
            //baseQty -= temp3.getFloat(0) * temp3.getFloat(1);
        }

        ArrayList itemUOMList = new ArrayList();
        temp5 = db.getSalesDtlHistoryPricebyItemCode(item.getItemCode());
        if (temp5.getCount() > 0)
        {
            while(temp5.moveToNext()) {
                itemUOMList.add(temp5.getFloat(0));
                baseQty -= temp5.getFloat(0);
            }
            //baseQty -= temp3.getFloat(0);
        }



        itemUOM.setBalQty(qty);
        itemUOM.setBaseBalQty(baseQty);


    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 6:
                if(data != null) {
                    AC_Class.ItemUOM itemUOM1 = data.getParcelableExtra("UOMKey");
                    if (itemUOM != null ) {
                        item.setItemCode(itemUOM1.getItemCode());
                        item.setShelf(itemUOM1.getShelf());
                        item.setUOM(itemUOM1.getUOM());
                        item.setRate(itemUOM1.getRate());
                        item.setPrice(Float.valueOf(itemUOM1.getPrice()));
                        item.setPrice2(Float.valueOf(itemUOM1.getPrice2()));
                        item.setPrice3(Float.valueOf(itemUOM1.getPrice3()));
                        item.setPrice4(Float.valueOf(itemUOM1.getPrice4()));
                        item.setPrice5(Float.valueOf(itemUOM1.getPrice5()));
                        item.setPrice6(Float.valueOf(itemUOM1.getPrice6()));
                        item.setMinPrice(Float.valueOf(itemUOM1.getMinPrice()));
                        item.setMinPrice(Float.valueOf(itemUOM1.getMinPrice()));
                        item.setBarCode(itemUOM1.getBarCode());


                    }


                    getBalance();
                }
                break;
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
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
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public class MyClickHandler {
        Context context;

        public MyClickHandler(Context context) {
            this.context = context;
        }


        public void onUOMTxtViewClicked(View view) {
            if (item.getItemCode() != null) {
                Intent new_intent = new Intent(context, ItemUOMList.class);
                new_intent.putExtra("ItemKey", item.getItemCode());
                startActivityForResult(new_intent, 6);
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
