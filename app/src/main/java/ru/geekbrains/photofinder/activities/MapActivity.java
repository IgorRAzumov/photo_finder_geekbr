package ru.geekbrains.photofinder.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;

import ru.geekbrains.photofinder.R;
import ru.geekbrains.photofinder.fragments.MapFragment;
import ru.geekbrains.photofinder.fragments.ResultListFragment;

public class MapActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener,
        ru.geekbrains.photofinder.fragments.MapFragment.OnActivityCallback {
//    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
//        accessToken = getIntent().getStringExtra(getString(R.string.vk_access_token_intent_key));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        getResources().getInteger(R.integer.request_location_permission_id));
            }
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fl_map_fragment_container);
        if (fragment == null) {
            fragment = new MapFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fl_map_fragment_container, fragment)
                    .commit();
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
    //вопрос - каждый ли раз, когда мы так стартуем активити с вложенным в него лоадером
    // - будет запускаться новый лоадер? Вроде, как читал и дебажил - создается новый.
    // Просто в чем суть - если создается новый каждый раз - все нормю Тогда мы не обрабатываем
    // изменения в Preference в этом классе. А запрашиваем их из них значения при новом запросе
    @Override
    public void onMapClick(LatLng latLng) {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(getString(R.string.latitude_intent_key), latLng.latitude);
        intent.putExtra(getString(R.string.longitude_intent_key), latLng.longitude);
        startActivity(intent);
    }

    @Override
    public void onMapReady() {

    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(getString(R.string.pref_sort_category_key))) {

        }

        if (s.equals(getString(R.string.pref_radius_category_key))) {

        }

        if (s.equals(getString(R.string.pref_date_category_key))) {

        }


    }
}
