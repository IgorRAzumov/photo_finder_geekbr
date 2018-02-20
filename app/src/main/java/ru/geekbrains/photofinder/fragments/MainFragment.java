package ru.geekbrains.photofinder.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import ru.geekbrains.photofinder.R;
import ru.geekbrains.photofinder.activities.MainActivity;
import ru.geekbrains.photofinder.activities.MapActivity;

public class MainFragment extends Fragment implements View.OnClickListener {
    private final static String[] SCOPE = new String[]{VKScope.PHOTOS, VKScope.OFFLINE};

    private Button loginButton;

    public MainFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        loginButton = view.findViewById(R.id.bt_login_vk);
        loginButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {// вот об этом вызове я в main activity писал
      //   VKSdk.login(getActivity(), SCOPE);

        Intent intent = new Intent(getActivity(), MapActivity.class);
        String accessTokenIntentKey = getString(R.string.vk_access_token_intent_key);
        intent.putExtra(accessTokenIntentKey, "");
        startActivity(intent);
    }

}
