package ru.geekbrains.photofinder.preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;

import ru.geekbrains.photofinder.R;

/**
 * Created by XXX on 22.02.2018.
 */

public class DatePreference extends DialogPreference{

    private int mTime;


   // private int mDialogLayoutResId = R.layout.pref_dialog_time;

    public DatePreference(Context context) {
        // Delegate to other constructor
        this(context, null);
    }

    public DatePreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.preferenceStyle);
    }

    public DatePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, defStyleAttr);
    }

    public DatePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

    }



}

