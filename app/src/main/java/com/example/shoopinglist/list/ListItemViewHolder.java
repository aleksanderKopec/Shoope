package com.example.shoopinglist.list;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoopinglist.R;

public class ListItemViewHolder extends RecyclerView.ViewHolder {
    private final TextView itemNameView;
    private final TextView itemValueView;
    private final TextView itemAmountView;
    private final ImageView itemImageView;

    private boolean isChecked = false;

    public ListItemViewHolder(@NonNull View itemView) {
        super(itemView);
        itemNameView = (TextView) itemView.findViewById(R.id.common_item_text);
        itemAmountView = (TextView) itemView.findViewById(R.id.common_item_amount);
        itemValueView = (TextView) itemView.findViewById(R.id.common_item_value);
        itemImageView = (ImageView) itemView.findViewById(R.id.checkmark_image_view);
    }

    public void setImageOnClickListener() {
        ImageView imageView = (ImageView) itemView.findViewById(R.id.checkmark_image_view);
        imageView.setOnClickListener((it) -> {
            switchBackgroundImage();
        });

    }

    private void switchBackgroundImage() {
        Context context = itemImageView.getContext();
        if (isChecked) {
            itemImageView.setBackground(AppCompatResources.getDrawable(context, R.drawable.xmark));
        } else {
            itemImageView.setBackground(AppCompatResources.getDrawable(context, R.drawable.checkmark));
        }
        isChecked = !isChecked;
    }
    
    public TextView getItemNameView() {
        return itemNameView;
    }

    public TextView getItemValueView() {
        return itemValueView;
    }

    public TextView getItemAmountView() {
        return itemAmountView;
    }
}


