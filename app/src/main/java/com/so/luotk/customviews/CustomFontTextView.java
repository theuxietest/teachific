package com.so.luotk.customviews;

import android.content.Context;
import android.util.AttributeSet;


import com.so.luotk.MyApplication;

public class CustomFontTextView extends androidx.appcompat.widget.AppCompatTextView {

    public CustomFontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    public CustomFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomFontTextView(Context context) {
        super(context);

        init();
    }

    private void init() {
        // setTypeface(new MyApplication().openSans);//Set Typeface from MyApplication
        // setTypeface(new MyApplication().robotoRegular);
//        setTypeface(new MyApplication().latoRegular);
        setTypeface(new MyApplication().lato);
    }
}
