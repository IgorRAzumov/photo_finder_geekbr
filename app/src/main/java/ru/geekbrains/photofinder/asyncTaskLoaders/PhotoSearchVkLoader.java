package ru.geekbrains.photofinder.asyncTaskLoaders;


import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.AsyncTaskLoader;
import android.text.format.DateUtils;

import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKPhotoArray;

import java.text.ParseException;

import ru.geekbrains.photofinder.R;
import ru.geekbrains.photofinder.utils.DateTimeUtils;
import ru.geekbrains.photofinder.utils.NetworkUtils;
import ru.geekbrains.photofinder.utils.PrefUtils;


public class PhotoSearchVkLoader extends AsyncTaskLoader<VKPhotoArray> {
    private VKPhotoArray vkPhotoArray = null;
    private double longitude;
    private double latitude;
    private int radius;
    private String startDate;
    private String endDate;
    private String sortBy;


    public PhotoSearchVkLoader(Context context, Bundle bundle) {
        super(context);
        if (bundle != null) {
            try {
                longitude = bundle.getDouble(context.getString(R.string.photo_loader_bundle_key_longitude));
                latitude = bundle.getDouble(context.getString(R.string.photo_loader_bundle_key_latitude));
                sortBy = PrefUtils.getSearchSortForPreference(context);
                radius = PrefUtils.getSearchRadiusForPreference(context);

                startDate = DateTimeUtils.convertPrefDataToUnix(
                        PrefUtils.getSearchStartForPreference(context));
                endDate = DateTimeUtils.convertPrefDataToUnix(
                        PrefUtils.getSearchStartForPreference(context));
            } catch (ParseException e) {
                e.printStackTrace();
            }

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
        VKResponse response = NetworkUtils.getPhotos(getContext(), latitude, longitude,
                startDate, endDate, sortBy, String.valueOf(radius));
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
