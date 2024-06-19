package com.example.wolseytechhr;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PDFManager {

    private final String pdfName;
    private Context fileCenterContext;
    private final String downloadFolderName = "downloadedPDFs";
    private File downloadFolder;
    private DownloadManager downloadManager;
    private final String webLinkToPDF;
    private int CREATE_FILE = 1;
    private static final int PICK_PDF_FILE = 2;

    /**
     * Constructor of PDFManager
     * @param pdfName
     */
    public PDFManager(Context fileCenterContext, String pdfName, String webLinkToPDF){

        this.pdfName = pdfName;
        this.fileCenterContext = fileCenterContext;
        this.webLinkToPDF = webLinkToPDF;
    }

    /**
     * This method opens the PDF File
     */
    public void openPDF(){

        ensureDownloadFolderExists();

        if(!isPDFInDirectory()){
            downloadPDF();
        }
        openFile(Uri.parse(Environment.DIRECTORY_DOCUMENTS + File.separator + pdfName + ".pdf"));

    }

    /**
     * Getter that returns pdf name
     * @return
     */
    public String getPdfName(){
        return pdfName;
    }

    /**
     * This methods determines whether a PDF is found within the downloads folder
     * @return
     */
    public boolean isPDFInDirectory() {

        try {
            File documentsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            if (documentsDirectory != null) {
                File file = new File(documentsDirectory, pdfName + ".pdf");
                return file.exists();
            }

            return false;
        }
        catch (SecurityException securityException){

            Log.e("Security Exception", securityException.getMessage());
            return false;
        }
    }

    /**
     * This method returns pdfDownloadFolder if exists; otherwise it creates one and returns an empty folder
     */
    public void ensureDownloadFolderExists(){

        // Create the File object for the download folder.
        String downloadsFolderURI = Environment.DIRECTORY_DOCUMENTS;
        Path path = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            path = Paths.get(downloadsFolderURI);
        }

        // Ensure that the folder and its parent directories exist
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!Files.exists(path)) {

                    Files.createDirectories(path);
                }
            }
        }
        catch (IOException e){
            System.err.println("Failed to create directory!" + e.getMessage());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            downloadFolder = new File(path.toUri());
        }

    }


    /**
     * This method downloads a PDF to the local pdfDownloads File from the web url
     */
    public void downloadPDF(){

        downloadManager = (DownloadManager) fileCenterContext.getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE); // initializing the downloadManager

        Uri downloadSource = Uri.parse(webLinkToPDF); // Storing the uri from which the pdf is downloaded

        // Creating a request to the download manager
        DownloadManager.Request request = new DownloadManager.Request(downloadSource)
                .setTitle(pdfName)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOCUMENTS, pdfName + ".pdf")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        downloadManager.enqueue(request); // Enqueuing the request

    }

    private void openFile(Uri pickerInitialUri) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");

        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        startActivityForResult((Activity) fileCenterContext,intent ,PICK_PDF_FILE,null);
    }




}
