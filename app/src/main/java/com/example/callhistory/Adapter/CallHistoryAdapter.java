package com.example.callhistory.Adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.callhistory.R;
import com.example.callhistory.model.CallHistoryItem;

import java.util.List;
public class CallHistoryAdapter extends RecyclerView.Adapter<CallHistoryAdapter.ViewHolder> {

    private List<CallHistoryItem> callHistoryList;

    public CallHistoryAdapter(Context context, List<CallHistoryItem> callHistoryList) {
        this.callHistoryList = callHistoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CallHistoryItem item = callHistoryList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return callHistoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView typeTextView;
        private TextView timeTextView;
        private TextView durationTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            typeTextView = itemView.findViewById(R.id.typeTextViewHistory);
            timeTextView = itemView.findViewById(R.id.timeTextViewHistory);
            durationTextView = itemView.findViewById(R.id.durationTextViewHistory);
        }

        public void bind(CallHistoryItem item) {
            typeTextView.setText(item.getCallType());
            timeTextView.setText(item.getFormattedTime());
            durationTextView.setText(item.getFormattedDuration());
        }
    }
}
