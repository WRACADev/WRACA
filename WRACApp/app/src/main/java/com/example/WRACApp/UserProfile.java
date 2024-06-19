package com.example.wolseytechhr;

/**
 * UserProfile.java class is designed to control the frontend of the the profile activity. This
 * includes things like displaying the user information based on their company credentials. These
 * methods are responsible for pulling the user data, displaying it in the correct format, and
 * allowing for dynamic additions to the length of the UI cards in order to account for more or less
 * user information.
 *
 *
 * Some of the important methods are:
 *
 * NAVIGATION METHODS:
 * --------------------
 *
 * openSettings()
 * openTimesheets()
 * openFileCenter()
 * logout(View myView)
 * onItemSelected(AdapterView<CharSequence> parent, View view, int position, long id)
 * onNothingSelected(AdapterView<CharSequence> parent)
 * --------------------
 *
 *
 * UI ELEMENT METHODS:
 * --------------------
 * displayData()
 * refreshEmployeeInfo()
 * displayEmployeeInfo()
 * displayDataViews()
 * changeColours()
 * initializeCardsWidth()
 * --------------------
 *
 * DATA METHODS:
 * --------------------
 * openSavedData()
 * buildAddress()
 * buildContacts()
 */

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class UserProfile extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private String auth_code = "default";
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ImageView logoView = findViewById(R.id.logo);
        ImageLoader imageLoader = new ImageLoader(getApplicationContext());
        imageLoader.loadImage(RetrieveEmployeeProfile.getLogoLink(), logoView);



        // Setup an array adapter so that the string resources which contain all of options for the
        // dropdown menu can be used.
        createNavbar();

        // Set the navbar colour
        changeColors();

        // Getting the saved auth_code
        sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        auth_code = sharedPreferences.getString("auth_code", "");

        // checking for internet connection
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        // Checking if the users log in data was saved before, if it was and the user is offline
        // Display the last saved data since we can not update the data with what is online
        if(sharedPreferences.getBoolean("saveProfile", false) && !isConnected){
            TextView noInternetDisplay = findViewById(R.id.noInternetDisplay);
            noInternetDisplay.setVisibility(View.VISIBLE);
            openSavedData();
        }
        // Otherwise if they are online pull user data from online
        else if(isConnected){

            displayEmployeeInfo();
        }
        // Make the cards containing the employee information to be a good size for the users screen
        initializeCardsWidth();
    }
    //==============================================================================================
    //                              METHODS FOR DISPLAYING DATA
    //==============================================================================================

    /**
     * This method is used to show data from saved preferences if the user has
     * has saved data and is offline. It first grabs all of the saved data and then
     * displays it in the front end.
     */
    public void openSavedData(){
        String address1 = sharedPreferences.getString("address1", null);
        String address2 = sharedPreferences.getString("address2", null);
        String city = sharedPreferences.getString("city", null);
        String province = sharedPreferences.getString("province", null);
        String postalCode = sharedPreferences.getString("postalCode", null);
        String firstName = sharedPreferences.getString("firstName", null);
        String lastName = sharedPreferences.getString("lastName", null);
        String cell = sharedPreferences.getString("cell", null);
        String phone = sharedPreferences.getString("phone", null);
        String email = sharedPreferences.getString("email", null);

        // Displaying employees address info
        buildAddress(address1,address2,city, province, postalCode);

        // Displaying employees contact info
        buildContacts(phone,cell,email);

        // Displaying the name of the employee at the top of the screen
        TextView firstNameTextView = findViewById(R.id.firstNameDisplay);
        firstNameTextView.setText(firstName);
        TextView lastNameTextView = findViewById(R.id.lastNameDisplay);
        lastNameTextView.setText(lastName);

    }

    /**
     * This method displays the employees data found on the server. It creates an instance of
     * employee to get the data and then uses the getters in employee to display the data.
     * After displaying the profile data this method saves the data into
     * shared preferences.
     */
    public void displayEmployeeInfo(){
        // Getting employee information
        RetrieveEmployeeProfile employee = new RetrieveEmployeeProfile(auth_code);

        // Displaying the employees address information
        buildAddress(employee.getAddress1(),employee.getAddress2(),employee.getCity(),
                employee.getProvince(), employee.getPostalCode());

        // Displaying employee information
        buildContacts(employee.getPhoneNumber(),employee.getCellNumber(),employee.getEmailAddress());

        // Displaying the name of the employee at the top of the screen
        TextView firstNameTextView = findViewById(R.id.firstNameDisplay);
        firstNameTextView.setText(employee.getFirstName());
        TextView lastNameTextView = findViewById(R.id.lastNameDisplay);
        lastNameTextView.setText(employee.getLastName());


        // Saving users data so can be viewed offline
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("saveProfile", true);
        editor.putString("firstName", employee.getFirstName());
        editor.putString("middleName", employee.getMiddleName());
        editor.putString("lastName", employee.getLastName());
        editor.putString("address1", employee.getAddress1());
        editor.putString("address2", employee.getAddress2());
        editor.putString("postalCode", employee.getPostalCode());
        editor.putString("city", employee.getCity());
        editor.putString("province", employee.getProvince());
        editor.putString("phone", employee.getPhoneNumber());
        editor.putString("cell", employee.getCellNumber());
        editor.putString("email", employee.getEmailAddress());
        editor.apply();
    }

    /**
     * This method builds the address card of the employee as a String. If one of the bits
     * if information is not given there will be no field for it on the card. This method
     * displays the data in the front end.
     *
     * @param address1
     * @param address2
     * @param city
     * @param province
     * @param postalCode
     */
    private void buildAddress(String address1, String address2, String city, String province, String postalCode){
        // Building the string that will display address info
        String stringBuilder = "Home Address\n\n";
        if(address1 != null)
            stringBuilder += address1 + "\n";

        if(address2 != null)
            stringBuilder += address2 + "\n";

        if(city != null && province != null){
            stringBuilder += city + ", " + province + "\n";
        }
        else if(city != null)
            stringBuilder += city + "\n";

        else if(province != null)
            stringBuilder += province + "\n";

        if(postalCode != null)
            stringBuilder += postalCode;

        if(stringBuilder.equals("Home Address\n\n"))
            stringBuilder += "Not Available";
        // Displaying employee's address
        TextView homeAddress = findViewById(R.id.home_address);
        homeAddress.setText(stringBuilder);
    }

    /**
     * This method is used to build the card on the profile page that displays the
     * employees contact information. Like buildAddress if a bit of information
     * is not given the method will not create a feild for it.
     *
     * @param phoneNumber
     * @param cellNumber
     * @param email
     */
    private void buildContacts(String phoneNumber, String cellNumber, String email){
        // Building the string that will display the employees contact info
        String stringBuilder = "Contact Information\n\n";

        if(phoneNumber != null)
            stringBuilder += "Tel: " + phoneNumber + "\n";

        if(cellNumber != null)
            stringBuilder += "Cell: " + cellNumber + "\n";

        if(email != null)
            stringBuilder += "E-mail: \n" + email + "\n";

        if(stringBuilder.equals("Contact Information\n\n"))
            stringBuilder += "Not available";

        TextView contactsView = findViewById(R.id.contacts);
        contactsView.setText(stringBuilder);
    }
    /**
     * This method initializes the width of the cards in the profile section depending
     * on the width of the users screen
     */
    private void initializeCardsWidth(){

        // Selecting the cards of the user profile
        CardView addressCard = findViewById(R.id.address_card);
        CardView contactsCard = findViewById(R.id.contacts_card);

        // Getting required width
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        // Setting new width to cards

        addressCard.getLayoutParams().width = (int)(width * 0.99);
        contactsCard.getLayoutParams().width = (int)(width * 0.99);

    }



    //==============================================================================================
    //                              METHODS FOR SWITCHING PAGE
    //==============================================================================================

    /**
     * Opens the file center page and sends the users auth_code so that it does not need to be sent
     * multiple times. It also displays the loading textview to tell the user that the app is loading
     */
    public void openFileCenter(){
        TextView loadingDisplay = findViewById(R.id.loadingDisplay);
        loadingDisplay.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, FileCenter.class);
        intent.putExtra("auth_code", auth_code);
        startActivity(intent);
        finish();
    }

    /**
     * This method is used to open settings from the main menu. It adds the auth_code to the next
     * intent so it does not need to be found another time. This method will also display the
     * loading textview to tell the user that the app is loading.
     */
    public void openSettings(){
        TextView loadingDisplay = findViewById(R.id.loadingDisplay);
        loadingDisplay.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, SettingsActivity.class);
        sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        intent.putExtra("auth_code", auth_code);
        startActivity(intent);
        finish();
    }

    /**
     * Opens the time sheet page and sends user info. Like the other open methods it also sends the
     * auth_code so it does not need to be found again and displays the loading textview to
     * tell the user that the app is loading.
     */
    public void openTimeSheet(){
        TextView loadingDisplay = findViewById(R.id.loadingDisplay);
        loadingDisplay.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, Timesheets.class);
        intent.putExtra("auth_code", auth_code);
        startActivity(intent);
        finish();
    }

    /**
     * Logs the user out, takes user back to log in page. This method opens the main log in page
     * and clears the users saved data. It is used when they press the logout power button.
     * @param myView
     */
    public void logout(View myView){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        finish();
    }

    /**
     * This method is used to refresh the profile. It finishes the current instance and makes a new
     * version of it.
     *
     * @param myView
     */
    public void refreshEmployeeInfo(View myView){
        TextView loadingDisplay = findViewById(R.id.loadingDisplay);
        loadingDisplay.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, UserProfile.class);
        intent.putExtra("auth_code", auth_code);
        startActivity(intent);
        finish();
    }
    //==============================================================================================
    //                              METHODS FOR NAVBAR
    //==============================================================================================

    /**
     * This function is activated whenever a selection from the dropdown menu is chosen.
     * Uses a spinner object as the menu.
     * All of the options in the dropdown menu are added and removed in the strings.xml file
     *
     * @param parent The AdapterView where the selection happened
     * @param view The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedOption = parent.getItemAtPosition(position).toString();

        switch (selectedOption) {
            case "Menu":
                break;
            case "Profile":
                break;
            case "Timesheet":
                openTimeSheet();
                break;
            case "File Center":
                openFileCenter();
                break;
            case "Settings":
                openSettings();
                break;
        }
    }

    /**
     * Function for nothing selected in dropdown menu
     *
     * @param parent The AdapterView that now contains no selected item.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



    /**
     * This method sets the current colors saved to the ones that the user selected. This method
     * also sets the background color of the containers to the navbar color.
     */
    private void changeColors(){
        sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        int colorNav = sharedPreferences.getInt("colorNav", 0);
        String textColor = sharedPreferences.getString("colorNavText", "");
        int backgroundColor = sharedPreferences.getInt("colorBg", 0);

        Spinner spinner = findViewById(R.id.navigationMenu);

        if(colorNav != 0){
            LinearLayout home = findViewById(R.id.home_container);
            LinearLayout phone = findViewById(R.id.contacts_container);
            home.setBackgroundResource(colorNav);
            phone.setBackgroundResource(colorNav);
            spinner.setBackgroundResource(colorNav);
            spinner.setPopupBackgroundResource(colorNav);
        } else{
            spinner.setBackgroundResource(R.color.company_colour);
            spinner.setPopupBackgroundResource(R.color.company_colour);
        }

        if(backgroundColor != 0){
            ConstraintLayout page = findViewById(R.id.profilePage);
            page.setBackgroundResource(backgroundColor);
        }
    }

    /**
     * This method is used to create and initilize the navbar found on the user profile
     */
    public void createNavbar(){
        // Initialize dropdown menu object
        Spinner spinner = findViewById(R.id.navigationMenu);
        // Setup an array adapter so that the string resources which contain all of options for the
        // dropdown menu can be use
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.navigation_menu, R.layout.navigation_menu);
        adapter.setDropDownViewResource(R.layout.navigation_menu);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }
}