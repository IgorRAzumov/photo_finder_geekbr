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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import ru.geekbrains.photofinder.R;


public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {
    private static final int REQUEST_LOCATION_PERMISSIONS_ID = 13;

    private ProgressBar progressBar;
    private GoogleMap map;
    private OnActivityCallback onActivityCallback;
    private Marker marker;


    public MapFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        progressBar = view.findViewById(R.id.pb_fragment_map_progress);
        progressBar.setVisibility(View.VISIBLE);

        return view;
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

    private void startMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.fl_map_fragment_container);
        if (mapFragment != null) {////!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            mapFragment.getMapAsync(this);
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
        if (onActivityCallback != null) {
            onActivityCallback.onMapClick(latLng);
            if (marker == null) {
                marker = map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .alpha(0.7f));
            } else {
                marker.setPosition(latLng);
            }

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
    }

    private void setDefaultMapSettings() {
        LatLng defaultLatLng = new LatLng(Double.parseDouble(getString(R.string.default_latitude)),
                Double.parseDouble(getString(R.string.default_longitude)));
        float zoom = Float.parseFloat(getString(R.string.default_zoom));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, zoom));

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

    }
}
