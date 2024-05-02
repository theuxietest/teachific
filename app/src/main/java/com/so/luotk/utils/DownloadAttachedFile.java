package com.so.luotk.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.so.luotk.R;
import com.so.luotk.customviews.ProgressView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadAttachedFile extends AsyncTask<Void, Void, String> {
    private final ProgressView mProgressDialog;
    private final Context context;
    private final String filename;
    private File file;
    private String downloadResult;
    private final String type;
    private final String isFrom;

    public DownloadAttachedFile(Context context, String fileName, String type, String isFrom) {
        this.context = context;
        this.filename = fileName;
        this.type = type;
        this.isFrom = isFrom;
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
        HttpURLConnection c;

        try {

            URL url = new URL("https://web.smartowls.in/studymaterial/" + filename);
            c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();
        } catch (IOException e1) {
            return e1.getMessage();
        }

        File myFilesDir = new File(Environment
                .getExternalStorageDirectory().getAbsolutePath()
                + "/Download/"+ context.getString(R.string.app_name).replaceAll(" ", "") + "Files");

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
        return "true";
        /*HttpURLConnection c;
        URL url;
        try {
            if (isFrom.equalsIgnoreCase("studymaterial")) {
                url = new URL("https://web.smartowls.in/" + filename);
            } else {
                url = new URL("https://web.smartowls.in/attachment/" + filename);
            }

            c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();
        } catch (IOException e1) {
            return e1.getMessage();
        }

        File myFilesDir = new File(Environment
                .getExternalStorageDirectory().toString()
                + "/Download");

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
        return "true";*/
    }


    @Override
    protected void onPostExecute(String result) {
        downloadResult = result;
        mProgressDialog.dismiss();
        if (result != null && result.equalsIgnoreCase("true")) {
            if (type.equalsIgnoreCase("doc") && file != null) {
                try {
//                    Utilities.openAttachedFile(context, file);
                    Utilities.openFile(context,file);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        super.onPostExecute(result);
    }


}
