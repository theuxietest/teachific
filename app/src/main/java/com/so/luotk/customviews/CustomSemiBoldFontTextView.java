package com.so.luotk.customviews;

import android.content.Context;
import android.util.AttributeSet;

import com.so.luotk.MyApplication;

public class CustomSemiBoldFontTextView extends androidx.appcompat.widget.AppCompatTextView {

    public CustomSemiBoldFontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    public CustomSemiBoldFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomSemiBoldFontTextView(Context context) {
        super(context);

        init();
    }

    private void init() {
//        setTypeface(new MyApplication().openSansSemiBold);//Set Typeface from MyApplication
   //  setTypeface(new MyApplication().robotoMedium);
//        setTypeface(new MyApplication().latoRegular);
        setTypeface(new MyApplication().latoSemiBold);
    }


}
