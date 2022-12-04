package com.example.shoopinglist.list;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoopinglist.R;
import com.example.shoopinglist.common.SwipeToDeleteCallback;
import com.example.shoopinglist.databinding.FragmentListBinding;
import com.example.shoopinglist.login.AuthManager;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;

public class ListFragment extends Fragment {

    private FragmentListBinding binding;
    private RecyclerView listRecyclerView;
    private ListItemAdapter listAdapter;
    private final AuthManager authManager = AuthManager.createInstance(registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    ));

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentListBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        listRecyclerView = rootView.findViewById(R.id.list_recycler_view);

        listRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        listAdapter = new ListItemAdapter();
        createSampleDataset(listAdapter, 5);
        listRecyclerView.setAdapter(listAdapter);
        enableSwipeToDeleteAndUndo();
        enableAddingListItems();
        return rootView;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonAddList.setOnClickListener(view1 -> authManager.startLoginActivity());
        binding.buttonAddList.setText(R.string.LoginButtonText);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void createSampleDataset(ListItemAdapter adapter, int amount) {
        for (int i = 0; i < amount; i++) {
            adapter.addItem(new ListItem("This is item #" + i, 0, 0));
        }
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this.getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                listAdapter.popItem(position);
            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(listRecyclerView);
    }

    private void enableAddingListItems() {
        binding.fab.setOnClickListener((view -> {
            View dialogView = this.getLayoutInflater().inflate(R.layout.popup_layout, (ViewGroup) requireView(), false);
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireActivity());
            dialogBuilder.setView(dialogView);
            dialogBuilder.setTitle("Add new item");
            dialogBuilder.setPositiveButton("Add", (dialogInterface, i) -> {
                String popupItemName = ((EditText) dialogView.findViewById(R.id.popup_item_name)).getText().toString();
                String popupItemValue = ((EditText) dialogView.findViewById(R.id.popup_item_amount)).getText().toString();
                String popupItemAmount = ((EditText) dialogView.findViewById(R.id.popup_item_value)).getText().toString();
                ListItem newItem = new ListItem(popupItemName, Integer.parseInt(popupItemValue), Double.parseDouble(popupItemAmount));
                listAdapter.addItem(newItem);
            });
            dialogBuilder.setCancelable(true);
            dialogBuilder.show();
        }));
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            authManager.refreshUser();
            Toast.makeText(this.getContext(), "Successfully logged in", Toast.LENGTH_SHORT).show();
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            Toast.makeText(this.getContext(), "Failed to log in", Toast.LENGTH_SHORT).show();
        }
    }

}