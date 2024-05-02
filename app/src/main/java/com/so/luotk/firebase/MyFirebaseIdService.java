package com.so.luotk.firebase;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.so.luotk.utils.PreferenceHandler;


public class MyFirebaseIdService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("Firebase:", "Refreshed token: " + token);
        PreferenceHandler.writeString(getApplicationContext(), PreferenceHandler.FCM_TOKEN, token);
    }

}

