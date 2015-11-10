package com.beintoo.utils.ui;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.Calendar;

public class BeTextWatcher implements TextWatcher {

    public interface CardInsertListener {
        public void onCardNumberInserted(boolean validMonth, boolean validYear);

        public void onCardNumberInvalid();
    }

    private EditText etBox1;
    private EditText etBox2;
    private EditText etBox3;
    private EditText etBox4;

    private EditText etMonth;
    private EditText etYear;

    private CardInsertListener mListener;

    public BeTextWatcher(EditText etBox1, EditText etBox2, EditText etBox3, EditText etBox4, EditText etMonth, EditText etYear, CardInsertListener listener) {
        this.etBox1 = etBox1;
        this.etBox2 = etBox2;
        this.etBox3 = etBox3;
        this.etBox4 = etBox4;
        this.etMonth = etMonth;
        this.etYear = etYear;
        this.mListener = listener;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        boolean b1, b2, b3, b4, month, year;
        b1 = b2 = b3 = b4 = month = year = false;

        if (etBox1.getText().length() == 4) {
            b1 = true;
            if (etBox2.getText().length() < 4) {
                etBox2.requestFocus();
            }
        }

        if (etBox2.getText().length() == 4) {
            b2 = true;
            if (etBox3.getText().length() < 4) {
                etBox3.requestFocus();
            }
        }

        if (etBox3.getText().length() == 4) {
            b3 = true;
            if (etBox4.getText().length() < 4) {
                etBox4.requestFocus();
            }
        }

        if (etBox4.getText().length() >= 2) {
            b4 = true;
        }

        int monthVal = 0;
        try {
            monthVal = Integer.parseInt(etMonth.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (etMonth.getText().length() == 2 && monthVal > 0 && monthVal <= 12) {
            month = true;
        }

        int yearVal = 0;
        try {
            yearVal = Integer.parseInt(etYear.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (etYear.getText().length() == 4 && yearVal > Calendar.getInstance().get(Calendar.YEAR)) {
            year = true;
        } else if(etYear.getText().length() == 4 && yearVal == Calendar.getInstance().get(Calendar.YEAR) && monthVal > Calendar.getInstance().get(Calendar.MONTH)) {
            year = true;
        }

        if (mListener != null) {
            if (b1 && b2 && b3 && b4) {
                mListener.onCardNumberInserted(month, year);
            } else {
                mListener.onCardNumberInvalid();
            }
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
