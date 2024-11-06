package com.example.androidmobilestock_bangkok;
import com.example.androidmobilestock_bangkok.Model.BarChart2;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ACDatabase db;
    String[] nameProductbyAmt = new String[10];
    String[] valueProducrbyAmt = new String[10];
    String debtorName;
    String[] dnArray = new String[50];
    String[] dnArray2 = new String[50];
    String[] sArray = new String[50];
    String[] tmpNArray;
    String[] tmpVArray;
    String temp;
    String[] sArrayQty = new String[50];
    ArrayList xAxis2 = new ArrayList();
    String qty;
    TextView cover;
    TextView date;

    String[] itemArray = new String[50];
    String[] itemArray2 = new String[50];
    String[] itemArray3 = new String[50];
    String[] itemArray4 = new String[50];
    String[] itemSaleArray = new String[50];
    String[] itemQtyArray = new String[50];

    String[] itemBalanceArray = new String[50];

    android.widget.ImageView imageView;
    String URL;
    String URLStr;
    TextView txtURL;


    public Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home.
     */
    // TODO: Rename and change types and number of parameters
    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
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
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        db = new ACDatabase(getActivity());
        cover = v.findViewById(R.id.ttlsales);
        date = v.findViewById(R.id.date);
        getDataSales("");

        getData();
        for (int i = 0; i < dnArray.length && dnArray[i] != null; i++) {
            getData2(dnArray[i], i);
        }
        arrangeArray(dnArray, sArray);
        dnArray = tmpNArray;
        sArray = tmpVArray;


        //top 10 sales by Customer (Amt)
        BarChart barChart = v.findViewById(R.id.barChart);

        ArrayList<BarEntry> sales = new ArrayList<>();
        for (int i = 0; i < 10; i++) {

            if (sArray[i] != null) {
                Float y = Float.parseFloat(sArray[i]);
                sales.add(new BarEntry(i + 1, y));
            } else {
                break;
            }
        }

        BarDataSet barDataSet = new BarDataSet(sales, "Sales");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);
        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.animateY(2000);
        barChart.setDescription(null);

        ValueFormatter xAxisFormatter = new DayAxisValueFormatter(barChart);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);

        //top 10 sales by Customer (Qty)
        BarChart barChart2 = v.findViewById(R.id.barChart2);
        BarChart2 myBC2 = getBC2();

        //getData3();

        arrangeArrayBC2(myBC2);
        //arrangeArray(dnArray2, sArrayQty);

        ArrayList<BarEntry> salesqty = new ArrayList<>();

        if (myBC2.DebtorArray.size() > 0) {
            for (int i = 0; i < myBC2.DebtorArray.size(); i++) {

                if (myBC2.QtyArray.get(i) != null) {
                    Float y = Float.parseFloat(myBC2.QtyArray.get(i));
                    salesqty.add(new BarEntry(i + 1, y));
                } else {
                    break;
                }
            }
        }

        BarDataSet barDataSet2 = new BarDataSet(salesqty, "Sales");
        barDataSet2.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet2.setValueTextColor(Color.BLACK);
        barDataSet2.setValueTextSize(16f);

        BarData barData2 = new BarData(barDataSet2);
        barChart2.setFitBars(true);
        barChart2.setData(barData2);
        barChart2.animateY(2000);
        barChart2.setDescription(null);

        ValueFormatter xAxisFormatter2 = new DayAxisValueFormatter2(barChart2);
        XAxis xAxis2 = barChart2.getXAxis();
        xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis2.setDrawGridLines(false);
        xAxis2.setGranularity(1f); // only intervals of 1 day
        xAxis2.setLabelCount(7);
        xAxis2.setValueFormatter(xAxisFormatter2);

        //top 10 sales by product (Amt) & (Qty)
        getData_AllItems();
        for (int i = 0; i < itemArray.length && itemArray[i] != null; i++) {
            getData_TopItemsBySale(itemArray[i], i);
            getData_TopItemsByQty(itemArray2[i], i);
        }

        arrangeArray(itemArray, itemSaleArray);
        arrangeArray(itemArray2, itemQtyArray);

        //top 10 sales by product (Amt)
        BarChart barChart3 = v.findViewById(R.id.barChart3);

        ArrayList<BarEntry> productAmt = new ArrayList<>();
        for (int i = 0; i < 10; i++) {

            if (itemSaleArray[i] != null) {
                Float y = Float.parseFloat(itemSaleArray[i]);
                productAmt.add(new BarEntry(i + 1, y));
            } else {
                break;
            }
        }

        BarDataSet barDataSet3 = new BarDataSet(productAmt, "Sales");
        barDataSet3.setColors(ColorTemplate.JOYFUL_COLORS);
        barDataSet3.setValueTextColor(Color.BLACK);
        barDataSet3.setValueTextSize(16f);

        BarData barData3 = new BarData(barDataSet3);
        barChart3.setFitBars(true);
        barChart3.setData(barData3);
        barChart3.animateY(2000);
        barChart3.setDescription(null);

        ValueFormatter xAxisFormatter3 = new DayAxisValueFormatter3(barChart3);
        XAxis xAxis3 = barChart3.getXAxis();
        xAxis3.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis3.setDrawGridLines(false);
        xAxis3.setGranularity(1f); // only intervals of 1 day
        xAxis3.setLabelCount(7);
        xAxis3.setValueFormatter(xAxisFormatter3);

        //top 10 sales by product (Qty)
        BarChart barChart4 = v.findViewById(R.id.barChart4);

        ArrayList<BarEntry> productQty = new ArrayList<>();
        for (int i = 0; i < 10; i++) {

            if (itemQtyArray[i] != null) {
                Float y = Float.parseFloat(itemQtyArray[i]);
                productQty.add(new BarEntry(i + 1, y));
            } else {
                break;
            }
        }

        BarDataSet barDataSet4 = new BarDataSet(productQty, "Sales");
        barDataSet4.setColors(ColorTemplate.JOYFUL_COLORS);
        barDataSet4.setValueTextColor(Color.BLACK);
        barDataSet4.setValueTextSize(16f);

        BarData barData4 = new BarData(barDataSet4);
        barChart4.setFitBars(true);
        barChart4.setData(barData4);
        barChart4.animateY(2000);
        barChart4.setDescription(null);

        ValueFormatter xAxisFormatter4 = new DayAxisValueFormatter4(barChart4);
        XAxis xAxis4 = barChart4.getXAxis();
        xAxis4.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis4.setDrawGridLines(false);
        xAxis4.setGranularity(1f); // only intervals of 1 day
        xAxis4.setLabelCount(7);
        xAxis4.setValueFormatter(xAxisFormatter4);

        //top 10 highest product (balance qty)
        BarChart barChart5 = v.findViewById(R.id.barChart5);
        for (int i = 0; i < itemArray.length && itemArray[i] != null; i++) {
            getAllStockCount(itemArray3[i], i);
            //getData_TopItemsByQty(itemArray[i],i);
        }
        arrangeArray(itemArray3, itemBalanceArray);

        ArrayList<BarEntry> productHighest = new ArrayList<>();
        for (int i = 0; i < 10; i++) {

            if (itemBalanceArray[i] != null) {
                Float y = Float.parseFloat(itemBalanceArray[i]);
                productHighest.add(new BarEntry(i + 1, y));
            } else {
                break;
            }
        }
        //arrangeArray(itemArray,itemBalanceArray);

        BarDataSet barDataSet5 = new BarDataSet(productHighest, "Sales");
        barDataSet5.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet5.setValueTextColor(Color.BLACK);
        barDataSet5.setValueTextSize(16f);

        BarData barData5 = new BarData(barDataSet5);
        barChart5.setFitBars(true);
        barChart5.setData(barData5);
        barChart5.animateY(2000);
        barChart5.setDescription(null);

        ValueFormatter xAxisFormatter5 = new DayAxisValueFormatter5(barChart5);
        XAxis xAxis5 = barChart5.getXAxis();
        xAxis5.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis5.setDrawGridLines(false);
        xAxis5.setGranularity(1f); // only intervals of 1 day
        xAxis5.setLabelCount(7);
        xAxis5.setValueFormatter(xAxisFormatter5);

        //top 10 lowest product (balance qty)
        BarChart barChart6 = v.findViewById(R.id.barChart6);
        for (int i = 0; i < itemArray.length && itemArray[i] != null; i++) {
            getAllStockCount(itemArray4[i], i);
            //getData_TopItemsByQty(itemArray[i],i);
        }
        arrangeArrayDesc(itemArray4, itemBalanceArray);

        ArrayList<BarEntry> productLowest = new ArrayList<>();
        for (int i = 0; i < 10; i++) {

            if (itemBalanceArray[i] != null) {
                Float y = Float.parseFloat(itemBalanceArray[i]);
                productLowest.add(new BarEntry(i + 1, y));
            } else {
                break;
            }
        }
        //arrangeArray(itemArray,itemBalanceArray);

        BarDataSet barDataSet6 = new BarDataSet(productLowest, "Sales");
        barDataSet6.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet6.setValueTextColor(Color.BLACK);
        barDataSet6.setValueTextSize(16f);

        BarData barData6 = new BarData(barDataSet6);
        barChart6.setFitBars(true);
        barChart6.setData(barData6);
        barChart6.animateY(2000);
        barChart6.setDescription(null);

        ValueFormatter xAxisFormatter6 = new DayAxisValueFormatter6(barChart6);
        XAxis xAxis6 = barChart6.getXAxis();
        xAxis6.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis6.setDrawGridLines(false);
        xAxis6.setGranularity(1f); // only intervals of 1 day
        xAxis6.setLabelCount(7);
        xAxis6.setValueFormatter(xAxisFormatter6);

        return v;
    }


    public void getData() {
        Cursor data;

        data = db.getSalesDebtor();

        int i = 0;

        List<String> temp = new ArrayList<>();

        if (data.getCount() > 0) {
            while (data.moveToNext()) {
                try {
                    debtorName = data.getString(data.getColumnIndex("DebtorName"));

                    if (!temp.contains(debtorName)) {
                        temp.add(debtorName);

                        dnArray[i] = debtorName;
                        dnArray2[i] = debtorName;
                        Log.wtf("Debtor Name: ", dnArray[i]);
                        i++;
                    }

                } catch (Exception e) {
                    Log.i("custDebug", "error reading image: " + e.getMessage());
                }
            }
        }

    }

    public void getData2(String debtorName, int j) {
        Cursor data2;

        data2 = db.getDebtorDocNo(debtorName);

        float sales = 0;

        if (data2.getCount() > 0) {
            while (data2.moveToNext()) {
                try {
                    Log.wtf("DocNo: ", data2.getString(data2.getColumnIndex("DocNo")));

                    sales = getDataSales(data2.getString(data2.getColumnIndex("DocNo")), sales);
                    sArray[j] = String.valueOf(sales);
                } catch (Exception e) {
                    Log.i("custDebug", "error reading image: " + e.getMessage());
                }
            }
        }

        Log.wtf("Total Sales: ", sArray[j]);
    }

    public void getData3() {
        Cursor data2;

        data2 = db.getDebtorDocNobyQty();

        if (data2.getCount() > 0) {
            int j = 0;
            while (data2.moveToNext()) {
                try {
                    qty = data2.getString(data2.getColumnIndex("NUM"));
                    Log.wtf("DocNo: ", data2.getString(data2.getColumnIndex("DebtorName")) + data2.getString(data2.getColumnIndex("NUM")));

                    sArrayQty[j] = qty;
                    j++;
                    if (j == 10) {
                        break;
                    }
                } catch (Exception e) {
                    Log.i("custDebug", "error reading image: " + e.getMessage());
                }
            }

        }

        //   Log.wtf("Total Sales: ",sArray[j]);
    }

    public BarChart2 getBC2() {
        BarChart2 myBC2 = new BarChart2();
        Cursor data2 = db.getTop10DebtorQty();

        if (data2.getCount() > 0) {
            while (data2.moveToNext()) {
                myBC2.DebtorArray.add(data2.getString(data2.getColumnIndex("DebtorName")));
                myBC2.QtyArray.add(data2.getString(data2.getColumnIndex("TotalQty")));
            }
        }

        return myBC2;
    }

    public void arrangeArray(String[] name, String[] value) {
        int size = 0;
        tmpNArray = name;
        tmpVArray = value;

        for (int i = 0; i < name.length; i++) {
            if (name[i] != null) {
                size++;
            }
        }

        for (int i = 0; i < size && value[i] != null; i++) {
            for (int j = i + 1; j < size; j++) {
                int compare = Float.compare(Float.parseFloat(value[i]), Float.parseFloat(value[j]));
                if (compare < 0) {
                    temp = tmpVArray[i];
                    tmpVArray[i] = tmpVArray[j];
                    tmpVArray[j] = temp;

                    temp = tmpNArray[i];
                    tmpNArray[i] = tmpNArray[j];
                    tmpNArray[j] = temp;
                }
            }
        }
    }

    public void arrangeArrayBC2(BarChart2 bc2FP) {
        int size = 0;
        tmpNArray = bc2FP.DebtorArray.toArray(tmpNArray);
        tmpVArray = bc2FP.QtyArray.toArray(tmpVArray);

        for (int i = 0; i < bc2FP.DebtorArray.size(); i++) {
            for (int j = i + 1; j < size; j++) {
                int compare = Float.compare(Float.parseFloat(bc2FP.QtyArray.get(i)), Float.parseFloat(bc2FP.QtyArray.get(j)));
                if (compare < 0) {
                    temp = tmpVArray[i];
                    tmpVArray[i] = tmpVArray[j];
                    tmpVArray[j] = temp;

                    temp = tmpNArray[i];
                    tmpNArray[i] = tmpNArray[j];
                    tmpNArray[j] = temp;
                }
            }
        }
    }

    public void arrangeArrayDesc(String[] name, String[] value) {
        int size = 0;
        tmpNArray = name;
        tmpVArray = value;

        for (int i = 0; i < name.length; i++) {
            if (name[i] != null) {
                size++;
            }
        }

        for (int i = 0; i < size && value[i] != null; i++) {
            for (int j = i + 1; j < size; j++) {
                int compare = Float.compare(Float.parseFloat(value[i]), Float.parseFloat(value[j]));
                if (compare > 0) {
                    temp = tmpVArray[i];
                    tmpVArray[i] = tmpVArray[j];
                    tmpVArray[j] = temp;

                    temp = tmpNArray[i];
                    tmpNArray[i] = tmpNArray[j];
                    tmpNArray[j] = temp;
                }
            }
        }
    }


    public float getDataSales(String docNo, float sales) {
        Cursor data;

        data = db.getTopSales();
        if (data.getCount() > 0) {
            while (data.moveToNext()) {
                try {

                    sales = sales + Float.parseFloat(data.getString(data.getColumnIndex("SUM(Totalin)")));
                    Log.wtf("Sales Value: ", String.valueOf(sales));

                } catch (Exception e) {
                    Log.i("custDebug", "error reading image: " + e.getMessage());

                }
            }
        }

        return sales;
    }

    public class DayAxisValueFormatter extends ValueFormatter {
        private final BarLineChartBase<?> chart;

        public DayAxisValueFormatter(BarLineChartBase<?> chart) {
            this.chart = chart;
        }

        @Override
        public String getFormattedValue(float value) {
            int value_int = (int) (value -1);
            return String.valueOf(dnArray[(int) value_int]);
        }
    }

    public class DayAxisValueFormatter2 extends ValueFormatter {
        private final BarLineChartBase<?> chart;

        public DayAxisValueFormatter2(BarLineChartBase<?> chart) {
            this.chart = chart;
        }

        @Override
        public String getFormattedValue(float value) {
            int value_int = (int) (value -1);
            return String.valueOf(dnArray2[(int) value_int]);
        }
    }

    public class DayAxisValueFormatter3 extends ValueFormatter {
        private final BarLineChartBase<?> chart;

        public DayAxisValueFormatter3(BarLineChartBase<?> chart) {
            this.chart = chart;
        }

        @Override
        public String getFormattedValue(float value) {
            int value_int = (int) (value -1);
            return String.valueOf(itemArray[(int) value_int]);
        }
    }

    public class DayAxisValueFormatter4 extends ValueFormatter {
        private final BarLineChartBase<?> chart;

        public DayAxisValueFormatter4(BarLineChartBase<?> chart) {
            this.chart = chart;
        }

        @Override
        public String getFormattedValue(float value) {
            int value_int = (int) (value -1);
            return String.valueOf(itemArray2[(int) value_int]);
        }
    }

    public class DayAxisValueFormatter5 extends ValueFormatter {
        private final BarLineChartBase<?> chart;

        public DayAxisValueFormatter5(BarLineChartBase<?> chart) {
            this.chart = chart;
        }

        @Override
        public String getFormattedValue(float value) {
            int value_int = (int) (value -1);
            return String.valueOf(itemArray3[(int) value_int]);
        }
    }

    public class DayAxisValueFormatter6 extends ValueFormatter {
        private final BarLineChartBase<?> chart;

        public DayAxisValueFormatter6(BarLineChartBase<?> chart) {
            this.chart = chart;
        }

        @Override
        public String getFormattedValue(float value) {
            int value_int = (int) (value -1);
            return String.valueOf(itemArray4[(int) value_int]);
        }
    }

    public void getData_AllItems() {
        Cursor data3;
        data3 = db.getAllItemsInSales();

        int i = 0;

        List<String> temp = new ArrayList<>();

        if (data3.getCount() > 0) {
            while (data3.moveToNext()) {
                try {

                    if (!temp.contains(data3.getString(data3.getColumnIndex("ItemCode")))) {
                        itemArray[i] = data3.getString(data3.getColumnIndex("ItemCode"));
                        itemArray2[i] = data3.getString(data3.getColumnIndex("ItemCode"));
                        itemArray3[i] = data3.getString(data3.getColumnIndex("ItemCode"));
                        itemArray4[i] = data3.getString(data3.getColumnIndex("ItemCode"));
                        temp.add(itemArray[i]);
                        temp.add(itemArray2[i]);
                        temp.add(itemArray3[i]);
                        temp.add(itemArray4[i]);

                        Log.wtf("ItemCode: ", itemArray[i]);
                        i++;
                    } else {
                        Log.wtf("ItemCode: ", "It repeated");
                        continue;
                    }
                } catch (Exception e) {
                    Log.i("custDebug", "error reading image: " + e.getMessage());
                }
            }
        }

    }

    public void getData_TopItemsBySale(String docNo, int i) {
        Cursor data4;
        data4 = db.getTopItemsBySales(docNo);

        if (data4.getCount() > 0) {
            while (data4.moveToNext()) {
                try {
                    itemSaleArray[i] = data4.getString(data4.getColumnIndex("SUM(Totalin)"));
                } catch (Exception e) {
                    Log.i("custDebug", "error reading image: " + e.getMessage());
                }
            }
        }

    }

    public void getData_TopItemsByQty(String docNo, int i) {
        Cursor data5;
        data5 = db.getTopItemsByQty(docNo);

        if (data5.getCount() > 0) {
            while (data5.moveToNext()) {
                try {
                    itemQtyArray[i] = data5.getString(data5.getColumnIndex("SUM(Qty)"));
                } catch (Exception e) {
                    Log.i("custDebug", "error reading image: " + e.getMessage());
                }
            }
        }

    }

    public void getAllStockCount(String docNo, int i) {
        Cursor data4;
        data4 = db.getAllStockCount(docNo);

        if (data4.getCount() > 0) {
            while (data4.moveToNext()) {
                try {
                    Log.i("balance:", data4.getString(data4.getColumnIndex("BalQty")));

                    itemBalanceArray[i] = data4.getString(data4.getColumnIndex("BalQty"));

                } catch (Exception e) {
                    Log.i("custDebug", "error reading image: " + e.getMessage());
                }
            }
        } else {
            itemBalanceArray[i] = "0.0";
        }
    }

    public void getDataSales(String substring) {
        Cursor data = db.getInvoiceMenuLike(substring);
        List<AC_Class.InvoiceMenu> invoiceMenus = new ArrayList<>();
        //SharedPreferences prefs = getSharedPreferences("com.presoft.androidmobilestock", Context.MODE_PRIVATE);

        String todaydate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        Double sales=0.0;
        if (data.getCount() > 0)
        {
//            Log.i("custDebug", "debug");
            while (data.moveToNext()) {
                AC_Class.InvoiceMenu invoiceMenu = new AC_Class.InvoiceMenu(data.getString(0), data.getString(1), data.getString(2), data.getString(3), data.getString(4), data.getDouble(8));
//                Log.i("custDebug", invoiceMenu.getDebtorName()+", "+invoiceMenu.getDocNo());
                if (todaydate.equals(data.getString(1))){
                    sales+=data.getDouble(8);
                }
                invoiceMenus.add(invoiceMenu);
            }
        }

        Cursor cur = db.getReg("6");
        if(cur.moveToFirst()){
            cur.getString(0);
        }
        cover.setText(cur.getString(0)+" "+ String.format("%.2f", sales));
        date.setText(todaydate);
    }


}