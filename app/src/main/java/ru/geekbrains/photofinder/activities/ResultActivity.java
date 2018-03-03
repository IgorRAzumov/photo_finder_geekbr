package ru.geekbrains.photofinder.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKPhotoArray;

import java.lang.ref.WeakReference;

import ru.geekbrains.photofinder.R;
import ru.geekbrains.photofinder.asyncTaskLoaders.PhotoSearchVkLoader;
import ru.geekbrains.photofinder.fragments.ProgressFragment;
import ru.geekbrains.photofinder.fragments.ResultListFragment;
import ru.geekbrains.photofinder.fragments.ResultViewPagerFragment;

public class ResultActivity extends AppCompatActivity implements
        ResultListFragment.OnActivityCallback, ResultViewPagerFragment.OnActivityCallback,
        LoaderManager.LoaderCallbacks<VKPhotoArray> {
    private static final int SHOW_ERROR_MESSAGE_HANDLER_CODE = 222;
    private static final int CHANGE_FRAGMENT_MESSAGE_HANDLER_CODE = 111;

    private double longitude;
    private double latitude;
    private VKPhotoArray vkPhotoArray;
    private ResultActivityHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        getDataFromIntent();

        handler = new ResultActivityHandler(new WeakReference<>(this));

        getSupportLoaderManager().initLoader(getResources().getInteger(
                R.integer.photo_list_loader_id), null, this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fl_result_container);
        if (fragment == null) {
            fragment = new ProgressFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fl_result_container, fragment)
                    .commit();
        }
    }

    @Override
    public Loader<VKPhotoArray> onCreateLoader(int id, Bundle args) {
        if (getResources().getInteger(R.integer.photo_list_loader_id) == id) {
            Bundle bundle = new Bundle();
            bundle.putDouble(getString(R.string.photo_loader_bundle_key_longitude), longitude);
            bundle.putDouble(getString(R.string.photo_loader_bundle_key_latitude), latitude);
            return new PhotoSearchVkLoader(this, bundle);

        } else {
            throw new RuntimeException(getString(R.string.list_result_fragment_loader_id_error)
                    + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<VKPhotoArray> loader, final VKPhotoArray data) {
        if (data != null) {
            if (data.size() != 0) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Message message = handler.obtainMessage(
                                CHANGE_FRAGMENT_MESSAGE_HANDLER_CODE, data);
                        handler.sendMessage(message);
                    }
                });
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Message message = handler.obtainMessage(SHOW_ERROR_MESSAGE_HANDLER_CODE,
                                getString(R.string.list_result_no_search_result));
                        handler.sendMessage(message);
                    }
                });


            }
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Message message = handler.obtainMessage(SHOW_ERROR_MESSAGE_HANDLER_CODE,
                            getString(R.string.error_request));
                    handler.sendMessage(message);
                }
            });

        }
    }

    @Override
    public void onLoaderReset(Loader<VKPhotoArray> loader) {

    }

    public void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void switchRecyclerToViewPager(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fl_result_container);
        if (fragment == null || !(fragment instanceof ResultViewPagerFragment)) {
            ResultViewPagerFragment resultViewPagerFragment = ResultViewPagerFragment.newInstance(
                    getString(R.string.vk_photo_array_bundle_key), vkPhotoArray,
                    getString(R.string.vk_photo_array_position_bundle_key), position);

            fragmentManager.beginTransaction()
                    .replace(R.id.fl_result_container, resultViewPagerFragment)
                    //    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void openProfile(int position) {
        VKApiPhoto vkApiPhoto = vkPhotoArray.get(position);
        int id = Math.abs(vkApiPhoto.owner_id);
        if (id != 0) {
            Uri address = Uri.parse(getString(R.string.vk_url) + id);
            Intent openProfileIntent = new Intent(Intent.ACTION_VIEW, address);
            startActivity(openProfileIntent);
        }
    }

    @Override
    public void switchViewPagerToRecycler(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fl_result_container);
        if (fragment == null || !(fragment instanceof ResultListFragment)) {
            ResultListFragment resultListFragment = ResultListFragment.newInstance(
                    getString(R.string.vk_photo_array_bundle_key), vkPhotoArray,
                    getString(R.string.vk_photo_array_position_bundle_key), position);

            fragmentManager.beginTransaction()
                    .replace(R.id.fl_result_container, resultListFragment)
                    .commit();
        }
    }

    private void switchProgressToResult(VKPhotoArray vkPhotoArray) {
        this.vkPhotoArray = vkPhotoArray;

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fl_result_container);
        if (fragment == null || !(fragment instanceof ResultListFragment)) {
            ResultListFragment resultListFragment = ResultListFragment.newInstance(
                    getString(R.string.vk_photo_array_bundle_key), vkPhotoArray);

            fragmentManager.beginTransaction()
                    .replace(R.id.fl_result_container, resultListFragment)
                    .commit();
        }
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        longitude = intent.getDoubleExtra(getString(R.string.longitude_intent_key), 0);
        latitude = intent.getDoubleExtra(getString(R.string.latitude_intent_key), 0);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fl_result_container);
        if (fragment != null) {
            if (fragment instanceof ResultViewPagerFragment) {
                ResultViewPagerFragment resultViewPagerFragment = (ResultViewPagerFragment) fragment;
                resultViewPagerFragment.onBackPressed();
            } else if (fragment instanceof ResultListFragment) {
                ResultListFragment resultListFragment = (ResultListFragment) fragment;
                if (!resultListFragment.isNeedBackPressed()) {
                    resultListFragment.onBackPressed();
                } else {
                    super.onBackPressed();
                }
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    private static class ResultActivityHandler extends Handler {
        private final WeakReference<ResultActivity> reference;

        private ResultActivityHandler(WeakReference<ResultActivity> reference) {
            this.reference = reference;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CHANGE_FRAGMENT_MESSAGE_HANDLER_CODE: {
                    ResultActivity resultActivity = reference.get();
                    if (resultActivity != null) {
                        FragmentManager fragmentManager = resultActivity.getSupportFragmentManager();
                        Fragment fragment = fragmentManager.findFragmentById(R.id.fl_result_container);
                        if (fragment instanceof ProgressFragment) {
                            resultActivity.switchProgressToResult((VKPhotoArray) msg.obj);
                        }
                    }
                    break;
                }
                case SHOW_ERROR_MESSAGE_HANDLER_CODE: {
                    ResultActivity resultActivity = reference.get();
                    if (resultActivity != null) {
                        resultActivity.showErrorMessage((String) msg.obj);
                        resultActivity.onBackPressed();
                        break;
                    }
                }
                default: {
                    super.handleMessage(msg);
                }
            }
        }

    }
}
