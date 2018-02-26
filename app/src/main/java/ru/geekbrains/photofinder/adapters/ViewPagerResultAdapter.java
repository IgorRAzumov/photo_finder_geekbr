package ru.geekbrains.photofinder.adapters;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.vk.sdk.api.model.VKPhotoArray;

import ru.geekbrains.photofinder.R;
import ru.geekbrains.photofinder.fragments.PageFragment;

public class ViewPagerResultAdapter extends FragmentStatePagerAdapter {
    private final String VK_API_PHOTO_BUNDLE_KEY;
    private VKPhotoArray vkPhotoArray;

    public ViewPagerResultAdapter(FragmentManager fm, VKPhotoArray vkPhotoArray, Context context) {
        super(fm);
        this.vkPhotoArray = vkPhotoArray;
        VK_API_PHOTO_BUNDLE_KEY = context.getString(R.string.vk_photo_bundle_key);
    }

    @Override
    public Fragment getItem(int position) {
        return PageFragment.newInstance(VK_API_PHOTO_BUNDLE_KEY, vkPhotoArray.get(position));
    }

    @Override
    public int getCount() {
        return vkPhotoArray.size();
    }
}
