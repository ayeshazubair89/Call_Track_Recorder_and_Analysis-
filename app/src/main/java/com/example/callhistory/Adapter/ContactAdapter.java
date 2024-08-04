package com.example.callhistory.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.callhistory.R;
import com.example.callhistory.model.ContactItem;
import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private List<ContactItem> contactItemList;
    private List<ContactItem> originalContactItemList;

    public ContactAdapter(List<ContactItem> contactItemList) {
        this.contactItemList = contactItemList;
        this.originalContactItemList = new ArrayList<>(contactItemList); // Make a copy of the original list
    }

    public void filter(String query) {
        contactItemList.clear();

        if (query.isEmpty()) {
            contactItemList.addAll(originalContactItemList);
        } else {
            query = query.toLowerCase();
            for (ContactItem contact : originalContactItemList) {
                if (contact.getName().toLowerCase().contains(query) || contact.getNumber().contains(query)) {
                    contactItemList.add(contact);
                }
            }
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contacts, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ContactItem contactItem = contactItemList.get(position);
        holder.nameTextView.setText(contactItem.getName());
        holder.numberTextView.setText(contactItem.getNumber());
        holder.totalIncomingCallsTextView.setText(String.valueOf(contactItem.getTotalIncomingCalls()));
        holder.totalMissedCallsTextView.setText(String.valueOf(contactItem.getTotalMissedCalls()));
        holder.totalOutgoingCallsTextView.setText(String.valueOf(contactItem.getTotalOutgoingCalls()));
    }

    @Override
    public int getItemCount() {
        return contactItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, numberTextView;
        TextView totalIncomingCallsTextView, totalMissedCallsTextView, totalOutgoingCallsTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.text_name);
            numberTextView = itemView.findViewById(R.id.text_number);
            totalIncomingCallsTextView = itemView.findViewById(R.id.total_incoming_calls);
            totalMissedCallsTextView = itemView.findViewById(R.id.total_missed_calls);
            totalOutgoingCallsTextView = itemView.findViewById(R.id.total_outgoing_calls);
        }
    }
}
