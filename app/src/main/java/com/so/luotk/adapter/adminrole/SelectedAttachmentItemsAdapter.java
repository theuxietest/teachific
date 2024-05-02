package com.so.luotk.adapter.adminrole;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.so.luotk.R;
import com.so.luotk.databinding.ItemAttachedFilesBinding;

import java.io.File;
import java.util.List;

public class SelectedAttachmentItemsAdapter extends RecyclerView.Adapter<SelectedAttachmentItemsAdapter.ViewHolder> {
    private final List<String> selectedAttachments;
    private Context context;
    private ItemAttachedFilesBinding itemsBinding;
    public onRemoveListener removeListener;
    private final boolean isFromTeacher;

    private final onItemClickListener clickListener;

    public SelectedAttachmentItemsAdapter(List<String> selectedAttachments, boolean isFromTeacher, SelectedAttachmentItemsAdapter.onItemClickListener clickListener, onRemoveListener removeListener) {
        this.selectedAttachments = selectedAttachments;
        this.clickListener = clickListener;
        this.removeListener = removeListener;
        this.isFromTeacher = isFromTeacher;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new SelectedAttachmentItemsAdapter.ViewHolder(ItemAttachedFilesBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String selectedfile = selectedAttachments.get(position);
        File file = new File(selectedfile);
        Log.e("TAG", "onBindViewHolder: "+ file.getPath() + " Ur file path"+Uri.fromFile(file));
        if (isFromTeacher)
            holder.binding.imgRemove.setVisibility(View.GONE);
        else holder.binding.imgRemove.setOnClickListener(v -> {
            if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                removeAt(holder.getAdapterPosition());
                removeListener.onRemove();
                /*if (CreateAssignmentTestActivity.allOldFilePaths.size() > 0) {
                    for (int i = 0; i < CreateAssignmentTestActivity.allOldFilePaths.size(); i++) {
                        if (selectedfile.equals(CreateAssignmentTestActivity.allOldFilePaths.get(i))) {
                            CreateAssignmentTestActivity.allOldFilePaths.remove(i);
                        }
                    }
                }*/

            }
        });
        holder.binding.tvFileName.setText(file.getName());

        holder.binding.tvFileName.setOnClickListener(view -> {
            if (holder.getAdapterPosition() != RecyclerView.NO_POSITION)
                clickListener.onClick(holder.getAdapterPosition());
        });
        setItemIcons(selectedfile);
    }

    private void setItemIcons(String selectedfile) {
        String lowerPath = selectedfile.toLowerCase();
        if (lowerPath.contains(".jpg") || lowerPath.contains(".jpeg") || lowerPath.contains(".png")) {
            itemsBinding.imgFileType.setImageResource(R.drawable.ic_img);
        }
        if (lowerPath.contains(".pdf")) {
            itemsBinding.imgFileType.setImageResource(R.drawable.ic_pdf);
        }
        if (lowerPath.contains(".doc")) {
            itemsBinding.imgFileType.setImageResource(R.drawable.ic_ppt);
        }
        if (lowerPath.contains(".ppt")) {
            itemsBinding.imgFileType.setImageResource(R.drawable.ic_real_ppt);
        }
    }

    public interface onItemClickListener {
        void onClick(int position);
    }

    public List<String> getList() {
        return this.selectedAttachments;
    }

    public interface onRemoveListener {
        void onRemove();
    }

    @Override
    public int getItemCount() {
        return selectedAttachments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemAttachedFilesBinding binding;

        public ViewHolder(@NonNull ItemAttachedFilesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemsBinding = binding;
        }
    }

    public void removeAt(int position) {
        selectedAttachments.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, selectedAttachments.size());
    }
}
