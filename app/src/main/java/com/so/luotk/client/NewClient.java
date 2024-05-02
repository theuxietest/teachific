package com.so.luotk.client;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.so.luotk.models.output.CreateMeetingInput;
import com.so.luotk.models.output.CreateMeetingResponse;
import com.so.luotk.utils.PreferenceHandler;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class NewClient {
    private final String baseurl = "https://api.zoom.us/v2/users/";
    private final ZoomApi api;
    private final String TAG = this.getClass().getSimpleName();
    private final String userId;
    private String token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJuamczWVJLRlJaaUlqdjZLRDlwV0lnIiwiZXhwIjoxNjA4NTgwNjI2fQ.flWFprZPVeXxTvToGiieCJOjftLv2P-NoGW04Yei5lo";
    private final Map<String, String> header = new HashMap<>();

    public NewClient(Context context) {
        userId = PreferenceHandler.readString(context, PreferenceHandler.ZOOM_USER_ID, "");
        token = "Bearer " + createJWTAccessToken(context);
        header.put("Authorization", token);
        header.put("Content-Type", "application/json");
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseurl).addConverterFactory(GsonConverterFactory.create()).build();
        api = retrofit.create(ZoomApi.class);

    }

    public interface ZoomApi {
        @POST("{id}/meetings")
        Call<CreateMeetingResponse> createMeeting(@HeaderMap Map<String, String> header, @Path("id") String email, @Body CreateMeetingInput createMeetingInput);
    }

    public void createZoomMeeting(String batchId, ResponseObject responseObject) {
        api.createMeeting(header, userId, new CreateMeetingInput(batchId)).enqueue(new Callback<CreateMeetingResponse>() {
            @Override
            public void onResponse(Call<CreateMeetingResponse> call, Response<CreateMeetingResponse> response) {
                if (response.isSuccessful())
                    responseObject.OnCallback(response.body(), null);
                else responseObject.OnCallback(null, response.errorBody().toString());
            }

            @Override
            public void onFailure(Call<CreateMeetingResponse> call, Throwable t) {
                responseObject.OnCallback(null, t.getMessage());
                Log.e(TAG, "onResponse: " + t.getMessage());
            }
        });
    }

    public static String createJWTAccessToken(Context context) {
        String api_key = PreferenceHandler.readString(context, PreferenceHandler.ZOOM_APP_KEY, null);
        String api_secret = PreferenceHandler.readString(context, PreferenceHandler.ZOOM_APP_SECRET, null);
        long time = System.currentTimeMillis() / 1000 + 3600 * 2;
        if (api_key != null && api_secret != null) {
            String header = "{\"alg\": \"HS256\", \"typ\": \"JWT\"}";
            String payload = "{\"iss\": \"" + api_key /*API_KEY*/ + "\"" + ", \"exp\": " + time + "}";
            try {
                String headerBase64Str = Base64.encodeToString(header.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP | Base64.NO_PADDING | Base64.URL_SAFE);
                String payloadBase64Str = Base64.encodeToString(payload.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP | Base64.NO_PADDING | Base64.URL_SAFE);
                final Mac mac = Mac.getInstance("HmacSHA256");
                SecretKeySpec secretKeySpec = new SecretKeySpec(api_secret.getBytes()/*API_SECRET.getBytes()*/, "HmacSHA256");
                mac.init(secretKeySpec);

                byte[] digest = mac.doFinal((headerBase64Str + "." + payloadBase64Str).getBytes());

                return headerBase64Str + "." + payloadBase64Str + "." + Base64.encodeToString(digest, Base64.NO_WRAP | Base64.NO_PADDING | Base64.URL_SAFE);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

