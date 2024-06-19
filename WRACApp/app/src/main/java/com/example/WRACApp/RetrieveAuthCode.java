package com.example.wolseytechhr;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.security.PublicKey;

import javax.crypto.SecretKey;

/**
 *
 * RetrieveAuthCode.java class is designed to retrieve authorization codes from the API that
 * Wolsey-Tech has provided for us. We use the auth code in order to pull data from their servers
 *
 * Some of the important methods are:
 *
 * DATA METHODS:
 * --------------------
 * findAuthCode()
 * makeURLToGetAuthCode()
 * --------------------
 *
 * There are also getters in this class that are used to retrieve flags for errors so
 * the errors can be reported in the front end.
 *
 */

public class RetrieveAuthCode {
    private final String link = "https://hr-demo.wolsey-tech.com";
    private String linkToGetAuthCode;
    private boolean isIncorrectPassword = false;
    private String userName;
    private String password;
    private String companyName;
    private boolean isIncorrectUserNameOrPassword = false;
    private String auth_code;
    private boolean isErrorGettingAuthCode = false;
    private boolean needToResetPassword = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public RetrieveAuthCode(String userName, String password, String companyName){
        this.userName = userName;
        this.password = password;
        this.companyName = companyName;
        findAuthCode();
    }
    /**
     * This method is used to find the auth_code of the user. It works by creating a thread,
     * this is important so that we wait and ensure this section completes before moving on,
     * if we did this on the main thread it crash due to how long it takes.
     * The thread first uses the makeURLToGetAuthCode() function to make the link
     * of the page we will need to scrape, it then uses the Jsoup library to grab the body of the
     * page, this is what we need since the body contains the user. The thread then manipulates
     * the bodies data to get the auth_code. This method also checks the raw input of the webpage
     * to see if the user needs to reset their password or if there was an error getting their
     * auth_code.
     */

    @RequiresApi(api = Build.VERSION_CODES.O)
    void findAuthCode() {
        Thread findAuthCodeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Make the link to the site that will contain the auth_code
                    linkToGetAuthCode = makeURLToGetAuthCode();
                    Log.i("linktoauthcode", linkToGetAuthCode);
                    // using jsoup to scrape the site
                    Document doc = Jsoup.connect(linkToGetAuthCode).get();
                    Elements body = doc.select("body");
                    // Turning it into a string
                    auth_code = body.text();
                    // checking if there was an error code

                    if(auth_code.substring(6, 9).equals("101")) {
                        isIncorrectUserNameOrPassword = true;
                    }
                    else if(auth_code.substring(6, 9).equals("103")){
                        needToResetPassword = true;
                    }
                    else if(auth_code.substring(6, 9).equals("104")){
                        isIncorrectPassword = true;
                    }
                    else if(auth_code.substring(0, 5).equals("ERROR")){
                        isErrorGettingAuthCode = true;
                    }
                    // chopping off the message before the auth_code and last '[' character
                    auth_code = auth_code.substring(11, auth_code.length() - 1);

                }
                catch (Exception e) {
                    Log.i("bad", "findAuthCode");
                }
            }
        });
        findAuthCodeThread.start();

        // https://ducmanhphan.github.io/2020-03-20-Waiting-threads-to-finish-completely-in-
        // Java/#using-join()-method-of-Thread-class
        // .join makes it so this thread completes before the other code continues

        try {
            findAuthCodeThread.join();
        }
        catch (InterruptedException e) {

        }
    }
    /**
     * This method will generate the link that is used to get a users auth_code that is
     * used to get their data from the database. It takes the default link and adds the users info
     * to it
     *
     * @return the url where the auth_code can be found
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    String makeURLToGetAuthCode() throws Exception {
        String url = this.link;
        SecretKey secretKey = LoginEncryption.generateAESKey();
        PublicKey publicKey = LoginEncryption.generateRSAKeyPair().getPublic();
        byte[] encryptedPassword = LoginEncryption.encrypt(password, publicKey , secretKey);

        url = url + "/get_auth_code.asp" + "?user_name=" + userName + "&password=" + password
                + "&company_name=" + companyName;
        return url;
    }


    public boolean getIsErrorGettingAuthCode(){
        return isErrorGettingAuthCode;
    }
    public String getAuth_code(){
        return auth_code;
    }
    public boolean getNeedToResetPassword(){
        return needToResetPassword;
    }
    public boolean getIsIncorrectUserNameOrPassword(){
        return isIncorrectUserNameOrPassword;
    }
    public boolean getIsIncorrectPassword(){
        return isIncorrectPassword;
    }

}
