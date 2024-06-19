package com.example.wolseytechhr;

import android.content.Context;
import androidx.room.Room;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * This method is used for managing the database, it has a method to initialize the database, put
 * employee file table into the database and put the employee profile table into the database.
 *
 *
 */
public class DatabaseManager {

    private static AppDatabase appDatabase;

    // Ensure that you initialize the database only once
    public static AppDatabase getAppDatabase(Context context) {
        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "your-database-name")
                    .allowMainThreadQueries() // For simplicity, handle database operations on the main thread
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return appDatabase;
    }


    // Executor for background threading
    private static final Executor executor = Executors.newSingleThreadExecutor();

    /**
     * This method is used to insert the employee file data into the users database. It
     * inserts the data but also deletes the old saved data so there wont be duplicate files
     * that build up.
     *
     * @param employeeFilesEntities
     * @param context
     */
    public static void insertFileDataIntoDatabase(List<EmployeeFilesEntity> employeeFilesEntities, Context context) {
        executor.execute(() -> {
            AppDatabase appDatabase = getAppDatabase(context);
            appDatabase.employeeFilesDao().deleteAllEmployeeFiles();
            appDatabase.employeeFilesDao().insertAllEmployeeFiles(employeeFilesEntities);
        });
    }

    /**
     * This method is used to insert data into the employee profile part of the database
     *
     * @param employeeEntity
     * @param context
     */
    public static void insertEmployeeData(EmployeeEntity employeeEntity, Context context) {
        executor.execute(() -> {
            AppDatabase appDatabase = getAppDatabase(context);
         //   appDatabase.employeeDao().insertEmployee(employeeEntity);
        });
    }
}
