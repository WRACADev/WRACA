package com.example.wolseytechhr;

/**
 * RetrieveEmployeeProfile.java class is designed to use threads to retrieve data from the
 * Wolsey-Tech database. Threads are needed so that one section of the code will have completed
 * before moving onto the next section.
 *
 *
 * Some of the important methods are:
 *
 * DATA METHODS:
 * --------------------
 * getUserRawInfoFromServer()
 * findUserProfileDataLink()
 * convertRawInfo(Context context)
 * --------------------
 */

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import android.util.Log;


public class RetrieveEmployeeProfile {
    // Variables have been declared in the same order that the developer site's personal information
    // page was ordered
    private final String link = "https://hr-demo.wolsey-tech.com";
    public static String logoLink = "https://hr-demo.wolsey-tech.com/images/logo.png";
    private String linkToGetProfileInfo;
    private String firstName;
    private String middleName;
    private String lastName;
    private String address1;
    private String address2;
    private String postalCode;
    private String city;
    private String province;
    private String phoneNumber;
    private String cellNumber;
    private String emailAddress;
    private String auth_code;
    private String rawProfileInfo;

    /**
     * This class uses threads to retrieve data from the Wolsey tech database. Threads are needed
     * so that one section of the code will have completed before moving onto the next section.
     * This class works in the following way, first it creates a link to a webpage containing data,
     * then it scrapes the data from the web page, then it cleans the data and has getters so the
     * data can be easily displayed in the front end.
     */
    public RetrieveEmployeeProfile(String auth_code) {
        this.auth_code = auth_code;


        // Start and complete the thread that will find the raw user profile data
        getUserRawInfoFromServer();

        // Split up the users raw info into the private variables in this class
        convertRawInfo();
    }


    /**
     * This method is used to find the raw profile data of the user. It works by creating a thread,
     * this is important so that we wait and ensure this section completes before moving on.
     * It works by first finding the link of the site needed to scrape using
     * findUserProfileDataLink(), it then scrapes the body of this site and chops off the
     * success message.
     */
    private void getUserRawInfoFromServer() {
        Thread getUserRawInfoFromServerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Make the link to the site that will contain the users profile data
                    linkToGetProfileInfo = findUserProfileDataLink();
                    // Scraping the site to get the body
                    Document doc = Jsoup.connect(linkToGetProfileInfo).get();
                    Elements body = doc.select("body");
                    // Turning the body into a string
                    rawProfileInfo = body.text();
                    Log.i("priflie info", rawProfileInfo);
                    // Chopping off the success message
                    rawProfileInfo = rawProfileInfo.substring(35, rawProfileInfo.length());

                } catch (Exception e) {
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
        } catch (InterruptedException e) {

        }
    }


    /**
     * This method creates a link that will lead to a page containing the users profile information
     * The information thaw will be displayed is in the body of this page.
     *
     * @return the link to page containing the profile
     */
    private String findUserProfileDataLink() {
        String link = this.link;
        link = link + "/get_data.asp?auth_code=" + getAuth_code() + "&query_type=personal_info";
        return link;
    }


    /**
     * Converts the users raw profile info that is in a string into the correct variables.
     * It works by first splitting the raw data into an array on all of the "],[" and then
     * splits each part of the array on "=". If splitting on "=" made an array of length 2
     *  that means that part of the array had info in it so the info will be stored into
     *  the correct variable using the jump table
     */
    private void convertRawInfo() {
        // Splitting the raw input into an array where each part of the array contains one
        // part of the data
        String[] profileInfoArray = rawProfileInfo.split("\\],\\[");


        // Iterating through the profileInfoArray, matching each bit of data to the correct
        // variable. Each part of the array will be split into 2 parts where the left is the
        // type of data and the right is the data. If there is only one part that means
        // this data field was not filled in


        for (int i = 0; i < profileInfoArray.length; i++) {
            if (profileInfoArray[i].contains("=")) {
                String[] infoArray = profileInfoArray[i].split("=");

                // CHECK IF THERE ARE TWO PARTS IN infoArray
                if (infoArray.length == 2) {
                    switch (infoArray[0]) {
                        case "[first_name":
                            firstName = infoArray[1];
                            break;
                        case "middle_name":
                            middleName= infoArray[1];
                            break;
                        case "last_name":
                            lastName= infoArray[1];
                            break;
                        case "address_1":
                            address1= infoArray[1];
                            break;
                        case "address_2":
                            address2= infoArray[1];
                            break;
                        case "city":
                            city= infoArray[1];
                            break;
                        case "province_name":
                            province= infoArray[1];
                            break;
                        case "postal_code":
                            postalCode= infoArray[1];
                            break;
                        case "home_phone":
                            phoneNumber= infoArray[1];
                            break;
                        case "cell_phone":
                            cellNumber= infoArray[1];
                            break;
                        case "email":
                            emailAddress= infoArray[1].substring(0, infoArray[1].length() - 1);
                            break;
                        default:
                            break;
                    }

                }

            }
        }

    }



    // ---------------------------------------- Getters ----------------------------------------- //

    public String getPostalCode() {
        return postalCode;
    }

    public String getAuth_code() {
        return auth_code;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    public String getCity() {
        return city;
    }

    public String getProvince() {
        return province;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCellNumber() {
        return cellNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public static String getLogoLink() {
        return logoLink;
    }

    // FOR TESTING
    public String getLink(){
        return findUserProfileDataLink();
    }
    public String getRawProfileInfo(){
        return rawProfileInfo;
    }
}