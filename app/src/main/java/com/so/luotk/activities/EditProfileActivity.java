package com.so.luotk.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.so.luotk.R;
import com.so.luotk.api.APIInterface;
import com.so.luotk.api.ApiUtils;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.databinding.ActivityUpdatedEditProfileBinding;
import com.so.luotk.databinding.ImagePickerLayoutBinding;
import com.so.luotk.models.output.GetUserProfileResponse;
import com.so.luotk.models.output.GetUserProfileResult;
import com.so.luotk.models.output.UpdateUserProfileResponse;
import com.so.luotk.utils.AmazingSpinner;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.RealPathUtil;
import com.so.luotk.utils.Utilities;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityUpdatedEditProfileBinding binding;
    TextInputEditText edt_name, edt_mobile, edt_email, edt_college_address, edt_school_address,
            edt_parent_name, edt_parent_email, edt_parent_mobile;
    TextInputLayout layout_mobile;
    private Button save_details_button;
    private Toolbar toolbar;
    private TextView toolbarCustomTitle;
    private APIInterface apiInterface;
    private ProgressView mProgressDialog;
    private AppCompatImageView btnSave;
    private CircleImageView imgUserProfile;
    private LinearLayout layoutBasicParentInfo;
    private TextView txtBasicParentInfo, address_title, parent_title;
//    private ImageView imgName;

    private LinearLayout layoutBloodGroup;
    private EditText edtDob;
    private EditText edtAdharNo;
    private LinearLayout layoutEditAddressInfo;
    private EditText edtCorrospondAddress;
    private MaterialCardView layoutEditParentInfo;
    private EditText edtCorrospondPincode;
    private boolean isAllowedToUpdate = true;
    private String imgPath;
    //    private TextView tvEdit;
    private View editIcon;
    private RelativeLayout layoutProfileImage;
    private ShimmerFrameLayout shimmerFrameLayout;
    private Handler handler;
    private Runnable runnable;
    private RelativeLayout layoutDataView;
    private boolean isFirstInternetToastDone, isEmailLogin;
    private FloatingActionButton fab;
    private ProgressBar progressBar;
    private final String emailPattern = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.+[A-Za-z]{2,4}" /*"[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"*/;
    private String bloodGroup, gender;
    private final String[] PERM = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private final static int GALLERY = 100, CAMERA = 200, PERMISSION_ALL = 4;
    private Uri imageUri;
    private File cameraPhotoFile;
    private AmazingSpinner spinnerGender, spinnerBloodGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.restrictScreenShot(this);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        Utilities.setUpStatusBar(this);
        Utilities.setLocale(this);
        binding = ActivityUpdatedEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        isEmailLogin = PreferenceHandler.readBoolean(this, PreferenceHandler.IS_EMAIL_LOGIN, false);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        Utilities.setUpStatusBar(this);
        Utilities.setLocale(this);
        setToolbar();
        setupUI();
    }

    private void setToolbar() {
        //toolbar setup
        toolbar = findViewById(R.id.toolbar);
        toolbarCustomTitle = findViewById(R.id.tv_toolbar_title);
        toolbarCustomTitle.setText(R.string.edit_profile);
        save_details_button = findViewById(R.id.save_details_button);
        save_details_button.setOnClickListener(this::onClick);
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(upArrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.hideKeyBoard(EditProfileActivity.this);
                onBackPressed();
            }
        });
    }


    private void setupUI() {
        apiInterface = ApiUtils.getApiInterface();
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
        imgUserProfile = (CircleImageView) findViewById(R.id.img_user_profile);
        progressBar = findViewById(R.id.progress);
        layoutBasicParentInfo = (LinearLayout) findViewById(R.id.layout_basic_parent_info);
        txtBasicParentInfo = findViewById(R.id.txt_basic_parent_info);
//        imgName = (ImageView) findViewById(R.id.img_name);
//        txtName = findViewById(R.id.txt_name);
        edt_name = findViewById(R.id.edt_name);
        edt_mobile = findViewById(R.id.edt_mobile);
        edt_email = findViewById(R.id.edt_email);
//        edt_blood_group = findViewById(R.id.edt_blood_group);
        spinnerBloodGroup = findViewById(R.id.spinner_blood_group);
//        edt_gender = findViewById(R.id.edt_gender);
        spinnerGender = findViewById(R.id.spinner_gender);
        layoutEditAddressInfo = (LinearLayout) findViewById(R.id.layout_edit_address_info);
        edt_school_address = findViewById(R.id.edt_school_address);
        edt_college_address = findViewById(R.id.edt_college_address);
        layoutEditParentInfo = (MaterialCardView) findViewById(R.id.layout_edit_parent_info);
        edt_parent_name = findViewById(R.id.edt_parent_name);
        edt_parent_email = findViewById(R.id.edt_parent_email);
        edt_parent_mobile = findViewById(R.id.edt_parent_mobile);
        edtCorrospondPincode = findViewById(R.id.edt_corrospond_pincode);
        editIcon = findViewById(R.id.edit_icon);
        layoutProfileImage = findViewById(R.id.layout_profile_image);
        layoutDataView = findViewById(R.id.layout_data_view);
        shimmerFrameLayout = findViewById(R.id.shimmer_layout);
        layout_mobile = (TextInputLayout) findViewById(R.id.layout_mobile);
//        binding.notEditableLayer.setLayoutParams(binding.infoView.getLayoutParams());


       /* setupBloodGroupSpinner();
        setupGenderSpinner();*/

        shimmerFrameLayout.startShimmer();
        handler = new Handler(Looper.myLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                checkInternet();
            }
        };

        checkInternet();

    }

    private void checkInternet() {
        if (Utilities.checkInternet(this)) {
            handler.removeCallbacks(runnable);
            hitUserProfileInfoService();
        } else {
            handler.postDelayed(runnable, 5000);
            if (!isFirstInternetToastDone) {
                Utilities.makeToast(EditProfileActivity.this, getString(R.string.internet_connection_error));
                isFirstInternetToastDone = true;
            }
        }
    }


    private void hitUserProfileInfoService() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", PreferenceHandler.readString(this, PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(this, PreferenceHandler.DEVICE_ID, ""));
        Call<GetUserProfileResponse> call = apiInterface.getUserProfile(headers);
        // mProgressDialog = new ProgressView(this);
        // mProgressDialog.show();
        call.enqueue(new Callback<GetUserProfileResponse>() {
            @Override
            public void onResponse(Call<GetUserProfileResponse> call, Response<GetUserProfileResponse> response) {
                //   mProgressDialog.dismiss();
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                layoutDataView.setVisibility(View.VISIBLE);
                if (response.isSuccessful()) {
                    if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                        if (response.body().getStatus() != null && response.body().getSuccess().equalsIgnoreCase("true")) {
                            if (response.body().getResult() != null) {
                                setupUserData(response.body().getResult());
                            }
                        }
                    } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                        Utilities.openUnauthorizedDialog(EditProfileActivity.this);
                    } else {
                        Utilities.makeToast(EditProfileActivity.this, getString(R.string.server_error));
                    }
                }

            }

            @Override
            public void onFailure(Call<GetUserProfileResponse> call, Throwable t) {
                //mProgressDialog.dismiss();
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                layoutDataView.setVisibility(View.VISIBLE);
                Utilities.makeToast(EditProfileActivity.this, getString(R.string.server_error));
            }
        });
    }

    private void setupUserData(GetUserProfileResult result) {
        /*basic info*/
        if (result.getName() != null)
            edt_name.setText(result.getName());
        if (result.getPhone() != null)
            edt_mobile.setText(result.getPhone());
        if (result.getEmail() != null)
            edt_email.setText(result.getEmail());

        bloodGroup = result.getBloodGroup();
        gender = result.getGender();

        /*if (result.getBloodGroup() != null)
            edt_blood_group.setText(result.getBloodGroup());
        if (result.getGender() != null)
            edt_gender.setText(result.getGender());*/

        /*address information*/
        if (result.getCollege() != null)
            edt_college_address.setText(result.getCollege());
        if (result.getSchool() != null)
            edt_school_address.setText(result.getSchool());
        /*parent information*/
        if (result.getParent1Name() != null)
            edt_parent_name.setText(result.getParent1Name());
        if (result.getParent1Email() != null)
            edt_parent_email.setText(result.getParent1Email());
        if (result.getParent1Mobile() != null)
            edt_parent_mobile.setText(result.getParent1Mobile());

        if (result.getPhoto() != null) {
            Glide.with(getApplicationContext())
                    .load(result.getPhoto())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(imgUserProfile);
        } else progressBar.setVisibility(View.GONE);

        spinnerGender.setVisibility(View.VISIBLE);
        spinnerBloodGroup.setVisibility(View.VISIBLE);
        setupGenderSpinner(gender);
        setupBloodGroupSpinner(bloodGroup);


        isAllowedToUpdate = true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_details_button:
                if (isAllowedToUpdate) {
                    Utilities.hideKeyBoard(EditProfileActivity.this);
                    if (Utilities.checkInternet(this)) {
                        if (checkValidation()) {
                            hitUpdateProfileService();
                        }
                    } else {
                        Utilities.makeToast(EditProfileActivity.this, getString(R.string.internet_connection_error));
                    }
                } else {
                    doAllowToeditProfile();
                }

                break;

            case R.id.layout_profile_image:
                Utilities.hideKeyBoard(EditProfileActivity.this);
                if (!hasPermissions(this, PERM))
                    ActivityCompat.requestPermissions(this, PERM, PERMISSION_ALL);
                else{
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, GALLERY);
                }
                break;
            default:
                break;
        }
    }

    public Uri getCameraImageUri(Context inContext) throws Exception {
        Bitmap rotatedBitmap = null;
        Bitmap inImage = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(cameraPhotoFile));
        rotatedBitmap = Utilities.rotateBitmap(inImage, RealPathUtil.getRealPath(this, Uri.fromFile(cameraPhotoFile)));
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), rotatedBitmap, "Image" + Utilities.getUniqueCode(), null);
        return Uri.parse(path);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case GALLERY:
                    if (data != null) {
                        imgPath = RealPathUtil.getRealPath(this, data.getData());
                        Glide.with(this).load(data.getData()).centerCrop().into(imgUserProfile);
                    }
                    break;
            }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_ALL:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else
                    ActivityCompat.requestPermissions(this, PERM, PERMISSION_ALL);
                return;
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void openCustomImagePicker() {
        if (!hasPermissions(this, PERM))
            ActivityCompat.requestPermissions(this, PERM, PERMISSION_ALL);
        Dialog dialog = new Dialog(this);
        ImagePickerLayoutBinding binding = ImagePickerLayoutBinding.inflate(LayoutInflater.from(this));
        dialog.setContentView(binding.getRoot());
        dialog.create();
        if (!isFinishing())
            dialog.show();
        binding.cancel.setOnClickListener(v -> {
            if (!isFinishing())
                dialog.dismiss();
        });
        binding.openCamera.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraPhotoFile = createImageFile();
            if (cameraPhotoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraPhotoFile));
            }
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, CAMERA);
            }
            if (!isFinishing())
                dialog.dismiss();
        });
        binding.openGallery.setOnClickListener(v -> {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, GALLERY);
            if (!isFinishing())
                dialog.dismiss();
        });
    }

    private File createImageFile() {
        long timeStamp = System.currentTimeMillis();
        String imageFileName = "NAME_" + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,
                    ".jpg",
                    storageDir
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    private void doAllowToeditProfile() {
//        tvEdit.setText(R.string.txt_done);
        editIcon.setVisibility(View.VISIBLE);
        layoutProfileImage.setOnClickListener(this);
        binding.notEditableLayer.setVisibility(View.GONE);
        if (TextUtils.isEmpty(edt_parent_mobile.getText())) {
            edt_parent_mobile.setFocusable(true);
            edt_parent_mobile.setClickable(true);
            edt_parent_mobile.setLongClickable(true);
            edt_parent_mobile.setFocusableInTouchMode(true);
        }
        if (isEmailLogin)
            binding.edtEmail.setEnabled(false);
//        edt_blood_group.setVisibility(View.GONE);
        spinnerBloodGroup.setVisibility(View.VISIBLE);
        setupBloodGroupSpinner(bloodGroup);
        if (bloodGroup != null) {
            String[] bloodGroups = getResources().getStringArray(R.array.blood_groups);
            int len = bloodGroups.length;
            for (int i = 0; i < len; i++) {
                if (bloodGroup.equalsIgnoreCase(bloodGroups[i]))
                    spinnerBloodGroup.setSelection(i);
            }
        }


//        edt_gender.setVisibility(View.GONE);
        spinnerGender.setVisibility(View.VISIBLE);
        setupGenderSpinner(gender);
        if (gender != null) {
            String[] genderList = getResources().getStringArray(R.array.gender);
            int len = genderList.length;
            for (int i = 0; i < len; i++) {
                if (gender.equalsIgnoreCase(genderList[i])) {
                    spinnerGender.setSelection(i);
                }
            }
        }

        isAllowedToUpdate = true;

    }

    private void doNotAllowToeditProfile() {
//        tvEdit.setText(getString(R.string.edit));
        editIcon.setVisibility(View.GONE);
        binding.notEditableLayer.setVisibility(View.VISIBLE);
        layoutProfileImage.setOnClickListener(null);
        /*edt_blood_group.setVisibility(View.VISIBLE);
        if (!spinnerBloodGroup.getSelectedItem().toString().equalsIgnoreCase("Select blood group")) {
            edt_blood_group.setText(spinnerBloodGroup.getSelectedItem().toString());
        } else {
            edt_blood_group.setText("");
        }*/

        spinnerBloodGroup.setVisibility(View.GONE);


      /*  edt_gender.setVisibility(View.VISIBLE);
        if (!spinnerGender.getSelectedItem().toString().equalsIgnoreCase("Select gender")) {
            edt_gender.setText(spinnerGender.getSelectedItem().toString());
        } else {
            edt_gender.setText("");
        }*/

        spinnerGender.setVisibility(View.GONE);


        isAllowedToUpdate = false;

    }

    private void hitUpdateProfileService() {
        APIInterface apiInterface = ApiUtils.getApiInterface();
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", PreferenceHandler.readString(this, PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(this, PreferenceHandler.DEVICE_ID, ""));
        //All the String parameters, you have to put like
        Map<String, RequestBody> map = new HashMap<>();
        if (!edt_name.getText().toString().isEmpty())
            map.put("name", toRequestBody(edt_name.getText().toString()));
        if (!edt_email.getText().toString().isEmpty())
            map.put("email", toRequestBody(edt_email.getText().toString()));

        /*if (!spinnerBloodGroup.getSelectedItem().toString().isEmpty()) {
            bloodGroup = spinnerBloodGroup.getSelectedItem().toString();
            if (!spinnerBloodGroup.getSelectedItem().toString().equalsIgnoreCase("Select blood group")) {
                map.put("bloodGroup", toRequestBody(spinnerBloodGroup.getSelectedItem().toString()));
            } else {
                map.put("bloodGroup", toRequestBody(""));
            }
        }

        if (!spinnerGender.getSelectedItem().toString().isEmpty()) {
            gender = spinnerGender.getSelectedItem().toString();
            if (!spinnerGender.getSelectedItem().toString().equalsIgnoreCase("Select gender")) {
                map.put("gender", toRequestBody(spinnerGender.getSelectedItem().toString()));
            } else {
                map.put("gender", toRequestBody(""));
            }
        }*/
        if (!spinnerBloodGroup.getEditableText().toString().isEmpty()) {
            bloodGroup = spinnerBloodGroup.getEditableText().toString();
            if (!spinnerBloodGroup.getEditableText().toString().equalsIgnoreCase("Select blood group")) {
                map.put("bloodGroup", toRequestBody(spinnerBloodGroup.getEditableText().toString()));
            } else {
                map.put("bloodGroup", toRequestBody(""));
            }
        }
        if (!spinnerGender.getEditableText().toString().isEmpty()) {
            gender = spinnerGender.getEditableText().toString();
            if (!spinnerGender.getEditableText().toString().equalsIgnoreCase("Select gender")) {
                map.put("gender", toRequestBody(spinnerGender.getEditableText().toString()));
            } else {
                map.put("gender", toRequestBody(""));
            }
        }

        if (!edt_school_address.getText().toString().isEmpty()) {
            map.put("school", toRequestBody(edt_school_address.getText().toString()));
        } else {
            map.put("school", toRequestBody(""));
        }
        if (!edt_college_address.getText().toString().isEmpty()) {
            map.put("college", toRequestBody(edt_college_address.getText().toString()));
        } else {
            map.put("college", toRequestBody(""));
        }
        if (!edt_parent_name.getText().toString().isEmpty()) {
            map.put("parent1Name", toRequestBody(edt_parent_name.getText().toString()));
        } else {
            map.put("parent1Name", toRequestBody(""));
        }
        if (!edt_parent_email.getText().toString().isEmpty()) {
            map.put("parent1Email", toRequestBody(edt_parent_email.getText().toString()));
        } else {
            map.put("parent1Email", toRequestBody(""));
        }
        if (!edt_parent_mobile.getText().toString().isEmpty()) {
            map.put("parent1Mobile", toRequestBody(edt_parent_mobile.getText().toString()));
        } else {
            map.put("parent1Mobile", toRequestBody(""));
        }
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
        mProgressDialog = new ProgressView(this);
        mProgressDialog.show();
        call.enqueue(new Callback<UpdateUserProfileResponse>() {
            @Override
            public void onResponse(Call<UpdateUserProfileResponse> call, Response<UpdateUserProfileResponse> response) {
                mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                        if (response.body().getSuccess().equalsIgnoreCase("true")) {
                            if (response.body().getResult() != null) {
                                Toast.makeText(EditProfileActivity.this, "Profile has been updated successfully", Toast.LENGTH_SHORT).show();
//                                doNotAllowToeditProfile();
                            }
                        } else {
                            Toast.makeText(EditProfileActivity.this, response.body().getResult(), Toast.LENGTH_SHORT).show();
                        }
                    } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                        Utilities.openUnauthorizedDialog(EditProfileActivity.this);
                    } else {
                        Utilities.makeToast(EditProfileActivity.this, getString(R.string.server_error));
                    }
                }

            }

            @Override
            public void onFailure(Call<UpdateUserProfileResponse> call, Throwable t) {
                mProgressDialog.dismiss();
                Utilities.makeToast(EditProfileActivity.this, getString(R.string.server_error));
            }
        });
    }

    // This method  converts String to RequestBody
    public static RequestBody toRequestBody(String value) {
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value);
        return body;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title" + Utilities.getUniqueCode(), null);
        return Uri.parse(path);
    }


    private boolean checkValidation() {
        if (edt_email.getText().toString().isEmpty()) {
            Utilities.makeToast(EditProfileActivity.this, "Email address id required");
            return false;
        } else if (!edt_email.getText().toString().isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(edt_email.getText().toString()).matches()) {
            Utilities.makeToast(EditProfileActivity.this, "Invalid email address");
            return false;
        } else if (!edt_parent_email.getText().toString().isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(edt_parent_email.getText().toString()).matches()) {
            Utilities.makeToast(EditProfileActivity.this, "Invalid parent email address");
            return false;
        } else if (!edt_parent_mobile.getText().toString().isEmpty() && edt_parent_mobile.getText().toString().length() < 10) {
            Utilities.makeToast(EditProfileActivity.this, "Please enter a valid parent phone number");
            return false;
        } else {
            return true;
        }
    }


    private void setupBloodGroupSpinner() {
        // spinnerBloodGroup.setOnItemSelectedListener(this);
        ArrayAdapter aa = new ArrayAdapter(this, R.layout.layout_profile_spiner_item, getResources().getStringArray(R.array.blood_groups));
        aa.getItem(0);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBloodGroup.setAdapter(aa);
    }

    private void setupBloodGroupSpinner(String bloodgRoup) {
        ArrayAdapter aa = new ArrayAdapter(this, R.layout.layout_profile_spiner_item, getResources().getStringArray(R.array.blood_groups));
//        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBloodGroup.setAdapter(aa);
        try {
            if (bloodgRoup != null) {
                String[] groupList = getResources().getStringArray(R.array.blood_groups);
                int len = groupList.length;
                for (int i = 0; i < len; i++) {
                    Log.d("TAG", "setupUserData: " + gender + " : "+groupList[i]);
                    if (bloodgRoup.equalsIgnoreCase(groupList[i])) {
                        spinnerBloodGroup.setText(spinnerBloodGroup.getAdapter().getItem(i).toString(), false);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupGenderSpinner(String gender) {
        ArrayAdapter aa = new ArrayAdapter(this, R.layout.layout_profile_spiner_item, getResources().getStringArray(R.array.gender));
//        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(aa);
        try {
            if (gender != null) {
                String[] genderList = getResources().getStringArray(R.array.gender);
                int len = genderList.length;
                for (int i = 0; i < len; i++) {
                    Log.d("TAG", "setupUserData: " + gender + " : "+genderList[i]);
                    if (gender.equalsIgnoreCase(genderList[i])) {
                        spinnerGender.setText(spinnerGender.getAdapter().getItem(i).toString(), false);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
