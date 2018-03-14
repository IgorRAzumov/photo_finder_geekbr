package ru.geekbrains.photofinder.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import io.saeid.fabloading.LoadingView;
import ru.geekbrains.photofinder.R;
import ru.geekbrains.photofinder.utils.UiUtils;


public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {
    private static final int REQUEST_LOCATION_PERMISSIONS_ID = 13;

    private ProgressBar progressBar;
    private LoadingView floatButton;

    private GoogleMap map;
    private Marker marker;

    private boolean isPlaceSelected;
    private LatLng savedMarkerPosition;
    private OnActivityCallback onActivityCallback;

    public MapFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        progressBar = view.findViewById(R.id.pb_fragment_map_progress);
        floatButton = view.findViewById(R.id.fbt_map_fragment);
        progressBar.setVisibility(View.VISIBLE);

        checkSavedSelectedPosition(savedInstanceState);
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (marker != null) {
            outState.putParcelable(getString(R.string.lat_lang_bundle_key), marker.getPosition());
        }
        super.onSaveInstanceState(outState);
    }

    private void checkSavedSelectedPosition(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            savedMarkerPosition = savedInstanceState
                    .getParcelable(getString(R.string.lat_lang_bundle_key));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        getResources().getInteger(R.integer.request_location_permission_id));
            } else {
                startMapFragment();
            }
        }
    }

    private void initFloatButton() {
        boolean isLollipop = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
        int selectPlace = isLollipop
                ? R.drawable.select_place_map_floating_bt_lollipop
                : R.drawable.select_place_map_floating_bt;
        int selectOptions = isLollipop
                ? R.drawable.search_settings_floating_bt_lollipop
                : R.drawable.search_settings_floating_bt;

        int selectPlaceBackground = getResources().getColor(R.color.activity_map_float_bt_linear_background);
        int selectSettingsBackground = getResources().getColor(R.color.activity_map_float_bt_grid_background);

        floatButton.addAnimation(selectPlaceBackground, selectPlace, LoadingView.FROM_TOP);
        floatButton.addAnimation(selectSettingsBackground, selectOptions, LoadingView.FROM_BOTTOM);
        /*floatButton.addAnimation(selectSettingsBackground, selectOptions, LoadingView.FROM_BOTTOM);
        floatButton.addAnimation(selectPlaceBackground, selectPlace, LoadingView.FROM_TOP);*/

        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaceSelected) {
                    buttonSettingsClick();
                } else {
                    UiUtils.showMessage(floatButton,
                            getString(R.string.ui_message_need_select_place));
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

    private void startMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.fl_activity_map_fragment_container);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);

            View zoomControls = mapFragment.getView().findViewById((0x1));

            if (zoomControls != null && zoomControls.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                RelativeLayout.LayoutParams params_zoom = (RelativeLayout.LayoutParams) zoomControls.getLayoutParams();

                params_zoom.addRule(RelativeLayout.CENTER_HORIZONTAL);
                params_zoom.addRule(RelativeLayout.ALIGN_PARENT_START);

                final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35,
                        getResources().getDisplayMetrics());
                params_zoom.setMargins(margin, margin, margin, margin);
            }

            initFloatButton();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSIONS_ID: {
                startMapFragment();
            }
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
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
    public void onMapClick(LatLng latLng) {
        if (!isPlaceSelected) {
            isPlaceSelected = true;
        }

        floatButton.startAnimation();
        onActivityCallback.onMapClick(latLng);
        if (marker == null) {
            marker = map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .alpha(Float.parseFloat(getString(R.string.map_fragment_map_marker_alpha))));
        } else {
            marker.setPosition(latLng);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        progressBar.setVisibility(View.GONE);
        map = googleMap;
        map.setOnMapClickListener(this);
        map.setOnMarkerClickListener(this);

        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setCompassEnabled(false);
        uiSettings.setMapToolbarEnabled(false);

        Context context = getContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                map.setMyLocationEnabled(true);
            } else {
                setDefaultMapSettings();
            }
        } else {
            map.setMyLocationEnabled(true);
        }

        if (savedMarkerPosition != null) {
            isPlaceSelected = true;
            floatButton.startAnimation();
            marker = map.addMarker(new MarkerOptions()
                    .position(savedMarkerPosition)
                    .alpha(Float.parseFloat(getString(R.string.map_fragment_map_marker_alpha))));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(savedMarkerPosition,
                    Float.parseFloat(getString(R.string.map_fragment_map_default_zoom))));
        }
    }

    private void buttonSettingsClick() {
        onActivityCallback.buttonSettingsClick();
    }

    private void setDefaultMapSettings() {
        LatLng defaultLatLng = new LatLng(Double.parseDouble(getString(R.string.default_latitude)),
                Double.parseDouble(getString(R.string.default_longitude)));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng,
                Float.parseFloat(getString(R.string.map_fragment_map_default_zoom))));

    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        onActivityCallback.onMapClick(marker.getPosition());
        return false;
    }

    public void onPlaceSelected(Place place) {
        if (map != null) {
            LatLng latLng = place.getLatLng();
            onMapClick(latLng);
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }

    public interface OnActivityCallback {
        void onMapClick(LatLng latLng);

        void buttonSettingsClick();
    }
}
