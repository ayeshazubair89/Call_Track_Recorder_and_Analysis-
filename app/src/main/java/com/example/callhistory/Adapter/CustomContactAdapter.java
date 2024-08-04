package com.example.callhistory.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.callhistory.R;
import com.example.callhistory.model.CustomContactItem;

import java.util.List;

public class CustomContactAdapter extends RecyclerView.Adapter<CustomContactAdapter.CustomViewHolder> {

    private List<CustomContactItem> contactItems;

    public CustomContactAdapter(List<CustomContactItem> contactItems) {
        this.contactItems = contactItems;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_contact, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        CustomContactItem contactItem = contactItems.get(position);
        holder.contactNameTextView.setText(contactItem.getName());
        holder.callCountTextView.setText(contactItem.getCallCount() + " calls");
    }

    @Override
    public int getItemCount() {
        return contactItems.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView contactNameTextView;
        TextView callCountTextView;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            contactNameTextView = itemView.findViewById(R.id.contactNameTextView);
            callCountTextView = itemView.findViewById(R.id.callCountTextView);
        }
    }
}
