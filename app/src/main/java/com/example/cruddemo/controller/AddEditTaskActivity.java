package com.example.cruddemo.controller;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cruddemo.R;
import com.example.cruddemo.database.AppDatabase;
import com.example.cruddemo.model.Task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEditTaskActivity extends AppCompatActivity {

    private EditText etTitle, etDescription, etDueDate, etRemarks;
    private Spinner spinnerStatus;
    private Button btnSave;
    private ImageButton btnBack;
    private TextView tvHeader;
    private AppDatabase db;

    private int taskId = -1;
    private Task existingTask;
    private int currentUserId;
    private String currentUserName;

    private final String[] statusOptions = {"Pending", "In Progress", "Completed"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        db = AppDatabase.getInstance(this);

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        currentUserId = prefs.getInt("user_id", -1);
        currentUserName = prefs.getString("user_name", "User");

        etTitle = findViewById(R.id.etTaskTitle);
        etDescription = findViewById(R.id.etTaskDescription);
        etDueDate = findViewById(R.id.etTaskDueDate);
        etRemarks = findViewById(R.id.etTaskRemarks);
        spinnerStatus = findViewById(R.id.spinnerTaskStatus);
        btnSave = findViewById(R.id.btnSaveTask);
        btnBack = findViewById(R.id.btnBack);
        tvHeader = findViewById(R.id.tvFormHeader);

        // Setup status spinner
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, statusOptions);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        // Date picker for due date
        etDueDate.setOnClickListener(v -> showDatePicker());
        etDueDate.setFocusable(false);

        // Check if editing
        taskId = getIntent().getIntExtra("task_id", -1);
        if (taskId != -1) {
            tvHeader.setText("Edit Task");
            loadTask();
        } else {
            tvHeader.setText("Add New Task");
        }

        btnSave.setOnClickListener(v -> saveTask());

        btnBack.setOnClickListener(v -> finish());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    etDueDate.setText(sdf.format(selected.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void loadTask() {
        existingTask = db.taskDao().getTaskById(taskId);
        if (existingTask != null) {
            etTitle.setText(existingTask.getTaskTitle());
            etDescription.setText(existingTask.getTaskDescription());
            etDueDate.setText(existingTask.getTaskDueDate());
            etRemarks.setText(existingTask.getTaskRemarks());

            // Set spinner selection
            for (int i = 0; i < statusOptions.length; i++) {
                if (statusOptions[i].equals(existingTask.getTaskStatus())) {
                    spinnerStatus.setSelection(i);
                    break;
                }
            }
        }
    }

    private void saveTask() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String dueDate = etDueDate.getText().toString().trim();
        String status = spinnerStatus.getSelectedItem().toString();
        String remarks = etRemarks.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            etTitle.setError("Title is required");
            etTitle.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(description)) {
            etDescription.setError("Description is required");
            etDescription.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(dueDate)) {
            etDueDate.setError("Due date is required");
            etDueDate.requestFocus();
            return;
        }

        long currentTime = System.currentTimeMillis();

        if (taskId != -1 && existingTask != null) {
            // Update
            existingTask.setTaskTitle(title);
            existingTask.setTaskDescription(description);
            existingTask.setTaskDueDate(dueDate);
            existingTask.setTaskStatus(status);
            existingTask.setTaskRemarks(remarks);
            existingTask.setLastUpdatedOn(currentTime);
            existingTask.setLastUpdatedByName(currentUserName);
            existingTask.setLastUpdatedById(currentUserId);
            db.taskDao().update(existingTask);
            Toast.makeText(this, "Task updated successfully!", Toast.LENGTH_SHORT).show();
        } else {
            // Create
            Task task = new Task();
            task.setTaskTitle(title);
            task.setTaskDescription(description);
            task.setTaskDueDate(dueDate);
            task.setTaskStatus(status);
            task.setTaskRemarks(remarks);
            task.setCreatedOn(currentTime);
            task.setLastUpdatedOn(currentTime);
            task.setCreatedByName(currentUserName);
            task.setCreatedById(currentUserId);
            task.setLastUpdatedByName(currentUserName);
            task.setLastUpdatedById(currentUserId);
            db.taskDao().insert(task);
            Toast.makeText(this, "Task created successfully!", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}

