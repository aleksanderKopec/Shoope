package com.example.shoopinglist.list;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoopinglist.R;

/**
 * Pojemnik odpowiadający za widok poszczególnych elementów z listy.
 */
public class ListItemViewHolder extends RecyclerView.ViewHolder {
    private final TextView itemNameView;
    private final TextView itemValueView;
    private final TextView itemAmountView;
    private final ImageView itemImageView;
    private final View itemView;

    private final boolean isChecked = false;

    public ListItemViewHolder(@NonNull View itemView) {
        super(itemView);
        this.itemView = itemView;
        itemNameView = (TextView) itemView.findViewById(R.id.common_item_text);
        itemAmountView = (TextView) itemView.findViewById(R.id.common_item_amount);
        itemValueView = (TextView) itemView.findViewById(R.id.common_item_value);
        itemImageView = (ImageView) itemView.findViewById(R.id.checkmark_image_view);
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

    public View getItemView() {
        return itemView;
    }
}

