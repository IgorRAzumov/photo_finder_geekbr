package ru.geekbrains.photofinder.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vk.sdk.api.model.VKPhotoArray;

import ru.geekbrains.photofinder.R;
import ru.geekbrains.photofinder.adapters.ViewPagerResultAdapter;


public class ResultViewPagerFragment extends Fragment {
    private ViewPager resultViewPager;
    private ViewPagerResultAdapter resultAdapter;
    private onActivityCallback listener;
    private int position;
    public ResultViewPagerFragment() {

    }

    public static ResultViewPagerFragment newInstance(String vkArrayKey, VKPhotoArray vkPhotoArray,
                                                      String positionKey, int position) {
        ResultViewPagerFragment resultViewPagerFragment = new ResultViewPagerFragment();
        Bundle args = new Bundle();
        args.putParcelable(vkArrayKey, vkPhotoArray);
        args.putInt(positionKey, position);
        resultViewPagerFragment.setArguments(args);
        return resultViewPagerFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            VKPhotoArray vkApiPhotos = bundle.getParcelable(getString(R.string.vk_photo_array_bundle_key));
            position = bundle.getInt(getString(R.string.vk_photo_array_position_bundle_key));
            resultAdapter = new ViewPagerResultAdapter(getFragmentManager(), vkApiPhotos, getContext());
        } else {
            throw new RuntimeException(getString(R.string.inbox_fragment_argument_error) +
                    this.getClass().getSimpleName());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_pager_result, container, false);
        resultViewPager = view.findViewById(R.id.vp_result_photos_search);
        resultViewPager.setAdapter(resultAdapter);
        resultViewPager.setCurrentItem(position);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onActivityCallback) {
            listener = (onActivityCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + getString(R.string.on_activity_callback__fragment_error) +
                    this.getClass().getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface onActivityCallback {

    }


}
