package com.example.shoopinglist.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.shoopinglist.list.ListItem;
import com.example.shoopinglist.list.ListItemAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class DatabaseManager {

    private static final String LOG_TAG = "DatabaseManager";

    private static final String LIST_KEY = "LIST";

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();

    private final DatabaseReference userReference;
    private final DatabaseReference listReference;

    public DatabaseManager(String userId) {
        this.userReference = database.getReference(userId);
        this.listReference = userReference.child(LIST_KEY);
    }

    public void setList(List<ListItem> list) {
        listReference.setValue(list);
    }

    public void addDataChangeListener(ListItemAdapter adapter) {
        listReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ListItem> newList = snapshot.getValue(new GenericTypeIndicator<>() {
                });
                Log.d(LOG_TAG, "addDataChangeListener - newlist:" + newList);
                if (newList != null) {
                    adapter.setItemList(newList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("DATABASE_MANAGER", "addDataChangeListener::onCancelled");
            }
        });
    }

}
