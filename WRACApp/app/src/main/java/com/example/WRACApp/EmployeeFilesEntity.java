package com.example.wolseytechhr;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "employee_files_table")
public class EmployeeFilesEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String fileCenterId = " ";
    public String fileName = " ";
    public String dateUploaded = " ";
    public String filePath = " ";
    public String firstName = " ";
    public String lastName = " ";
    public String fileCenterType = "h";

    // Constructor
    public EmployeeFilesEntity(String fileCenterId, String fileName, String dateUploaded, String filePath,
                               String firstName, String lastName, String fileCenterType) {
        this.fileCenterId = fileCenterId;
        this.fileName = fileName;
        this.dateUploaded = dateUploaded;
        this.filePath = filePath;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fileCenterType = fileCenterType;
    }
    // Getter method for file name
    public String getFileName() {
        return fileName;
    }
}
