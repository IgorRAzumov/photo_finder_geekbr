package ru.geekbrains.photofinder.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import ru.geekbrains.photofinder.R;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener {
    private static final int REQUEST_LOCATION_PERMISSIONS_ID = 0;
    private GoogleMap map;
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        accessToken = getIntent().getStringExtra(getString(R.string.vk_access_token_intent_key));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fr_map_fragment);
        if (mapFragment != null) {////!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            mapFragment.getMapAsync(this);
        }
    }

    @SuppressLint("MissingPermission")
//не знаю почему, но линт считает, что мы не проверяем полученные разрешения, а мы - проверяем
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSIONS_ID: {
                if (permissions.length == 1 &&
                        permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    map.setMyLocationEnabled(true);
                } else {
                    setDefaultMapLatLong();
                }
                break;
            }
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    private void setDefaultMapLatLong() {
        LatLng defaultLatLng = new LatLng(Double.parseDouble(getString(R.string.default_latitude)),
                Double.parseDouble(getString(R.string.default_longitude)));
        float zoom = Float.parseFloat(getString(R.string.default_zoom));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, zoom));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                map.setMyLocationEnabled(true);
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION_PERMISSIONS_ID);
            }
        } else {
            map.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Intent intent = new Intent(this, ListResultActivity.class);
        intent.putExtra(getString(R.string.latitude_intent_key), latLng.latitude);
        intent.putExtra(getString(R.string.longitude_intent_key), latLng.longitude);
        startActivity(intent);
    }
}
