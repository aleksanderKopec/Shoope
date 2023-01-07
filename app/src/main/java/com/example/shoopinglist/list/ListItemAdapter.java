package com.example.shoopinglist.list;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoopinglist.R;
import com.example.shoopinglist.database.DatabaseManager;
import com.example.shoopinglist.login.AuthManager;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Odpowiada za większość funkcjonalności związanych z listą elementów.
 */
public class ListItemAdapter extends RecyclerView.Adapter<ListItemViewHolder> {

    public static String fileUri;

    public static int REQUEST_IMAGE_CAPTURE = 0;
    private final AuthManager authManager;
    private final LayoutInflater layoutInflater;
    private final View fragmentView;
    private final Activity activity;
    public DatabaseManager databaseManager;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private List<ListItem> listItems = new ArrayList<>();
    private String currentPhotoPath;

    /**
     * Oprócz standardowej konstrukcji obiektu wywoluje także odświeżenie wartości w bazie danych.
     */
    public ListItemAdapter(AuthManager authManager, LayoutInflater layoutInflater, View fragmentView, Activity activity) {
        this.authManager = authManager;
        this.layoutInflater = layoutInflater;
        this.fragmentView = fragmentView;
        this.activity = activity;
        reloadDatabase();
    }

    /**
     * Uruchamia mechanizmy konieczne do edytowania elementów znajdująych się na liście.
     *
     * @param itemView Widok wyświetlanego elementu
     * @param position Pozycja elementu na liście.
     */
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
            ImageView dialogImageView = dialogView.findViewById(R.id.popup_item_image);

            if (item.getPhotoFilePath() != null) {
                File imgFile = new File(item.getPhotoFilePath());
                if (imgFile.exists()) {
                    Bitmap imageBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    dialogImageView.setImageBitmap(imageBitmap);
                } else {
                    FileDownloadTask task = storage.getReference().child(imgFile.getName()).getFile(Uri.fromFile(imgFile));
                    task.addOnSuccessListener(taskSnapshot -> {
                        Bitmap imageBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        dialogImageView.setImageBitmap(imageBitmap);
                    });

                    task.addOnFailureListener(taskSnaphot -> {
                        Log.d("ERROR", "Error uploading image");
                        Log.d("ERROR", taskSnaphot.getMessage());
                    });
                }
            } else {
                dialogImageView.setVisibility(View.GONE);
            }

            dialogNameView.setText(item.getText());
            dialogAmountView.setText(String.valueOf(item.getAmount()));
            dialogValueView.setText(String.valueOf(item.getValue()));

            dialogBuilder.setNeutralButton("Take photo", ((dialogInterface, i) -> {
                dispatchTakePictureIntent();
                if (currentPhotoPath != null) {
                    item.setPhotoFilePath(currentPhotoPath);
                    editItem(position, item);
                }
                currentPhotoPath = null;
            }));

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


    /**
     * Odświeża połączenie z bazą danych zależnie od stanu autentykacji uzytkownika.
     */
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

    /**
     * Wykonuje operacje potrzebne do zapewnienia interaktywności elementów listy.
     *
     * @param holder   Pojemnik na widok elementu listy
     * @param position Pozycja elementu listy
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ListItemViewHolder holder, int position) {
        ListItem listItem = listItems.get(position);
        holder.getItemNameView().setText(listItem.getText());
        holder.getItemAmountView().setText(listItem.getAmount() + "x");
        holder.getItemValueView().setText(String.valueOf(listItem.getValue()));

        ImageView itemImageView = holder.getItemView().findViewById(R.id.checkmark_image_view);
        itemImageView.setOnClickListener((it) -> {
            listItem.setChecked(!listItem.isChecked());
            editItem(position, listItem);
        });
        enableEditingItem(holder.getItemView(), position);
        setItemCheckedImage(itemImageView, listItem);
    }

    /**
     * Ustawia wyświetlany obrazek zależnie od stanu elementu
     *
     * @param itemImageView Widok elementu
     * @param item          item którego stan został zmieniony
     */
    private void setItemCheckedImage(ImageView itemImageView, ListItem item) {
        Context context = itemImageView.getContext();
        if (item.isChecked()) {
            itemImageView.setBackground(AppCompatResources.getDrawable(context, R.drawable.xmark));
        } else {
            itemImageView.setBackground(AppCompatResources.getDrawable(context, R.drawable.checkmark));
        }
    }

    /**
     * Uruchamia element odpowiadający za robienie zdjęć elementów listy oraz tworzenie pliku dla tych zdjęć.
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(fragmentView.getContext(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                fileUri = photoURI.toString();
                activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    /**
     * Tworzy plik dla zdjęcia. Plik musi być utworzony przed faktycznym stworzeniem zdjęcia.
     *
     * @return zwraca obiekt odpowiadający utworzonemu plikowi
     * @throws IOException jeśli nie udało się utworzyć pliku
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }
}