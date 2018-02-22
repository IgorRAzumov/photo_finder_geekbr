package ru.geekbrains.photofinder.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import ru.geekbrains.photofinder.R;
import ru.geekbrains.photofinder.adapters.ViewPagerResultAdapter;
import ru.geekbrains.photofinder.utils.NetworkUtils;


public class PageFragment extends Fragment {
    private ImageView photoImageView;
    private String photoUrl;

    public PageFragment() {

    }

    public static PageFragment newInstance(String photoUrl) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putString(ViewPagerResultAdapter.PAGER_RESULT_PHOTO_URL_BUNDLE_KEY, photoUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            photoUrl = getArguments().getString(
                    ViewPagerResultAdapter.PAGER_RESULT_PHOTO_URL_BUNDLE_KEY, null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        photoImageView = view.findViewById(R.id.iv_pager_view_photo);

        NetworkUtils.loadImage(photoImageView,photoUrl,getContext(),300,300);
        return view;
    }

}
