package ru.geekbrains.photofinder.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import ru.geekbrains.photofinder.R;
import ru.geekbrains.photofinder.fragments.MainFragment;

public class MainActivity extends AppCompatActivity implements MainFragment.OnActivityCallback {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                showErrorMessage(error.errorMessage);
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
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void noLoginSelected() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }
}
