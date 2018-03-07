package ru.geekbrains.photofinder.asyncTaskLoaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;

import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKPhotoArray;

import ru.geekbrains.photofinder.R;
import ru.geekbrains.photofinder.utils.DateTimeUtils;
import ru.geekbrains.photofinder.utils.NetworkUtils;
import ru.geekbrains.photofinder.utils.PrefUtils;

public class PhotoSearchVkLoader extends AsyncTaskLoader<VKPhotoArray> {
    private VKPhotoArray vkPhotoArray = null;
    private String accessToken;
    private double longitude;
    private double latitude;
    private int radius;
    private String startDate;
    private String endDate;
    private String sortBy;


    public PhotoSearchVkLoader(Context context, Bundle bundle) {
        super(context);
        if (bundle != null) {
            getData(bundle, context);
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
        VKResponse response;

        if (accessToken != null && !TextUtils.isEmpty(accessToken)) {
            response = NetworkUtils.getPhotos(getContext(), latitude, longitude, startDate,
                    endDate, sortBy, String.valueOf(0), String.valueOf(radius), accessToken);

        } else {
            response = NetworkUtils.getPhotos(getContext(), latitude, longitude,
                    startDate, endDate, sortBy, String.valueOf(0), String.valueOf(radius));
        }

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

    private void getData(Bundle bundle, Context context) {
        longitude = bundle.getDouble(context.getString(R.string.photo_loader_bundle_key_longitude));
        latitude = bundle.getDouble(context.getString(R.string.photo_loader_bundle_key_latitude));
        accessToken = bundle.getString(context.getString(R.string.vk_access_token_key),
                "");

        sortBy = PrefUtils.getSearchSortForPreference(context);
        radius = PrefUtils.getSearchRadiusForPreference(context);

        try {
            startDate = PrefUtils.getSearchStartForPreference(context);
            startDate = DateTimeUtils.convertPrefDataToUnix(context, startDate);

            endDate = PrefUtils.getSearchEndForPreference(context);
            endDate = DateTimeUtils.convertPrefDataToUnix(context, endDate);

            /*if (endDateForPref == null || TextUtils.isEmpty(endDateForPref)) {
                endDate = String.valueOf(System.currentTimeMillis() / getContext().getResources()
                        .getInteger(R.integer.millisecond_in_second));
            } else {
                endDate = DateTimeUtils.convertPrefDataToUnix(context, endDateForPref);
            }*/
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (startDate == null || TextUtils.isEmpty(startDate)) {
                startDate = context.getString(R.string.pref_date_start_default_value_unix_time);
            }
            if (endDate == null || TextUtils.isEmpty(endDate)) {
                endDate = String.valueOf(System.currentTimeMillis() / context.getResources()
                        .getInteger(R.integer.millisecond_in_second));
            }
        }

    }
}
