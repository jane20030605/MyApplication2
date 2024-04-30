package com.example.myapplication.ui.Calender;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashSet;
import java.util.Set;

public class CalenderFragment extends Fragment {

    private TextView eventTextView;
    private Set<String> eventsSet;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calender, container, false);

        eventTextView = root.findViewById(R.id.eventTextView);

        FloatingActionButton fabAddCircularButton = root.findViewById(R.id.fabAddEvent);
        fabAddCircularButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.nav_calender_thing);
            }
        });

        eventTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDeleteDialog(eventTextView.getText().toString(), v);
            }
        });

        displayEvents();

        return root;
    }

    private void displayEvents() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyCalendar", Context.MODE_PRIVATE);
        eventsSet = sharedPreferences.getStringSet("events", new HashSet<>());

        StringBuilder eventsText = new StringBuilder();
        for (String event : eventsSet) {
            eventsText.append(event).append("\n\n");
        }
        eventTextView.setText(eventsText.toString());
    }

    private void showEditDeleteDialog(final String eventText, View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("選擇操作")
                .setMessage("您要編輯還是刪除此事件?")
                .setPositiveButton("編輯", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Bundle bundle = new Bundle();
                        bundle.putString("eventDetails", eventText);
                        bundle.putBoolean("isEditing", true);
                        Navigation.findNavController(requireView()).navigate(R.id.nav_calender_thing, bundle);
                    }
                })
                .setNegativeButton("刪除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eventsSet.remove(eventText);
                        saveEventsToSharedPreferences();
                        displayEvents();
                    }
                })
                .setNeutralButton("取消", null)
                .create()
                .show();
    }

    private void saveEventsToSharedPreferences() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyCalendar", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("events", eventsSet);
        editor.apply();
    }
}
