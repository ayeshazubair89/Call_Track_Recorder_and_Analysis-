package com.example.callhistory.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.Manifest;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.callhistory.FilterDialogFragment;
import com.example.callhistory.R;
import com.example.callhistory.model.CallLogItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CallLogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private static final int CALL_PERMISSION_REQUEST_CODE = 123;
    private static List<CallLogItem> callLogItems;
    private Context context;
    private List<CallLogItem> filteredCallLogItems;
    public CallLogAdapter(List<CallLogItem> callLogItems) {
        this.callLogItems = callLogItems != null ? callLogItems : new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == VIEW_TYPE_HEADER) {
            View headerView = LayoutInflater.from(context).inflate(R.layout.call_log_header_item, parent, false);
            return new HeaderViewHolder(headerView);
        } else {
            View itemView = LayoutInflater.from(context).inflate(R.layout.call_log_item, parent, false);
            return new ViewHolder(itemView);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CallLogItem item = callLogItems.get(position);

        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.dateTextView.setText(item.getDate());
        } else if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;

            viewHolder.nameTextView.setText(item.getName());
            viewHolder.numberTextView.setText(item.getNumber());
            viewHolder.timeTextView.setText(formatTime(item.getTime()));
            viewHolder.typeTextView.setText(item.getCallType());

            int colorResId = R.color.default_call_type_color;
            switch (item.getCallType()) {
                case "1":
                    colorResId = R.color.missed_call_color;
                    break;
                case "2":
                    colorResId = R.color.incoming_call_color;
                    break;
                case "3":
                    colorResId = R.color.outgoing_call_color;
                    break;
            }

            int color = ContextCompat.getColor(context, colorResId);
            viewHolder.typeTextView.setTextColor(color);

            char initialLetter = item.getName().charAt(0);
            String initialLetterString = String.valueOf(initialLetter);
        }
    }

    private String formatTime(String seconds) {
        int totalSeconds = Integer.parseInt(seconds);
        int minutes = totalSeconds / 60;
        int remainingSeconds = totalSeconds % 60;

        return String.format(Locale.getDefault(), "%02d:%02d", minutes, remainingSeconds);
    }

    @Override
    public int getItemCount() {
        return callLogItems.size();
    }
    public void filter(String query) {
        filteredCallLogItems.clear();
        if (query.isEmpty()) {
            filteredCallLogItems.addAll(callLogItems);
        } else {
            query = query.toLowerCase();
            for (CallLogItem item : callLogItems) {
                if (item.getName().toLowerCase().contains(query)) {
                    filteredCallLogItems.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }
    @Override
    public int getItemViewType(int position) {
        return (callLogItems.get(position).getName() == null) ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    public void updateData(List<CallLogItem> filteredData) {
        callLogItems.clear();
        callLogItems.addAll(filteredData);
        notifyDataSetChanged();
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView dateTextView;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        TextView numberTextView;
        TextView dateTextView;
        TextView timeTextView;
        TextView typeTextView;
        ImageView contactInitialTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            numberTextView = itemView.findViewById(R.id.numberTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            typeTextView = itemView.findViewById(R.id.typeTextView);
            contactInitialTextView = itemView.findViewById(R.id.contactInitialImageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showContactOptionsDialog(callLogItems.get(getAdapterPosition()));
                }
            });

        }

        private void showContactOptionsDialog(CallLogItem callLogItem) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(callLogItem.getName());

            View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_dialog_layout, null);
            builder.setView(dialogView);

            ImageView callIcon = dialogView.findViewById(R.id.callIcon);
            ImageView textIcon = dialogView.findViewById(R.id.textIcon);
            ImageView deleteIcon = dialogView.findViewById(R.id.deleteIcon);

            callIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phoneNumber = callLogItem.getNumber();
                    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));

                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        context.startActivity(callIntent);
                    } else {
                        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION_REQUEST_CODE);
                    }
                }
            });

            textIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phoneNumber = callLogItem.getNumber();
                    Intent textIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
                    context.startActivity(textIntent);
                }
            });

            deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteConfirmationDialog(callLogItem);
                }
            });

            builder.setPositiveButton("Close", null);

            AlertDialog dialog = builder.create();
            dialog.show();
        }

        private void showDeleteConfirmationDialog(CallLogItem callLogItem) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Delete Call Log");
            builder.setMessage("Are you sure you want to delete this call log entry?");

            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteCallLogEntry(callLogItem);
                }
            });

            builder.setNegativeButton("Cancel", null);

            AlertDialog dialog = builder.create();
            dialog.show();
        }

        private void deleteCallLogEntry(CallLogItem callLogItem) {
            String selection = CallLog.Calls.NUMBER + " = ? AND " + CallLog.Calls.DATE + " = ?";
            String[] selectionArgs = new String[]{callLogItem.getNumber(), String.valueOf(callLogItem.getTime())};
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, selection, selectionArgs);
                callLogItems.remove(callLogItem);
                notifyDataSetChanged();
            } else {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_CALL_LOG}, CALL_PERMISSION_REQUEST_CODE);
            }
        }
    }
}
