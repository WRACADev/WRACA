package com.example.wolseytechhr;

/**
 * SettingsActivity.java class is designed to control the settings of the application. This
 * includes things like changing the colour of the background, the navigation bar, and some
 * potential additions like the big mode and sound level.
 *
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
 * setupVolumeControl()
 * onStartTrackingTouch(SeekBar seekBar)
 * onStopTrackingTouch(SeekBar seekBar)
 * setupBigModeToggle()
 * --------------------
 *
 * DATA METHODS:
 * --------------------
 * saveVolumeSetting(int volume)
 * saveBigModeSetting(boolean bigMode)
 * setCurrentColorDisplay()
 * getColorResource(String colorName)
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREF_NAME = "MyAppPrefs";
    private static final String VOLUME_KEY = "volume";
    private static final String BIG_MODE_KEY = "big_mode";

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // Setting current color display
        setCurrentColorDisplay();
        preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        setupVolumeControl();
        setupBigModeToggle();
      //  setupDarkModeToggle();
    }


    //==============================================================================================
    //                              METHODS FOR VOLUME
    //==============================================================================================

    /**
     * This method is used to set up a listener to listen to the change of the volume slider. When
     * the volume changes it uses saveVolumeSetting to save the new volume. This is an unused feature
     * that might be used in the future if we decide to add sounds for buttons to improve
     * informative feedback
     */

    private void setupVolumeControl() {
        SeekBar volumeSeekBar = findViewById(R.id.volumeSeekBar);

        volumeSeekBar.setProgress(preferences.getInt(VOLUME_KEY, 50)); // Set initial progress

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Handle volume change
                // You can use AudioManager to set the device volume
                saveVolumeSetting(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /**
     * This method is used to save the volume level in shared preferences
     *
     * @param volume
     */
    private void saveVolumeSetting(int volume) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(VOLUME_KEY, volume);
        editor.apply();
    }


    //==============================================================================================
    //                              METHODS FOR BIG MODE
    //==============================================================================================

    /**
     * This method is used to setup a listener for the big mode toggle in settings. If it is on
     * then the enableBigMode() method will be called and if it is unchecked then the disableBigMode()
     * will be called.
     */

    private void setupBigModeToggle() {
        Switch bigModeSwitch = findViewById(R.id.bigModeSwitch);

        bigModeSwitch.setChecked(preferences.getBoolean(BIG_MODE_KEY, false)); // Set initial state

        bigModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Handle big mode change
                if (isChecked) {
                    // Enable big mode (increase text size)
                    // You can use TextView.setTextSize() for this
                    enableBigMode();
                } else {
                    // Disable big mode (reset text size)
                    disableBigMode();
                }
            }
        });
    }

    /**
     * This method is used to save if big mode has been pressed. It is saved into shared
     * preferences so in the future we can implement this setting on other pages.
     *
     * @param bigMode
     */
    private void saveBigModeSetting(boolean bigMode) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(BIG_MODE_KEY, bigMode);
        editor.apply();
    }

    /**
     * This method is used to enable big mode, it increases the size of all fonts to 20. In the
     * future this method will have to be changed so that it will increase all fonts by a specific
     * percent because most fonts are already at least 20dp and this would also be shrinking some
     * text.
     */
    private void enableBigMode() {
        // Enable big mode (increase text size) for all TextViews
        float newSize = 20;
        applyTextSizeToAllTextViews(newSize);
        saveBigModeSetting(true);
    }

    /**
     * This method is used to shrink back all text size to a smaller size however this method
     * should in the future be shrinking the text size of all views by the % they were increased
     * by because this would shrink some views smaller than they were in the first place and
     * it would also make all text the same size, which it should not be.
     */
    private void disableBigMode() {
        // Disable big mode (reset text size) for all TextViews
        float defaultSize = 16;
        applyTextSizeToAllTextViews(defaultSize);
        saveBigModeSetting(false);
    }

    /**
     * This method is used to apply the changes in text sizes to views, is uses the applyTextSize
     * method to achive this.
     *
     * @param textSize
     */
    private void applyTextSizeToAllTextViews(float textSize) {
        // Iterate through all layouts and set the text size for each TextView
        ViewGroup rootView = (ViewGroup) findViewById(android.R.id.content).getRootView();
        applyTextSize(rootView, textSize);
    }

    /**
     * This method recursively applies the new text size to all views in a given view group
     *
     * @param viewGroup
     * @param textSize
     */
    private void applyTextSize(ViewGroup viewGroup, float textSize) {
        // Recursively apply text size to all TextViews in the given ViewGroup
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof TextView) {
                ((TextView) child).setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            } else if (child instanceof ViewGroup) {
                applyTextSize((ViewGroup) child, textSize);
            }
        }
    }

    //==============================================================================================
    //                              METHODS FOR SWITCHING COLOUR
    //==============================================================================================

    /**
     * This method is used to set the current color display. It first tries to get the color that
     * the app is currently set to, if it is not set it will be set to company_color by default.
     */
    private void setCurrentColorDisplay(){
        preferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        int currentNavColor = preferences.getInt("colorNav", 0);
        int currentBgColor = preferences.getInt("colorBg", 0);
        Button currentTaskDisplay = findViewById(R.id.currentTaskDisplay);
        Button currentBgDisplay = findViewById(R.id.currentBackgroundDisplay);
        if(currentNavColor != 0) {
            currentTaskDisplay.setBackgroundResource(currentNavColor);
        }
        if(currentBgColor != 0) {
            currentBgDisplay.setBackgroundResource(currentBgColor);
        }
    }


    //==============================================================================================
    //                              METHODS FOR TASKBAR COLOURS
    //==============================================================================================
    /**
     * This method sets the current color saved to the one that the user selected. It also
     * changes the color display to the one the user pressed.
     *
     * @param myView
     */
    public void switchGreen(View myView){
        preferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int colorResource = getColorResource("green");
        editor.putInt("colorNav", colorResource);
        editor.putString("colorNavText", textColor);
        editor.apply();
        Button currentColorDisplay = findViewById(R.id.currentTaskDisplay);
        currentColorDisplay.setBackgroundResource(colorResource);
    }
    /**
     * This method sets the current color saved to the one that the user selected. It also
     * changes the color display to the one the user pressed.
     *
     * @param myView
     */
    public void switchYellow(View myView){
        preferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int colorResource = getColorResource("yellow");
        editor.putInt("colorNav", colorResource);
        editor.putString("colorNavText", textColor);
        editor.apply();
        Button currentColorDisplay = findViewById(R.id.currentTaskDisplay);
        currentColorDisplay.setBackgroundResource(colorResource);
    }
    /**
     * This method sets the current color saved to the one that the user selected. It also
     * changes the color display to the one the user pressed.
     *
     * @param myView
     */
    public void switchRed(View myView){
        preferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        int colorResource = getColorResource("red");
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("colorNav", colorResource);
        editor.putString("colorNavText", textColor);
        editor.apply();
        Button currentColorDisplay = findViewById(R.id.currentTaskDisplay);
        currentColorDisplay.setBackgroundResource(colorResource);
    }

    /**
     * This method sets the current color saved to the one that the user selected. It also
     * changes the color display to the one the user pressed.
     *
     * @param myView
     */
    public void switchWhite(View myView){
        preferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int colorResource = getColorResource("white");
        editor.putInt("colorNav", colorResource);
        editor.putString("colorNavText", textColor);
        editor.apply();
        Button currentColorDisplay = findViewById(R.id.currentTaskDisplay);
        currentColorDisplay.setBackgroundResource(colorResource);
    }

    /**
     * This method sets the current color saved to the one that the user selected. It also
     * changes the color display to the one the user pressed.
     *
     * @param myView
     */
    public void switchLightBlue(View myView){
        preferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int colorResource = getColorResource("blue");
        editor.putInt("colorNav", colorResource);
        editor.putString("colorNavText", textColor);
        editor.apply();
        Button currentColorDisplay = findViewById(R.id.currentTaskDisplay);
        currentColorDisplay.setBackgroundResource(colorResource);
    }

    /**
     * This method sets the current color saved to the one that the user selected. It also
     * changes the color display to the one the user pressed.
     *
     * @param myView
     */
    public void switchGrey(View myView){
        preferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int colorResource = getColorResource("grey");
        editor.putInt("colorNav", colorResource);
        editor.putString("colorNavText", textColor);
        editor.apply();
        Button currentColorDisplay = findViewById(R.id.currentTaskDisplay);
        currentColorDisplay.setBackgroundResource(colorResource);
    }

    /**
     * This method sets the current color saved to the one that the user selected. It also
     * changes the color display to the one the user pressed.
     *
     * @param myView
     */
    public void switchPurple(View myView){
        preferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int purple = getColorResource("purple");
        editor.putInt("colorNav", purple);
        editor.putString("colorNavText", textColor);
        editor.apply();
        Button currentColorDisplay = findViewById(R.id.currentTaskDisplay);
        currentColorDisplay.setBackgroundResource(purple);
    }

    /**
     * This method sets the current color saved to the one that the user selected. It also
     * changes the color display to the one the user pressed.
     *
     * @param myView
     */
    public void switchCompanyColor(View myView){
        preferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int color = getColorResource("companyColor");
        editor.putInt("colorNav", color);
        editor.putString("colorNavText", textColor);
        editor.apply();
        Button currentColorDisplay = findViewById(R.id.currentTaskDisplay);
        currentColorDisplay.setBackgroundResource(color);
    }


    //==============================================================================================
    //                            METHODS FOR CHANGING BACKGROUND COLOUR
    //==============================================================================================
    /**
     * This method sets the current color saved to the one that the user selected. It also
     * changes the color display to the one the user pressed.
     *
     * @param myView
     */
    public void switchGreenBackground(View myView){
        preferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int colorResource = getColorResource("green");
        editor.putInt("colorBg", colorResource);
        editor.apply();
        Button currentColorDisplay = findViewById(R.id.currentBackgroundDisplay);
        currentColorDisplay.setBackgroundResource(colorResource);
    }
    /**
     * This method sets the current color saved to the one that the user selected. It also
     * changes the color display to the one the user pressed.
     *
     * @param myView
     */
    public void switchBlueBackground(View myView){
        preferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int colorResource = getColorResource("darkBlue");
        editor.putInt("colorBg", colorResource);
        editor.apply();
        Button currentColorDisplay = findViewById(R.id.currentBackgroundDisplay);
        currentColorDisplay.setBackgroundResource(colorResource);
    }
    /**
     * This method sets the current color saved to the one that the user selected. It also
     * changes the color display to the one the user pressed.
     *
     * @param myView
     */
    public void switchRedBackground(View myView){
        preferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        int colorResource = getColorResource("red");
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("colorBg", colorResource);
        editor.apply();
        Button currentColorDisplay = findViewById(R.id.currentBackgroundDisplay);
        currentColorDisplay.setBackgroundResource(colorResource);
    }

    /**
     * This method sets the current color saved to the one that the user selected. It also
     * changes the color display to the one the user pressed.
     *
     * @param myView
     */
    public void switchBlackBackground(View myView){
        preferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int colorResource = getColorResource("black");
        editor.putInt("colorBg", colorResource);
        editor.apply();
        Button currentColorDisplay = findViewById(R.id.currentBackgroundDisplay);
        currentColorDisplay.setBackgroundResource(colorResource);
    }

    /**
     * This method sets the current color saved to the one that the user selected. It also
     * changes the color display to the one the user pressed.
     *
     * @param myView
     */
    public void switchWhiteBackground(View myView){
        preferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int colorResource = getColorResource("white");
        editor.putInt("colorBg", colorResource);
        editor.apply();
        Button currentColorDisplay = findViewById(R.id.currentBackgroundDisplay);
        currentColorDisplay.setBackgroundResource(colorResource);
    }

    /**
     * This method sets the current color saved to the one that the user selected. It also
     * changes the color display to the one the user pressed.
     *
     * @param myView
     */
    public void switchGreyBackground(View myView){
        preferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int colorResource = getColorResource("lightGrey");
        editor.putInt("colorBg", colorResource);
        editor.apply();
        Button currentColorDisplay = findViewById(R.id.currentBackgroundDisplay);
        currentColorDisplay.setBackgroundResource(colorResource);
    }

    /**
     * This method sets the current color saved to the one that the user selected. It also
     * changes the color display to the one the user pressed.
     *
     * @param myView
     */
    public void switchPurpleBackground(View myView){
        preferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int purple = getColorResource("purple");
        editor.putInt("colorBg", purple);
        editor.apply();
        Button currentColorDisplay = findViewById(R.id.currentBackgroundDisplay);
        currentColorDisplay.setBackgroundResource(purple);
    }

    /**
     * This method sets the current color saved to the one that the user selected. It also
     * changes the color display to the one the user pressed.
     *
     * @param myView
     */
    public void switchCompanyColorBackground(View myView){
        preferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int color = getColorResource("companyColor");
        editor.putInt("colorBg", color);
        editor.apply();
        Button currentColorDisplay = findViewById(R.id.currentBackgroundDisplay);
        currentColorDisplay.setBackgroundResource(color);
    }


    //==============================================================================================
    //                              METHODS FOR CHANGING COLOUR FOR BOTH
    //==============================================================================================
    /**
     * This method is used to get a color resource int from a String color that is inputed.
     * This int that us returned is used to set the background of views. The private variable
     * is used for text color since you cant return 2 things at once.
     *
     * @param colorName
     * @return
     */
    private String textColor = "black";
    private int getColorResource(String colorName) {
        switch (colorName) {
            case "black":
                textColor = "white";
                return R.color.black;
            case "white":
                textColor = "black";
                return R.color.white;
            case "grey":
                textColor = "black";
                return R.color.grey;
            case "darkBlue":
                textColor = "white";
                return R.color.dark_blue;
            case "green":
                textColor = "white";
                return R.color.green;
            case "red":
                textColor = "black";
                return R.color.red;
            case "companyColor":
                textColor = "white";
                return R.color.company_colour;
            case "purple":
                textColor = "white";
                return R.color.purple;
            case "yellow":
                textColor = "black";
                return R.color.yellow;
            case "blue":
                textColor = "white";
                return R.color.blue;
            case "lightGrey":
                textColor = "black";
                return R.color.lightGrey;
            default:
                return 0;
        }
    }
    //==============================================================================================
    //                              METHODS FOR SWITCHING PAGES
    //==============================================================================================

    /**
     * This method is used to take the user back to the profile section when they press the
     * open profile button. In the future we may add the navbar to this page so that the
     * user can go back to wherever they want from the settings page.
     *
     * @param myView
     */

    public void returnProfile(View myView){
        TextView loading = findViewById(R.id.loadingDisplay);
        loading.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, UserProfile.class);
        startActivity(intent);
        finish();
    }
}
