package com.beintoo.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public interface DateListener {
        public void onDatePicked(Date date);
    }

    private DateListener mCallback;
    private Date mDefaultDate;

    private boolean mLockToThirteen = false;

    public DatePickerFragment(DateListener callback){
        mCallback = callback;
    }

    public DatePickerFragment(DateListener callback, boolean lockToThirteen){
        mCallback = callback;
        mLockToThirteen = true;
    }

    public DatePickerFragment(DateListener callback, Date defaultDate, boolean lockToThirteen){
        mCallback = callback;
        mDefaultDate = defaultDate;
        mLockToThirteen = lockToThirteen;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        if(mLockToThirteen)
            c.add(Calendar.YEAR, -13);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = null;
        if(mDefaultDate == null){
            datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        }else{
            final Calendar defaultDate = Calendar.getInstance();
            defaultDate.setTime(mDefaultDate);
            datePickerDialog = new DatePickerDialog(getActivity(), this, defaultDate.get(Calendar.YEAR), defaultDate.get(Calendar.MONTH), defaultDate.get(Calendar.DAY_OF_MONTH));
        }

        datePickerDialog.setInverseBackgroundForced(true);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            datePickerDialog.getDatePicker().setMaxDate(c.getTime().getTime());
        }

        return datePickerDialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);

        mCallback.onDatePicked(cal.getTime());
    }
}