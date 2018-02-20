package ru.geekbrains.photofinder.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import ru.geekbrains.photofinder.R;
import ru.geekbrains.photofinder.adapters.ListResultAdapter;
import ru.geekbrains.photofinder.fragments.ListResultFragment;

public class ListResultActivity extends AppCompatActivity implements
        ListResultFragment.OnActivityCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_result);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager
                .findFragmentById(R.id.fl_list_result_container);
        if (fragment == null) {
            fragment = new ListResultFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fl_list_result_container, fragment)
                    .commit();
        }

    }

    @Override
    public void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}
