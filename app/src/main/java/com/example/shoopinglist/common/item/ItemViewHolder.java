package com.example.shoopinglist.common.item;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoopinglist.R;

public class ItemViewHolder extends RecyclerView.ViewHolder {

    private final TextView textView;
    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);
        textView = (TextView) itemView.findViewById(R.id.common_item_text);
    }

    public TextView getTextView(){
        return textView;
    }
}
