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
import android.widget.FrameLayout;

import com.vk.sdk.api.model.VKApiPhoto;

import java.lang.ref.WeakReference;

import ru.geekbrains.photofinder.R;
import ru.geekbrains.photofinder.asyncTaskLoaders.PhotoSearchVkLoader;
import ru.geekbrains.photofinder.data.PhotosResponse;
import ru.geekbrains.photofinder.fragments.ProgressFragment;
import ru.geekbrains.photofinder.fragments.ResultListFragment;
import ru.geekbrains.photofinder.fragments.ResultViewPagerFragment;
import ru.geekbrains.photofinder.utils.UiUtils;

public class ResultActivity extends AppCompatActivity implements
        ResultListFragment.OnActivityCallback, ResultViewPagerFragment.OnActivityCallback,
        LoaderManager.LoaderCallbacks<PhotosResponse> {
    private static final int SHOW_ERROR_MESSAGE_HANDLER_CODE = 222;
    private static final int CHANGE_FRAGMENT_MESSAGE_HANDLER_CODE = 111;
    private static final int ADD_MORE_DATA_MESSAGE_HANDLER_CODE = 333;

    private FrameLayout rootView;
    private ResultActivityHandler handler;

    private double longitude;
    private double latitude;
    private String accessToken;
    private boolean isLoading;
    private int offset;

    private PhotosResponse photosResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        getDataFromIntent();
        rootView = findViewById(R.id.fl_activity_result_root_view);
        handler = new ResultActivityHandler(new WeakReference<>(this));

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fl_result_container);
        if (fragment == null) {
            fragment = new ProgressFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fl_result_container, fragment)
                    .commit();
        }

        getSupportLoaderManager().initLoader(getResources().getInteger(
                R.integer.photo_list_loader_id), getLoaderArguments(), this);
    }


    @Override
    public Loader<PhotosResponse> onCreateLoader(int id, Bundle args) {
        if (getResources().getInteger(R.integer.photo_list_loader_id) == id) {
            return new PhotoSearchVkLoader(this, args);

        } else {
            throw new RuntimeException(getString(R.string.list_result_fragment_loader_id_error)
                    + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<PhotosResponse> loader, final PhotosResponse data) {
        if (data != null) {
            if (offset == 0) {
                if (data.getVkPhotoArray().size() != 0) {
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
                        Message message = handler.obtainMessage(ADD_MORE_DATA_MESSAGE_HANDLER_CODE,
                                data);
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
    public void onLoaderReset(Loader<PhotosResponse> loader) {

    }


    @Override
    public void switchResultRecyclerToViewPager(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fl_result_container);
        if (fragment == null || !(fragment instanceof ResultViewPagerFragment)) {
            ResultViewPagerFragment resultViewPagerFragment = ResultViewPagerFragment.newInstance(
                    getString(R.string.vk_photo_array_bundle_key), photosResponse.getVkPhotoArray(),
                    getString(R.string.vk_photo_array_position_bundle_key), position);

            fragmentManager.beginTransaction()
                    .replace(R.id.fl_result_container, resultViewPagerFragment)
                    .commit();
        }
    }

    @Override
    public void openProfile(int position) {
        VKApiPhoto vkApiPhoto = photosResponse.getVkPhotoArray().get(position);
        int id = Math.abs(vkApiPhoto.owner_id);
        if (id != 0) {
            Uri address = Uri.parse(getString(R.string.vk_url) + id);
            Intent openProfileIntent = new Intent(Intent.ACTION_VIEW, address);
            startActivity(openProfileIntent);
        }
    }


    @Override
    public void loadMoreData() {
        isLoading = true;
        offset += getResources().getInteger(R.integer.vk_api_paream_count_default_value);
        Bundle bundle = getLoaderArguments();
        bundle.putInt(getString(R.string.photo_loader_bundle_key_offset), offset);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<PhotosResponse> photosSearchLoader = loaderManager.getLoader(getResources().getInteger(R.integer.photo_list_loader_id));
        if (photosSearchLoader == null) {
            loaderManager.initLoader(getResources().getInteger(R.integer.photo_list_loader_id),
                    bundle, this);
        } else {
            loaderManager.restartLoader(getResources().getInteger(R.integer.photo_list_loader_id),
                    bundle, this);
        }
    }

    @Override
    public boolean isDataEnd() {
        boolean isDataEnd;
        if (photosResponse != null) {
            isDataEnd = offset + getResources().getInteger(R.integer.vk_api_paream_count_default_value) >=
                    photosResponse.getTotalPhotosCount();
        } else {
            isDataEnd = false;
        }
        return isDataEnd;
    }

    @Override
    public boolean isLoading() {
        return isLoading;
    }


    @Override
    public void switchViewPagerToResultRecycler(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fl_result_container);
        if (fragment == null || !(fragment instanceof ResultListFragment)) {
            ResultListFragment resultListFragment = ResultListFragment.newInstance(
                    getString(R.string.vk_photo_array_bundle_key), photosResponse.getVkPhotoArray(),
                    getString(R.string.vk_photo_array_position_bundle_key), position);

            fragmentManager.beginTransaction()
                    .replace(R.id.fl_result_container, resultListFragment)
                    .commit();
        }
    }

    private void switchProgressToResultRecycler(PhotosResponse data) {
        isLoading = false;
        photosResponse = data;

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fl_result_container);
        if (fragment == null || !(fragment instanceof ResultListFragment)) {
            ResultListFragment resultListFragment = ResultListFragment.newInstance(
                    getString(R.string.vk_photo_array_bundle_key), photosResponse.getVkPhotoArray());

            fragmentManager.beginTransaction()
                    .replace(R.id.fl_result_container, resultListFragment)
                    .commit();
        }
    }

    private void addMoreData(PhotosResponse data) {
        isLoading = false;
        photosResponse.refreshPhotosResponse(data);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fl_result_container);
        if (fragment != null && fragment instanceof ResultListFragment) {
            ResultListFragment resultListFragment = (ResultListFragment) fragment;
            resultListFragment.addMoreData(data);
        }
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

    private void errorRequestPhotos(String message) {
        isLoading = false;
        UiUtils.showMessage(rootView, message);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, getResources().getInteger(R.integer.result_activity_finish_delay));
    }


    private Bundle getLoaderArguments() {
        Bundle bundle = new Bundle();
        bundle.putDouble(getString(R.string.photo_loader_bundle_key_longitude), longitude);
        bundle.putDouble(getString(R.string.photo_loader_bundle_key_latitude), latitude);
        if (accessToken != null) {
            bundle.putString(getString(R.string.vk_access_token_bundle_key), accessToken);
        }
        return bundle;
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        longitude = intent.getDoubleExtra(getString(R.string.longitude_intent_key), 0);
        latitude = intent.getDoubleExtra(getString(R.string.latitude_intent_key), 0);
        accessToken = intent.getStringExtra(getString(R.string.vk_access_token_bundle_key));
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
                            resultActivity.switchProgressToResultRecycler((PhotosResponse) msg.obj);
                        }
                    }
                    break;
                }
                case SHOW_ERROR_MESSAGE_HANDLER_CODE: {
                    ResultActivity resultActivity = reference.get();
                    if (resultActivity != null) {
                        resultActivity.errorRequestPhotos((String) msg.obj);
                        break;
                    }
                }
                case ADD_MORE_DATA_MESSAGE_HANDLER_CODE: {
                    ResultActivity resultActivity = reference.get();
                    if (resultActivity != null) {
                        resultActivity.addMoreData((PhotosResponse) msg.obj);
                    }
                    break;
                }
                default: {
                    super.handleMessage(msg);
                }
            }
        }

    }
}
