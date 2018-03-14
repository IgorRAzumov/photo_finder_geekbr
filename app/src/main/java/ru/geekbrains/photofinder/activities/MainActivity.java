package ru.geekbrains.photofinder.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import ru.geekbrains.photofinder.R;
import ru.geekbrains.photofinder.fragments.MainFragment;
import ru.geekbrains.photofinder.utils.UiUtils;

public class MainActivity extends AppCompatActivity implements MainFragment.OnActivityCallback {
    private FrameLayout rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootView = findViewById(R.id.fl_main_activity_root_view);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                String accessTokenIntentKey = getString(R.string.vk_access_token_bundle_key);
                intent.putExtra(accessTokenIntentKey, res.accessToken);
                startActivity(intent);
            }

            @Override
            public void onError(VKError error) {
                UiUtils.showMessage(rootView, getString(R.string.authorization_error));
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public boolean isOnline() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();

        }
        return false;
    }


    @Override
    public void noLoginSelected() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }
}
