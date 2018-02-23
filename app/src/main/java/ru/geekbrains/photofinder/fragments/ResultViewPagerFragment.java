package ru.geekbrains.photofinder.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.geekbrains.photofinder.R;
import ru.geekbrains.photofinder.adapters.PhotoResultAdapter;
import ru.geekbrains.photofinder.adapters.ViewPagerResultAdapter;


public class ResultViewPagerFragment extends Fragment {
    public interface onActivityCallback {

    }

    private ViewPager resultViewPager;
    private ViewPagerResultAdapter resultAdapter;
    private onActivityCallback mListener;

    public ResultViewPagerFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_pager_result, container, false);
        resultViewPager = view.findViewById(R.id.vp_result_photos_search);
        resultAdapter = new ViewPagerResultAdapter(getFragmentManager(), PhotoResultAdapter.photosArray);
        resultViewPager.setAdapter(resultAdapter);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onActivityCallback) {
            mListener = (onActivityCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onActivityCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


}
