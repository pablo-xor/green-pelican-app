package com.paulsoft.service;

import android.widget.EditText;
import android.widget.TextView;

public final class UiControlHelper {

    public static String getValue(EditText editText) {
        return editText.getText().toString();
    }

    public static void setValue(String value, EditText editText) {
        editText.setText(value, TextView.BufferType.NORMAL);
    }


}
