package com.tranetech.renderdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.util.Log;
import android.widget.Toast;

import com.hendrix.pdfmyxml.PdfDocument;
import com.hendrix.pdfmyxml.viewRenderer.AbstractViewRenderer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.content.ContentValues.TAG;

/**
 * Created by HIREN AMALIYAR on 12-05-2017.
 */

@SuppressLint("NewApi")
public class PrintPDFAdapter extends PrintDocumentAdapter {
    private File pdfFile;
    private String fileName;
    int TotalPages;

    public PrintPDFAdapter(File pdfFile, String fileName) {

        this.pdfFile = pdfFile;
        this.fileName = fileName;

        Log.e(TAG, "File Path : " + this.pdfFile);
        Log.e(TAG, "File Name : " + this.fileName);
    }


    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }


        PrintDocumentInfo pdi = new PrintDocumentInfo.Builder(fileName).setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build();
        TotalPages = pdi.getPageCount();
        callback.onLayoutFinished(pdi, true);




    }

    @Override
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
        InputStream input = null;
        OutputStream output = null;

        try {

            input = new FileInputStream(pdfFile);
            output = new FileOutputStream(destination.getFileDescriptor());

            byte[] buf = new byte[1024];
            int bytesRead;

            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }

            callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                input.close();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}