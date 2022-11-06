package com.example.shoopinglist.common.item;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public abstract class ItemAdapter<T extends Item> extends RecyclerView.Adapter<ItemViewHolder> {

    private final ArrayList<T> dataSet;

    public ItemAdapter(ArrayList<T> dataSet) {
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(T.getLayout(), parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.getTextView()
                .setText(
                        dataSet.get(position).getText());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
