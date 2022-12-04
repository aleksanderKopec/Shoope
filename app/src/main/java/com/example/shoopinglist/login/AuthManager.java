package com.example.shoopinglist.login;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class AuthManager {

    private static AuthManager INSTANCE;

    private final ActivityResultLauncher<Intent> signInLauncher;

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private AuthManager(ActivityResultLauncher<Intent> signInLauncher) {
        this.signInLauncher = signInLauncher;
    }

    public static AuthManager createInstance(ActivityResultLauncher<Intent> signInLauncher) {
        INSTANCE = new AuthManager(signInLauncher);
        return INSTANCE;
    }

    public static AuthManager getInstance() {
        if (INSTANCE == null) {
            throw new RuntimeException("AuthManager has not been instantiated yet.");
        }
        return INSTANCE;
    }

    public void startLoginActivity() {
        List<AuthUI.IdpConfig> providers = List.of(
                new AuthUI.IdpConfig.EmailBuilder().build()
        );

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();
        signInLauncher.launch(signInIntent);
    }

    public FirebaseUser getUser() {
        return mAuth.getCurrentUser();
    }

    public void logout(Context context) {
        mAuth.signOut();
        Toast.makeText(context, "Succesfully logged out", Toast.LENGTH_SHORT).show();
    }
}