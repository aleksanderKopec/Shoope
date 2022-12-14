package com.example.shoopinglist.list;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoopinglist.R;
import com.example.shoopinglist.common.SwipeToDeleteCallback;
import com.example.shoopinglist.database.DatabaseManager;
import com.example.shoopinglist.databinding.FragmentListBinding;
import com.example.shoopinglist.login.AuthManager;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;

/**
 * Klasa odpowiada za wyświetlanie głownego widoku listy elementów
 */
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
        enableSwipeToDeleteAndUndo();
        enableAddingListItems();
        return rootView;
    }

    /**
     * Funkcja odpowiadająca za zapewnienie interakcji przycisków logowania i wylogowywania
     *
     */
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listAdapter = new ListItemAdapter(authManager, getLayoutInflater(), requireView(), requireActivity());
        listRecyclerView.setAdapter(listAdapter);

        binding.buttonLogin.setOnClickListener(view1 -> {
            if (authManager.getUser() == null) {
                authManager.startLoginActivity();
                listAdapter.reloadDatabase();
            } else {
                Toast.makeText(getContext(), "You are already logged in!", Toast.LENGTH_SHORT).show();
            }
        });
        binding.buttonLogin.setText(R.string.LoginButtonText);

        binding.buttonLogout.setOnClickListener(view1 -> {
            if (authManager.getUser() != null) {
                authManager.logout(getContext());
                listAdapter.reloadDatabase();
            } else {
                Toast.makeText(getContext(), "You are not logged in!", Toast.LENGTH_SHORT).show();
            }
        });
        binding.buttonLogout.setText(R.string.LogoutButtonText);

        binding.buttonShare.setText(R.string.Share);
        enableSharingList(binding.buttonShare);
    }

    /**
     * Wprowadza funkcjonalność list udostępniania list pomiędzy innymi użytkownikami
     * @param shareButtonView Przycisk odpowiadający za wyświetlanie okna udostępniania list
     */
    private void enableSharingList(View shareButtonView) {
        shareButtonView.setOnClickListener((view -> {

            if (authManager.getUser() == null) {
                Toast.makeText(getContext(), "You are not logged in!", Toast.LENGTH_SHORT).show();
                return;
            }

            View dialogView = getLayoutInflater().inflate(R.layout.share_popup_layout, (ViewGroup) requireView(), false);
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());

            dialogBuilder.setView(dialogView);
            dialogBuilder.setTitle("Share list");

            TextView popupIdSelf = dialogView.findViewById(R.id.popup_id_self);
            EditText popupIdOther = dialogView.findViewById(R.id.popup_id_other);

            popupIdSelf.setText(authManager.getUser().getUid());

            dialogBuilder.setNeutralButton("Copy ID", ((dialogInterface, i) -> {
                ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("userId", authManager.getUser().getUid());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), "Id copied to clipboard", Toast.LENGTH_SHORT).show();
            }));

            dialogBuilder.setPositiveButton("Connect", (dialogInterface, i) -> {
                String id = popupIdOther.getText().toString();
                if (id.isBlank()) {
                    Toast.makeText(getContext(), "Id cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    listAdapter.databaseManager = new DatabaseManager(id);
                    listAdapter.databaseManager.addDataChangeListener(listAdapter);
                }
            });
            dialogBuilder.setCancelable(true);
            dialogBuilder.show();
        }));
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

    /**
     * Uruchamia funkcjonalność usuwania elementów poprzez przeciągnięcie w prawo lub lewo.
     */
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

    /**
     * Uruchamia funkcjonalność dodawania elementów do listy
     */
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

    /**
     * Obsługuje wynik do logowania do bazy danych Firebase
     * @param result przechowuje o powodzeniu logowania
     */

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            listAdapter.reloadDatabase();
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