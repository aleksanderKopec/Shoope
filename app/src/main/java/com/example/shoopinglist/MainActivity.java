package com.example.shoopinglist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shoopinglist.databinding.ActivityMainBinding;
import com.example.shoopinglist.list.ListItemAdapter;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.wtf("ACTIVITYRESULT", String.valueOf(resultCode));
        if (requestCode == ListItemAdapter.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Uri photoUri = Uri.parse(ListItemAdapter.fileUri);
            Log.d("UPLOAD", "Srting uploading the file: ");
            Log.d("UPLOAD", photoUri.toString());
            UploadTask task = FirebaseStorage.getInstance().getReference().child(photoUri.getLastPathSegment()).putFile(photoUri);

            task.addOnProgressListener(taskSnapshot -> {
                Log.d("COUNTING", String.valueOf(taskSnapshot.getBytesTransferred()));
                Log.d("COUNTING", String.valueOf(taskSnapshot.getTotalByteCount()));
            });

            task.addOnSuccessListener(taskSnapshot -> {
                Log.d("SUCCESS", "Successfully uploaded file");
            });
            task.addOnFailureListener(taskSnapshot -> {
                Log.d("FAILURE", "Failed to upload file");
                Log.wtf("FAILURE", taskSnapshot.getMessage());
            });
        }
    }

}