package com.so.luotk.utils;

import android.content.Context;
import android.graphics.Typeface;

public class FontTypeFontAwseme {
    private static Typeface fontAwsome, poppinsSemibold;

    public static Typeface getFontAwsome(Context context) {
        fontAwsome = Typeface.createFromAsset(context.getAssets(), "fontawesome-webfont.ttf");
        return fontAwsome;
    }

    public static Typeface getSemiBoldFontAwsome(Context context) {
        poppinsSemibold = Typeface.createFromAsset(context.getAssets(), "font/poppins_semibold.ttf");
        return poppinsSemibold;
    }
}
