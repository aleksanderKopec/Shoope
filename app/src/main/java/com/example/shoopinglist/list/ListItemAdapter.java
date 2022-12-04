package com.example.shoopinglist.list;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoopinglist.database.DatabaseManager;
import com.example.shoopinglist.login.AuthManager;

import java.util.ArrayList;
import java.util.List;

public class ListItemAdapter extends RecyclerView.Adapter<ListItemViewHolder> {
    private final AuthManager authManager;
    private List<ListItem> listItems = new ArrayList<>();
    private DatabaseManager databaseManager;

    public ListItemAdapter(AuthManager authManager) {
        this.authManager = authManager;
        reloadDatabase();
    }

    public void reloadDatabase() {
        Log.d("RELOAD_DATABASE", "reloading database");
        if (authManager.getUser() != null) {
            databaseManager = new DatabaseManager(authManager.getUser().getUid());
            addDataChangeListener();
        } else {
            databaseManager = null;
        }
    }

    public ListItem getItem(int position) {
        return listItems.get(position);
    }

    public List<ListItem> getItemList() {
        return this.listItems;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItemList(List<ListItem> newList) {
        this.listItems = newList;
        this.notifyDataSetChanged();
    }

    public void addItem(int position, ListItem item) {
        listItems.add(position, item);
        if (databaseManager != null) {
            databaseManager.setList(listItems);
        }
        this.notifyItemInserted(position);
    }

    public void addItem(ListItem item) {
        listItems.add(item);
        if (databaseManager != null) {
            databaseManager.setList(listItems);
        }
        this.notifyItemInserted(listItems.size() - 1);
    }


    public ListItem popItem(int position) {
        ListItem item = listItems.remove(position);
        if (databaseManager != null) {
            databaseManager.setList(listItems);
        }
        this.notifyItemRemoved(position);
        return item;
    }

    public void addDataChangeListener() {
        databaseManager.addDataChangeListener(this);
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
        holder.getItemAmountView().setText(listItems.get(position).getAmount() + "x");
        holder.getItemValueView().setText(String.valueOf(listItems.get(position).getValue()));
        holder.setImageOnClickListener();
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }
}