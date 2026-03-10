package com.example.cruddemo.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cruddemo.R;
import com.example.cruddemo.database.AppDatabase;
import com.example.cruddemo.model.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TaskDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvDescription, tvDueDate, tvStatus, tvRemarks,
            tvCreatedOn, tvLastUpdatedOn, tvCreatedBy, tvLastUpdatedBy;
    private Button btnEdit, btnDelete;
    private ImageButton btnBack;
    private AppDatabase db;
    private Task task;
    private int taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        db = AppDatabase.getInstance(this);

        tvTitle = findViewById(R.id.tvDetailTitle);
        tvDescription = findViewById(R.id.tvDetailDescription);
        tvDueDate = findViewById(R.id.tvDetailDueDate);
        tvStatus = findViewById(R.id.tvDetailStatus);
        tvRemarks = findViewById(R.id.tvDetailRemarks);
        tvCreatedOn = findViewById(R.id.tvDetailCreatedOn);
        tvLastUpdatedOn = findViewById(R.id.tvDetailLastUpdatedOn);
        tvCreatedBy = findViewById(R.id.tvDetailCreatedBy);
        tvLastUpdatedBy = findViewById(R.id.tvDetailLastUpdatedBy);
        btnEdit = findViewById(R.id.btnDetailEdit);
        btnDelete = findViewById(R.id.btnDetailDelete);
        btnBack = findViewById(R.id.btnDetailBack);

        taskId = getIntent().getIntExtra("task_id", -1);

        btnBack.setOnClickListener(v -> finish());

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(TaskDetailActivity.this, AddEditTaskActivity.class);
            intent.putExtra("task_id", taskId);
            startActivity(intent);
        });

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(TaskDetailActivity.this)
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete this task?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        if (task != null) {
                            db.taskDao().delete(task);
                            Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTaskDetails();
    }

    private void loadTaskDetails() {
        try {
            task = db.taskDao().getTaskById(taskId);
            if (task == null) {
                Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            tvTitle.setText(task.getTaskTitle() != null ? task.getTaskTitle() : "-");
            tvDescription.setText(task.getTaskDescription() != null && !task.getTaskDescription().isEmpty()
                    ? task.getTaskDescription() : "No description");
            tvDueDate.setText(task.getTaskDueDate() != null ? task.getTaskDueDate() : "-");
            tvStatus.setText(task.getTaskStatus() != null ? task.getTaskStatus() : "Pending");
            tvRemarks.setText(task.getTaskRemarks() != null && !task.getTaskRemarks().isEmpty()
                    ? task.getTaskRemarks() : "No remarks");

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());

            if (task.getCreatedOn() > 0) {
                tvCreatedOn.setText(sdf.format(new Date(task.getCreatedOn())));
            } else {
                tvCreatedOn.setText("-");
            }

            if (task.getLastUpdatedOn() > 0) {
                tvLastUpdatedOn.setText(sdf.format(new Date(task.getLastUpdatedOn())));
            } else {
                tvLastUpdatedOn.setText("-");
            }

            String createdByName = task.getCreatedByName() != null ? task.getCreatedByName() : "Unknown";
            tvCreatedBy.setText(createdByName + " (ID: " + task.getCreatedById() + ")");

            String updatedByName = task.getLastUpdatedByName() != null ? task.getLastUpdatedByName() : "Unknown";
            tvLastUpdatedBy.setText(updatedByName + " (ID: " + task.getLastUpdatedById() + ")");

            // Color code status
            String status = task.getTaskStatus() != null ? task.getTaskStatus() : "";
            switch (status) {
                case "Pending":
                    tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
                    break;
                case "In Progress":
                    tvStatus.setBackgroundResource(R.drawable.bg_status_in_progress);
                    break;
                case "Completed":
                    tvStatus.setBackgroundResource(R.drawable.bg_status_completed);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading task: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}

