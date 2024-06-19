package com.example.wolseytechhr;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * This class represents the Room Database for the application, providing access to DAOs and managing database creation and migration.
 */
@Database(entities = {EmployeeFilesEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    /**
     * Provides access to the EmployeeFilesDao.
     *
     * @return The EmployeeFilesDao instance.
     */
    public abstract EmployeeFilesDao employeeFilesDao();

    /**
     * Provides access to the EmployeeDao.
     *
     * @return The EmployeeDao instance.
     */
    public abstract EmployeeDao employeeDao();

    private static AppDatabase INSTANCE;

    /**
     * Retrieves the singleton instance of the AppDatabase.
     *
     * @param context The application context.
     * @return The AppDatabase instance.
     */
    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            // Create a new instance if it doesn't exist
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "your-database-name")
                    .fallbackToDestructiveMigration() // Recreates the database if migrations fail
                    .build();
        }
        return INSTANCE;
    }
}
