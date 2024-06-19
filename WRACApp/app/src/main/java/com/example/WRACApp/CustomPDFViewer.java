package com.example.wolseytechhr;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class CustomPDFViewer extends AppCompatActivity {

    private String pdfName;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_pdf_viewer);

        // Retrieve the PDF name from your intent or wherever you're getting it
        pdfName = "your_pdf_name.pdf";
        viewDownloadedPDF();
    }

    private void viewDownloadedPDF() {
        File file = new File(getFilesDir() + File.separator + pdfName);
        Uri path = Uri.fromFile(file);
        Log.i("CustomPDFActivity", String.valueOf(path));

        Intent pdfOpenIntent = new Intent(Intent.ACTION_VIEW);
        pdfOpenIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pdfOpenIntent.setDataAndType(path, "application/pdf");

        try {
            startActivity(pdfOpenIntent);
        } catch (ActivityNotFoundException e) {
            openFileCenter();
        }
    }

    private void openFileCenter() {
        // Implement the logic to navigate back to FileCenter or any other desired activity
        Intent fileCenterIntent = new Intent(this, FileCenter.class);
        startActivity(fileCenterIntent);
    }

    private void downloadPDF() {
        File downloadDirectory = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File destinationFile = new File(downloadDirectory, pdfName);

        if (!isDuplicateFile(destinationFile)) {
            copyFile(new File(getFilesDir() + File.separator + pdfName), destinationFile);
            Toast.makeText(this, "Download complete", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Already downloaded", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isDuplicateFile(File destinationFile) {
        return destinationFile.exists();
    }

    private void copyFile(File source, File destination) {
        try (FileInputStream inStream = new FileInputStream(source);
             FileOutputStream outStream = new FileOutputStream(destination);
             FileChannel inChannel = inStream.getChannel();
             FileChannel outChannel = outStream.getChannel()) {

            inChannel.transferTo(0, inChannel.size(), outChannel);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
