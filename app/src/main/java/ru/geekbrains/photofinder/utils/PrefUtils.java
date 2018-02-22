package ru.geekbrains.photofinder.utils;

import android.content.Context;
import android.content.SharedPreferences;

import ru.geekbrains.photofinder.R;
import ru.geekbrains.photofinder.adapters.ListResultAdapter;



public class PrefUtils {
    //private static final
    public static void writeAdapterTypeToSharedPref(Context context, int type) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.app_pref), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(R.string.list_result_recycle_view_type_pref_key), type);
        editor.apply();
    }


    public static int getViewTypeForPreference(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.app_pref), Context.MODE_PRIVATE);

        return sharedPreferences.getInt(context.getString(R.string.list_result_recycle_view_type_pref_key),
                ListResultAdapter.LINEAR_TYPE);
    }
}
