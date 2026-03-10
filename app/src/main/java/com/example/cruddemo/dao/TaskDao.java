package com.example.cruddemo.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.cruddemo.model.Task;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    long insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("SELECT * FROM tasks ORDER BY lastUpdatedOn DESC")
    List<Task> getAllTasks();

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    Task getTaskById(int id);

    @Query("SELECT * FROM tasks WHERE taskTitle LIKE '%' || :query || '%' OR taskDescription LIKE '%' || :query || '%' OR taskStatus LIKE '%' || :query || '%' OR taskRemarks LIKE '%' || :query || '%' ORDER BY lastUpdatedOn DESC")
    List<Task> searchTasks(String query);

    @Query("SELECT * FROM tasks WHERE createdById = :userId ORDER BY lastUpdatedOn DESC")
    List<Task> getTasksByUser(int userId);

    @Query("SELECT * FROM tasks WHERE createdById = :userId AND (taskTitle LIKE '%' || :query || '%' OR taskDescription LIKE '%' || :query || '%' OR taskStatus LIKE '%' || :query || '%' OR taskRemarks LIKE '%' || :query || '%') ORDER BY lastUpdatedOn DESC")
    List<Task> searchTasksByUser(int userId, String query);
}

