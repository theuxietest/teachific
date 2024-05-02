package com.so.luotk.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.so.luotk.firebase.NotificationDataModel;
import com.so.luotk.models.newmodels.courseModel.CourseModel;
import com.so.luotk.models.output.BatchListResult;

import java.lang.reflect.Type;
import java.util.List;

public class PreferenceHandler {
    public static final String BATCH_CODE = "BATCH_CODE";
    public static final String QUESTION_DATA = "QUESTION_DATA";
    public static final String BUNDLE = "BUNDLE";
    public static final String TOKEN = "TOKEN";
    public static final String TESTID = "TESTID";
    public static final String DEVICE_ID = "DEVICE_ID";
    public static final String SUBJECT_NAME = "SUBJECT_NAME";
    public static final String CLASS_NAME = "CLASS_NAME";
    public static final String BATCH_ID = "BATCH_ID";
    public static final String TOPIC_NAME = "TOPIC_NAME";
    public static final String IS_FROM = "IS_FROM";
    public static final String ATTACHMENT = "ATTACHMENT";
    public static final String NOTES = "NOTES";
    public static final String DATE = "DATE";
    public static final String TIME = "TIME";
    public static final String DATA = "DATA";
    public static final String FRAG = "FRAG";
    public static final String DATE_TIME = "DATE_TIME";
    public static final String ROOM_NAME = "ROOM_NAME";
    public static final String ZOOM_MEETING_ID = "ZOOM_MEETING_ID";
    public static final String ZOOM_MEETING_PASSWORD = "ZOOM_MEETING_PASSWORD";
    public static final String ANSWER_KEY = "ANSWER_KEY";
    public static final String ADMIN_MOBILE_NO = "ADMIN_MOBILE_NO";
    public static final String ASSIGNMENT_ID = "ASSIGNMENT_ID";
    public static final String FOLDER_ID = "FOLDER_ID";
    public static final String ORG_CODE = "ORG_CODE";
    public static final String ROOM_ID = "ROOM_ID";
    public static final String LOGGED_IN_USERNAME = "LOGGED_IN_USERNAME";
    public static final String BATCH_NAME = "BATCH_NAME";
    public static final String IS_FROM_SEND_JOIN_REQUEST = "IS_FROM_SEND_JOIN_REQUEST";
    public static final String LIST_ITEM = "LIST_ITEM";
    public static final String STATUS = "STATUS";
    public static final String IS_DATA_SUBMITTED = "IS_DATA_SUBMITTED";
    public static final String IS_FOLDER_CREATED = "IS_FOLDER_CREATED";
    public static final String IS_FOLDER_EDIT = "IS_FOLDER_EDIT";
    public static final String FEE_STRUCTURE_PHOTO = "FEE_STRUCTURE_PHOTO";
    public static final String TEST_DURATION = "TEST_DURATION";
    public static final String SHARE_MESSAGE = "SHARE_MESSAGE";
    public static final String ORG_LOGO = "ORG_LOGO";
    public static final String BATCH_COLOR_CODE = "BATCH_COLOR_CODE";
    public static final String BATCH_SETTINGS = "BATCH_SETTINGS";
    public static final String ROOM_PASSWORD = "ROOM_PASSWORD";
    public static final String IS_ADMIN = "IS_ADMIN";
    public static final String END_JITSI_ROOM_ACTION = "END_JITSI_ROOM_ACTION";
    public static final String START_JITSI_ROOM_ACTION = "START_JITSI_ROOM_ACTION";
    public static final String IS_JITSI_LIVE_NOTIFICATION = "IS_JITSI_LIVE_NOTIFICATION";
    public static final String IS_FROM_BATCH_NOTIFICATION = "IS_FROM_BATCH_NOTIFICATION";
    public static final String BROADCAST_ROOM_ID = "BROADCAST_ROOM_ID";
    public static final String BROADCAST_BATCH_ID = "BROADCAST_BATCH_ID";
    public static final String NOTIFICATION_TYPE = "NOTIFICATION_TYPE";
    public static final String NOTIFICATION_MSG = "NOTIFICATION_MSG";
    public static final String NOTIFICATION_DATA = "NOTIFICATION_DATA";
    public static final String TEST_REPORT_ID = "TEST_REPORT_ID";
    public static final String IS_FROM_REPORT_NOTIFICATION = "IS_FROM_REPORT_NOTIFICATION";
    public static final String IS_FROM_ANNOUNCEMENT_NOTIFICATION = "IS_FROM_ANNOUNCEMENT_NOTIFICATION";
    public static final String DOCUMENT_PATH = "DOCUMENT_PATH";
    public static final String LIVE_OPTION = "LIVE_OPTION";
    public static final String USER_TYPE = "USER_TYPE";
    public static final String IS_FIRST_TIME_LAUNCH = "IS_FIRST_TIME_LAUNCH";
    public static final String IS_FROM_BATCH = "IS_FROM_BATCH";
    public static final String COURSE_ID = "COURSE_ID";
    public static final String IS_COURSE_ADDED = "IS_COURSE_ADDED";
    public static final String IS_PAYMENT_DONE = "IS_PAYMENT_DONE";
    public static final String IS_VISIT_LATER = "IS_VISIT_LATER";
    public static final String SELLING_PRICE ="SELLING_PRICE" ;
    public static final String LOCALE ="LOCALE" ;
    private static final String PREF_NAME = "smart_owls_app_prefs";
    private static final int MODE = Context.MODE_PRIVATE;
    public static final String USERID = "USERID";
    public static final String EMPNAME = "empName";
    public static final String ROLE_ID = "ROLE_ID";
    public static final String DEVICE_INFO_ID = "DEVICE_INFO_ID";
    public static final String LOGIN_RESPONSE_JSON = "LOGIN_RESPONSE_JSON";
    public static final String OTP_CLICK_COUNT = "OTP_CLICK_COUNT";
    public static final String SELECTED_TAB_POSITION = "SELECTED_TAB_POSITION";
    public static final String TOO_MANY_REQUEST = "TOO_MANY_REQUEST";
    public static final String LOGGED_IN = "LOGGED_IN";
    public static final String ADMIN_LOGGED_IN = "ADMIN_LOGGED_IN";
    public static final String CREATED_BATCH = "CREATED_BATCH";
    public static final String STATIC_LOGGED_IN = "STATIC_LOGGED_IN";
    public static final String USER_MOBILE = "USER_MOBILE";
    public static final String USER_NAME = "USER_NAME";
    public static final String PDF_NAME = "PDF_NAME";
    public static final String DAYS_NAME_LIST = "DAYS_NAME_LIST";
    public static final String START_TIME_LIST = "START_TIME_LIST";
    public static final String END_TIME_LIST = "END_TIME_LIST";
    public static final String ORG_NAME = "ORG_NAME";
    public static final String ORG_EMAIL = "ORG_EMAIL";
    public static final String ORG_PHONE_NO = "ORG_PHONE_NO";
    public static final String ORG_INSTAGRAM_LINK = "ORG_INSTAGRAM_LINK";
    public static final String ORG_FB_LINK = "ORG_FB_LINK";
    public static final String ORG_WEBSITE_LINK = "ORG_WEBSITE_LINK";
    public static final String IS_ADMIN_ROLE_LOGGED = "IS_ADMIN_ROLE_LOGGED";
    public static final String FIELD_NAME = "FIELD_NAME";
    public static final String SELECTED_CATEGORY = "SELECTED_CATEGORY";
    public static final String SELECTED_COURSE = "SELECTED_COURSE";
    public static final String SELECTED_SUBJECT = "SELECTED_SUBJECT";
    public static final String SELECTED_CATEGORY_ID = "SELECTED_CATEGORY_ID";
    public static final String SELECTED_COURSE_ID = "SELECTED_COURSE_ID";
    public static final String SELECTED_SUBJECT_ID = "SELECTED_SUBJECT_ID";
    public static final String IS_EMAIL_LOGIN="IS_EMAIL_LOGIN";
    public static final String IS_SMS="IS_SMS";
    public static final String HOMEFRAGMENT_CACHE="home_fragment_cache";
    public static final String LAST_LOGIN_DATE="LAST_LOGIN_DATE";

    //zoom
    public static final String ZOOM_USER_ID = "ZOOM_USER_ID";
    public static final String ZOOM_SDK_KEY = "ZOOM_SDK_KEY";
    public static final String ZOOM_SDK_SECRET = "ZOOM_SDK_SECRET";
    public static final String ZOOM_APP_KEY = "ZOOM_APP_KEY";
    public static final String ZOOM_APP_SECRET = "ZOOM_APP_SECRET";
    //course store
    public static final String COURSE_NAME = "COURSE_NAME";
    public static final String COURSE_FEE = "COURSE_FEE";
    public static final String FCM_TOKEN = "FCM_TOKEN";
    public static final String BATCH_LIST = "BATCH_LIST";
    public static final String FEATURED_COURSE = "FEATURED_COURSE";
    public static final String VIDEO_CACHING = "VIDEO_CACHING";
    public static final String MY_COURSE = "MY_COURSE";
    public static final String TOTAL_ASSIGNMENT_REPORT = "TOTAL_ASSIGNMENT_REPORT";
    public static final String TOTAL_TEST_REPORT = "TOTAL_TEST_REPORT";
    public static final String IS_COURSE_LOADED = "IS_COURSE_LOADED";
    public static final String SmFolderId = "SmFolderId";
    public static final String smFolderName = "smFolderName";

    // Vishal Prefrences
    public static final String BATCH_GROUP = "Groups";
    public static final String USER_GROUP = "Users";
    public static final String EXTERNAL_APP = "EXTERNAL_APP";
    public static final String LANGUAG_ID = "lang_id";
    public static final String LANGUAG_SELECTED = "lang_selected";
    public static final String SCREENSHARE = "SCREENSHARE";
    public static final String EXTERNAL_DOC = "EXTERNAL_DOC";
    public static final String FILE_DOWNLOADED = "FILE_DOWNLOADED";
    public static final String YOUTUBE_SPEED = "youtube_speed";
    public static final String DOC_URL = "doc_url";
    public static final String FILE_NAME = "fileNameDoc";
    public static final String ADMIN_ID = "ADMIN_ID";
    public static final String TEACHERORNOT = "teacher_or_not";
    public static final String PDF_PATH = "PDF_PATH";
    public static final String PDF_URI = "uri";
    public static final String FROM_LOCAL = "local";
    public static final int MEDIA_STORE_VERSION = Build.VERSION_CODES.Q;
    public static final String ISSTATICLOGIN = "IsStaticLogin";
    public static final String ISACTIVEORG = "IsActiveOrg";
    public static final String USESTATIC_NUMBER = "USESTATIC_NUMBER";
    public static final String WHATSAPP_NUMBER = "whatsapp_No";
    public static final String CALLING_NUMBER = "calling_No";
    public static final String FOLDER_IN_FOLDER = "FOLDER_IN_FOLDER";
    public static final String IS_LITE_APP = "IS_LITE_APP";
    public static final String COURSES_ON_HOME = "courses_on_home";
    public static final String SHARE_COURSE = "share_course";
    public static final String PDF_FILE_CACHE = "pdf_file_cache";

    //New Updates
    private final SharedPreferences sharedPreferences;
    private final Editor editor;
    public final static String STUDENT = "Student";
    public final static String ADMIN = "Admin";
    public final static String TEACHER = "Teacher";
    public final static String PARENT = "Parent";
    public final static String VERIFYORG = "verifyOrg";
    private final Context context;
    private final Gson gson;
    public final static String LIVE = "live";
    public final static String USERDATA = "data";

    public PreferenceHandler(Context context) {
        sharedPreferences = context.getSharedPreferences("KEY", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        this.context = context;
        gson = new Gson();
    }

    //-------------New Code--------------------------
    public void removePref(String key) {
        editor.remove(key);
        editor.apply();
    }


    public void hideKeyboardFrom(View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void writeBoolean(Context context, String key, boolean value) {
        getEditor(context).putBoolean(key, value).apply();

    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.apply();
    }

    public static boolean isFirstTimeLaunch(Context context, String key,
                                            boolean defValue) {
        return getPreferences(context).getBoolean(key, defValue);
    }

    public static boolean readBoolean(Context context, String key,
                                      boolean defValue) {
        return getPreferences(context).getBoolean(key, defValue);
    }

    public static void writeInteger(Context context, String key, int value) {
        getEditor(context).putInt(key, value).apply();
    }

    public static int readInteger(Context context, String key, int defValue) {
        return getPreferences(context).getInt(key, defValue);
    }

    public static void writeString(Context context, String key, String value) {
        getEditor(context).putString(key, value).apply();
    }

    public static String readString(Context context, String key, String defValue) {
        return getPreferences(context).getString(key, defValue);
    }

    public static void writeFloat(Context context, String key, float value) {
        getEditor(context).putFloat(key, value).apply();
    }

    public static float readFloat(Context context, String key, float defValue) {
        return getPreferences(context).getFloat(key, defValue);
    }

    public static void writeLong(Context context, String key, long value) {
        getEditor(context).putLong(key, value).apply();
    }

    public static long readLong(Context context, String key, long defValue) {
        return getPreferences(context).getLong(key, defValue);
    }

    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, MODE);
    }

    /*  //for list type data
      public static void writeList(Context context, String key, String value) {
          getEditor(context).putString(key, value).apply();
      }
  */
    public static <T> void setList(Context context, String key, List<T> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        getEditor(context).putString(key, json).apply();
    }

    public static List<NotificationDataModel> getNotificationDataList(Context context) {
        List<NotificationDataModel> arrayItems = null;
        String serializedObject = PreferenceHandler.readString(context, PreferenceHandler.NOTIFICATION_DATA, null);
        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<NotificationDataModel>>() {
            }.getType();
            arrayItems = gson.fromJson(serializedObject, type);
        }
        return arrayItems;
    }

    public static <T> void saveBatchList(Context context, String key, List<T> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        getEditor(context).putString(key, json).apply();
    }

    public static List<BatchListResult> getSavedBatchList(Context context) {
        List<BatchListResult> arrayItems = null;
        String serializedObject = PreferenceHandler.readString(context, PreferenceHandler.BATCH_LIST, null);
        // String serializedObject = sharedPreferences.getString(NOTIFICATION_DATA, null);
        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<BatchListResult>>() {
            }.getType();
            arrayItems = gson.fromJson(serializedObject, type);
        }
        return arrayItems;
    }


    public static <T> void saveFeaturedCoursetList(Context context, String key, List<T> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        getEditor(context).putString(key, json).apply();
    }

    public static List<CourseModel> getFeaturedCoursetList(Context context) {
        List<CourseModel> arrayItems = null;
        String serializedObject = PreferenceHandler.readString(context, PreferenceHandler.FEATURED_COURSE, null);
        // String serializedObject = sharedPreferences.getString(NOTIFICATION_DATA, null);
        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<BatchListResult>>() {
            }.getType();
            arrayItems = gson.fromJson(serializedObject, type);
        }
        return arrayItems;
    }

    public static <T> void saveMyCoursetList(Context context, String key, List<T> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        getEditor(context).putString(key, json).apply();
    }

    public static List<BatchListResult> getMyCoursetList(Context context) {
        List<BatchListResult> arrayItems = null;
        String serializedObject = PreferenceHandler.readString(context, PreferenceHandler.BATCH_LIST, null);
        // String serializedObject = sharedPreferences.getString(NOTIFICATION_DATA, null);
        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<BatchListResult>>() {
            }.getType();
            arrayItems = gson.fromJson(serializedObject, type);
        }
        return arrayItems;
    }

    public static Editor getEditor(Context context) {
        return getPreferences(context).edit();
    }

    public static void clearPreferences(Context context) {
        getEditor(context).clear().apply();

    }

}
