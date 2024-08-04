package com.example.callhistory;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.callhistory.Adapter.ContactAdapter;
import com.example.callhistory.model.ContactItem; // Make sure to import the correct model class

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class Contacts extends Fragment {

    private static final int PERMISSION_REQUEST_READ_CONTACTS = 123;
    private RecyclerView recyclerView;
    private ContactAdapter adapter;
    private List<ContactItem> contactItemList;
    private SearchView searchView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.contact_fragment, container, false);
        searchView = rootView.findViewById(R.id.search_view);
        recyclerView = rootView.findViewById(R.id.recycler_view_contacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        contactItemList = fetchContacts(); // Fetch contacts and call types
        adapter = new ContactAdapter(contactItemList);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    private List<ContactItem> fetchContacts() {
        List<ContactItem> contactsList = new ArrayList<>();
        HashSet<String> uniqueNumbers = new HashSet<>();
        Cursor cursor = requireContext().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                @SuppressLint("Range") String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                // Check if the number is already added to the HashSet
                String normalizedNumber = normalizeNumber(number);

                if (!uniqueNumbers.contains(normalizedNumber)) {
                    // Add the contact to the list
                    int totalIncomingCalls = calculateTotalIncomingCallsForContact(number);
                    int totalOutgoingCalls = calculateTotalOutgoingCallsForContact(number);
                    int totalMissedCalls = calculateTotalMissedCallsForContact(number);

                    ContactItem contactItem = new ContactItem(name, number);
                    contactItem.setTotalIncomingCalls(totalIncomingCalls);
                    contactItem.setTotalOutgoingCalls(totalOutgoingCalls);
                    contactItem.setTotalMissedCalls(totalMissedCalls);

                    contactsList.add(contactItem);

                    // Add the normalized number to the HashSet
                    uniqueNumbers.add(normalizedNumber);
                }
            }
            cursor.close();
        }
        setupSearchView();
        return contactsList;
    }
    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });
    }
    private int calculateTotalIncomingCallsForContact(String contactNumber) {
        int totalIncomingCalls = 0;

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            // Define the projection to retrieve call details
            String[] projection = {
                    CallLog.Calls.TYPE
            };

            // Define the selection criteria to retrieve incoming calls for the given contactNumber
            String selection = CallLog.Calls.NUMBER + " = ? AND " + CallLog.Calls.TYPE + " = ?";
            String[] selectionArgs = {contactNumber, String.valueOf(CallLog.Calls.INCOMING_TYPE)};

            // Query the call history using a cursor
            Cursor cursor = requireContext().getContentResolver().query(
                    CallLog.Calls.CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    null
            );

            if (cursor != null) {
                totalIncomingCalls = cursor.getCount();
                cursor.close();
            }
        }

        return totalIncomingCalls;
    }

    private int calculateTotalOutgoingCallsForContact(String contactNumber) {
        int totalOutgoingCalls = 0;

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            // Define the projection to retrieve call details
            String[] projection = {
                    CallLog.Calls.TYPE
            };

            // Define the selection criteria to retrieve outgoing calls for the given contactNumber
            String selection = CallLog.Calls.NUMBER + " = ? AND " + CallLog.Calls.TYPE + " = ?";
            String[] selectionArgs = {contactNumber, String.valueOf(CallLog.Calls.OUTGOING_TYPE)};

            // Query the call history using a cursor
            Cursor cursor = requireContext().getContentResolver().query(
                    CallLog.Calls.CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    null
            );

            if (cursor != null) {
                totalOutgoingCalls = cursor.getCount();
                cursor.close();
            }
        }

        return totalOutgoingCalls;
    }

    private int calculateTotalMissedCallsForContact(String contactNumber) {
        int totalMissedCalls = 0;

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            // Define the projection to retrieve call details
            String[] projection = {
                    CallLog.Calls.TYPE
            };

            // Define the selection criteria to retrieve missed calls for the given contactNumber
            String selection = CallLog.Calls.NUMBER + " = ? AND " + CallLog.Calls.TYPE + " = ?";
            String[] selectionArgs = {contactNumber, String.valueOf(CallLog.Calls.MISSED_TYPE)};

            // Query the call history using a cursor
            Cursor cursor = requireContext().getContentResolver().query(
                    CallLog.Calls.CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    null
            );

            if (cursor != null) {
                totalMissedCalls = cursor.getCount();
                cursor.close();
            }
        }

        return totalMissedCalls;
    }

    private ArrayList<ContactItem> deduplicateContacts(ArrayList<ContactItem> contactsList) {
        ArrayList<ContactItem> deduplicatedList = new ArrayList<>();
        HashSet<String> uniqueNumbers = new HashSet<>();

        for (ContactItem contact : contactsList) {
            String normalizedNumber = normalizeNumber(contact.getNumber());

            if (!uniqueNumbers.contains(normalizedNumber)) {
                deduplicatedList.add(contact);
                uniqueNumbers.add(normalizedNumber);
            }
        }

        return deduplicatedList;
    }

    private String normalizeNumber(String number) {
        // Normalize the number by removing spaces, dashes, and other formatting
        return number.replaceAll("\\s+", "").replaceAll("-", "");
    }
}
