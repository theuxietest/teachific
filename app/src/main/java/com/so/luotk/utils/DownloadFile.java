package com.so.luotk.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.TextView;
import android.widget.Toast;

import com.so.luotk.R;
import com.so.luotk.customviews.ProgressView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadFile extends AsyncTask<Void, Void, String> {
    private final ProgressView mProgressDialog;
    private final Context context;
    private final String filename;
    private boolean isDataDownloaded;
    private final TextView textView;
    private File file;

    public DownloadFile(Context context, String fileName, TextView textView) {
        this.context = context;
        this.filename = fileName;
        this.textView = textView;
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
                + "/Download/"+ context.getString(R.string.app_name).replaceAll(" ", "") +"Files");

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
    }


    @Override
    protected void onPostExecute(String result) {
        mProgressDialog.dismiss();
        if (result.equalsIgnoreCase("true")) {
            Toast.makeText(context, "Data has been downloaded successfully", Toast.LENGTH_LONG).show();
            if (textView != null) {
                textView.setText("View");
                //isDataDownloaded = true;
            }
           /* try {
                openFile(context, file);
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        }
        super.onPostExecute(result);
    }

    public boolean isDownloadCompleted() {
        return isDataDownloaded;
    }

    public static void openFile(Context context, File url) throws IOException {
        // Create URI
        File file = url;
        Uri uri = Uri.fromFile(file);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        // Check what kind of file you are trying to open, by comparing the url with extensions.
        // When the if condition is matched, plugin sets the correct intent (mime) type,
        // so Android knew what application to use to open the file
        if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if (url.toString().contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if (url.toString().contains(".zip") || url.toString().contains(".rar")) {
            // WAV audio file
            intent.setDataAndType(uri, "application/x-wav");
        } else if (url.toString().contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf");
        } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav");
        } else if (url.toString().contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        } else if (url.toString().contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
            // Video files
            intent.setDataAndType(uri, "video/*");
        } else {
            //if you want you can also define the intent type for any other file

            //additionally use else clause below, to manage other unknown extensions
            //in this case, Android will show all applications installed on the device
            //so you can choose which application to use
            intent.setDataAndType(uri, "*/*");
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        context.startActivity(intent);
    }
}
