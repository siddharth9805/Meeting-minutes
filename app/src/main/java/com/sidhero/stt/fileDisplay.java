package com.sidhero.stt;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class fileDisplay extends AppCompatActivity {
    TextView display, displayTrans;
    public String text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_display);
        display=(TextView)findViewById(R.id.textView3);
        displayTrans = (TextView) findViewById(R.id.textView4);
        text=getIntent().getExtras().getString("fileitem");
        display.setText(text);
        load();

    }

    public void load() {

        //final LayoutInflater li = LayoutInflater.from(getApplicationContext());
        //View promptsView = li.inflate(R.layout.prompts, null);

        String text2;


        FileInputStream fis = null;

        try {
            Log.d("mmmmmm","in try");
            fis = openFileInput(text+".txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();

            while ((text2 = br.readLine()) != null) {
                sb.append(text2).append("\n");
            }

            displayTrans.setText(sb.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//                getApplicationContext());
//
//        // set prompts.xml to alertdialog builder
//        alertDialogBuilder.setView(promptsView);
//
//        final EditText userInput = (EditText) promptsView
//                .findViewById(R.id.editTextDialogUserInput);
//
//        // set dialog message
//        alertDialogBuilder
//                .setCancelable(false)
//                .setPositiveButton("OK",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                // get user input and set it to result
//                                // edit text
//                                //File root = new File(Environment.getExternalStorageDirectory(), "Notes");
//                                //FILE_NAME = userInput.getText().toString();
//                                //Log.d("mihir",FILE_NAME);
//
//
//                            }
//                        })
//                .setNegativeButton("Cancel",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                dialog.cancel();
//                            }
//                        });
//
////        Intent intent =new Intent(this,logs.class);
////        intent.putExtra("file_name", FILE_NAME);
////        startActivity(intent);
//
//        // create alert dialog
//        AlertDialog alertDialog = alertDialogBuilder.create();
//
//        // show it
//        alertDialog.show();
   }
}
