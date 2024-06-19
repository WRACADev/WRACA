package com.example.wolseytechhr;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EmployeeFilesDao {
    @Insert
    void insertAllEmployeeFiles(List<EmployeeFilesEntity> employeeFilesEntity);

    @Query("SELECT * FROM employee_files_table")
    List<EmployeeFilesEntity> getAllEmployeeFiles();

    @Query("DELETE FROM employee_files_table")
    void deleteAllEmployeeFiles();
}
