package com.example.callhistory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import com.example.callhistory.Adapter.CallHistoryAdapter;

import java.util.Arrays;
import java.util.List;

// HistoryOnClick.java
public class HistoryOnClick extends AppCompatActivity {
    RecyclerView recyclerView;
    CallHistoryAdapter adapter;

  /*  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_log_history_on_click);

        recyclerView = findViewById(R.id.callHistoryRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String phoneNumber = getIntent().getStringExtra("PHONE_NUMBER");
        String callHistory = getCallHistory(phoneNumber);
        List<String> callHistoryList = Arrays.asList(callHistory.split("\n"));

        adapter = new CallHistoryAdapter(callHistoryList);
        recyclerView.setAdapter(adapter);
    }

    private String getCallHistory(String phoneNumber) {
        // Your implementation here
        return phoneNumber;
    }*/
}