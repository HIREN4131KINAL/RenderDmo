package com.tranetech.renderdemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.content.ContentValues.TAG;


/**
 * Created by HIREN AMALIYAR on 02-05-2017.
 */

@TargetApi(19)
public class MyPrintDocumentAdapter extends PrintDocumentAdapter {

    Activity mcontext;
    PrintedPdfDocument myPdfDocument;
    int pageHeight, pageWidth;
    int totalpages;
    File pdfFile;
    String pdfFileName, str;

    PdfDocument.Page[] writtenPagesArray;

    public MyPrintDocumentAdapter(Activity mcontext, File pdfFile, String pdfFileName) {
        this.mcontext = mcontext;
        this.pdfFile = pdfFile;
        this.pdfFileName = pdfFileName;

        Log.e(TAG, "File Path : " + pdfFile);
        Log.e(TAG, "File Name : " + pdfFileName);

    }

    @Override
    public void onLayout(PrintAttributes oldAttributes,
                         PrintAttributes newAttributes,
                         CancellationSignal cancellationSignal,
                         LayoutResultCallback callback,
                         Bundle metadata) {


        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }
        PrintDocumentInfo pdi = new PrintDocumentInfo.Builder(pdfFileName).setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build();
        callback.onLayoutFinished(pdi, true);
        totalpages = pdi.getPageCount();

        Log.e(TAG, "onLayout: totalPages " + totalpages);

        myPdfDocument = new PrintedPdfDocument(mcontext, newAttributes);

        if (myPdfDocument.getPageContentRect().height() < myPdfDocument.getPages().size())
            totalpages++;

        pageHeight = newAttributes.getMediaSize().getHeightMils() / 1000 * 72;
        pageWidth =
                newAttributes.getMediaSize().getWidthMils() / 1000 * 72;



        if (totalpages > 0) {
            PrintDocumentInfo.Builder builder = new PrintDocumentInfo
                    .Builder(pdfFileName)
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(totalpages);

            PrintDocumentInfo info = builder.build();
            callback.onLayoutFinished(info, true);
        } else {
            callback.onLayoutFailed("Page count is zero.");
        }
    }


    @Override
    public void onWrite(final PageRange[] pageRanges,
                        final ParcelFileDescriptor destination,
                        final CancellationSignal cancellationSignal,
                        final WriteResultCallback callback) {

        for (int i = 0; i < totalpages; i++) {
            if (pageInRange(pageRanges, i)) {

                PdfDocument.PageInfo newPage = new PdfDocument.PageInfo.Builder(pageWidth,
                        pageHeight, i).create();

                PdfDocument.Page page =
                        myPdfDocument.startPage(newPage);

                if (cancellationSignal.isCanceled()) {
                    callback.onWriteCancelled();
                    myPdfDocument.close();
                    myPdfDocument = null;
                    return;
                }

                drawPage(page, i);
                myPdfDocument.finishPage(page);
            }
        }

        try {
            myPdfDocument.writeTo(new FileOutputStream(
                    destination.getFileDescriptor()));
        } catch (IOException e) {
            callback.onWriteFailed(e.toString());
            return;
        } finally {
            myPdfDocument.close();
            myPdfDocument = null;
        }

        callback.onWriteFinished(pageRanges);

        InputStream input = null;
        OutputStream output = null;

        try {

            str = fil_to_print();

            input = new FileInputStream(str);
            output = new FileOutputStream(destination.getFileDescriptor());

            byte[] buf = new byte[1024];
            int bytesRead;

            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }

            callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});

        } catch (FileNotFoundException ee) {
            //Catch exception
            ee.printStackTrace();
        } catch (Exception e) {
            //Catch exception
            e.printStackTrace();
        } finally {
            try {
                if (input != null)
                    input.close();
                if (output != null)
                    output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private String fil_to_print() {
        //   File sdcard = Environment.getExternalStorageDirectory();

//Get the text file
        File file = new File(pdfFile, pdfFileName);

//Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text.toString();
    }

    private boolean pageInRange(PageRange[] pageRanges, int page) {
        for (int i = 0; i < pageRanges.length; i++) {
            if ((page >= pageRanges[i].getStart()) &&
                    (page <= pageRanges[i].getEnd()))
                return true;
        }
        return false;
    }


    private void drawPage(PdfDocument.Page page,
                          int pagenumber) {
        Canvas canvas = page.getCanvas();

        pagenumber++; // Make sure page numbers start at 1

        int titleBaseLine = 72;
        int leftMargin = 54;

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(40);
        canvas.drawText(
                "Test Print Document Page " + pagenumber,
                leftMargin,
                titleBaseLine,
                paint);

        paint.setTextSize(14);
        //  canvas.drawText("This is some test content to verify that custom document printing works", leftMargin, titleBaseLine + 35, paint);

        drawTextOnCanvas(canvas, str, paint);
    }


    private void drawTextOnCanvas(Canvas canvas, String text, Paint paint) {
        // maybe color the bacground..
        canvas.drawPaint(paint);

        // Setup a textview like you normally would with your activity context
        TextView tv = new TextView(mcontext);

        // setup text
        tv.setText(text);

        // maybe set textcolor
        tv.setTextColor(Color.BLACK);

        // you have to enable setDrawingCacheEnabled, or the getDrawingCache will return null
        tv.setDrawingCacheEnabled(true);

        // we need to setup how big the view should be..which is exactly as big as the canvas
        tv.measure(View.MeasureSpec.makeMeasureSpec(canvas.getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(canvas.getHeight(), View.MeasureSpec.EXACTLY));

        // assign the layout values to the textview
        tv.layout(10, 10, tv.getMeasuredWidth(), tv.getMeasuredHeight());

        // draw the bitmap from the drawingcache to the canvas
        canvas.drawBitmap(tv.getDrawingCache(), 50, 50, paint);

        // disable drawing cache
        tv.setDrawingCacheEnabled(false);
    }


}