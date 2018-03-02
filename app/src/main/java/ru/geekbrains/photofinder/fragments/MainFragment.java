package ru.geekbrains.photofinder.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
    private OnActivityCallback onActivityCallback;

    public MainFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        noLoginButton = view.findViewById(R.id.bt_main_fragment_no_login_vk);
        noLoginButton.setOnClickListener(this);

        loginButton = view.findViewById(R.id.bt_main_fragment_login_vk);
        loginButton.setOnClickListener(this);
        return view;
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

    }
}
