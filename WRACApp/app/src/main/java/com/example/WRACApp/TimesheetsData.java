package com.example.wolseytechhr;

/**
 * TimesheetsData.java class is designed to control the data retrieval and cleaning of the timesheet
 * data. This is used to prepare data for the frontend. This class works with 3 main steps, the
 * first is to create a link to the webpage that contains the users timesheet information, the second is
 * to scrape the data from that page which is contained in the body of that page and the third is
 * to clean the data and put it in to a list where each spot in the list has a string array that
 * represents one dats information.
 *
 *
 * Some of the important methods are:
 *
 * DATA METHODS:
 * --------------------
 * getUserRawInfoFromServer()
 * findUserTimeSheetLink()
 * convertRawInfo()
 * --------------------
 */

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.List;

public class TimesheetsData {
        private final String link = "https://hr-demo.wolsey-tech.com";
        private String linkToGetTimesheetInfo;
        private String auth_code;
        private String rawTimesheetInfo;
        private String startDate;
        private String endDate;
        private boolean isNoData = false;
        private List<String[]> convertedInfo;

        public  TimesheetsData(String startDate, String endDate, String auth_code) {
            this.startDate = startDate;
            this.endDate = endDate;
            this.auth_code = auth_code;
            getUserRawInfoFromServer();
            convertedInfo = convertRawInfo();

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
                        linkToGetTimesheetInfo = findUserTimeSheetLink();

                        // Scraping the site to get the body
                        Document doc = Jsoup.connect(linkToGetTimesheetInfo).get();
                        Elements body = doc.select("body");
                        // Turning the body into a string
                        rawTimesheetInfo = body.text();

                        // Chopping off the success message
                        if(rawTimesheetInfo.length() > 36) {
                            rawTimesheetInfo = rawTimesheetInfo.substring(36, rawTimesheetInfo.length() - 1);
                        }
                        else{
                            isNoData = true;
                        }
                        Log.i("linktimesheet", rawTimesheetInfo);
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
     * This method creates a link that will lead to a page containing the users time sheet
     * information.
     *
     * @return the link of page containing the profile
     */
        private String findUserTimeSheetLink(){
            String link = this.link;
            link = link + "/get_data.asp?auth_code=" + auth_code + "&query_type=time_sheet&start_date="
            + startDate + "&end_date=" + endDate;
            return link;
        }

    /**
     * This method converts the raw input data that is type String into a list containing string
     * arrays where each array in the list has the info for one day. It works by first splitting
     * the raw data on all of the "] [" which will split the data into an array where each spot has
     * one day. The second thing that happens is each spot in that array is split on "],[" splitting
     * into the individual bits of information that are split on "=", and if this final split is
     * an array of length 2 the part at index 1 is put into the right spot in the the clean data list
     *
     * @return cleaned data
     */
        private List<String[]> convertRawInfo() {
            // Splitting the raw input into the days

            String[] daysInfoArray = rawTimesheetInfo.split("\\] \\[");

            // Initializing a list to store TimesheetInfo arrays

            List<String[]> TimesheetInfoList = new ArrayList<>();


            // Iterating through the timesheetInfoArray going day by day
            for (String day : daysInfoArray) {
                // creating variables to count total hours of a specific type
                int totalReg = 0;
                int totalOt = 0;
                // Creating an array that will represent one days data
                String[] timesheetInfo = new String[18];
                // splitting the day into its bits of data
                String[] timesheetInfoArray = day.split("\\],\\[");
                // looping through the data of the current day
                for(int i = 0; i < timesheetInfoArray.length; i++) {
                    if (timesheetInfoArray[i].contains("=")) {
                        String[] infoArray = timesheetInfoArray[i].split("=");

                        // Matching the data to the correct part in the array of daya data
                        if (infoArray.length == 2) {
                            switch (infoArray[0]) {
                                case "[time_sheet_id":
                                    // putting the array containing timsheet info into the list
                                    timesheetInfo[0] = infoArray[1];
                                    break;
                                case "time_sheet_id":
                                    // putting the array containing timsheet info into the list
                                    timesheetInfo[0] = infoArray[1];
                                    break;
                                case "time_sheet_date":
                                    timesheetInfo[1] = infoArray[1];
                                    break;
                                case "remote_tour_id":
                                    timesheetInfo[2] = infoArray[1];
                                    break;
                                case "location_name":
                                    timesheetInfo[3] = infoArray[1];
                                    break;
                                case "title_name":
                                    timesheetInfo[4] = infoArray[1];
                                    break;
                                case "reg_hrs":
                                    totalReg += Integer.valueOf(infoArray[1]);
                                    timesheetInfo[5] = infoArray[1];
                                    break;
                                case "ot_hrs":
                                    totalOt += Integer.valueOf(infoArray[1]);
                                    timesheetInfo[6] = infoArray[1];
                                    break;
                                case "shop_hrs":
                                    totalReg += Integer.valueOf(infoArray[1]);
                                    timesheetInfo[7] = infoArray[1];
                                    break;
                                case "shop_ot_hrs":
                                    totalOt += Integer.valueOf(infoArray[1]);
                                    timesheetInfo[8] = infoArray[1];
                                    break;
                                case "shop_kms":
                                    timesheetInfo[9] = infoArray[1];
                                    break;
                                case "stat_hrs":
                                    timesheetInfo[10] = infoArray[1];
                                    break;
                                case "travel_hrs":
                                    timesheetInfo[11] = infoArray[1];
                                    break;
                                case "kms":
                                    timesheetInfo[12] = infoArray[1];
                                    break;
                                case "sub_charged":
                                    timesheetInfo[13] = infoArray[1];
                                    break;
                                case "sub_not_charged":
                                    timesheetInfo[14] = infoArray[1];
                                    break;
                                case "addons":
                                    timesheetInfo[15] = infoArray[1].substring(0,infoArray[1].length() -1);
                                    break;
                                case "addons]":
                                    timesheetInfo[15] = infoArray[1].substring(0,infoArray[1].length() -1);
                                    break;
                                default:
                                    Log.i("Switch table", "There is a new type of data");
                                    break;
                            }
                        }
                    }

                }
                // Adding the hours of a specific type at the end of the array
                timesheetInfo[16] = String.valueOf(totalReg);
                timesheetInfo[17] = String.valueOf(totalOt);

                // Adding the cleaned data of the day that waas just cleaned into the
                // List that will contain all cleaned data
                TimesheetInfoList.add(timesheetInfo);

            }
            // Returning the data
            return TimesheetInfoList;

        }

        //--------------------------------- GETTERS ---------------------------------------------
        public List<String[]> getConvertedInfo(){
            return convertedInfo;
        }
        public boolean getIsNoData(){
            return isNoData;
        }

    }
