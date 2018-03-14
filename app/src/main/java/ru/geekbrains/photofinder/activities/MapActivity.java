package ru.geekbrains.photofinder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

import ru.geekbrains.photofinder.R;
import ru.geekbrains.photofinder.fragments.MapFragment;
import ru.geekbrains.photofinder.utils.UiUtils;

public class MapActivity extends AppCompatActivity implements
        ru.geekbrains.photofinder.fragments.MapFragment.OnActivityCallback {
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    private FrameLayout rootView;

    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        rootView = findViewById(R.id.fl_activity_map_fragment_container);


        checkSavedAccessToken(savedInstanceState);
        if (accessToken == null || TextUtils.isEmpty(accessToken)) {
            getDataFromIntent();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fl_activity_map_fragment_container);
        if (fragment == null) {
            fragment = new MapFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fl_activity_map_fragment_container, fragment)
                    .commit();

        }
    }

    private void checkSavedAccessToken(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            accessToken = savedInstanceState.getString(getString(R.string.vk_access_token_bundle_key),
                    getString(R.string.vk_access_token_empty_value));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (accessToken != null && !TextUtils.isEmpty(accessToken)) {
            outState.putString(getString(R.string.vk_access_token_bundle_key), accessToken);
        }
        super.onSaveInstanceState(outState);
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            accessToken = getIntent().getStringExtra(getString(R.string.vk_access_token_bundle_key));
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
            case R.id.action_search_Address: {
                actionSearchClick();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private void actionSearchClick() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            UiUtils.showMessage(rootView, getString(R.string.error_request_autocomplete));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PLACE_AUTOCOMPLETE_REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
                    Place place = PlaceAutocomplete.getPlace(this, data);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    Fragment fragment = fragmentManager.findFragmentById(R.id.fl_activity_map_fragment_container);
                    if (fragment != null & fragment instanceof MapFragment) {
                        MapFragment mapFragment = (MapFragment) fragment;
                        mapFragment.onPlaceSelected(place);
                    }
                }
                break;
            }
            case PlaceAutocomplete.RESULT_ERROR: {
                UiUtils.showMessage(rootView, getString(R.string.error_request_autocomplete));
                break;
            }
        }

    }

    @Override
    public void onMapClick(LatLng latLng) {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(getString(R.string.latitude_intent_key), latLng.latitude);
        intent.putExtra(getString(R.string.longitude_intent_key), latLng.longitude);
        if (accessToken != null && !TextUtils.isEmpty(accessToken)) {
            intent.putExtra(getString(R.string.vk_access_token_bundle_key), accessToken);
        }

        startActivity(intent);
    }

    @Override
    public void buttonSettingsClick() {
        Intent intent = new Intent(this, SettingsSearchActivity.class);
        startActivity(intent);
    }
}
