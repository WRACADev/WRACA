package com.example.wolseytechhr;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private String userName;
    private String password;
    private String companyName;
    private String auth_code;
    private SharedPreferences sharedPreferences;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);

        // Checking if the users wanted to stay logged in, if they did log them in using
        // saved data
        if(sharedPreferences.getBoolean("saveLogin", false)){
            Log.d("zap", "auth_code exists");
            openLoggedInUser();
        }

        TextView errorLogInDisplay = findViewById(R.id.errorLogInDisplay);
        errorLogInDisplay.setVisibility(View.INVISIBLE);
        Button forgotPasswordButton = findViewById(R.id.forgotPassword);
        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to ResetPasswordActivity when the "Forgot Password" button is clicked
                Intent intent = new Intent(MainActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });
    }


    /**
     * This method is called when the user attempts to log in. It grabs the info the user
     * entered into the edit text views and attempts to log them in using that info.
     *
     * @param view - Current View
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void openMainMenu(View view){

        // Telling the user loading their info
        TextView loading = findViewById(R.id.loadingDisplay);
        loading.setVisibility(View.VISIBLE);

        // Make login error invisable
        TextView errorLogInDisplay = findViewById(R.id.errorLogInDisplay);
        errorLogInDisplay.setVisibility(View.VISIBLE);
        // Getting saved data
        sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);

        // Getting the information the user entered
        TextView userNameText = findViewById(R.id.editUsername);
        userName = userNameText.getText().toString();

        TextView passwordText = findViewById(R.id.editPassword);
        password= passwordText.getText().toString();

        TextView companyNameText = findViewById(R.id.editCompany);
        companyName = companyNameText.getText().toString();

        // Check for internet connection. This works if you turn off internet connection in phone
        // emulator
        // learned at https://blog.devgenius.io/how-to-check-the-internet-connection-in-an-android-project-4371aafa59ca
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        // If the person logging in has the same credentials as the last person logged in, and
        // the user if offline log them in using the saved data.
        if(sharedPreferences.getString("userName", "ijbefiebo").equals(userName) &&
                sharedPreferences.getString("password", "ib9uwef").equals(password)
        && sharedPreferences.getString("companyName", "9uhfwe").equals(companyName)
        && !isConnected){
            Log.i("login", "main activity offline log in");
            openLoggedInUser();
        }

        else if(isConnected) {
            Log.i("login", "main activity online log in");
            logInUserUsingInternet();
        }
        // If not connected to internet and not the same user as the last logged in, tell the user
        // log in error
        else{
            Log.i("login", "no internet cant log in");
            errorLogInDisplay.setVisibility(View.VISIBLE);
            loading.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * This method is used to log in a user using the servers. This method is called if the
     * user opening the app hasn't logged in before, or us on internet and didn't save their log in
     * or is a new user.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void logInUserUsingInternet(){
        // Telling the user loading their info
        TextView loading = findViewById(R.id.loadingDisplay);
        loading.setVisibility(View.VISIBLE);
        TextView errorLogInDisplay = findViewById(R.id.errorLogInDisplay);
        errorLogInDisplay.setVisibility(View.INVISIBLE);
        errorLogInDisplay.setText("Error logging in");

        // Get the users auth_code. If any errors were detected this class has flags to
        // tell the user
        RetrieveAuthCode getCode = new RetrieveAuthCode(userName, password, companyName);
        auth_code = getCode.getAuth_code();
        // error 103. If the user needs to reset their password we will force them to by bringing
        // Them to the password reset page
        if (getCode.getNeedToResetPassword()) {
            openPasswordReset();
        }
        // If there was an error logging in tell the user
        else if (getCode.getIsErrorGettingAuthCode()) {
            loading.setVisibility(View.INVISIBLE);
            errorLogInDisplay.setVisibility(View.VISIBLE);
            errorLogInDisplay.setText("Error logging in");
        // error 101
        }
        else if (getCode.getIsIncorrectUserNameOrPassword()) {
            errorLogInDisplay.setVisibility(View.VISIBLE);
            errorLogInDisplay.setText("Incorrect Username or Password");
            loading.setVisibility(View.INVISIBLE);
        }
        // error 104
        else if (getCode.getIsIncorrectPassword()) {
            errorLogInDisplay.setVisibility(View.VISIBLE);
            errorLogInDisplay.setText("Incorrect Password");
            loading.setVisibility(View.INVISIBLE);
        }

        // If the user could log in move to next page, since this is a new user reset saved data.
        // Since the user is
        // vaild clear the saved user info so it can be replaced with the new user or updated

        else if (!getCode.getIsErrorGettingAuthCode()) {
            // Clear out saved data so new user doesn't see old users data
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();


            Intent intent = new Intent(this, UserProfile.class);


            CheckBox checkBox = findViewById(R.id.checkBox);

            // if they want to remain logged in
            if (checkBox.isChecked()) {
                saveData(true);
            }
            else {
                saveData(false);
            }
            startActivity(intent);

            finish();

        }
    }
    /**
     * This method is for logging in a user who wants to remain logged in, it pulls up their
     * saved data from saved preferences and send it to the main page
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void openLoggedInUser(){
        // Tell the user loading
        TextView loading = findViewById(R.id.loadingDisplay);
        loading.setVisibility(View.VISIBLE);
        // Getting saved data
        userName = sharedPreferences.getString("userName", "");
        password = sharedPreferences.getString("password", "");
        auth_code = sharedPreferences.getString("auth_code", "");
        companyName = sharedPreferences.getString("companyName", "");
        RetrieveAuthCode getCode = new RetrieveAuthCode(userName, password, companyName);
        // If their auth_code is still valid, existing saved matches the actual, or they are
        // offline (unable to verify auth_code
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        Log.i("zap", "openLoggedInUser");
        if(auth_code.equals(getCode.getAuth_code()) || !isConnected) { // OR OFFLINE
            // Making the new intent passing the users saved data
            openProfile();
        }
        // otherwise the users auth_code is no longer valid
        else{
            // Clear users current saved data from app if their auth_code is no longer valid
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            loading.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * This method saves data to saved preferences along with a boolean that indicates that
     * their data has been saved.
     */
    private void saveData(boolean stayLoggedIn){
        // Saving user info in shared preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(stayLoggedIn){
            editor.putBoolean("saveLogin", true);
        }
        else{
            editor.putBoolean("saveLogin", false);
        }
        editor.putString("auth_code", auth_code);
        editor.putString("userName", userName);
        editor.putString("password", password);
        editor.putString("companyName", companyName);
        editor.apply();
    }
    //==============================================================================================
    //                              METHODS FOR SWITCHING PAGE
    //==============================================================================================
    /**
     * Opens the profile page. Sends information to the profile that is necessary for getting
     * data and tells the user that the application is loading
     *
     */
    public void openProfile(){
            TextView loadingDisplay = findViewById(R.id.loadingDisplay);
            loadingDisplay.setVisibility(View.VISIBLE);
            Intent intent = new Intent(this, UserProfile.class);
            intent.putExtra("auth_code", auth_code);
            startActivity(intent);
            finish();

    }
    /**
     * Opens the password reset page. Sends information to the profile that is necessary for getting
     * data.
     *
     */
    public void openPasswordReset(){
        TextView loadingDisplay = findViewById(R.id.loadingDisplay);
        loadingDisplay.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, ResetPasswordActivity.class);
        intent.putExtra("forced", true);
        startActivity(intent);
        finish();

    }
}