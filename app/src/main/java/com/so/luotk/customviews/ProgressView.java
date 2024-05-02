package com.so.luotk.customviews;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.so.luotk.R;


public class ProgressView {
    private TextView tvLoadingTitle;
    private final AlertDialog dialog;
    private Context context;
    private final Activity activity;

    public ProgressView(Context context) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        activity = (Activity) context;
        View progressView = LayoutInflater.from(context).inflate(R.layout.progress_indicator, null, false);
        // tvLoadingTitle = progressView.findViewById(R.id.tv_loading_title);
        dialogBuilder.setView(progressView);
        dialogBuilder.setCancelable(false);
        /*dialogBuilder.window!!.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT))*/
        dialog = dialogBuilder.create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    public void show() {
        if (!activity.isFinishing())
            dialog.show();
    }

    public void dismiss() {
        if (!activity.isFinishing())
            dialog.dismiss();
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }
/*
    public String setLoaingTitle(String loadingTitle) {
        tvLoadingTitle.setText(loadingTitle);
        return loadingTitle;
    }*/

}
