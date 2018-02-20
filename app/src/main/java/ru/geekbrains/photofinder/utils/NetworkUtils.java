package ru.geekbrains.photofinder.utils;

import android.content.Context;
import android.util.Log;

import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKPhotoArray;

import ru.geekbrains.photofinder.R;




public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    public static VKResponse getPhotos(final Context context, double latitude, double longitude) {
      final VKResponse[] returnedVkResponse = new VKResponse[1];
        VKRequest request = new VKRequest("photos.search",
                VKParameters.from(VKApiConst.LAT, latitude,
                        VKApiConst.LONG, longitude,
                        VKApiConst.COUNT, 100,
                        VKApiConst.VERSION, context.getString(R.string.vk_api_version),
                        VKPhotoArray.class));
        request.executeSyncWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                returnedVkResponse[0] = response;
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                Log.d(TAG, error.errorMessage);
            }

        });
        return returnedVkResponse[0];
    }
}

