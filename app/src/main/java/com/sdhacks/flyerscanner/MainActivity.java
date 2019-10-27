package com.sdhacks.flyerscanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.camerakit.CameraKitView;

public class MainActivity extends AppCompatActivity {

    private CameraKitView cameraKitView;
    private Button photoButton;
    private Button listButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
        constraintLayout.setBackgroundColor(Color.BLACK);

        cameraKitView = findViewById(R.id.camera);
        photoButton = findViewById(R.id.photoButton);
        listButton = findViewById(R.id.listButton);

        photoButton.setOnClickListener(photoOnClickListener);
    }

    private View.OnClickListener photoOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.v("FlyerScanner-onclick", "Outer most Onclick triggered");
            cameraKitView.captureImage((view, photo) -> {
                Log.v("FlyerScanner-onclick", "Onclick triggered");
                new Thread(() -> {
                    try {
                        Log.v("FlyerScanner-inneronclick", "We have started this intent");
                        Intent confirmIntent = new Intent(getApplicationContext(), ConfirmActivity.class);
                        confirmIntent.putExtra(getResources().getString(R.string.image_bytes), photo);
                        startActivity(confirmIntent);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            });
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        cameraKitView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraKitView.onResume();
    }

    @Override
    protected void onPause() {
        cameraKitView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        cameraKitView.onStop();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void onListButtonClicked(View view) {
        Intent listIntent = new Intent(this, ListActivity.class);
        startActivity(listIntent);
    }
}
