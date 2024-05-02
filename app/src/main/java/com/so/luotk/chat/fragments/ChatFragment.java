package com.so.luotk.chat.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.so.luotk.activities.adminrole.AdminBatchDetailActivity;
import com.so.luotk.chat.adapters.ChatAdapter;
import com.so.luotk.chat.models.MessageModel;
import com.so.luotk.databinding.FragmentChatBinding;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;

import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String TAG = "ChatFrag";
    FragmentChatBinding binding;
    private Handler handler;
    private final Handler searchHandler = new Handler(Looper.myLooper());
    private String batchId;
    private final String pageLength = "10";
    private final int folderpageNo = 1;
    private int file;
    private int folder;
    private String folderId, courseId, isFrom;
    private String selllingPrice = "";
    private boolean isSearchOpen, isFragmentLoaded, isRefreshing, teacheCheck;
    private final int i = 0;
    private View rootLayout;
    private Context context;
    private Activity mActivity;
    private Runnable runnable, searchRunnable;
    String UserId = "", UserMobile = "", UserName ="";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    ArrayList<MessageModel> messageModels = new ArrayList<>();
    ChatAdapter adapter;
    DatabaseReference chatRefrence;
    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance(String batchId, String folderId, String isFrom, String sellingPrice, boolean teacherornot) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(PreferenceHandler.BATCH_ID, batchId);
        args.putString(PreferenceHandler.FOLDER_ID, folderId);
        args.putString(PreferenceHandler.IS_FROM, isFrom);
        args.putString(PreferenceHandler.SELLING_PRICE, sellingPrice);
        args.putBoolean(PreferenceHandler.TEACHERORNOT, teacherornot);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.restrictScreenShot(getActivity());
//     getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        handler = new Handler(Looper.myLooper());
        if (getArguments() != null) {
            folderId = getArguments().getString(PreferenceHandler.FOLDER_ID);
            isFrom = getArguments().getString(PreferenceHandler.IS_FROM);
            selllingPrice = getArguments().getString(PreferenceHandler.SELLING_PRICE);
            teacheCheck = getArguments().getBoolean(PreferenceHandler.TEACHERORNOT);
            assert isFrom != null;
            if (isFrom.equalsIgnoreCase("batch"))
                batchId = getArguments().getString(PreferenceHandler.BATCH_ID);
            else if (isFrom.equalsIgnoreCase("course"))
                courseId = getArguments().getString(PreferenceHandler.BATCH_ID);
        }
        if (PreferenceHandler.readBoolean(getActivity(), PreferenceHandler.ADMIN_LOGGED_IN, false)) {
            AdminBatchDetailActivity.img_announcemenr_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   adapter.deleteItem(AdminBatchDetailActivity.messageModelsDelete);
//                Toast.makeText(context, "Call Delete", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater, container, false);
        rootLayout = binding.getRoot();
        runnable = new Runnable() {
            @Override
            public void run() {
//                checkInternet();
            }
        };
        UserName = PreferenceHandler.readString(getActivity(), PreferenceHandler.USER_NAME, "");
        UserId = PreferenceHandler.readString(getActivity(), PreferenceHandler.USERID, "");
        UserMobile = PreferenceHandler.readString(getActivity(), PreferenceHandler.USER_MOBILE, "");


        adapter = new ChatAdapter(messageModels, getActivity(), teacheCheck, batchId/*, messageModelsReverse*/);
        binding.recyclerView.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(linearLayoutManager);


        binding.itemMicClickParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String message = binding.itemInput.getText().toString();
                if (!TextUtils.isEmpty(message)){
                    String fromName = "";
                    if (teacheCheck) {
                        if (PreferenceHandler.readString(getActivity(), PreferenceHandler.ORG_NAME, "") != null && !PreferenceHandler.readString(getActivity(), PreferenceHandler.ORG_NAME, "").equals("")) {
                            fromName = PreferenceHandler.readString(getActivity(), PreferenceHandler.ORG_NAME, "");
                        } else {
                            fromName = PreferenceHandler.readString(getActivity(), PreferenceHandler.ORG_PHONE_NO, "");
                        }
                    } else {
                        if (PreferenceHandler.readString(getActivity(), PreferenceHandler.LOGGED_IN_USERNAME, "") != null && !PreferenceHandler.readString(getActivity(), PreferenceHandler.LOGGED_IN_USERNAME, "").equals("")) {
                            fromName = PreferenceHandler.readString(getActivity(), PreferenceHandler.LOGGED_IN_USERNAME, "");
                        } else {
                            fromName = PreferenceHandler.readString(getActivity(), PreferenceHandler.USER_MOBILE, "");
                        }
                    }
                    final MessageModel model = new MessageModel(UserId, message);

                    String pushValu = database.getReference().child("Groups")
                            .child("fk_" + batchId)
                            .push().getKey();

                    model.setTimestamp(new Date().getTime());
                    model.setMessageId(pushValu);
                    model.setMessageName(fromName);
                    binding.itemInput.setText("");

                    database.getReference().child("Groups")
                            .child("fk_" + batchId)
                            .child(pushValu)
                            .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });
                } else {
                    Utilities.makeToast(getContext(), "Please enter message.");
                }
            }
        });


        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        messageModels.clear();

        database.getReference().child("Groups").child("fk_" + batchId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    Log.d(TAG, "onChildAdded: " + snapshot.getChildrenCount());
                    Log.d(TAG, "onChildAddedMessage: " + snapshot.getValue(MessageModel.class).getMessageName());
                    MessageModel model = snapshot.getValue(MessageModel.class);
                    messageModels.add(model);
                    adapter.notifyDataSetChanged();
                    binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "onChildChanged: ");
                MessageModel messages = snapshot.getValue(MessageModel.class);
                Log.d(TAG, "onChildChanged: " + messages.getMessageId());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//                if (!teacheCheck) {
                    boolean delete = false;
                    MessageModel messages = snapshot.getValue(MessageModel.class);
                    if (!delete) {
                        for (int i = 0; i < messageModels.size(); i++) {
                            if (messages.getMessageId().equals(messageModels.get(i).getMessageId())) {
                                messageModels.remove(messages);
                                messageModels.remove(i);
                                adapter.notifyDataSetChanged();
                                delete = true;
                            } else {
                                delete = false;
                            }
                        }
                    }
                }
//            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}