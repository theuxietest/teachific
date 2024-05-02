package com.so.luotk.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.so.luotk.R;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapterExample extends
        SliderViewAdapter<SliderAdapterExample.SliderAdapterVH> {

    private final Context context;
    private String fromWhere;
    private List<String> mSliderItems = new ArrayList<>();

    public SliderAdapterExample(Context context) {
        this.context = context;
    }

    public void renewItems(List<String> sliderItems, String fromWhere) {
        this.mSliderItems = sliderItems;
        this.fromWhere = fromWhere;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.mSliderItems.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(String sliderItem) {
        this.mSliderItems.add(sliderItem);
        notifyDataSetChanged();
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_slider, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {

        String sliderItem = mSliderItems.get(position);

    /*    viewHolder.textViewDescription.setText(sliderItem.getDescription());
        viewHolder.textViewDescription.setTextSize(16);
        viewHolder.textViewDescription.setTextColor(Color.WHITE);*/
        if (fromWhere.equals("web")) {
            Glide.with(viewHolder.itemView)
                    .load(sliderItem)
                    .fitCenter()
                    .into(viewHolder.imageViewBackground);
        } else {
            Glide.with(viewHolder.itemView)
                    .load(Uri.parse("file:///android_asset/sliderImages/" + sliderItem))
                    .fitCenter()
                    .into(viewHolder.imageViewBackground);
        }
        if (getCount() == 1) {
            viewHolder.itemView.setEnabled(false);
            viewHolder.itemView.setOnTouchListener(null);
        }
        else
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(context, "This is item in position " + position, Toast.LENGTH_SHORT).show();
                }
            });
    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return mSliderItems.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;
  /*      ImageView imageGifContainer;
        TextView textViewDescription;*/

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.slider_image);
           /* imageGifContainer = itemView.findViewById(R.id.iv_gif_container);
            textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider);*/
            this.itemView = itemView;
        }
    }

}
