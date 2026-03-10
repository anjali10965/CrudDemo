package com.example.cruddemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.cruddemo.R;
import com.example.cruddemo.model.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskGridAdapter extends BaseAdapter {

    private final Context context;
    private final List<Task> taskList;
    private final OnTaskActionListener listener;

    public interface OnTaskActionListener {
        void onEdit(Task task);
        void onDelete(Task task);
        void onView(Task task);
    }

    public TaskGridAdapter(Context context, List<Task> taskList, OnTaskActionListener listener) {
        this.context = context;
        this.taskList = taskList;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return taskList.size();
    }

    @Override
    public Object getItem(int position) {
        return taskList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return taskList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_task_grid, parent, false);
            holder = new ViewHolder();
            holder.tvTitle = convertView.findViewById(R.id.tvItemTitle);
            holder.tvStatus = convertView.findViewById(R.id.tvItemStatus);
            holder.tvDueDate = convertView.findViewById(R.id.tvItemDueDate);
            holder.tvDescription = convertView.findViewById(R.id.tvItemDescription);
            holder.tvRemarks = convertView.findViewById(R.id.tvItemRemarks);
            holder.tvCreatedBy = convertView.findViewById(R.id.tvItemCreatedBy);
            holder.tvCreatedOn = convertView.findViewById(R.id.tvItemCreatedOn);
            holder.tvUpdatedOn = convertView.findViewById(R.id.tvItemUpdatedOn);
            holder.tvUpdatedBy = convertView.findViewById(R.id.tvItemUpdatedBy);
            holder.btnEdit = convertView.findViewById(R.id.btnItemEdit);
            holder.btnDelete = convertView.findViewById(R.id.btnItemDelete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Task task = taskList.get(position);

        holder.tvTitle.setText(task.getTaskTitle());
        holder.tvDescription.setText(task.getTaskDescription());
        holder.tvRemarks.setText("Remarks: " + (task.getTaskRemarks() != null && !task.getTaskRemarks().isEmpty()
                ? task.getTaskRemarks() : "N/A"));
        holder.tvDueDate.setText("Due: " + task.getTaskDueDate());
        holder.tvStatus.setText(task.getTaskStatus());
        holder.tvCreatedBy.setText("By: " + task.getCreatedByName());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
        holder.tvCreatedOn.setText("Created: " + sdf.format(new Date(task.getCreatedOn())));
        holder.tvUpdatedOn.setText("Updated: " + sdf.format(new Date(task.getLastUpdatedOn())));
        holder.tvUpdatedBy.setText("Updated by: " + task.getLastUpdatedByName() + " (ID: " + task.getLastUpdatedById() + ")");

        // Color code status
        switch (task.getTaskStatus()) {
            case "Pending":
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
                break;
            case "In Progress":
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_in_progress);
                break;
            case "Completed":
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_completed);
                break;
        }

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(task));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(task));

        convertView.setOnClickListener(v -> listener.onView(task));

        return convertView;
    }

    static class ViewHolder {
        TextView tvTitle, tvStatus, tvDueDate, tvDescription, tvRemarks, tvCreatedBy, tvCreatedOn, tvUpdatedOn, tvUpdatedBy;
        ImageButton btnEdit, btnDelete;
    }
}

