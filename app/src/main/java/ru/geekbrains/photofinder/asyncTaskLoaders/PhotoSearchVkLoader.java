package ru.geekbrains.photofinder.asyncTaskLoaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;

import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKPhotoArray;

import org.json.JSONException;
import org.json.JSONObject;

import ru.geekbrains.photofinder.R;
import ru.geekbrains.photofinder.data.PhotosResponse;
import ru.geekbrains.photofinder.utils.DateTimeUtils;
import ru.geekbrains.photofinder.utils.NetworkUtils;
import ru.geekbrains.photofinder.utils.PrefUtils;

public class PhotoSearchVkLoader extends AsyncTaskLoader<PhotosResponse> {
    private PhotosResponse mapResponse;

    private double latitude;
    private double longitude;
    private String startDate;
    private String endDate;
    private String sortBy;
    private int offset;
    private int radius;
    private String accessToken;


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

        if (mapResponse != null) {
            deliverResult(mapResponse);
        } else {
            forceLoad();
        }
    }

    @Override
    public PhotosResponse loadInBackground() {
        VKResponse response;

        if (accessToken != null && !TextUtils.isEmpty(accessToken)) {
            response = NetworkUtils.getPhotos(getContext(), latitude, longitude, startDate,
                    endDate, sortBy, String.valueOf(offset),
                    String.valueOf(getContext().getResources().getInteger(R.integer.vk_api_const_count)),
                    String.valueOf(radius), accessToken);

        } else {
            response = NetworkUtils.getPhotos(getContext(), latitude, longitude,
                    startDate, endDate, sortBy, String.valueOf(offset),
                    String.valueOf(getContext().getResources().getInteger(R.integer.vk_api_const_count)),
                    String.valueOf(radius));
        }

        if (response != null) {
            try {
                return mapResponseToPhotosResponse(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private PhotosResponse mapResponseToPhotosResponse(VKResponse response) throws JSONException {
        JSONObject responseJSONObject = response.json.getJSONObject("response");
        int count = responseJSONObject.getInt(VKApiConst.COUNT);
        return new PhotosResponse(count, (VKPhotoArray) response.parsedModel);
    }

    @Override
    public void deliverResult(PhotosResponse data) {
        mapResponse = data;
        super.deliverResult(data);
    }

    private void getData(Bundle bundle, Context context) {
        longitude = bundle.getDouble(context.getString(R.string.photo_loader_bundle_key_longitude));
        latitude = bundle.getDouble(context.getString(R.string.photo_loader_bundle_key_latitude));
        offset = bundle.getInt(getContext().getString(R.string.photo_loader_bundle_key_offset),
                getContext().getResources().getInteger(R.integer.vk_request_offset_default_value));
        accessToken = bundle.getString(context.getString(R.string.vk_access_token_bundle_key),
                "");

        sortBy = PrefUtils.getSearchSortForPreference(context);
        radius = PrefUtils.getSearchRadiusForPreference(context);

        try {
            startDate = PrefUtils.getSearchStartForPreference(context);
            startDate = DateTimeUtils.convertPrefDataToUnix(context, startDate);

            endDate = PrefUtils.getSearchEndForPreference(context);

            if (endDate == null || TextUtils.isEmpty(endDate)) {
                endDate = String.valueOf(System.currentTimeMillis() / getContext().getResources()
                        .getInteger(R.integer.millisecond_in_second));
            } else {
                endDate = DateTimeUtils.convertPrefDataToUnix(context, endDate);
            }
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
