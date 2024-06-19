package com.example.wolseytechhr;
import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class Notifications {
    private Context context;

    public Notifications(Context context) {
        this.context = context;
    }

    // Method to display a notification if there are new files
    public void displayFileNotifications(List<EmployeeFilesEntity> existingFiles, List<EmployeeFilesEntity> newFiles) {
        // Check for new files not in the database
        List<EmployeeFilesEntity> newFilesToDisplay = getNewFiles(existingFiles, newFiles);

        // Display notifications for new files
        if (!newFilesToDisplay.isEmpty()) {
            String notificationMessage = buildNotificationMessage(newFilesToDisplay);
            displayMessage(notificationMessage);
        }
    }

    // Method to find new files not in the database
    private List<EmployeeFilesEntity> getNewFiles(List<EmployeeFilesEntity> existingFiles, List<EmployeeFilesEntity> newFiles) {
        List<EmployeeFilesEntity> newFilesToDisplay = new ArrayList<>();

        for (EmployeeFilesEntity newFile : newFiles) {
            if (!existingFiles.contains(newFile)) {
                newFilesToDisplay.add(newFile);
            }
        }

        return newFilesToDisplay;
    }

    // Method to build a notification message
    private String buildNotificationMessage(List<EmployeeFilesEntity> newFiles) {
        StringBuilder messageBuilder = new StringBuilder("NEW FILES ADDED:\n");

        for (EmployeeFilesEntity newFile : newFiles) {
            messageBuilder.append(newFile.getFileName()).append("\n");
        }

        return messageBuilder.toString();
    }

    // Method to display a notification
    public void displayMessage(String message) {
        // IMPLEMENT YOUR NOTIFICATION DISPLAY LOGIC HERE, LIKE USING A TOAST
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    // Method to schedule weekly reminders for updated timesheets
    public void scheduleWeeklyReminder() {
        Timer timer = new Timer();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 12); // Adjust the hour as needed
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // Schedule the reminder to run every week
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Code to remind the user about updated timesheets
                String reminderMessage = "Hey dude, don't forget to check the updated timesheets!";
                displayMessage(reminderMessage);
            }
        }, calendar.getTime(), 7 * 24 * 60 * 60 * 1000); // Repeat every week
    }
}
