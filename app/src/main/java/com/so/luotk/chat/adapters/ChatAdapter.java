package com.so.luotk.chat.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.so.luotk.R;
import com.so.luotk.activities.adminrole.AdminBatchDetailActivity;
import com.so.luotk.chat.models.MessageModel;
import com.so.luotk.utils.PreferenceHandler;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class ChatAdapter extends RecyclerView.Adapter {
    String currentDate = "", NextDate = "", previousDate;
    ArrayList<MessageModel> messageModels;
    Context context;
    String batchId;
    boolean teacheCheck;
    DatabaseReference chatRefrence;
    boolean multipleClick = false;
    int SENDER_VIEW_TYPE = 1;
    int RECIEVER_VIEW_TYPE = 2;

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context) {
        this.messageModels = messageModels;
        this.context = context;
    }

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context, boolean teacheCheck, String batchId) {
        this.messageModels = messageModels;
        this.context = context;
        this.teacheCheck = teacheCheck;
        this.batchId = batchId;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_VIEW_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_sender, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_reciever, parent, false);
            return new RecieverViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel messageModel = messageModels.get(position);
        currentDate = getDate(messageModel.getTimestamp());
        try {
            if (position > 0) {
                NextDate = getDate(messageModels.get(position - 1).getTimestamp());
                Log.d("TAG", "onBindViewHolder: " + position);
                if (holder.getClass() == SenderViewHolder.class) {
                    if (!currentDate.equals(NextDate)) {
                        ((SenderViewHolder) holder).dateLayout.setVisibility(View.VISIBLE);
                        ((SenderViewHolder) holder).dateTv.setText(getDate(messageModel.getTimestamp()));
                        currentDate = NextDate;
                        Log.d("TAG", "SenderViewHolder2: ");
                    } else {
                        Log.d("TAG", "SenderViewHolder3: ");
                        ((SenderViewHolder) holder).dateLayout.setVisibility(View.GONE);
                    }
                    ((SenderViewHolder) holder).senderMsg.setText(messageModel.getMessages());
                    ((SenderViewHolder) holder).senderTime.setText(getTime(messageModel.getTimestamp()));
                    ((SenderViewHolder) holder).itemLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (multipleClick) {
                                if (messageModel.isSelected()) {
                                    AdminBatchDetailActivity.messageModelsDelete.remove(messageModel);
                                } else {
                                    AdminBatchDetailActivity.messageModelsDelete.add(messageModel);
                                }
                                Log.d("TAG", "onClick1: " + AdminBatchDetailActivity.messageModelsDelete.size());
                                if (AdminBatchDetailActivity.messageModelsDelete.size() == 0) {
                                    multipleClick = false;
                                    AdminBatchDetailActivity.img_announcemenr_icon.setVisibility(View.GONE);
                                }
                                messageModel.setSelected(!messageModel.isSelected());
                                ((SenderViewHolder) holder).view.setBackgroundColor(messageModel.isSelected() ? context.getResources().getColor(R.color.selected_backgroun) : context.getResources().getColor(R.color.white));
                            }
                        }
                    });
                    ((SenderViewHolder) holder).view.setBackgroundColor(context.getResources().getColor(R.color.white));
                } else {
                    if (!currentDate.equals(NextDate)) {
                        ((RecieverViewHolder) holder).dateLayout.setVisibility(View.VISIBLE);
                        ((RecieverViewHolder) holder).dateTv.setText(getDate(messageModel.getTimestamp()));
                        currentDate = NextDate;
                        Log.d("TAG", "onBindViewHolder5: ");
                    } else {
                        Log.d("TAG", "onBindViewHolder6: ");
                        ((RecieverViewHolder) holder).dateLayout.setVisibility(View.GONE);
                    }
                    ((RecieverViewHolder) holder).recieverMsg.setText(messageModel.getMessages());
                    ((RecieverViewHolder) holder).recieverTime.setText(getTime(messageModel.getTimestamp()));
                    ((RecieverViewHolder) holder).rec_name.setText(messageModel.getMessageName());
                    ((RecieverViewHolder)holder).itemLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (multipleClick) {
                                if (messageModel.isSelected()) {
                                    AdminBatchDetailActivity.messageModelsDelete.remove(messageModel);
                                } else {
                                    AdminBatchDetailActivity.messageModelsDelete.add(messageModel);
                                }
                                Log.d("TAG", "onClick2: " + AdminBatchDetailActivity.messageModelsDelete.size());
                                if (AdminBatchDetailActivity.messageModelsDelete.size() == 0) {
                                    multipleClick = false;
                                    AdminBatchDetailActivity.img_announcemenr_icon.setVisibility(View.GONE);
                                }
                                messageModel.setSelected(!messageModel.isSelected());
                                ((RecieverViewHolder) holder).view.setBackgroundColor(messageModel.isSelected() ? context.getResources().getColor(R.color.selected_backgroun) : context.getResources().getColor(R.color.white));
                            }
                        }
                    });
                    ((RecieverViewHolder) holder).view.setBackgroundColor(context.getResources().getColor(R.color.white));
                }

            } else {
                if (holder.getClass() == SenderViewHolder.class) {
                    ((SenderViewHolder) holder).dateLayout.setVisibility(View.VISIBLE);
                    ((SenderViewHolder) holder).dateTv.setText(getDate(messageModel.getTimestamp()));
                    ((SenderViewHolder) holder).senderMsg.setText(messageModel.getMessages());
                    ((SenderViewHolder) holder).senderTime.setText(getTime(messageModel.getTimestamp()));
                    ((SenderViewHolder)holder).itemLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (multipleClick) {
                                if (messageModel.isSelected()) {
                                    AdminBatchDetailActivity.messageModelsDelete.remove(messageModel);
                                } else {
                                    AdminBatchDetailActivity.messageModelsDelete.add(messageModel);
                                }
                                Log.d("TAG", "onClick3: " + AdminBatchDetailActivity.messageModelsDelete.size());
                                if (AdminBatchDetailActivity.messageModelsDelete.size() == 0) {
                                    multipleClick = false;
                                    AdminBatchDetailActivity.img_announcemenr_icon.setVisibility(View.GONE);
                                }
                                messageModel.setSelected(!messageModel.isSelected());
                                ((SenderViewHolder) holder).view.setBackgroundColor(messageModel.isSelected() ? context.getResources().getColor(R.color.selected_backgroun) : context.getResources().getColor(R.color.white));
                            }
                        }
                    });
                    ((SenderViewHolder) holder).view.setBackgroundColor(context.getResources().getColor(R.color.white));
                } else {
                    ((RecieverViewHolder) holder).dateLayout.setVisibility(View.VISIBLE);
                    ((RecieverViewHolder) holder).dateTv.setText(getDate(messageModel.getTimestamp()));
                    ((RecieverViewHolder) holder).recieverMsg.setText(messageModel.getMessages());
                    ((RecieverViewHolder) holder).recieverTime.setText(getTime(messageModel.getTimestamp()));
                    ((RecieverViewHolder) holder).rec_name.setText(messageModel.getMessageName());
                    ((RecieverViewHolder)holder).itemLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (multipleClick) {
                                if (messageModel.isSelected()) {
                                    AdminBatchDetailActivity.messageModelsDelete.remove(messageModel);
                                } else {
                                    AdminBatchDetailActivity.messageModelsDelete.add(messageModel);
                                }
                                Log.d("TAG", "onClick4: " + AdminBatchDetailActivity.messageModelsDelete.size());
                                if (AdminBatchDetailActivity.messageModelsDelete.size() == 0) {
                                    multipleClick = false;
                                    AdminBatchDetailActivity.img_announcemenr_icon.setVisibility(View.GONE);
                                }
                                messageModel.setSelected(!messageModel.isSelected());
                                ((RecieverViewHolder) holder).view.setBackgroundColor(messageModel.isSelected() ? context.getResources().getColor(R.color.selected_backgroun) : context.getResources().getColor(R.color.white));
                            }
                        }
                    });
                    ((RecieverViewHolder) holder).view.setBackgroundColor(context.getResources().getColor(R.color.white));
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (messageModels.get(position).getuId().equals(PreferenceHandler.readString(context, PreferenceHandler.USERID, ""))) {
            return SENDER_VIEW_TYPE;
        } else {
            return RECIEVER_VIEW_TYPE;
        }
    }

    public class RecieverViewHolder extends RecyclerView.ViewHolder {

        TextView recieverMsg, recieverTime, dateTv, rec_name;
        RelativeLayout dateLayout;
        LinearLayout itemLayout;
        private final View view;

        public RecieverViewHolder(@NonNull View itemView) {
            super(itemView);
            recieverMsg = itemView.findViewById(R.id.text_content);
            recieverTime = itemView.findViewById(R.id.text_time);
            dateLayout = itemView.findViewById(R.id.dateLayout);
            dateTv = itemView.findViewById(R.id.dateTv);
            rec_name = itemView.findViewById(R.id.rec_name);
            itemLayout = itemView.findViewById(R.id.itemLayout);
            view = itemView;
            itemLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.d("TAG", "onLongClick: " + teacheCheck);
                    if (!teacheCheck) {
                        Log.d("MultipleReciever", "onLongClick: " + multipleClick);
                        if (!multipleClick) {
                            int pos = getPosition();
                            chatRefrence = FirebaseDatabase.getInstance().getReference().child(PreferenceHandler.BATCH_GROUP).child("fk_" + batchId);
                            Log.d("TAG", "onLongClick: " + batchId);
                            MessageModel messageModel = messageModels.get(pos);
                            multipleClick = true;
                            messageModel.setSelected(!messageModel.isSelected());
                            view.setBackgroundColor(context.getResources().getColor(R.color.selected_backgroun));
                            AdminBatchDetailActivity.messageModelsDelete.add(messageModel);
//                            view.setBackgroundColor(messageModel.isSelected() ? Color.CYAN : Color.WHITE);
                            AdminBatchDetailActivity.img_announcemenr_icon.setVisibility(View.VISIBLE);
//                            AdminBatchDetailActivity.iconLayout.setImageResource(R.drawable.ic_baseline_delete_24);

                        }
                    }
                    return true;
                }
            });

        }
    }

    public void deleteItem(ArrayList<MessageModel> messageModels) {
        new AlertDialog.Builder(context)
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete this message?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i  = 0; i < messageModels.size(); i++) {
                            chatRefrence.child(messageModels.get(i).getMessageId())
                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                   /* messageModels.remove(pos);
                                    notifyItemRemoved(pos);
                                    notifyItemRangeChanged(pos,messageModels.size());*/
                                        setAfterDelete();
                                        Toast.makeText(context, "Deleted Successfully.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "Error Occured.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    public void setAfterDelete() {
        multipleClick = false;
        AdminBatchDetailActivity.img_announcemenr_icon.setVisibility(View.GONE);
        AdminBatchDetailActivity.messageModelsDelete.clear();
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {

        TextView senderMsg, senderTime, dateTv;
        RelativeLayout dateLayout;
        LinearLayout itemLayout;
        private final View view;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.text_content);
            senderTime = itemView.findViewById(R.id.text_time);
            dateLayout = itemView.findViewById(R.id.dateLayout);
            dateTv = itemView.findViewById(R.id.dateTv);
            itemLayout = itemView.findViewById(R.id.itemLayout);
            view = itemView;
            itemLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.d("TAG", "onLongClick: " + teacheCheck);
                    if (!teacheCheck) {
                        Log.d("MultipleSender", "onLongClick: " + multipleClick);
                        if (!multipleClick) {
                            int pos = getPosition();
                            chatRefrence = FirebaseDatabase.getInstance().getReference().child(PreferenceHandler.BATCH_GROUP).child("fk_" + batchId);
                            Log.d("TAG", "onLongClick: " + batchId);
                            MessageModel messageModel = messageModels.get(pos);
                            multipleClick = true;
                            view.setBackgroundColor(context.getResources().getColor(R.color.selected_backgroun));
                            AdminBatchDetailActivity.messageModelsDelete.add(messageModel);
//                            view.setBackgroundColor(messageModel.isSelected() ? Color.CYAN : Color.WHITE);
                            AdminBatchDetailActivity.img_announcemenr_icon.setVisibility(View.VISIBLE);
//                            AdminBatchDetailsFragment.img_settings_icon.setImageResource(R.drawable.ic_baseline_delete_24);
                        }
                        /*new AlertDialog.Builder(context)
                                .setTitle("Delete")
                                .setMessage("Are you sure you want to delete this message?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        chatRefrence.child(messageModel.getMessageId())
                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    messageModels.remove(pos);
                                                    notifyItemRemoved(pos);
                                                    notifyItemRangeChanged(pos,messageModels.size());
                                                    Toast.makeText(context, "Deleted Successfully.", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(context, "Error Occured.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();*/
                    }
                    return true;
                }
            });

        }
    }

    private String getDateTime(long time_stamp_server) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        return formatter.format(time_stamp_server);
    }

    private String getDate(long time_stamp_server) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        return formatter.format(time_stamp_server);
    }

    private String getTime(long time_stamp_server) {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
        return formatter.format(time_stamp_server);
    }

    public ArrayList<MessageModel> reverse(ArrayList<MessageModel> list) {

        for (int i = 0; i < list.size() / 2; i++) {
            MessageModel temp = list.get(i);
            list.set(i, list.get(list.size() - i - 1));
            list.set(list.size() - i - 1, temp);
        }

        return list;
    }
    public static final Drawable getDrawable(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 21) {
            return ContextCompat.getDrawable(context, id);
        } else {
            return context.getResources().getDrawable(id);
        }
    }
}