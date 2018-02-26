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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import ru.geekbrains.photofinder.R;


public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener {
    private ProgressBar progressBar;
    private GoogleMap map;
    private OnActivityCallback onActivityCallback;
    public MapFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        progressBar = view.findViewById(R.id.pb_fragment_map_progress);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.fl_map_fragment_container);
        if (mapFragment != null) {////!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            mapFragment.getMapAsync(this);
            progressBar.setVisibility(View.VISIBLE);
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
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        progressBar.setVisibility(View.GONE);
        map = googleMap;
        map.setOnMapClickListener(this);

        Context context = getContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                map.setMyLocationEnabled(true);
            } else {
                setDefaultMapSettings();
            }
        }
    }

    private void setDefaultMapSettings() {
        LatLng defaultLatLng = new LatLng(Double.parseDouble(getString(R.string.default_latitude)),
                Double.parseDouble(getString(R.string.default_longitude)));
        float zoom = Float.parseFloat(getString(R.string.default_zoom));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, zoom));

    }


    public interface OnActivityCallback {
        void onMapClick(LatLng latLng);

    }
}
