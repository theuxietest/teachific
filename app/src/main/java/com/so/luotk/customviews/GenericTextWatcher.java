package com.so.luotk.customviews;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.so.luotk.R;

public class GenericTextWatcher implements TextWatcher {
    private int previousLength;
    private boolean backSpace;
    private final EditText[] editText;
    private final View view;
    public GenericTextWatcher(View view, EditText[] editText)
    {
        this.editText = editText;
        this.view = view;
    }

    @Override
    public void afterTextChanged(Editable editable) {
        String text = editable.toString();
        backSpace = previousLength > editable.length();
        Log.d("TAGTAG", "afterTextChanged: " + backSpace);
        if (backSpace) {

            // do your stuff ...

        }
        switch (view.getId()) {

            case R.id.otp1:
                if (text.length() == 1)
                    editText[1].requestFocus();
                break;
            case R.id.otp2:

                if (text.length() == 1)
                    editText[2].requestFocus();
                else if (text.length() == 0)
                    if (backSpace) {
                        editText[0].requestFocus();
                    }
                break;
            case R.id.otp3:
                if (text.length() == 1)
                    editText[3].requestFocus();
                else if (text.length() == 0)
                    if (backSpace) {
                        editText[1].requestFocus();
                    }
                break;
            case R.id.otp4:
                if (text.length() == 0)
                    if (backSpace) {
                        editText[2].requestFocus();
                    }
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        previousLength = arg0.length();
    }

    @Override
    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }
}