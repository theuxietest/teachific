package com.so.luotk.customviews;

import android.content.Context;
import android.util.AttributeSet;

import com.so.luotk.MyApplication;

public class CustomFontButton extends androidx.appcompat.widget.AppCompatButton {

    public CustomFontButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    public CustomFontButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomFontButton(Context context) {
        super(context);

        init();
    }

    private void init() {
   //  setTypeface(new MyApplication().openSans);//Set Typeface from MyApplication
      //  setTypeface(new MyApplication().robotoMedium);
        setTypeface(new MyApplication().productSansRegular);
    }
}
