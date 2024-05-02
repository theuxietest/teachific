package com.so.luotk.fragments.more;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.core.widget.NestedScrollView;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.button.MaterialButton;
import com.so.luotk.scoppedStorage.utils.PickerOptions;
import com.so.luotk.scoppedStorage.Picker;
import com.so.luotk.R;
import com.so.luotk.activities.EditProfileActivity;
import com.so.luotk.activities.TermsOfUseActivity;
import com.so.luotk.api.APIInterface;
import com.so.luotk.api.ApiUtils;
import com.so.luotk.customviews.MyDialogFragment;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.databinding.CustomDialogAddMaterialBinding;
import com.so.luotk.models.output.GetUserProfileResponse;
import com.so.luotk.models.output.UpdateUserProfileResponse;
import com.so.luotk.utils.ImageRealPath;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.RealPathUtil;
import com.so.luotk.utils.Utilities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static androidx.core.content.PermissionChecker.checkSelfPermission;
import static com.so.luotk.scoppedStorage.Picker.PICKED_MEDIA_LIST;
import static com.so.luotk.scoppedStorage.Picker.PICKER_OPTIONS;
import static com.so.luotk.scoppedStorage.Picker.REQUEST_CODE_PICKER;
import static com.so.luotk.utils.Utilities.getUniqueCode;

public class MoreOptionFragment extends Fragment implements View.OnClickListener {

    public static String[] storage_permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storage_permissions_33 = {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO
    };
    private final static int PERMISSION_ALL_NEW = 959;
    public static final int PIC_CROP = 1010;
    private ProgressView mProgressDialog;
    private Button privacyAndTerms;
    private final String[] PERM = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_IMAGES};
    private final static int GALLERY = 100, CAMERA = 200, PERMISSION_ALL = 4;
    private Uri imageUri;
    private File cameraPhotoFile;
    private String imgPath;
    private TextView user_name, user_phone_number, whatsapp_button, call_button, whatsapp_number, phone_number,
            like_facebook, follow_insta, org_name_tv, selected_language, joining_date, follow_youtube, follow_twitter, follow_linkedin, lang_selected;
    private String whatsAppNo, callingNo, fbLink, instaLink, orgName, orgPhoneNumber;
    public static String FACEBOOK_URL = "https://facebook.com/";
    private Button rate_us_button, view_account_button, go_to_settings, privacy_button, terms_button, change_language_button;
    MaterialButton view_profile_button;
    private static final int REQUEST_PHONE_CALL = 1;
    private LinearLayout layoutMyProfile, layoutSetting, layoutContactUs, layoutAnnouncement, layoutTimeTable, layoutLanguage;
    private boolean isBatchCreated;
    private View timeTableBottomLine;
    //  private View imgCart;
    private Context context;
    private Activity mActivity;
    private long mLastClickTime = 0;
    MyDialogFragment myDialogFragment;
    double versionCode = 1.0;
    private Runnable runnable;
    private APIInterface apiInterface;
    ImageView profile_pic;
    String userType;
    private Bitmap imgPathBitmap;
    private Handler handler;
    private final Handler searchHandler = new Handler(Looper.myLooper());
    private boolean isFirstInternetToastDone;
    private static final String[] PERMISSION_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_IMAGES};
    private NestedScrollView nestedScrollView;
    private ShimmerFrameLayout shimmerFrameLayout;
    private ConstraintLayout social_mediLay;
    PickerOptions pickerOptions;
    ArrayList<String> testImages = new ArrayList<>();
    public MoreOptionFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        handler = new Handler(Looper.myLooper());
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Utilities.restrictScreenShot(getActivity());
        View view = inflater.inflate(R.layout.fragment_more_new, container, false);
        setupUI(view);

        /*runnable = new Runnable() {
            @Override
            public void run() {
                hitUserProfileInfoService();
            }
        };
        hitUserProfileInfoService();*/

        return view;
    }


    private void checkInternet() {
        Log.d("TAG", "checkInternet: ");
        if (context != null && Utilities.checkInternet(context)) {
            handler.removeCallbacks(runnable);
            hitUserProfileInfoService();
        } else {
            handler.postDelayed(runnable, 5000);
            if (!isFirstInternetToastDone) {
                Utilities.makeToast(context, getString(R.string.internet_connection_error));
                isFirstInternetToastDone = true;
            }

        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }
    public static String[] permissions() {
        String[] p;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            p = storage_permissions_33;
        } else {
            p = storage_permissions;
        }
        return p;
    }
    private boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("Return", "hasPermissions: ");
                    if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permission)){
                    } else {
                        ActivityCompat.requestPermissions(mActivity,
                                permissions(),
                                PERMISSION_ALL_NEW);
                    }
                    return false;
                }
            }
        }
        Log.d("Return", "hasPermissions: return");
        return true;
    }
    private void setupUI(View view) {

        apiInterface = ApiUtils.getApiInterface();
        if (PreferenceHandler.readBoolean(context, PreferenceHandler.USESTATIC_NUMBER, false)){
            whatsAppNo = PreferenceHandler.readString(context, PreferenceHandler.WHATSAPP_NUMBER, getString(R.string.whatsap_no_more));
            callingNo = PreferenceHandler.readString(context, PreferenceHandler.CALLING_NUMBER, getString(R.string.call_no_more));
        } else {
            whatsAppNo = PreferenceHandler.readString(context, PreferenceHandler.ORG_PHONE_NO, null);
            callingNo = PreferenceHandler.readString(context, PreferenceHandler.ORG_PHONE_NO, null);
        }
        orgName = PreferenceHandler.readString(context, PreferenceHandler.ORG_NAME, null);
        fbLink = PreferenceHandler.readString(context, PreferenceHandler.ORG_FB_LINK, null);
        instaLink = PreferenceHandler.readString(context, PreferenceHandler.ORG_INSTAGRAM_LINK, null);
        orgPhoneNumber = PreferenceHandler.readString(context, PreferenceHandler.ORG_PHONE_NO, null);
        userType = PreferenceHandler.readString(context, PreferenceHandler.USER_TYPE, null);
        user_name = view.findViewById(R.id.user_name);
        user_phone_number = view.findViewById(R.id.user_phone_number);
        profile_pic = view.findViewById(R.id.profile_pic);
        view_profile_button = view.findViewById(R.id.view_profile_button);
        whatsapp_button = view.findViewById(R.id.whatsapp_button);
        call_button = view.findViewById(R.id.call_button);
        rate_us_button = view.findViewById(R.id.rate_us_button);
        whatsapp_number = view.findViewById(R.id.whatsapp_number);
        phone_number = view.findViewById(R.id.phone_number);
        like_facebook = view.findViewById(R.id.like_facebook);
        follow_insta = view.findViewById(R.id.follow_insta);
        view_account_button = view.findViewById(R.id.view_account_button);
        org_name_tv = view.findViewById(R.id.org_name);
        selected_language = view.findViewById(R.id.selected_language);
        go_to_settings = view.findViewById(R.id.go_to_settings);
        joining_date = view.findViewById(R.id.joining_date);
        privacy_button = view.findViewById(R.id.privacy_button);
        terms_button = view.findViewById(R.id.terms_button);
        privacyAndTerms = view.findViewById(R.id.privacyAndTerms);
        follow_youtube = view.findViewById(R.id.follow_youtube);
        follow_twitter = view.findViewById(R.id.follow_twitter);
        follow_linkedin = view.findViewById(R.id.follow_linkedin);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_layout);
        nestedScrollView = view.findViewById(R.id.nested_scroll);
        social_mediLay = view.findViewById(R.id.invite_teacher_layout);
        change_language_button = view.findViewById(R.id.changeLangButton);
        lang_selected = view.findViewById(R.id.lang_selected);

        view_profile_button.setOnClickListener(this);
        whatsapp_button.setOnClickListener(this);
        call_button.setOnClickListener(this);
        rate_us_button.setOnClickListener(this);
        like_facebook.setOnClickListener(this);
        follow_insta.setOnClickListener(this);
        view_account_button.setOnClickListener(this);
        go_to_settings.setOnClickListener(this);
        privacy_button.setOnClickListener(this);
        terms_button.setOnClickListener(this);
        change_language_button.setOnClickListener(this);

        pickerOptions = PickerOptions.init();
        pickerOptions.setAllowFrontCamera(true);
        pickerOptions.setMaxCount(1);
        pickerOptions.setExcludeVideos(true);
        pickerOptions.setPreSelectedMediaList(testImages);

        if (PreferenceHandler.readString(getActivity(), PreferenceHandler.LOGGED_IN_USERNAME, "") != null) {
            user_name.setText(PreferenceHandler.readString(getActivity(), PreferenceHandler.LOGGED_IN_USERNAME, ""));
        }
        if (PreferenceHandler.readString(getActivity(), PreferenceHandler.USER_MOBILE, "") != null) {
            user_phone_number.setText(PreferenceHandler.readString(getActivity(), PreferenceHandler.USER_MOBILE, ""));
        }
        if (orgName != null) {
            org_name_tv.setText(orgName);
        }


        if (userType.equals("faculty")) {
            if (PreferenceHandler.readBoolean(context, PreferenceHandler.USESTATIC_NUMBER, false)){
                whatsAppNo = PreferenceHandler.readString(context, PreferenceHandler.WHATSAPP_NUMBER, getString(R.string.whatsap_no_more));
                callingNo = PreferenceHandler.readString(context, PreferenceHandler.CALLING_NUMBER, getString(R.string.call_no_more));
            } else {
                whatsAppNo = PreferenceHandler.readString(context, PreferenceHandler.ORG_PHONE_NO, null);
                callingNo = PreferenceHandler.readString(context, PreferenceHandler.ORG_PHONE_NO, null);
            }
//            whatsAppNo = "8448909398";
        } else if (userType.equals("student")) {
            if (PreferenceHandler.readBoolean(context, PreferenceHandler.USESTATIC_NUMBER, false)){
                whatsAppNo = PreferenceHandler.readString(context, PreferenceHandler.WHATSAPP_NUMBER, getString(R.string.whatsap_no_more));
                callingNo = PreferenceHandler.readString(context, PreferenceHandler.CALLING_NUMBER, getString(R.string.call_no_more));
            } else {
                whatsAppNo = PreferenceHandler.readString(context, PreferenceHandler.ORG_PHONE_NO, null);
                callingNo = PreferenceHandler.readString(context, PreferenceHandler.ORG_PHONE_NO, null);
            }
//            whatsAppNo = "8448909398";
        } else {
            whatsAppNo = getString(R.string.smart_owls_number);
            callingNo = getString(R.string.smart_owls_number);
        }

        whatsapp_number.setText(whatsAppNo);
        phone_number.setText(callingNo);


        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasPermissions(getActivity(), permissions())) {
                    ActivityCompat.requestPermissions(getActivity(), permissions(), PERMISSION_ALL_NEW);
                    return;
                }
                if (isCameraPermitted(mActivity)) {
                    Intent mPickerIntent = new Intent(getActivity(), Picker.class);
                    mPickerIntent.putExtra(PICKER_OPTIONS, pickerOptions);
                    mPickerIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivityForResult(mPickerIntent, REQUEST_CODE_PICKER);
                } else {
                    requestCameraPermission(CAMERA);
                }
//                showDialog();
            }
        });
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            int version = pInfo.versionCode;
            String versionName = pInfo.versionName;
            selected_language.setText(versionName);

            String loginDate = PreferenceHandler.readString(getActivity(), PreferenceHandler.LAST_LOGIN_DATE, "");
            Log.d("TAG", "setupUI: " + loginDate);
            joining_date.setText(getString(R.string.last_login) + ": " + loginDate);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        follow_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/vivekranaware1"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.setPackage("com.google.android.youtube");
                startActivity(intent);
            }
        });
        follow_youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/channel/UCsNJL2KyV3eGdAvM0zsIE2Q"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.google.android.youtube");
                startActivity(intent);
            }
        });
        follow_linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLinkedInProfile();
            }
        });
        String locale = PreferenceHandler.readString(context, PreferenceHandler.LOCALE, "en");

        if (locale.equals("en")) {
            lang_selected.setText("English");
            setClickableString("Terms & Conditions", "Read our Terms & Conditions of using the product", privacyAndTerms);
        }
        if (locale.equals("hi")) {
            lang_selected.setText("हिंदी");
            setClickableString("नियम और शर्तें", "उत्पाद का उपयोग करने के हमारे नियम और शर्तें पढ़ें", privacyAndTerms);
        }
        if (locale.equals("mr")) {
            lang_selected.setText("मराठा");
            setClickableString("वाचा & विद्युतप्रवाह", "आमच्या अटी वाचा & विद्युतप्रवाह मोजण्याच्या एककाचे संक्षिप्त रुप; उत्पादन वापरण्याच्या अटी", privacyAndTerms);
        }
        if (locale.equals("kn")) {
            lang_selected.setText("ಕನ್ನಡ");
            setClickableString("ನಿಯಮಗಳು ಮತ್ತು ನಿಬಂಧನೆಗಳು", "ಉತ್ಪನ್ನವನ್ನು ಬಳಸುವ ನಮ್ಮ ನಿಯಮಗಳು ಮತ್ತು ನಿಬಂಧನೆಗಳು ಓದಿ", privacyAndTerms);
        }
        if (locale.equals("ar")) {
            lang_selected.setText("عربي");
            setClickableString("الشروط والأحكام", "اقرأ الشروط والأحكام الخاصة باستخدام المنتج", privacyAndTerms);
        }
        if (locale.equals("pa")) {
            lang_selected.setText("ਪੰਜਾਬੀ");
            setClickableString("ਨਿਯਮ ਅਤੇ ਸ਼ਰਤਾਂ", "ਉਤਪਾਦ ਦੀ ਵਰਤੋਂ ਕਰਨ ਦੇ ਸਾਡੇ ਨਿਯਮ ਅਤੇ ਸ਼ਰਤਾਂ ਪੜ੍ਹੋ", privacyAndTerms);
        }
        if (locale.equals("or")) {
            lang_selected.setText("ଓଡି");
            setClickableString("ସର୍ତ୍ତାବଳୀ", "ଉତ୍ପାଦ ବ୍ୟବହାର କରିବାର ସର୍ତ୍ତାବଳୀ |", privacyAndTerms);
        }
        if (fbLink != null && !fbLink.equalsIgnoreCase("false")) {
            like_facebook.setVisibility(VISIBLE);
//            social_mediLay.setVisibility(GONE);
        } else {
            like_facebook.setVisibility(GONE);
        }
        if (instaLink != null && !instaLink.equalsIgnoreCase("false")) {
            follow_insta.setVisibility(VISIBLE);
//            social_mediLay.setVisibility(GONE);
        } else {
            follow_insta.setVisibility(GONE);
        }
        if ((fbLink != null && !fbLink.equalsIgnoreCase("false")) || (instaLink != null && !instaLink.equalsIgnoreCase("false"))) {
            social_mediLay.setVisibility(VISIBLE);
        } else {
            social_mediLay.setVisibility(GONE);
        }


        runnable = () -> checkInternet();
        checkInternet();
    }
    private void openLinkedInProfile() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("linkedin://add/%@" + "vivekranaware"));
            final PackageManager packageManager = context.getPackageManager();
            final List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (list.isEmpty()) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.linkedin.com/profile/view?id=" + "vivekranaware"));
            }
            startActivity(intent);
        } catch (Exception e) {
            Log.e("TAG", "openLinkedInProfile: " + e.getMessage());
        }


    }
    public void setClickableString(String clickableValue, String wholeValue, Button yourTextView){
        String value = wholeValue;
        SpannableString spannableString = new SpannableString(value);
        int startIndex = value.indexOf(clickableValue);
        int endIndex = startIndex + clickableValue.length();
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false); // <-- this will remove automatic underline in set span
            }

            @Override
            public void onClick(View widget) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                startActivity(new Intent(context, TermsOfUseActivity.class));
                // do what you want with clickable value
            }
        }, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        yourTextView.setText(spannableString);
        yourTextView.setMovementMethod(LinkMovementMethod.getInstance()); // <-- important, onClick in ClickableSpan won't work without this
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case GALLERY:
                    if (data != null) {
                        imgPath = RealPathUtil.getRealPath(getActivity(), data.getData());
                        /*Glide.with(this).load(data.getData()).centerCrop().into(profile_pic);
                        hitUpdateProfileService();*/
                        performCrop(data.getData());
                    }
                    break;
                case CAMERA:
                    if (resultCode == RESULT_OK) {
                        if (cameraPhotoFile != null) {
                            Uri compressedUri = getCameraImageUri(getContext());
                            if (compressedUri != null) {
                                Log.d("TAG", "onActivityResult: " + RealPathUtil.getRealPath(getContext(), compressedUri));

                                imgPath = RealPathUtil.getRealPath(getContext(), compressedUri);
                                /*Glide.with(this).load(imgPath).centerCrop().into(profile_pic);
                                hitUpdateProfileService();*/
                                performCrop(compressedUri);
                            }
                        }
                    }
                    break;
                case REQUEST_CODE_PICKER:
                    if (data != null) {
                        if (data.getStringArrayListExtra(PICKED_MEDIA_LIST).size() > 0) {
                            for (int i = 0; i < data.getStringArrayListExtra(PICKED_MEDIA_LIST).size(); i++) {

                                if (data.getStringArrayListExtra(PICKED_MEDIA_LIST).get(i).contains("content:")) {
                                    String imageGalleryPath = ImageRealPath.getUriRealPath(mActivity, Uri.parse(data.getStringArrayListExtra(PICKED_MEDIA_LIST).get(i)));
                                    testImages.add(imageGalleryPath);
                                    performCrop(Uri.parse(data.getStringArrayListExtra(PICKED_MEDIA_LIST).get(i)));

                                } else {
                                    testImages.add(data.getStringArrayListExtra(PICKED_MEDIA_LIST).get(i));
                                    String path = Uri.parse(data.getStringArrayListExtra(PICKED_MEDIA_LIST).get(i)).toString(); // "file:///mnt/sdcard/FileName.mp3"
                                    File file = new File(path);
                                    DocumentFile documentFile = DocumentFile.fromSingleUri(requireContext(), Uri.parse(file.getPath()));
                                    performCrop(documentFile.getUri());
                                }

                            }
                        }
                    }
                    break;
                case PIC_CROP:
                    if (data != null) {
                        try {
                            String setImage = "";
                            try {
                                Bitmap bitmap =
                                        MediaStore.Images.Media.getBitmap(context.getContentResolver(), data.getData());
                                imgPathBitmap = bitmap;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            File sdCardDirectory = new File(
                                    Environment
                                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                                    getString(R.string.app_name));
                            if (!sdCardDirectory.exists()) {
                                if (!sdCardDirectory.mkdirs()) {
                                    Log.d("MySnaps", "failed to create directory");

                                }
                            }
                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                                    .format(new Date());
                            String nw = "Image_" + timeStamp + ".jpeg";
                            File image = new File(sdCardDirectory, nw);
                            boolean success = false;
                            // Encode the file as a PNG image.
                            FileOutputStream outStream;
                            try {

                                outStream = new FileOutputStream(image);
                                imgPathBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                                /* 100 to keep full quality of the image */

                                outStream.flush();
                                outStream.close();
                                success = true;
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            setImage = RealPathUtil.getRealPath(getActivity(), Uri.fromFile(new File(image.getAbsolutePath())));
                            String respath= image.getPath();
                            imgPath = respath;
                            if(image.exists()){
                                Bitmap myBitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
                               /* Glide.with(context).load("/storage/emulated/0/Pictures/Sara Tutorials/Image_20210819_152621.jpeg")
                                        .dontTransform()
                                        .into(binding.profilePic);*/

                            }
                            hitUpdateProfileService();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Glide.with(this).load(data.getData()).centerCrop().into(profile_pic);
                            hitUpdateProfileService();
                        }

                    }
                    break;

            }
    }


    public Uri getCameraImageUri(Context inContext) {
        Bitmap rotatedBitmap = null;
        try {
            Bitmap inImage = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), Uri.fromFile(cameraPhotoFile));
            rotatedBitmap = Utilities.rotateBitmap(inImage, RealPathUtil.getRealPath(getContext(), Uri.fromFile(cameraPhotoFile)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), rotatedBitmap, "Title" + getUniqueCode(), null);
        return Uri.parse(path);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_ALL:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    ActivityCompat.requestPermissions(getActivity(), PERM, PERMISSION_ALL);
                }
                break;
            case REQUEST_PHONE_CALL:
                callIntent();
                break;
        }
    }


    private void showDialog() {
        if (!hasPermissions(getActivity(), permissions())) {
            ActivityCompat.requestPermissions(getActivity(), permissions(), PERMISSION_ALL_NEW);
            return;
        }
        View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_dialog_add_material, null, false);
        CustomDialogAddMaterialBinding dialogBinding = CustomDialogAddMaterialBinding.bind(view);
        Dialog dialog = new Dialog(getContext());
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.round_corners_drawable);
        dialog.setContentView(dialogBinding.getRoot());
        dialog.show();

        dialogBinding.tvAddFolder.setVisibility(GONE);
        dialogBinding.folderView.setVisibility(GONE);
        dialogBinding.tvAddLink.setVisibility(GONE);
        dialogBinding.linkView.setVisibility(GONE);
        dialogBinding.tvAddDocument.setVisibility(GONE);

        dialogBinding.tvOpenCam.setOnClickListener(view1 -> {
            if (isCameraPermitted(getActivity())) {
                if (isStoragePermissionRequired(getActivity())) {
                    requestStoragePermission(100);
                } else {
                    dialog.dismiss();
                    openCamera();
                }
            } else {
                requestCameraPermission(CAMERA);
            }
            if (!getActivity().isFinishing())
                dialog.dismiss();

        });
        dialogBinding.tvOpenGallery.setOnClickListener(view1 ->
        {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, GALLERY);
            if (!getActivity().isFinishing())
                dialog.dismiss();
        });

    }

    /*private void openCustomImagePicker() {

        if (!hasPermissions(getActivity(), PERM)) {
            ActivityCompat.requestPermissions(getActivity(), PERM, PERMISSION_ALL);
            return;
        }
        Dialog dialog = new Dialog(getActivity());
        ImagePickerLayoutBinding binding = ImagePickerLayoutBinding.inflate(LayoutInflater.from(getActivity()));
        dialog.setContentView(binding.getRoot());
        dialog.create();
        if (!getActivity().isFinishing())
            dialog.show();
        binding.cancel.setOnClickListener(v -> {
            if (!getActivity().isFinishing())
                dialog.dismiss();
        });
        binding.openCamera.setOnClickListener(v -> {

            if (isCameraPermitted(getActivity())) {
                if (isStoragePermissionRequired(getActivity())) {
                    requestStoragePermission(100);
                } else {
                    dialog.dismiss();
                    openCamera();
                }
            } else {
                requestCameraPermission(200);
            }
            if (!getActivity().isFinishing())
                dialog.dismiss();
        });

        binding.openGallery.setOnClickListener(v -> {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, GALLERY);
            if (!getActivity().isFinishing())
                dialog.dismiss();
        });
    }*/
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraPhotoFile = createImageFile();
        if (cameraPhotoFile != null) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraPhotoFile));
        }
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAMERA);
        }
    }

    private void requestStoragePermission(int requestCode) {
        requestPermissions(PERMISSION_STORAGE, requestCode);
    }

    private boolean isStoragePermissionRequired(Activity activity) {
        return checkSelfPermission(activity, PERMISSION_STORAGE[0]) != PermissionChecker.PERMISSION_GRANTED && checkSelfPermission(activity, PERMISSION_STORAGE[1]) != PermissionChecker.PERMISSION_GRANTED;
    }


    private boolean isCameraPermitted(Activity activity) {
        return checkSelfPermission(activity, Manifest.permission.CAMERA) == PermissionChecker.PERMISSION_GRANTED;
    }

    private void requestCameraPermission(int requestCode) {
        requestPermissions(new String[]{Manifest.permission.CAMERA}, requestCode);
    }
    private File createImageFile() {
        File image = null;
        try {
            long timeStamp = System.currentTimeMillis();
            String imageFileName = "NAME_" + timeStamp;
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            image = null;
            try {
                image = File.createTempFile(
                        imageFileName,
                        ".jpg",
                        storageDir
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("TAG", "createImageFile: " + image);
        return image;
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    private void hitUserProfileInfoService() {
        shimmerFrameLayout.startShimmer();
        shimmerFrameLayout.setVisibility(VISIBLE);
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", PreferenceHandler.readString(getActivity(), PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(getActivity(), PreferenceHandler.DEVICE_ID, ""));
        Call<GetUserProfileResponse> call = apiInterface.getUserProfile(headers);
        // mProgressDialog = new ProgressView(this);
        // mProgressDialog.show();
        call.enqueue(new Callback<GetUserProfileResponse>() {
            @Override
            public void onResponse(Call<GetUserProfileResponse> call, Response<GetUserProfileResponse> response) {
                Log.d("TAG", "onResponse: " + response.isSuccessful());
                if (response.isSuccessful()) {
                    if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                        if (response.body().getStatus() != null && response.body().getSuccess().equalsIgnoreCase("true")) {
                            if (response.body().getResult() != null) {

                                if (response.body().getResult().getPhoto() != null) {
                                    Glide.with(getActivity())
                                            .load(response.body().getResult().getPhoto())
                                            .listener(new RequestListener<Drawable>() {
                                                @Override
                                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                                                    progressBar.setVisibility(View.GONE);
                                                    return false;
                                                }

                                                @Override
                                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                                                    progressBar.setVisibility(View.GONE);
                                                    return false;
                                                }
                                            })
                                            .into(profile_pic);
                                } else {
//                                    progressBar.setVisibility(View.GONE);
                                }

//                                setupUserData(response.body().getResult());
                            }
                        }
                    } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                        Utilities.openUnauthorizedDialog(getActivity());
                    } else {
                        Utilities.makeToast(getActivity(), getString(R.string.server_error));
                    }
                }
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(GONE);
                nestedScrollView.setVisibility(VISIBLE);

            }

            @Override
            public void onFailure(Call<GetUserProfileResponse> call, Throwable t) {
                //mProgressDialog.dismiss();
                Utilities.makeToast(getActivity(), getString(R.string.server_error));
            }
        });
    }
    @Override
    public void onClick(View v) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        switch (v.getId()) {
            case R.id.view_profile_button:
                openEditProfileActivity();
                break;
            case R.id.whatsapp_button:
                if (whatsAppNo != null) {
                    openWhatsAppIntent();
                }
                break;
            case R.id.call_button:
                checkPermissionGranted();
                break;
            case R.id.rate_us_button:
                openRateUsIntent();
                break;
            case R.id.like_facebook:
                setFacebookIntent();
                break;
            case R.id.follow_insta:
                openInstaProfile();
                break;
            case R.id.view_account_button:
                showAlertDialogButtonClicked();
                break;
            case R.id.go_to_settings:
                Intent settingsIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, getActivity().getPackageName());
//                        .putExtra(Settings.EXTRA_CHANNEL_ID, MY_CHANNEL_ID);
                startActivity(settingsIntent);
                break;
            case R.id.privacy_button:
                startActivity(new Intent(getActivity(), TermsOfUseActivity.class));
                break;
            case R.id.terms_button:
                startActivity(new Intent(getActivity(), TermsOfUseActivity.class));
                break;
            case R.id.changeLangButton:
                SetLanguageFragment setLanguageFragment = SetLanguageFragment.newInstance(false);
                openNextFragment(setLanguageFragment);
                break;
            /*case R.id.layout_setting:
                SettingFragment settingFragment = new SettingFragment();
                openNextFragment(settingFragment);
                break;
            case R.id.layout_contact_us:
                checkPermissionGranted();
                //openCallDialog();

                break;
            case R.id.layout_announcement:
                AnnoucementsFragment upcomingEventFragment = AnnoucementsFragment.newInstance(getString(R.string.announcement), "");
                openNextFragment(upcomingEventFragment);
                break;
            case R.id.layout_time_table:
            case R.id.layout_language:
                SetLanguageFragment setLanguageFragment = SetLanguageFragment.newInstance(false);
                openNextFragment(setLanguageFragment);
                break;*/
            default:
                break;
        }
    }

    public void setFacebookIntent() {
        if (fbLink != null && !fbLink.equalsIgnoreCase("false")) {
            Uri uri = Uri.parse("fb://page/" + fbLink);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.facebook.katana");
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                try {
                    Intent intent2 = new Intent(new Intent(Intent.ACTION_VIEW, Uri.parse(FACEBOOK_URL + fbLink)));
                    if (intent2.resolveActivity(mActivity.getPackageManager()) != null)
                        startActivityForResult(intent2, 0);
                    else {
                        Toast.makeText(context, " No browser found.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                } catch (Exception exp) {
                    exp.printStackTrace();
                }

            }
        }
    }

    private void openInstaProfile() {
        if (instaLink != null && !instaLink.equalsIgnoreCase("false")) {
            Uri uri = Uri.parse("instagram://user?username=" + instaLink);
            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
            likeIng.setPackage("com.instagram.android");

            try {
                startActivity(likeIng);
            } catch (ActivityNotFoundException e) {
                try {
                    Intent intent = new Intent(new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/" + instaLink)));
                    if (intent.resolveActivity(mActivity.getPackageManager()) != null)
                        startActivityForResult(intent, 0);
                    else {
                        Toast.makeText(context, " No browser found.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                } catch (Exception exp) {
                    exp.printStackTrace();
                }

            }
        }
    }

    private void openRateUsIntent() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + mActivity.getPackageName())));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + mActivity.getPackageName())));
        }
    }

    private void openWhatsAppIntent() {
        String url = "https://api.whatsapp.com/send?phone=" + "91" + " " + whatsAppNo;
        try {
            PackageManager pm = mActivity.getPackageManager();
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(context, getString(R.string.install_whatsapp), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public boolean isAppInstalled() {
        try {
            mActivity.getApplicationContext().getPackageManager().getApplicationInfo("com.facebook.katana", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }*/

    private void openEditProfileActivity() {
        Intent intent = new Intent(context, EditProfileActivity.class);
        //intent.putExtra("infoParticular", infoParticular);
        startActivity(intent);
        // getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    private void openNextFragment(Fragment fragment) {
        if (getActivity() != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //fragmentTransaction.add(R.id.frame_layout_more, fragment);
            fragmentTransaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out);
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    public void checkPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);

            } else {
                callIntent();
            }
        } else {
            callIntent();

        }
    }

    public void callIntent() {
        try {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + callingNo));
            startActivity(callIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void showAlertDialogButtonClicked()
    {

        myDialogFragment = new MyDialogFragment(getActivity());

        Bundle bundle = new Bundle();
        bundle.putBoolean("notAlertDialog", true);

        myDialogFragment.setArguments(bundle);

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);


        myDialogFragment.show(ft, "dialog");
    }

    private void hitUpdateProfileService() {
        APIInterface apiInterface = ApiUtils.getApiInterface();
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", PreferenceHandler.readString(getActivity(), PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(getActivity(), PreferenceHandler.DEVICE_ID, ""));
        //All the String parameters, you have to put like
        Map<String, RequestBody> map = new HashMap<>();
        Call<UpdateUserProfileResponse> call = null;
        File file = null;
        if (imgPath != null) {
            file = new File(imgPath);
            //file = Utilities.resaveBitmap(imgPath,imgPath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("*/*"), file);
            MultipartBody.Part ProfilePhotoToUpload = MultipartBody.Part.createFormData("photo", file.getName(), requestFile);
            call = apiInterface.updateUserProfileWithImage(headers, map, ProfilePhotoToUpload /*input,*/);
        } else {
            call = apiInterface.updateUserProfile(headers, map);
        }
        mProgressDialog = new ProgressView(getActivity());
        mProgressDialog.show();
        call.enqueue(new Callback<UpdateUserProfileResponse>() {
            @Override
            public void onResponse(Call<UpdateUserProfileResponse> call, Response<UpdateUserProfileResponse> response) {
                mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                        if (response.body().getSuccess().equalsIgnoreCase("true")) {
                            if (response.body().getResult() != null) {
                                Toast.makeText(getActivity(), getString(R.string.profile_has_been_updated), Toast.LENGTH_SHORT).show();
                                handler.removeCallbacks(runnable);
                                hitUserProfileInfoService();
//                                doNotAllowToeditProfile();
                            }
                        } else {
                            Toast.makeText(getActivity(), response.body().getResult(), Toast.LENGTH_SHORT).show();
                        }
                    } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                        Utilities.openUnauthorizedDialog(getActivity());
                    } else {
                        Utilities.makeToast(getActivity(), getString(R.string.server_error));
                    }
                }

            }

            @Override
            public void onFailure(Call<UpdateUserProfileResponse> call, Throwable t) {
                mProgressDialog.dismiss();
                Utilities.makeToast(getActivity(), getString(R.string.server_error));
            }
        });
    }

    private void performCrop(Uri picUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties here
            cropIntent.putExtra("crop", true);
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 128);
            cropIntent.putExtra("outputY", 128);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}
