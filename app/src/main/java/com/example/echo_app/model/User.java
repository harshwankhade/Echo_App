package com.example.echo_app.model;

/**
 * User.java
 *
 * Role: Data model representing a user entity in the EchoApp.
 *
 * This POJO (Plain Old Java Object) represents the User entity with all relevant
 * properties. It is used to transfer user data between different layers of the
 * application (Repository, ViewModel, UI).
 *
 * Responsibilities:
 * - Define user data structure
 * - Store user properties (id, name, email, profile, etc.)
 * - Provide getters/setters for data access
 * - Optionally implement serialization for Firebase
 *
 * Part of: Model Layer (MVVM Architecture)
 */
public class User {

    // TODO: Define user properties
    // private String userId;
    // private String email;
    // private String displayName;
    // private String profileImageUrl;
    // private long createdAt;

    public User() {
        // Default constructor for Firebase deserialization
    }

    // TODO: Add constructor with parameters
    // public User(String userId, String email, String displayName) { ... }

    // TODO: Add getters and setters for all properties
}

