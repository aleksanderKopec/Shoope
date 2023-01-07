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

/**
 * Punk wejsćiowy aplikacji - odpowiada za tworzenie głównego widoku i jest 'rodzicem' wszystkich
 * innych widoków.
 */
public class MainActivity extends AppCompatActivity {


    /**
     * Punkt wejściowy dla każdej instacji MainActivity. W naszym przypadku jedynie tworzy
     * podstawowy widok.
     *
     * @param savedInstanceState Zawiera dane przesłane z poprzedniej instacji MainActivity lub 'null'
     *                           jeśli żadne nie zostały przesłane.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
    }

    /**
     * Obsługuje dane zwrócone przez zewnętrzne Activity. W naszym przypadku służy wyłącznie do
     * obłużenia stworzonego zdjęcia - znajduje plik w którym zapisane jest zdjęcie i wysyła
     * je do firebase database.
     *
     * @param requestCode Używany do ustalenia źródła które wywołało reakcje. W naszym przypadku to zawsze
     *                    wartość 0 (pod nazwą REQUEST_IMAGE_CAPTURE).
     * @param resultCode  Używany do rozpoznania stanu zwróconej reakcji (powodzenie/przerwanie/niepowodzenie).
     * @param data        Zawiera w sobie dodatkowe dane przesłane przez zakończone Activity. W naszym przypadku zawsze 'null'.
     */
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