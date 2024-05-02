package com.so.luotk.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import com.so.luotk.R;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;

public class VisitWebsiteActivity extends AppCompatActivity {
    private WebView mWebView;
    private Toolbar toolbar;
    private ProgressView mProgressDialog;
    private String docxUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.restrictScreenShot(this);
// getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        Utilities.setUpStatusBar(this); Utilities.setLocale(this);
        setContentView(R.layout.activity_visit_website);
        docxUrl = getIntent().getStringExtra(PreferenceHandler.DOC_URL);
        setupUI();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupUI() {
//        String websiteLink="<iframe src='http://view.officeapps.live.com/op/view.aspx?src=https://web.smartowls.in/"+docxUrl+"' width='100%' height='100%' style='border: none;'></iframe>";
        String websiteLink="https://view.officeapps.live.com/op/view.aspx?src=https://web.smartowls.in/" + docxUrl;
//        String websiteLink = PreferenceHandler.readString(this, PreferenceHandler.ORG_WEBSITE_LINK, null);
//        websiteLink = "https://www.youtube.com/channel/UC4tyuCK5wdoeVg7erQ7aZuw";
        Log.d("TAG", "setupUI: " + websiteLink);
        if (websiteLink != null) {
            mWebView = findViewById(R.id.webview);
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.getSettings().setLoadWithOverviewMode(true);
            mWebView.getSettings().setUseWideViewPort(true);
            mWebView.setWebViewClient(new WebViewClient());
            mWebView.loadUrl(websiteLink);
            /*mWebView.loadData(websiteLink, "text/html",
                    "utf-8");*/
            mProgressDialog = new ProgressView(VisitWebsiteActivity.this);
            try {
                mProgressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }

            mWebView.setWebViewClient(new WebViewClient() {

                public void onPageFinished(WebView view, String url) {
                    if (!isFinishing())
                        mProgressDialog.dismiss();
                }
            });

        }

    }

    @Override
    public void onBackPressed() {
        /* super.onBackPressed();*/
        if (!isFinishing())
            finish();
    }
}
