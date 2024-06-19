package com.example.wolseytechhr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class HelpCenterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_center);

        // Enable the back button in the ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get reference to the LinearLayout in your layout
        LinearLayout faqLayout = findViewById(R.id.faqLayout);

        // Create FAQ entries dynamically
        addFaqEntry("How do I reset my password?", "To reset your password, go to the 'Forgot Password' section on the login screen.");
        addFaqEntry("What should I do if the app crashes?", "Make sure you have the latest version installed. If the issue persists, contact support.");

    }
    private void addFaqEntry(String question, final String answer) {
        // Create a new TextView for each FAQ entry
        TextView faqTextView = new TextView(this);
        faqTextView.setText(question);
        faqTextView.setTextSize(18);
        faqTextView.setPadding(16, 8, 16, 8);

        // Set OnClickListener to display the answer when clicked
        faqTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAnswer(answer);
            }
        });

        // Get reference to the LinearLayout and add the TextView dynamically
        LinearLayout faqLayout = findViewById(R.id.faqLayout);
        faqLayout.addView(faqTextView);
    }

    private void displayAnswer(String answer) {
        // You can customize how the answer is displayed (e.g., in a dialog, another activity, etc.)
        // For simplicity, let's just log it for now
        System.out.println("Answer: " + answer);
    }


    // Override onOptionsItemSelected to handle the back button click
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate back to the main page
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
