package com.example.wolseytechhr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Date;

/**
 * Timesheets.java class is designed to control the frontend of the the timesheets activity. This
 * includes things like displaying the data based on a certain time frame that has been selected,
 * the methods that are responsible for traversing the pages, and any other UI elements that have
 * specific parameters to be set.
 *
 * Some of the important methods are:
 *
 * NAVIGATION METHODS:
 * --------------------
 *
 * openProfile()
 * openSettings()
 * onItemSelected()
 * onNothingSelected()
 * --------------------
 *
 *
 * UI ELEMENT METHODS:
 * --------------------
 * displayData()
 * displayDataViews()
 * openDropMenu(View myView)
 * hideDropMenu(View myView)
 * changeColours()
 * --------------------
 */

public class Timesheets extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private String auth_code = "default";
    // Start dates for the 3 available timesheet periods
    private String firstDate;
    private String middleDate;
    private String lastDate;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timesheets);
        ImageView logoView = findViewById(R.id.logo);
        ImageLoader imageLoader = new ImageLoader(getApplicationContext());
        imageLoader.loadImage(RetrieveEmployeeProfile.getLogoLink(), logoView);

        createNavbar();

        // Set the navbar colour
        changeColours();

        // Getting auth_code
        sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        auth_code = sharedPreferences.getString("auth_code", "");
        setDateButtons();
    }

    //==============================================================================================
    //                              METHODS FOR OPENING SPECIFIC TIMESHEETS
    //==============================================================================================

    /**
     * This method is used to get and display the timesheet information found in the backend.
     * It first gets the start and end date depending on the button to sent to the backend then
     * displays the information from the backed.This is used when the date on the top of the
     * drop down date select is pressed.
     *
     * @param myView
     */
    public void openFirstDate(View myView){
        // Hiding date select views
        hideDateSelect();
        // If the user is online get their data
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if(isConnected) {
            // Getting end date
            String endDate = getEndDate(firstDate);
            // Displaying the timesheet data
            displayData(firstDate, endDate);
        }
        else{
            TextView noInternetDisplay = findViewById(R.id.noInternetDisplay);
            noInternetDisplay.setVisibility(View.VISIBLE);
        }

    }

    /**
     * This method is used to get and display the timesheet information found in the backend.
     * It first gets the start and end date depending on the button to sent to the backend then
     * displays the information from the backed. This is used when the date at the middle of the
     * drop down date select is pressed.
     *
     * @param myView
     */
    public void openMiddleDate(View myView){
        // Hiding date select views
        hideDateSelect();
        // If the user is online get their data
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if(isConnected) {
            // Getting end date
            String endDate = getEndDate(middleDate);
            // Displaying the timesheet data
            displayData(middleDate, endDate);
        }
        else{
            TextView noInternetDisplay = findViewById(R.id.noInternetDisplay);
            noInternetDisplay.setVisibility(View.VISIBLE);
        }

    }

    /**
     * This method is used to get and display the timesheet information found in the backend.
     * It first gets the start and end date depending on the button to sent to the backend then
     * displays the information from the backed. This is used when the date on the bottom of the
     * drop down date select is pressed.
     *
     * @param myView
     */
    public void openLastDate(View myView){
        // Hiding date select views
        hideDateSelect();
        // If the user is online get their data
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if(isConnected) {
            // Getting end date
            String endDate = getEndDate(lastDate);
            // Displaying the timesheet data
            displayData(lastDate, endDate);
        }
        else{
            TextView noInternetDisplay = findViewById(R.id.noInternetDisplay);
            noInternetDisplay.setVisibility(View.VISIBLE);
        }

    }
    //==============================================================================================
    //                              METHODS FOR DISPLAYING DATA
    //==============================================================================================

    /**
     * This method is used to un-hide the views that are used to display a users timesheet
     * data. displayData is what will display the data from the backed using these views.
     * This is used in displayData
     */
    private void displayDataViews(){
        ScrollView scrollInfo = findViewById(R.id.timesheetScrollView);
        scrollInfo.setVisibility(View.VISIBLE);
    }

    /**
     * This method is used to display the data from the back end, it uses TimesheetsData
     * to get a users timesheet data and displays it in a LinearLayout that is made by looping
     * through the data, making rows where each row represents a day, and in each row TextViews
     * are added to display the information about that day. Each day will be displayed in its
     * own card. If a data field is null it will be set to "".
     *
     */
    private void displayData(String startDate, String endDate) {
        // Displaying views that will hold timesheet data
        displayDataViews();

        // Tell user their data is loading
        Toast.makeText(Timesheets.this, "Loading", Toast.LENGTH_LONG).show();

        // Getting timesheet data
        TimesheetsData dataFromServer = new TimesheetsData(startDate, endDate, auth_code);
        Log.i("asd", "iu");
        List<String[]> data = dataFromServer.getConvertedInfo();


        // If there was timesheet data
        if(!dataFromServer.getIsNoData()){
            // Assuming you have a layout container where you want to display the timesheet information,

            // let's say it's a LinearLayout with an id 'timesheetContainer'.
            LinearLayout timesheetContainer = findViewById(R.id.timesheetContainer);
            timesheetContainer.setVisibility(View.VISIBLE);

            // Clear existing views in the container
            timesheetContainer.removeAllViews();

            // Loop through the timesheet data day by day and create TextViews to display data
            for (int day = data.size()-1; day > -1; day--) {
                    CardView card = new CardView(this);
                    // Relative layout to contain info
                    RelativeLayout cardContentLayout = new RelativeLayout(this);
                    cardContentLayout.setPadding(20, 20, 20, 20);

                    // TextView for date
                    TextView dateTextView = new TextView(this);
                    dateTextView.setText("Date: \n" + data.get(day)[1]);
                    dateTextView.setTextSize(25);
                    dateTextView.setTextColor(Color.BLACK);
                    dateTextView.setGravity(Gravity.CENTER);


                    // Linear layout to stack info on right
                    LinearLayout rightLayout = new LinearLayout(this);
                    rightLayout.setOrientation(LinearLayout.VERTICAL);
                    rightLayout.setBackgroundResource(R.color.odd_row_color);

                    // text view for id
                    TextView id = new TextView(this);
                    id.setText("ID: " + String.format("%1$" + 24 + "s", data.get(day)[0]));
                    if (data.get(day)[0] == null) {
                        data.get(day)[0] = "";
                    }
                    id.setTypeface(Typeface.MONOSPACE);
                    id.setTextColor(Color.BLACK);
                    id.setBackgroundResource(R.color.even_row_color);
                    id.setGravity(Gravity.CENTER);
                    id.setTextSize(20);

                    TextView remoteTourId = new TextView(this);
                    if (data.get(day)[2] == null) {
                        data.get(day)[2] = "";
                    }
                     // text view for remote tour id
                    remoteTourId.setText("Remote Tour ID: " + String.format("%1$" + 12 + "s", data.get(day)[2]));
                    remoteTourId.setTypeface(Typeface.MONOSPACE);
                    remoteTourId.setBackgroundResource(R.color.odd_row_color);
                    remoteTourId.setTextColor(Color.BLACK);
                    remoteTourId.setGravity(Gravity.CENTER);
                    remoteTourId.setTextSize(20);

                    TextView locationName = new TextView(this);
                    if (data.get(day)[3] == null) {
                        data.get(day)[3] = "";
                    }
                    // text view for location name
                    locationName.setText("Location Name: " + String.format("%1$" + 13 + "s", data.get(day)[3]));
                    locationName.setTypeface(Typeface.MONOSPACE);
                    locationName.setTextColor(Color.BLACK);
                    locationName.setBackgroundResource(R.color.even_row_color);
                    locationName.setGravity(Gravity.CENTER);
                    locationName.setGravity(Gravity.CENTER);
                    locationName.setTextSize(20);

                    TextView titleName = new TextView(this);
                    if (data.get(day)[4] == null) {
                        data.get(day)[4] = "";
                    }
                    titleName.setText("Title Name: " + String.format("%1$" + 16 + "s", data.get(day)[4]));
                    titleName.setTypeface(Typeface.MONOSPACE);
                    titleName.setTextColor(Color.BLACK);
                    titleName.setBackgroundResource(R.color.odd_row_color);
                    titleName.setGravity(Gravity.CENTER);
                    titleName.setTextSize(20);

                    // text view for reg hours
                    TextView regHours = new TextView(this);
                    if (data.get(day)[5] != null) {
                    }
                    regHours.setText("Regular Hours: " + String.format("%1$" + 13 + "s", data.get(day)[5]));
                    regHours.setTypeface(Typeface.MONOSPACE);
                    regHours.setTextColor(Color.BLACK);
                    regHours.setBackgroundResource(R.color.even_row_color);
                    regHours.setGravity(Gravity.CENTER);
                    regHours.setTextSize(20);

                    if (data.get(day)[6] == null) {
                        data.get(day)[6] = "";
                    }
                    // text view for ot hours
                    TextView otHours = new TextView(this);
                    otHours.setText("Overtime Hours: " + String.format("%1$" + 12 + "s", data.get(day)[6]));
                    otHours.setTypeface(Typeface.MONOSPACE);
                    otHours.setTextColor(Color.BLACK);
                    otHours.setBackgroundResource(R.color.odd_row_color);
                    otHours.setGravity(Gravity.CENTER);
                    otHours.setTextSize(20);

                    if (data.get(day)[7] == null) {
                        data.get(day)[7] = "";
                    }
                    // text view for shop hours
                    TextView shopHours = new TextView(this);
                    shopHours.setText("Shop Hours: " + String.format("%1$" + 16 + "s", data.get(day)[7]));
                    shopHours.setTypeface(Typeface.MONOSPACE);
                    shopHours.setBackgroundResource(R.color.even_row_color);
                    shopHours.setGravity(Gravity.CENTER);
                    shopHours.setTextColor(Color.BLACK);
                    shopHours.setTextSize(20);

                    if (data.get(day)[8] == null) {
                        data.get(day)[8] = "";
                    }
                    // text view for shop ot hours
                    TextView shopOtHours = new TextView(this);
                    shopOtHours.setText("Shop Overtime Hours: " + String.format("%1$" + 7 + "s", data.get(day)[8]));
                    shopOtHours.setTypeface(Typeface.MONOSPACE);
                    shopOtHours.setBackgroundResource(R.color.odd_row_color);
                    shopOtHours.setGravity(Gravity.CENTER);
                    shopOtHours.setTextColor(Color.BLACK);
                    shopOtHours.setTextSize(20);

                    if (data.get(day)[9] == null) {
                        data.get(day)[9] = "";
                    }
                    // text view for shop kms
                    TextView shopKMS = new TextView(this);
                    shopKMS.setText("Shop Kms: " + String.format("%1$" + 18 + "s", data.get(day)[9]));
                    shopKMS.setTypeface(Typeface.MONOSPACE);
                    shopKMS.setBackgroundResource(R.color.even_row_color);
                    shopKMS.setGravity(Gravity.CENTER);
                    shopKMS.setTextColor(Color.BLACK);
                    shopKMS.setTextSize(20);

                    if (data.get(day)[10] == null) {
                        data.get(day)[10] = "";
                    }
                    // text view for stat hourrs
                    TextView statHrs = new TextView(this);
                    statHrs.setText("STAT Hours: " + String.format("%1$" + 16 + "s", data.get(day)[10]));
                    statHrs.setTypeface(Typeface.MONOSPACE);
                    statHrs.setBackgroundResource(R.color.odd_row_color);
                    statHrs.setGravity(Gravity.CENTER);
                    statHrs.setTextColor(Color.BLACK);
                    statHrs.setTextSize(20);

                    if (data.get(day)[11] == null) {
                        data.get(day)[11] = "";
                    }
                    // text view for travel hours
                    TextView travelHours = new TextView(this);
                    travelHours.setText("Travel Hours: " + String.format("%1$" + 14 + "s", data.get(day)[11]));
                    travelHours.setTypeface(Typeface.MONOSPACE);
                    travelHours.setBackgroundResource(R.color.even_row_color);
                    travelHours.setGravity(Gravity.CENTER);
                    travelHours.setTextColor(Color.BLACK);
                    travelHours.setTextSize(20);

                    if (data.get(day)[12] == null) {
                        data.get(day)[12] = "";
                    }
                    // text view for travel kms
                    TextView KMS = new TextView(this);
                    KMS.setText("Kms: " + String.format("%1$" + 23 + "s", data.get(day)[12]));
                    KMS.setTypeface(Typeface.MONOSPACE);
                    KMS.setBackgroundResource(R.color.odd_row_color);
                    KMS.setTextColor(Color.BLACK);
                    KMS.setGravity(Gravity.CENTER);
                    KMS.setTextSize(20);

                    if (data.get(day)[13] == null) {
                        data.get(day)[13] = "";
                    }
                    // text view for sub charged
                    TextView subCharged = new TextView(this);
                    subCharged.setText("Sub Charged: " + String.format("%1$" + 15 + "s", data.get(day)[13]));
                    subCharged.setTypeface(Typeface.MONOSPACE);
                    subCharged.setTextColor(Color.BLACK);
                    subCharged.setBackgroundResource(R.color.even_row_color);
                    subCharged.setGravity(Gravity.CENTER);
                    subCharged.setTextSize(20);

                    if (data.get(day)[14] == null) {
                        data.get(day)[14] = "";
                    }
                     // text view for sub not charged
                    TextView subNotCharged = new TextView(this);
                    subNotCharged.setText("Sub Not Charged: " + String.format("%1$" + 11 + "s", data.get(day)[14]));
                    subNotCharged.setTypeface(Typeface.MONOSPACE);
                    subNotCharged.setTextColor(Color.BLACK);
                    subNotCharged.setBackgroundResource(R.color.odd_row_color);
                    subNotCharged.setGravity(Gravity.CENTER);
                    subNotCharged.setTextSize(20);

                    if (data.get(day)[15] == null) {
                        data.get(day)[15] = "";
                    }
                    // text view for addons
                    TextView addons = new TextView(this);
                    addons.setText("Add ons: " + String.format("%1$" + 19 + "s", data.get(day)[15]));
                    addons.setTypeface(Typeface.MONOSPACE);
                    addons.setTextColor(Color.BLACK);
                    addons.setBackgroundResource(R.color.even_row_color);
                    addons.setGravity(Gravity.CENTER);
                    addons.setTextSize(20);

                    // Add textViews to layouts
                    rightLayout.addView(dateTextView);
                    rightLayout.addView(id);
                    rightLayout.addView(remoteTourId);
                    rightLayout.addView(locationName);
                    rightLayout.addView(titleName);
                    rightLayout.addView(regHours);
                    rightLayout.addView(otHours);
                    rightLayout.addView(shopHours);
                    rightLayout.addView(shopOtHours);
                    rightLayout.addView(shopKMS);
                    rightLayout.addView(statHrs);
                    rightLayout.addView(travelHours);
                    rightLayout.addView(KMS);
                    rightLayout.addView(subCharged);
                    rightLayout.addView(subNotCharged);
                    rightLayout.addView(addons);

                    // hours layout params
                    RelativeLayout.LayoutParams rightLayoutParams = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                    );

                    // rightLayoutParams.setMarginStart(400);
                    rightLayout.setLayoutParams(rightLayoutParams);

                    // Add info to cardview
                    card.addView(cardContentLayout);
                    cardContentLayout.addView(rightLayout);

                    // Set layout parameters for the CardView
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    layoutParams.setMargins(30, 30, 30, 0);

                    layoutParams.gravity = Gravity.CENTER;
                    card.setLayoutParams(layoutParams);


                    card.setCardElevation(8);
                    card.setRadius(20);

                    timesheetContainer.addView(card);
                }

        }
        // If cant get data tell user the data doesn't exist
        else{
            TextView noDataDisplay = findViewById(R.id.unableToGetData);
            noDataDisplay.setVisibility(View.VISIBLE);
        }

    }

    //==============================================================================================
    //                              METHODS FOR GETTING TIMESHEET DATES
    //==============================================================================================


    /**
     * This method will find the current time periods that should be displayed on each button.
     * It does this by adding to a date that was an old time period by 14 until reaching the
     * current time period. Then it will find the 2 previous time periods by subtracting 14
     *
     */
    public void setDateButtons(){
        // Getting buttons so can set their text to the dates
        Button firstDate = findViewById(R.id.firstDate);
        Button middleDate = findViewById(R.id.middleDate);
        Button lastDate = findViewById(R.id.lastDate);
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            // setting a date to the starting day, this is the date that will
            // be added to
            Date currentDate = new Date();
            Date startDate = dateFormat.parse("2023-12-09");

            // Initialize the calendar with the start date
            Calendar currentPeriodStart = Calendar.getInstance();
            currentPeriodStart.setTime(startDate);

            // while the starting point is before the current pay period add 2 week
            while (currentPeriodStart.getTime().before(currentDate)) {
                currentPeriodStart.add(Calendar.DAY_OF_YEAR, 14);
            }
            // The current time period starts 14 before the date the while loop found,
            // if finds the end date of current pay period
            currentPeriodStart.add(Calendar.DAY_OF_YEAR, -14);

            // Calculate the middle date
            Calendar middlePeriodStart = (Calendar) currentPeriodStart.clone();
            middlePeriodStart.add(Calendar.DAY_OF_YEAR, -14);

            // calculate the last date
            Calendar lastPeriodStart = (Calendar) middlePeriodStart.clone();
            lastPeriodStart.add(Calendar.DAY_OF_YEAR, -14);

            // set date strings
            this.firstDate = dateFormat.format(currentPeriodStart.getTime());
            this.middleDate = dateFormat.format(middlePeriodStart.getTime());
            this.lastDate = dateFormat.format(lastPeriodStart.getTime());

            // set date buttons
            firstDate.setText(dateFormat.format(currentPeriodStart.getTime()));
            middleDate.setText(dateFormat.format(middlePeriodStart.getTime()));
            lastDate.setText(dateFormat.format(lastPeriodStart.getTime()));
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * This method is used to find the end date of a pay period. It works by taking a start date
     * as a param and adds 14 to it, returns the end date as a string.
     *
     * @param startDateString
     * @return
     */
    private static String getEndDate(String startDateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date startDate = dateFormat.parse(startDateString);

            // initialize the calendar with the start date so can add to it
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);

            // add 14 days to the start date to get end date
            calendar.add(Calendar.DAY_OF_YEAR, 14);

            // return the end date as a string
            return dateFormat.format(calendar.getTime());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    //==============================================================================================
    //                              METHODS FOR START DATE DROP DOWN
    //==============================================================================================

    /**
     * This method is used to hide the date select views when opening the timesheet data
     */
    public void hideDateSelect(){
        Button backToDateSelect = findViewById(R.id.backToDateSelectButton);
        backToDateSelect.setVisibility(View.VISIBLE);
        TextView instructions = findViewById(R.id.instructions);
        instructions.setVisibility(View.INVISIBLE);
        Button f = findViewById(R.id.firstDate);
        f.setVisibility(View.INVISIBLE);
        Button m = findViewById(R.id.middleDate);
        m.setVisibility(View.INVISIBLE);
        Button l = findViewById(R.id.lastDate);
        l.setVisibility(View.INVISIBLE);
    }
    /**
     * This method is used to open the date select items and hide the table displaying the users data
     * when they want to go back to see different days.
     */
    public void openDateSelectHideData(View myView){

        Button backToDateSelect = findViewById(R.id.backToDateSelectButton);
        backToDateSelect.setVisibility(View.INVISIBLE);
        TextView instructions = findViewById(R.id.instructions);
        instructions.setVisibility(View.VISIBLE);
        Button f = findViewById(R.id.firstDate);
        f.setVisibility(View.VISIBLE);
        Button m = findViewById(R.id.middleDate);
        m.setVisibility(View.VISIBLE);
        Button l = findViewById(R.id.lastDate);
        l.setVisibility(View.VISIBLE);

        LinearLayout timesheetContainer = findViewById(R.id.timesheetContainer);
        timesheetContainer.setVisibility(View.INVISIBLE);
        ScrollView scrollInfo = findViewById(R.id.timesheetScrollView);
        scrollInfo.setVisibility(View.INVISIBLE);
        TextView noInternetDisplay = findViewById(R.id.noInternetDisplay);
        noInternetDisplay.setVisibility(View.INVISIBLE);
        TextView noDataDisplay = findViewById(R.id.unableToGetData);
        noDataDisplay.setVisibility(View.INVISIBLE);

    }

    //==============================================================================================
    //                              METHODS FOR SWITCHING PAGES
    //==============================================================================================

    /**
     * This method is used to refresh the profile. It finished the current instance and makes
     * a new version of it.
     *
     * @param myView
     */
    public void refreshEmployeeInfo(View myView){
        TextView loadingDisplay = findViewById(R.id.loadingDisplay);
        loadingDisplay.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, Timesheets.class);
        startActivity(intent);
        finish();
    }

    /**
     * Opens the profile page
     */
    public void openProfile(){
        TextView loadingDisplay = findViewById(R.id.loadingDisplay);
        loadingDisplay.setVisibility(View.VISIBLE);
        TextView noInternetDisplay = findViewById(R.id.noInternetDisplay);
        noInternetDisplay.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(this, UserProfile.class);
        startActivity(intent);
        finish();
    }

    /**
     * Opens the file center page
     */
    public void openFileCenter(){
        TextView loadingDisplay = findViewById(R.id.loadingDisplay);
        loadingDisplay.setVisibility(View.VISIBLE);
        TextView noInternetDisplay = findViewById(R.id.noInternetDisplay);
        noInternetDisplay.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(this, FileCenter.class);
        intent.putExtra("auth_code", auth_code);
        startActivity(intent);
        finish();

    }

    /**
     * This method is used to open settings
     */
    public void openSettings(){
        TextView loadingDisplay = findViewById(R.id.loadingDisplay);
        loadingDisplay.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, SettingsActivity.class);
        sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        startActivity(intent);
        finish();
    }

    /**
     * Logs the user out, takes user back to log in page and clears their data
     * @param myView
     */
    public void logout(View myView){
        Intent intent = new Intent(this, MainActivity.class);
        sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
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
                openProfile();
                break;
            case "Timesheet":

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
     * This method sets the current colors saved to the ones that the user selected. It will change
     * the colors of the navbar and the background
     */
    private void changeColours(){
        sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        int colorNav = sharedPreferences.getInt("colorNav", 0);
        int backgroundColor = sharedPreferences.getInt("colorBg", 0);

        Spinner spinner = findViewById(R.id.navigationMenu);

        if(colorNav != 0){
            spinner.setBackgroundResource(colorNav);
            spinner.setPopupBackgroundResource(colorNav);
        } else{
            spinner.setBackgroundResource(R.color.company_colour);
            spinner.setPopupBackgroundResource(R.color.company_colour);
        }

        if(backgroundColor != 0){
            ConstraintLayout page = findViewById(R.id.timesheetPage);
            page.setBackgroundResource(backgroundColor);
        }
    }
    /**
     * This method is used to open the drop down menu that
     * displays time periods that the user can check
     *
     * @param myView
     */
    public void openDropDown(View myView){
        Button f = findViewById(R.id.firstDate);
        f.setVisibility(View.VISIBLE);
        Button m = findViewById(R.id.middleDate);
        m.setVisibility(View.VISIBLE);
        Button l = findViewById(R.id.lastDate);
        l.setVisibility(View.VISIBLE);
        Button d = findViewById(R.id.dropDownButton);
        d.setVisibility(View.INVISIBLE);
    }

    /**
     * This method collapses the drop down menu that shows
     * the time periods the user can view
     *
     * @param myView
     */
    public void hideDropMenu(View myView){
        Button f = findViewById(R.id.firstDate);
        f.setVisibility(View.INVISIBLE);
        Button m = findViewById(R.id.middleDate);
        m.setVisibility(View.INVISIBLE);
        Button l = findViewById(R.id.lastDate);
        l.setVisibility(View.INVISIBLE);
        Button d = findViewById(R.id.dropDownButton);
        d.setVisibility(View.VISIBLE);

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