package ru.geekbrains.photofinder.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;

import ru.geekbrains.photofinder.R;

public class MainFragment extends Fragment implements View.OnClickListener {
    private final static String[] SCOPE = new String[]{VKScope.PHOTOS, VKScope.OFFLINE};

    private Button noLoginButton;
    private Button loginButton;
    private Button refreshButton;

    private OnActivityCallback onActivityCallback;

    public MainFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        noLoginButton = view.findViewById(R.id.bt_main_fragment_no_login_vk);
        loginButton = view.findViewById(R.id.bt_main_fragment_login_vk);
        refreshButton = view.findViewById(R.id.bt_main_fragment_refresh);

        noLoginButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        refreshButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!onActivityCallback.isOnline()) {
            noLoginButton.setEnabled(getResources()
                    .getBoolean(R.bool.main_fragment_no_login_button_no_network_visible));
            loginButton.setEnabled(getResources()
                    .getBoolean(R.bool.main_fragment_login_button_no_network));
            refreshButton.setVisibility(View.VISIBLE);
            onActivityCallback.showErrorMessage(getString(R.string.error_no_network));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_main_fragment_no_login_vk: {
                onActivityCallback.noLoginSelected();
                break;
            }
            case R.id.bt_main_fragment_login_vk: {
                if (getActivity() != null) {
                    VKSdk.login(getActivity(), SCOPE);
                }
                break;
            }
            case R.id.bt_main_fragment_refresh: {
                refreshNetworkState();
                break;
            }
        }
    }

    public void errorAuth() {

    }

    private void refreshNetworkState() {
        if (onActivityCallback.isOnline()) {
            noLoginButton.setEnabled(getResources().getBoolean(
                    R.bool.main_fragment_no_login_button_on_network_visible));
            loginButton.setEnabled(getResources().getBoolean(
                    R.bool.main_fragment_login_button_on_network_visible));
            refreshButton.setVisibility(View.GONE);

        } else {
            onActivityCallback.showErrorMessage(getString(R.string.error_no_network));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainFragment.OnActivityCallback) {
            onActivityCallback = (MainFragment.OnActivityCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + getString(R.string.on_activity_callback__fragment_error) +
                    this.getClass().getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onActivityCallback = null;
    }


    public interface OnActivityCallback {
        void noLoginSelected();

        boolean isOnline();

        void showErrorMessage(String message);
    }
}
