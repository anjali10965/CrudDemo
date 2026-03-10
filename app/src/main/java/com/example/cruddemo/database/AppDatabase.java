package com.example.cruddemo.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.cruddemo.dao.TaskDao;
import com.example.cruddemo.dao.UserDao;
import com.example.cruddemo.model.Task;
import com.example.cruddemo.model.User;

@Database(entities = {User.class, Task.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract UserDao userDao();
    public abstract TaskDao taskDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "cruddemo_db"
                    ).allowMainThreadQueries()
                     .fallbackToDestructiveMigration()
                     .build();
                }
            }
        }
        return INSTANCE;
    }
}

