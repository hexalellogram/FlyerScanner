package com.sdhacks.flyerscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
    }

    public void onScanAnotherClick(View view) {
        Intent cameraIntent = new Intent(this, MainActivity.class);
        startActivity(cameraIntent);
    }

    public void onDeleteClick(View view) {

    }
}
