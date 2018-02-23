package ru.geekbrains.photofinder.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import ru.geekbrains.photofinder.R;
import ru.geekbrains.photofinder.utils.PrefUtils;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener,SharedPreferences.OnSharedPreferenceChangeListener{
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSIONS_ID: {
                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_find_settings: {
                Intent intent = new Intent(this, SettingsSearchActivity.class);
                startActivity(intent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
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
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(getString(R.string.latitude_intent_key), latLng.latitude);
        intent.putExtra(getString(R.string.longitude_intent_key), latLng.longitude);
        startActivity(intent);
    }

    private void setDefaultMapLatLong() {
        LatLng defaultLatLng = new LatLng(Double.parseDouble(getString(R.string.default_latitude)),
                Double.parseDouble(getString(R.string.default_longitude)));
        float zoom = Float.parseFloat(getString(R.string.default_zoom));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, zoom));

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if(s.equals(getString(R.string.pref_sort_category_key))){

        }

        if(s.equals(getString(R.string.pref_radius_category_key))){

        }

        if(s.equals(getString(R.string.pref_date_category_key))){

        }


    }
}
