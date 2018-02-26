package ru.geekbrains.photofinder.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.vk.sdk.api.model.VKApiPhoto;

import ru.geekbrains.photofinder.R;
import ru.geekbrains.photofinder.utils.NetworkUtils;


public class PageFragment extends Fragment {
    private ImageView photoImageView;
    private VKApiPhoto vkApiPhoto;

    public PageFragment() {

    }

    public static PageFragment newInstance(String vkApiBundleKey, VKApiPhoto vkApiPhoto) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putParcelable(vkApiBundleKey, vkApiPhoto);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            vkApiPhoto = arguments.getParcelable(getString(R.string.vk_photo_bundle_key));
        } else {
            throw new RuntimeException(getString(R.string.inbox_fragment_argument_error)
                    + this.getClass().getSimpleName());
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        photoImageView = view.findViewById(R.id.iv_pager_view_photo);

        String photoUrl = getPhotoUrl();
        if (photoUrl != null && !photoUrl.isEmpty()) {
            NetworkUtils.loadImage(photoImageView, photoUrl, getContext(),
                    300, 300);
        } else {
            showErrorMessage(getString(R.string.error_no_photo_url_found));
        }

        return view;
    }


    private String getPhotoUrl() {
        String tempPhotoUrl = vkApiPhoto.photo_807;
        if (tempPhotoUrl == null || tempPhotoUrl.isEmpty()) {
            tempPhotoUrl = vkApiPhoto.photo_1280;
        }

        if (tempPhotoUrl != null && tempPhotoUrl.isEmpty()) {
            tempPhotoUrl = vkApiPhoto.photo_604;
        }
        return tempPhotoUrl;
    }

    private void showErrorMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}
