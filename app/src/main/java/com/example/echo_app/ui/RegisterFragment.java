package com.example.echo_app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.echo_app.R;

/**
 * RegisterFragment.java
 *
 * Role: Fragment responsible for user registration and account creation.
 *
 * ARCHITECTURE: Fragment in Single-Activity Navigation Structure
 *
 * This fragment handles new user registration. Users can create an account
 * with email, password, and profile information. After successful registration,
 * they are logged in and navigate to the chat list.
 *
 * Navigation Flow:
 * - Accessed from LoginFragment (for new users)
 * - Action: registerFragment → loginFragment (if user has account)
 * - Action: registerFragment → chatListFragment (after registration)
 *
 * Responsibilities:
 * - Inflate registration layout
 * - Capture user registration data (email, password, name, etc.)
 * - Validate user input
 * - Trigger registration/sign-up requests
 * - Display registration error messages
 * - Navigate to chat screen upon successful registration
 * - Provide link back to login screen
 *
 * ViewModels:
 * - LoginViewModel: Handles registration logic
 *   (will be implemented in Phase 2)
 *
 * Part of: UI Layer (MVVM Architecture)
 *
 * @see LoginViewModel
 * @see Navigation
 */
public class RegisterFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate registration layout
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO: Initialize UI components (buttons, text fields)
        // TODO: Set up LoginViewModel observation
        // TODO: Handle register button click
        // TODO: Handle back to login button click

        // Example navigation code (will be used in Phase 2):
        /*
        registerButton.setOnClickListener(v -> {
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();
            String name = nameField.getText().toString();

            // Call LoginViewModel.register(email, password, name)
            // On success, navigate to chatListFragment:
            Navigation.findNavController(view).navigate(
                R.id.action_registerFragment_to_chatListFragment
            );
        });

        backToLoginButton.setOnClickListener(v -> {
            // Navigate back to LoginFragment
            Navigation.findNavController(view).navigate(
                R.id.action_registerFragment_to_loginFragment
            );
        });
        */
    }
}

