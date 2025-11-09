package com.example.echo_app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.echo_app.R;

/**
 * ChatListFragment.java
 *
 * Role: Fragment responsible for displaying user's chat list and conversations.
 *
 * ARCHITECTURE: Fragment in Single-Activity Navigation Structure
 *
 * This is the main screen after successful login. It displays a list of all
 * conversations the user has. Users can view individual conversations or
 * access their profile from here.
 *
 * Navigation Flow:
 * - Accessed after successful login from LoginFragment
 * - Action: chatListFragment → chatFragment (view individual chat)
 * - Action: chatListFragment → profileFragment (view user profile)
 * - Action: chatListFragment → loginFragment (logout)
 *
 * Responsibilities:
 * - Inflate chat list layout with RecyclerView
 * - Display list of conversations
 * - Handle chat selection to open individual chat
 * - Provide profile navigation
 * - Handle logout action
 * - Observe chat list from ChatListViewModel
 *
 * ViewModels:
 * - ChatListViewModel: Manages chat list data and operations
 *   (will be implemented in Phase 2)
 *
 * UI Components:
 * - RecyclerView: Display list of chats
 * - Toolbar: App bar with navigation options
 * - FloatingActionButton: Create new chat (optional)
 * - Menu: Logout and other options
 *
 * Part of: UI Layer (MVVM Architecture)
 *
 * @see ChatListViewModel
 * @see Navigation
 */
public class ChatListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate chat list layout
        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO: Initialize UI components (RecyclerView, buttons)
        // TODO: Set up ChatListViewModel observation
        // TODO: Set up RecyclerView adapter for chat list
        // TODO: Handle chat item click to navigate to ChatFragment
        // TODO: Handle profile navigation
        // TODO: Handle logout action

        // Example navigation code (will be used in Phase 2):
        /*
        chatRecyclerView.setAdapter(chatAdapter);
        chatAdapter.setOnItemClickListener(chat -> {
            // Navigate to ChatFragment with chatId argument
            ChatListFragmentDirections.ActionChatListFragmentToChatFragment action =
                ChatListFragmentDirections.actionChatListFragmentToChatFragment(chat.getId());
            Navigation.findNavController(view).navigate(action);
        });

        profileButton.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(
                R.id.action_chatListFragment_to_profileFragment
            );
        });

        logoutMenuItem.setOnMenuItemClickListener(item -> {
            // Call ChatListViewModel.logout() or LoginViewModel.logout()
            Navigation.findNavController(view).navigate(
                R.id.action_chatListFragment_to_loginFragment
            );
            return true;
        });

        // Observe chat list from ViewModel
        viewModel.getChatList().observe(getViewLifecycleOwner(), chats -> {
            chatAdapter.submitList(chats);
        });
        */
    }
}

