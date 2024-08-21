package com.example.androidmobilestock;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class Jobsheet_Fragment3 extends Fragment {

    private static final String ARG_DOC_NO = "docNo";
    private static final String ARG_DEBTOR_CODE = "debtorCode";

    private String docNo;
    private String debtorCode;
    TextView tv_workType;
    ACDatabase db;

    public static Jobsheet_Fragment3 newInstance(String docNo, String debtorCode) {
        Jobsheet_Fragment3 fragment = new Jobsheet_Fragment3();
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



        return view;
    }
}