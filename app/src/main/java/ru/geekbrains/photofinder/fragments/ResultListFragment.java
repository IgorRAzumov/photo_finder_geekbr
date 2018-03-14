package ru.geekbrains.photofinder.fragments;


import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vk.sdk.api.model.VKPhotoArray;

import io.saeid.fabloading.LoadingView;
import ru.geekbrains.photofinder.R;
import ru.geekbrains.photofinder.adapters.PhotoResultAdapter;
import ru.geekbrains.photofinder.data.PhotosResponse;
import ru.geekbrains.photofinder.utils.PhotosResultScrollListener;
import ru.geekbrains.photofinder.utils.PrefUtils;


public class ResultListFragment extends Fragment implements
        PhotoResultAdapter.RecycleViewOnItemClickListener {
    private OnActivityCallback onActivityCallback;
    private int lastSavedPosition;

    private RecyclerView resultRecyclerView;
    private PhotoResultAdapter photoResultAdapter;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private LoadingView floatButton;


    public ResultListFragment() {
    }

    public static ResultListFragment newInstance(String vkPhotoArrayBundleKey, VKPhotoArray vkPhotoArray) {
        ResultListFragment resultListFragment = new ResultListFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable(vkPhotoArrayBundleKey, vkPhotoArray);

        resultListFragment.setArguments(arguments);
        return resultListFragment;
    }

    public static ResultListFragment newInstance(String vkPhotoArrayBundleKey, VKPhotoArray vkPhotoArray,
                                                 String photoPositionBundleKey, int position) {
        ResultListFragment resultListFragment = new ResultListFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable(vkPhotoArrayBundleKey, vkPhotoArray);
        arguments.putInt(photoPositionBundleKey, position);
        resultListFragment.setArguments(arguments);
        return resultListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle bundle = getArguments();
        if (bundle != null) {
            VKPhotoArray vkPhotoArray = bundle.getParcelable(
                    getString(R.string.vk_photo_array_bundle_key));
            lastSavedPosition = bundle.getInt(getString(R.string.vk_photo_array_position_bundle_key),
                    getResources().getInteger(R.integer.list_result_no_save_instance_position));
            initPhotoResultAdapter(vkPhotoArray);
        } else {
            throw new RuntimeException(getString(R.string.inbox_fragment_argument_error) +
                    this.getClass().getSimpleName());
        }
    }

    private void initPhotoResultAdapter(VKPhotoArray vkPhotoArray) {
        photoResultAdapter = new PhotoResultAdapter(this, vkPhotoArray);
        gridLayoutManager = new GridLayoutManager(getContext(), getResources()
                .getInteger(R.integer.span_count_grid_result_portr));
        linearLayoutManager = new LinearLayoutManager(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_result, container, false);
        resultRecyclerView = view.findViewById(R.id.rv_result_photos_list);
        floatButton = view.findViewById(R.id.fbt_result_list_fragment);


        int viewType = PrefUtils.getViewTypeFromPreference(getActivity());
        initFloatButton(viewType);
        initRecyclerView(viewType);
        if (lastSavedPosition > 0) {
            restoreRecyclerState();
        } else {
            checkSavedPosition(savedInstanceState);
        }
        return view;
    }

    private void initRecyclerView(int viewType) {
        photoResultAdapter.setItemViewType(viewType);
        if (viewType == PhotoResultAdapter.LINEAR_TYPE) {
            resultRecyclerView.setLayoutManager(linearLayoutManager);
        } else {
            resultRecyclerView.setLayoutManager(gridLayoutManager);
        }
        resultRecyclerView.setAdapter(photoResultAdapter);

        resultRecyclerView.addOnScrollListener(new PhotosResultScrollListener(linearLayoutManager, gridLayoutManager) {
            @Override
            protected void loadMoreData() {
                onActivityCallback.loadMoreData();
                photoResultAdapter.setItemViewType(PhotoResultAdapter.PROGRESS_TYPE);
            }

            @Override
            public boolean isDataEnd() {
                return onActivityCallback.isDataEnd();
            }

            @Override
            public boolean isLoading() {
                return onActivityCallback.isLoading();
            }
        });
    }

    private void checkSavedPosition(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            int noSavePosition = getResources().getInteger(R.integer.list_result_no_save_instance_position);
            lastSavedPosition = savedInstanceState.getInt(
                    getString(R.string.list_result_position_save_key), noSavePosition
            );
            if (lastSavedPosition > noSavePosition) {
                restoreRecyclerState();
            }
        }
    }

    private void initFloatButton(int viewType) {
        boolean isLollipop = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
        int grid = isLollipop ? R.drawable.grid_view_floating_bt_lollipop : R.drawable.grid_view_floating_bt;
        int linear = isLollipop ? R.drawable.linear_view_floating_bt_lollipop : R.drawable.linear_view_floating_bt;

        int gridBackground = getResources().getColor(R.color.result_list_float_bt_grid_background);
        int linearBackground = getResources().getColor(R.color.result_list_float_bt_linear_background);
        switch (viewType) {

            case PhotoResultAdapter.GRID_TYPE: {
                floatButton.addAnimation(gridBackground, grid, LoadingView.FROM_TOP);
                floatButton.addAnimation(linearBackground, linear, LoadingView.FROM_BOTTOM);
                break;
            }
            default: {
                floatButton.addAnimation(linearBackground, linear, LoadingView.FROM_BOTTOM);
                floatButton.addAnimation(gridBackground, grid, LoadingView.FROM_TOP);
            }
        }

        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatButton.startAnimation();
                if (photoResultAdapter != null) {
                    switchRecyclerViewLayoutManager();
                }
            }
        });

        floatButton.addListener(new LoadingView.LoadingListener() {
            @Override
            public void onAnimationStart(int currentItemPosition) {
                floatButton.setClickable(false);
            }

            @Override
            public void onAnimationRepeat(int nextItemPosition) {
            }

            @Override
            public void onAnimationEnd(int nextItemPosition) {
                floatButton.setClickable(true);
            }
        });
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (photoResultAdapter != null) {
            int fistVisiblePosition;
            if (photoResultAdapter.getItemViewType(getResources()
                    .getInteger(R.integer.list_result_default_number_view_type_check)) ==
                    PhotoResultAdapter.GRID_TYPE) {
                fistVisiblePosition = gridLayoutManager.findFirstVisibleItemPosition();
            } else {
                fistVisiblePosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
            }
            outState.putInt(getString(R.string.list_result_position_save_key), fistVisiblePosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        PrefUtils.writeAdapterTypeToSharedPref(getActivity(), photoResultAdapter
                .getItemViewType(getResources().getInteger(
                        R.integer.list_result_default_number_view_type_check)));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnActivityCallback) {
            onActivityCallback = (OnActivityCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + getString(R.string.on_activity_callback__fragment_error) +
                    this.getClass().getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onActivityCallback = null;
    }

    @Override
    public void onItemRecyclerClick(int position) {
        onActivityCallback.switchResultRecyclerToViewPager(position);
    }

    @Override
    public void onOpenProfileButtonClickListener(int position) {
        onActivityCallback.openProfile(position);
    }

    private void restoreRecyclerState() {
        switch (photoResultAdapter.getItemViewType(getResources()
                .getInteger(R.integer.list_result_default_number_view_type_check))) {
            case PhotoResultAdapter.PROGRESS_TYPE: {
                break;
            }
            case PhotoResultAdapter.GRID_TYPE: {
                gridLayoutManager.scrollToPosition(lastSavedPosition);
                break;
            }
            default: {
                linearLayoutManager.scrollToPosition(lastSavedPosition);
                break;
            }

        }
    }

    private void switchRecyclerViewLayoutManager() {
        boolean isGrid = resultRecyclerView.getLayoutManager() == gridLayoutManager;
        int fistVisiblePosition;

        if (!isGrid) {
            fistVisiblePosition = linearLayoutManager.findFirstVisibleItemPosition();
            resultRecyclerView.setLayoutManager(gridLayoutManager);
            photoResultAdapter.setItemViewType(PhotoResultAdapter.GRID_TYPE);
            gridLayoutManager.scrollToPosition(fistVisiblePosition);
        } else {
            fistVisiblePosition = gridLayoutManager.findFirstVisibleItemPosition();
            resultRecyclerView.setLayoutManager(linearLayoutManager);
            photoResultAdapter.setItemViewType(PhotoResultAdapter.LINEAR_TYPE);
            linearLayoutManager.scrollToPosition(fistVisiblePosition);
        }

        resultRecyclerView.invalidateItemDecorations();
    }

    public void onBackPressed() {
        floatButton.startAnimation();
        switchRecyclerViewLayoutManager();
    }

    public boolean isNeedBackPressed() {
        return resultRecyclerView.getLayoutManager() != gridLayoutManager;
    }

    public void addMoreData(PhotosResponse data) {
        photoResultAdapter.setItemViewType(photoResultAdapter.getPenultimateViewType());
        photoResultAdapter.removeLoadingFooter();
        photoResultAdapter.addAll(data.getVkPhotoArray());

        if (!onActivityCallback.isDataEnd()) {
            photoResultAdapter.addLoadingFooter();
        }
    }

    public interface OnActivityCallback {
        void switchResultRecyclerToViewPager(int position);

        void openProfile(int position);

        void loadMoreData();

        boolean isDataEnd();

        boolean isLoading();
    }
}