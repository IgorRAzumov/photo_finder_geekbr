package ru.geekbrains.photofinder.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ru.geekbrains.photofinder.R;
import ru.geekbrains.photofinder.adapters.PhotoResultAdapter;



public class PrefUtils {
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
                PhotoResultAdapter.LINEAR_TYPE);
    }


    public static String getSearchSortForPreference(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPreferences.getString(context.getString(R.string.pref_sort_by__key),
                context.getString(R.string.pref_sort_by_date_default_value));
    }

    public static int getSearchRadiusForPreference(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPreferences.getInt(context.getString(R.string.pref_radius__key),
                context.getResources().getInteger(R.integer.pref_radius_defaul_value));
    }

    public static String  getSearchStartForPreference(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPreferences.getString(context.getString(R.string.pref_date_start_key),
               context.getString(R.string.pref_date_min_date));
    }

    public static String  getSearchEndForPreference(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPreferences.getString(context.getString(R.string.pref_date_end_key),
              "");
    }


}
