package com.example.cruddemo.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cruddemo.R;
import com.example.cruddemo.adapter.TaskGridAdapter;
import com.example.cruddemo.database.AppDatabase;
import com.example.cruddemo.model.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class TaskListActivity extends AppCompatActivity {

    private GridView gridView;
    private EditText etSearch;
    private FloatingActionButton fabAdd;
    private ImageButton btnLogout;
    private TextView tvWelcome, tvNoTasks;
    private AppDatabase db;
    private TaskGridAdapter adapter;
    private List<Task> taskList;
    private int currentUserId;
    private String currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        db = AppDatabase.getInstance(this);

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        currentUserId = prefs.getInt("user_id", -1);
        currentUserName = prefs.getString("user_name", "User");

        if (currentUserId == -1) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        gridView = findViewById(R.id.gridViewTasks);
        etSearch = findViewById(R.id.etSearch);
        fabAdd = findViewById(R.id.fabAddTask);
        btnLogout = findViewById(R.id.btnLogout);
        tvWelcome = findViewById(R.id.tvWelcome);
        tvNoTasks = findViewById(R.id.tvNoTasks);

        tvWelcome.setText("Welcome, " + currentUserName + "!");

        taskList = new ArrayList<>();
        adapter = new TaskGridAdapter(this, taskList, new TaskGridAdapter.OnTaskActionListener() {
            @Override
            public void onEdit(Task task) {
                Intent intent = new Intent(TaskListActivity.this, AddEditTaskActivity.class);
                intent.putExtra("task_id", task.getId());
                startActivity(intent);
            }

            @Override
            public void onDelete(Task task) {
                new AlertDialog.Builder(TaskListActivity.this)
                        .setTitle("Delete Task")
                        .setMessage("Are you sure you want to delete \"" + task.getTaskTitle() + "\"?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            db.taskDao().delete(task);
                            loadTasks();
                            Toast.makeText(TaskListActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }

            @Override
            public void onView(Task task) {
                Intent intent = new Intent(TaskListActivity.this, TaskDetailActivity.class);
                intent.putExtra("task_id", task.getId());
                startActivity(intent);
            }
        });
        gridView.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(TaskListActivity.this, AddEditTaskActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(TaskListActivity.this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Logout", (dialog, which) -> {
                        prefs.edit().clear().apply();
                        startActivity(new Intent(TaskListActivity.this, LoginActivity.class));
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchTasks(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }

    private void loadTasks() {
        taskList.clear();
        taskList.addAll(db.taskDao().getAllTasks());
        adapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void searchTasks(String query) {
        taskList.clear();
        if (query.isEmpty()) {
            taskList.addAll(db.taskDao().getAllTasks());
        } else {
            taskList.addAll(db.taskDao().searchTasks(query));
        }
        adapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (taskList.isEmpty()) {
            tvNoTasks.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);
        } else {
            tvNoTasks.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
        }
    }
}

