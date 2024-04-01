package com.example.imagepicker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    TextView imageList;

    StringBuilder sb;

    TextView selectedImageText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        imageList = (TextView) findViewById(R.id.selectedImageText);
        sb = new StringBuilder();
        Intent intent = getIntent();
        ArrayList<String> images = intent.getStringArrayListExtra("images");

        for (String s : images) {

            sb.append(s+"\n\n");

        }

        imageList.setText(sb.toString());

    }
}
