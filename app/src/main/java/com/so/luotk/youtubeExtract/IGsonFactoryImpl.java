package com.so.luotk.youtubeExtract;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.so.luotk.models.youtubeEx.youtube.playerResponse.Cipher;

import java.lang.reflect.Type;

import static com.so.luotk.youtubeExtract.utils.StringUtils.urlDecode;

class IGsonFactoryImpl implements IGsonFactory {

    @Override
    public Gson initGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Cipher.class, new CipherDeserializer());
        return gsonBuilder.create();
    }

    private class CipherDeserializer implements JsonDeserializer<Cipher> {
        @Override
        public Cipher deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String[] arr = json.getAsString().split("&");
            return new Cipher(arr[0].replace("s=", ""),
                    arr[1].replace("sp=", ""),
                    urlDecode(arr[2].replace("url=", "")));
        }
    }
}
