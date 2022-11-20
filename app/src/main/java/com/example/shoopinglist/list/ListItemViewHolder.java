package com.example.shoopinglist.list;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoopinglist.R;

public class ListItemViewHolder extends RecyclerView.ViewHolder {
    private final TextView textView;

    public ListItemViewHolder(@NonNull View itemView) {
        super(itemView);

        textView = (TextView) itemView.findViewById(R.id.common_item_text);
    }

    public TextView getTextView() {
        return textView;
    }
}
