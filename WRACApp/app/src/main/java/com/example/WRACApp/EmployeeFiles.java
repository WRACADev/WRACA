package com.example.wolseytechhr;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Button;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * This class is used to retrieve the employees file information from the servers. This works in
 * a 3 step process, first creating a link to the page that will have their information,
 * then scraping the page that that link led to, then this class will clean the data putting it
 * into a list of arrays where each array in the list contains the information for one file.
 * If the user is offline instead of this information being pulled from the servers the
 * class will try to pull it from the local database on this phone, if there is data it will pull it
 * and convert it into a list of arrays where each array contains the info for one file.
 *
 *
 * Important methods ***
 *
 *
 * getEmployeeInfoFromDatabase() - gets info from the local database
 * findUserFileDataLink() - makes link to the server
 * getUserRawInfoFromServer() - gets the raw data from the server
 * convertRawInfo() - cleans raw info from server
 *
 */
public class EmployeeFiles {
    private final String link = "https://hr-demo.wolsey-tech.com";
    private String linkToGetProfileInfo;
    private String auth_code;
    private String rawFileInfo;
    private String linkToPdf= "https://hr-demo.wolsey-tech.com/file_center/";
    public String getAuth_code(){return auth_code; }
    private Context context;  // Add a context field

    // Add a constructor to initialize DataRetrieval, EmployeeFilesDao, and context
    public EmployeeFiles(String auth_code,  Context context) {
        this.auth_code = auth_code;
        this.context = context;
    }

    /**
     * This is the method that is being used in other classes to retrieve file information.
     * It checks if the user is online and if the user is online then it will grab the users
     * file data from the internet, however if the user is offline then the method will try to
     * grab data from the local database.
     *
     * @param context
     * @return
     */
    public List<String[]> retrieveInfo(Context context) {
        // Checking internet
        if (isOnline(context)) {
            getUserRawInfoFromServer();
            // Continue with the remaining code as needed
            List<String[]> convertedInfo = convertRawInfo();

            DatabaseManager.insertFileDataIntoDatabase(convertStringArrayToLocalData(convertedInfo), context);
            // returning the file data from online
            return convertedInfo;
        }
        else {
            // Fetch data from the local database if offline
            List<EmployeeFilesEntity> localData = getEmployeeInfoFromDatabase();
            // returning the file data from the database
            return convertLocalDataToStringArray(localData);
        }
    }

    /**
     * This method is used to retrieve employee information from the local database
     *
     * @return
     */
    private List<EmployeeFilesEntity> getEmployeeInfoFromDatabase() {
        final List<EmployeeFilesEntity>[] result = new List[]{null};

        // Create a new thread
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Inside the new thread, perform the database operation
                AppDatabase appDatabase = AppDatabase.getInstance(context);
                result[0] = appDatabase.employeeFilesDao().getAllEmployeeFiles();
            }
        });

        // Start the thread
        thread.start();

        // Wait for the thread to finish
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Return the result obtained in the new thread
        return result[0];
    }

    /**
     * This method is used to convert the saved data that is on the device into a List<String[]>
     *
     * @param localData
     * @return
     */
    private List<String[]> convertLocalDataToStringArray(List<EmployeeFilesEntity> localData) {
        List<String[]> result = new ArrayList<>();
        for (EmployeeFilesEntity entity : localData) {
            String[] convertedData = new String[]{
                    entity.fileCenterId,
                    entity.fileName,
                    entity.dateUploaded,
                    entity.filePath,
                    entity.firstName,
                    entity.lastName,
                    entity.fileCenterType,

            };

            result.add(convertedData);
        }
        return result;
    }

    private List<EmployeeFilesEntity> convertStringArrayToLocalData(List<String[]> stringArrayData) {
        List<EmployeeFilesEntity> result = new ArrayList<>();
        for (String[] data : stringArrayData) {
            if (data.length >= 2) {  // Ensure that the array has at least two elements
                EmployeeFilesEntity entity = new EmployeeFilesEntity(data[0], data[1], data[2], data[3], data[4], data[5], data[6]);
                result.add(entity);
            }
        }
        return result;
    }


    /**
     * This method is used to find the raw file data of the user. It works by creating a thread,
     * this is important so that we wait and ensure this section completes before moving on.
     * It works by first finding the link of the site needed to scrape using
     * findUserFileDataLink(), it then scrapes the body of this site and chops off the
     * success message.
     */
    public void getUserRawInfoFromServer() {

        Thread getUserRawInfoFromServerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Make the link to the site that will contain the users profile data
                    linkToGetProfileInfo = findUserFileDataLink();
                    // Scraping the site to get the body
                    Document doc = Jsoup.connect(linkToGetProfileInfo).get();
                    Elements body = doc.select("body");
                    // Turning the body into a string
                    rawFileInfo = body.text();
                    // Chopping off the success message
                    rawFileInfo = rawFileInfo.substring(35, rawFileInfo.length());

                }
                catch (Exception e) {
                    Log.i("bad", "getUserRawInfoFromServer");
                }
            }
        });
        getUserRawInfoFromServerThread.start();

        // https://ducmanhphan.github.io/2020-03-20-Waiting-threads-to-finish-completely-in-
        // Java/#using-join()-method-of-Thread-class
        // .join makes it so this thread completes before the other code continues

        try {
            getUserRawInfoFromServerThread.join();
        }
        catch (InterruptedException e) {

        }
    }

    /**
     * This method creates a link that will lead to a page containing the users file information
     *
     * @return the link to page containing the profile
     */
    private String findUserFileDataLink(){
        String link = this.link;
        link = link + "/get_data.asp?auth_code=" + getAuth_code() + "&query_type=file_center";

        return link;
    }

    /**
     * This method converts the raw input data into a list containing string arrays where each
     * array in the list has the info for one file. This method works by first splitting the raw
     * data on each "] [" which will split the raw data into an array where each spot has one
     * file. It will them split each file on "],[" splitting it into the bits of info, and
     * then it will split each of those on "=" and if this final spot has length == 2 that means
     * that there is data for that field so the data will be saved.
     *
     * @return
     */
    private List<String[]> convertRawInfo() {
        // Splitting the raw input into the seperate files
        String[] filesInfoArray = rawFileInfo.split("\\] \\[");

        // Initializing a list to store fileInfo arrays
        List<String[]> fileInfoList = new ArrayList<>();

        // Initializing the fileInfo array
        String[] fileInfo = new String[7];

        // Iterating through the profileInfoArray going file by file
        for (String file : filesInfoArray) {
            String[] fileInfoArray = file.split("\\],\\[");
            // looping through the data of the current file
            for(int i = 0; i < fileInfoArray.length; i++) {
                if (fileInfoArray[i].contains("=")) {
                    String[] infoArray = fileInfoArray[i].split("=");

                    // Matching the data to the correct part in the array of file data
                    if (infoArray.length == 2) {
                        switch (infoArray[0]) {
                            case "file_center_id":
                                // putting the array containing file info into the list
                                fileInfoList.add(fileInfo);
                                fileInfo = new String[7];
                                fileInfo[0] = infoArray[1];
                                break;
                            case "file_name":
                                fileInfo[1] = infoArray[1];
                                break;
                            case "date_uploaded":
                                fileInfo[2] = infoArray[1];
                                break;
                            case "file_path":
                                fileInfo[3] = linkToPdf + infoArray[1];
                                Log.i("link", fileInfo[3]);
                                break;
                            case "first_name":
                                fileInfo[4] = infoArray[1];
                                break;
                            case "last_name":
                                fileInfo[5] = infoArray[1];
                                break;
                            case "file_center_type":
                                Log.i("wad", infoArray[1]);
                                if (infoArray[1].charAt(infoArray[1].length() - 1) == ']') {
                                    fileInfo[6] = infoArray[1].substring(0,infoArray[1].length()-1);
                                }
                                else {
                                    fileInfo[6] = infoArray[1];
                                }
                                break;
                            default:
                                Log.i("Switch table", "There is a new type of data");
                                break;
                        }
                    }
                }
            }
        }

        // Adding the last fileInfo to the list

        fileInfoList.add(fileInfo);
        return fileInfoList;
    }

    /**
     * This method is used to check if the user is offline so we know if we should try
     * to pull data from online or pull data from the local database.
     *
     * @param context
     * @return true if online false if offline
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        }
        return false;
    }


}