package ru.geekbrains.photofinder.utils;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKPhotoArray;

import ru.geekbrains.photofinder.R;


public class NetworkUtils {

    public static VKResponse getPhotos(final Context context, double latitude, double longitude,
                                       String startTime, String endTime, String sort, String radius,
                                       String accessToken) {
        VKRequest request = getAuthPhotoRequest(context, latitude, longitude, startTime, endTime,
                sort, radius, accessToken);
        return executePhotoRequest(request);
    }


    public static VKResponse getPhotos(final Context context, double latitude, double longitude,
                                       String startTime, String endTime, String sort, String radius) {
        VKRequest request = getNoAuthPhotoRequest(context, latitude, longitude, startTime, endTime,
                sort, radius);
        return executePhotoRequest(request);
    }

    private static VKResponse executePhotoRequest(VKRequest request) {
        final VKResponse[] returnedVkResponse = new VKResponse[1];
        request.executeSyncWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                returnedVkResponse[0] = response;
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
            }

        });
        return returnedVkResponse[0];
    }

    private static VKRequest getNoAuthPhotoRequest(final Context context, double latitude, double longitude,
                                                   String startTime, String endTime, String sort, String radius) {
        VKRequest request = new VKRequest(context.getString(R.string.vk_photo_search_request_method_name_api),
                VKParameters.from(
                        VKApiConst.LAT, latitude,
                        VKApiConst.LONG, longitude,
                        context.getString(R.string.vk_api_const_start_time), startTime,
                        context.getString(R.string.vk_api_const_end_time), endTime,
                        VKApiConst.SORT, sort,
                        context.getString(R.string.vk_api_const_radius), radius,
                        VKApiConst.VERSION, context.getString(R.string.vk_api_version)));
        request.setModelClass(VKPhotoArray.class);
        return request;
    }

    private static VKRequest getAuthPhotoRequest(final Context context, double latitude, double longitude,
                                                 String startTime, String endTime, String sort, String radius,
                                                 String accessToken) {
        VKRequest request = getNoAuthPhotoRequest(context, latitude, longitude, startTime,
                endTime, sort, radius);
        request.addExtraParameter(VKApiConst.ACCESS_TOKEN, accessToken);
        return request;
    }

    public static void loadImage(ImageView imageView, String url, Context context, int targetWidth,
                                 int targetHeight) {
        if (url != null) {
            Picasso
                    .with(context)
                    .load(url)
                    // .resize(targetWidth, targetHeight)
                    .into(imageView);
        }
    }


}

