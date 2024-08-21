package com.example.androidmobilestock;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Analytics extends AppCompatActivity {

    ACDatabase db;
    String[] dnArray = new String[50];
    String[] dnArray2 = new String[50];
    String[] dnArray3 = new String[50];
    List<String> list = new ArrayList<>();
    List<String> list2 = new ArrayList<>();
    List<String> list3 = new ArrayList<>();
    List<String> list4 = new ArrayList<>();
    List<String> list5 = new ArrayList<>();
    List<String> list6 = new ArrayList<>();
    List<String> list7 = new ArrayList<>();

    String[] itemArray = new String[50];
    String[] itemArray2 = new String[50];
    String[] itemArray3 = new String[50];
    String[] itemArray4 = new String[50];

    LinearLayout individual, graph;
    String url;
    int AnalyticControl = 0;
    Double ytdSales = 0.0, tdySales = 0.0, lmSales = 0.0, tmSales = 0.0, lySales = 0.0, tySales = 0.0;
    List<AC_Class.AgentSalesList> agentSalesLists = new ArrayList<>();
    LinearLayout barChartagent;
    TextView text0;
    BarChart barchartAgent;
    private ViewPager viewPager;
    private ArrayList<AnalyticModel> modelArrayList;
    private AnalyticsAdapter adapter;
    String tdyDate, ytdDate, lastmonthdate, thismonth, lastyeardate, thisyear;
    String Default_curr;
    ArrayList<String> myModules = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);
        barChartagent = findViewById(R.id.barchartagentlayout);
        barChartagent.setVisibility(View.GONE);
        viewPager = findViewById(R.id.viewPager);
        modelArrayList = new ArrayList<>();

        ActionBar actionBar = getSupportActionBar();
        try {
            actionBar.setTitle("Analytics");
            actionBar.setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            Log.i("custDebug", "Analytics: "+e.getMessage());}

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        ytdDate = dateFormat.format(cal.getTime());

        DateFormat dateFormat2 = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal2 = Calendar.getInstance();
        tdyDate = dateFormat2.format(cal2.getTime());

        Calendar cal3 = Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
        thismonth = month_date.format(cal3.getTime());

        Calendar cal4 = Calendar.getInstance();
        cal4.add(Calendar.MONTH, -1);
        SimpleDateFormat month_date2 = new SimpleDateFormat("MMMM");
        lastmonthdate = month_date2.format(cal4.getTime());

        SimpleDateFormat dateFormat5 = new SimpleDateFormat("yyyy");
        Calendar cal5 = Calendar.getInstance();
        cal5.add(Calendar.YEAR, -1);
        lastyeardate = dateFormat5.format(cal5.getTime());

        DateFormat dateFormat6 = new SimpleDateFormat("yyyy");
        Calendar cal6 = Calendar.getInstance();
        thisyear = dateFormat6.format(cal6.getTime());

        db = new ACDatabase(this);
        individual = findViewById(R.id.linearLayoutIndividual);
        graph = findViewById(R.id.graph);
        text0 = findViewById(R.id.text0);
        barchartAgent = findViewById(R.id.barChartAgent);

        Cursor cur5 = db.getReg("2");
        if (cur5.moveToFirst()) {
            url = cur5.getString(0);
        }

        Cursor data = db.getReg("45");
        if (data.moveToFirst()) {
            AnalyticControl = data.getInt(0);
        }

        getModules();

        Cursor dcurren = db.getReg("6");
        if (dcurren.moveToFirst()) {
            Default_curr = dcurren.getString(0);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout); // get the reference of TabLayout
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        list7.clear();
                        dnArray3 = new String[50];
                        barchartAgent.setAnimation(null);
                        barchartAgent.invalidate();
                        barchartAgent.clear();
                        barchartAgent.clearAnimation();

                        modelArrayList = new ArrayList<>();
                        getDataSales("");
                        getMonthlySales("");
                        getYearlySales("");
                        setAdapter();
                        list.clear();
                        dnArray = new String[50];
                        list2.clear();
                        dnArray2 = new String[50];
                        list3.clear();
                        itemArray = new String[50];
                        list4.clear();
                        itemArray2 = new String[50];
                        list5.clear();
                        itemArray3 = new String[50];
                        list6.clear();
                        itemArray4 = new String[50];
                        getIndividualGraph();
                        break;
                    case 1:
                        agentSalesLists.clear();
                        //customer(Amt) graph
                        list.clear();
                        dnArray = new String[50];
                        new GetGraph_CusAmt(Analytics.this).execute(url);
                        //customer(Qty)
                        list2.clear();
                        dnArray2 = new String[50];
                        new GetGraph_CusQty(Analytics.this).execute(url);
                        //Product(Amt)
                        list3.clear();
                        itemArray = new String[50];
                        new GetGraph_ProAmt(Analytics.this).execute(url);
                        //Product(Qty)
                        list4.clear();
                        itemArray2 = new String[50];
                        new GetGraph_ProQty(Analytics.this).execute(url);
                        //Highest Balance
                        list5.clear();
                        itemArray3 = new String[50];
                        new GetGraph_HighBal(Analytics.this).execute(url);
                        //Lowest Balance
                        list6.clear();
                        itemArray4 = new String[50];
                        new GetGraph_LowBal(Analytics.this).execute(url);

                        barChartagent.setVisibility(View.VISIBLE);
                        list7.clear();
                        dnArray3 = new String[50];
                        new GetGraph_TopAgent(Analytics.this).execute(url);
                        new GetAllAgentSales_ytd(Analytics.this).execute(url);

                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        if (myModules.contains("ANALYTICS")) {
            if (AnalyticControl == 0) {
                ((LinearLayout) tabLayout.getTabAt(1).view).setVisibility(View.GONE);
            }
        }else{
            ((LinearLayout) tabLayout.getTabAt(1).view).setVisibility(View.GONE);
        }

        getDataSales("");
        getMonthlySales("");
        getYearlySales("");
        setAdapter();
        getIndividualGraph();
    }

    public void previous(View view) {

        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
    }

    public void next(View view) {

        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
    }

    private void setAdapter() {
        adapter = new AnalyticsAdapter(this, modelArrayList);
        viewPager.setAdapter(adapter);
    }

    public void getIndividualGraph(){

        getData();

        //top 10 sales by Customer (Amt)
        BarChart barChart = findViewById(R.id.barChart);

        ArrayList<BarEntry> sales = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Float y = Float.parseFloat(list.get(i));
            sales.add(new BarEntry(i + 1, y));
        }

        BarDataSet barDataSet = new BarDataSet(sales, "Customer");
        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(12f);

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


        //top 10 sales by debtor (Qty)
        getData2();

        BarChart barChart2 = findViewById(R.id.barChart2);

        ArrayList<BarEntry> sales2 = new ArrayList<>();
        for (int i = 0; i < list2.size(); i++) {
            Float y = Float.parseFloat(list2.get(i));
            sales2.add(new BarEntry(i + 1, y));
        }

        BarDataSet barDataSet2 = new BarDataSet(sales2, "Customer");
        barDataSet2.setColors(ColorTemplate.JOYFUL_COLORS);
        barDataSet2.setValueTextColor(Color.BLACK);
        barDataSet2.setValueTextSize(12f);

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

        //top 10 sales by product (Amt)
        getData3();

        BarChart barChart3 = findViewById(R.id.barChart3);

        ArrayList<BarEntry> productAmt = new ArrayList<>();
        for (int i = 0; i < list3.size(); i++) {

            Float y = Float.parseFloat(list3.get(i));
            productAmt.add(new BarEntry(i + 1, y));
        }

        BarDataSet barDataSet3 = new BarDataSet(productAmt, "Product");
        barDataSet3.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet3.setValueTextColor(Color.BLACK);
        barDataSet3.setValueTextSize(12f);

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
        getData4();

        BarChart barChart4 = findViewById(R.id.barChart4);

        ArrayList<BarEntry> productAmt2 = new ArrayList<>();
        for (int i = 0; i < list4.size(); i++) {

            Float y = Float.parseFloat(list4.get(i));
            productAmt2.add(new BarEntry(i + 1, y));
        }

        BarDataSet barDataSet4 = new BarDataSet(productAmt2, "Product");
        barDataSet4.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet4.setValueTextColor(Color.BLACK);
        barDataSet4.setValueTextSize(12f);

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

        //top 10 sales by product (Qty)
        getData5();

        BarChart barChart5 = findViewById(R.id.barChart5);

        ArrayList<BarEntry> balanceAmt = new ArrayList<>();
        for (int i = 0; i < list5.size(); i++) {

            Float y = Float.parseFloat(list5.get(i));
            balanceAmt.add(new BarEntry(i + 1, y));
        }

        BarDataSet barDataSet5 = new BarDataSet(balanceAmt, "Product");
        barDataSet5.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet5.setValueTextColor(Color.BLACK);
        barDataSet5.setValueTextSize(12f);

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


        getData6();

        BarChart barChart6 = findViewById(R.id.barChart6);

        ArrayList<BarEntry> balanceQty = new ArrayList<>();
        for (int i = 0; i < list6.size(); i++) {

            Float y = Float.parseFloat(list6.get(i));
            balanceQty.add(new BarEntry(i + 1, y));
        }

        BarDataSet barDataSet6 = new BarDataSet(balanceQty, "Product");
        barDataSet6.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet6.setValueTextColor(Color.BLACK);
        barDataSet6.setValueTextSize(12f);

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
    }


    public void getData() {
        Cursor data;
        data = db.getTopSales();

        if (data.getCount() > 0) {
            int j = 0;
            while (data.moveToNext()) {
                try {

                    dnArray[j] = data.getString(data.getColumnIndex("DebtorName"));
                    list.add(data.getString(data.getColumnIndex("Total")));
                    j++;
                    if (j == 10) {
                        break;
                    }
                } catch (Exception e) {
                    Log.i("custDebug", "error reading image: " + e.getMessage());
                }
            }
        }


    }

    public void getData2() {
        Cursor data;
        data = db.getTop10DebtorQty();

        if (data.getCount() > 0) {
            int j = 0;
            while (data.moveToNext()) {
                try {

                    dnArray2[j] = data.getString(data.getColumnIndex("DebtorName"));
                    list2.add(data.getString(data.getColumnIndex("TotalQty")));
                    j++;
                    if (j == 10) {
                        break;
                    }
                } catch (Exception e) {
                    Log.i("custDebug", "error reading image: " + e.getMessage());
                }
            }
        }


    }


    public void getData3() {
        Cursor data;
        data = db.getTop10ItemsBySales();


        if (data.getCount() > 0) {
            int j = 0;
            while (data.moveToNext()) {
                try {

                    itemArray[j] = data.getString(data.getColumnIndex("ItemCode"));
                    list3.add(data.getString(data.getColumnIndex("Total")));
                    j++;
                    if (j == 10) {
                        break;
                    }
                } catch (Exception e) {
                    Log.i("custDebug", "error reading image: " + e.getMessage());
                }
            }
        }
    }

    public void getData4() {
        Cursor data;
        data = db.getTop10ItemsByQty();

        if (data.getCount() > 0) {
            int j = 0;
            while (data.moveToNext()) {
                try {

                    itemArray2[j] = data.getString(data.getColumnIndex("ItemCode"));
                    list4.add(data.getString(data.getColumnIndex("Total")));
                    j++;
                    if (j == 10) {
                        break;
                    }
                } catch (Exception e) {
                    Log.i("custDebug", "error reading image: " + e.getMessage());
                }
            }
        }
    }


    public void getData5() {
        Cursor data;
        data = db.getTopStockBalance();

        if (data.getCount() > 0) {
            int j = 0;
            while (data.moveToNext()) {
                try {

                    itemArray3[j] = data.getString(data.getColumnIndex("ItemCode"));
                    list5.add(data.getString(data.getColumnIndex("BalQty")));
                    j++;
                    if (j == 10) {
                        break;
                    }
                } catch (Exception e) {
                    Log.i("custDebug", "error reading image: " + e.getMessage());
                }
            }
        }
    }

    public void getData6() {
        Cursor data;
        data = db.getLowStockBalance();

        if (data.getCount() > 0) {
            int j = 0;
            while (data.moveToNext()) {
                try {

                    itemArray4[j] = data.getString(data.getColumnIndex("ItemCode"));
                    list6.add(data.getString(data.getColumnIndex("BalQty")));
                    j++;
                    if (j == 10) {
                        break;
                    }
                } catch (Exception e) {
                    Log.i("custDebug", "error reading image: " + e.getMessage());
                }
            }
        }
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

    public class DayAxisValueFormatter7 extends ValueFormatter {
        private final BarLineChartBase<?> chart;

        public DayAxisValueFormatter7(BarLineChartBase<?> chart) {
            this.chart = chart;
        }

        @Override
        public String getFormattedValue(float value) {
            int value_int = (int) (value -1);
            return String.valueOf(dnArray3[(int) value_int]);
        }
    }

    public void getDataSales(String substring) {
        Cursor data = db.getInvoiceMenuLike(substring);
        List<AC_Class.InvoiceMenu> invoiceMenus = new ArrayList<>();
        //SharedPreferences prefs = getSharedPreferences("com.presoft.androidmobilestockCSY", Context.MODE_PRIVATE);

        String todaydate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        String yesterday = getPreviousDate(new Date());
        Double sales=0.0;
        Double y_sales=0.0;
        if (data.getCount() > 0)
        {
//            Log.i("custDebug", "debug");
            while (data.moveToNext()) {
                AC_Class.InvoiceMenu invoiceMenu = new AC_Class.InvoiceMenu(data.getString(0), data.getString(1), data.getString(2), data.getString(3), data.getString(4), data.getDouble(8));
//                Log.i("custDebug", invoiceMenu.getDebtorName()+", "+invoiceMenu.getDocNo());
                if (todaydate.equals(data.getString(1))){
                    sales+=data.getDouble(8);
                }
                if (yesterday.equals(data.getString(1))){
                    y_sales+=data.getDouble(8);
                }
                invoiceMenus.add(invoiceMenu);
            }
        }

        Cursor cur = db.getReg("6");
        if(cur.moveToFirst()){
            cur.getString(0);
        }

        modelArrayList.add(new AnalyticModel(
                "Yesterday", "Today", ytdDate, tdyDate, Default_curr + " " + String.format("%.2f", y_sales), Default_curr + " " + String.format("%.2f", sales)
        ));
    }


    public void getMonthlySales(String substring) {
        Cursor data = db.getInvoiceMenuLike(substring);
        List<AC_Class.InvoiceMenu> invoiceMenus = new ArrayList<>();
        //SharedPreferences prefs = getSharedPreferences("com.presoft.androidmobilestockCSY", Context.MODE_PRIVATE);

        String todaydate = new SimpleDateFormat("MM").format(new Date());
        String last_mm = getPreviousMonth(new Date());
        Double sales=0.0;
        Double lastmonth_sales=0.0;
        Double totalsales=0.0;
        String month = "";
        String lastmonth = "";
        if (data.getCount() > 0)
        {
//            Log.i("custDebug", "debug");
            while (data.moveToNext()) {
                AC_Class.InvoiceMenu invoiceMenu = new AC_Class.InvoiceMenu(data.getString(0), data.getString(1), data.getString(2), data.getString(3), data.getString(4), data.getDouble(8));
//                Log.i("custDebug", invoiceMenu.getDebtorName()+", "+invoiceMenu.getDocNo());

                String date = data.getString(1);

                try {
                    Date date1=new SimpleDateFormat("dd/MM/yyyy").parse(date);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date1);
                    month = checkDigit(cal.get(Calendar.MONTH)+1);

                } catch (ParseException e) {
                    e.printStackTrace();
                }


                if (todaydate.equals(month)){
                    sales+=data.getDouble(8);
                }
                if(last_mm.equals(lastmonth)){
                    lastmonth_sales +=data.getDouble(8);
                }


                invoiceMenus.add(invoiceMenu);
            }
        }

        Cursor cur = db.getReg("6");
        if(cur.moveToFirst()){
            cur.getString(0);
        }
        modelArrayList.add(new AnalyticModel(
                "Last Month", "This Month", lastmonthdate, thismonth, Default_curr + " " + String.format("%.2f", lastmonth_sales), Default_curr + " " + String.format("%.2f", sales)
        ));
    }

    public void getYearlySales(String substring) {
        Cursor data = db.getInvoiceMenuLike(substring);
        List<AC_Class.InvoiceMenu> invoiceMenus = new ArrayList<>();

        String thisyear = new SimpleDateFormat("yyyy").format(new Date());
        String last_yy = getPreviousYear(new Date());
        Double sales=0.0;
        Double lastyear_sales=0.0;
        String year = "";
        String lastyear = "";
        if (data.getCount() > 0)
        {
            while (data.moveToNext()) {
                AC_Class.InvoiceMenu invoiceMenu = new AC_Class.InvoiceMenu(data.getString(0), data.getString(1), data.getString(2), data.getString(3), data.getString(4), data.getDouble(8));
//                Log.i("custDebug", invoiceMenu.getDebtorName()+", "+invoiceMenu.getDocNo());
                String date = data.getString(1);

                try {
                    Date date1=new SimpleDateFormat("dd/MM/yyyy").parse(date);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date1);
                    year = checkDigit(cal.get(Calendar.YEAR));
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                if (thisyear.equals(year)){
                    sales+=data.getDouble(8);
                }
                if(last_yy.equals(year)){
                    lastyear_sales +=data.getDouble(8);
                }


                invoiceMenus.add(invoiceMenu);
            }
        }

        Cursor cur = db.getReg("6");
        if(cur.moveToFirst()){
            cur.getString(0);
        }

        modelArrayList.add(new AnalyticModel(
                "Last Year", "This Year", lastyeardate, thisyear, Default_curr + " " + String.format("%.2f", lastyear_sales), Default_curr + " " + String.format("%.2f", sales)
        ));
    }

    public String checkDigit (int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    private static String getPreviousMonth(Date date){
        final SimpleDateFormat format = new SimpleDateFormat("MM");

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.DATE, -1);

        Date preMonthDate = cal.getTime();
        return format.format(preMonthDate);
    }

    private static String getPreviousDate(Date date){
        final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, -1);

        Date preMonthDate = cal.getTime();
        return format.format(preMonthDate);
    }

    private static String getPreviousYear(Date date){
        final SimpleDateFormat format = new SimpleDateFormat("yyyy");

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.YEAR, -1);

        Date preMonthDate = cal.getTime();
        return format.format(preMonthDate);
    }


    class GetAllAgentSales_ytd extends AsyncTask<String, Void, Boolean> {
        Activity context;
        ProgressDialog pd;

        GetAllAgentSales_ytd(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... connUrl) {
            return getSalesOrderHeader(connUrl[0]);
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);

            if (!bool)
                Snackbar.make(this.context.findViewById(android.R.id.content), "Server connection failed.", Snackbar.LENGTH_SHORT).show();
            new GetAllAgentSales_tdy(Analytics.this).execute(url);
        }
    }

    private Boolean getSalesOrderHeader(String connUrl)
    {
        Boolean result = false;

        HttpURLConnection conn;
        BufferedReader reader;
        try {
            URL url = new URL(connUrl + "/getAllAgentSales/0");
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestMethod("GET");

            // Receive chunk of data
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            String status = sb.toString();

            if(status != null)
            {
                JSONArray location = new JSONArray(status);

                for (int i = 0; i < location.length(); i++) {
                    JSONObject object = location.getJSONObject(i);
                    ytdSales = object.getDouble("Total");
                }

            }
            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i("custDebug", ex.getMessage());
        }
        return result;
    }

    class GetAllAgentSales_tdy extends AsyncTask<String, Void, Boolean> {
        Activity context;

        GetAllAgentSales_tdy(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(String... connUrl) {
            return getSalesOrderHeader_tdy(connUrl[0]);
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);


            if (!bool)
                Snackbar.make(this.context.findViewById(android.R.id.content), "Server connection failed.", Snackbar.LENGTH_SHORT).show();
            new GetAllAgentSales_lmm(Analytics.this).execute(url);
        }
    }

    private Boolean getSalesOrderHeader_tdy(String connUrl)
    {
        Boolean result = false;

        HttpURLConnection conn;
        BufferedReader reader;
        try {
            URL url = new URL(connUrl + "/getAllAgentSales/1");
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestMethod("GET");

            // Receive chunk of data
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            String status = sb.toString();

            if(status != null)
            {
                JSONArray location = new JSONArray(status);

                for (int i = 0; i < location.length(); i++) {
                    JSONObject object = location.getJSONObject(i);
                    tdySales = object.getDouble("Total");
                }
            }
            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i("custDebug", ex.getMessage());
        }
        return result;
    }

    class GetAllAgentSales_lmm extends AsyncTask<String, Void, Boolean> {
        Activity context;
        ProgressDialog pd;

        GetAllAgentSales_lmm(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... connUrl) {
            return getSalesOrderHeader_lmm(connUrl[0]);
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);

            if (!bool)
                Snackbar.make(this.context.findViewById(android.R.id.content), "Server connection failed.", Snackbar.LENGTH_SHORT).show();

            new GetAllAgentSales_tmm(Analytics.this).execute(url);
        }
    }

    private Boolean getSalesOrderHeader_lmm(String connUrl)
    {
        Boolean result = false;

        HttpURLConnection conn;
        BufferedReader reader;
        try {
            URL url = new URL(connUrl + "/getAllAgentSales/2");
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestMethod("GET");

            // Receive chunk of data
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            String status = sb.toString();

            if(status != null)
            {
                JSONArray location = new JSONArray(status);

                for (int i = 0; i < location.length(); i++) {
                    JSONObject object = location.getJSONObject(i);
                    lmSales = object.getDouble("Total");
                }

            }
            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i("custDebug", ex.getMessage());
        }
        return result;
    }

    class GetAllAgentSales_tmm extends AsyncTask<String, Void, Boolean> {
        Activity context;
        ProgressDialog pd;

        GetAllAgentSales_tmm(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(String... connUrl) {
            return getSalesOrderHeader_tmm(connUrl[0]);
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);


            if (!bool)
                Snackbar.make(this.context.findViewById(android.R.id.content), "Server connection failed.", Snackbar.LENGTH_SHORT).show();
            new GetAllAgentSales_lyy(Analytics.this).execute(url);
        }
    }

    private Boolean getSalesOrderHeader_tmm(String connUrl)
    {
        Boolean result = false;

        HttpURLConnection conn;
        BufferedReader reader;
        try {
            URL url = new URL(connUrl + "/getAllAgentSales/3");
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestMethod("GET");

            // Receive chunk of data
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            String status = sb.toString();

            if(status != null)
            {
                JSONArray location = new JSONArray(status);

                for (int i = 0; i < location.length(); i++) {
                    JSONObject object = location.getJSONObject(i);
                    tmSales = object.getDouble("Total");
                }

            }
            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i("custDebug", ex.getMessage());
        }
        return result;
    }

    class GetAllAgentSales_lyy extends AsyncTask<String, Void, Boolean> {
        Activity context;
        ProgressDialog pd;

        GetAllAgentSales_lyy(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... connUrl) {
            return getSalesOrderHeader_lyy(connUrl[0]);
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);

            if (!bool)
                Snackbar.make(this.context.findViewById(android.R.id.content), "Server connection failed.", Snackbar.LENGTH_SHORT).show();

            new GetAllAgentSales_tyy(Analytics.this).execute(url);
        }
    }

    private Boolean getSalesOrderHeader_lyy(String connUrl)
    {
        Boolean result = false;

        HttpURLConnection conn;
        BufferedReader reader;
        try {
            URL url = new URL(connUrl + "/getAllAgentSales/4");
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestMethod("GET");

            // Receive chunk of data
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            String status = sb.toString();

            if(status != null)
            {
                JSONArray location = new JSONArray(status);

                for (int i = 0; i < location.length(); i++) {
                    JSONObject object = location.getJSONObject(i);
                    lySales = object.getDouble("Total");
                }
            }
            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i("custDebug", ex.getMessage());
        }
        return result;
    }

    class GetAllAgentSales_tyy extends AsyncTask<String, Void, Boolean> {
        Activity context;
        ProgressDialog pd;

        GetAllAgentSales_tyy(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... connUrl) {
            return getSalesOrderHeader_tyy(connUrl[0]);
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);


            modelArrayList = new ArrayList<>();

            modelArrayList.add(new AnalyticModel(
                    "Yesterday", "Today", ytdDate, tdyDate, Default_curr + " " + String.format("%.2f", ytdSales), Default_curr + " " + String.format("%.2f", tdySales)
            ));
            modelArrayList.add(new AnalyticModel(
                    "Last Month", "This Month", lastmonthdate, thismonth, Default_curr + " " + String.format("%.2f", lmSales), Default_curr + " " + String.format("%.2f", tmSales)
            ));

            modelArrayList.add(new AnalyticModel(
                    "Last Year", "This Year", lastyeardate, thisyear, Default_curr + " " + String.format("%.2f", lySales), Default_curr + " " + String.format("%.2f", tySales)
            ));
            setAdapter();
            if (!bool)
                Snackbar.make(this.context.findViewById(android.R.id.content), "Server connection failed.", Snackbar.LENGTH_SHORT).show();
        }
    }

    private Boolean getSalesOrderHeader_tyy(String connUrl)
    {
        Boolean result = false;

        HttpURLConnection conn;
        BufferedReader reader;
        try {
            URL url = new URL(connUrl + "/getAllAgentSales/5");
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestMethod("GET");

            // Receive chunk of data
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            String status = sb.toString();

            if(status != null)
            {
                JSONArray location = new JSONArray(status);

                for (int i = 0; i < location.length(); i++) {
                    JSONObject object = location.getJSONObject(i);
                    tySales = object.getDouble("Total");
                }
            }
            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i("custDebug", ex.getMessage());
        }
        return result;
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    class GetGraph_CusAmt extends AsyncTask<String, Void, Boolean> {
        Activity context;
        ProgressDialog pd;

        GetGraph_CusAmt(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... connUrl) {
            return getGetGraph_ytd(connUrl[0]);
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);

            BarChart barChart = findViewById(R.id.barChart);

            ArrayList<BarEntry> sales = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                Float y = Float.parseFloat(list.get(i));
                sales.add(new BarEntry(i + 1, y));
            }

            BarDataSet barDataSet = new BarDataSet(sales, "Sales");
            barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
            barDataSet.setValueTextColor(Color.BLACK);
            barDataSet.setValueTextSize(12f);

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

            if (!bool)
                Snackbar.make(this.context.findViewById(android.R.id.content), "Server connection failed.", Snackbar.LENGTH_SHORT).show();
        }
    }

    private Boolean getGetGraph_ytd(String connUrl)
    {
        Boolean result = false;

        HttpURLConnection conn;
        BufferedReader reader;
        try {
            URL url = new URL(connUrl + "/getGraphDebtor/0");
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestMethod("GET");

            // Receive chunk of data
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            String status = sb.toString();

            if(status != null)
            {
                JSONArray location = new JSONArray(status);

                for (int i = 0; i < location.length(); i++) {
                    JSONObject object = location.getJSONObject(i);
                    dnArray[i] = object.getString("DebtorName");
                    list.add(String.valueOf(object.getDouble("Total")));
                }
            }
            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i("custDebug", ex.getMessage());
        }
        return result;
    }


    class GetGraph_CusQty extends AsyncTask<String, Void, Boolean> {
        Activity context;
        ProgressDialog pd;

        GetGraph_CusQty(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... connUrl) {
            return getGetGraph_tdy(connUrl[0]);
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);

            BarChart barChart2 = findViewById(R.id.barChart2);

            ArrayList<BarEntry> sales2 = new ArrayList<>();
            for (int i = 0; i < list2.size(); i++) {
                Float y = Float.parseFloat(list2.get(i));
                sales2.add(new BarEntry(i + 1, y));
            }

            BarDataSet barDataSet2 = new BarDataSet(sales2, "Sales");
            barDataSet2.setColors(ColorTemplate.JOYFUL_COLORS);
            barDataSet2.setValueTextColor(Color.BLACK);
            barDataSet2.setValueTextSize(12f);

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

            if (!bool)
                Snackbar.make(this.context.findViewById(android.R.id.content), "Server connection failed.", Snackbar.LENGTH_SHORT).show();
        }
    }

    private Boolean getGetGraph_tdy(String connUrl)
    {
        Boolean result = false;

        HttpURLConnection conn;
        BufferedReader reader;
        try {
            URL url = new URL(connUrl + "/getGraphDebtor/1");
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestMethod("GET");

            // Receive chunk of data
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            String status = sb.toString();

            if(status != null)
            {
                JSONArray location = new JSONArray(status);

                for (int i = 0; i < location.length(); i++) {
                    JSONObject object = location.getJSONObject(i);
                    dnArray2[i] = object.getString("DebtorName");
                    list2.add(String.valueOf(object.getDouble("Total")));
                }
            }
            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i("custDebug", ex.getMessage());
        }
        return result;
    }


    class GetGraph_ProAmt extends AsyncTask<String, Void, Boolean> {
        Activity context;
        ProgressDialog pd;

        GetGraph_ProAmt(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... connUrl) {
            return getGetGraph_ProAmt(connUrl[0]);
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);

            BarChart barChart3 = findViewById(R.id.barChart3);

            ArrayList<BarEntry> productAmt = new ArrayList<>();
            for (int i = 0; i < list3.size(); i++) {

                Float y = Float.parseFloat(list3.get(i));
                productAmt.add(new BarEntry(i + 1, y));
            }

            BarDataSet barDataSet3 = new BarDataSet(productAmt, "Sales");
            barDataSet3.setColors(ColorTemplate.MATERIAL_COLORS);
            barDataSet3.setValueTextColor(Color.BLACK);
            barDataSet3.setValueTextSize(12f);

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

            if (!bool)
                Snackbar.make(this.context.findViewById(android.R.id.content), "Server connection failed.", Snackbar.LENGTH_SHORT).show();
        }
    }

    private Boolean getGetGraph_ProAmt(String connUrl)
    {
        Boolean result = false;

        HttpURLConnection conn;
        BufferedReader reader;
        try {
            URL url = new URL(connUrl + "/getGraphItem/0");
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestMethod("GET");

            // Receive chunk of data
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            String status = sb.toString();

            if(status != null)
            {
                JSONArray location = new JSONArray(status);

                for (int i = 0; i < location.length(); i++) {
                    JSONObject object = location.getJSONObject(i);
                    itemArray[i] = object.getString("ItemCode");
                    list3.add(String.valueOf(object.getDouble("Total")));
                }
            }
            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i("custDebug", ex.getMessage());
        }
        return result;
    }

    class GetGraph_ProQty extends AsyncTask<String, Void, Boolean> {
        Activity context;
        ProgressDialog pd;

        GetGraph_ProQty(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... connUrl) {
            return getGetGraph_ProQty(connUrl[0]);
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);


            BarChart barChart4 = findViewById(R.id.barChart4);

            ArrayList<BarEntry> productAmt2 = new ArrayList<>();
            for (int i = 0; i < list4.size(); i++) {

                Float y = Float.parseFloat(list4.get(i));
                productAmt2.add(new BarEntry(i + 1, y));
            }

            BarDataSet barDataSet4 = new BarDataSet(productAmt2, "Sales");
            barDataSet4.setColors(ColorTemplate.MATERIAL_COLORS);
            barDataSet4.setValueTextColor(Color.BLACK);
            barDataSet4.setValueTextSize(12f);

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

            if (!bool)
                Snackbar.make(this.context.findViewById(android.R.id.content), "Server connection failed.", Snackbar.LENGTH_SHORT).show();
        }
    }

    private Boolean getGetGraph_ProQty(String connUrl)
    {
        Boolean result = false;

        HttpURLConnection conn;
        BufferedReader reader;
        try {
            URL url = new URL(connUrl + "/getGraphItem/1");
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestMethod("GET");

            // Receive chunk of data
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            String status = sb.toString();

            if(status != null)
            {
                JSONArray location = new JSONArray(status);

                for (int i = 0; i < location.length(); i++) {
                    JSONObject object = location.getJSONObject(i);
                    itemArray2[i] = object.getString("ItemCode");
                    list4.add(String.valueOf(object.getDouble("Total")));
                }
            }
            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i("custDebug", ex.getMessage());
        }
        return result;
    }


    class GetGraph_HighBal extends AsyncTask<String, Void, Boolean> {
        Activity context;
        ProgressDialog pd;

        GetGraph_HighBal(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... connUrl) {
            return getGetGraph_HighBal(connUrl[0]);
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);

            BarChart barChart5 = findViewById(R.id.barChart5);

            ArrayList<BarEntry> balanceAmt = new ArrayList<>();
            for (int i = 0; i < list5.size(); i++) {

                Float y = Float.parseFloat(list5.get(i));
                balanceAmt.add(new BarEntry(i + 1, y));
            }

            BarDataSet barDataSet5 = new BarDataSet(balanceAmt, "Sales");
            barDataSet5.setColors(ColorTemplate.COLORFUL_COLORS);
            barDataSet5.setValueTextColor(Color.BLACK);
            barDataSet5.setValueTextSize(12f);

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

            if (!bool)
                Snackbar.make(this.context.findViewById(android.R.id.content), "Server connection failed.", Snackbar.LENGTH_SHORT).show();
        }
    }

    private Boolean getGetGraph_HighBal(String connUrl)
    {
        Boolean result = false;

        HttpURLConnection conn;
        BufferedReader reader;
        try {
            URL url = new URL(connUrl + "/getGraphItem/2");
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestMethod("GET");

            // Receive chunk of data
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            String status = sb.toString();

            if(status != null)
            {
                JSONArray location = new JSONArray(status);

                for (int i = 0; i < location.length(); i++) {
                    JSONObject object = location.getJSONObject(i);
                    itemArray3[i] = object.getString("ItemCode");
                    list5.add(String.valueOf(object.getDouble("Total")));
                }
            }
            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i("custDebug", ex.getMessage());
        }
        return result;
    }


    class GetGraph_LowBal extends AsyncTask<String, Void, Boolean> {
        Activity context;
        ProgressDialog pd;

        GetGraph_LowBal(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... connUrl) {
            return getGetGraph_LowBal(connUrl[0]);
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);

            BarChart barChart6 = findViewById(R.id.barChart6);

            ArrayList<BarEntry> balanceQty = new ArrayList<>();
            for (int i = 0; i < list6.size(); i++) {

                Float y = Float.parseFloat(list6.get(i));
                balanceQty.add(new BarEntry(i + 1, y));
            }

            BarDataSet barDataSet6 = new BarDataSet(balanceQty, "Sales");
            barDataSet6.setColors(ColorTemplate.COLORFUL_COLORS);
            barDataSet6.setValueTextColor(Color.BLACK);
            barDataSet6.setValueTextSize(12f);

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

            if (!bool)
                Snackbar.make(this.context.findViewById(android.R.id.content), "Server connection failed.", Snackbar.LENGTH_SHORT).show();
        }
    }

    private Boolean getGetGraph_LowBal(String connUrl)
    {
        Boolean result = false;

        HttpURLConnection conn;
        BufferedReader reader;
        try {
            URL url = new URL(connUrl + "/getGraphItem/3");
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestMethod("GET");

            // Receive chunk of data
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            String status = sb.toString();

            if(status != null)
            {
                JSONArray location = new JSONArray(status);

                for (int i = 0; i < location.length(); i++) {
                    JSONObject object = location.getJSONObject(i);
                    itemArray4[i] = object.getString("ItemCode");
                    list6.add(String.valueOf(object.getDouble("Total")));
                }
            }
            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i("custDebug", ex.getMessage());
        }
        return result;
    }


    class GetGraph_TopAgent extends AsyncTask<String, Void, Boolean> {
        Activity context;
        ProgressDialog pd;

        GetGraph_TopAgent(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... connUrl) {
            return getGetGraph_topagent(connUrl[0]);
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);


            BarChart barChart2 = findViewById(R.id.barChartAgent);

            ArrayList<BarEntry> sales2 = new ArrayList<>();
            for (int i = 0; i < list7.size(); i++) {
                Float y = Float.parseFloat(list7.get(i));
                sales2.add(new BarEntry(i + 1, y));
            }

            BarDataSet barDataSet2 = new BarDataSet(sales2, "Sales");
            barDataSet2.setColors(ColorTemplate.PASTEL_COLORS);
            barDataSet2.setValueTextColor(Color.BLACK);
            barDataSet2.setValueTextSize(12f);

            BarData barData2 = new BarData(barDataSet2);
            barChart2.setFitBars(true);
            barChart2.setData(barData2);
            barChart2.animateY(2000);
            barChart2.setDescription(null);

            ValueFormatter xAxisFormatter2 = new DayAxisValueFormatter7(barChart2);
            XAxis xAxis2 = barChart2.getXAxis();
            xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis2.setDrawGridLines(false);
            xAxis2.setGranularity(1f); // only intervals of 1 day
            xAxis2.setLabelCount(7);
            xAxis2.setValueFormatter(xAxisFormatter2);

            if (!bool)
                Snackbar.make(this.context.findViewById(android.R.id.content), "Server connection failed.", Snackbar.LENGTH_SHORT).show();
        }
    }

    private Boolean getGetGraph_topagent(String connUrl)
    {
        Boolean result = false;

        HttpURLConnection conn;
        BufferedReader reader;
        try {
            URL url = new URL(connUrl + "/getGraphDebtor/2");
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestMethod("GET");

            // Receive chunk of data
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            String status = sb.toString();

            if(status != null)
            {
                JSONArray location = new JSONArray(status);

                for (int i = 0; i < location.length(); i++) {
                    JSONObject object = location.getJSONObject(i);
                    dnArray3[i] = object.getString("DebtorName");
                    list7.add(String.valueOf(object.getDouble("Total")));
                }
            }
            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i("custDebug", ex.getMessage());
        }
        return result;
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

    private void getModules() {
        Cursor data = db.getModules();
        if (data.getCount() > 0) {
            while (data.moveToNext()) {
                myModules.add(data.getString(data.getColumnIndex("Name")));
            }
        }
        data.close();
    }
}