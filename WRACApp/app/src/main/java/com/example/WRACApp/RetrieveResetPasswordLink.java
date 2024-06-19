package com.example.wolseytechhr;

/**
 * RetrieveResetPasswordLink.java class is designed to allow for password resets. It checks if the
 * password is valid, if it is the password will be reset.
 *
 *
 * Some of the important methods are:
 *
 * DATA METHODS:
 * --------------------
 * checkIfValidPassword(String currentPassword)
 * makeLink()
 * --------------------
 */

import android.util.Log;




public class RetrieveResetPasswordLink {
    private String userName;
    private String password;
    private String oldPassword;
    private String link;
    private String companyName;
    private boolean resetSuccess = false;
    private boolean isValidPassword; // Used to indicate successful reset of password
    private boolean isServerError = false;

    /**
     * This class is used to reset a password. It checks if the password is valid, if it is the
     * password will be reset.
     * @param userName
     * @param password
     * @param companyName
     */
    public RetrieveResetPasswordLink(String userName, String password, String oldPassword, String companyName){
        this.userName = userName;
        this.password = password;
        this.companyName = companyName;
        this.oldPassword = oldPassword;
        // If it is valid reset their password to the new one
        if(checkIfValidPassword(password)){
            isValidPassword = true;
            // Reset the password
        }
        // otherwise tell user their password is invalid.
        else{
            isValidPassword = false;
        }
    }

    public RetrieveResetPasswordLink(String userName, String password, String companyName){
        this.userName = userName;
        this.password = password;
        this.companyName = companyName;
        // If it is valid reset their password to the new one
        if(checkIfValidPassword(password)){
            isValidPassword = true;
            // Reset the password
        }
        // otherwise tell user their password is invalid.
        else{
            isValidPassword = false;
        }
    }


    /**
     * This method checks if a password is valid. A password is valid if it is at least 8 characters
     * long and has at least one letter and one number. If the password was valid a reset link will \
     * be made
     *
     * @return if the password is valid
     */
    // Inside ResetPassword class
    private boolean checkIfValidPassword(String currentPassword) {
        boolean hasLetter = false;
        boolean hasNumber = false;

        // If new password is different from new one
        if(password.equals(oldPassword)){
            Log.i("not new", password);
            return false;
        }

        // If the password is big enough
        if (password.length() > 7) {
            for (int i = 0; i < password.length(); i++) {
                if (Character.isLetter(password.charAt(i))) {
                    hasLetter = true;
                }
                if (Character.isDigit(password.charAt(i))) {
                    hasNumber = true;
                }
            }
            if (hasNumber && hasLetter) {
                makeLink();
                return true;
            }
        }
        Log.i(" not valid password", password);
        return false;
    }

    /**
     * This method makes a link that when ran resets a password
     */
    private void makeLink(){
        link = "https://hr-demo.wolsey-tech.com/get_auth_code.asp?user_name=" + userName + "&password=" + password + "&company_name=" + companyName + "&password_reset=n";
    }

    public String getLink(){
        return link;
    }
    // Used to indicate if the password was reset
    public boolean getIsValidPassword(){
        return isValidPassword;
    }
    public boolean getIsServerError(){
        return isServerError;
    }
    public boolean getResetSuccess(){
        return resetSuccess;
    }
}
