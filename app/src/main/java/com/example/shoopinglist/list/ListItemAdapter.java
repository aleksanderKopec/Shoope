package com.example.shoopinglist.list;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoopinglist.R;
import com.example.shoopinglist.database.DatabaseManager;
import com.example.shoopinglist.login.AuthManager;

import java.util.ArrayList;
import java.util.List;

public class ListItemAdapter extends RecyclerView.Adapter<ListItemViewHolder> {
    private final AuthManager authManager;
    private final LayoutInflater layoutInflater;
    private final View fragmentView;
    private final Activity activity;
    private List<ListItem> listItems = new ArrayList<>();
    private DatabaseManager databaseManager;


    public ListItemAdapter(AuthManager authManager, LayoutInflater layoutInflater, View fragmentView, Activity activity) {
        this.authManager = authManager;
        this.layoutInflater = layoutInflater;
        this.fragmentView = fragmentView;
        this.activity = activity;
        reloadDatabase();
    }

    private void enableEditingItem(View itemView, int position) {
        itemView.setOnClickListener((view -> {
            ListItem item = listItems.get(position);
            View dialogView = layoutInflater.inflate(R.layout.popup_layout, (ViewGroup) fragmentView, false);
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);

            dialogBuilder.setView(dialogView);
            dialogBuilder.setTitle("Edit item");

            EditText dialogNameView = dialogView.findViewById(R.id.popup_item_name);
            EditText dialogAmountView = dialogView.findViewById(R.id.popup_item_amount);
            EditText dialogValueView = dialogView.findViewById(R.id.popup_item_value);

            dialogNameView.setText(item.getText());
            dialogAmountView.setText(String.valueOf(item.getAmount()));
            dialogValueView.setText(String.valueOf(item.getValue()));

            dialogBuilder.setPositiveButton("Save", (dialogInterface, i) -> {
                String popupItemName = dialogNameView.getText().toString();
                String popupItemValue = dialogValueView.getText().toString();
                String popupItemAmount = dialogAmountView.getText().toString();
                ListItem newItem = new ListItem(popupItemName, Integer.parseInt(popupItemAmount), Double.parseDouble(popupItemValue));
                editItem(position, newItem);
            });
            dialogBuilder.setCancelable(true);
            dialogBuilder.show();
        }));
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

    public void editItem(int position, ListItem newItem) {
        listItems.set(position, newItem);
        if (databaseManager != null) {
            databaseManager.setList(listItems);
        }
        this.notifyItemChanged(position);
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ListItemViewHolder holder, int position) {
        ListItem listItem = listItems.get(position);
        holder.getItemNameView().setText(listItem.getText());
        holder.getItemAmountView().setText(listItem.getAmount() + "x");
        holder.getItemValueView().setText(String.valueOf(listItem.getValue()));

        ImageView itemImageView = (ImageView) holder.getItemView().findViewById(R.id.checkmark_image_view);
        itemImageView.setOnClickListener((it) -> {
            listItem.setChecked(!listItem.isChecked());
            editItem(position, listItem);
        });
        enableEditingItem(holder.getItemView(), position);
        setItemCheckedImage(itemImageView, listItem);
    }

    private void setItemCheckedImage(ImageView itemImageView, ListItem item) {
        Context context = itemImageView.getContext();
        if (item.isChecked()) {
            itemImageView.setBackground(AppCompatResources.getDrawable(context, R.drawable.xmark));
        } else {
            itemImageView.setBackground(AppCompatResources.getDrawable(context, R.drawable.checkmark));
        }
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }
}