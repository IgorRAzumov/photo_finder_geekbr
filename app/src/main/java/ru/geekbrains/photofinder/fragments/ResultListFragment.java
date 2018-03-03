package ru.geekbrains.photofinder.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.vk.sdk.api.model.VKPhotoArray;

import ru.geekbrains.photofinder.R;
import ru.geekbrains.photofinder.adapters.PhotoResultAdapter;
import ru.geekbrains.photofinder.utils.PrefUtils;


public class ResultListFragment extends Fragment implements
        PhotoResultAdapter.RecycleViewOnItemClickListener {
    private OnActivityCallback onActivityCallback;
    private int lastSavedPosition;
    private RecyclerView resultRecyclerView;
    private PhotoResultAdapter photoResultAdapter;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;

    public ResultListFragment() {
    }

    public static ResultListFragment newInstance(String vkPhotoArrayBundleKey,
                                                 VKPhotoArray vkPhotoArray) {
        ResultListFragment resultListFragment = new ResultListFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable(vkPhotoArrayBundleKey, vkPhotoArray);
        resultListFragment.setArguments(arguments);
        return resultListFragment;
    }

    public static ResultListFragment newInstance(String vkPhotoArrayBundleKey,
                                                 VKPhotoArray vkPhotoArray,
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
        gridLayoutManager = new GridLayoutManager(getContext(), getResources()
                .getInteger(R.integer.span_count_grid_result_portr));
        linearLayoutManager = new LinearLayoutManager(getContext());

        int viewType = PrefUtils.getViewTypeForPreference(getActivity());//PhotoResultAdapter.LINEAR_TYPE;
        photoResultAdapter.setViewType(viewType);
        if (viewType == PhotoResultAdapter.LINEAR_TYPE) {
            resultRecyclerView.setLayoutManager(linearLayoutManager);
        } else {
            resultRecyclerView.setLayoutManager(gridLayoutManager);
        }

        resultRecyclerView.setAdapter(photoResultAdapter);

        int noSavePosition = getResources().getInteger(R.integer.list_result_no_save_instance_position);
        if (savedInstanceState != null) {
            lastSavedPosition = savedInstanceState.getInt(
                    getString(R.string.list_result_position_save_key), noSavePosition
            );
        }

        if (lastSavedPosition > noSavePosition) {
            restoreRecyclerState();
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.result_list_menu, menu);
        MenuItem action_switch_view = menu.findItem(R.id.action_change_view);
        int viewType = photoResultAdapter.getItemViewType(getResources().getInteger(
                R.integer.list_result_default_number_view_type_check));
        if (viewType == PhotoResultAdapter.LINEAR_TYPE) {
            action_switch_view.setIcon(R.drawable.ic_action_grid_view);
        } else {
            action_switch_view.setIcon(R.drawable.ic_action_linear_view);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.action_change_view): {
                if (photoResultAdapter != null) {
                    switchRecyclerViewLayoutManager();

                    int viewType = photoResultAdapter.getItemViewType(getResources().getInteger(
                            R.integer.list_result_default_number_view_type_check));
                    if (viewType == PhotoResultAdapter.LINEAR_TYPE) {
                        item.setIcon(R.drawable.ic_action_grid_view);
                    } else {
                        item.setIcon(R.drawable.ic_action_linear_view);
                    }
                }
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }

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
        onActivityCallback.switchRecyclerToViewPager(position);
    }

    @Override
    public void onOpenProfileButtonClickListener(int position) {
        onActivityCallback.openProfile(position);
    }

    private void restoreRecyclerState() {
        switch (photoResultAdapter.getItemViewType(getResources()
                .getInteger(R.integer.list_result_default_number_view_type_check))) {
            case PhotoResultAdapter.GRID_TYPE: {
                gridLayoutManager.scrollToPosition(lastSavedPosition);
                break;
            }
            default: {
                linearLayoutManager.scrollToPosition(lastSavedPosition);
            }
        }
    }

    private void switchRecyclerViewLayoutManager() {
        boolean isGrid = resultRecyclerView.getLayoutManager() == gridLayoutManager;
        int fistVisiblePosition;

        if (!isGrid) {
            fistVisiblePosition = linearLayoutManager.findFirstVisibleItemPosition();
            resultRecyclerView.setLayoutManager(gridLayoutManager);
            photoResultAdapter.setViewType(PhotoResultAdapter.GRID_TYPE);
            gridLayoutManager.scrollToPosition(fistVisiblePosition);
        } else {
            fistVisiblePosition = gridLayoutManager.findFirstVisibleItemPosition();
            resultRecyclerView.setLayoutManager(linearLayoutManager);
            photoResultAdapter.setViewType(PhotoResultAdapter.LINEAR_TYPE);
            linearLayoutManager.scrollToPosition(fistVisiblePosition);
        }
        onDataChange();
    }

    private void onDataChange() {
        resultRecyclerView.invalidateItemDecorations();
        resultRecyclerView.invalidate();
    }

    public void onBackPressed() {
        switchRecyclerViewLayoutManager();
    }

    public boolean isNeedBackPressed() {
        return resultRecyclerView.getLayoutManager() != gridLayoutManager;
    }

    public interface OnActivityCallback {
        void switchRecyclerToViewPager(int position);

        void openProfile(int position);
    }
}