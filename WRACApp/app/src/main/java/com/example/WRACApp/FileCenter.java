package com.example.wolseytechhr;

/**
 * FileCenter.java class is designed to control the frontend and data retrieval of the the file
 * center activity. This includes things like displaying the available files to be downloaded
 * based on the categories they belong to. These methods are responsible for pulling the user data,
 * displaying it in the correct format, and allowing for dynamic additions to the list of files.
 * These files are downloadable and must be first downloaded to the device and then viewed in a
 * separate PDF viewer
 *
 *
 * Some of the important methods are:
 *
 * NAVIGATION METHODS:
 * --------------------
 * openSettings()
 * openTimesheets()
 * openProfile()
 * logout(View myView)
 * onItemSelected(AdapterView<CharSequence> parent, View view, int position, long id)
 * onNothingSelected(AdapterView<CharSequence> parent)
 * --------------------
 *
 *
 * UI ELEMENT METHODS:
 * --------------------
 * setOnClickListenersToTableRows(TableLayout table)
 * openPDFInWebView(String fileName, String filePath)
 * openDownloadPopup(String fileName, String filePath)
 * createTables()
 * setUpTableTitles(TextView titleView)
 * setUpTableLayout(TableLayout table)
 * convertDpToPx(Context context, float dp)
 * changeColours()
 * --------------------
 *
 *
 * DATA METHODS:
 * --------------------
 * putInfoIntoTable(Map <String, TableLayout> tables)
 * formatPDFDownloadLink(TextView titleView)
 * refreshEmployeeInfo(View myView)
 * --------------------
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class FileCenter extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private SharedPreferences sharedPreferences;
    private String auth_code = "default";
    List<String[]> employeeFileInfo;
    RecyclerView recyclerView; // This allows the scrolling of dynamic content
    Context fileCenterContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_center);
        ImageView logoView = findViewById(R.id.logo);
        TextView loadingDisplay = findViewById(R.id.loadingDisplay);
        ImageLoader imageLoader = new ImageLoader(getApplicationContext());
        imageLoader.loadImage(RetrieveEmployeeProfile.getLogoLink(), logoView);
        loadingDisplay.setVisibility(View.INVISIBLE);

        auth_code = getIntent().getStringExtra("auth_code");
        sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);

        // Creating the navbar
        createNavbar();

        // Set the navbar colour
        changeColors();

        // If the user is online get and display their data
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        employeeFileInfo = new EmployeeFiles( auth_code,  FileCenter.this).retrieveInfo(FileCenter.this);
        LinkedHashMap mapTableLayouts = createTables();
        if(employeeFileInfo != null) {
            putInfoIntoTable(mapTableLayouts);
        }
        // Creating an adapter for the recycle view
        recyclerView = findViewById(R.id.file_center_body);
        RecycleViewAdapter recycleViewAdapter = new RecycleViewAdapter(this, mapTableLayouts);
        recyclerView.setAdapter(recycleViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        for (Object table : mapTableLayouts.values()) {
            setOnClickListenersToTableRows((TableLayout) table);
        }

    }

    /**
     * This method is used to set onclick listeners to each row that is representing a file.
     * This will make it so that when the user presses the file row they will be able to
     * download the pdf in that row so it can be viewed offline.
     *
     * @param table
     */

    public void setOnClickListenersToTableRows(TableLayout table){
        int numRowsTable = table.getChildCount();
        if(numRowsTable > 1) {
            for (int i = 1; i < numRowsTable; i++) {
                TableRow row = (TableRow) table.getChildAt(i);

                // Extracting file name from the first TextView in the row
                TextView fileNameView = (TextView) row.getChildAt(0);
                String fileName = fileNameView.getText().toString();

                // Getting file path of the row that is currently being set up
                String filePath = employeeFileInfo.get(i)[3];

                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PDFManager pdfManager = new PDFManager(fileCenterContext, fileName, filePath);

                        if(!pdfManager.isPDFInDirectory()){

                            openDownloadPopup(pdfManager);

                        }
                        else{

                            confirmToOpenFile(pdfManager);

                        }

                    }
                });
            }
        }

    }

    /**
     * This method is used to make a pop up that asks the user if they want to download the
     * file that they selected.
     *
     * @param pdfManager
     */
    public void confirmToOpenFile(PDFManager pdfManager){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm File Selection");
        builder.setMessage("Do you want to open: " + pdfManager.getPdfName() + " ?");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            // Opening the downloaded pdfs
            pdfManager.openPDF();

        });

        builder.setNegativeButton("No", (dialog, which) -> {
            // Handle the case when the user clicks "No"
            Toast.makeText(FileCenter.this, "PDF retrieval cancelled", Toast.LENGTH_SHORT).show();
        });

        builder.show();




    }

    /**
     * This method is used to open the download pop up
     *
     * @param pdfManager
     */
    private void openDownloadPopup(PDFManager pdfManager) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Download");
        builder.setMessage("Do you want to download: " + pdfManager.getPdfName() + " ?");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            // Open the PDF viewer directly
            pdfManager.openPDF();
            //openPDFInWebView(fileName, filePath);  // COMMENTING OUT SHADI'S LINE
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            // Handle the case when the user clicks "No"
            Toast.makeText(FileCenter.this, "Download canceled", Toast.LENGTH_SHORT).show();
        });

        builder.show();
    }

    /**
     * This methods fills up the tables in the file center with their corresponding information
     *
     * @param tables
     */
    public void putInfoIntoTable(Map <String, TableLayout> tables){
        for (int i = 0; i < employeeFileInfo.size() ; i++) {
            String[] fileData = employeeFileInfo.get(i);
            // Creating new row
            TableRow row = new TableRow(this);

            // Creating text views that will be added to the row to show info
            TextView fileNameView = new TextView(this);
            fileNameView.setTextSize(TypedValue.COMPLEX_UNIT_SP,17);;
            fileNameView.setText(fileData[1]);
            fileNameView.setGravity(Gravity.CENTER);
            fileNameView.setWidth(convertDpToPx(this, 50));
            fileNameView.setPadding(0,0, convertDpToPx(this, 10), 0);
            formatPDFDownloadLink(fileNameView); // format the link



            TextView fileUploaderView = new TextView(this);
            fileUploaderView.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
            fileUploaderView.setText(fileData[5] + ", " + fileData[4]);
            fileUploaderView.setGravity(Gravity.CENTER);
            fileUploaderView.setWidth(convertDpToPx(this, 50));

            TextView dateView = new TextView(this);
            dateView.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
            dateView.setText(fileData[2]);
            dateView.setGravity(Gravity.CENTER);

            // Adding the new views to the row
            row.addView(fileNameView);
            row.addView(fileUploaderView);
            row.addView(dateView);

            // Adding padding to the row

            row.setPadding(0,convertDpToPx(this,10),0,convertDpToPx(this,5));


            // Putting the row into the correct table
            String  fileCenterType = fileData[6]; // file center type which is also table title
            //fileCenterType = sanitizeFileCenterType(fileCenterType);

            if(fileCenterType != null){
                tables.get(fileCenterType).addView(row);
                //Getting index of added row
                int indexOfLastChild = tables.get(fileCenterType).getChildCount() - 1;
                // setting the color of row to gray if its index is odd
                if(indexOfLastChild % 2 == 1){
                    row.setBackgroundColor(getResources().getColor(R.color.odd_row_color));
                }

            }

        }

    }

    /**
     * This method formats the appearance of a PDF link
     * @param titleView
     * HTML Link to icon:
     * <a target="_blank" href="https://icons8.com/icon/13593/pdf">PDF</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a>
     */
    private void formatPDFDownloadLink(TextView titleView){

        titleView.setPaintFlags(titleView.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        titleView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icons8_pdf_30,0,0,0);
        titleView.setCompoundDrawablePadding(2);




    }

    /**
     * This method creates tables in the file center
     * It takes care of the layout of the tables and the table titles
     * @return LinkedHashMap mapping Strings (Table titles) to tableLayouts.
     */
    public LinkedHashMap <String, TableLayout> createTables(){

        LinkedHashMap <String, TableLayout> mapOfFileCenterTypesToTableLayouts = new LinkedHashMap<>(); // stores the file center categories
        for (int i = 0; i < employeeFileInfo.size() ; i++){
            // Looping through all the files
            String[] fileData = employeeFileInfo.get(i); // array of information about the file
            String  fileCenterType = fileData[6]; // file center type which is also table title

            if(!mapOfFileCenterTypesToTableLayouts.containsKey(fileCenterType)){ // if table not already there for file type
                // Creating a title of the table and setting it up
                TextView tableTitle = new TextView(this);
                tableTitle.setText(fileCenterType);

                //Creating the table
                TableLayout newTable = new TableLayout(this);
                setUpTableLayout(newTable);

                // Adding the table to the map
                mapOfFileCenterTypesToTableLayouts.put(fileCenterType,newTable);

                // Adding header to table
                setUpTableHeader(newTable);

                //
                newTable.setBackground(getResources().getDrawable(R.drawable.table_border));
            }
        }

        return mapOfFileCenterTypesToTableLayouts;
    }
    /**
     * This method sets up the title of a table. It applied visual formating of size and underline to it
     */
    public static void setUpTableTitles(TextView titleView){



        // Apply the styled text to the TextView
        titleView.setTextColor(Color.BLACK);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP,27);
        titleView.setTypeface(null, Typeface.BOLD);;




    }

    /**
     * This method sets the header of the table. It describes the columns, showing file name, publisher, and date
     * @param table
     */
    public void setUpTableHeader(TableLayout table){

        // Setting row element and background colour
        TableRow row = new TableRow(this);
        row.setBackgroundColor(getResources().getColor(R.color.dark_grey));

        // Setting layout parameters to be applied
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,  // Width
                LinearLayout.LayoutParams.WRAP_CONTENT   // Height
        );
        layoutParams.setMargins(convertDpToPx(this,15), 0, 0, 7);

        // Setting up the text views

        // Setting up file name text view
        TextView fileName = new TextView(this);
        fileName.setTextSize(TypedValue.COMPLEX_UNIT_SP,17);
        fileName.setText("File Name");
        fileName.setGravity(Gravity.CENTER);
        fileName.setTypeface(Typeface.DEFAULT_BOLD);
        fileName.setTextColor(getResources().getColor(R.color.white));

        // Setting up publisher text view
        TextView publisher = new TextView(this);
        publisher.setTextSize(TypedValue.COMPLEX_UNIT_SP,19);
        publisher.setText("Publisher");
        publisher.setGravity(Gravity.CENTER);
        publisher.setTypeface(Typeface.DEFAULT_BOLD);
        publisher.setTextColor(Color.WHITE);

        // Setting up date textview
        TextView date = new TextView(this);
        date.setTextSize(TypedValue.COMPLEX_UNIT_SP,19);
        date.setText("Date");
        date.setGravity(Gravity.CENTER);
        date.setTypeface(Typeface.DEFAULT_BOLD);
        date.setTextColor(Color.WHITE);

        // Adding the textviews to the row
        row.addView(fileName);
        row.addView(publisher);
        row.addView(date);

        // Adding the row to the table

        table.addView(row);



    }

    /**
     * This method applies proper layout formatting to the tables within the File Center
     * @param table
     */
    public void setUpTableLayout(TableLayout table){

        // Setting layout parameters to be applied
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(convertDpToPx(this,20), 0,convertDpToPx(this,20),0 );
        table.setLayoutParams(layoutParams);
        table.setPadding(0,0,0,convertDpToPx(this,20));

        table.setColumnStretchable(0,true);
        table.setColumnStretchable(2, true);

    }

    /**
     * This method converts dp units to pixels
     * @param context
     * @param dp
     * @return
     */
    public static int convertDpToPx(Context context, float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    /**
     * This method is used to refresh the information that is being displayed in file center.
     * It is essentially a copy of the onCreate method.
     *
     * @param myView
     */
    public void refreshEmployeeInfo(View myView){
        TextView loadingDisplay = findViewById(R.id.loadingDisplay);
        loadingDisplay.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, FileCenter.class);
        intent.putExtra("auth_code", auth_code);
        startActivity(intent);
        finish();
    }

    //==============================================================================================
    //                              METHODS FOR SWITCHING PAGES
    //==============================================================================================

    /**
     * Opens the profile page and sends the auth_code so that it does not need to be
     * calculated again. It also displays the loading textview so the user knows that
     * the app is loading.
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
     * Opens the time sheet page and sends the auth_code so that it does not need to be
     * calculated again. It also displays the loading textview so the user knows that
     * the app is loading.
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
     * This method is used to open settings from the main menu and sends the auth_code so that it does not need to be
     * calculated again. It also displays the loading textview so the user knows that
     * the app is loading.
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
     * Logs the user out, takes user back to log in page and clears their data.
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
                openTimeSheet();
                break;
            case "File Center":
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
     * This method sets the current colors saved to the ones that the user selected.
     */
    private void changeColors(){
        sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        int colorNav = sharedPreferences.getInt("colorNav", 0);
        String textColor = sharedPreferences.getString("colorNavText", "");
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
            ConstraintLayout page = findViewById(R.id.fileCenterPage);
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