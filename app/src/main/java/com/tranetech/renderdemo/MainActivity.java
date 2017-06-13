package com.tranetech.renderdemo;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hendrix.pdfmyxml.PdfDocument;
import com.hendrix.pdfmyxml.viewRenderer.AbstractViewRenderer;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    AbstractViewRenderer page;
    Context ctx = this;
    PdfDocument doc;
    File file, pdf_file;
    TextView tv_hello;
    String FILE, pdfFileName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button print = (Button) findViewById(R.id.print);

        print.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {

                CeateDocumentProcess();


            }
        });

    }


    @SuppressLint("NewApi")
    private void CeateDocumentProcess() {

        Toast.makeText(ctx, "Creating file...", Toast.LENGTH_SHORT).show();


        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String pdfFileName = "PDF" + timeStamp + "";

        String sdcard_path = Environment.getExternalStorageDirectory().getAbsolutePath();

        // for samsung device external sd detection hiren
        if (android.os.Build.DEVICE.contains("Samsung") || android.os.Build.MANUFACTURER.contains("Samsung")) {
            sdcard_path = sdcard_path + "/external_sd/";
        }

        FILE = sdcard_path
                + "/PDF/" + pdfFileName + ".pdf";

        // Create New Blank Document
        Document document = new Document(PageSize.A4);


        // Create Directory in External Storage
        String root = sdcard_path;
        File myDir = new File(root + "/PDF");
        myDir.mkdirs();


        // Create Pdf Writer for Writting into New Created Document
        try {
            PdfWriter.getInstance(document, new FileOutputStream(FILE));

            // Open Document for Writting into document
            document.open();
            // User Define Method
            addMetaData(document);
            addTitlePage(document);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // Close Document after writting all content
        document.close();


        Log.e(TAG, "File Path main: " + myDir);
        Log.e(TAG, "File Name main: " + pdfFileName);


        if (FILE != null) {
           /* Toast.makeText(ctx, "File created successfully.", Toast.LENGTH_SHORT).show();
            MyPrintDocumentAdapter myprintAdapte = new MyPrintDocumentAdapter(MainActivity.this,myDir, pdfFileName);
            PrintManager printManager = (PrintManager) ctx.getSystemService(Context.PRINT_SERVICE);
            String jobName = ctx.getString(R.string.app_name) + "Document";
            printManager.print(jobName, myprintAdapte, null);*/

            file = new File(FILE);

            if (file.isFile() && file.exists()) {


                //     File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/"+ filename);
                Intent target = new Intent(Intent.ACTION_VIEW);
                target.setDataAndType(Uri.fromFile(file), "application/pdf");
                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                Intent sendIntent = Intent.createChooser(target, "Open File");

                try {
                    // Verify that there are applications registered to handle this intent
                    // (resolveActivity returns null if none are registered)
                    if (sendIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(sendIntent);
                    }
                } catch (ActivityNotFoundException e) {
                    // Instruct the user to install a PDF reader here, or something
                    Toast.makeText(ctx, "Please download pdf reader from play store ", Toast.LENGTH_SHORT).show();
                }


            } else {
                Toast.makeText(ctx, "Error finding file...", Toast.LENGTH_SHORT).show();

            }
        }
    }

    // Set PDF document Properties

    public void addMetaData(Document document)

    {
        document.addTitle("RESUME");
        document.addSubject("Person Info");
        document.addKeywords("Personal,	Education, Skills");
        document.addAuthor("TAG");
        document.addCreator("TAG");
    }

    public void addTitlePage(Document document) throws DocumentException {
        // Font Style for Document
        Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD, BaseColor.MAGENTA);
        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 22, Font.BOLD
                | Font.UNDERLINE, BaseColor.MAGENTA);
        Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.MAGENTA);
        Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.MAGENTA);
        // Start New Paragraph
        Paragraph prHead = new Paragraph();
        // Set Font in this Paragraph
        prHead.setFont(titleFont);
        // Add item into Paragraph
        prHead.add("RESUME – Name\n");

        // Create Table into Document with 1 Row
        PdfPTable myTable = new PdfPTable(1);
        // 100.0f mean width of table is same as Document size
        myTable.setWidthPercentage(200.0f);

        // Create New Cell into Table
        PdfPCell myCell = new PdfPCell(new Paragraph(""));
        myCell.setBorder(Rectangle.BOTTOM);

        // Add Cell into Table
        myTable.addCell(myCell);

        prHead.setFont(catFont);
        prHead.add("\nName1 Name2\n");
        prHead.setAlignment(Element.ALIGN_CENTER);

        // Add all above details into Document
        document.add(prHead);
        document.add(myTable);
        document.add(myTable);

        // Now Start another New Paragraph
        Paragraph prPersinalInfo = new Paragraph();
        prPersinalInfo.setFont(smallBold);
        prPersinalInfo.add("Address 1\n");
        prPersinalInfo.add("Address 2\n");
        prPersinalInfo.add("City: SanFran. State: CA\n");
        prPersinalInfo.add("Country: USA Zip Code: 000001\n");
        prPersinalInfo.add("Mobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \n Mobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \nMobile: 9999999999 Fax: 1111111 Email: john_pit@gmail.com \n ");

        prPersinalInfo.setAlignment(Element.ALIGN_CENTER);

        document.add(prPersinalInfo);
        document.add(myTable);
        document.add(myTable);

        Paragraph prProfile = new Paragraph();
        prProfile.setFont(smallBold);
        prProfile.add("\n \n Profile : \n ");
        prProfile.setFont(normal);
        prProfile
                .add("\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG.\nI am Mr. XYZ. I am Android Application Developer at TAG. GAME OVER");

        prProfile.setFont(smallBold);
        document.add(prProfile);
        // Create new Page in PDF
        //   document.newPage();
    }


}
