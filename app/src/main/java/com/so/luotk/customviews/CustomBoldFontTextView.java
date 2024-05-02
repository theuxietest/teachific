package com.so.luotk.customviews;

import android.content.Context;
import android.util.AttributeSet;

import com.so.luotk.MyApplication;

public class CustomBoldFontTextView extends androidx.appcompat.widget.AppCompatTextView {

    public CustomBoldFontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    public CustomBoldFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomBoldFontTextView(Context context) {
        super(context);

        init();
    }

    private void init() {
       // setTypeface(new MyApplication().openSansBold);//Set Typeface from MyApplication
       // setTypeface(new MyApplication().robotoBold);
        setTypeface(new MyApplication().latoBold);
    }
}
