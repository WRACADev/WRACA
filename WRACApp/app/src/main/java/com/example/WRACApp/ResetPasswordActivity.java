package com.example.wolseytechhr;

/**
 * ResetPasswordActivity.java class is designed to allow for the user to reset their password if
 * they end up forgetting it. This class is also used to force the user to reset their password
 * if we detect error 104 when retrieving their authorization code
 *
 * Some of the important methods are:
 *
 * NAVIGATION METHODS:
 * -------------------
 * returnProfile(View myView)
 * -------------------
 *
 * UI ELEMENT METHODS:
 * --------------------
 * onClick(View view)
 * --------------------
 *
 * DATA METHODS:
 * --------------------
 * runResetLink(String linkToReset)
 * --------------------
 */

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ResetPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Initialize UI elements
        final EditText newPasswordEditText = findViewById(R.id.editTextNewPassword);
        final EditText confirmPasswordEditText = findViewById(R.id.editTextConfirmPassword);
        final EditText userNameEditText = findViewById(R.id.editTextUsername);
        final EditText companyEditText = findViewById(R.id.editTextCompany);
        Button resetButton = findViewById(R.id.buttonReset);

        // Hide exit button if forced reset
        if (getIntent().getBooleanExtra("forced", false)) {
            Button exit = findViewById(R.id.toLogin);
            exit.setVisibility(View.INVISIBLE);
        }

        // Set click listener for reset button
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // getting info the user typed in
                String newPassword = newPasswordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();
                String company = companyEditText.getText().toString();
                String userName = userNameEditText.getText().toString();

                if (newPassword.equals(confirmPassword)) {
                    // Passwords match, initiate password reset

                    RetrieveResetPasswordLink resetPassword = new RetrieveResetPasswordLink(userName, newPassword, company);

                    if (resetPassword.getIsValidPassword()) {
                        // Check internet connection
                        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

                        if (isConnected) {
                            // If the user is online, reset their password
                            runResetLink(resetPassword.getLink());
                            // Inform the user that their password was reset
                            Toast.makeText(ResetPasswordActivity.this, "Password reset successful!", Toast.LENGTH_SHORT).show();
                            // Take them back to login
                            openLogin();
                        } else {
                            Toast.makeText(ResetPasswordActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Password reset failed, show appropriate error message
                        if (resetPassword.getIsServerError()) {
                            Toast.makeText(ResetPasswordActivity.this, "Server error during password reset.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ResetPasswordActivity.this, "Invalid password.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    // Passwords don't match, show an error
                    Toast.makeText(ResetPasswordActivity.this, "Passwords do not match. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //==============================================================================================
    //                              METHODS FOR PASSWORD RESET
    //==============================================================================================


    /**
     * This method is used to run a password reset link. It runs it in a webview that is hidden in
     * the xml activity reset password.
     *
     * @param linkToReset The password reset link to be run.
     */
    private void runResetLink(String linkToReset) {
        // Get the webview
        WebView webView = findViewById(R.id.webView);

        // Run the link in the view
        webView.loadUrl(linkToReset);
    }

    //==============================================================================================
    //                              METHODS FOR SWITCHING PAGE
    //==============================================================================================


    /**
     * This method is to open the login page after password reset has happened. It also displays
     * a textview to tell the user that the app is loading.
     */
    public void openLogin() {
        TextView loadingDisplay = findViewById(R.id.loadingDisplay);
        loadingDisplay.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * This method is to open the login page via button press. This is used by users who enter this
     * page on their own. This method also tells the user that the new page is loading using the
     * loading textview.
     */
    public void openLogin(View myView) {
        TextView loadingDisplay = findViewById(R.id.loadingDisplay);
        loadingDisplay.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}
