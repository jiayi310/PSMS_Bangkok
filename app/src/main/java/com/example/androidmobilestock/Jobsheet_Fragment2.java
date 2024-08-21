package com.example.androidmobilestock;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class Jobsheet_Fragment2 extends Fragment {

    private static final String ARG_DOC_NO = "docNo";
    private static final String ARG_DEBTOR_CODE = "debtorCode";

    private String docNo;
    private String debtorCode;
    TextView tv_workType, tv_replacementType, tv_timeIn, tv_timeOut, tv_problem, tv_resolution, tv_remarks;
    ACDatabase db;

    public static Jobsheet_Fragment2 newInstance(String docNo, String debtorCode) {
        Jobsheet_Fragment2 fragment = new Jobsheet_Fragment2();
        Bundle args = new Bundle();
        args.putString(ARG_DOC_NO, docNo);
        args.putString(ARG_DEBTOR_CODE, debtorCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            docNo = getArguments().getString(ARG_DOC_NO);
            debtorCode = getArguments().getString(ARG_DEBTOR_CODE);
        }
        db = new ACDatabase(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_jobsheet_2, container, false);

        tv_workType = view.findViewById(R.id.tv_workType);
        tv_replacementType = view.findViewById(R.id.tv_replacementType);
        tv_timeIn = view.findViewById(R.id.tv_timeIn);
        tv_timeOut = view.findViewById(R.id.tv_timeOut);
        tv_problem = view.findViewById(R.id.tv_problem);
        tv_resolution = view.findViewById(R.id.tv_resolution);
        tv_remarks = view.findViewById(R.id.tv_remarks);

        loadData(docNo);

        return view;
    }

    private void loadData(String docNo) {
        Cursor cursor = db.getJobSheetByDocNo(docNo);
        if (cursor != null && cursor.moveToFirst()){
            String workType = cursor.getString(cursor.getColumnIndex("WorkType"));
            String replacementType = cursor.getString(cursor.getColumnIndex("ReplacementType"));
            String timeIn = cursor.getString(cursor.getColumnIndex("TimeIn"));
            String timeOut = cursor.getString(cursor.getColumnIndex("TimeOut"));
            String problem = cursor.getString(cursor.getColumnIndex("Problem"));
            String resolution = cursor.getString(cursor.getColumnIndex("Resolution"));
            String remarks = cursor.getString(cursor.getColumnIndex("JobSheetRemarks"));

            tv_workType.setText(workType != null ? workType : " ");
            tv_replacementType.setText(replacementType != null ? replacementType : " ");
            tv_timeIn.setText(timeIn != null ? timeIn : " ");
            tv_timeOut.setText(timeOut != null ? timeOut : " ");
            tv_problem.setText(problem != null ? problem : " ");
            tv_resolution.setText(resolution != null ? resolution : " ");
            tv_remarks.setText(remarks != null ? remarks : " ");

            cursor.close();
        } else {
            Toast.makeText(getContext(), "No record found.", Toast.LENGTH_SHORT).show();
        }
    }
}