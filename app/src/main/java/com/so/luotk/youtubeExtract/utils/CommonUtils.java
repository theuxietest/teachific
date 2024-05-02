package com.so.luotk.youtubeExtract.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.TypedValue;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.code.regexp.Matcher;
import com.google.code.regexp.Pattern;
import com.so.luotk.R;
import com.so.luotk.utils.NetworkType;

import org.joda.time.Period;

import java.text.ChoiceFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class CommonUtils {

    public static Matcher getMatcher(String stringPattern, String stringMatcher) {
        Pattern pattern = Pattern.compile(stringPattern);
        return pattern.matcher(stringMatcher);
    }

    public static String matchWithPatterns(List<Pattern> patterns, String inputString) {
        String outputString = null;
        Matcher matcher;
        Iterator<Pattern> iter = patterns.iterator();
        while (outputString == null && iter.hasNext()) {
            matcher = iter.next().matcher(inputString);
            if (matcher.find()) {
                // Restarting the search
                matcher.find(0);
                outputString = matcher.group(1);
            }
        }
        return outputString;
    }

    public static void LogI(String tag, String message) {
        Log.i(tag, message);
    }

    public static void LogE(String tag, String message) {
        Log.e(tag, message);
    }


    public String getCountryCode(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getNetworkCountryIso();
    }

    public NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    public NetworkType getNetworkClass(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        if (info == null || !info.isConnected())
            return NetworkType.TYPE_NOT_CONNECTED; //not connected
        if (info.getType() == ConnectivityManager.TYPE_WIFI)
            return NetworkType.TYPE_WIFI;
        if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            int networkType = info.getSubtype();
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                    return NetworkType.TYPE_2G;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                case TelephonyManager.NETWORK_TYPE_TD_SCDMA:  //api<25 : replace by 17
                    return NetworkType.TYPE_3G;
                case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                case TelephonyManager.NETWORK_TYPE_IWLAN:  //api<25 : replace by 18
                case 19:  //LTE_CA
                    return NetworkType.TYPE_4G;
                default:
                    return NetworkType.TYPE_NOT_CONNECTED;
            }
        }
        return NetworkType.TYPE_NOT_CONNECTED;
    }

    public boolean isNetworkAvailable(Context context) {
        NetworkInfo networkInfo = getNetworkInfo(context);
        return networkInfo != null && networkInfo.isConnected();
    }

    public String parseISO8601time(String iso8601time) {
        return DateUtils.formatElapsedTime(Period.parse(iso8601time).toStandardDuration().getStandardSeconds());
    }

    public String convertMillsIntoTimeString(int timeMs) {
        StringBuilder mFormatBuilder;
        mFormatBuilder = new StringBuilder();

        int seconds = timeMs % 60;
        int minutes = (timeMs / 60) % 60;
        int hours = timeMs / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    public String formatYtViewsAndLikesString(String rawString) {
        final char nonBreakableSpaceChar = (char) 160;
        final char commaChar = (char) 44;
        final char zeroChar = '0';
        if (rawString != null) {
            Double viewCount = Double.valueOf(rawString);
            String label = new ChoiceFormat("0# |100000#K|1000000#M|1000000000#B").format(viewCount);
            String formattedLine = NumberFormat.getInstance().format(viewCount);
            formattedLine = formattedLine.replace(nonBreakableSpaceChar, commaChar);
            int strLength = formattedLine.length();
            switch (label) {
                case " ":
                    return formattedLine;
                case "K": {
                    return formattedLine.substring(0, 3) + label;
                }
                case "M": {
                    if (strLength == 8) {
                        return formattedLine.substring(0, 1) + label;
                    } else if (strLength == 9 && formattedLine.charAt(2) != zeroChar) {
                        return formattedLine.substring(0, strLength - 6) + label;
                    } else {
                        return formattedLine.substring(0, strLength - 8) + label;
                    }
                }
                case "B": {
                    if (strLength == 13) {
                        if (formattedLine.charAt(2) != zeroChar) {
                            return formattedLine.substring(0, strLength - 10) + label;
                        } else {
                            return formattedLine.substring(0, strLength - 12) + label;
                        }
                    } else {
                        return formattedLine.substring(0, strLength - 11) + label;
                    }
                }
                default:
                    return formattedLine;
            }
        } else return "";
    }

    public float dpInPx(int dp, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public int getAndroidVersion() {
        return Build.VERSION.SDK_INT;
    }

    public boolean isServiceRunning(Class tClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (tClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void sendLocalBroadcastMessage(String action, Bundle bundle, Context context) {
        Intent intent = new Intent(action);
        intent.putExtras(bundle);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        localBroadcastManager.sendBroadcast(intent);
    }

    public AlertDialog createAlertDialog(int titleId, int messageId, boolean isCancelable,
                                         int positiveButtonId, DialogInterface.OnClickListener positiveCallback,
                                         int negativeButtonId, DialogInterface.OnClickListener negativeCallback,
                                         Context context) {
        AppCompatActivity activity = (AppCompatActivity) context;
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AlertDialogTheme));
        builder = builder.setTitle(titleId)
                .setMessage(messageId);
        if (isCancelable) {
            builder.setCancelable(true);
        } else {
            builder.setCancelable(false);
        }
        if (positiveButtonId != 0 && positiveCallback != null) {
            builder.setPositiveButton(positiveButtonId, positiveCallback);
        }
        if (negativeButtonId != 0 && negativeCallback != null) {
            builder.setNegativeButton(negativeButtonId, negativeCallback);
        }
        return builder.create();
    }

    public AlertDialog createAlertDialog(String title, String message, boolean isCancelable,
                                         int positiveButtonId, DialogInterface.OnClickListener positiveCallback,
                                         int negativeButtonId, DialogInterface.OnClickListener negativeCallback,
                                         Context context) {
        AppCompatActivity activity = (AppCompatActivity) context;
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AlertDialogTheme));
        builder = builder.setTitle(title)
                .setMessage(message);
        if (isCancelable) {
            builder.setCancelable(true);
        } else {
            builder.setCancelable(false);
        }
        if (positiveButtonId != 0 && positiveCallback != null) {
            builder.setPositiveButton(positiveButtonId, positiveCallback);
        }
        if (negativeButtonId != 0 && negativeCallback != null) {
            builder.setNegativeButton(negativeButtonId, negativeCallback);
        }
        return builder.create();
    }

    public Bitmap getBitmapFromVectorDrawable(int drawableId, Context context) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public String createChannelId() {
        return String.valueOf(Integer.parseInt(new SimpleDateFormat("ddHHmmss", Locale.US).format(new Date())));
    }
}
