package com.so.luotk.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.github.barteksc.pdfviewer.PDFView;
import com.so.luotk.R;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;

import java.io.File;

public class PDFViewNew extends AppCompatActivity {
    PDFView pdfView;
    ProgressBar progressBar;
    private static String dirPath;
    private String PDFUrl = "";
    String fileName = "", cache_file_name = "";
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.setUpStatusBar(this);
        Utilities.setLocale(this);
        setContentView(R.layout.activity_pdf_view_new);
        Utilities.restrictScreenShot(this);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        progressBar = findViewById(R.id.progressBar);
        pdfView = findViewById(R.id.pdfView);

        dirPath = Utilities.getRootDirPath(PDFViewNew.this);

        PDFUrl = getIntent().getStringExtra(PreferenceHandler.DOC_URL);
        fileName = getIntent().getStringExtra(PreferenceHandler.FILE_NAME);
        cache_file_name = getIntent().getStringExtra(PreferenceHandler.PDF_FILE_CACHE);
        PRDownloader.initialize(PDFViewNew.this);

        if (getIntent().getStringExtra(PreferenceHandler.FROM_LOCAL) != null) {
            if (getIntent().getStringExtra(PreferenceHandler.FROM_LOCAL).equals("true")) {
                progressBar.setVisibility(View.GONE);
                showPdfFromFile(new File(getIntent().getStringExtra(PreferenceHandler.DOC_URL)));
            } else {
                downloadPdfFromInternet(PDFUrl, dirPath, fileName, cache_file_name);
            }
        } else {
            downloadPdfFromInternet(PDFUrl, dirPath, fileName, cache_file_name);
        }

        setUpToolbar();
    }
    private void setUpToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(fileName);
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
    private void downloadPdfFromInternet(String url, final String dirPath, final String fileName, final String cache_file_name) {

        if (cache_file_name != null && !cache_file_name.equals("")) {
            PRDownloader.download(url, dirPath, cache_file_name).build().start(new OnDownloadListener() {
                @Override
                public void onDownloadComplete() {
                    File downloadedFile = new File(dirPath, cache_file_name);
                    progressBar.setVisibility(View.GONE);
                    showPdfFromFile(downloadedFile);
                }

                @Override
                public void onError(Error error) {
                    Toast.makeText(PDFViewNew.this,
                            "Error in downloading file : $error",
                            Toast.LENGTH_LONG).show();
                }
            });

            PRDownloader.download(
                    url,
                    dirPath,
                    fileName
            ).build();
        } else {
            PRDownloader.download(url, dirPath, cache_file_name).build().start(new OnDownloadListener() {
                @Override
                public void onDownloadComplete() {
                    File downloadedFile = new File(dirPath, cache_file_name);
                    progressBar.setVisibility(View.GONE);
                    showPdfFromFile(downloadedFile);
                }

                @Override
                public void onError(Error error) {
                    Toast.makeText(PDFViewNew.this,
                            "Error in downloading file : $error",
                            Toast.LENGTH_LONG).show();
                }
            });
            PRDownloader.download(
                    url,
                    dirPath,
                    fileName
            ).build();
        }

    }

    private void showPdfFromFile(File file) {
        pdfView.fromFile(file)
                .password(null)
                .defaultPage(0)
                .enableAntialiasing(true)
                .enableAnnotationRendering(true)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .load();
    }

    @Override
    public void onBackPressed() {

        finish();
    }
}