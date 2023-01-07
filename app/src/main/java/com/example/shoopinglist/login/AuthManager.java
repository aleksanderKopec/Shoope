package com.example.shoopinglist.login;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

/**
 * Odpowiada za obsługe autentykacji i autoryzacji użytkowników.
 */
public class AuthManager {

    private static AuthManager INSTANCE;

    private final ActivityResultLauncher<Intent> signInLauncher;

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private AuthManager(ActivityResultLauncher<Intent> signInLauncher) {
        this.signInLauncher = signInLauncher;
    }

    /**
     * Tworzy statyczną instancje AuthManager'a. Co do zasady powinna być wywoływana raz w ciągu
     * działania programu, ale w niektórych okolicznościach może być potrzebne jej odświeżenie.
     *
     * @param signInLauncher Launcher używany do uruchomienia Activity logowania użytkownika.
     * @return Nowo utworzona instancja klasy AuthManager.
     */
    public static AuthManager createInstance(ActivityResultLauncher<Intent> signInLauncher) {
        INSTANCE = new AuthManager(signInLauncher);
        return INSTANCE;
    }

    /**
     * Zwraca wcześniej utworzoną instancje.
     *
     * @return Instancja klasy AuthManager.
     */
    public static AuthManager getInstance() {
        if (INSTANCE == null) {
            throw new RuntimeException("AuthManager has not been instantiated yet.");
        }
        return INSTANCE;
    }

    /**
     * Uruchamia Activity odpowiedzialne za logowanie/rejestracje użytkownika
     */
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

    /**
     * Wylogowuje użytkownika
     *
     * @param context Kontekst w którym powinna być wyświetlona informacja o wylogowaniu.
     */
    public void logout(Context context) {
        mAuth.signOut();
        Toast.makeText(context, "Succesfully logged out", Toast.LENGTH_SHORT).show();
    }
}