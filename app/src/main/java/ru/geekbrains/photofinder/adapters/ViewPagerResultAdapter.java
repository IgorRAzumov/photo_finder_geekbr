package ru.geekbrains.photofinder.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.vk.sdk.api.model.VKPhotoArray;

import ru.geekbrains.photofinder.fragments.PageFragment;

public class ViewPagerResultAdapter extends FragmentStatePagerAdapter {
    public static final String PAGER_RESULT_PHOTO_URL_BUNDLE_KEY = "pager-photo-key";
    private VKPhotoArray vkPhotoArray;

    public ViewPagerResultAdapter(FragmentManager fm, VKPhotoArray vkPhotoArray) {
        super(fm);
        this.vkPhotoArray = vkPhotoArray;
    }

    @Override
    public Fragment getItem(int position) {
        return PageFragment.newInstance(vkPhotoArray.get(position).photo_807 );
    }

    @Override
    public int getCount() {
        return vkPhotoArray.size();
    }
}
