package com.so.luotk.api;

public class ApiUtils {

    //public static final String BASE_URL = "https://doubt.ecbse.in/api/v1/";
    public static final String BASE_URL = "https://web.smartowls.in/api/v1/";

    public static APIInterface getApiInterface() {
        return APIClient.getClient(BASE_URL).create(APIInterface.class);
    }

}
