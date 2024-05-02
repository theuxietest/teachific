package com.so.luotk.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.shockwave.pdfium.PdfDocument;
import com.so.luotk.databinding.ActivityTermsOfUseBinding;
import com.so.luotk.utils.Utilities;

import java.util.List;

public class TermsOfUseActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener,
        OnPageErrorListener {
    ActivityTermsOfUseBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTermsOfUseBinding.inflate(getLayoutInflater());
        Utilities.setUpStatusBar(this);
        setContentView(binding.getRoot());
        Utilities.hideKeyBoard(this);
        binding.toolbar.setNavigationIcon(Utilities.setNavigationIconColorBlack(this));
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        displayFromAsset("terms_and_conditions.pdf");
    }


    private void displayFromAsset(String assetFileName) {
        binding.pdfView.fromAsset(assetFileName)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10) // in dp
                .onPageError(this)
                .load();
    }

    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = binding.pdfView.getDocumentMeta();
        Log.e("TAG", "title = " + meta.getTitle());
        Log.e("TAG", "author = " + meta.getAuthor());
        Log.e("TAG", "subject = " + meta.getSubject());
        Log.e("TAG", "keywords = " + meta.getKeywords());
        Log.e("TAG", "creator = " + meta.getCreator());
        Log.e("TAG", "producer = " + meta.getProducer());
        Log.e("TAG", "creationDate = " + meta.getCreationDate());
        Log.e("TAG", "modDate = " + meta.getModDate());

        printBookmarksTree(binding.pdfView.getTableOfContents(), "-");
    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e("TAG", String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }
    @Override
    public void onPageChanged(int page, int pageCount) {
        Log.d("TAG", "onPageChanged: " + page);
    }

    @Override
    public void onPageError(int page, Throwable t) {
        Log.e("TAG", "Cannot load page " + page);
    }
}