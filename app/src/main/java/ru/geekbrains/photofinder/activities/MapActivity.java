package ru.geekbrains.photofinder.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

import io.saeid.fabloading.LoadingView;
import ru.geekbrains.photofinder.R;
import ru.geekbrains.photofinder.fragments.MapFragment;
import ru.geekbrains.photofinder.utils.UiUtils;

public class MapActivity extends AppCompatActivity implements
        ru.geekbrains.photofinder.fragments.MapFragment.OnActivityCallback, SharedPreferences.OnSharedPreferenceChangeListener {
    private static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    private FrameLayout rootView;
    private LoadingView floatButton;

    private boolean isPlaceSelected;
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        rootView = findViewById(R.id.fl_activity_map_fragment_container);
        floatButton = findViewById(R.id.fbt_map_fragment);

        Intent intent = getIntent();
        if (intent != null) {
            accessToken = getIntent().getStringExtra(getString(R.string.vk_access_token_key));
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fl_activity_map_fragment_container);
        if (fragment == null) {
            fragment = new MapFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fl_activity_map_fragment_container, fragment)
                    .commit();

        }

        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
        shared.registerOnSharedPreferenceChangeListener(this);

        initFloatButton();
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

        floatButton.addAnimation(selectSettingsBackground, selectOptions, LoadingView.FROM_BOTTOM);
        floatButton.addAnimation(selectPlaceBackground, selectPlace, LoadingView.FROM_TOP);


        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaceSelected) {
                    actionSettingsClick();
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

    private void actionSettingsClick() {
        Intent intent = new Intent(this, SettingsSearchActivity.class);
        startActivity(intent);
    }

    private void actionSearchClick() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            showErrorMessage(getString(R.string.error_request_autocomplite));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment fragment = fragmentManager.findFragmentById(R.id.fl_activity_map_fragment_container);
                if (fragment != null & fragment instanceof MapFragment) {
                    MapFragment mapFragment = (MapFragment) fragment;
                    mapFragment.onPlaceSelected(place);
                }

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                showErrorMessage(getString(R.string.error_request_autocomplite));
            }
        }
    }

    private void showErrorMessage(String errorMessage) {
        UiUtils.showMessage(rootView, errorMessage);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (isPlaceSelected) {
            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra(getString(R.string.latitude_intent_key), latLng.latitude);
            intent.putExtra(getString(R.string.longitude_intent_key), latLng.longitude);
            if (accessToken != null && !TextUtils.isEmpty(accessToken)) {
                intent.putExtra(getString(R.string.vk_access_token_key), accessToken);
            }
            startActivity(intent);
        } else {
            isPlaceSelected = true;
            floatButton.startAnimation();
        }

    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }
}
