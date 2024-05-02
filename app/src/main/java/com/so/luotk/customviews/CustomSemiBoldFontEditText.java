package com.so.luotk.customviews;

import android.content.Context;
import android.util.AttributeSet;

import com.so.luotk.MyApplication;

public class CustomSemiBoldFontEditText extends androidx.appcompat.widget.AppCompatEditText {

    public CustomSemiBoldFontEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    public CustomSemiBoldFontEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomSemiBoldFontEditText(Context context) {
        super(context);

        init();
    }

    private void init() {
       // setTypeface(new MyApplication().robotoRegular);//Set Typeface from MyApplication
        setTypeface(new MyApplication().productSansMedium);
    }
}
