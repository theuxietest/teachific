package com.so.luotk.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;
import com.so.luotk.R;


import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class ViewAttachmentActivity extends AppCompatActivity {

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
    private String attachmentName;
    private Toolbar toolbar;
    private ImageView imgAttachment;
    private ScaleGestureDetector mScaleGestureDetector;
    private final float mScaleFactor = 1.0f;
    private ProgressDialog progressDialog;
    private WebView urlWebView;
    private View layoutImage;
    private String isFrom;
    private ProgressBar progressBar;
    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.restrictScreenShot(this);
// getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        Utilities.setUpStatusBar(this); Utilities.setLocale(this);
        setContentView(R.layout.activity_view_p_d_f);
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
        //  mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        if (getIntent() != null) {
            attachmentName = getIntent().getStringExtra(PreferenceHandler.PDF_NAME);
            isFrom = getIntent().getStringExtra("isFrom");
        }
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(upArrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }


    private void setupUI() {
        imgAttachment = findViewById(R.id.img_attachment);
        urlWebView = (WebView) findViewById(R.id.web_view);
        layoutImage = findViewById(R.id.layout_image);
        progressBar = findViewById(R.id.progress_bar);
        // mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        if (!TextUtils.isEmpty(attachmentName)) {
            if (!TextUtils.isEmpty(isFrom) && isFrom.equalsIgnoreCase("studymaterial")) {
                toolbar.setTitle(attachmentName);
                String lowerpath = attachmentName.toLowerCase();
                if (lowerpath.contains("png") || lowerpath.contains("jpg") || lowerpath.contains("jpeg") || lowerpath.contains("gif")) {
                    layoutImage.setVisibility(View.VISIBLE);
                    urlWebView.setVisibility(View.GONE);
//                    Picasso.get().load("https://web.smartowls.in/attachment/" + attachmentName).fit().into(imgAttachment);
                    //Glide.with(getApplicationContext()).load("https://web.smartowls.in/" + attachmentName).into(imgAttachment);
                    Glide.with(getApplicationContext())
                            .load(Uri.parse("https://web.smartowls.in/" + attachmentName))
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
                            .into(imgAttachment);

                } else {
                    setupWebView();
                }

            } else if (!TextUtils.isEmpty(isFrom) && isFrom.equalsIgnoreCase("adminAttachment")) {
                try {

                    layoutImage.setVisibility(View.VISIBLE);
                    urlWebView.setVisibility(View.GONE);

                    if (getIntent().getStringExtra(PreferenceHandler.FILE_NAME) != null) {
                        toolbar.setTitle(getIntent().getStringExtra(PreferenceHandler.FILE_NAME));
                    } else {
                        toolbar.setTitle(attachmentName);
                    }
                    viewImageFromPath(attachmentName);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                Log.d("TAG", "setupUI: " + "https://web.smartowls.in/attachment/" + attachmentName);

                if (attachmentName.contains("attachment/")) {
                    attachmentName = attachmentName.replaceAll("attachment/", "");
                }
                toolbar.setTitle(attachmentName);
                String lowerpath = attachmentName.replaceAll("\"", "").toLowerCase();
                if (lowerpath.contains("png") || lowerpath.contains("jpg") || lowerpath.contains("jpeg") || lowerpath.contains("gif")) {
                    layoutImage.setVisibility(View.VISIBLE);
                    urlWebView.setVisibility(View.GONE);
                    //  Picasso.get().load("https://web.smartowls.in/attachment/" + attachmentName).fit().into(imgAttachment);
                    //  Glide.with(getApplicationContext()).load("https://web.smartowls.in/attachment/" + attachmentName).into(imgAttachment);
                    Glide.with(getApplicationContext())
                            .load("https://web.smartowls.in/attachment/" + attachmentName)
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
                            .into(imgAttachment);

                } else {
                    setupWebView();
                }
            }


        }
    }

    /**
     * callback method for permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
        }
    }


    public void viewImageFromPath(String path) throws IOException {
        String state = Environment.getExternalStorageState();
        Bitmap myBitmap = null;
        File imgFile = new File(path);
        Log.d("TAG", "viewImageFromPath: " + imgFile);
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkPermission()) {
                    if (imgFile.exists()) {
                        Glide.with(this)
                                .load(imgFile)
                                .into(imgAttachment);
                    }
                } else {
                    requestPermission();
                }
            }
        }

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

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES);
            return result == PackageManager.PERMISSION_GRANTED;
        } else {
            int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_REQUEST_CODE);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


/*    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f,
                    Math.min(mScaleFactor, 10.0f));
            imgAttachment.setScaleX(mScaleFactor);
            imgAttachment.setScaleY(mScaleFactor);
            return true;
        }
    }*/

  /*  public boolean onTouchEvent(MotionEvent motionEvent) {
        mScaleGestureDetector.onTouchEvent(motionEvent);
        return true;
    }*/


    private void setupWebView() {
        layoutImage.setVisibility(View.GONE);
        urlWebView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        urlWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        WebSettings settings = urlWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setDomStorageEnabled(true);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setLoadWithOverviewMode(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setDatabaseEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        urlWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        urlWebView.setScrollbarFadingEnabled(true);
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading...");
        }

        // String docUrl = "http://docs.google.com/gview?embedded=true&url=";
        urlWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url.startsWith("http") || url.startsWith("https")) {
                    return false;
                }
                if (url.startsWith("intent")) {
                    try {
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);

                        String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                        if (fallbackUrl != null) {
                            view.loadUrl(fallbackUrl);
                            return true;
                        }
                    } catch (URISyntaxException e) {
                        //not an intent uri

                    }
                }
                return true;//do nothing in other cases
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                if (!isFinishing() && progressDialog != null) {
                    progressDialog.dismiss();
                }
                super.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (!isFinishing()) {
                    progressDialog.show();
                }

            }
        });
        urlWebView.loadUrl(/*docUrl + *//*"https://web.smartowls.in/attachment/" + */attachmentName);
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }
}
