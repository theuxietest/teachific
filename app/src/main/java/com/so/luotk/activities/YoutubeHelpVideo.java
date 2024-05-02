package com.so.luotk.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.so.luotk.R;
import com.so.luotk.adapter.adminrole.YouTubeHelpVideoAdapter;
import com.so.luotk.databinding.ActivityYoutubeHelpVideoBinding;
import com.so.luotk.models.newmodels.YoutubeVideoModel;
import com.so.luotk.utils.Utilities;

import java.util.ArrayList;

public class YoutubeHelpVideo extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView toolbarCustomTitle;
    ActivityYoutubeHelpVideoBinding binding;
    ArrayList<YoutubeVideoModel> youtubeVideoModels = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.restrictScreenShot(this);
        binding = ActivityYoutubeHelpVideoBinding.inflate(getLayoutInflater());
        overridePendingTransition(0, 0);
        Utilities.setUpStatusBar(this);
        Utilities.setLocale(this);
        setContentView(binding.getRoot());
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

        binding.recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(mLayoutManager);
        YoutubeVideoModel youtubeVideoModel = new YoutubeVideoModel("Bs7LAFVIzB0", "Login To Admin Portal and Create a Batch", "https://youtu.be/Bs7LAFVIzB0");
        youtubeVideoModels.add(youtubeVideoModel);

        youtubeVideoModel = new YoutubeVideoModel("RhakZNdSj74", "Conduct assignments and Automatic Attendance", "https://youtu.be/RhakZNdSj74");
        youtubeVideoModels.add(youtubeVideoModel);

        youtubeVideoModel = new YoutubeVideoModel("Otiax3RCLlA", "How to add students, Faculty and make Announcement?", "https://youtu.be/Otiax3RCLlA");
        youtubeVideoModels.add(youtubeVideoModel);

        youtubeVideoModel = new YoutubeVideoModel("ByXWHSPR_r0", "How to share recorded videos and study material?", "https://youtu.be/ByXWHSPR_r0");
        youtubeVideoModels.add(youtubeVideoModel);

        youtubeVideoModel = new YoutubeVideoModel("SZjL4lyDS24", "How to create subjective test?", "https://youtu.be/SZjL4lyDS24");
        youtubeVideoModels.add(youtubeVideoModel);

        youtubeVideoModel = new YoutubeVideoModel("cr-KYOqoIFA", "How to create objective test?", "https://youtu.be/cr-KYOqoIFA");
        youtubeVideoModels.add(youtubeVideoModel);

        youtubeVideoModel = new YoutubeVideoModel("LqwGDHDny2w", "How to take Live Classes?", "https://www.youtube.com/watch?v=LqwGDHDny2w");
        youtubeVideoModels.add(youtubeVideoModel);

        youtubeVideoModel = new YoutubeVideoModel("PmcrhyyQIlE", "How to Manage Enquires? &How to Upload your Content in Store Section?", "https://www.youtube.com/watch?v=PmcrhyyQIlE");
        youtubeVideoModels.add(youtubeVideoModel);

        youtubeVideoModel = new YoutubeVideoModel("d-5w4TQQJcA", "How to design the Homepage of your App?", "https://www.youtube.com/watch?v=d-5w4TQQJcA");
        youtubeVideoModels.add(youtubeVideoModel);

        youtubeVideoModel = new YoutubeVideoModel("z8WL3RIUf2s", "Everything about Student Panel.", "https://www.youtube.com/watch?v=z8WL3RIUf2s");
        youtubeVideoModels.add(youtubeVideoModel);


        YouTubeHelpVideoAdapter recyclerViewAdapter = new YouTubeHelpVideoAdapter(YoutubeHelpVideo.this, youtubeVideoModels);
        binding.recyclerView.setAdapter(recyclerViewAdapter);
    }
}