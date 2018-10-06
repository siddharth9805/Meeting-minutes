package com.sidhero.stt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class fileDisplay extends AppCompatActivity {
    TextView display;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_display);
        display=(TextView)findViewById(R.id.textView3);
        String text=getIntent().getExtras().getString("fileitem");
        display.setText(text);
    }
}
