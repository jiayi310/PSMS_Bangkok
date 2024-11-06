package com.example.androidmobilestock_bangkok;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.androidmobilestock_bangkok.databinding.PlFragmentMultipletabDetailBinding;

public class PPL_MultipleTab_DetailFragment extends Fragment {
    ACDatabase db;
    ListView listview;
    PlFragmentMultipletabDetailBinding binding;
    AC_Class.DO packingList;

    private OnFragmentInteractionListener mListener;

    public PPL_MultipleTab_DetailFragment() {
        // Required empty public constructor
    }

    public static PPL_MultipleTab_DetailFragment newInstance(Bundle myBundle) {
        PPL_MultipleTab_DetailFragment fragment = new PPL_MultipleTab_DetailFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.pl_fragment_multipletab_detail, container, false);
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
            PL_PLDtlListViewAdapter adapter = new PL_PLDtlListViewAdapter(getActivity(), packingList.getDODtlList());
            binding.lvDetail.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

}
