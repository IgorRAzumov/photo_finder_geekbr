package ru.geekbrains.photofinder.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKPhotoArray;

import ru.geekbrains.photofinder.utils.NetworkUtils;
import ru.geekbrains.photofinder.utils.PrefUtils;
import ru.geekbrains.photofinder.R;
import ru.geekbrains.photofinder.adapters.ListResultAdapter;


public class ListResultFragment extends Fragment implements
        ListResultAdapter.RecycleViewOnItemClickListener, LoaderManager.LoaderCallbacks<VKPhotoArray> {
    public interface OnActivityCallback {
        void showErrorMessage(String message);
    }

    private double longitude;
    private double latitude;
    private String accessToken;
    private int lastSavedPosition;
    private VKPhotoArray vkPhotoArray;

    private RecyclerView resultRecyclerView;
    private ListResultAdapter listResultAdapter;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;

    private OnActivityCallback onActivityCallback;

    public ListResultFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getDataFromIntent();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_result, container, false);
        resultRecyclerView = view.findViewById(R.id.rv_result_photos_list);
        gridLayoutManager = new GridLayoutManager(getContext(), getResources()
                .getInteger(R.integer.span_count_grid_result_portr));
        linearLayoutManager = new LinearLayoutManager(getContext());
        resultRecyclerView.setLayoutManager(linearLayoutManager);
        listResultAdapter = new ListResultAdapter(this);
        resultRecyclerView.setAdapter(listResultAdapter);

        lastSavedPosition = getResources().getInteger(R.integer.list_result_no_save_instance_position);
        if (savedInstanceState != null) {
            lastSavedPosition = savedInstanceState.getInt(
                    getString(R.string.list_result_position_save_key),
                    getResources().getInteger(R.integer.list_result_no_save_instance_position));
        }

        loadSettings();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LoaderManager loaderManager = getActivity().getSupportLoaderManager();
        loaderManager.initLoader(getResources().getInteger(
                R.integer.integer_phto_list_loader_id), null, this);

        Loader<VKPhotoArray> getPhotosLoader = loaderManager.getLoader(getResources().getInteger(
                R.integer.integer_phto_list_loader_id));

        if (getPhotosLoader == null) {
            loaderManager.initLoader(getResources().getInteger(
                    R.integer.integer_phto_list_loader_id), null, this);
        } else {
            loaderManager.restartLoader(getResources().getInteger(
                    R.integer.integer_phto_list_loader_id), null, this);
        }

    }

    @SuppressLint("StaticFieldLeak")//как быть лучше в такой ситуации?
    @Override
    public android.support.v4.content.Loader<VKPhotoArray> onCreateLoader(int id, final Bundle args) {
        if (getResources().getInteger(R.integer.integer_phto_list_loader_id) == id) {
            return new AsyncTaskLoader<VKPhotoArray>(getContext()) {
                VKPhotoArray vkPhotoArray = null;

                @Override
                protected void onStartLoading() {
                  /*  if (args == null) {
                        return;
                    }*/
                    if (vkPhotoArray != null) {
                        deliverResult(vkPhotoArray);
                    } else {
                        forceLoad();
                    }
                }


                @Override
                public VKPhotoArray loadInBackground() {
                    VKResponse response = NetworkUtils.getPhotos(getContext(), latitude, longitude);
                    if (response != null) {
                        vkPhotoArray = (VKPhotoArray) response.parsedModel;

                    }
                    return vkPhotoArray;
                }

                @Override
                public void deliverResult(VKPhotoArray data) {
                    vkPhotoArray = data;
                    super.deliverResult(data);
                }
            };


        } else {
            throw new RuntimeException(getString(R.string.list_result_fragment_loader_id_error)
                    + id);
        }

    }

    @Override
    public void onLoadFinished
            (android.support.v4.content.Loader<VKPhotoArray> loader, VKPhotoArray data) {
        if (data != null) {
            if (data.size() != 0) {
                listResultAdapter.setData(data);
                listResultAdapter.notifyDataSetChanged();

                if (lastSavedPosition > getResources().getInteger(
                        R.integer.list_result_no_save_instance_position)) {
                    restoreRecyclerState();
                }
            } else {
                onActivityCallback.showErrorMessage(getActivity()
                        .getString(R.string.no_search_result));
            }
        } else {
            onActivityCallback.showErrorMessage(getContext().getString(R.string.error_request));
        }
    }


    @Override
    public void onLoaderReset(android.support.v4.content.Loader<VKPhotoArray> loader) {

    }

    private void restoreRecyclerState() {
        switch (listResultAdapter.getItemViewType(getResources()
                .getInteger(R.integer.list_result_default_number_view_type_check))) {
            case ListResultAdapter.GRID_TYPE: {
                gridLayoutManager.scrollToPosition(lastSavedPosition);
                break;
            }
            default: {
                linearLayoutManager.scrollToPosition(lastSavedPosition);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (listResultAdapter != null) {
            switch (item.getItemId()) {
                case (R.id.action_change_view): {
                    switchRecyclerViewLayoutManager();
                    return true;
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (listResultAdapter != null) {
            int fistVisiblePosition;
            if (listResultAdapter.getItemViewType(getResources()
                    .getInteger(R.integer.list_result_default_number_view_type_check)) ==
                    ListResultAdapter.GRID_TYPE) {
                fistVisiblePosition = gridLayoutManager.findFirstVisibleItemPosition();
            } else {
                fistVisiblePosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
            }
            outState.putInt(getString(R.string.list_result_position_save_key), fistVisiblePosition);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        PrefUtils.writeAdapterTypeToSharedPref(getActivity(), listResultAdapter
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
                    + getContext().getString(R.string.on_activity_callback__fragmen_error));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onActivityCallback = null;
    }


    @Override
    public void onItemRecyclerClick(View v, int position) {

    }

    private void getDataFromIntent() {
        Intent intent = getActivity().getIntent();
        longitude = intent.getDoubleExtra(getString(R.string.longitude_intent_key), 0);
        latitude = intent.getDoubleExtra(getString(R.string.latitude_intent_key), 0);
        accessToken = intent.getStringExtra(getString(R.string.vk_access_token_intent_key));
    }

    private void loadSettings() {
        int viewType = PrefUtils.getViewTypeForPreference(getActivity());//ListResultAdapter.LINEAR_TYPE;
        listResultAdapter.setViewType(viewType);
        if (viewType == ListResultAdapter.LINEAR_TYPE) {
            resultRecyclerView.setLayoutManager(linearLayoutManager);
        } else {
            resultRecyclerView.setLayoutManager(gridLayoutManager);
        }
    }


    private void switchRecyclerViewLayoutManager() {
        boolean isGrid = resultRecyclerView.getLayoutManager() == gridLayoutManager;
        int fistVisiblePosition;
        if (!isGrid) {
            fistVisiblePosition = linearLayoutManager.findFirstVisibleItemPosition();
            resultRecyclerView.setLayoutManager(gridLayoutManager);
            listResultAdapter.setViewType(ListResultAdapter.GRID_TYPE);
            onDataChange();
            gridLayoutManager.scrollToPosition(fistVisiblePosition);

        } else {
            fistVisiblePosition = gridLayoutManager.findFirstVisibleItemPosition();
            resultRecyclerView.setLayoutManager(linearLayoutManager);
            listResultAdapter.setViewType(ListResultAdapter.LINEAR_TYPE);
            onDataChange();
            linearLayoutManager.scrollToPosition(fistVisiblePosition);
        }
    }

    private void onDataChange() {
        listResultAdapter.notifyDataSetChanged();
        resultRecyclerView.invalidateItemDecorations();
        getActivity().invalidateOptionsMenu();
    }
}