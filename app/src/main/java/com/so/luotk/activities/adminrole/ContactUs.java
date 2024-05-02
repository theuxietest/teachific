package com.so.luotk.activities.adminrole;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.so.luotk.R;
import com.so.luotk.databinding.ActivityContactUsBinding;
import com.so.luotk.utils.Utilities;

public class ContactUs extends AppCompatActivity {

    private final String whatsAppNo = "9814698149";
    ActivityContactUsBinding binding;
    private static final int REQUEST_PHONE_CALL = 1;
    private Toolbar toolbar;
    private TextView toolbarCustomTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.restrictScreenShot(this);
        binding = ActivityContactUsBinding.inflate(getLayoutInflater());
        overridePendingTransition(0, 0);
        Utilities.setUpStatusBar(this);
        Utilities.setLocale(this);
        setContentView(binding.getRoot());
        setToolbar();
        binding.chatOnWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWhatsAppIntent();
            }
        });

        binding.contactUsWhatsappLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.chatOnWhatsapp.performClick();
            }
        });
        binding.phoneNumberText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionGranted();
            }
        });
        binding.phoneImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.phoneNumberText.performClick();
            }
        });
        binding.emailUs.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Drawable drawable = binding.emailUs.getCompoundDrawables()[2];
                if (drawable == null) {
                    return false;
                }
                //drawleft is less than, drawright is greater than
                //The left, right, up and down correspond to 0 1 2 3
                if (event.getX() > binding.emailUs.getWidth() - binding.emailUs.getCompoundDrawables()[2].getBounds().width()) {
                    setClipboard(ContactUs.this, binding.emailUs.getText().toString());
                    Snackbar snackbar = Snackbar
                            .make(binding.mainLayout, "Email copied", Snackbar.LENGTH_LONG);
                    snackbar.show();
//                    Toast.makeText(ContactUs.this, "Email Code copied.", Toast.LENGTH_SHORT).show();

                    return false;
                }
                return false;
            }
        });

        binding.emailUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setClipboard(ContactUs.this, binding.emailUs.getText().toString());
                Snackbar snackbar = Snackbar
                        .make(binding.mainLayout, "Email copied", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });

        binding.mailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.emailUs.performClick();
            }
        });
    }


    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbarCustomTitle = findViewById(R.id.tv_toolbar_title);
        setSupportActionBar(toolbar);
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.SRC_ATOP);
        this.getSupportActionBar().setHomeAsUpIndicator(upArrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    private void setClipboard(Context context, String text) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }

    private void openWhatsAppIntent() {
        String url = "https://api.whatsapp.com/send?phone=" + "91" + " " + whatsAppNo;
        try {
            PackageManager pm = getPackageManager();
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(ContactUs.this, "Please install whatsapp to use this feature.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(ContactUs.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);

            } else {
                callIntent();
            }
        } else {
            callIntent();

        }
    }

    public void callIntent() {
        try {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + whatsAppNo));
            startActivity(callIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void openGmail() {
        try{
            Intent intent = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:" + getString(R.string.contact_email)));
            intent.putExtra(Intent.EXTRA_SUBJECT, "your_subject");
            intent.putExtra(Intent.EXTRA_TEXT, "your_text");
            startActivity(intent);
        }catch(ActivityNotFoundException e){
            //TODO smth
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PHONE_CALL) {
            callIntent();
        }
    }
}