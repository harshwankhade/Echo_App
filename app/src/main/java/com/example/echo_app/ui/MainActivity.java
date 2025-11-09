package com.example.echo_app.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.example.echo_app.R;

/**
 * MainActivity.java
 *
 * Role: Main entry point activity for the EchoApp application.
 *
 * ARCHITECTURE: Single-Activity with Fragment-based Navigation
 *
 * This Activity serves as the main container for the Navigation Component.
 * It hosts a NavHostFragment which manages all fragment transitions using
 * the Navigation Graph (nav_graph.xml).
 *
 * Navigation Flow:
 * - User starts at LoginFragment (authentication flow)
 * - After successful login, navigates to ChatListFragment (main screen)
 * - From ChatListFragment, can navigate to:
 *   • ChatFragment (individual conversation)
 *   • ProfileFragment (user profile)
 * - All fragments managed by NavController
 *
 * Responsibilities:
 * - Host NavHostFragment and Navigation Graph
 * - Initialize and manage NavController
 * - Set up action bar back navigation
 * - Manage app-level navigation UI integration
 *
 * Part of: UI Layer (MVVM Architecture)
 *
 * @see androidx.navigation.NavController
 * @see androidx.navigation.fragment.NavHostFragment
 * @see NavigationUI
 */
public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Navigation Component
        initializeNavigation();
    }

    /**
     * Initialize NavController and Navigation UI integration.
     *
     * Sets up:
     * - NavHostFragment reference
     * - NavController for navigation management
     * - Action bar back navigation support
     */
    private void initializeNavigation() {
        // Get NavHostFragment from the layout
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            // Get NavController from NavHostFragment
            navController = navHostFragment.getNavController();

            // Set up action bar with NavController for back navigation
            NavigationUI.setupActionBarWithNavController(this, navController);
        }
    }

    /**
     * Override onSupportNavigateUp() to handle back navigation.
     *
     * Called when the back button is pressed in the action bar.
     * Delegates to NavController to handle fragment transitions.
     *
     * @return true if NavController handled the navigation, false otherwise
     */
    @Override
    public boolean onSupportNavigateUp() {
        if (navController != null) {
            return navController.navigateUp() || super.onSupportNavigateUp();
        }
        return super.onSupportNavigateUp();
    }
}

