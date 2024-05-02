package com.so.luotk.utils;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UsbReceiver extends BroadcastReceiver {
    private final String TAG = "status....";
    private static final String ACTION_USB_PERMISSION =
            "com.smartowls.smartowls.USB_PERMISSION";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_USB_PERMISSION.equals(intent.getAction())) {
            context.sendBroadcast(intent);
        }

    }

}
