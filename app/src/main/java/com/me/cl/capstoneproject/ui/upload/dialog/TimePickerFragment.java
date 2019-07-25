package com.me.cl.capstoneproject.ui.upload.dialog;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;

import java.util.Calendar;

/**
 * Created by CL on 12/1/17.
 */

public class TimePickerFragment extends DialogFragment {

    private  TimePickerDialog.OnTimeSetListener onTimeSetListener;

    public TimePickerFragment() {
    }

    public static TimePickerFragment newInstance(TimePickerDialog.OnTimeSetListener onTimeSetListener) {
        Bundle args = new Bundle();
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        fragment.setOnTimeSetListener(onTimeSetListener);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), onTimeSetListener, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void setOnTimeSetListener(TimePickerDialog.OnTimeSetListener onTimeSetListener) {
        this.onTimeSetListener = onTimeSetListener;
    }
}
