package com.so.luotk.customviews;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.so.luotk.R;
import com.so.luotk.utils.PreferenceHandler;

import org.jetbrains.annotations.NotNull;

public class BottomSheetDialogCommon extends BottomSheetDialogFragment {
    String message, messageBody;
    Context context;
    public BottomSheetDialogCommon(Context context, String s, String messageBody) {
        this.message = s;
        this.context = context;
        this.messageBody = messageBody;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.invite_bottom_layout, container, false);
        TextView message_body = v.findViewById(R.id.message_body);
        Button whatsapp_share_button = v.findViewById(R.id.whatsapp_share_button);
        Button share_button = v.findViewById(R.id.share_button);
        ImageButton cancel_button = v.findViewById(R.id.cancel_button);
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        whatsapp_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appInstalledOrNot("com.whatsapp")) {
                    sendToWhatsapp(messageBody);
                    dismiss();
                } else {
                    Toast.makeText(context, context.getString(R.string.install_whatsapp), Toast.LENGTH_SHORT).show();
                }
            }
        });
        share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareApp(messageBody);
                dismiss();
            }
        });
        message_body.setText(Html.fromHtml(message));
        return v;
    }

    private void sendToWhatsapp(String messageBody) {
//        String smsNumber = "7****"; // E164 format without '+' sign
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, messageBody);
//        sendIntent.putExtra("jid", smsNumber + "@s.whatsapp.net"); //phone number without "+" prefix
        sendIntent.setPackage("com.whatsapp");
        /*if (sendIntent.resolveActivity(getActivity().getPackageManager()) == null) {
            Toast.makeText(this, "Error/n" + e.toString(), Toast.LENGTH_SHORT).show();
            return;
        }*/
        startActivity(sendIntent);
    }

    private boolean appInstalledOrNot(String uri)
    {
        PackageManager pm = context.getPackageManager();
        boolean app_installed = false;
        try
        {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            app_installed = false;
        }
        return app_installed ;
    }

    private void shareApp(String messageBody) {
        String shareMessage = PreferenceHandler.readString(context, PreferenceHandler.SHARE_MESSAGE, null);
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
//            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Hi!");
//            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            shareMessage = messageBody;
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
