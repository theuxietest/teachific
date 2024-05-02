package com.so.luotk.activities.adminrole;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.so.luotk.R;
import com.so.luotk.activities.PDFViewNew;
import com.so.luotk.activities.ViewAttachmentActivity;
import com.so.luotk.adapter.adminrole.SelectedAttachmentItemsAdapter;
import com.so.luotk.api.APIInterface;
import com.so.luotk.api.ApiUtils;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.databinding.ActivityCreateAssignmentTestBinding;
import com.so.luotk.databinding.LayoutDialogOptionChooserBinding;
import com.so.luotk.models.output.Data;
import com.so.luotk.models.output.GetBatchSubmitAssignmentTestResponse;
import com.so.luotk.models.output.StudentDetailsData;
import com.so.luotk.scoppedStorage.Picker;
import com.so.luotk.scoppedStorage.utils.PickerOptions;
import com.so.luotk.utils.FileUtils;
import com.so.luotk.utils.ImageRealPath;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;

import org.json.JSONArray;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.models.FileType;
import droidninja.filepicker.models.sort.SortingTypes;*/
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.so.luotk.scoppedStorage.Picker.PICKED_MEDIA_LIST;
import static com.so.luotk.scoppedStorage.Picker.PICKER_OPTIONS;
import static com.so.luotk.scoppedStorage.Picker.REQUEST_CODE_PICKER;


public class CreateAssignmentTestActivity extends AppCompatActivity {
    private final static int PERMISSION_ALL_NEW = 959;
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
    private static final int PDF_VIEW_REQUEST = 1010;
    private static final int DOC_CREATE = 4587;
    private static final int DOC_FILE_VIEW = 4587;

    private ActivityCreateAssignmentTestBinding binding;
    private String isFrom, batchId;
    private List<StudentDetailsData> studentDetailsDataList;

    private final int MAX_ATTACHMENT_COUNT = 10;
    private final ArrayList<Uri> photoPaths = new ArrayList<>();
    private final ArrayList<Uri> docPaths = new ArrayList<>();
    ArrayList<String> newAttachment = new ArrayList<>();
    private File cameraPhotoFile;
    private String optionClicked;
    private Dialog mDialog;
    private List<String> allFilePaths;
    public static List<String> allOldFilePaths;
    public static List<String> sentOldFilePaths = new ArrayList<>();
    private SelectedAttachmentItemsAdapter attachmentsAdapter;
    private ProgressView mProgressDialog;
    private APIInterface apiInterface;
    private View mainLayout;
    private String editable = "false", assignmentData;
    private String assignmentId;
    private boolean isDocSelected, isPdfSelected;
    private long mLastClickTime = 0;
    private String downloadResult, selectedAttachment;
    private File file;
    private String toolBarTitleNew;
    PickerOptions pickerOptions;
    ArrayList<String> testImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        binding = ActivityCreateAssignmentTestBinding.inflate(getLayoutInflater());
        Utilities.restrictScreenShot(this);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());
        Utilities.setUpStatusBar(this);
        Utilities.setLocale(this);
        setUpUi();
        setClickListeners();
        // setAttachmentsAdapter();
    }

    private void setAttachmentsAdapter() {
        if (!allFilePaths.isEmpty()) {
            Set<String> set = new HashSet<>(allFilePaths);
            allFilePaths.clear();
            allFilePaths.addAll(set);
            attachmentsAdapter = new SelectedAttachmentItemsAdapter(allFilePaths, false, position -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                optionClicked = "viewattachment";
                selectedAttachment = allFilePaths.get(position);
                if (android.os.Build.VERSION.SDK_INT >= PreferenceHandler.MEDIA_STORE_VERSION) {
                    if (allFilePaths.get(position).contains("storage") || allFilePaths.get(position).contains(getPackageName())) {
                        setUpAttachmentView(allFilePaths.get(position));
                    } else {
                        setupViewAttachmentClick(allFilePaths.get(position));
                    }
                } else {
                    if (allFilePaths.get(position).contains("storage") || allFilePaths.get(position).contains(getPackageName())) {
                        setUpAttachmentView(allFilePaths.get(position));
                    } else {
                        setupViewAttachmentClick(allFilePaths.get(position));
                    }
                }
            }, () -> {
            });
            binding.recylerAttachedItems.setVisibility(View.VISIBLE);
            binding.recylerAttachedItems.setAdapter(attachmentsAdapter);
        } else {
            binding.recylerAttachedItems.setVisibility(View.GONE);
        }
    }
    private boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("Return", "hasPermissions: ");
                    if (ActivityCompat.shouldShowRequestPermissionRationale(CreateAssignmentTestActivity.this, permission)){
                    } else {
                        ActivityCompat.requestPermissions(CreateAssignmentTestActivity.this,
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

    public static String[] permissions() {
        String[] p;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            p = storage_permissions_33;
        } else {
            p = storage_permissions;
        }
        return p;
    }

    private void setupViewAttachmentClick(String selectedAttachment) {
        if (!hasPermissions(CreateAssignmentTestActivity.this, permissions())) {
            ActivityCompat.requestPermissions(CreateAssignmentTestActivity.this, permissions(), PERMISSION_ALL_NEW);
            return;
        } else {
            File myFilesDir = null;
            File myFile = null;
            File file = new File(selectedAttachment);
            String strFileName = file.getName();
            String lowerpath = selectedAttachment.toLowerCase();
            if (android.os.Build.VERSION.SDK_INT >= PreferenceHandler.MEDIA_STORE_VERSION) {
                if (lowerpath.contains(".pdf") || lowerpath.contains(".doc")) {
                    myFilesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    myFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), strFileName);
                } else {
                    myFilesDir = new File(Environment
                            .getExternalStorageDirectory().getAbsolutePath()
                            + "/Download/"+ getString(R.string.app_name).replaceAll(" ", "") + "Files");
                    myFile = new File(myFilesDir, strFileName);
                }
            } else {
                myFilesDir = new File(Environment
                        .getExternalStorageDirectory().getAbsolutePath()
                        + "/Download/"+ getString(R.string.app_name).replaceAll(" ", "") + "Files");
                myFile = new File(myFilesDir, strFileName);
            }

            if (lowerpath.contains(".pdf")) {

                String docUrl = "https://web.smartowls.in/attachment/" + selectedAttachment;
                Intent in = new Intent(CreateAssignmentTestActivity.this, PDFViewNew.class);
                in.putExtra(PreferenceHandler.PDF_NAME, selectedAttachment);
                in.putExtra(PreferenceHandler.DOC_URL,docUrl);
                in.putExtra(PreferenceHandler.FROM_LOCAL,"false");
                in.putExtra(PreferenceHandler.FILE_NAME, selectedAttachment);

                startActivity(in);

            } else if (lowerpath.contains(".png")
                    || lowerpath.contains(".jpg") ||
                    selectedAttachment.contains(".jpeg") || selectedAttachment.contains(".gif")) {
                Intent intent = new Intent(this, ViewAttachmentActivity.class);
                intent.putExtra(PreferenceHandler.PDF_NAME, selectedAttachment);
                intent.putExtra(PreferenceHandler.FILE_NAME, new File(selectedAttachment).getName());
                startActivity(intent);
            } else {
                if (myFile.exists()) {
                    try {
                        if (android.os.Build.VERSION.SDK_INT >= PreferenceHandler.MEDIA_STORE_VERSION) {

                            File finalMyFile = myFile;
                            MediaScannerConnection.scanFile(this,
                                    new String[]{myFile.getAbsolutePath()}, null,
                                    new MediaScannerConnection.OnScanCompletedListener() {
                                        public void onScanCompleted(String path, Uri uri) {
                                            Log.e("ExternalStorage2", "Scanned " + path + ":");
                                            Log.e("ExternalStorage2", "-> uri=" + uri);
                                            try {
                                                openFileList(CreateAssignmentTestActivity.this, finalMyFile);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                            // only for gingerbread and newer versions
                        } else {
                            openFileList(CreateAssignmentTestActivity.this, myFile);
//                            openFile(this, myFile, Uri.fromFile(myFile));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    downloadAttachments(selectedAttachment, true, false);
                }
            }

        }
    }


    private void setUpAttachmentView(String path) {
        String filePath = "";
        if (android.os.Build.VERSION.SDK_INT >= PreferenceHandler.MEDIA_STORE_VERSION) {
            if (path.contains(".pdf") || path.contains(".doc")) {
                filePath = path.replace("/data/user/0/" + getPackageName() + "/cache/", "");
            } else {
                filePath = path/*.replace("/storage/emulated/0", "")*/;
            }
        } else {
            if (path.contains(".pdf") || path.contains(".doc")) {
                if (path.contains(getPackageName())) {
                    filePath = path.replace("/data/user/0/" + getPackageName() + "/cache/", "");
                } else {
                    filePath = path/*.replace("/storage/emulated/0", "")*/;
                }
            } else {
                filePath = path/*.replace("/storage/emulated/0", "")*/;
            }
        }

        Log.d("TAG", "setUpAttachmentView: " + filePath + " : " + path);
        if (path.contains(".jpg") || path.contains(".jpeg") || path.contains(".png")) {
            startActivity(new Intent(this, ViewAttachmentActivity.class).putExtra("isFrom", "adminAttachment")
                    .putExtra(PreferenceHandler.PDF_NAME, filePath)
                    .putExtra(PreferenceHandler.FILE_NAME, new File(filePath).getName())

            );
        }
        if (path.contains(".pdf")) {
            Log.d("FileName", "setUpAttachmentView: " + filePath);
            startActivity(new Intent(this, PDFViewNew.class)
                    .putExtra(PreferenceHandler.PDF_NAME, filePath)
                    .putExtra(PreferenceHandler.DOC_URL, path)
                    .putExtra(PreferenceHandler.FILE_NAME, filePath)
                    .putExtra(PreferenceHandler.FROM_LOCAL, "true")
                    );
        }
        if (path.contains(".doc") || path.contains(".ppt")) {
            if (android.os.Build.VERSION.SDK_INT >= PreferenceHandler.MEDIA_STORE_VERSION) {
                MediaScannerConnection.scanFile(this,
                        new String[]{path}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String pathhh, Uri uri) {
                                Log.e("ExternalStorageA", "Scanned " + pathhh + ":");
                                Log.e("ExternalStorageA", "-> uri=" + uri);
                                try {
                                    openDocFile(CreateAssignmentTestActivity.this, new File(path));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

            } else {
                try {
                    openFile(this, filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void openFile(Context context, File url, Uri uriFrom) {
        // Create URI
        File file = url;
        Uri uri;
        if (android.os.Build.VERSION.SDK_INT >= PreferenceHandler.MEDIA_STORE_VERSION) {
            uri = Uri.fromFile(url);
        } else {
            uri = Uri.fromFile(file);
        }
        Log.d("TAG", "openFile: " + file.getAbsolutePath());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(file.getPath()));
        Log.d("TAG", "openFile: " + mimeType);
        intent.setDataAndType(uri, mimeType);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(PreferenceHandler.PDF_NAME, url.getName());
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        List resolvedActivityList;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            resolvedActivityList =
                    packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        } else {
            resolvedActivityList = packageManager.queryIntentActivities(intent, 0);
        }
        boolean isIntentSafe = resolvedActivityList.size() > 0;
        intent = Intent.createChooser(intent, "Open File");
        if (isIntentSafe) {
            // startActivityForResult(intent,101);
            startActivityForResult(intent, DOC_FILE_VIEW);
        } else {
            Toast.makeText(this, getString(R.string.device_doesnot_support_file), Toast.LENGTH_SHORT).show();

        }

    }

    private void setClickListeners() {
        binding.tvSelectDate.setOnClickListener(v -> {
            Utilities.openDatePickerSlashTextView(this, binding.tvSelectDate, false);
        });
        binding.tvSelectTime.setOnClickListener(view -> {
            Utilities.openTimePicker24(this, binding.tvSelectTime);
        });
        binding.tvSelectAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                optionClicked = "gallery";
                if (!hasPermissions(CreateAssignmentTestActivity.this, permissions())) {
                    ActivityCompat.requestPermissions(CreateAssignmentTestActivity.this, permissions(), PERMISSION_ALL_NEW);
                    return;
                } else {
                    if (allFilePaths.size() < 10)
                        openOptionChooserDialog();
//                        openImagePicker();
                    else
                        Toast.makeText(CreateAssignmentTestActivity.this, "Cannot attach more than 10 items", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("TAG", "onClickAll: " + allFilePaths.toString());
                Log.d("TAG", "onClickAllOld: " + allOldFilePaths.toString());
                if (checkValidation()) {
                    newAttachment.clear();
                    sentOldFilePaths.clear();
                    sentOldFilePaths = findCommonElement(allFilePaths, allOldFilePaths);
                    Log.d("TAG", "onClickSendOld: " + sentOldFilePaths.toString());
                    if (allOldFilePaths.size() > 0) {
                        if (sentOldFilePaths.size() > 0) {
                            for (int i = 0; i < sentOldFilePaths.size(); i++) {
                                for (int j = 0; j < allFilePaths.size(); j++) {
                                    if (allFilePaths.get(j).equals(sentOldFilePaths.get(i))) {
                                        Log.d("TAG", "onClick: Equal");
                                    } else {
                                        if (findItemInTheList(allFilePaths.get(j))) {
                                            Log.d("TAG", "onClick: true");
                                        } else {
                                            if (allFilePaths.get(j).contains("storage")) {
                                                newAttachment.add(allFilePaths.get(j));
                                                Log.d("TAG", "onClick: Not Equal");
                                            } else if (allFilePaths.get(j).contains(getPackageName())) {
                                                newAttachment.add(allFilePaths.get(j));
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            newAttachment.addAll(allFilePaths);
                        }
                    } else {
                        newAttachment.addAll(allFilePaths);
                    }
                    Log.d("TAG", "onClickGet: " + sentOldFilePaths.toString() + " : " + allFilePaths.toString() + " : " + newAttachment.toString());
                    if (Utilities.checkInternet(CreateAssignmentTestActivity.this)) {
                        ArrayList<String> studentsArray = new ArrayList<>();
                        String currentDate = getCurrentDateTime();
                        studentDetailsDataList = (List<StudentDetailsData>) getIntent().getSerializableExtra("selectedlist");
                        studentsArray.clear();
                        for (int i = 0; i < studentDetailsDataList.size(); i++) {
                            Log.d("TAG", "setUpUi: " + studentDetailsDataList.get(i).getId());
                            studentsArray.add(String.valueOf(studentDetailsDataList.get(i).getId()));
                        }
                        String IsNotify = "";
                        if (binding.checkboxNotify.isChecked()) {
                            IsNotify = "1";
                        } else {
                            IsNotify = "0";
                        }
                        if (batchId != null) {
                            hitSubmitAssignmentTestService(binding.edtTopic.getText().toString(), batchId, binding.tvSelectDate.getText().toString(), binding.tvSelectTime.getText().toString()
                                    , binding.edtNotes.getText().toString(), IsNotify, studentsArray, allFilePaths, editable);
                        }
                    } else {
                        Snackbar.make(mainLayout, "Check internet connection", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private boolean checkValidation() {
        boolean val = true;
        if (TextUtils.isEmpty(binding.tvSelectTime.getText().toString().trim())) {
//            binding.erTime.setVisibility(View.VISIBLE);
            binding.tvSelectTime.requestFocus();
            binding.tvSelectTime.getParent().requestChildFocus(binding.tvSelectTime, binding.tvSelectTime);
            binding.tvSelectTimeLay.setErrorEnabled(true);
            binding.tvSelectTimeLay.setError(getString(R.string.required_time));
            binding.tvSelectTimeLay.setErrorIconDrawable(0);
            val = false;
        } else {
            binding.erTime.setVisibility(View.GONE);
            binding.tvSelectTimeLay.setErrorEnabled(false);
            binding.tvSelectTimeLay.setError(null);
        }
        if (TextUtils.isEmpty(binding.tvSelectDate.getText().toString().trim())) {
//            binding.erDate.setVisibility(View.VISIBLE);
            binding.tvSelectDate.requestFocus();
            binding.tvSelectDate.getParent().requestChildFocus(binding.tvSelectDate, binding.tvSelectDate);
            binding.tvSelectDateLay.setErrorEnabled(true);
            binding.tvSelectDateLay.setError(getString(R.string.required_date));
            binding.tvSelectDateLay.setErrorIconDrawable(0);
            val = false;
        } else {
            binding.erDate.setVisibility(View.GONE);
            binding.tvSelectDateLay.setErrorEnabled(false);
            binding.tvSelectDateLay.setError(null);
        }
        if (TextUtils.isEmpty(binding.edtTopic.getText().toString().trim())) {
//            binding.erTopic.setVisibility(View.VISIBLE);
            val = false;
            binding.edtTopic.requestFocus();
            binding.edtTopic.getParent().requestChildFocus(binding.edtTopic, binding.edtTopic);
            binding.edtTopicLay.setErrorEnabled(true);
            binding.edtTopicLay.setError(getString(R.string.required_topic));
            binding.edtTopicLay.setErrorIconDrawable(0);
        } else {
            binding.erTopic.setVisibility(View.GONE);
            binding.edtTopicLay.setErrorEnabled(false);
            binding.edtTopicLay.setError(null);
        }
        return val;
    }

    private boolean findItemInTheList(String itemToFind) {
        if (newAttachment.size() > 0) {
            if (newAttachment.contains(itemToFind)) {
                Log.d("TAG", "findItemInTheList: " + itemToFind + " was found in the list");
                return true;

            } else {
                Log.d("TAG", "findItemInTheList: " + itemToFind + " was not found in the list");
                return false;

            }
        } else {
            return false;
        }
    }

    public static RequestBody toRequestBody(String value) {
        Log.d("TAG", "toRequestBody: " + value);
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value);
        return body;
    }

    private void setUpUi() {
        mainLayout = binding.itemLayout;
        apiInterface = ApiUtils.getApiInterface();
        allFilePaths = new ArrayList<>();
        allOldFilePaths = new ArrayList<>();
        if (getIntent() != null) {
            isFrom = getIntent().getStringExtra(PreferenceHandler.IS_FROM);
            toolBarTitleNew = getIntent().getStringExtra("title");
            studentDetailsDataList = (List<StudentDetailsData>) getIntent().getSerializableExtra("selectedlist");
            batchId = getIntent().getStringExtra(PreferenceHandler.BATCH_ID);

            if (getIntent().getStringExtra("edit") != null) {
                editable = getIntent().getStringExtra("edit");
            }
        }
        if (!TextUtils.isEmpty(isFrom)) {
            binding.toolbarLayout.toolbarTitle.setText(toolBarTitleNew);
        }

        pickerOptions = PickerOptions.init();
        pickerOptions.setAllowFrontCamera(true);
        pickerOptions.setMaxCount(10);
        pickerOptions.setExcludeVideos(true);
        pickerOptions.setPreSelectedMediaList(testImages);
        binding.toolbarLayout.textSteps.setText(getString(R.string.step) + " 2/2");
        binding.toolbarLayout.toolbar.setNavigationIcon(Utilities.setNavigationIconColorBlack(this));
        binding.toolbarLayout.toolbar.setNavigationOnClickListener(view -> finish());
        binding.recylerAttachedItems.setLayoutManager(new LinearLayoutManager(this));
        if (isFrom.equalsIgnoreCase("test"))
            binding.layoutTestItems.setVisibility(View.VISIBLE);
        else
            binding.layoutTestItems.setVisibility(View.GONE);


        if (isFrom.equals("assignment")) {
            if (editable.equals("true")) {
                assignmentData = getIntent().getStringExtra("assignmentData");
                Data assignmentListData = new Gson().fromJson(assignmentData, Data.class);
                setAssignmentEditData(assignmentListData);
            }
        }

    }


    private void setAssignmentEditData(Data assignmentListData) {
        try {
            allFilePaths.clear();
            allOldFilePaths.clear();
            Log.d("TAG", "setAssignmentEditData: " + assignmentListData.getSubmit_date());
            assignmentId = assignmentListData.getId();
            Log.d("TAG", "setAssignmentEditData: " + assignmentId);
            binding.edtTopic.setText(assignmentListData.getTopic());
            if (!TextUtils.isEmpty(assignmentListData.getSubmitDate())) {
                String date = assignmentListData.getSubmitDate();
                SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd");
                Date newDate = null;
                try {
                    newDate = spf.parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                spf = new SimpleDateFormat("dd/MM/yyyy");
                date = spf.format(newDate);

                binding.tvSelectDate.setText(date);
            }
            if (!TextUtils.isEmpty(assignmentListData.getSubmitTime())) {
                Log.d("TAG", "setAssignmentEditData: " + assignmentListData.getSubmitTime());
                if (assignmentListData.getSubmitTime().toLowerCase().contains("am") || assignmentListData.getSubmitTime().toLowerCase().contains("pm")) {
                    String inputFormat = "hh:mm a";
                    String OutPutFormat = "HH:mm";
                    String convertedDate = formatDate(assignmentListData.getSubmitTime(), inputFormat, OutPutFormat);
                    binding.tvSelectTime.setText(convertedDate);
                } else {
                    binding.tvSelectTime.setText(assignmentListData.getSubmitTime());
                }

            }

//            binding.tvSelectTime.setText(assignmentListData.getSubmitTime());
            binding.edtNotes.setText(assignmentListData.getNotes());
            binding.checkboxNotify.setChecked(!assignmentListData.getIsSms().equals("0"));

            convertStringToJson(assignmentListData.getAttachment());
            setAttachmentsAdapter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String formatDate(String dateToFormat, String inputFormat, String outputFormat) {
        try {
            Log.e("DATE", "Input Date Date is " + dateToFormat);

            String convertedDate = new SimpleDateFormat(outputFormat)
                    .format(new SimpleDateFormat(inputFormat)
                            .parse(dateToFormat));

            Log.e("DATE", "Output Date is " + convertedDate);

            //Update Date
            return convertedDate;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;

    }

    private void convertStringToJson(String attachment) {
        try {

            allOldFilePaths.clear();
            Log.d("TAG", "convertStringToJson: " + attachment);
            if (attachment != null) {
                JSONArray jsonArray = new JSONArray(attachment);
                int arrLength = jsonArray.length();
                if (arrLength > 0) {
                    for (int i = 0; i < arrLength; i++) {
                        String item = (String) jsonArray.get(i);
                        allOldFilePaths.add(item.replace("attachment/", ""));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        allFilePaths.addAll(allOldFilePaths);
    }


    public void openOptionChooserDialog() {
        mDialog = new Dialog(this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_dialog_option_chooser, null, false);
        LayoutDialogOptionChooserBinding dialogBinding = LayoutDialogOptionChooserBinding.bind(view);
        mDialog.setContentView(dialogBinding.getRoot());
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(mDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mDialog.show();
        mDialog.setCancelable(true);

        dialogBinding.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasPermissions(CreateAssignmentTestActivity.this, permissions())) {
                    ActivityCompat.requestPermissions(CreateAssignmentTestActivity.this, permissions(), PERMISSION_ALL_NEW);
                    return;
                }
                optionClicked = "gallery";

                    if (!hasPermissions(CreateAssignmentTestActivity.this, permissions())) {
                        ActivityCompat.requestPermissions(CreateAssignmentTestActivity.this, permissions(), PERMISSION_ALL_NEW);
                        return;
                    } else {
                        if (Utilities.isCameraPermitted(CreateAssignmentTestActivity.this)) {
                            if (allFilePaths.size() < 10) {
                                Intent mPickerIntent = new Intent(CreateAssignmentTestActivity.this, Picker.class);
                                mPickerIntent.putExtra(PICKER_OPTIONS, pickerOptions);
                                mPickerIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivityForResult(mPickerIntent, REQUEST_CODE_PICKER);
                            } else {
                                Toast.makeText(CreateAssignmentTestActivity.this, "Cannot attach more than 10 items", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Utilities.requestCameraPermission(CreateAssignmentTestActivity.this, 2000);
                        }
                    }


            }
        });
        dialogBinding.layoutAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionClicked = "attachment";
                if (!hasPermissions(CreateAssignmentTestActivity.this, permissions())) {
                    ActivityCompat.requestPermissions(CreateAssignmentTestActivity.this, permissions(), PERMISSION_ALL_NEW);
                    return;
                } else {
                    if (allFilePaths.size() < 10)
                        createFile();
                    else
                        Toast.makeText(CreateAssignmentTestActivity.this, "Cannot attach more than 10 items", Toast.LENGTH_SHORT).show();
                }

            }
        });
        mDialog.show();
    }
    private void createFile() {
        String[] mimeTypes =
                {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/pdf"
                };
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }
//        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);
        startActivityForResult(intent, DOC_CREATE);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        switch (requestCode) {

            case REQUEST_CODE_PICKER:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        if (data.getStringArrayListExtra(PICKED_MEDIA_LIST).size() > 0) {
                            for (int i = 0; i < data.getStringArrayListExtra(PICKED_MEDIA_LIST).size(); i++) {

                                if (data.getStringArrayListExtra(PICKED_MEDIA_LIST).get(i).contains("content:")) {
                                    String imageGalleryPath = ImageRealPath.getUriRealPath(CreateAssignmentTestActivity.this, Uri.parse(data.getStringArrayListExtra(PICKED_MEDIA_LIST).get(i)));
                                    testImages.add(imageGalleryPath);
                                    if (allFilePaths.size() < 11) {
                                        allFilePaths.add(imageGalleryPath);
                                    }
                                } else {
                                    testImages.add(data.getStringArrayListExtra(PICKED_MEDIA_LIST).get(i));
                                    String imageCameraPath = ImageRealPath.getUriRealPath(CreateAssignmentTestActivity.this, Uri.parse(data.getStringArrayListExtra(PICKED_MEDIA_LIST).get(i)));
                                    Log.d("TAG", "onActivityResult: " + imageCameraPath);
                            /*String path = Uri.parse(data.getStringArrayListExtra(PICKED_MEDIA_LIST).get(i)).toString(); // "file:///mnt/sdcard/FileName.mp3"

                            File file = new File(path);*/
                                    if (allFilePaths.size() < 11) {
                                        allFilePaths.add(imageCameraPath);
                                    }
                                }

                            }
                            setAttachmentsAdapter();
                        }
                    }
                }
                break;
            case DOC_CREATE:
                if (resultCode == RESULT_OK && data != null) {
                        if (null != data.getClipData()) { // checking multiple selection or not
                            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                                Uri uri = data.getClipData().getItemAt(i).getUri();
                                getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                DocumentFile documentFile = DocumentFile.fromSingleUri(CreateAssignmentTestActivity.this, uri);
                                try {
                                    File file = FileUtils.from(CreateAssignmentTestActivity.this, documentFile.getUri());
                                    allFilePaths.add(file.getAbsolutePath());
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        } else {
                            Uri uri = data.getData();
                            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            DocumentFile documentFile = DocumentFile.fromSingleUri(CreateAssignmentTestActivity.this, uri);
                            try {
                                File file = FileUtils.from(CreateAssignmentTestActivity.this, documentFile.getUri());
                                allFilePaths.add(file.getAbsolutePath());
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        setAttachmentsAdapter();
                }
                break;
        }
    }


    private void openFile(Context context, String path) throws IOException {
        // Create URI
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + path);
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(file.getPath()));
        intent.setDataAndType(uri, mimeType);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PackageManager packageManager = getPackageManager();

        List resolvedActivityList;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            resolvedActivityList =
                    packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        } else {
            resolvedActivityList = packageManager.queryIntentActivities(intent, 0);

        }
        boolean isIntentSafe = resolvedActivityList.size() > 0;
        intent = Intent.createChooser(intent, "Open File");
        if (isIntentSafe) {
            // startActivityForResult(intent,101);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Your device does not support this file", Toast.LENGTH_SHORT).show();

        }
        PreferenceHandler.writeBoolean(this, PreferenceHandler.EXTERNAL_APP, false);
        PreferenceHandler.writeString(this, PreferenceHandler.EXTERNAL_DOC, new File(path).getAbsolutePath());

    }

    private void hitSubmitAssignmentTestService(final String topic, final String fk_batchId, final String submitDate, final String submitTime, final String notes,
                                                final String isSms, final ArrayList<String> students, final List<String> attachmentArray, String editable) {
        Map<String, RequestBody> map = new HashMap<>();
        Map<String, String> checkMap = new HashMap<>();
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", PreferenceHandler.readString(this, PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(this, PreferenceHandler.DEVICE_ID, ""));
        Call<GetBatchSubmitAssignmentTestResponse> call = null;
        MultipartBody.Part[] multipartImage = null;
        MultipartBody.Part[] multipartImageEditNew = null;

        if (editable.equals("true")) {
            multipartImageEditNew = new MultipartBody.Part[newAttachment.size()];
            if (!assignmentId.isEmpty()) {
                map.put("assignmentId", toRequestBody(assignmentId));
                checkMap.put("assignmentId", assignmentId);
            }
            if (newAttachment.size() > 0) {
                for (int index = 0; index < newAttachment.size(); index++) {
                    File file = new File(newAttachment.get(index));
                    RequestBody assignmentImgRequestBody11 = RequestBody.create(MediaType.parse("*/*"), file);/*getContentResolver().getType(uri)*/
                    if (isFrom.equalsIgnoreCase("assignment")) {
                        Log.d("TAG", "hitSubmitAssignmentTestService: Check");
                        multipartImageEditNew[index] = MultipartBody.Part.createFormData("attachment[]", file.getName(), assignmentImgRequestBody11);
                    } else {
                        multipartImageEditNew[index] = MultipartBody.Part.createFormData("test[]", file.getName(), assignmentImgRequestBody11);
                    }

                }
            }
        } else {
            multipartImage = new MultipartBody.Part[attachmentArray.size()];
            if (allFilePaths.size() > 0) {
                for (int index = 0; index < attachmentArray.size(); index++) {
                    File file = new File(attachmentArray.get(index));
                    RequestBody assignmentImgRequestBody22 = RequestBody.create(MediaType.parse("*/*"), file);/*getContentResolver().getType(uri)*/
                    if (isFrom.equalsIgnoreCase("assignment")) {
                        Log.d("TAG", "hitSubmitAssignmentTestService: Check");
                        multipartImage[index] = MultipartBody.Part.createFormData("attachment[]", file.getName(), assignmentImgRequestBody22);
                    } else {
                        multipartImage[index] = MultipartBody.Part.createFormData("test[]", file.getName(), assignmentImgRequestBody22);
                    }

                }
            }
        }
        Log.d("TAG", "field: " + editable);
        if (!topic.isEmpty()) {
            map.put("topic", toRequestBody(topic));
            checkMap.put("topic", topic);
        }
        if (!fk_batchId.isEmpty()) {
            map.put("fk_batchId", toRequestBody(fk_batchId));
            checkMap.put("fk_batchId", fk_batchId);
        }
        if (!submitDate.isEmpty()) {
            map.put("submitDate", toRequestBody(submitDate));
            checkMap.put("submitDate", submitDate);
        }
        if (!submitTime.isEmpty()) {
            map.put("submitTime", toRequestBody(submitTime));
            checkMap.put("submitTime", submitTime);
        }
        if (!notes.isEmpty()) {
            map.put("notes", toRequestBody(notes));
            checkMap.put("notes", notes);
        }
        if (!isSms.isEmpty()) {
            map.put("isSms", toRequestBody(isSms));
            checkMap.put("isSms", isSms);
        }

        for (Map.Entry<String, String> entry : checkMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            Log.d("TAG", "hitCreateEditBatch: " + key + " : " + value);

        }
        Log.d("+++", "hitSubmitAssignmentTestService: " + "\nNew File: " + newAttachment.toString() + "\nAll Old File: " + sentOldFilePaths.toString() + "\nAll Files: " + allFilePaths.toString() + "\nStudentArray: " + students.toString());
        if (isFrom.equalsIgnoreCase("assignment")) {
            try {
                if (editable.equals("true")) {
                    if (newAttachment.size() > 0) {
                        Log.d("TAG", "hitSubmitAssignmentTestService: 000");
                        if (sentOldFilePaths.size() > 0) {
                            Log.d("TAG", "hitSubmitAssignmentTestService: 111");
                            call = apiInterface.updateAssignment(headers, map, students.toString(), sentOldFilePaths, multipartImageEditNew);
                        } else {
                            Log.d("TAG", "hitSubmitAssignmentTestService: 222");
                            call = apiInterface.updateAssignment(headers, map, students.toString(), multipartImageEditNew);
                        }
                    } else {
                        if (sentOldFilePaths.size() > 0) {
                            Log.d("TAG", "hitSubmitAssignmentTestService: 333");
                            call = apiInterface.updateAssignment(headers, map, students.toString(), sentOldFilePaths);
                        } else {
                            call = apiInterface.updateAssignment(headers, map, students.toString());
                        }
                    }
                } else {
                    if (allFilePaths.size() > 0) {
                        call = apiInterface.createNewAssignment(headers, map, students, multipartImage);
                    } else {
                        call = apiInterface.createNewAssignment(headers, map, students);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mProgressDialog = new ProgressView(this);
        mProgressDialog.show();
        try {
            call.enqueue(new Callback<GetBatchSubmitAssignmentTestResponse>() {
                @Override
                public void onResponse(Call<GetBatchSubmitAssignmentTestResponse> call, Response<GetBatchSubmitAssignmentTestResponse> response) {
                    mProgressDialog.dismiss();
                    try {
                        if (response.isSuccessful()) {
                            if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200") && response.body().getSuccess().equalsIgnoreCase("true")) {
                                if (response.body().getResult() != null && response.body().getResult().equalsIgnoreCase("success")) {
                                    if (isFrom.equalsIgnoreCase("assignment")) {
                                        Snackbar.make(mainLayout, getString(R.string.assihnmet_submitted_success), Snackbar.LENGTH_SHORT).show();
                                    } else {
                                        Snackbar.make(mainLayout, getString(R.string.test_submit_success), Snackbar.LENGTH_SHORT).show();
                                    }

                                    if (editable.equals("true")) {
                                        Intent intent = new Intent();
                                        intent.putExtra(PreferenceHandler.IS_DATA_SUBMITTED, true);
                                        intent.putExtra(PreferenceHandler.TOPIC_NAME, topic);
                                        intent.putExtra(PreferenceHandler.DATE, submitDate);
                                        intent.putExtra(PreferenceHandler.TIME, submitTime);
                                        intent.putExtra(PreferenceHandler.NOTES, notes);
                                        intent.putExtra(PreferenceHandler.IS_SMS, isSms);
                                        intent.putExtra(PreferenceHandler.ATTACHMENT, allFilePaths.toString());
                                        intent.putExtra(PreferenceHandler.IS_FROM, isFrom);
                                        setResult(Activity.RESULT_OK, intent);
                                        finish();
                                    } else {
                                        Intent intent = new Intent();
                                        intent.putExtra(PreferenceHandler.IS_DATA_SUBMITTED, true);
                                        intent.putExtra(PreferenceHandler.IS_FROM, isFrom);
                                        setResult(Activity.RESULT_OK, intent);
                                        finish();
                                    }

                                }
                            } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                                Utilities.openUnauthorizedDialog(CreateAssignmentTestActivity.this);
                            } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("406")) {
                                Utilities.makeToast(getApplicationContext(), "406 error");
                            } else {
                                Utilities.makeToast(getApplicationContext(), getString(R.string.server_error));
                            }
                        } else {
                            Utilities.makeToast(getApplicationContext(), getString(R.string.assignment_not_submitted));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<GetBatchSubmitAssignmentTestResponse> call, Throwable t) {
                    mProgressDialog.dismiss();
                    Utilities.makeToast(getApplicationContext(), getString(R.string.server_error));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getCurrentDateTime() {
        Date currentTime = Calendar.getInstance().getTime();
        return Utilities.formatCurrentDate(currentTime);
    }


    private void openFileList(Context context, File url) throws IOException {
        try {
            File file = url;
            Uri uri = FileProvider.getUriForFile(context, getPackageName(), url);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(file.getPath()));
            intent.setDataAndType(uri, mimeType);
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK /*| Intent.FLAG_GRANT_READ_URI_PERMISSION */ | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PackageManager packageManager = getPackageManager();
            List<ResolveInfo> activities = packageManager.queryIntentActivities(intent,
                    PackageManager.MATCH_DEFAULT_ONLY);
            boolean isIntentSafe = activities.size() > 0;
            intent = Intent.createChooser(intent, "Open File");
            if (isIntentSafe) {
                // startActivityForResult(intent,101);
                try {
                    startActivity(intent);
                } catch (Exception e) {

                }

            } else {
                Snackbar.make(mainLayout, getString(R.string.device_doesnot_support_file), Snackbar.LENGTH_SHORT).show();

            }
            PreferenceHandler.writeBoolean(this, PreferenceHandler.EXTERNAL_APP, true);
            PreferenceHandler.writeString(this, PreferenceHandler.EXTERNAL_DOC, url.getAbsolutePath());
        } catch (Exception e) {
            openFile(CreateAssignmentTestActivity.this, url, Uri.fromFile(url));
//            e.printStackTrace();
        }

    }

    private void openDocFile(Context context, File url) throws IOException {
        try {
            File file = url;
            Uri uri = FileProvider.getUriForFile(context, getPackageName(), url);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(file.getPath()));
            intent.setDataAndType(uri, mimeType);
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK /*| Intent.FLAG_GRANT_READ_URI_PERMISSION */ | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PackageManager packageManager = getPackageManager();
            List<ResolveInfo> activities = packageManager.queryIntentActivities(intent,
                    PackageManager.MATCH_DEFAULT_ONLY);
            boolean isIntentSafe = activities.size() > 0;
            intent = Intent.createChooser(intent, "Open File");
            if (isIntentSafe) {
                // startActivityForResult(intent,101);
                try {
                    startActivity(intent);
                } catch (Exception e) {

                }

            } else {
                Snackbar.make(mainLayout, getString(R.string.device_doesnot_support_file), Snackbar.LENGTH_SHORT).show();

            }
            PreferenceHandler.writeBoolean(this, PreferenceHandler.EXTERNAL_APP, false);
            PreferenceHandler.writeString(this, PreferenceHandler.EXTERNAL_DOC, url.getAbsolutePath());
        } catch (Exception e) {
            openFile(CreateAssignmentTestActivity.this, url, Uri.fromFile(url));
//            e.printStackTrace();
        }

    }

    private void downloadAttachments(String attachmentToDownload, boolean isDoc, boolean isPdf) {
        isPdfSelected = isPdf;
        isDocSelected = isDoc;
        DownloadAttachmentFile downloadFile = new DownloadAttachmentFile(CreateAssignmentTestActivity.this, attachmentToDownload, isDoc);
        downloadFile.execute();
    }


    public class DownloadAttachmentFile extends AsyncTask<Void, Void, String> {
        private final ProgressView mProgressDialog;
        private final Context context;
        private final String filename;

        public DownloadAttachmentFile(Context context, String fileName, boolean isDoc) {
            this.context = context;
            this.filename = fileName;
            mProgressDialog = new ProgressView(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();

        }

        @Override
        protected String doInBackground(Void... params) {
            //String filename = "somefile.pdf";
            String path = "true";
            HttpURLConnection c;
            if (android.os.Build.VERSION.SDK_INT >= PreferenceHandler.MEDIA_STORE_VERSION) {
                Uri newFileUri = addFileToDownloadsApi29(filename);

                OutputStream outputStream;
                ResponseBody responseBody = downloadFileFromInternet("https://web.smartowls.in/attachment/" + filename);
                try {
                    outputStream = getContentResolver().openOutputStream(newFileUri, "w");
                    outputStream.write(responseBody.bytes());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("TAG", "doInBackground: " + responseBody);
                path = getMediaStoreEntryPathApi29(newFileUri);

                Log.d("Path", "doInBackground: " + path);

                // only for gingerbread and newer versions
            } else {
                try {
                    URL url = new URL("https://web.smartowls.in/attachment/" + filename);
                    c = (HttpURLConnection) url.openConnection();
                    c.setRequestMethod("GET");
                    c.setDoOutput(true);
                    c.connect();
                } catch (IOException e1) {
                    return e1.getMessage();
                }

                File myFilesDir = new File(Environment
                        .getExternalStorageDirectory().getAbsolutePath()
                        + "/Download/"+ getString(R.string.app_name).replaceAll(" ", "") + "Files");

                file = new File(myFilesDir, filename);

                if (file.exists()) {
                    file.delete();
                }

                if ((myFilesDir.mkdirs() || myFilesDir.isDirectory())) {
                    try {
                        InputStream is = c.getInputStream();
                        FileOutputStream fos = new FileOutputStream(myFilesDir
                                + "/" + filename);

                        byte[] buffer = new byte[1024];
                        int len1 = 0;
                        while ((len1 = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, len1);
                        }
                        fos.close();
                        is.close();

                    } catch (Exception e) {
                        return e.getMessage();
                    }


                } else {
                    return "Unable to create folder";
                }
            }

            return path;
        }


        @Override
        protected void onPostExecute(String result) {
            downloadResult = result;
            mProgressDialog.dismiss();
            if (android.os.Build.VERSION.SDK_INT >= PreferenceHandler.MEDIA_STORE_VERSION) {
                File file = new File(getExternalFilesDir(null), filename);
                Log.d("TAG", "onPostExecute: " + file.getAbsolutePath());
                Log.d("TAG", "onPostExecute: " + file.exists());

                MediaScannerConnection.scanFile(CreateAssignmentTestActivity.this,
                        new String[]{result}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                                Log.e("ExternalStorage3", "Scanned " + path + ":");
                                Log.e("ExternalStorage3", "-> uri=" + uri);
//                                    openFile(context, file, uri);
                                File fileeee = new File(path);
                                Log.d("TAG", "onScanCompleted: " + fileeee.exists());
                                if (isDocSelected && fileeee != null) {
                                    try {
                                        openFileList(CreateAssignmentTestActivity.this, fileeee);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });

            } else {
                if (result != null && result.equalsIgnoreCase("true")) {
                    if (isDocSelected && file != null) {
                        try {
                            openFileList(CreateAssignmentTestActivity.this, file);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            super.onPostExecute(result);
        }
    }

    private Uri addFileToDownloadsApi29(String filename) {
        Uri collection = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        }

        ContentValues values = new ContentValues();
        values.put(MediaStore.Downloads.DISPLAY_NAME, filename);
        ContentResolver contentResolver = getContentResolver();

        Uri downloadedFileUri = contentResolver.insert(collection, values);
        return downloadedFileUri;
    }

    private String getMediaStoreEntryPathApi29(Uri uri) {

        Cursor cursor = getContentResolver().query(
                uri,
                new String[]{MediaStore.Files.FileColumns.DATA},
                null,
                null,
                null
        );
        Log.d("TAG", "getMediaStoreEntryPathApi29: " + cursor);
        while (!cursor.moveToNext()) {
            // Use an ID column from the projection to get
            // a URI representing the media item itself.
            return null;
        }
        Log.d("TAG", "getMediaStoreEntryPathApi29: " + cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)));
        return cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));

    }

    private ResponseBody downloadFileFromInternet(String url) {
        // We use OkHttp to create HTTP request
        OkHttpClient httpClient = new OkHttpClient();
        okhttp3.Response response = null;
        try {
            response = httpClient.newCall(new Request.Builder().url(url).build()).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("TAG", "downloadFileFromInternet: " + response + " : " + response.body());
        return response.body();
    }

    public List<String> findCommonElement(List<String> a, List<String> b) {

        List<String> commonElements = new ArrayList<String>();

        for (int i = 0; i < a.size(); i++) {
            for (int j = 0; j < b.size(); j++) {
                if (a.get(i) == b.get(j)) {
                    //Check if the list already contains the common element
                    if (!commonElements.contains(a.get(i))) {
                        //add the common element into the list
                        commonElements.add(a.get(i));
                    }
                }
            }
        }
        return commonElements;
    }
}