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
                String accessTokenIntentKey = getString(R.string.vk_access_token_key);
                intent.putExtra(accessTokenIntentKey, res.accessToken);
                startActivity(intent);
            }

            @Override
            public void onError(VKError error) {
                showErrorMessage(getString(R.string.authorization_error));
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return true;
        }

        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void showErrorMessage(String errorMessage) {

        UiUtils.showMessage(rootView, errorMessage);
    }

    @Override
    public void noLoginSelected() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }
}
