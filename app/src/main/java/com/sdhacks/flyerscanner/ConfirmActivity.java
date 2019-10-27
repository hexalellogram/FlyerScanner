package com.sdhacks.flyerscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jpegkit.Jpeg;
import com.jpegkit.JpegImageView;

public class ConfirmActivity extends AppCompatActivity {

    private byte[] picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        picture = getIntent().getExtras().getByteArray(getResources().getString(R.string.image_bytes));

        if(picture != null){
            final Jpeg jpeg = new Jpeg(picture);
            final JpegImageView imageView = findViewById(R.id.imageView);
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    imageView.setJpeg(jpeg);
                }
            });
        }
    }

    public void onRetakeClick(View view) {
        Intent retakeIntent = new Intent(this, MainActivity.class);
        startActivity(retakeIntent);
    }

    public void onContinueClick(View view) {

    }
}
