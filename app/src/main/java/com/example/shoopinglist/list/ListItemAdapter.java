package com.example.shoopinglist.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ListItemAdapter extends RecyclerView.Adapter<ListItemViewHolder> {
    private List<ListItem> listItems = new ArrayList<>();

    public ListItemAdapter(List<ListItem> listItems) {
        this.listItems = listItems;
    }

    public ListItemAdapter() {
    }

    public void addItem(int position, ListItem item) {
        listItems.add(position, item);
        this.notifyItemInserted(position);
    }

    public void addItem(ListItem item) {
        listItems.add(item);
        this.notifyItemInserted(listItems.size() - 1);
    }


    public ListItem popItem(int position) {
        ListItem item = listItems.remove(position);
        this.notifyItemRemoved(position);
        return item;
    }


    @NonNull
    @Override
    public ListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(ListItem.getLayout(), parent, false);
        return new ListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemViewHolder holder, int position) {
        holder.getItemNameView().setText(listItems.get(position).getText());
        holder.getItemAmountView().setText(String.valueOf(listItems.get(position).getAmount()));
        holder.getItemValueView().setText(String.valueOf(listItems.get(position).getValue()));
        holder.setImageOnClickListener();
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }
}