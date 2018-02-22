package ru.geekbrains.photofinder.asyncTaskLoaders;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKPhotoArray;

import ru.geekbrains.photofinder.R;
import ru.geekbrains.photofinder.utils.NetworkUtils;


public class PhotoSearchVkLoader extends AsyncTaskLoader<VKPhotoArray> {
    private VKPhotoArray vkPhotoArray = null;
    private double longitude;
    private double latitude;

    public PhotoSearchVkLoader(Context context, Bundle bundle) {
        super(context);
        if (bundle != null) {
            longitude = bundle.getDouble(context.getString(R.string.photo_loader_bundle_key_longitude));
            latitude = bundle.getDouble(context.getString(R.string.photo_loader_bundle_key_latitude));
        }
    }

    @Override
    protected void onStartLoading() {
        if (longitude == 0 && latitude == 0) {
            return;
        }

        if (vkPhotoArray != null) {
            deliverResult(vkPhotoArray);
        } else {
            forceLoad();
        }
    }


    @Override
    public VKPhotoArray loadInBackground() {
        VKResponse response = NetworkUtils.getPhotos(getContext(), latitude, longitude);
        if (response != null) {
            vkPhotoArray = (VKPhotoArray) response.parsedModel;

        }
        return vkPhotoArray;
    }

    @Override
    public void deliverResult(VKPhotoArray data) {
        vkPhotoArray = data;
        super.deliverResult(data);
    }
}
