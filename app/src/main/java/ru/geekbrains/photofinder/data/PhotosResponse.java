package ru.geekbrains.photofinder.data;

import com.vk.sdk.api.model.VKPhotoArray;

public class PhotosResponse {
    private VKPhotoArray vkPhotoArray;
    private int totalPhotosCount;

    public PhotosResponse(int totalPhotosCount, VKPhotoArray vkPhotoArray) {
        this.vkPhotoArray = vkPhotoArray;
        this.totalPhotosCount = totalPhotosCount;
    }

    public VKPhotoArray getVkPhotoArray() {
        return vkPhotoArray;
    }

    public int getTotalPhotosCount() {
        return totalPhotosCount;
    }

    public void refreshPhotosResponse(PhotosResponse photosResponse) {
        vkPhotoArray.addAll(photosResponse.getVkPhotoArray());
        totalPhotosCount = photosResponse.getTotalPhotosCount();
    }
}
