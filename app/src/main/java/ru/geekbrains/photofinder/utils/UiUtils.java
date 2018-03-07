package ru.geekbrains.photofinder.utils;


import android.support.design.widget.Snackbar;
import android.view.View;

public class UiUtils {
    public static void showMessage(View parentLayout, String message) {
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG).show();
    }
}
