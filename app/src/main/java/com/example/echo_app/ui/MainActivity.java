package com.example.echo_app.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.echo_app.R;

/**
 * MainActivity.java
 *
 * Role: Main entry point activity for the EchoApp application.
 *
 * This is the primary Activity that users see when launching the app.
 * It serves as a container for fragments and manages the overall app navigation flow.
 *
 * Responsibilities:
 * - Inflate main layout
 * - Initialize UI components
 * - Delegate business logic to appropriate ViewModels
 * - Observe LiveData changes from ViewModels
 *
 * Part of: UI Layer (MVVM Architecture)
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: Initialize UI components and ViewModels
    }
}

