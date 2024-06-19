package com.example.wolseytechhr;

import android.os.Environment;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.util.List;

public class PDFGenerator {

    public static void generateTimesheetPdf(String filePath, List<String[]> timesheetData) {
        // Create a document
        Document document = new Document();

        try {
            // Create a PdfWriter instance
            PdfWriter.getInstance(document, new FileOutputStream(filePath));

            // Open the document for writing
            document.open();

            // Add content to the PDF
            document.add(new Paragraph("Timesheet Data"));

            // Loop through the timesheet data and add it to the PDF
            for (int i = 0; i < timesheetData.size(); i++) {
                String[] timesheetInfo = timesheetData.get(i);
                // Add information to the PDF
                document.add(new Paragraph("Date: " + timesheetInfo[1]));
                document.add(new Paragraph("ID: " + timesheetInfo[0]));
                // Add more fields as needed...
                document.add(new Paragraph("\n")); // Add a newline for separation
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the document
            document.close();
        }
    }

    public static String getDefaultFilePath() {
        // Define the default path where the PDF will be saved
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/timesheet.pdf";
    }
}
