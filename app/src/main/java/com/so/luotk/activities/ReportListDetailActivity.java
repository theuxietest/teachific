package com.so.luotk.activities;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.so.luotk.R;
import com.so.luotk.adapter.adminrole.SelectedAttachmentItemsAdapter;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.databinding.ActivityReportListDetailBinding;
import com.so.luotk.models.output.Data;
import com.so.luotk.models.output.ReportTestData;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

public class ReportListDetailActivity extends AppCompatActivity implements View.OnClickListener {
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
    private static final int DOC_FILE_VIEW = 1011;
    private ActivityReportListDetailBinding binding;
    private Toolbar toolbar;
    private TextView toolbarCustomTitle;
    private LinearLayout cardViewMarksReport, cardViewAnswerKey, cardViewAttachment;
    private MaterialCardView cardViewAssignment;
    private TextView tvTopicName, tvSubTopicName, tvdate, tvTime, tvMarks, tvAttempted, tvCorrect, tvWrong, tvAccuracy;
    private String isFrom;
    private Data assignmentDataItem;
    private ReportTestData reportDataItem;
    private ArrayList<String> answerKeyList, userAnswerList, teacherAttachmentsList;
    private boolean isDocSelected, isPdfSelected;
    private File file;
    private View rootLayout;
    private RecyclerView answerKeyRecyclerView, userAnswerRecycler, teacherAttachmentsRecycler;
    private String selectedAttachment, selectedAnswer;
    private SelectedAttachmentItemsAdapter adapter, userAdapter, attachmentAdapter;
    private long mLastClickTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.restrictScreenShot(this);
// getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        binding = ActivityReportListDetailBinding.inflate(getLayoutInflater());
        Utilities.setLocale(this);
        Utilities.setUpStatusBar(this);
        setContentView(binding.getRoot());
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        setToolbar();
        setupUI();
        Log.d("TAGDetail", "onCreate: ");
    }

    private void setToolbar() {
        //toolbar setup
        toolbar = findViewById(R.id.toolbar);
        toolbarCustomTitle = findViewById(R.id.tv_toolbar_title);
        setSupportActionBar(toolbar);
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(upArrow);
        if (getIntent() != null) {
            isFrom = getIntent().getStringExtra(PreferenceHandler.IS_FROM);
            if (isFrom.equalsIgnoreCase("test")) {
                reportDataItem = (ReportTestData) getIntent().getSerializableExtra(PreferenceHandler.LIST_ITEM);
            } else {
                assignmentDataItem = (Data) getIntent().getSerializableExtra(PreferenceHandler.LIST_ITEM);
            }
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void setupUI() {
        cardViewAssignment = binding.cardViewAssignment;
        cardViewMarksReport = binding.cardViewMarksReport;
        cardViewAnswerKey = binding.cardViewAnswerKey;
        tvTopicName = binding.tvTopicName;
        tvSubTopicName = binding.tvSubTopicName;
        tvdate = binding.tvAssignDate;
        tvTime = binding.tvAssignTime;
        tvMarks = binding.tvMarks;
        tvAttempted = binding.tvAttempted;
        tvCorrect = binding.tvCorrect;
        tvWrong = binding.tvWrong;
        tvAccuracy = binding.tvAccuracy;
        cardViewAttachment = binding.cardViewAttachment;
        answerKeyRecyclerView = binding.recylerTeacherAttachedItems;
        userAnswerRecycler = binding.recylerAttachedItems;
        teacherAttachmentsRecycler = binding.recyclerAttachments;
        answerKeyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAnswerRecycler.setLayoutManager(new LinearLayoutManager(this));
        teacherAttachmentsRecycler.setLayoutManager(new LinearLayoutManager(this));
        userAnswerRecycler.setNestedScrollingEnabled(false);
        answerKeyRecyclerView.setNestedScrollingEnabled(false);
        teacherAttachmentsRecycler.setNestedScrollingEnabled(false);
        rootLayout = binding.getRoot();
        answerKeyList = new ArrayList<>();
        userAnswerList = new ArrayList<>();
        teacherAttachmentsList = new ArrayList<>();
        PreferenceHandler.writeBoolean(this, PreferenceHandler.EXTERNAL_APP, false);
        if (isFrom.equalsIgnoreCase("test")) {
            cardViewMarksReport.setVisibility(View.VISIBLE);
            cardViewAssignment.setVisibility(View.GONE);
            binding.cardViewTest.setVisibility(View.VISIBLE);
            if (reportDataItem != null) {
                String submitted_time, deadline = "";
                if (reportDataItem.getTopic() != null) {
                    toolbarCustomTitle.setText(reportDataItem.getTopic());
                    binding.tvTestTopicName.setText(reportDataItem.getTopic());
                }
                if (reportDataItem.getSubmitDate() != null) {
                    deadline = reportDataItem.getSubmitDate();
                    binding.tvTestAssignDate.setText(Utilities.getFormattedDate(reportDataItem.getSubmitDate(), false) + ",");
                    binding.tvAssignDate.setText(Utilities.getOnlyDate(reportDataItem.getSubmitDate()));
                    binding.tvTestMonth.setText(Utilities.getOnlyMonth(reportDataItem.getSubmitDate()));
                }
                if (reportDataItem.getSubmitTime() != null) {
                    String submitTime = convertTimeTo24Format(reportDataItem.getSubmitTime());
                    deadline += " " + submitTime;
                    binding.tvTestAssignTime.setText(reportDataItem.getSubmitTime());
                }
                if (reportDataItem.getSubmit_date() != null) {
                    submitted_time = reportDataItem.getSubmit_date();
                    checkForLateSubmission(submitted_time, deadline);
                }
                if (reportDataItem.getTopic() != null) {
                    binding.tvTopicName.setText(reportDataItem.getTopic());
                }
                if (reportDataItem.getDuration() != null) {
                    binding.tvDuration.setText(getString(R.string.duration) + reportDataItem.getDuration());
                }

                if (reportDataItem.getTotalMarksObtained() != null) {
                    tvMarks.setText(reportDataItem.getTotalMarksObtained() + "/" + reportDataItem.getTotalMarks());
                    if (reportDataItem.getAttempted() != null)
                        tvAttempted.setText(reportDataItem.getAttempted() + "/" + reportDataItem.getTotalQuestion());
                    if (reportDataItem.getCorrect() != null)
                        tvCorrect.setText(reportDataItem.getCorrect() + "/" + reportDataItem.getTotalQuestion());
                    if (reportDataItem.getWrong() != null)
                        tvWrong.setText(reportDataItem.getWrong() + "/" + reportDataItem.getTotalQuestion());


                    Double value = (new Double(reportDataItem.getTotalMarksObtained()) / new Double(reportDataItem.getTotalMarks()));
                    double perc = value * 100;
                    String percent = String.format("%.2f", perc);
                    tvAccuracy.setText(percent + "%");
                } else {
                    cardViewMarksReport.setVisibility(View.GONE);
                }

                if (reportDataItem.getAnswer() != null) {
                    convertStringToJson(reportDataItem.getAnswer());
                } else {
                    cardViewAnswerKey.setVisibility(View.GONE);
                }
                if (reportDataItem.getAttachment() != null) {
                    convertAttachmentListToJson(reportDataItem.getAttachment());
                } else {
                    cardViewAttachment.setVisibility(View.GONE);
                }
                if (reportDataItem.getUser_answer() != null)
                    convertUserAnswerListToJson(reportDataItem.getUser_answer());
            }
        } else {
            cardViewAssignment.setVisibility(View.VISIBLE);
            cardViewMarksReport.setVisibility(View.GONE);
            binding.cardViewTest.setVisibility(View.GONE);
            String submitted_time, deadline = "";

            if (assignmentDataItem != null) {
                if (assignmentDataItem.getTopic() != null)
                    toolbarCustomTitle.setText(assignmentDataItem.getTopic());
                tvTopicName.setText(assignmentDataItem.getTopic());

                if (assignmentDataItem.getSubmitDate() != null) {
                    deadline = assignmentDataItem.getSubmitDate();
                    tvdate.setText(Utilities.getFormattedDate(assignmentDataItem.getSubmitDate(), false) + ",");
                }
                if (assignmentDataItem.getSubmitTime() != null) {
                    String submitTime = convertTimeTo24Format(assignmentDataItem.getSubmitTime());
                    deadline += " " + submitTime;
                    tvTime.setText(assignmentDataItem.getSubmitTime());
                }
                if (assignmentDataItem.getSubmit_date() != null) {
                    submitted_time = assignmentDataItem.getSubmit_date();
                    checkForLateSubmission(submitted_time, deadline);
                }
                if (assignmentDataItem.getAnswer() != null) {
                    convertStringToJson(assignmentDataItem.getAnswer());
                } else {
                    cardViewAnswerKey.setVisibility(View.GONE);
                }
                if (assignmentDataItem.getAttachment() != null) {
                    convertAttachmentListToJson(assignmentDataItem.getAttachment());
                } else {
                    cardViewAttachment.setVisibility(View.GONE);
                }
                if (assignmentDataItem.getUser_answer() != null)
                    convertUserAnswerListToJson(assignmentDataItem.getUser_answer());
            }
        }


    }

    private String convertTimeTo24Format(String submitTime) {
        String[] onlytime = submitTime.split(" ");
        String[] time = onlytime[0].split(":");
        int hour = Integer.parseInt(time[0]);
        if (onlytime[1].startsWith("A")) {
            if (hour != 12)
                return onlytime[0];
            else return "00" + ":" + time[1];
        } else {
            if (hour != 12) {
                int hour_24 = hour + 12;
                return hour_24 + ":" + time[1];
            } else return onlytime[0];
        }
    }

    private void checkForLateSubmission(String submitted_time, String deadline) {
        //  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm ");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            if (sdf2.parse(submitted_time).after(sdf2.parse(deadline))) {
                binding.submittedDate.setTextColor(ContextCompat.getColor(this, R.color.red));
                binding.txtLateSubmitted.setVisibility(View.VISIBLE);
            } else {
                binding.submittedDate.setTextColor(ContextCompat.getColor(this, R.color.gray));
                binding.txtLateSubmitted.setVisibility(View.GONE);

            }
            binding.layoutLateSubmission.setVisibility(View.VISIBLE);
            binding.submittedDate.setText(Utilities.formatCurrentTime(submitted_time, 3));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setUpAnswerKeyAdapter(ArrayList<String> attachmentList) {
        adapter = new SelectedAttachmentItemsAdapter(attachmentList, true, position -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            selectedAttachment = attachmentList.get(position);
            setupViewAttachmentClick(attachmentList.get(position));
        }, () -> {

        });
        answerKeyRecyclerView.setAdapter(adapter);


    }

    private void setUpTeacherAttachmentAdapter(ArrayList<String> teacherAttachmentList) {

        attachmentAdapter = new SelectedAttachmentItemsAdapter(teacherAttachmentList, true, position -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            Log.d("TAG", "setUpTeacherAttachmentAdapter: " + attachmentAdapter.getItemCount());
            mLastClickTime = SystemClock.elapsedRealtime();
            selectedAttachment = teacherAttachmentList.get(position);
            setupViewAttachmentClick(teacherAttachmentList.get(position));
        }, () -> {

        });
        teacherAttachmentsRecycler.setAdapter(attachmentAdapter);


    }

    private void setUpUserAnswerAdapter(ArrayList<String> attachmentList) {

        userAdapter = new SelectedAttachmentItemsAdapter(attachmentList, true, position -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            Log.d("TAG", "setUpUserAnswerAdapter: " + userAdapter.getItemCount());
            mLastClickTime = SystemClock.elapsedRealtime();
            selectedAttachment = attachmentList.get(position);
            setupViewAttachmentClick(attachmentList.get(position));
        }, () -> {

        });
        userAnswerRecycler.setAdapter(userAdapter);


    }

    private void convertStringToJson(String answerKey) {
        try {
            answerKeyList.clear();
            JSONArray jsonArray = new JSONArray(answerKey);
            for (int i = 0; i <= jsonArray.length(); i++) {
                String item = (String) jsonArray.get(i);
                answerKeyList.add(item.replace("attachment/", ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        setUpAnswerKeyAdapter(answerKeyList);
    }

    private void convertUserAnswerListToJson(String userAnswerKey) {
        try {
            userAnswerList.clear();
            JSONArray jsonArray = new JSONArray(userAnswerKey);
            for (int i = 0; i < jsonArray.length(); i++) {
                String item = (String) jsonArray.get(i);
                userAnswerList.add(item.replace("attachment/", ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setUpUserAnswerAdapter(userAnswerList);
    }

    private void convertAttachmentListToJson(String userAnswerKey) {
        try {
            teacherAttachmentsList.clear();
            JSONArray jsonArray = new JSONArray(userAnswerKey);
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    String item = (String) jsonArray.get(i);
                    teacherAttachmentsList.add(item.replace("attachment/", ""));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setUpTeacherAttachmentAdapter(teacherAttachmentsList);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {


            default:
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (Utilities.verifyPermissions(grantResults)) {
                if (selectedAttachment != null)
                    setupViewAttachmentClick(selectedAttachment);
            }
        }
    }
    private boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("Return", "hasPermissions: ");
                    if (ActivityCompat.shouldShowRequestPermissionRationale(ReportListDetailActivity.this, permission)){
                    } else {
                        ActivityCompat.requestPermissions(ReportListDetailActivity.this,
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
    private void setupViewAttachmentClick(String spinnerSelectedAttachment) {
        if (!hasPermissions(ReportListDetailActivity.this, permissions())) {
            ActivityCompat.requestPermissions(ReportListDetailActivity.this,
                    permissions(),
                    PERMISSION_ALL_NEW);
            return;
        } else {
            File myFilesDir = null;
            File myFile = null;
            File file = new File(spinnerSelectedAttachment);
            String strFileName = file.getName();

            /*File myFilesDir = new File(Environment
                    .getExternalStorageDirectory().getAbsolutePath()
                    + "/Download/"+ getString(R.string.app_name).replaceAll(" ", "") + "Files");
            File myFile = new File(myFilesDir, spinnerSelectedAttachment);*/
            String lowerpath = spinnerSelectedAttachment.toLowerCase();
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
                String fileName = spinnerSelectedAttachment;
                String dirPath = Utilities.getRootDirPath(ReportListDetailActivity.this);
                File downloadedFile = new File(dirPath, fileName);
                Intent in = new Intent(ReportListDetailActivity.this, PDFViewNew.class);
                in.putExtra(PreferenceHandler.PDF_FILE_CACHE, fileName);
                if (downloadedFile.exists()) {
                    in.putExtra(PreferenceHandler.DOC_URL,downloadedFile.getAbsolutePath());
                    in.putExtra(PreferenceHandler.FROM_LOCAL, "true");
                } else {
                    String docUrl = "https://web.smartowls.in/attachment/" + spinnerSelectedAttachment;
                    in.putExtra(PreferenceHandler.DOC_URL,docUrl);
                    in.putExtra(PreferenceHandler.FROM_LOCAL, "false");
                }
                in.putExtra(PreferenceHandler.FILE_NAME,spinnerSelectedAttachment);
                startActivity(in);
                /*String docUrl = "https://web.smartowls.in/attachment/" + spinnerSelectedAttachment;

                Intent in = new Intent(ReportListDetailActivity.this, PDFViewNew.class);
                in.putExtra(PreferenceHandler.DOC_URL,docUrl);
                in.putExtra(PreferenceHandler.FILE_NAME,spinnerSelectedAttachment);
                startActivity(in);*/
            } else if (lowerpath.contains(".png") || lowerpath.contains(".jpg") || lowerpath.contains(".jpeg") || lowerpath.contains(".gif")) {

                Intent intent = new Intent(this, ViewAttachmentActivity.class);
                intent.putExtra(PreferenceHandler.PDF_NAME, spinnerSelectedAttachment);
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
                                            Log.e("ExternalStorage", "Scanned " + path + ":");
                                            Log.e("ExternalStorage", "-> uri=" + uri);
                                            try {
                                                openFile(ReportListDetailActivity.this, new File(path));
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                            // only for gingerbread and newer versions
                        } else {
                            openFile(ReportListDetailActivity.this, myFile);
//                            openFile(this, myFile, Uri.fromFile(myFile));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    /*try {

                        openFile(ReportListDetailActivity.this, myFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                } else {

                    downloadAttachments(spinnerSelectedAttachment, true, false);
                }
            }

        }
    }

    private void downloadAttachments(String attachmentToDownload, boolean isDoc, boolean isPdf) {
        isPdfSelected = isPdf;
        isDocSelected = isDoc;

        DownloadAttachmentFile downloadFile = new DownloadAttachmentFile(ReportListDetailActivity.this, attachmentToDownload, isDoc);
        downloadFile.execute();

    }


    private class DownloadAttachmentFile extends AsyncTask<Void, Void, String> {
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

/*            if (file.exists()) {
                file.delete();
            }*/

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
            mProgressDialog.dismiss();

            if (android.os.Build.VERSION.SDK_INT >= PreferenceHandler.MEDIA_STORE_VERSION) {
                MediaScannerConnection.scanFile(ReportListDetailActivity.this,
                        new String[]{result}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                                Log.e("ExternalStorage", "Scanned " + path + ":");
                                Log.e("ExternalStorage", "-> uri=" + uri);
//                                    openFile(context, file, uri);
                                File fileeee = new File(path);
                                if (isDocSelected && fileeee != null) {
                                    openDownloadedFile(context, fileeee, uri);
                                }
                            }
                        });

            } else {
                if (result != null && result.equalsIgnoreCase("true")) {
                    if (isDocSelected && file != null) {
                        try {
                            openFile(ReportListDetailActivity.this, file);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            super.onPostExecute(result);
        }
    }

    private void openDownloadedFile(Context context, File url, Uri uriFrom) {
        // Create URI
        File file = url;
        Uri uri;
        if (android.os.Build.VERSION.SDK_INT >= PreferenceHandler.MEDIA_STORE_VERSION) {
            uri = uriFrom;
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
            Toast.makeText(ReportListDetailActivity.this, getString(R.string.device_doesnot_support_file), Toast.LENGTH_SHORT).show();

        }
        PreferenceHandler.writeBoolean(this, PreferenceHandler.EXTERNAL_APP, true);
        PreferenceHandler.writeString(this, PreferenceHandler.EXTERNAL_DOC, url.getAbsolutePath());

    }

    private void openFile(Context context, File url) throws IOException {
        // Create URI
        File file = url;
        //Uri uri;
        Uri uri = Uri.fromFile(file);
   /*     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            uri = FileProvider.getUriForFile(this, "com.smartowls.smartowls.provider", file);
        } else {
            uri = Uri.fromFile(file);
        }*/


        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        // Intent intent = new Intent(Intent.ACTION_VIEW);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(file.getPath()));
        intent.setDataAndType(uri, mimeType);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK /*| Intent.FLAG_GRANT_READ_URI_PERMISSION *//*| Intent.FLAG_ACTIVITY_CLEAR_TOP*/);
        PackageManager packageManager = getPackageManager();
       /* List<ResolveInfo> activities = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);*/
        List resolvedActivityList;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            resolvedActivityList =
                    packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        } else {
            resolvedActivityList = packageManager.queryIntentActivities(intent, 0);

        }
        boolean isIntentSafe = resolvedActivityList.size() > 0;
        intent = Intent.createChooser(intent, "Open file with");
        if (isIntentSafe) {
            startActivityForResult(intent, DOC_FILE_VIEW);
//            startActivity(intent);
        } else {
            Toast.makeText(context, "Your device does not support this file", Toast.LENGTH_SHORT).show();
        }
        PreferenceHandler.writeBoolean(this, PreferenceHandler.EXTERNAL_APP, true);
        PreferenceHandler.writeString(this, PreferenceHandler.EXTERNAL_DOC, url.getAbsolutePath());

        /*if (url.exists()) {
            url.delete();
            Log.d("TAG", "openFile: Deleted" + url.getAbsolutePath());
        }*/

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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PDF_VIEW_REQUEST) {
            if (data != null) {
                String doc_name = data.getStringExtra(PreferenceHandler.PDF_NAME);
                Log.d("TAG", "onActivityResult: " + doc_name);
                if (Build.VERSION.SDK_INT >= PreferenceHandler.MEDIA_STORE_VERSION) {
                    String filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                    File myFile = new File(filepath, doc_name);
                    Log.d("TAG", "onActivityResult: " + myFile.getAbsolutePath());
                    if (!PreferenceHandler.readBoolean(this, PreferenceHandler.FILE_DOWNLOADED, false)) {
                        myFile.delete();
                    }
                } else {
                    String pdf_name = data.getStringExtra(PreferenceHandler.PDF_NAME);
                    File myFilesDir = new File(Environment
                            .getExternalStorageDirectory().getAbsolutePath()
                    );
                    File myFile = new File(myFilesDir, pdf_name);

                    if (!PreferenceHandler.readBoolean(this, PreferenceHandler.FILE_DOWNLOADED, false)) {
                        myFile.delete();
                    }
                }

                   /* if (myFile.delete())
                        Utilities.makeToast(this, "File Deleted");*/


            }
        } else if (requestCode == DOC_FILE_VIEW) {
            if (data != null) {
                try {
                    String doc_name = data.getStringExtra(PreferenceHandler.PDF_NAME);
                    Log.d("TAG", "onActivityResult: " + doc_name);
                    if (Build.VERSION.SDK_INT >= PreferenceHandler.MEDIA_STORE_VERSION) {
                        String filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                        File myFile = new File(filepath, doc_name);
                        Log.d("TAG", "onActivityResult: " + myFile.getAbsolutePath());
                        myFile.delete();
                    } else {
                        File myFilesDir = new File(Environment
                                .getExternalStorageDirectory().getAbsolutePath()
                        );
                        File myFile = new File(myFilesDir, doc_name);
                        if (!PreferenceHandler.readBoolean(this, PreferenceHandler.FILE_DOWNLOADED, false)) {
                            myFile.delete();
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("TAG", "onRestart: ");
        if (PreferenceHandler.readBoolean(this, PreferenceHandler.EXTERNAL_APP, false)) {
            File filll = new File(PreferenceHandler.readString(this, PreferenceHandler.EXTERNAL_DOC, ""));
            if (filll.exists()) {
                if (!PreferenceHandler.readBoolean(this, PreferenceHandler.FILE_DOWNLOADED, false)) {
                    filll.delete();
                    PreferenceHandler.writeBoolean(this, PreferenceHandler.EXTERNAL_APP, false);
                    Log.d("TAG", "openFile: Deleted" + filll.getAbsolutePath());
                }
            } else {
                Log.d("TAG", "onRestart: Not Exist");
            }
        }
    }
}
