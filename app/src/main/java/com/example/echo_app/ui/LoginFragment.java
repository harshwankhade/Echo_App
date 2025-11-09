package com.example.echo_app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.echo_app.R;

/**
 * LoginFragment.java
 *
 * Role: Fragment responsible for user authentication and login flow.
 *
 * ARCHITECTURE: Fragment in Single-Activity Navigation Structure
 *
 * This fragment is the entry point for unauthenticated users.
 * It presents the login UI where users can authenticate using email/password.
 *
 * Navigation Flow:
 * - Starting destination in nav_graph.xml
 * - Action: loginFragment → registerFragment (for new users)
 * - Action: loginFragment → chatListFragment (after successful login)
 *
 * Responsibilities:
 * - Inflate login layout
 * - Capture user credentials (email, password)
 * - Trigger login/authentication requests
 * - Display authentication error messages
 * - Navigate to main chat screen upon successful login
 * - Provide link to registration for new users
 *
 * ViewModels:
 * - LoginViewModel: Handles authentication logic
 *   (will be implemented in Phase 2)
 *
 * Part of: UI Layer (MVVM Architecture)
 *
 * @see LoginViewModel
 * @see Navigation
 */
public class LoginFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate login layout
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO: Initialize UI components (buttons, text fields)
        // TODO: Set up LoginViewModel observation
        // TODO: Handle login button click
        // TODO: Handle register button click to navigate to RegisterFragment

        // Example navigation code (will be used in Phase 2):
        /*
        loginButton.setOnClickListener(v -> {
            // Call LoginViewModel.login(email, password)
            // On success, navigate to chatListFragment:
            Navigation.findNavController(view).navigate(
                R.id.action_loginFragment_to_chatListFragment
            );
        });

        registerButton.setOnClickListener(v -> {
            // Navigate to RegisterFragment
            Navigation.findNavController(view).navigate(
                R.id.action_loginFragment_to_registerFragment
            );
        });
        */
    }
}

