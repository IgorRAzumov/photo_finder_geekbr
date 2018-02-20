package ru.geekbrains.photofinder.utils;

import android.content.Context;

import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKPhotoArray;

import org.json.JSONArray;
import org.json.JSONObject;

import ru.geekbrains.photofinder.R;


public class JSONUtils {
    public static VKPhotoArray parsePhotosResponseToVkList(VKResponse response, Context context) {
        VKPhotoArray vkPhotoArray = new VKPhotoArray();
        try {
            JSONObject jsonObject = response.json.getJSONObject(
                    context.getString(R.string.json_object_vk_req_name_json_key));
            JSONArray jsonArray = jsonObject.getJSONArray(
                    context.getString(R.string.json_array_vk_req_json_key));
            for (int i = 0; i < jsonArray.length(); i++) {
                VKApiPhoto photo = new VKApiPhoto(jsonArray.getJSONObject(i));
                vkPhotoArray.add(photo);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return vkPhotoArray;
    }
}
