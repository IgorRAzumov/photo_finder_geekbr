package ru.geekbrains.photofinder.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import ru.geekbrains.photofinder.R;
import ru.geekbrains.photofinder.fragments.ResultListFragment;
import ru.geekbrains.photofinder.fragments.ResultViewPagerFragment;

public class ResultActivity extends AppCompatActivity implements
        ResultListFragment.OnActivityCallback, ResultViewPagerFragment.onActivityCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_result);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager
                .findFragmentById(R.id.fl_list_result_container);
        if (fragment == null) {
            fragment = new ResultListFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fl_list_result_container, fragment)
                    .commit();
        }

    }

    @Override
    public void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void switchRecyclerToViewPager(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ResultViewPagerFragment resultViewPagerFragment = new ResultViewPagerFragment();

        fragmentManager.beginTransaction()
                .replace(R.id.fl_list_result_container, resultViewPagerFragment)
                .addToBackStack(null)
                .commit();

    }

}
