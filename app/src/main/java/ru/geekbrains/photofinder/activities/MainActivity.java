package ru.geekbrains.photofinder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import ru.geekbrains.photofinder.R;

public class MainActivity extends AppCompatActivity {
    private static int PHOTO_SEARCH_LOADER_ID = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //наверное, не совсем логично сделал что метод авторизации вызывается в фрагменте, а колбек от него прилетает сюда
    //вся проблема в том, что методу loginVk() можно подать фрагмент на вход(но, фрагмент -
    // не из support ,библиотеки) поэтому в метод подается activity, а вызывается он сам в фрагменте
    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                String accessTokenIntentKey = getString(R.string.vk_access_token_intent_key);
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

    private void showErrorMessage(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }
}
